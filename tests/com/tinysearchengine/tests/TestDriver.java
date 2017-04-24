package com.tinysearchengine.tests;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.runner.*;

import com.tinysearchengine.crawler.tests.DocumentFingerprintCacheTests;
import com.tinysearchengine.crawler.tests.RobotInfoCacheTests;
import com.tinysearchengine.crawler.tests.URLFrontierTests;
import com.tinysearchengine.database.tests.DdbConnectorTests;
import com.tinysearchengine.utils.tests.TimedBlockingPriorityQueueTests;

public class TestDriver {
	
	public static void setUpLogging() {
		ConsoleAppender appender = new ConsoleAppender();
		String pattern = "[%p] %c [%t] %d: %m%n";
		appender.setLayout(new PatternLayout(pattern));
		appender.setThreshold(Level.INFO);
		appender.activateOptions();

		Logger.getRootLogger().addAppender(appender);
	}
	
	public static void main(String[] args) {
		setUpLogging();
		
		JUnitCore junit = new JUnitCore();
		junit.addListener(new VerboseRunListener());
		junit.run(URLFrontierTests.class,
				TimedBlockingPriorityQueueTests.class,
				DdbConnectorTests.class,
				RobotInfoCacheTests.class,
				DocumentFingerprintCacheTests.class);
	}
}
