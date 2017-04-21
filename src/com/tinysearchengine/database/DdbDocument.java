package com.tinysearchengine.database;

import java.net.MalformedURLException;
import java.net.URL;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "DocumentTable")
public class DdbDocument {
	private URL d_url;
	private byte[] d_content;
	private String d_contentType;
	private long d_crawledTime;
	private String d_charset;

	@DynamoDBHashKey(attributeName = "url")
	public String getUrlAsString() {
		return d_url.toString();
	}
	public void setUrlAsString(String url) throws MalformedURLException {
		d_url = new URL(url);
	}

	@DynamoDBAttribute(attributeName = "content")
	public byte[] getContent() {
		return d_content;
	}
	public void setContent(byte[] content) {
		d_content = content;
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
	
	@DynamoDBIgnore
	public URL getUrl() {
		return d_url;
	}
	public void setUrl(URL url) {
		d_url = url;
	}

}
