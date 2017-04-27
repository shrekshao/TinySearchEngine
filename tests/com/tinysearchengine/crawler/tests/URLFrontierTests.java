package com.tinysearchengine.crawler.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.*;
import org.aspectj.util.FileUtil;
import org.junit.*;

import com.tinysearchengine.crawler.frontier.URLFrontier;
import com.tinysearchengine.crawler.frontier.URLFrontier.Request;
import com.tinysearchengine.database.DBEnv;
import com.tinysearchengine.utils.TimedBlockingPriorityQueue;

import spark.Spark;

public class URLFrontierTests {

	static DBEnv d_testEnv;
	static URLFrontier d_frontier;

	public static void setUpStorage() {
		long now = new Date().getTime();
		File testDir = new File("./teststorage-" + now);
		testDir.mkdirs();
		d_testEnv = new DBEnv(testDir);
	}

	public static void closeStorage() {
		d_testEnv.close();
	}

	@BeforeClass
	public static void setUpLogging() {
		ConsoleAppender appender = new ConsoleAppender();
		String pattern = "[%p] %c [%t] %d: %m%n";
		appender.setLayout(new PatternLayout(pattern));
		appender.setThreshold(Level.INFO);
		appender.activateOptions();

		if (!Logger.getRootLogger().getAllAppenders().hasMoreElements()) {
			Logger.getRootLogger().addAppender(appender);
		}
	}

	@BeforeClass
	public static void setUpSpark() {
		Spark.port(8080);
	}

	@Before
	public void setUp() throws MalformedURLException, InterruptedException {
		setUpStorage();

		Set<URL> seeds = new HashSet<>();
		seeds.add(new URL("http://www.google.com"));
		d_frontier = new URLFrontier(20, seeds, d_testEnv);
		d_frontier.start();
	}

	@After
	public void tearDown() {
		d_frontier.stop();
		closeStorage();
	}

	@Test
	public void testBreathing()
			throws MalformedURLException, InterruptedException {
		long startTime = System.nanoTime();

		long now = (new Date()).getTime();
		d_frontier.put(new URL("http://www.google2.com"),
				URLFrontier.Priority.Medium,
				now + 5000);

		long lastScheduledTime =
			d_frontier.lastScheduledTime("www.google2.com");
		assertEquals(now + 5000, lastScheduledTime);

		now = (new Date()).getTime();
		d_frontier.put(new URL("http://www.facebook.com"),
				URLFrontier.Priority.Medium,
				now + 1000);

		now = (new Date()).getTime();
		d_frontier.put(new URL("http://www.apple.com"),
				URLFrontier.Priority.Medium,
				now + 2000);

		now = (new Date()).getTime();
		d_frontier.put(new URL("http://www.tesla.com"),
				URLFrontier.Priority.Medium,
				now + 3000);

		Thread.sleep(5000);

		URLFrontier.Request r = d_frontier.get();
		assertEquals("http://www.google.com", r.url.toString());

		r = d_frontier.get();
		assertEquals("http://www.facebook.com", r.url.toString());

		r = d_frontier.get();
		assertEquals("http://www.apple.com", r.url.toString());

		r = d_frontier.get();
		assertEquals("http://www.tesla.com", r.url.toString());

		r = d_frontier.get();
		assertEquals("http://www.google2.com", r.url.toString());

		long elapsedTime = System.nanoTime() - startTime;
		System.out
				.println("Took " + elapsedTime / 1000000 + " milliseconds...");
	}

