package com.tinysearchengine.indexer.database;

import java.net.MalformedURLException;
import java.net.URL;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "KeywordTable")
public class DdbKeyword {

	private String d_word;
	private int d_globalCount;
	private float d_idf;		// log( N/n )

	@DynamoDBHashKey(attributeName = "word")
	public String getKeyword() {
		return d_word;
	}
	public void setKeyword(String word) {
		this.d_word = word;
	}

	@DynamoDBAttribute(attributeName = "globalCount")
	public int getGlobalCount() {
		return d_globalCount;
	}
	public void setContent(int count) {
		d_globalCount = count;
	}

	@DynamoDBAttribute(attributeName = "idf")
	public float getContentType() {
		return d_idf;
	}
	public void setContentType(float idf) {
		d_idf = idf;
	}

}
