package com.tinysearchengine.crawler;

import java.util.List;
import java.util.Map;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
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
	private Map<byte[], Boolean> d_cache;

	/**
	 * The connector to dynamo db.
	 */
	private DdbConnector d_connector;

	public DocumentFingerprintCache(DdbConnector connector) {
		d_connector = connector;

		ConcurrentLinkedHashMap.Builder<byte[], Boolean> builder =
			new ConcurrentLinkedHashMap.Builder<>();
		d_cache = builder
				.concurrencyLevel(10)
				.initialCapacity(2000)
				.maximumWeightedCapacity(k_MAX_ENTRIES).build();
	}

	/**
	 * Add the given fingerprint to the cache.
	 * 
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
