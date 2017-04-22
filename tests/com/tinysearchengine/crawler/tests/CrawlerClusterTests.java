package com.tinysearchengine.crawler.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.tinysearchengine.crawler.CrawlerCluster;
import com.tinysearchengine.crawler.CrawlerContext;
import com.tinysearchengine.crawler.frontier.URLFrontier;
import com.tinysearchengine.database.DBEnv;

public class CrawlerClusterTests {
	static DBEnv d_testEnv;
	CrawlerCluster d_cluster;
	CrawlerContext d_context;
	URLFrontier d_frontier;
	LinkedHashMap<String, String> d_LRUdue;
	
	@BeforeClass
	public static void setUpStorage() {
		File testDir = new File("./testclusterstorage");
		testDir.mkdirs();
		d_testEnv = new DBEnv(testDir);	
	}
	
	@Before
	public void setUp() throws MalformedURLException {
		Set<URL> seeds = new HashSet<>();
		seeds.add(new URL("http://www.google.com"));
		d_frontier = new URLFrontier(20, seeds, d_testEnv);
		d_frontier.start();
		
		d_LRUdue = new LinkedHashMap<String, String>(20000);
		//d_context = new CrawlerContext(d_testEnv, d_frontier, d_LRUdue);
		// d_cluster = new CrawlerCluster(8080, d_context);
	}
	
	@After
	public void tearDown() {
		d_frontier.stop();
	}
	
	/*
	@Test
	public void testResponse() throws IOException {
		URL url = new URL("http://127.0.0.1/pushdata");
				
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		
		String body = "url=http://www.apple.com";

		OutputStream os = conn.getOutputStream();
		byte[] toSend = body.getBytes();
		os.write(toSend);
		
		assertEquals(conn.getResponseCode(), HttpURLConnection.HTTP_OK);
	}
	*/
}
