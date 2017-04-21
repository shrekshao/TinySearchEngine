package com.tinysearchengine.database.tests;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

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
		URL url = new URL("http://www.foobar.com"); 
		doc.setUrl(url);
		doc.setContentType("application/html");
		doc.setCrawledTime(new Date().getTime());
		doc.setCharset("UTF-8");
		
		String content = "<h1>this is some content</h1>"; 
		doc.setContent(content.getBytes());
		
		d_connector.putDocument(doc);
		DdbDocument doc2 = d_connector.getDocument(url);
		
		assertEquals(doc.getUrl(), doc2.getUrl());
		
		assertEquals(content, new String(doc2.getContent()));
		assertTrue(Arrays.equals(doc.getContent(), doc2.getContent()));
		assertEquals(doc.getContentType(), doc2.getContentType());
		assertEquals(doc.getCrawledTime(), doc2.getCrawledTime());
		assertEquals(doc.getCharset(), doc2.getCharset());
	}
}
