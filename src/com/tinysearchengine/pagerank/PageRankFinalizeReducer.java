package com.tinysearchengine.pagerank;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PageRankFinalizeReducer extends Reducer<Text,Text,Text,Text> {
	@Override 
	public void reduce(Text key ,Iterable<Text> values,Context context) throws IOException, InterruptedException {
		for(Text value : values) {
			context.write(key, value); //we need to write the url & score
			return;
		}
	}	
}