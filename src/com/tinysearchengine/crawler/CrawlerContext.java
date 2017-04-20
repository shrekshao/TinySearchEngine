package com.tinysearchengine.crawler;

import com.tinysearchengine.database.DatabaseWrapper;

public class CrawlerContext {
	private DatabaseWrapper m_dbWrapper = null;
	// private URLFrontier m_URLFrontier;
	
	public CrawlerContext(DatabaseWrapper dbwrapper) {
		m_dbWrapper = dbwrapper;
	}
}
