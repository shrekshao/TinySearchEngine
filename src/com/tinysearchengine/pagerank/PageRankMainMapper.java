package com.tinysearchengine.pagerank;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

//input :  URL	  Score|1.0 outurl1 outurl2 ....
//output:  URL    PageRankScore(current round)
//get each single one, combine and process it in reducer

public class PageRankMainMapper extends Mapper<LongWritable,Text,Text,Text> {
	@Override 
	public void map(LongWritable key ,Text values, Context context) throws IOException, InterruptedException
	{
		String[] urls = values.toString().split("\t", 2); //based on the tab sign I stored
		if(urls.length != 2) { return; }
		context.write(new Text(urls[0]), new Text(urls[1]));					
		String curURL = urls[0]; 
		String scoreAndLinks[]= urls[1].split(" ", 2);  
		double averageScore = 0.0;
		double curPageRankScore = Double.parseDouble(scoreAndLinks[0].split("Score|")[1]); 
		if(scoreAndLinks.length != 2) { //no outURLs, points to nobody :(
			averageScore = curPageRankScore; //we just put it here
			context.write(new Text(curURL), new Text(String.valueOf(averageScore)));
		} else { //at least one outURLs
			String outURLstring = scoreAndLinks[1];
			String outURLs[] = outURLstring.split(" "); 
			int outURLNum = outURLs.length;
			if(outURLs.length > 0) { 
				averageScore = curPageRankScore / outURLs.length;
			} else {
				averageScore = 0.0;
			}
			for(int i = 0; i < outURLNum; i++) {
				context.write(new Text(outURLs[i]), new Text(String.valueOf(averageScore)));
			}	
		}
 	}
}