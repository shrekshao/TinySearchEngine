package com.tinysearchengine.indexer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


/**
 * 
 * @author shrekshao
 * 
 * Input: < `word`, docid, tf >
 * Output: < word_i, docid_j, tf_ij, numDocsContainThisWord_i >
 *
 */
public class BuildInvertedIndexReducer extends Reducer<Text, Text, Text, Text> {
	
//	static final String SEPARATOR = " ";
	
	static final int GLOBAL_DOC_NUM = 1251461;
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException 
	{
		int numDocs = 0;	// sum of documents that contain this
		
//		HashSet<String> docs = new HashSet<String>();
		HashMap<String, Text> docs = new HashMap<String, Text>();
		
		for (Text text : values)
		{
			String str = text.toString();
			String[] parts = str.split(BuildInvertedIndexMapper.SEPARATOR);
//			docs.add(parts[0]);
			docs.put(parts[0], text);
		}
		
		numDocs = docs.size();
		
		double idf = Math.log10((double) GLOBAL_DOC_NUM / numDocs);
		
//		String numDocsStrAppend = SEPARATOR +  Integer.toString(numDocs);
		String numDocsStrAppend = BuildInvertedIndexMapper.SEPARATOR +  Double.toString(idf);

		for (Map.Entry<String, Text> entry : docs.entrySet())
		{
			context.write(
					key, 
					new Text(entry.getValue().toString() + numDocsStrAppend)
					);
		}
		
		
		
	}
	
}
