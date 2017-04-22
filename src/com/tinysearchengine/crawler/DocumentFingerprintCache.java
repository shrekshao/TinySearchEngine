package com.tinysearchengine.crawler;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.tinysearchengine.database.DdbConnector;
import com.tinysearchengine.database.DdbDocument;

/**
 * This class implements an LRU cache on document fingerprints, thus providing a
 * way for testing whether we have seen the content of a document.
 * 
 * @author hengchu
 *
 */
public class DocumentFingerprintCache {
	/**
	 * Allow 50,000 entries at most.
	 */
	private final static int k_MAX_ENTRIES = 50000;

	/**
	 * The cache from
	 */
	private LinkedHashMap<byte[], Boolean> d_cache =
		new LinkedHashMap<byte[], Boolean>(2000) {
			private static final long serialVersionUID = -174763602709570347L;

			@Override
			protected boolean
					removeEldestEntry(Map.Entry<byte[], Boolean> entry) {
				if (size() > k_MAX_ENTRIES) {
					return true;
				}

				return false;
			}
		};

	/**
	 * The connector to dynamo db.
	 */
	private DdbConnector d_connector;
	
	public DocumentFingerprintCache(DdbConnector connector) {
		d_connector = connector;
	}
	
	/**
	 * Add the given fingerprint to the cache.
	 * @param fp
	 */
	public synchronized void addFingerprint(byte[] fp) {
		d_cache.put(fp, true);
	}

	/**
	 * Returns true if we've seen this content already. Note that it queries Ddb
	 * if we don't have the fingerprint cached.
	 * 
	 * @param content
	 * @return
	 */
	public boolean hasSeen(byte[] content) {
		byte[] fp = DdbDocument.computeFingerprint(content);
		synchronized (this) {
			if (d_cache.containsKey(fp)) {
				return true;
			}
		}

		List<DdbDocument> docs = d_connector.getDocumentByFingerprint(fp);
		if (docs.isEmpty()) {
			return false;
		} else {
			synchronized (this) {
				d_cache.put(fp, true);
			}

			return true;
		}
	}
}
