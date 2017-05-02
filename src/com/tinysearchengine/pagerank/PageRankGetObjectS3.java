package com.tinysearchengine.pagerank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class PageRankGetObjectS3 {
	private static String bucketName = "tinysearchengine-mapreduce";
	private static String keyName = "";
	private static AWSCredentialsProvider s3CredentialProvider = DefaultAWSCredentialsProviderChain.getInstance();
	private static AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(s3CredentialProvider).build();

	public static void main(String[] args) throws IOException {
         try {
            System.out.println("Listing objects");
            final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName).withMaxKeys(2);
            ListObjectsV2Result result;
            do {               
               result = s3Client.listObjectsV2(req);   
               for (S3ObjectSummary objectSummary : 
                   result.getObjectSummaries()) {
                   System.out.println(" - " + objectSummary.getKey() + "  " +
                           "(size = " + objectSummary.getSize() + 
                           ")");
               }
               System.out.println("Next Continuation Token : " + result.getNextContinuationToken());
               req.setContinuationToken(result.getNextContinuationToken());
            } while(result.isTruncated() == true ); 
            
         } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, " +
            		"which means your request made it " +
                    "to Amazon S3, but was rejected with an error response " +
                    "for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, " +
            		"which means the client encountered " +
                    "an internal error while trying to communicate" +
                    " with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
	
	
//	public static void main(String[] args) throws IOException {
//		ObjectListing aa = s3Client.listObjects(bucketName);
//		System.out.println(aa);
//	}
	
}
//		
//        try {
//            System.out.println("Downloading an object");
//            int counter = 500;
//         } catch (AmazonServiceException ase) {
//            System.out.println("Caught an AmazonServiceException, which" +
//            		" means your request made it " +
//                    "to Amazon S3, but was rejected with an error response" +
//                    " for some reason.");
//            System.out.println("Error Message:    " + ase.getMessage());
//            System.out.println("HTTP Status Code: " + ase.getStatusCode());
//            System.out.println("AWS Error Code:   " + ase.getErrorCode());
//            System.out.println("Error Type:       " + ase.getErrorType());
//            System.out.println("Request ID:       " + ase.getRequestId());
//        } catch (AmazonClientException ace) {
//            System.out.println("Caught an AmazonClientException, which means"+
//            		" the client encountered " +
//                    "an internal error while trying to " +
//                    "communicate with S3, " +
//                    "such as not being able to access the network.");
//            System.out.println("Error Message: " + ace.getMessage());
//        }
//    }
//	
//	public void UploadObjectSingleOperation(String bucket_name,
//			String file_name, String file_path) {
//		try {
//			System.out.print("Uploading rebuilt file to S3...");
//			File file = new File(file_path);
//			s3Client.putObject(new PutObjectRequest(bucket_name, file_name, file));
//			System.out.println("Success!");
//		} catch (AmazonServiceException ase) {
//			System.out.println("Caught an AmazonServiceException, which "
//					+ "means your request made it "
//					+ "to Amazon S3, but was rejected with an error response"
//					+ " for some reason.");
//			System.out.println("Error Message:    " + ase.getMessage());
//			System.out.println("HTTP Status Code: " + ase.getStatusCode());
//			System.out.println("AWS Error Code:   " + ase.getErrorCode());
//			System.out.println("Error Type:       " + ase.getErrorType());
//			System.out.println("Request ID:       " + ase.getRequestId());
//		} catch (AmazonClientException ace) {
//			System.out.println("Caught an AmazonClientException, which "
//					+ "means the client encountered "
//					+ "an internal error while trying to "
//					+ "communicate with S3, "
//					+ "such as not being able to access the network.");
//			System.out.println("Error Message: " + ace.getMessage());
//		}
//	}
//	
//	public void rebuildFile(String bucketNameFrom, String bucketNameTo, String outPath, String fileName, String mode) throws IOException, JSONException {
//		File outDirectory = new File(outPath);
//		if(outDirectory.exists()) { System.out.println("outDirectory exists"); }
//		else if(new File(outPath).mkdir()) { System.out.println("Creating outDirectory Successfully!"); }
//		try {
//            System.out.println("Listing objects");
//            ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketNameFrom);
//            ObjectListing objectListing;     
//            //index for every one thousand files
//            int i=0;
//            //write objects infor into one file;
//            do {
//            	StringBuilder sb=new StringBuilder();
//                objectListing = s3Client.listObjects(listObjectsRequest);
//                
//                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
//                	
//                	 S3Object object = s3Client.getObject(new GetObjectRequest(bucketNameFrom, objectSummary.getKey()));
//                     System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
//                     //read the content of this object, might change due to the real content
//                     BufferedReader br= new BufferedReader(new InputStreamReader(object.getObjectContent()));
//                     String json = "";
//                    
//                     //json in this object
// 	        	    String line;
// 	        	    while ((line = br.readLine()) != null) {
// 	        	       json += line;	  
// 	        	    }
// 	        	   sb.append(parseForPageRank(json)+"\n");
// 	        	   
//                }
//                //write into file;
//                File file=new File(outPath+"/"+fileName+i+".txt");
//                if(file.exists())
//                	file.delete();
//                if(file.createNewFile())
//                	System.out.println("create file: rebuilt"+ i);
//                FileWriter filewriter = new FileWriter(file,true);
//                String str= sb.toString();
//                //delete the last "\n"???
//    			filewriter.write(str.substring(0,str.length()-2));
//    			filewriter.close();    			
//    			System.out.println(fileName+i+".txt ready!");
//    			UploadObjectSingleOperation(bucketNameTo,fileName+i+".txt",outPath+"/"+fileName+i+".txt");
//                listObjectsRequest.setMarker(objectListing.getNextMarker());
//                
//            } while (objectListing.isTruncated());
//          } catch (AmazonServiceException ase) {
//            System.out.println("Caught an AmazonServiceException, " +
//            		"which means your request made it " +
//                    "to Amazon S3, but was rejected with an error response " +
//                    "for some reason.");
//            System.out.println("Error Message:    " + ase.getMessage());
//            System.out.println("HTTP Status Code: " + ase.getStatusCode());
//            System.out.println("AWS Error Code:   " + ase.getErrorCode());
//            System.out.println("Error Type:       " + ase.getErrorType());
//            System.out.println("Request ID:       " + ase.getRequestId());
//        } catch (AmazonClientException ace) {
//            System.out.println("Caught an AmazonClientException, " +
//            		"which means the client encountered " +
//                    "an internal error while trying to communicate" +
//                    " with S3, " +
//                    "such as not being able to access the network.");
//            System.out.println("Error Message: " + ace.getMessage());
//        }
//    }
//	
//
//
//	public int getRoughNumberofObejects(String bucketName)
//	{
//		int count=0;
//		ObjectListing objectListing;     
//		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
//		do
//		 {   objectListing = s3Client.listObjects(listObjectsRequest);
//			 count++;  
//			 System.out.println("reached: "+count+"thousands");
//			 listObjectsRequest.setMarker(objectListing.getNextMarker());
//         
//		 } while (objectListing.isTruncated());
//		
//		return count;
//	}
//	// this function get the raw string from s3, then parse it to the url url1
//	// url2 url3 format
//	private String parseForPageRank(String json) throws JSONException {
//		String result = "";
//		JSONObject obj = new JSONObject(json.toString());
//		String parentUrl = obj.getString("parentUrl");
//		result += parentUrl + "\t";
//		JSONArray outlinks = obj.getJSONArray("urls");
//		for (int i = 0; i < outlinks.length(); i++) {
//			String outlink = outlinks.getString(i);
//			result += outlink + " ";
//		}
//		// System.out.println(result.substring(0,result.length()-1));
//		return result;
//	}
//	
//    private static void displayTextInputStream(InputStream input)
//    throws IOException {
//    	// Read one text line at a time and display.
//        BufferedReader reader = new BufferedReader(new 
//        		InputStreamReader(input));
//        while (true) {
//            String line = reader.readLine();
//            if (line == null) break;
//
//            System.out.println("    " + line);
//        }
//        System.out.println();
//    }
//    
//	// tools
//	private void deleteDirectory(File directory) {
//		for (File file : directory.listFiles()) {
//			if (file.isDirectory())
//				deleteDirectory(file);
//			else
//				file.delete();
//		}
//	}
//	
//}
// 
// 
