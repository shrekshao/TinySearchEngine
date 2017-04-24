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
import java.util.concurrent.ConcurrentHashMap;

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
	 * Every 10 seconds.
	 */
	private final static long k_STAT_INTERVAL = 1000 * 10;

	/**
	 * Number of milliseconds in a day.
	 */
	private final static long k_ONE_DAY = 1000 * 3600 * 24;

	/**
	 * Maximum size of the frontier.
	 */
	private final static long k_MAX_SIZE = 1500000;

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

	static public class URLFrontierStats {
		public int lowFrontendQueueCount;
		public int medFrontendQueueCount;
		public int highFrontendQueueCount;
		public String nextUrl;
		public long nextUrlReleaseTime;
		public String nextUrlReleaseTimeStr;
		public Map<String, Integer> backendQueueCounts;
		public String collectedAtTime;
		public long frontierSize;
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
	Map<String, Long> d_lastScheduledTime = new ConcurrentHashMap<>();

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

	/**
	 * The timer used to start collecting stats.
	 */
	Timer d_statTimer;

	/**
	 * The last stat object.
	 */
	private volatile URLFrontierStats d_lastStat = new URLFrontierStats();

	/**
	 * Current size of the frontier.
	 */
	private long d_frontierSize = 0;

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

	public URLFrontier(int numBackendQueues, Set<URL> seeds, DBEnv dbEnv)
			throws InterruptedException {
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
			removeOldSnapshots(snapshot.snapshotTime - k_ONE_DAY);
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

	private synchronized URLFrontierStats getStats() {
		URLFrontierStats stats = new URLFrontierStats();
		stats.lowFrontendQueueCount = d_frontendQueues[0].size();
		stats.medFrontendQueueCount = d_frontendQueues[1].size();
		stats.highFrontendQueueCount = d_frontendQueues[2].size();
		stats.backendQueueCounts = new HashMap<>();

		for (Map.Entry<String, Integer> d2bidx : d_domainToQueue.entrySet()) {
			stats.backendQueueCounts.put(d2bidx.getKey(),
					d_backendQueues[d2bidx.getValue()].size());
		}

		Pair<String, Long> domain = d_domainQueue.peek();

		if (domain == null) {
			stats.nextUrl = "Unknown";
			stats.nextUrlReleaseTime = 0;
			stats.nextUrlReleaseTimeStr = "Unknown";
		} else {
			int qid = d_domainToQueue.get(domain.getLeft());
			Request req = d_backendQueues[qid].peek();
			stats.nextUrl = req.url.toString();
			stats.nextUrlReleaseTime = domain.getRight();
			stats.nextUrlReleaseTimeStr =
				new Date(domain.getRight()).toString();
		}

		assert stats.nextUrl != null;
		assert stats.nextUrlReleaseTimeStr != null;

		stats.collectedAtTime = new Date().toString();
		return stats;
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

			d_frontierSize--;
			this.notify();
			return req;
		}
	}

	public void put(URL url, Priority priority, long releaseTime)
			throws InterruptedException {
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
			long releaseTime) throws InterruptedException {
		Logger logger = Logger.getLogger(URLFrontier.class);
		logger.debug("Putting url: " + req.url
				+ ", method: "
				+ req.method
				+ ", priority: "
				+ priority.toString()
				+ ", releasing at: "
				+ new Date(releaseTime).toString());

		while (d_frontierSize > k_MAX_SIZE) {
			logger.warn("Maximum size reached, throttling frontier put!");
			this.wait();
		}

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
		if (lastScheduledTime == null || releaseTime > lastScheduledTime) {
			d_lastScheduledTime.put(domain, releaseTime);
		}
		d_frontierSize++;
	}

	public long lastScheduledTime(String domain) {
		Long time = d_lastScheduledTime.get(domain);
		if (time == null) {
			return (new Date()).getTime();
		}

		return time.longValue();
	}

	/**
	 * Select a frontend queue based on the following intervals: [0, 10] -> low
	 * priority [10, 50] -> medium priority [50, 200] -> high priority
	 * 
	 * @return
	 */
	private int selectFrontendQueue() {
		int lowPrioEnd = d_frontendQueues[0].isEmpty() ? 0 : 10;
		int medPrioEnd = d_frontendQueues[1].isEmpty() ? lowPrioEnd : 50;
		int hiPrioEnd = d_frontendQueues[2].isEmpty() ? medPrioEnd : 200;

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
			snap.frontendQueues = new HashMap<>();
			for (int i = 0; i < d_frontendQueues.length; ++i) {
				snap.frontendQueues.put(i,
						d_frontendQueues[i].toArray(new Pair[0]));
			}

			snap.emptyBackendQueues = new HashSet<>(d_emptyBackendQueues);

			snap.backendQueues = new HashMap<>();
			for (int i = 0; i < d_backendQueues.length; ++i) {
				snap.backendQueues.put(i,
						d_backendQueues[i].toArray(new Request[0]));
			}

			snap.domainToQueue = new HashMap<>(d_domainToQueue);
			snap.domainQueue = d_domainQueue.dumpQueue(new Pair[0]);
			snap.lastScheduledTimes = new HashMap<>(d_lastScheduledTime);
			snap.frontierSize = d_frontierSize;
		}

		d_snapshotByTime.put(snap);
		d_dbEnv.getStore().sync();

		removeOldSnapshots(snap.snapshotTime - k_ONE_DAY);

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
		Logger logger = Logger.getLogger(URLFrontier.class);
		long now = new Date().getTime();
		long dur = now - snapshot.snapshotTime;
		logger.info("Restoration shifts time by: " + dur + " ms.");

		d_backendQueues = new Queue[snapshot.backendQueues.size()];
		for (int i = 0; i < d_backendQueues.length; ++i) {
			d_backendQueues[i] = new LinkedList<>();
			Request[] reqs = snapshot.backendQueues.get(i);
			for (int j = 0; j < reqs.length; ++j) {
				d_backendQueues[i].offer(reqs[j]);
			}
		}

		d_domainToQueue = new HashMap<>(snapshot.domainToQueue);
		d_frontendQueues = new Queue[snapshot.frontendQueues.size()];
		for (int i = 0; i < d_frontendQueues.length; ++i) {
			d_frontendQueues[i] = new LinkedList<>();
			Pair<Request, Long>[] reqs = snapshot.frontendQueues.get(i);
			for (int j = 0; j < reqs.length; ++j) {
				d_frontendQueues[i].offer(reqs[j]);
			}
		}

		d_emptyBackendQueues = new HashSet<>(snapshot.emptyBackendQueues);

		for (int i = 0; i < snapshot.domainQueue.length; ++i) {
			String domain = snapshot.domainQueue[i].getLeft();
			long releaseTime = snapshot.domainQueue[i].getRight() + dur;
			d_domainQueue.put(domain, releaseTime);
		}

		d_lastScheduledTime =
			new ConcurrentHashMap<>(snapshot.lastScheduledTimes);
		for (Map.Entry<String, Long> entry : d_lastScheduledTime.entrySet()) {
			String domain = entry.getKey();
			Long time = entry.getValue() + dur;
			d_lastScheduledTime.put(domain, time);
		}

		d_frontierSize = snapshot.frontierSize;
	}

	private void removeOldSnapshots(long olderThan) {
		Logger logger = Logger.getLogger(URLFrontier.class);
		EntityCursor<Long> keyCursor = d_snapshotByTime.keys();
		try {
			Long time = null;
			while ((time = keyCursor.next()) != null) {
				if (time < olderThan) {
					logger.info("Removing snapshot from: "
							+ new Date(time).toString());
					keyCursor.delete();
				}
			}
		} finally {
			keyCursor.close();
			d_dbEnv.getStore().sync();
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

		d_statTimer = new Timer("URLFrontierStats");
		d_statTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (self.get() != null) {
					d_lastStat = self.get().getStats();
				}
			}
		}, 0, k_STAT_INTERVAL);
	}

	public void stop() {
		if (d_snapshotTimer != null) {
			d_snapshotTimer.cancel();
			d_snapshotTimer = null;
		}

		if (d_statTimer != null) {
			d_statTimer.cancel();
			d_statTimer = null;
		}
	}

	public URLFrontierStats stats() {
		return d_lastStat;
	}
}
