package com.tinysearchengine.utils;

import java.util.Date;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * This class implements a delayed priority queue similar to DelayQueue, but it
 * uses absolute epoch time as a timeout.
 * 
 * @author hengchu
 *
 * @param <T>
 */
public class TimedBlockingPriorityQueue<T> {

	/**
	 * This priority queue holds objects to be released at the specified time
	 * (new Date(long)).
	 */
	private PriorityQueue<Pair<T, Long>> d_queue;
	private ReentrantLock d_queueLock = new ReentrantLock();
	private Condition d_canRelease = d_queueLock.newCondition();

	public TimedBlockingPriorityQueue() {
		d_queue = new PriorityQueue<Pair<T, Long>>((lhs, rhs) -> {
			return (int) (lhs.getRight().longValue()
					- rhs.getRight().longValue());
		});
	}

	/**
	 * Caller must make sure queue lock is acquired.
	 * 
	 * @return
	 */
	private boolean canRelease() {
		if (d_queue.isEmpty()) {
			return false;
		}

		Pair<T, Long> item = d_queue.peek();
		long now = new Date().getTime();
		return now >= item.getRight().longValue();
	}

	/**
	 * Caller must make sure queue lock is acquired.
	 * 
	 * @return
	 */
	private Date nextReleaseTime() {
		Pair<T, Long> item = d_queue.peek();
		if (item == null) {
			return new Date(Long.MAX_VALUE);
		} else {
			return new Date(item.getRight());
		}
	}

	public T get() throws InterruptedException {
		try {
			d_queueLock.lock();
			while (!canRelease()) {
				d_canRelease.awaitUntil(nextReleaseTime());
			}

			Pair<T, Long> item = d_queue.poll();
			assert item != null;

			return item.getLeft();
		} finally {
			d_queueLock.unlock();
		}
	}

	/**
	 * Put an item into the priority queue, and it can't be released before the
	 * specified time.
	 *
	 * @param item
	 * @param releaseTime
	 *            Milliseconds since epoch
	 */
	public void put(T item, long releaseTime) {
		try {
			d_queueLock.lock();
			Pair<T, Long> itemToInsert =
				new ImmutablePair<T, Long>(item, releaseTime);
			d_queue.add(itemToInsert);
			d_canRelease.signal();
		} finally {
			d_queueLock.unlock();
		}
	}

	public boolean isEmpty() {
		try {
			d_queueLock.lock();
			return d_queue.isEmpty();
		} finally {
			d_queueLock.unlock();
		}
	}

	public Pair<T, Long>[] dumpQueue(Pair<T, Long>[] repr) {
		try {
			d_queueLock.lock();
			return d_queue.toArray(repr);
		} finally {
			d_queueLock.unlock();
		}
	}
}