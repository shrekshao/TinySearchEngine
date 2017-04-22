package com.tinysearchengine.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tinysearchengine.crawler.frontier.URLFrontier;
import com.tinysearchengine.database.DBEnv;

/**
 * @author MIAO
 *
 */
public class Crawler {
	private DBEnv m_dbEnv = null;
	private URLFrontier m_URLFrontier = null;
	private CrawlerContext m_context = null;
	private ExecutorService m_executor = null;
	private CrawlerCluster m_cluster = null;
	private LinkedHashMap<String, String> m_LRUdue = null;
	
	/**
	 * @param dbDir	database directory
	 * @param port	crawler cluster listening port
	 * @param nThread	number of threads in the thread pool
	 */
	public Crawler(String dbDir, int port, int nThread) {
		m_dbEnv = new DBEnv(new File(dbDir));
		m_LRUdue = new LinkedHashMap<String, String>(20000);
		//m_URLFrontier = new URLFrontier();
		m_context = new CrawlerContext(m_dbEnv, m_URLFrontier, m_LRUdue, null);
		m_executor = Executors.newFixedThreadPool(nThread);
		m_cluster = new CrawlerCluster(port, m_context);
	}
	
	/**
	 * @param startURLs
	 * @param nMaxDocs
	 * @throws MalformedURLException
	 */
	public void crawl(String[] startURLs, int nMaxDocs) throws MalformedURLException {
		// start from startURLs
		for (String url : startURLs) {
			m_context.putTask(new URL(url));
		}
		
		while (m_context.getDocsCounter() < nMaxDocs) {
			// Queue<URL> queue = m_context.getTaskQueue();
			// TODO: what if the returned backend queue is empty?
			// m_executor.execute(new DownloadTask(queue.poll(), m_context));
		}
	}
	
	public static void main(String[] args) throws IOException {
		DBEnv m_dbEnv = null;
		URLFrontier m_URLFrontier = null;
		CrawlerContext m_context = null;
		CrawlerCluster m_cluster = null;
		LinkedHashMap<String, String> m_LRUdue = null;
		
		File dbDir = new File(args[1]);
		dbDir.mkdirs();
		m_dbEnv = new DBEnv(dbDir);
		
		m_LRUdue = new LinkedHashMap<String, String>(20000);
		m_context = new CrawlerContext(m_dbEnv, m_URLFrontier, m_LRUdue, null);
		m_cluster = new CrawlerCluster(Integer.parseInt(args[0]), m_context);
		
		System.out.println("Press [Enter] to exit...");
		(new BufferedReader(new InputStreamReader(System.in))).readLine();
	}
}
