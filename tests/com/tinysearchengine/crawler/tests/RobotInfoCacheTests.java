package com.tinysearchengine.crawler.tests;

import org.junit.*;
import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;

import com.tinysearchengine.crawler.RobotInfoCache;
import com.tinysearchengine.crawler.RobotInfoCache.RobotInfo;

public class RobotInfoCacheTests {

	static final String k_SAMPLE_ROBOT_TXT =
		"User-agent: *\n" + "Disallow: /foo\n"
				+ "Disallow: /bar\n"
				+ "Disallow: /privatestuff\n"
				+ "Crawl-delay: 50\n"
				+ "User-agent: BadRobot\n"
				+ "Disallow: /\n"
				+ "User-agent: GoodRobot\n"
				+ "Disallow:";

	RobotInfoCache d_cache;

	@Before
	public void setUp() {
		d_cache = new RobotInfoCache();
	}

	@Test
	public void testParser() {
		RobotInfoCache.RobotInfo info =
			RobotInfoCache.parserRobotTxt(k_SAMPLE_ROBOT_TXT);

		assertTrue(info.getDisallowedLinks("*").contains("/foo"));
		assertTrue(info.getDisallowedLinks("*").contains("/bar"));
		assertTrue(info.getDisallowedLinks("*").contains("/privatestuff"));
		assertEquals(info.getCrawlDelay("*"), 50);
		assertTrue(info.getDisallowedLinks("BadRobot").contains("/"));
		assertTrue(info.getDisallowedLinks("GoodRobot") == null);
	}

	@Test
	public void testGetRobotInfo() throws MalformedURLException {
		long start = System.currentTimeMillis();
		URL url = new URL("http://crawltest.cis.upenn.edu/nytimes/");
		RobotInfo info = d_cache.getInfoForUrl(url);
		long dur = System.currentTimeMillis() - start;

		System.out.println("First retrieval took " + dur + " milliseconds.");

		assertNotNull(info);
		assertTrue(info.getDisallowedLinks("*").contains("/marie/"));
		assertTrue(info.getDisallowedLinks("cis455crawler")
				.contains("/marie/private/"));
		assertTrue(info.getDisallowedLinks("cis455crawler").contains("/foo/"));
		assertTrue(info.getDisallowedLinks("evilcrawler").contains("/"));

		assertEquals(10, info.getCrawlDelay("*"));
		assertEquals(5, info.getCrawlDelay("cis455crawler"));

		start = System.currentTimeMillis();
		info = d_cache.getInfoForUrl(url);
		long dur2 = System.currentTimeMillis() - start;

		System.out.println("Second retrieval took " + dur2 + " milliseconds.");

		assertTrue(dur2 < dur);
	}

	@Test
	public void testDisallowCrawlTest() throws MalformedURLException {
		URL url = new URL("http://crawltest.cis.upenn.edu/nytimes/");
		RobotInfo info = d_cache.getInfoForUrl(url);

		assertFalse(RobotInfoCache.canCrawl(info,
				new URL("http://crawltest.cis.upenn.edu/marie"),
				"cis455crawler"));
		assertFalse(RobotInfoCache.canCrawl(info,
				new URL("http://crawltest.cis.upenn.edu/marie/tpc"),
				"cis455crawler"));
		assertFalse(RobotInfoCache.canCrawl(info,
				new URL("http://crawltest.cis.upenn.edu/marie/foo"),
				"cis455crawler"));
		assertFalse(RobotInfoCache.canCrawl(info,
				new URL("http://crawltest.cis.upenn.edu/foo"),
				"cis455crawler"));
		assertFalse(RobotInfoCache.canCrawl(info,
				new URL("http://crawltest.cis.upenn.edu/foo/"),
				"cis455crawler"));
		assertTrue(RobotInfoCache.canCrawl(info,
				new URL("http://crawltest.cis.upenn.edu/bbc/"),
				"cis455crawler"));
		assertTrue(RobotInfoCache.canCrawl(info,
				new URL("http://crawltest.cis.upenn.edu/nytimes/"),
				"cis455crawler"));
	}

	@Test
	public void testDisallowWikipedia() throws MalformedURLException {
		URL url = new URL("https://en.wikipedia.org");
		RobotInfo info = d_cache.getInfoForUrl(url);

		assertNotNull(info);
		assertFalse(RobotInfoCache.canCrawl(info,
				new URL("https://en.wikipedia.org/w/"),
				"cis455crawler"));
		assertFalse(RobotInfoCache.canCrawl(info,
				new URL("https://en.wikipedia.org/api/"),
				"cis455crawler"));
		assertFalse(RobotInfoCache.canCrawl(info,
				new URL("https://en.wikipedia.org/trap/"),
				"cis455crawler"));
		assertTrue(RobotInfoCache.canCrawl(info,
				new URL("https://en.wikipedia.org/wiki/Turkey"),
				"cis455crawler"));

	}

	@Test
	public void testDisallowedHackerNews() throws MalformedURLException {
		URL url = new URL(
				"https://news.ycombinator.com/vote?id=14152279&how=up&goto=show");
		
		RobotInfo info = d_cache.getInfoForUrl(url);
		assertNotNull(info);
		assertFalse(RobotInfoCache.canCrawl(info, url, "cis455crawlerg05"));
	}
}
