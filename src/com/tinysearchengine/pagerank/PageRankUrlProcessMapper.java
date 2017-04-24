package com.tinysearchengine.pagerank;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

//now assume input is key: url, value: {a set of output urls}

public class PageRankUrlProcessMapper extends Mapper<LongWritable, Text, Text, Text> {
	@Override 
	//input format : LongWritables, Text
	//Need to accommodate to Crawler input
	public void map(LongWritable key, Text values, Context context) throws IOException, InterruptedException {
		int counter = 0;
		String[] urls = values.toString().split("\t", 2);
		if(urls.length != 2) {
			System.out.println("this url is not valid"); //maybe the url is not valid, the url does not have any outlinks, etc. A lot needs to be considered
			return;
		}
		String url = urls[0]; 
		context.write(new Text(url), new Text("isValid"));
		String[] curOutlinks = urls[1].split(" ");
		for(String link : curOutlinks) {
			context.write(new Text(link), new Text(url)); //inverse key value pair, find all the links that points to it
			//one key could have multiple values
			//link2: link1, link2: link3.
			counter++;
		}
	}
}
