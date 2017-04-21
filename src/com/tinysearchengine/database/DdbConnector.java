package com.tinysearchengine.database;

import java.net.URL;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class DdbConnector {
	/**
	 * The client object.
	 */
	AmazonDynamoDB d_ddb;

	/**
	 * The object mapper.
	 */
	DynamoDBMapper d_mapper;

	/**
	 * Note that this class assumes you have the correct credentials in
	 * ~/.aws/credentials.
	 * 
	 * See
	 * http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html
	 */
	public DdbConnector() {
		d_ddb = AmazonDynamoDBClientBuilder.standard().build();
		d_mapper = new DynamoDBMapper(d_ddb);
	}

	/**
	 * Store the given document into DynamoDb.
	 * @param doc
	 */
	public void putDocument(DdbDocument doc) {
		assert doc != null;
		assert doc.getUrl() != null;
		assert doc.getUrlAsString() != null;
		assert doc.getContent() != null;
		assert doc.getContentType() != null;
		assert doc.getCrawledTime() > 0;
		
		d_mapper.save(doc);
	}
	
	/**
	 * Get the document by its url.
	 * @param url
	 * @return
	 */
	public DdbDocument getDocument(URL url) {
		assert url != null;
		return d_mapper.load(DdbDocument.class, url.toString());
	}
}
