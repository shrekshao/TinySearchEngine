package com.tinysearchengine.database;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "WordIdf")
public class DdbIdfScore {
	private String d_word;
	private double d_idfScore;
	
	@DynamoDBHashKey(attributeName = "word")
	public String getWord() {
		return d_word;
	}
	public void setWord(String w) {
		d_word = w;
	}
	
	@DynamoDBAttribute(attributeName = "idf")
	public double getIdf() {
		return d_idfScore;
	}
	public void setIdf(double score) {
		d_idfScore = score;
	}
}
