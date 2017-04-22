package com.tinysearchengine.crawler;

import java.net.URL;

public class DownloadTask implements Runnable {
	private URL m_URL = null;
	private CrawlerContext m_context = null;
	public DownloadTask(URL url, CrawlerContext context) {
		m_URL = url;
		m_context = context;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
