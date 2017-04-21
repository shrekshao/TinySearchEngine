package com.tinysearchengine.tests;

import org.junit.runner.*;

import com.tinysearchengine.crawler.tests.URLFrontierTests;
import com.tinysearchengine.database.tests.DdbConnectorTests;
import com.tinysearchengine.utils.tests.TimedBlockingPriorityQueueTests;

public class TestDriver {
	public static void main(String[] args) {
		JUnitCore junit = new JUnitCore();
		// junit.addListener(new VerboseRunListener());
		junit.run(URLFrontierTests.class,
				TimedBlockingPriorityQueueTests.class,
				DdbConnectorTests.class);
	}
}
