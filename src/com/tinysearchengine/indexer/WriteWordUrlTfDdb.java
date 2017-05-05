package com.tinysearchengine.indexer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.tinysearchengine.database.DdbConnector;

public class WriteWordUrlTfDdb {
	private static String bucketName = "tinysearchengine-mapreduce"; 
//	private static String key        = "10years.firstround.com";
	
//	private static String s3folder = "wordtfurl/";
	private static String s3folder = "wordtfurl-test/";
	
	private static String localfolder = "../s3/";
	
	
	private static String curWord = "@Null";
	private static HashMap <String, Double> curUrl2tf = new HashMap <String, Double>();
	
	private static DdbConnector ddb = new DdbConnector();
	
	
	public static void main(String[] args) throws IOException {
		
//		AWSCredentials credentials = new ;
		AWSCredentialsProvider s3CredentialProvider =
				DefaultAWSCredentialsProviderChain.getInstance();
		
//        AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(s3CredentialProvider).build();
		
		//aws s3 cp s3://tinysearchengine-mapreduce/inverted-index/output/InvertedIndex-r-00000 ./
		
		
		
//		String filename = "InvertedIndex-r-00000";
		
		for (String filename : args)
		{
			curUrl2tf.clear();
			
			String key = s3folder + filename;
			
			
	        try {
	            System.out.println("Reading: " + key);
	            S3Object s3object = s3Client.getObject(new GetObjectRequest(
	            		bucketName, key));
//	            System.out.println("Content-Type: "  + 
//	            		s3object.getObjectMetadata().getContentType());
	            
	            
//	            writeFileInputStream(s3object.getObjectContent(), filename);
	            writeDdbInputStream(s3object.getObjectContent());
	            
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
	        }
		}
		
		
		
        
        
        System.out.println("Done");
    }
    
    private static void writeFileInputStream(InputStream reader, String filename) throws IOException
    {
    	File file = new File(localfolder + filename);
    	OutputStream writer = new BufferedOutputStream(new FileOutputStream(file));

    	int read = -1;

    	while ( ( read = reader.read() ) != -1 ) {
    	    writer.write(read);
    	}

    	writer.flush();
    	writer.close();
    }
    
    
    private static void writeDdbInputStream(InputStream input) throws IOException
    {
    	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    	String line = null;
    	
    	while((line = reader.readLine()) != null) {
//    		System.out.println(line);
    		try
    		{
    			String[] parts = line.split("\001");
    			
    			if (parts.length < 3)
    			{
    				continue;
    			}
    			
    			String word = parts[0];
    			
    			if (word.equals("") || word.length() > 2000)
    			{
    				continue;
    			}
    			
    			double tf = Double.parseDouble(parts[1]);
				String url = parts[2];
    			
    			
    			if (!word.equals(curWord))
    			{
    				// write item to db
    				if (curUrl2tf.size() > )
    				
    				
    				
    				// record cur line
    				curWord = word;
    				curUrl2tf.clear();
    			}
    			
    			
				
				curUrl2tf.put(url, tf);
    			
    		}catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    		
        }
    	
    }
}
