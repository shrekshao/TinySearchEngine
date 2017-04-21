package com.tinysearchengine.tests;

import org.junit.*;
import org.junit.internal.TextListener;
import org.junit.runner.*;

import com.tinysearchengine.crawler.tests.URLFrontierTests;
import com.tinysearchengine.database.DdbConnector;
import com.tinysearchengine.utils.tests.TimedBlockingPriorityQueueTests;

public class TestDriver {
	public static void main(String[] args) {
		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener(System.out));
		junit.runClasses(URLFrontierTests.class,
				TimedBlockingPriorityQueueTests.class,
				DdbConnector.class);
	}
}
