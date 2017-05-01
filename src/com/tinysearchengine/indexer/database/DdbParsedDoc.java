package com.tinysearchengine.indexer.database;

import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "ParsedDoc")
public class DdbParsedDoc {
	private String d_docid;	// url or S3 link?
	
	private int d_numWords;	// total number of keywords in this doc
	
//	private float d_pageRankScore = 1.0f;	// default set to 1
	
	private Map<String, Float> d_word2tf;	// < word, tf >
	
	// TODO:other info useful for answering queries
	// titles, abstracts
	private String d_title;
	private String d_abstract = "";
	
	
	@DynamoDBHashKey(attributeName = "url")
	public String getDocId() {
		return d_docid;
	}
	public void setDocId(String docid) {
		this.d_docid = docid;
	}

	@DynamoDBAttribute(attributeName = "numWords")
	public int getNumWords() {
		return d_numWords;
	}
	public void setNumWords(int count) {
		d_numWords = count;
	}

//	@DynamoDBAttribute(attributeName = "pageRankScore")
//	public float getPageRankScore() {
//		return d_pageRankScore;
//	}
//	public void setPageRankScore(float pageRankScore) {
//		d_pageRankScore = pageRankScore;
//	}

	@DynamoDBAttribute(attributeName = "word2tf")
	public Map<String, Float> getWord2tf() {
		return d_word2tf;
	}

	public void setWord2tf(Map<String, Float> word2tf) {
		if (word2tf == null || word2tf.isEmpty()) {
			d_word2tf = null;
		} else {
			d_word2tf = word2tf;
		}
	}
	
	
	// info for display
	
	@DynamoDBAttribute(attributeName = "title")
	public String getTitle() {
		return d_title;
	}
	public void setTitle(String title) {
		d_title = title;
	}
	
	@DynamoDBAttribute(attributeName = "abstract")
	public String getAbstract() {
		return d_abstract;
	}
	public void setAbstract(String a) {
		d_abstract = a;
	}
}
