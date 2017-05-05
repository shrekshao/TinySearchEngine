package com.tinysearchengine.database;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "PageRankScoreNew")
public class DdbPageRankScore {
	public String d_url;
	public double d_pgRankScore;
	
	@DynamoDBHashKey(attributeName = "url")
	public String getUrl() {
		return d_url;
	}
	public void setUrl(String url) {
		d_url = url;
	}
	
	@DynamoDBAttribute(attributeName = "pageRankScore")
	public double getPageRankScore() {
		return d_pgRankScore;
	}
	public void setPageRankScore(double score) {
		d_pgRankScore = score;
	}
}
