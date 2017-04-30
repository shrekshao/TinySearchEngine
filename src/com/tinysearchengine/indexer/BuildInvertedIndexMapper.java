package com.tinysearchengine.indexer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Mapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

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


public class BuildInvertedIndexMapper extends Mapper<Text, Object, Text, Text> {
	
	static final Text GLOBAL_TOTAL_COUNT_KEY = new Text("@totalCount@");
	static final String SEPARATOR = " ";
	
	@Override
	public void map(Text key, Object value, Context context) throws IOException, InterruptedException {
		
		// TODO: use URL to read data from dynamoDB (might use batch load save ...)
		// TODO: use S3link to get document as a string
		String url = key.toString();
		String content = "";
		// -----------------------
		
		Document doc = Jsoup.parse(content);
    	Elements p = doc.select("p");
    	Elements title = doc.select("title");
    	
    	String text = title.text() +"\n" + p.text();
    	
        String[] words = (text).split("[^a-zA-Z0-9']+");
        
        HashMap<String, Integer> keyword2count = new HashMap<String, Integer>();
        int totalCount = 0;		// total num of words abstract from this doc
        
        
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
        	
        	totalCount++;
        	
        	if (keyword2count.containsKey(stemmed))
        	{
        		keyword2count.put(stemmed, keyword2count.get(stemmed) + 1);
        	}
        	else
        	{
        		keyword2count.put(stemmed, 1);
        	}
        }
        
        
        context.write(GLOBAL_TOTAL_COUNT_KEY, new Text(SEPARATOR + Integer.toString(totalCount)));
		
        
        // for this docuemnt (key)
        HashMap<String, Float> keyword2tf = new HashMap<String, Float>();
        
        for(Map.Entry<String, Integer> entry : keyword2count.entrySet())
        {
        	String w = entry.getKey();
        	int count = entry.getValue();
        	context.write(
        			new Text(w), 
        			new Text(Integer.toString(count))
        			);
        	
        	float tf = (float) count / totalCount;
        	keyword2tf.put(w, tf);
        }
        
        // TODO: write dynamoDB table ParsedDoc with dynamodbmapper
        // ? performance issue? write one tuple for each input key
        // An alternative would be emit a tuple, with url+doc being the key (which I don't find very efficient)
		
	}
}
