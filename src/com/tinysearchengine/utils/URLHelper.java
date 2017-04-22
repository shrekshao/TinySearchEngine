package com.tinysearchengine.utils;

import java.net.URL;
import java.util.UUID;

public class URLHelper {
	static public int getHostFingerPrint(URL url) {
		// how to calculate the fingerprint for URL?
		String host = url.getHost();
		return host.hashCode();
	}
	
	static public int getFingerPrint(URL url) {
		return url.hashCode();
	}
}
