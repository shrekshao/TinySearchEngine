package com.tinysearchengine.crawler;

public class DownloadTask implements Runnable {
	private String m_URL = null;
	private CrawlerContext m_context = null;
	public DownloadTask(String url, CrawlerContext context) {
		m_URL = url;
		m_context = context;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
