package com.tinysearchengine.utils.tests;

import static org.junit.Assert.*;

import java.util.Date;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.*;

import com.tinysearchengine.utils.TimedBlockingPriorityQueue;

public class TimedBlockingPriorityQueueTests {

	@Test
	public void testBreathing() throws InterruptedException {
		TimedBlockingPriorityQueue<Integer> q =
			new TimedBlockingPriorityQueue<Integer>();

		long now = new Date().getTime();
		Pair<Integer, Long> p1 =
			new ImmutablePair<Integer, Long>(2, now + 1000);

		now = new Date().getTime();
		Pair<Integer, Long> p2 =
			new ImmutablePair<Integer, Long>(2, now + 2000);

		now = new Date().getTime();
		Pair<Integer, Long> p3 =
			new ImmutablePair<Integer, Long>(2, now + 3000);

		q.put(p3.getLeft(), p3.getRight());
		q.put(p1.getLeft(), p1.getRight());
		q.put(p2.getLeft(), p2.getRight());

		Integer result = q.get();
		now = new Date().getTime();
		assertTrue(now + " >= " + p1.getRight(),
				now >= p1.getRight().longValue());
		assertEquals(p1.getLeft(), result);

		result = q.get();
		now = new Date().getTime();
		assertTrue(now + " >= " + p2.getRight(),
				now >= p2.getRight().longValue());
		assertEquals(p2.getLeft(), result);

		result = q.get();
		now = new Date().getTime();
		assertTrue(now + " >= " + p3.getRight(),
				now >= p3.getRight().longValue());
		assertEquals(p3.getLeft(), result);
	}
}
