package com.tinysearchengine.indexer;

import java.io.IOException;
import java.util.HashSet;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class BuildInvertedIndexReducer extends Reducer<Text, Text, Text, Text> {
	
	static final String SEPARATOR = " ";
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException 
	{
		int numDocs = 0;	// sum of documents that contain this
		
		HashSet<String> docs = new HashSet<String>();
		
		for (Text text : values)
		{
//			String str = text.toString();
//			String[] parts = str.split(BuildInvertedIndexMapper.SEPARATOR);
//			sum += Integer.parseInt(parts[1]);
			
			
			docs.add(text.toString());
		}
		
		numDocs = docs.size();
		
		
		
//		context.write(key, new Text(Integer.toString(sum)));
		
		StringBuilder sb = new StringBuilder();
//		sb.append(key.toString());
//		sb.append(SEPARATOR);
		sb.append(Integer.toString(numDocs));
		
		context.write(key, new Text(sb.toString()));
		
		
	}
	
}
