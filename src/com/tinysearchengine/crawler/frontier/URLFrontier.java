package com.tinysearchengine.crawler.frontier;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import com.tinysearchengine.utils.TimedBlockingPriorityQueue;

import java.util.Map;

public class URLFrontier {
	/**
	 * This represents the priority of a given URL to be enqueued into the
	 * frontier.
	 * 
	 * @author hengchu
	 *
	 */
	static public enum Priority {
		Low(0), Medium(1), High(2);

		private final int d_value;

		Priority(int value) {
			d_value = value;
		}

		public int toInt() {
			return d_value;
		}
	}

	static public class Request {
		/**
		 * The URL to execute the request on.
		 */
		URL url;

		/**
		 * The method to execute, either HEAD or GET.
		 */
		String method;
	}

	/**
	 * The 3 front end queues.
	 */
	Queue<URL>[] d_frontendQueues = new Queue[3];

	/**
	 * The set of backend queues, the number of backend queues is determined by
	 * the number of download threads * 3.
	 */
	Queue<URL>[] d_backendQueues;

	/**
	 * The indices of empty backend queues.
	 */
	Set<Integer> d_emptyBackendQueues = new HashSet<>();

	/**
	 * The map from domain strings to queue indices.
	 */
	Map<String, Integer> d_domainToQueue = new HashMap<>();

	/**
	 * The map from domain strings to when their last request was scheduled.
	 */
	Map<String, Long> d_lastScheduledTime = new HashMap<>();

	/**
	 * The priority queue from where we fetch the next domains.
	 */
	TimedBlockingPriorityQueue<String> d_domainQueue =
		new TimedBlockingPriorityQueue<>();
	
	public Queue<URL> getCurrentQueue() {
		// TODO: return the current (non-empty?) backend queue
		return d_backendQueues[0];
	}
}
