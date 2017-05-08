package com.tinysearchengine.indexer.database;

import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;


@DynamoDBTable(tableName = "Word2UrlTf")
public class DdbWord2UrlTf {
	private String d_word;	// word
	
	private Map<String, Double> d_url2tf;	// < url, tf >
	
	
	@DynamoDBHashKey(attributeName = "word")
	public String getDocId() {
		return d_word;
	}
	public void setDocId(String word) {
		this.d_word = word;
	}

	@DynamoDBAttribute(attributeName = "url2tf")
	public Map<String, Double> getWord2tf() {
		return d_url2tf;
	}

	public void setWord2tf(Map<String, Double> url2tf) {
		if (url2tf == null || url2tf.isEmpty()) {
			d_url2tf = null;
		} else {
			d_url2tf = url2tf;
		}
	}
	
	
}
