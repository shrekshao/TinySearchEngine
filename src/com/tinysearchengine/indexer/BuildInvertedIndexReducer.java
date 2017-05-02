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
//	static final int GLOBAL_DOC_NUM = 4;
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException 
	{
		int numDocs = 0;	// sum of documents that contain this
		
		HashSet<String> docs = new HashSet<String>();
		
		for (Text text : values)
		{
			numDocs++;
			docs.add(text.toString());
		}
		
//		numDocs = Iterators.size(values);
		
		double idf = Math.log10((double) GLOBAL_DOC_NUM / numDocs);
		
//		String numDocsStrAppend = SEPARATOR +  Integer.toString(numDocs);
		String idfStr = BuildInvertedIndexMapper.SEPARATOR +  Double.toString(idf);

		for (String line : docs)
		{
			context.write(
					key, 
					new Text(line + idfStr)
					);
		}
		
		
		
	}
	
}
