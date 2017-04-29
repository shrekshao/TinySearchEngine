package com.tinysearchengine.pagerank;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PageRankFinalizeMapper extends Mapper<LongWritable,Text,Text,Text> {
	@Override 
	public void map(LongWritable key, Text values, Context context) throws IOException, InterruptedException {
		String[] urls = values.toString().split("\t",2);
		if(urls.length != 2) {
			System.err.println("Invalid Input at Line" + new Text(key.toString()));
			return;
		} else {
			String curURL = urls[0];
			String finalPageRankScore = urls[1].split(" ")[0]; //|Score0.555
			double myScore = Double.parseDouble(finalPageRankScore.substring(6));//0.555
			context.write(new Text(curURL), new Text(String.valueOf(myScore)));
		}
	}
}