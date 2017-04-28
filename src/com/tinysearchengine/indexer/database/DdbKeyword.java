package com.tinysearchengine.indexer.database;

import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Keyword")
public class DdbKeyword {

	private String d_word;	// after stemmed
	private int d_globalCount;
	private float d_idf;		// log( N/n ), default set to 1
	
	private Set<String> d_docids;	// url
	

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
	public void setGlobalCount(int count) {
		d_globalCount = count;
	}

	@DynamoDBAttribute(attributeName = "idf")
	public float getIdf() {
		return d_idf;
	}
	public void setIdf(float idf) {
		d_idf = idf;
	}

	@DynamoDBAttribute(attributeName = "docs")
	public Set<String> getDocIds() {
		return d_docids;
	}

	public void setDocIds(Set<String> docids) {
		if (docids == null || docids.isEmpty()) {
			d_docids = null;
		} else {
			d_docids = docids;
		}
	}
	
}
