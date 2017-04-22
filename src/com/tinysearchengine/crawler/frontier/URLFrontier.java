package com.tinysearchengine.crawler.frontier;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.model.Persistent;
import com.tinysearchengine.database.DBEnv;
import com.tinysearchengine.utils.TimedBlockingPriorityQueue;

import java.util.Map;

import org.apache.log4j.*;

public class URLFrontier {

	/**
	 * Every 10 minutes;
	 */
	private final static long k_SNAPSHOT_INTERVAL = 1000 * 60 * 10;

	/**
	 * This represents the priority of a given URL to be enqueued into the
	 * frontier.
	 * 
	 * @author hengchu
	 *
	 */
	static public enum Priority {
		Low(0), Medium(1), High(2);

		private final int d_value;

		Priority(int value) {
			d_value = value;
		}

		public int toInt() {
			return d_value;
		}
	}

	@Persistent
	static public class Request {
		/**
		 * The URL to execute the request on.
		 */
		public URL url;

		/**
		 * The method to execute, either HEAD or GET.
		 */
		public String method;
	}

	/**
	 * The 3 front end queues.
	 */
	Queue<Pair<Request, Long>>[] d_frontendQueues = new Queue[3];

	/**
	 * The set of backend queues, the number of backend queues is determined by
	 * the number of download threads * 3.
	 */
	Queue<Request>[] d_backendQueues;

	/**
	 * The indices of empty backend queues.
	 */
	Set<Integer> d_emptyBackendQueues = new HashSet<>();

	/**
	 * The map from domain strings to queue indices.
	 */
	Map<String, Integer> d_domainToQueue = new HashMap<>();

	/**
	 * The map from domain strings to when their last request was scheduled.
	 */
	Map<String, Long> d_lastScheduledTime = new HashMap<>();

	/**
	 * The priority queue from where we fetch the next domains.
	 */
	TimedBlockingPriorityQueue<String> d_domainQueue =
		new TimedBlockingPriorityQueue<>();

	/**
	 * The random generator.
	 */
	Random d_generator = new Random();

	/**
	 * The entity store used to
	 */
	DBEnv d_dbEnv;

	/**
	 * The primary index used to access snapshots.
	 */
	PrimaryIndex<Long, URLFrontierSnapshot> d_snapshotByTime;

	/**
	 * The timer used to trigger snapshots.
	 */
	Timer d_snapshotTimer;

	@SuppressWarnings("unchecked")
	private void initialize(int numBackendQueues) {
		for (int i = 0; i < 3; ++i) {
			d_frontendQueues[i] = new LinkedList<>();
		}

		d_backendQueues = new Queue[numBackendQueues];
		for (int i = 0; i < numBackendQueues; ++i) {
			d_backendQueues[i] = new LinkedList<>();
			d_emptyBackendQueues.add(i);
		}
	}

	public URLFrontier(int numBackendQueues, Set<URL> seeds, DBEnv dbEnv) {
		initialize(numBackendQueues);
		d_dbEnv = dbEnv;
		d_snapshotByTime = d_dbEnv.getStore().getPrimaryIndex(Long.class,
				URLFrontierSnapshot.class);

		URLFrontierSnapshot snapshot = fetchLatestSnapshot();
		if (snapshot != null) {
			Logger logger = Logger.getLogger(URLFrontier.class);
			logger.info("Found a snapshot at: "
					+ new Date(snapshot.snapshotTime).toString());
			logger.info("Restoring from last snapshot.");
			restoreFromSnapshot(snapshot);
			logger.info("Finished restoring.");
		}

		for (URL url : seeds) {
			Request req = new Request();
			req.url = url;
			req.method = "HEAD";
			long now = (new Date()).getTime();
			put(req, Priority.Medium, now);
		}
	}

