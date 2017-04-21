package com.tinysearchengine.database.tests;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

		Logger.getRootLogger().addAppender(appender);
	}

	@Test
	public void testBreathing() throws MalformedURLException {
		DdbDocument doc = new DdbDocument();
		URL url = new URL("http://www.foobar.com/foobar/index.html");
		doc.setUrl(url);
		doc.setContentType("application/html");
		doc.setCrawledTime(new Date().getTime());
		doc.setCharset("UTF-8");
		
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

		System.out.println("Querying by fingerprint.");
		List<DdbDocument> docs = d_connector.getDocumentByFingerprint(fp);
		assertEquals(docs.size(), 1);
		doc2 = docs.get(0);
		assertEquals(content, new String(doc2.getContent()));
		assertTrue(Arrays.equals(doc.getContent(), doc2.getContent()));
		assertEquals(doc.getContentType(), doc2.getContentType());
		assertEquals(doc.getCrawledTime(), doc2.getCrawledTime());
		assertEquals(doc.getCharset(), doc2.getCharset());
	}

	@Test
	public void testLinkConversion() throws MalformedURLException {
		String[] urlsToTest = { "http://www.google.com/foobar",
				"http://www.google.com/foobar/index.html",
				"http://www.facebook.com/foobar?q=123" };

		String[] urlsToMatch = { "www.google.com/foobar",
				"www.google.com/foobar/index.html",
				"www.facebook.com/foobar?q=123" };
		
		assertEquals(urlsToTest.length, urlsToMatch.length);
		
		for (int i = 0; i < urlsToTest.length; ++i) {
			URL url = new URL(urlsToTest[i]);
			String keyName = DdbConnector.urlToS3LinkKey(url);
			assertEquals(urlsToMatch[i], keyName);
		}
	}

}
