package com.tinysearchengine.indexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

//import org.json.*;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
//import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

//class BuildInvertedIndexMapperOutValue implements Writable {
//    private String docid;	//url
//    private int count;
//
////    public  BuildInvertedIndexMapperOutValue() {
////        set(docid, count);
////    }
//    public  BuildInvertedIndexMapperOutValue(String docid, int count) {
//        set(docid, count);
//    }
//    public void set(String docid, int count) {
//        this.docid = docid;
//        this.count = count;
//    }
//    public String getDocid() {
//        return docid;
//    }
//    public int getCount() {
//        return count;
//    }
//    @Override
//    public void write(DataOutput out) throws IOException {
////        out.writeChars(docid);
//    	out.writeUTF(docid);
//        out.writeInt(count);
//    }
//    @Override
//    public void readFields(DataInput in) throws IOException {
//        docid = in.readUTF();	// ?
//        count = in.readInt();
//    }
//
//    /* (non-Javadoc)
//     * @see java.lang.Object#hashCode()
//     */
//    @Override
//    public int hashCode() {
////        final int prime = 31;
////        int result = 1;
////        long temp;
////        temp = Double.doubleToLongBits(first);
////        result = prime * result + (int) (temp ^ (temp >>> 32));
////        temp = Double.doubleToLongBits(second);
////        result = prime * result + (int) (temp ^ (temp >>> 32));
////        return result;
//    	return docid.hashCode();
//    }
//    /* (non-Javadoc)
//     * @see java.lang.Object#equals(java.lang.Object)
//     */
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (!(obj instanceof BuildInvertedIndexMapperOutValue)) {
//            return false;
//        }
//        BuildInvertedIndexMapperOutValue other = (BuildInvertedIndexMapperOutValue) obj;
////        if (Double.doubleToLongBits(first) != Double
////                .doubleToLongBits(other.first)) {
////            return false;
////        }
////        if (Double.doubleToLongBits(second) != Double
////                .doubleToLongBits(other.second)) {
////            return false;
////        }
//        if (!docid.equals(other.getDocid()))
//        {
//        	return false;
//        }
//        
//        if (count != other.getCount())
//        {
//        	return false;
//        }
//        
//        return true;
//    }
//    @Override
//    public String toString() {
//        return docid + " " + Integer.toString(count);
//    }
//}


/**
 * 
 * @author shrekshao
 * 
 * Expected Input: < url, s3key >	(assume bucket is tinysearchengine)
 * Output: < `word`, docid, tf >
 *
 */

public class BuildInvertedIndexMapper extends Mapper<LongWritable, Text, Text, Text> {
	
	static final String S3BUCKET_NAME = "tinysearchengine";
	
//	static final Text GLOBAL_TOTAL_COUNT_KEY = new Text("@totalCount@");
	
	
	static final String INPUT_SEPARATOR = "\001";	// mapper input separator
	static final String SEPARATOR = " ";
	
	
	
//	private AWSCredentialsProvider s3CredentialProvider =
//			DefaultAWSCredentialsProviderChain.getInstance();
//	private AmazonS3 s3Client = 
//			AmazonS3ClientBuilder.standard().withCredentials(s3CredentialProvider).build();
	
//	private AWSCredentials credentials = EnvironmentVariableCredentialsProvider.getCredential();
//	@SuppressWarnings("deprecation")
//	private AmazonS3 s3Client = 
//			new AmazonS3Client(new InstanceProfileCredentialsProvider(false));
	@SuppressWarnings("deprecation")
	private AmazonS3 s3Client = 
			new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
	
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
		
		// TODO: use URL to read data from dynamoDB (might use batch load save ...)
		// TODO: use S3link to get document as a string
		
		String line = value.toString();
		String[] parts = line.split(INPUT_SEPARATOR);
		
		
		if (parts.length < 2)
		{
			System.out.println(line);
			return;
		}
		
		
		
		String url = parts[0]; 
//		String s3key = parts[1];
		
		JSONObject obj = new JSONObject(parts[1]);
		String s3key = obj.getJSONObject("s3").getString("key");
		
//		System.out.println(s3key);
		
		String content = getS3FileContent(s3key);
		if (content == null)
		{
			System.out.println(s3key + " content null");
			return;
		}
		// -----------------------
		
		Document doc = Jsoup.parse(content);
		
		
		// kick out explicit non-english page
		Elements html = doc.select("html[lang]");
		if (html != null)
		{
			String language = html.attr("lang");
//			System.out.println(language);
			if (language != "")
			{
				if (language != "en" && language != "en-US" && language != "en-GB")
				{
					return;
				}
			}
			
		}
		
//		// TODO: change to parse for all text in the content
//    	Elements p = doc.select("p");
//    	Elements title = doc.select("title");
//    	
//    	String text = title.text() +"\n" + p.text();
		
		
		// parse english words only
		
		String text = doc.text();
    	
        String[] words = (text).split("[^a-zA-Z0-9']+");
        
        HashMap<String, Integer> keyword2count = new HashMap<String, Integer>();
        int totalCount = words.length;		// total num of words abstracted from this doc
        
        
        
        SnowballStemmer stemmer = new englishStemmer();
        
        for (String w : words)
        {	
        	String word = w.toLowerCase();
        	
        	// stop words
        	if (StopWordList.stopwords.contains(word))
        	{
        		continue;
        	}
        	
        	// stem
        	stemmer.setCurrent(word);
        	stemmer.stem();
        	String stemmed = stemmer.getCurrent();
        	
        	
        	if (keyword2count.containsKey(stemmed))
        	{
        		keyword2count.put(stemmed, keyword2count.get(stemmed) + 1);
        	}
        	else
        	{
        		keyword2count.put(stemmed, 1);
        	}
        }
        
        
//        context.write(GLOBAL_TOTAL_COUNT_KEY, new Text(SEPARATOR + Integer.toString(totalCount)));
		
        
        // for this docuemnt (key)
//        HashMap<String, Double> keyword2tf = new HashMap<String, Double>();
        
        
        for(Map.Entry<String, Integer> entry : keyword2count.entrySet())
        {
        	String w = entry.getKey();
        	int count = entry.getValue();
        	
        	double tf = (double) count / totalCount;
//        	keyword2tf.put(w, tf);
        	
        	context.write(
        			new Text(w), 
        			new Text(
        					url
        					+ SEPARATOR
        					+ Double.toString(tf)
        					)
        			);
        	
        	
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
