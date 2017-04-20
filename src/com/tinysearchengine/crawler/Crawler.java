package com.tinysearchengine.crawler;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tinysearchengine.database.DatabaseWrapper;

public class Crawler {
	private CrawlerContext m_context = null;
	private CrawlerCluster m_cluster = null;
	private DatabaseWrapper m_dbWrapper = null;
	ExecutorService m_executor = null;
	
	public Crawler(String dbDir, int port, int nThread) {
		m_dbWrapper = new DatabaseWrapper(dbDir);
		m_context = new CrawlerContext(m_dbWrapper);
		m_executor = Executors.newFixedThreadPool(nThread);
		m_cluster = new CrawlerCluster(port, m_context);
	}
	
	public void crawl(String[] startURLs, int nMaxDocs) {
		// start from startURLs
		for (String url : startURLs) {
			m_context.addTask(new DownloadTask(url, m_context));
		}
		
		while (m_context.getDocsCounter() < nMaxDocs) {
			Queue<Runnable> queue = m_context.getTaskQueue();
			Runnable task = queue.poll();
			if (task != null)
				m_executor.execute(task);
		}
	}
}
