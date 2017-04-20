package com.tinysearchengine.crawler;

import com.tinysearchengine.database.DatabaseWrapper;

public class Crawler {
	private CrawlerContext m_context = null;
	private CrawlerCluster m_cluster = null;
	private DatabaseWrapper m_dbWrapper = null;
	
	public Crawler(String dbDir, int port) {
		m_dbWrapper = new DatabaseWrapper(dbDir);
		m_context = new CrawlerContext(m_dbWrapper);
		m_cluster = new CrawlerCluster(port, m_context);
	}
	
	public void crawl(String[] startURL) {
		
	}
}
