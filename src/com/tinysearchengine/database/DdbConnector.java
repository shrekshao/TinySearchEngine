package com.tinysearchengine.database;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.PaginationLoadingStrategy;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
import com.amazonaws.services.dynamodbv2.datamodeling.S3Link;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.s3.model.Region;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinysearchengine.indexer.database.DdbParsedDoc;

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
	 * Return a lazy list of all documents in the table. Be very careful and not
	 * call methods like .size() on the returned list because that would require
	 * the entire database to be loaded into memory.
	 * 
	 * @return
	 */
	public List<DdbDocument> getAllDocumentsLazily() {
		DynamoDBScanExpression expr =
			new DynamoDBScanExpression().withConsistentRead(false);
		DynamoDBMapperConfig config =
			DynamoDBMapperConfig.builder().withPaginationLoadingStrategy(
					PaginationLoadingStrategy.ITERATION_ONLY).build();
		return d_mapper.parallelScan(DdbDocument.class, expr, 4, config);
	}

	public List<DdbDocument> getAllNonRepairedDocumentsLazily() {
		DynamoDBScanExpression expr = new DynamoDBScanExpression()
				.withConsistentRead(false).withFilterExpression(
						"attribute_not_exists(repaired) AND attribute_not_exists(links)");
		DynamoDBMapperConfig config =
			DynamoDBMapperConfig.builder().withPaginationLoadingStrategy(
					PaginationLoadingStrategy.ITERATION_ONLY).build();
		return d_mapper.scan(DdbDocument.class, expr, config);
	}

	public List<DdbWordDocTfTuple> getWordDocTfTuplesForWord(String word) {
		HashMap<String, AttributeValue> attrs = new HashMap<>();
		attrs.put(":wd", new AttributeValue().withS(word));

		DynamoDBQueryExpression<DdbWordDocTfTuple> expr =
			new DynamoDBQueryExpression<DdbWordDocTfTuple>()
					.withIndexName("word-index").withConsistentRead(false)
					.withKeyConditionExpression("word = :wd")
					.withExpressionAttributeValues(attrs);

		return d_mapper.query(DdbWordDocTfTuple.class, expr);
	}

	public DdbPageRankScore getPageRankScore(String url) {
		return d_mapper.load(DdbPageRankScore.class, url);
	}

	public Map<String, List<Object>> batchGetPageRankScore(List<String> urls) {
		Map<Class<?>, List<KeyPair>> loadDescriptor =
			new HashMap<Class<?>, List<KeyPair>>();

		List<KeyPair> kps = new LinkedList<KeyPair>();
		urls.forEach((url) -> {
			KeyPair kp = new KeyPair();
			kp.setHashKey(url);
			kps.add(kp);
		});

		loadDescriptor.put(DdbPageRankScore.class, kps);
		return d_mapper.batchLoad(loadDescriptor);
	}

	public Map<String, List<Object>> batchGetDocuments(List<String> urls) {
		Map<Class<?>, List<KeyPair>> loadDescriptor =
				new HashMap<Class<?>, List<KeyPair>>();

		List<KeyPair> kps = new LinkedList<KeyPair>();
		urls.forEach((url) -> {
			KeyPair kp = new KeyPair();
			kp.setHashKey(url);
			kps.add(kp);
		});

		loadDescriptor.put(DdbDocument.class, kps);
		return d_mapper.batchLoad(loadDescriptor);
	}

	/**
	 * Converts a url into a name appropriate as an S3Link key.
	 * 
	 * For example,
	 * 
	 * http://www.google.com/foobar -> www.google.com/foobar
	 * 
	 * http://www.google.com/foobar/ -> www.google.com/foobar
	 * 
	 * http://www.google.com/foobar/index.html ->
	 * www.google.com/foobar/index.html
	 * 
	 * http://www.facebook.com/foobar?q=123 -> www.facebook.com/foobar?q=123
	 * 
	 * @param url
	 * @return
	 */
	public static String urlToS3LinkKey(URL url) {
		StringBuilder builder = new StringBuilder();
		builder.append(url.getAuthority());
		String fileName = url.getFile();
		while (fileName.endsWith("/")) {
			fileName = fileName.substring(0, fileName.length() - 1);
		}
		builder.append(fileName);
		return builder.toString();
	}

	public S3Link createS3Link(String linkKey) {
		return d_mapper.createS3Link(Region.US_East_2, k_BUCKET_NAME, linkKey);
	}

	// Methods used for indexer to r/w ParsedDoc table (forward index) and
	// Keyword (inverted index)
	/**
	 * Store the given document into DynamoDb.
	 * 
	 * @param parsedDoc
	 */
	public void putParsedDoc(DdbParsedDoc parsedDoc) {
		assert parsedDoc != null;

		d_mapper.save(parsedDoc);
	}

}