	/**
	 * Return the next request to be executed.
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public Request get() throws InterruptedException {
		String domain = d_domainQueue.get();
		synchronized (this) {
			Integer qId = d_domainToQueue.get(domain);
			assert qId != null;
			assert !d_emptyBackendQueues.contains(qId);

			Queue<Request> backendQueue = d_backendQueues[qId];
			assert !backendQueue.isEmpty();

			Request req = backendQueue.poll();
			if (backendQueue.isEmpty()) {
				d_emptyBackendQueues.add(qId);
				d_domainToQueue.remove(domain);
			}

			return req;
		}
	}

	public void put(URL url, Priority priority, long releaseTime) {
		Request req = new Request();
		req.url = url;
		req.method = "GET";
		put(req, priority, releaseTime);
	}

	/**
	 * Put the given request into the frontier, and set to be released after the
	 * given releaseTime.
	 * 
	 * @param req
	 * @param priority
	 * @param releaseTime
	 * @throws InterruptedException
	 */
	public synchronized void put(Request req,
			Priority priority,
			long releaseTime) {
		Logger logger = Logger.getLogger(URLFrontier.class);
		logger.debug("Putting url: " + req.url + ", method: " + req.method);
		logger.debug("Releasing at: " + new Date(releaseTime).toString());
		
		// Some sanity check on the inputs.
		int frontendQueueId = priority.toInt();
		assert 0 <= frontendQueueId && frontendQueueId < 3;

		assert req.url.getProtocol().equals("http")
				|| req.url.getProtocol().equals("https");
		assert releaseTime > 0;

		d_frontendQueues[frontendQueueId]
				.offer(new ImmutablePair<>(req, releaseTime));
		moveFrontendToBackend();

		String domain = req.url.getAuthority();
		Long lastScheduledTime = d_lastScheduledTime.get(domain);
		if (lastScheduledTime == null) {
			d_lastScheduledTime.put(domain, releaseTime);
		} else if (releaseTime > lastScheduledTime) {
			d_lastScheduledTime.put(domain, releaseTime);
		}
	}

	public synchronized long lastScheduledTime(String domain) {
		Long time = d_lastScheduledTime.get(domain);
		if (time == null) {
			return (new Date()).getTime();
		}

		return time.longValue();
	}

	/**
	 * Select a frontend queue based on the following intervals: [0, 10] -> low
	 * priority [10, 50] -> medium priority [50, 120] -> high priority
	 * 
	 * @return
	 */
	private int selectFrontendQueue() {
		int lowPrioEnd = d_frontendQueues[0].isEmpty() ? 0 : 10;
		int medPrioEnd = d_frontendQueues[1].isEmpty() ? lowPrioEnd : 50;
		int hiPrioEnd = d_frontendQueues[2].isEmpty() ? medPrioEnd : 120;

		int rand = d_generator.nextInt(hiPrioEnd);
		if (0 <= rand && rand < lowPrioEnd) {
			return 0;
		} else if (lowPrioEnd <= rand && rand < medPrioEnd) {
			return 1;
		} else if (medPrioEnd <= rand && rand < hiPrioEnd) {
			return 2;
		} else {
			assert false;
			return -1;
		}
	}

	private boolean hasEmptyBackendQueue() {
		return !d_emptyBackendQueues.isEmpty();
	}

	private boolean frontendHeadMatchesBackend(int fIdx) {
		Pair<Request, Long> head = d_frontendQueues[fIdx].peek();
		if (head == null) {
			return false;
		}

		String domain = head.getLeft().url.getAuthority();
		Integer qId = d_domainToQueue.get(domain);
		if (qId == null) {
			return false;
		} else {
			return true;
		}
	}

	private void moveFrontendToMatchingBackend(int fIdx) {
		Pair<Request, Long> head = d_frontendQueues[fIdx].poll();
		assert head != null;

		String domain = head.getLeft().url.getAuthority();
		Long releaseTime = head.getRight();

		Integer matchedQId = d_domainToQueue.get(domain);
		assert matchedQId != null;

		d_backendQueues[matchedQId].offer(head.getLeft());
		d_domainQueue.put(domain, releaseTime);
	}

