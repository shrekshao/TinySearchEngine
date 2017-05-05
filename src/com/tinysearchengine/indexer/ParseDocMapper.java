package com.tinysearchengine.indexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.util.HashMap;
//import java.util.Map;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
//import org.apache.hadoop.mapreduce.Mapper.Context;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
//import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.tinysearchengine.indexer.database.DdbParsedDoc;

public class ParseDocMapper extends Mapper<LongWritable, Text, Text, Text> {
	static final String S3BUCKET_NAME = "tinysearchengine";
	static final String INPUT_SEPARATOR = "\001";	// mapper input separator
	static final String SEPARATOR = " ~~ ";
	
//	@SuppressWarnings("deprecation")
	DefaultAWSCredentialsProviderChain credential = new DefaultAWSCredentialsProviderChain();
	@SuppressWarnings("deprecation")
	private AmazonS3 s3Client = 
			new AmazonS3Client(credential);
	
//	@SuppressWarnings("deprecation")
//	AmazonDynamoDB d_ddb = new AmazonDynamoDBClient();
	AmazonDynamoDB d_ddb = AmazonDynamoDBClientBuilder.standard().build();
//	@SuppressWarnings("deprecation")
//	AmazonDynamoDB d_ddb = new AmazonDynamoDBClient();
//	@SuppressWarnings("deprecation")
	private DynamoDBMapper d_mapper = new DynamoDBMapper(d_ddb, credential);
	
	
	
//	private AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(InstanceProfileCredentialsProvider.getInstance()).build();
	
    private String getStringFromInputStream(InputStream input)
    		throws IOException 
    {
    	// Read one text line at a time and display.
        BufferedReader reader = new BufferedReader(new 
        		InputStreamReader(input));
        
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            sb.append(line);
            sb.append("\n");
        }
        
        return sb.toString();
    }
	
	
	private String getS3FileContent(String s3key)
	{
		S3Object s3object = s3Client.getObject(
				new GetObjectRequest(S3BUCKET_NAME, s3key));
//        System.out.println("Content-Type: "  + 
//        		s3object.getObjectMetadata().getContentType());
		
		String content = null;
		try
		{
			content = getStringFromInputStream(s3object.getObjectContent());
			s3object.close();
		} catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which" +
            		" means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means"+
            		" the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        } catch (IOException e)
		{
        	e.printStackTrace();
		}
		
		
		return content;
	}
	
	
	
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		String line = value.toString();
		String[] parts = line.split(INPUT_SEPARATOR);
		
		
		if (parts.length < 2)
		{
			System.out.println(line);
			return;
		}
		
		String url = parts[0]; 
		
		JSONObject obj = new JSONObject(parts[1]);
		String s3key = obj.getJSONObject("s3").getString("key");
		
		
		String content = getS3FileContent(s3key);
		if (content == null)
		{
			System.out.println(s3key + " content null");
			return;
		}
		// -----------------------
		
		Document doc;
		try
		{
			doc = Jsoup.parse(content);
		}
		catch(IllegalArgumentException e)
		{
			System.out.println(s3key + " content empty string");
			return;
		}
		
		
		try
		{
		
		// kick out explicit non-english page
		Elements html = doc.select("html[lang]");
		if (html != null)
		{
			String language = html.attr("lang");
			if (language != "")
			{
				if (language != "en" && language != "en-US" && language != "en-GB")
				{
					return;
				}
			}
			
		}
		
    	
    	Elements title = doc.select("title");
    	String titleStr = title.text();
    	
    	Elements p = doc.select("p");
    	String pStr = p.text();
    	
    	
    	if (titleStr.equals(""))
    	{
    		titleStr = "(No Title)";
    	}

    	if (pStr.equals(""))
    	{
    		pStr = "This document doesn't contain text info for an abstract.";
    	}
    	else if (pStr.length() > 201)
    	{
    		pStr = pStr.substring(0, 200) + " ...";
    	}
        
    	
    	DdbParsedDoc parsedDoc = new DdbParsedDoc();
      
        parsedDoc.setDocId(url);
        parsedDoc.setTitle(titleStr);
        parsedDoc.setAbstract(pStr);

        d_mapper.save(parsedDoc);
    	
    	
    	context.write(
    			new Text(url), 
    			new Text(
    					titleStr
    					+ SEPARATOR
    					+ pStr
    					)
    			);
    	
		
		
		} catch(Exception e)
		{
			e.printStackTrace();
		}
        
//        // Test output
//        System.out.println(s3key);
//        System.out.println(totalCount);
//        System.out.println(keyword2count.size());
        
        
//        // TODO: write dynamoDB table ParsedDoc with dynamodbmapper
//        // ? performance issue? write one tuple for each input key
//        // An alternative would be emit a tuple, with url+doc being the key (which I don't find very efficient)
//        DdbParsedDoc parsedDoc = new DdbParsedDoc();
//        
//        parsedDoc.setDocId(url);
//        parsedDoc.setNumWords(totalCount);
//        parsedDoc.setWord2tf(keyword2tf);
//        
//        parsedDoc.setTitle(title.text());
//        
//        
//        d_ddbConnector.putParsedDoc(parsedDoc);
        
	}
}
