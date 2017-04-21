package com.tinysearchengine.crawler.frontier;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import com.tinysearchengine.crawler.frontier.URLFrontier.Request;

@Entity
public class URLFrontierSnapshot {
	@PrimaryKey
	public Long snapshotTime;
	
	public Queue<Pair<Request, Long>> frontendQueues[];
	public Set<Integer> emptyBackendQueues;
	public Queue<Request> backendQueues[];
	public Map<String, Integer> domainToQueue;
	public Pair<String, Long>[] domainQueue;
}
