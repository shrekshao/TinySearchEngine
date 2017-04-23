package com.tinysearchengine.crawler.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.*;
import org.aspectj.util.FileUtil;
import org.junit.*;

import com.tinysearchengine.crawler.frontier.URLFrontier;
import com.tinysearchengine.crawler.frontier.URLFrontier.Request;
import com.tinysearchengine.database.DBEnv;

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
	public void setUp() throws MalformedURLException {
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
			d_frontier.put(g2, URLFrontier.Priority.Medium, now + 5000);

			now = (new Date()).getTime();
			d_frontier.put(fb, URLFrontier.Priority.Medium, now + 1000);

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
			d_frontier.put(ap, URLFrontier.Priority.Medium, now + 2000);

			now = (new Date()).getTime();
			d_frontier.put(ts, URLFrontier.Priority.Medium, now + 3000);

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

		long now = (new Date()).getTime();
		d_frontier.put(new URL("http://www.google2.com"),
				URLFrontier.Priority.Medium,
				now + 10000);

		long lastScheduledTime =
			d_frontier.lastScheduledTime("www.google2.com");
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

		Thread.sleep(5000);
		d_frontier.stop();
		d_frontier = new URLFrontier(20, new HashSet<>(), d_testEnv);
		d_frontier.start();

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
}
