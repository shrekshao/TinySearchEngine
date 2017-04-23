package com.tinysearchengine.crawler;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.tinysearchengine.crawler.frontier.URLFrontier;

public class CrawlerContext {
	private AtomicInteger m_totalDocs = null;
	private URLFrontier m_URLFrontier = null;
	private Map<URL, Boolean> m_LRUdue = null;
	private String[] m_workerList = null; // ip:port
	private RobotInfoCache m_robotInfoCache = null;
	private CrawlerCluster m_cluster = null;

	public CrawlerContext(URLFrontier frontier,
			Map<URL, Boolean> due,
			String[] workerList,
			CrawlerCluster cluster,
			RobotInfoCache robotCache) {
		m_totalDocs = new AtomicInteger(0);
		m_URLFrontier = frontier;
		m_LRUdue = due;
		m_workerList = workerList;
		m_cluster = cluster;
		m_robotInfoCache = robotCache;
	}

	public void incDocsCounter() {
		m_totalDocs.incrementAndGet();
	}

	public int getDocsCounter() {
		return m_totalDocs.get();
	}

	public void putTask(URL url) {
		if (!url.getProtocol().equals("http") &&
				!url.getProtocol().equals("https")) {
			// Just ignore.
			return;
		}
		try {
			m_cluster.distributeURL(url);
		} catch (IOException e) {
			Logger logger = Logger.getLogger(CrawlerContext.class);
			logger.error("Failed to put task: " + e.getMessage(), e);
		}
	}

	public Map<URL, Boolean> getDue() {
		return m_LRUdue;
	}

	public URLFrontier getFrontier() {
		return m_URLFrontier;
	}

	public String getWorkerByIndex(int i) {
		return m_workerList[i];
	}

	public int getTotalWorker() {
		return m_workerList.length;
	}

	public RobotInfoCache getRobotInfoCache() {
		return m_robotInfoCache;
	}
}
