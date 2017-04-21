package com.tinysearchengine.database;

import org.apache.commons.lang3.tuple.*;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;

@Persistent(proxyFor = ImmutablePair.class)
public class ImmutablePairPersistentProxy<L, R>
		implements PersistentProxy<ImmutablePair<L, R>> {
	private L d_left;
	private R d_right;

	@Override
	public ImmutablePair<L, R> convertProxy() {
		return new ImmutablePair<>(d_left, d_right);
	}

	@Override
	public void initializeProxy(ImmutablePair<L, R> p) {
		d_left = p.getLeft();
		d_right = p.getRight();
	}
}
