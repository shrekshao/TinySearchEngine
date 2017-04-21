package com.tinysearchengine.tests;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class VerboseRunListener extends RunListener {

	@Override
	public void testAssumptionFailure(Failure failure) {
		super.testAssumptionFailure(failure);
		System.out.print(failure.toString());
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		super.testFailure(failure);
		System.out.print(failure.toString());
	}

	@Override
	public void testFinished(Description description) throws Exception {
		super.testFinished(description);
		System.out.print(description.toString());
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		super.testIgnored(description);
		System.out.println(description.toString());
	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		super.testRunFinished(result);
		System.out.println(result.toString());
	}

	@Override
	public void testRunStarted(Description description) throws Exception {
		super.testRunStarted(description);
		System.out.println(description.toString());
	}

	@Override
	public void testStarted(Description description) throws Exception {
		super.testStarted(description);
		System.out.print(description.toString());
	}

}
