package com.tinysearchengine.tests;

import org.apache.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class VerboseRunListener extends RunListener {

	static Logger logger = Logger.getLogger(VerboseRunListener.class);

	@Override
	public void testAssumptionFailure(Failure failure) {
		super.testAssumptionFailure(failure);
		logger.info("TestAssumptionFailure: " + failure.getMessage());
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		super.testFailure(failure);
		logger.error("TestFailure: " + failure.getClass().getName()
				+ ":"
				+ failure.getTestHeader()
				+ failure.getMessage());
		logger.error(failure.getTrace());
	}

	@Override
	public void testFinished(Description description) throws Exception {
		super.testFinished(description);
		logger.info("Test finished: " + description.getClassName()
				+ ":"
				+ description.getMethodName().toString());
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		super.testIgnored(description);
		logger.info("Test ignored: " + description.getClassName()
				+ ":"
				+ description.getMethodName().toString());
	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		super.testRunFinished(result);
		logger.info("Test run finished.");
	}

	@Override
	public void testRunStarted(Description description) throws Exception {
		super.testRunStarted(description);
		logger.info("Test run started.");
	}

	@Override
	public void testStarted(Description description) throws Exception {
		super.testStarted(description);
		logger.info("Test started: " + description.getClassName()
				+ ":"
				+ description.getMethodName().toString());
	}

}
