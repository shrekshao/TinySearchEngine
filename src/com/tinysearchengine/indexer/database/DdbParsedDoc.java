package com.tinysearchengine.indexer.database;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "ParsedDoc")
public class DdbParsedDoc {
	private String d_docid;	// url or S3 link?
	
	// titles, abstracts
	private String d_title = "n";
	private String d_abstract = "n";
	
	
	@DynamoDBHashKey(attributeName = "url")
	public String getDocId() {
		return d_docid;
	}
	public void setDocId(String docid) {
		this.d_docid = docid;
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
