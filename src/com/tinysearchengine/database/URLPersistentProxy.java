package com.tinysearchengine.database;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;

@Persistent(proxyFor=URL.class)
public class URLPersistentProxy implements PersistentProxy<URL> {
	private String d_url;
	
	@Override
	public URL convertProxy() {
		try {
			return new URL(d_url);
		} catch (MalformedURLException e) {
			// Should be impossible.
			Logger logger = Logger.getLogger(URLPersistentProxy.class);
			logger.error(e.getStackTrace());
			return null;
		}
	}
	
	@Override
	public void initializeProxy(URL url) {
		d_url = url.toString();
	}
}
