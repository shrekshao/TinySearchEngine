package com.tinysearchengine.pagerank;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PageRankS3PrepareReducer extends Reducer<Text,Text,Text,Text> {
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		String urlString = "Score|1.0";
		for(Text v : values) {
			String curoutlink = v.toString();
			urlString += "\002" + curoutlink;
		}
		System.out.println(urlString);
		context.write(key, new Text(urlString));
	}
}
