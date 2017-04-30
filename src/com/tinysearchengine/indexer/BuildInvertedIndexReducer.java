package com.tinysearchengine.indexer;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class BuildInvertedIndexReducer extends Reducer<Text, Text, Text, Object> {
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException 
	{
		int sum = 0;
		for (Text text : values)
		{
			String str = text.toString();
			String[] parts = str.split(BuildInvertedIndexMapper.SEPARATOR);
			
			sum += Integer.parseInt(parts[1]);
		}
		
//		context.write(key, new Text(Integer.toString(sum)));
		
		
	}
	
}
