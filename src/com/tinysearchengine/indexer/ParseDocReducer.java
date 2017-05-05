package com.tinysearchengine.indexer;

import java.io.IOException;
import java.util.HashSet;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class ParseDocReducer extends Reducer<Text, Text, Text, Text> {
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException 
	{

		// IdentityReducer
		for (Text value : values)
		{
			context.write( key, value );
		}
			
		
	}
}
