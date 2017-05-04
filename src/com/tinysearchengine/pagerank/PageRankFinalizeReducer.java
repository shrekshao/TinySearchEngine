package com.tinysearchengine.pagerank;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PageRankFinalizeReducer extends Reducer<Text,Text,Text,Text> {
	@Override 
	public void reduce(Text key ,Iterable<Text> values,Context context) throws IOException, InterruptedException {
		String keystr = key.toString();
		if(keystr.contains("%09") == false) {
			for(Text value : values) {
				context.write(key, value); //we need to write the url & score
				return;
			}
		} else {
			keystr = keystr.replaceAll("%09", "\t");
			for(Text value : values) {
				context.write(new Text(keystr), value); //we need to write the url & score
				return;
			}
		}
	}	
}