package com.tinysearchengine.pagerank.test;
 
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "WordIdf")
public class WordIdfDdbDocument {
	private String d_word;
	private Double d_idf;

	@DynamoDBHashKey(attributeName = "word")
	public String getWord() {
		return d_word;
	}

	public void setWord(String d_word1) throws Exception {
		d_word = d_word1;
	}

	@DynamoDBAttribute(attributeName = "idf")
	public Double getIdf() {
		return d_idf;
	}

	public void setIdf(Double d_idf1) throws Exception {
		d_idf = d_idf1;
	}
     
}
