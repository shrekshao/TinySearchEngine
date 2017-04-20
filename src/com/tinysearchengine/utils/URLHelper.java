package com.tinysearchengine.utils;

import java.util.UUID;

public class URLHelper {
	static public String getFingerPrint(String url) {
		return UUID.nameUUIDFromBytes(url.getBytes()).toString();
	}
}
