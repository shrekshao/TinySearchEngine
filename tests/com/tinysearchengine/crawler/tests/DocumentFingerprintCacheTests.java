package com.tinysearchengine.crawler.tests;

import static org.junit.Assert.*;

import org.junit.*;

import com.tinysearchengine.crawler.DocumentFingerprintCache;
import com.tinysearchengine.database.DdbConnector;

public class DocumentFingerprintCacheTests {

	DocumentFingerprintCache d_cache;

	@Before
	public void setUp() {
		d_cache = new DocumentFingerprintCache(new DdbConnector());
	}

	@Test
	public void testBreathing() {
		String content = "<h1>this is some content</h1>";
		assertTrue(d_cache.hasSeen(content.getBytes()));
		assertFalse(d_cache.hasSeen(
				"<h1>hopefully no one has this content</h1>".getBytes()));
	}
}
