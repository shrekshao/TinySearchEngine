package com.tinysearchengine.crawler;

import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.tinysearchengine.crawler.frontier.URLFrontier;
import com.tinysearchengine.database.DBEnv;

public class CrawlerContext {
	private DBEnv m_dbEnv = null;
	private AtomicInteger m_totalDocs = null;
	private URLFrontier m_URLFrontier = null;
	private LinkedHashMap<String, String> m_LRUdue = null;
	private String[] m_workerList = null; // ip:port
	
	public CrawlerContext(DBEnv dbEnv, URLFrontier frontier, LinkedHashMap<String, String> due, String[] workerList) {
		m_dbEnv = dbEnv;
		m_totalDocs = new AtomicInteger(0);
		m_URLFrontier = frontier;
		m_LRUdue = due;
		m_workerList = workerList;
	}
	
	public void incDocsCounter() {
		m_totalDocs.incrementAndGet();
	}
	
	public int getDocsCounter() {
		return m_totalDocs.get();
	}
	
//	public Queue<URL> getTaskQueue() {
//		return m_URLFrontier.getCurrentQueue();
//	}
	
	public void putTask(URL url) {
		// TODO: add a new task to URL frontier
	}
	
	public LinkedHashMap<String, String> getDue() {
		return m_LRUdue;
	}
	
	public URLFrontier getFrontier() {
		return m_URLFrontier;
	}
	
	public String getWorkerByIndex(int i) {
		return m_workerList[i];
	}
}
