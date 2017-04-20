package com.tinysearchengine.utils;

import java.util.UUID;

public class URLHelper {
	static public String getFingerPrint(String url) {
		// how to calculate the fingerprint for URL?
		return UUID.nameUUIDFromBytes(url.getBytes()).toString();
	}
}
