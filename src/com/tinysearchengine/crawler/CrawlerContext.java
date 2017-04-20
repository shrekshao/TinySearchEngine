package com.tinysearchengine.crawler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.tinysearchengine.database.DatabaseWrapper;

public class CrawlerContext {
	private DatabaseWrapper m_dbWrapper = null;
	private AtomicInteger m_totalDocs = null;
	private Queue<Runnable> m_taskQueue = null;
	// private URLFrontier m_URLFrontier;
	
	public CrawlerContext(DatabaseWrapper dbwrapper) {
		m_dbWrapper = dbwrapper;
		m_totalDocs = new AtomicInteger(0);
		m_taskQueue = new ConcurrentLinkedQueue<Runnable>();
	}
	
	public void incDocsCounter() {
		m_totalDocs.incrementAndGet();
	}
	
	public int getDocsCounter() {
		return m_totalDocs.get();
	}
	
	public Queue<Runnable> getTaskQueue() {
		// TODO: should return the back queue which has the least timestamp
		return m_taskQueue;
	}
	
	public void addTask(Runnable task) {
		m_taskQueue.add(task);
	}
}
