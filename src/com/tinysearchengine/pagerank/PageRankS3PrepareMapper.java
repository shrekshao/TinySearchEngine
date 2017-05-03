package com.tinysearchengine.pagerank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.json.JSONObject;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.tinysearchengine.crawler.URLExtractor;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.model.S3Object;
 
//input :  S3 Line
//output: URL outlinks

public class PageRankS3PrepareMapper extends Mapper<LongWritable, Text, Text, Text>{
	static final String S3BUCKET_NAME = "tinysearchengine";
	static final String SEPARATOR = " ";
	
	private AmazonS3 s3Client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
		
    private String getStringFromInputStream(InputStream input) throws IOException {
    	// Read one text line at a time and display.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            sb.append(line);
            sb.append("\n");
        }
        return sb.toString();
    }
	
	private String getS3FileContent(String s3key) {
		S3Object s3object = s3Client.getObject(new GetObjectRequest(S3BUCKET_NAME, s3key));
		String content = null;
		try {
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
        } catch (IOException e) {
        	e.printStackTrace();
		}
		return content;
	}
	
	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {		
		String line = value.toString();
		String[] parts = line.split("\001");
		//should be 3 parts
		//url, s3key, outlinks
		//outlinks are divided using "\002"
		
		if (parts.length < 2) {
			System.out.println(line);
			return;
		}
		
		String url = parts[0]; 
//		if(url.endsWith("/")) {
//			url = url.substring(0, url.length() - 1);
//		}
//		url = url.replaceAll("\\s+$", "");
//	    if(url.contains(" ")) {
//	    	url = url.replaceAll(" ", "%20");
//	    }
		if(parts.length == 2 || parts[2] == "") { //Need to parse by myself
			JSONObject obj = new JSONObject(parts[1]);
			String s3key = obj.getJSONObject("s3").getString("key");			
			String content = getS3FileContent(s3key);
			if (content == null) { //pay attention to content is null. then nothing needs to be done.
				System.out.println(s3key + " content null");
				return;
			}		
			String[] links = URLExtractor.extract(content.getBytes());
			URL curUrl = new URL(url);
			for(String link: links) {	
				URL resolvedUrl = new URL(curUrl, link);
				String realLink = resolvedUrl.toString(); //get away! those mailto:
				if(realLink.startsWith("https") || realLink.startsWith("http")) {
//					realLink = realLink.replaceAll("\\s+$", "");
//				    if(realLink.contains(" ")) {
//					    realLink = realLink.replaceAll(" ", "%20");
//				    }	    
				    context.write(new Text(url), new Text(realLink));
				}		
			}	
		} else { //already parsed, just get
			String[] outlinks = parts[2].split("\002");
			for(String outlink : outlinks) {
				if(outlink.startsWith("https") || outlink.startsWith("http")) {
//					outlink = outlink.replaceAll("\\s+$", "");
//				    if(outlink.contains(" ")) {
//				    	outlink = outlink.replaceAll(" ", "%20");
//				    }
				    context.write(new Text(url), new Text(outlink));
				}	
			}
		}
	}
	
}
