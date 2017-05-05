package com.tinysearchengine.indexer.test;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class GetS3Object {
	private static String bucketName = "tinysearchengine-mapreduce"; 
//	private static String key        = "10years.firstround.com";
	
	private static String s3folder = "inverted-index/output/";
	
	private static String localfolder = "s3/"; 
	
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
			String key = s3folder + filename;
			
			
	        try {
	            System.out.println("Downloading: " + key);
	            S3Object s3object = s3Client.getObject(new GetObjectRequest(
	            		bucketName, key));
//	            System.out.println("Content-Type: "  + 
//	            		s3object.getObjectMetadata().getContentType());
	            
	            
//	            displayTextInputStream(s3object.getObjectContent());
	            writeFileInputStream(s3object.getObjectContent(), filename);
	            
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

    private static void displayTextInputStream(InputStream input)
    throws IOException {
    	// Read one text line at a time and display.
        BufferedReader reader = new BufferedReader(new 
        		InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            System.out.println("    " + line);
        }
        System.out.println();
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
}
