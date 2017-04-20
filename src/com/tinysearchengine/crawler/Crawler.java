package com.tinysearchengine.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tinysearchengine.crawler.frontier.URLFrontier;
import com.tinysearchengine.database.DatabaseWrapper;

/**
 * @author MIAO
 *
 */
public class Crawler {
	private DatabaseWrapper m_dbWrapper = null;
	private URLFrontier m_URLFrontier = null;
	private CrawlerContext m_context = null;
	private ExecutorService m_executor = null;
	private CrawlerCluster m_cluster = null;
	
	/**
	 * @param dbDir	database directory
	 * @param port	crawler cluster listening port
	 * @param nThread	number of threads in the thread pool
	 */
	public Crawler(String dbDir, int port, int nThread) {
		m_dbWrapper = new DatabaseWrapper(dbDir);
		m_URLFrontier = new URLFrontier();
		m_context = new CrawlerContext(m_dbWrapper, m_URLFrontier);
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
			Queue<URL> queue = m_context.getTaskQueue();
			// TODO: what if the returned backend queue is empty?
			m_executor.execute(new DownloadTask(queue.poll(), m_context));
		}
	}
}
