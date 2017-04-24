package com.tinysearchengine.crawler.frontier;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.lang3.tuple.Pair;
import com.tinysearchengine.crawler.frontier.URLFrontier.Request;

@Entity
public class URLFrontierSnapshot {
	@PrimaryKey
	public Long snapshotTime;
	
	public HashMap<Integer, Pair<Request, Long>[]> frontendQueues;
	public HashSet<Integer> emptyBackendQueues;
	public HashMap<Integer, Request[]> backendQueues;
	public HashMap<String, Integer> domainToQueue;
	public Pair<String, Long>[] domainQueue;
	public HashMap<String, Long> lastScheduledTimes;
}
