package com.tinysearchengine.database;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class DdbConnector {

	public final static String k_BUCKET_NAME = "tinysearchengine";

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

		AWSCredentialsProvider s3CredentialProvider =
			DefaultAWSCredentialsProviderChain.getInstance();
		d_mapper = new DynamoDBMapper(d_ddb, s3CredentialProvider);
	}

	/**
	 * Store the given document into DynamoDb.
	 * 
	 * @param doc
	 */
	public void putDocument(DdbDocument doc) {
		assert doc != null;
		assert doc.getUrl() != null;
		assert doc.getUrlAsString() != null;
		assert doc.getContent() != null;
		assert doc.getContentType() != null;
		assert doc.getCrawledTime() > 0;
		assert doc.getContentLink() != null;

		if (doc.hasNewContent()) {
			doc.getContentLink().uploadFrom(doc.getContent());
		}

		d_mapper.save(doc);
	}

	/**
	 * Get the document by its url.
	 * 
	 * @param url
	 * @return
	 */
	public DdbDocument getDocument(URL url) {
		assert url != null;
		return d_mapper.load(DdbDocument.class, url.toString());
	}

	/**
	 * Get the document by a fingerprint of its content.
	 * 
	 * @param fp
	 * @return
	 */
	public List<DdbDocument> getDocumentByFingerprint(byte[] fp) {
		assert fp != null;
		HashMap<String, AttributeValue> attrs = new HashMap<>();
		attrs.put(":fp", new AttributeValue().withB(ByteBuffer.wrap(fp)));

		DynamoDBQueryExpression<DdbDocument> expr =
			new DynamoDBQueryExpression<DdbDocument>()
					.withIndexName("fingerprint-index")
					.withConsistentRead(false)
					.withKeyConditionExpression("fingerprint = :fp")
					.withExpressionAttributeValues(attrs);

		return d_mapper.query(DdbDocument.class, expr);
	}

	/**
	 * Converts a url into a name appropriate as an S3Link key.
	 * 
	 * For example,
	 * 
	 * http://www.google.com/foobar
	 * 	-> www.google.com/foobar
	 * 
	 * http://www.google.com/foobar/index.html
	 * 	-> www.google.com/foobar/index.html
	 * 
	 * http://www.facebook.com/foobar?q=123
	 * 	-> www.facebook.com/foobar?q=123
	 * 
	 * @param url
	 * @return
	 */
	public static String urlToS3LinkKey(URL url) {
		StringBuilder builder = new StringBuilder();
		builder.append(url.getAuthority());
		builder.append(url.getFile());
		return builder.toString();
	}

	public S3Link createS3Link(String linkKey) {
		return d_mapper.createS3Link(k_BUCKET_NAME, linkKey);
	}
}
