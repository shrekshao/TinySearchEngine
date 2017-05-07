package com.tinysearchengine.database;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "WordDocTfTupleUUID")
public class DdbWordDocTfTuple {
	String d_id;
	double d_tf;
	String d_url;
	String d_word;

	@DynamoDBHashKey(attributeName = "id")
	public String getId() {
		return d_id;
	}

	public void setId(String id) {
		d_id = id;
	}

	@DynamoDBAttribute(attributeName = "tf")
	public double getTf() {
		return d_tf;
	}

	public void setTf(double tf) {
		d_tf = tf;
	}

	@DynamoDBAttribute(attributeName = "url")
	public String getUrl() {
		return d_url;
	}

	public void setUrl(String url) {
		d_url = url;
	}

	@DynamoDBIndexHashKey(globalSecondaryIndexName = "word-index",
			attributeName = "word")
	public String getWord() {
		return d_word;
	}

	public void setWord(String word) {
		d_word = word;
	}
}
