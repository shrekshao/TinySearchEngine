package com.tinysearchengine.database.tests;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.*;

import com.tinysearchengine.database.*;

public class DdbConnectorTests {

	static DdbConnector d_connector;

	@BeforeClass
	public static void setUp() {
		d_connector = new DdbConnector();
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

	@Test
	public void testBreathing() throws MalformedURLException {
		DdbDocument doc = new DdbDocument();
		URL url = new URL("http://www.foobar.com/foobar/index.html");
		doc.setUrl(url);
		doc.setContentType("application/html");
		doc.setCrawledTime(new Date().getTime());
		doc.setCharset("UTF-8");

		Set<String> links = new HashSet<>();
		links.add("www.barfoo.com");
		links.add("www.barbaz.com");
		doc.setLinks(links);

		String s3LinkName = DdbConnector.urlToS3LinkKey(doc.getUrl());
		doc.setContentLink(d_connector.createS3Link(s3LinkName));

		String content = "<h1>this is some content</h1>";
		doc.setContent(content.getBytes());

		byte[] fp = DdbDocument.computeFingerprint(doc.getContent());
		doc.setFingerprint(fp);

		d_connector.putDocument(doc);
		DdbDocument doc2 = d_connector.getDocument(url);

		assertEquals(doc.getUrl(), doc2.getUrl());

		assertEquals(content, new String(doc2.getContent()));
		assertTrue(Arrays.equals(doc.getContent(), doc2.getContent()));
		assertEquals(doc.getContentType(), doc2.getContentType());
		assertEquals(doc.getCrawledTime(), doc2.getCrawledTime());
		assertEquals(doc.getCharset(), doc2.getCharset());
		assertTrue(doc2.getLinks().contains("www.barfoo.com"));
		assertTrue(doc2.getLinks().contains("www.barbaz.com"));
		assertFalse(doc2.getRepaired());

		System.out.println("Querying by fingerprint.");
		List<DdbDocument> docs = d_connector.getDocumentByFingerprint(fp);
		assertEquals(docs.size(), 1);
		doc2 = docs.get(0);
		assertEquals(content, new String(doc2.getContent()));
		assertTrue(Arrays.equals(doc.getContent(), doc2.getContent()));
		assertEquals(doc.getContentType(), doc2.getContentType());
		assertEquals(doc.getCrawledTime(), doc2.getCrawledTime());
		assertEquals(doc.getCharset(), doc2.getCharset());
		assertTrue(doc.getLinks().contains("www.barfoo.com"));
		assertTrue(doc.getLinks().contains("www.barbaz.com"));
	}

	@Test
	public void testLinkConversion() throws MalformedURLException {
		String[] urlsToTest = { "http://www.google.com/foobar",
				"http://www.google.com/foobar/index.html",
				"http://www.facebook.com/foobar?q=123",
				"http://www.google.com/foobar/" };

		String[] urlsToMatch = { "www.google.com/foobar",
				"www.google.com/foobar/index.html",
				"www.facebook.com/foobar?q=123",
				"www.google.com/foobar" };

		assertEquals(urlsToTest.length, urlsToMatch.length);

		for (int i = 0; i < urlsToTest.length; ++i) {
			URL url = new URL(urlsToTest[i]);
			String keyName = DdbConnector.urlToS3LinkKey(url);
			assertEquals(urlsToMatch[i], keyName);
		}
	}

	@Test
	public void testGetAllDocumentsLazily() {
		List<DdbDocument> docs = d_connector.getAllDocumentsLazily();
		Iterator<DdbDocument> docIt = docs.iterator();
		int totalIterations = 1000;
		int iteration = totalIterations;
		long startTime = System.nanoTime();
		while (iteration >= 0 && docIt.hasNext()) {
			DdbDocument doc = docIt.next();
			System.out.println(doc.getUrlAsString());
			iteration -= 1;
		}
		long dur = System.nanoTime() - startTime;

		System.out.println("Remaining it: " + iteration);
		System.out.println(
				"Avg latency: " + (dur / (long) totalIterations) + " ns");
	}

	@Test
	public void testGetNonRepairedDocuments() {
		List<DdbDocument> docs = d_connector.getAllNonRepairedDocumentsLazily();
		Iterator<DdbDocument> docIt = docs.iterator();
		int totalIterations = 100;
		int iteration = totalIterations;
		long startTime = System.nanoTime();
		while (iteration >= 0 && docIt.hasNext()) {
			DdbDocument doc = docIt.next();
			System.out.println(doc.getUrlAsString());
			assertFalse(doc.getRepaired());
			assertNull(doc.getLinks());
			iteration -= 1;
		}
		long dur = System.nanoTime() - startTime;

		System.out.println("Remaining it: " + iteration);
		System.out.println(
				"Avg latency: " + (dur / (long) totalIterations) + " ns");

	}

	@Test
	public void testGetWordDocTfTuple() {
		List<DdbWordDocTfTuple> tuples =
			d_connector.getWordDocTfTuplesForWord("start");
		Iterator<DdbWordDocTfTuple> it = tuples.iterator();
		while (it.hasNext()) {
			DdbWordDocTfTuple tuple = it.next();
			System.out.println(tuple.getUrl() + ", "
					+ tuple.getWord()
					+ ", "
					+ tuple.getTf());
		}
	}

	@Test
	public void testBatchGetPgRank() {
		List<String> urls = new LinkedList<>();
		urls.add("https://www.nytimes.com");
		urls.add("https://news.ycombinator.com/news");

		Map<String, List<Object>> results =
			d_connector.batchGetPageRankScore(urls);

		for (Map.Entry<String, List<Object>> kv : results.entrySet()) {
			System.out.println(kv.getKey());
			for (Object obj : kv.getValue()) {
				assertTrue(obj instanceof DdbPageRankScore);
				DdbPageRankScore pgScore = (DdbPageRankScore) obj;
				System.out.println(
						pgScore.getUrl() + ", " + pgScore.getPageRankScore());
			}
		}
	}

	@Test
	public void testBatchGetDocuments() {
		List<String> urls = new LinkedList<>();
		urls.add("https://www.nytimes.com");
		urls.add("https://news.ycombinator.com/news");

		Map<String, List<Object>> results = d_connector.batchGetDocuments(urls);

		for (Map.Entry<String, List<Object>> kv : results.entrySet()) {
			System.out.println(kv.getKey());
			for (Object obj : kv.getValue()) {
				assertTrue(obj instanceof DdbDocument);
				DdbDocument doc = (DdbDocument) obj;
				System.out.println(
						doc.getUrlAsString() + ", " + doc.getFingerprint());
			}
		}
	}

	@Test
	public void testBatchGetWordIdfs() {
		List<String> words = new LinkedList<>();
		words.add("comput");
		words.add("answer");

		Map<String, List<Object>> results =
			d_connector.batchGetWordIdfScores(words);
		
		for (Map.Entry<String, List<Object>> kv : results.entrySet()) {
			System.out.println(kv.getKey());
			for (Object obj : kv.getValue()) {
				assertTrue(obj instanceof DdbIdfScore);
				DdbIdfScore score = (DdbIdfScore) obj;
				System.out.println(score.getWord() + ": " + score.getIdf());
			}
		}
	}
}
