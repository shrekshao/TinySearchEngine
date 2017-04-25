package com.tinysearchengine.database;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import org.apache.log4j.Logger;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;

@DynamoDBTable(tableName = "DocumentTable")
public class DdbDocument {
	private URL d_url;
	private boolean d_hasNewContent = false;
	private byte[] d_content;
	private String d_contentType;
	private long d_crawledTime;
	private String d_charset;
	private byte[] d_fingerprint;
	private S3Link d_contentLink;
	private Set<String> d_links;

	@DynamoDBHashKey(attributeName = "url")
	public String getUrlAsString() {
		return d_url.toString();
	}

	public void setUrlAsString(String url) throws MalformedURLException {
		d_url = new URL(url);
	}

	@DynamoDBAttribute(attributeName = "contentLink")
	public S3Link getContentLink() {
		return d_contentLink;
	}

	public void setContentLink(S3Link link) {
		d_contentLink = link;
	}

	@DynamoDBAttribute(attributeName = "contentType")
	public String getContentType() {
		return d_contentType;
	}

	public void setContentType(String contentType) {
		d_contentType = contentType;
	}

	@DynamoDBAttribute(attributeName = "crawledTime")
	public long getCrawledTime() {
		return d_crawledTime;
	}

	public void setCrawledTime(long time) {
		d_crawledTime = time;
	}

	@DynamoDBAttribute(attributeName = "charset")
	public String getCharset() {
		return d_charset;
	}

	public void setCharset(String charset) {
		d_charset = charset;
	}

	@DynamoDBIndexHashKey(globalSecondaryIndexName = "fingerprint-index",
			attributeName = "fingerprint")
	public byte[] getFingerprint() {
		return d_fingerprint;
	}

	public void setFingerprint(byte[] fingerprint) {
		d_fingerprint = fingerprint;
	}

	@DynamoDBAttribute(attributeName = "links")
	public Set<String> getLinks() {
		return d_links;
	}

	public void setLinks(Set<String> links) {
		if (links == null || links.isEmpty()) {
			d_links = null;
		} else {
			d_links = links;
		}
	}

	@DynamoDBIgnore
	public URL getUrl() {
		return d_url;
	}

	public void setUrl(URL url) {
		d_url = url;
	}

	@DynamoDBIgnore
	public byte[] getContent() {
		if (d_content == null) {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			d_contentLink.downloadTo(output);
			d_content = output.toByteArray();
		}

		return d_content;
	}

	public void setContent(byte[] content) {
		d_hasNewContent = true;
		d_content = content;
	}

	public boolean hasNewContent() {
		return d_hasNewContent;
	}

	/**
	 * Computes a short fingerprint of the document.
	 * 
	 * @param document
	 * @return
	 */
	public static byte[] computeFingerprint(byte[] document) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			return md.digest(document);
		} catch (NoSuchAlgorithmException e) {
			Logger logger = Logger.getLogger(DdbDocument.class);
			logger.error("NoHashAlgorithm", e);
			assert false;
			return null;
		}
	}
}