	private void moveFrontendToEmptyBackend(int fIdx) {
		Pair<Request, Long> head = d_frontendQueues[fIdx].poll();
		assert head != null;

		assert d_emptyBackendQueues.size() > 0;
		Integer emptyQId = d_emptyBackendQueues.iterator().next();
		assert emptyQId != null;
		d_emptyBackendQueues.remove(emptyQId);

		String domain = head.getLeft().url.getAuthority();
		Long releaseTime = head.getRight();

		d_backendQueues[emptyQId].offer(head.getLeft());
		d_domainQueue.put(domain, releaseTime);
		d_domainToQueue.put(domain, emptyQId);
	}

	private void moveFrontendToBackend() {
		boolean hasEmptyBackend = hasEmptyBackendQueue();
		int fIdx = selectFrontendQueue();
		boolean frontendMatches = frontendHeadMatchesBackend(fIdx);
		boolean frontendHasItem = !d_frontendQueues[fIdx].isEmpty();

		while (frontendHasItem && (hasEmptyBackend || frontendMatches)) {
			if (frontendMatches) {
				moveFrontendToMatchingBackend(fIdx);
			} else {
				assert hasEmptyBackend;
				moveFrontendToEmptyBackend(fIdx);
			}

			hasEmptyBackend = hasEmptyBackendQueue();
			frontendMatches = frontendHeadMatchesBackend(fIdx);
			frontendHasItem = !d_frontendQueues[fIdx].isEmpty();
		}
	}

	@SuppressWarnings("unchecked")
	private void snapshot() {
		Logger logger = Logger.getLogger(URLFrontier.class);
		Date now = new Date();

		logger.info("Snapshotting URLFrontier at: " + now.toString());
		URLFrontierSnapshot snap = new URLFrontierSnapshot();

		snap.snapshotTime = now.getTime();
		synchronized (this) {
			snap.frontendQueues = d_frontendQueues;
			snap.emptyBackendQueues = d_emptyBackendQueues;
			snap.backendQueues = d_backendQueues;
			snap.domainToQueue = d_domainToQueue;
			snap.domainQueue = d_domainQueue.dumpQueue(new Pair[0]);
		}

		d_snapshotByTime.put(snap);
		d_dbEnv.getStore().sync();

		logger.info("Finished snapshot.");
	}

	/**
	 * Return the latest snapshot, if none exists, return null.
	 * 
	 * @return
	 */
	private URLFrontierSnapshot fetchLatestSnapshot() {
		EntityCursor<Long> keyCursor = d_snapshotByTime.keys();
		try {
			Long lastKey = keyCursor.last();
			if (lastKey != null) {
				return d_snapshotByTime.get(lastKey);
			}

			return null;
		} finally {
			keyCursor.close();
		}
	}

	/**
	 * Restore the state of the frontier from the given snapshot.
	 * 
	 * @param snapshot
	 */
	private void restoreFromSnapshot(URLFrontierSnapshot snapshot) {
		long now = new Date().getTime();
		long dur = now - snapshot.snapshotTime;

		d_backendQueues = snapshot.backendQueues;
		d_domainToQueue = snapshot.domainToQueue;
		d_frontendQueues = snapshot.frontendQueues;
		d_emptyBackendQueues = snapshot.emptyBackendQueues;
		for (int i = 0; i < snapshot.domainQueue.length; ++i) {
			String domain = snapshot.domainQueue[i].getLeft();
			long releaseTime = snapshot.domainQueue[i].getRight() + dur;
			d_domainQueue.put(domain, releaseTime);
		}
	}

	/**
	 * Start the background thread that snapshots the URLFrontier to BDB
	 * regularly. If you don't need snapshot, simply don't call this function.
	 */
	public void start() {
		if (d_snapshotTimer != null) {
			return;
		}

		WeakReference<URLFrontier> self = new WeakReference<>(this);
		d_snapshotTimer = new Timer("URLFrontierSnapshot");
		d_snapshotTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (self.get() != null) {
					self.get().snapshot();
				}
			}
		}, 0, k_SNAPSHOT_INTERVAL);
	}

	public void stop() {
		d_snapshotTimer.cancel();
		d_snapshotTimer = null;
	}
}