	@Test
	public void testConcurrency()
			throws InterruptedException, MalformedURLException {
		URL g2 = new URL("http://www.google2.com");
		URL fb = new URL("http://www.facebook.com");
		URL ap = new URL("http://www.apple.com");
		URL ts = new URL("http://www.tesla.com");

		Thread t1 = new Thread(() -> {
			long now = (new Date()).getTime();
			try {
				d_frontier.put(g2, URLFrontier.Priority.Medium, now + 5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			now = (new Date()).getTime();
			try {
				d_frontier.put(fb, URLFrontier.Priority.Medium, now + 1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Request url;
			try {
				url = d_frontier.get();

				url = d_frontier.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "T1");

		Thread t2 = new Thread(() -> {
			long now = (new Date()).getTime();
			try {
				d_frontier.put(ap, URLFrontier.Priority.Medium, now + 2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			now = (new Date()).getTime();
			try {
				d_frontier.put(ts, URLFrontier.Priority.Medium, now + 3000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Request url;
			try {
				url = d_frontier.get();

				url = d_frontier.get();

				url = d_frontier.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "T2");

		long startTime = System.nanoTime();
		t1.start();
		t2.start();

		t1.join();
		t2.join();
		long elapsedTime = System.nanoTime() - startTime;
		System.out
				.println("Took " + elapsedTime / 1000000 + " milliseconds...");
	}

	@Test
	public void testRestoration()
			throws MalformedURLException, InterruptedException {
		long startTime = System.nanoTime();
		d_frontier.stop();

		Set<URL> seeds = new HashSet<>();
		seeds.add(new URL("http://www.google.com"));
		d_frontier = new URLFrontier(20, seeds, d_testEnv);

		long now = (new Date()).getTime();
		d_frontier.put(new URL("http://www.google2.com"),
				URLFrontier.Priority.Medium,
				now + 10000);

		long lastScheduledTime =
			d_frontier.lastScheduledTime("www.google2.com");
		System.out.println("Time for google2: " + lastScheduledTime);
		assertEquals(now + 10000, lastScheduledTime);

		now = (new Date()).getTime();
		d_frontier.put(new URL("http://www.facebook.com"),
				URLFrontier.Priority.Medium,
				now + 1000);

		now = (new Date()).getTime();
		d_frontier.put(new URL("http://www.apple.com"),
				URLFrontier.Priority.Medium,
				now + 2000);

		now = (new Date()).getTime();
		d_frontier.put(new URL("http://www.tesla.com"),
				URLFrontier.Priority.Medium,
				now + 3000);

		d_frontier.start();
		Thread.sleep(2000);
		d_frontier.stop();

		d_frontier = new URLFrontier(20, new HashSet<>(), d_testEnv);

		lastScheduledTime = d_frontier.lastScheduledTime("www.google2.com");
		System.out.println("Time for google2: " + lastScheduledTime);

		URLFrontier.Request r = d_frontier.get();
		assertEquals("http://www.google.com", r.url.toString());

		r = d_frontier.get();
		assertEquals("http://www.facebook.com", r.url.toString());

		r = d_frontier.get();
		assertEquals("http://www.apple.com", r.url.toString());

		r = d_frontier.get();
		assertEquals("http://www.tesla.com", r.url.toString());

		r = d_frontier.get();
		assertEquals("http://www.google2.com", r.url.toString());

		long elapsedTime = System.nanoTime() - startTime;
		System.out
				.println("Took " + elapsedTime / 1000000 + " milliseconds...");

	}

	private Map<Integer, Long>
			setupDelayUrls(TimedBlockingPriorityQueue<Integer> q, int numUrls)
					throws MalformedURLException, InterruptedException {
		Map<Integer, Long> delayTime = new HashMap<>();
		Random gen = new Random();
		for (int i = 0; i < numUrls; ++i) {
			// A random delay less than 10 seconds, larger than 5 seconds.
			long delay = gen.nextInt(5) + 5;
			long releaseTime = System.currentTimeMillis() + delay * 1000;
			q.put(i, releaseTime);
			delayTime.put(i, releaseTime);
		}

		return delayTime;
	}

	@Test
	public void testTimedQueueDelay()
			throws MalformedURLException, InterruptedException {
		long start = System.currentTimeMillis();
		TimedBlockingPriorityQueue<Integer> q =
			new TimedBlockingPriorityQueue<>();
		Map<Integer, Long> delay = setupDelayUrls(q, 10000);
		long end = System.currentTimeMillis();

		System.out.println("Setup took " + (end - start) + " ms");

		System.out.println("Getting: " + delay.size() + " items");
		start = System.currentTimeMillis();
		Map<Integer, Long> readTime = new HashMap<>();
		for (int i = 0; i < delay.size(); ++i) {
			Integer v = q.get();
			readTime.put(v, System.currentTimeMillis());
		}
		end = System.currentTimeMillis();
		System.out.println(
				"Experiment took: " + (end - start) * 1.0 / 1000 + " seconds.");

		long maxDelay = 0;
		long avgDelay = 0;
		long minDelay = Long.MAX_VALUE;
		for (Map.Entry<Integer, Long> kv : delay.entrySet()) {
			Integer i = kv.getKey();
			Long readT = readTime.get(i);
			if (readT != null) {
				long d = readT - kv.getValue();
				if (d > maxDelay) {
					maxDelay = d;
				}
				if (d < minDelay) {
					minDelay = d;
				}
				avgDelay += d;
			}
		}

		System.out.println("Max delay: " + maxDelay);
		System.out.println("Min delay: " + minDelay);
		System.out.println("Avg delay: " + avgDelay * 1.0 / delay.size());
	}

	private void setupFrontierForDelayTesting(int numItems)
			throws MalformedURLException {
		for (int i = 0; i < numItems; ++i) {
			String urlStr = "http://" + i + ".com";
			URL url = new URL(urlStr);

		}
	}

	@Test
	public void testFrontierDelay() {

	}
}
