package com.tinysearchengine.pagerank;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PageRankUrlProcessReducer extends Reducer<Text,Text,Text,Text> {
	@Override 
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		StringBuffer pointToCurLink = new StringBuffer();
		boolean isValid = false;
		for(Text value : values) { //value has two situation: 1. isvalid; 2. links that points to this key
			String curValue = value.toString();
			if(curValue.equalsIgnoreCase("isValid")) {
				isValid = true;
				context.write(key, new Text("isValid"));
			}
			else pointToCurLink.append(" " + curValue);
		}
		int pointersLength = pointToCurLink.length();
		if(pointersLength != 0 && isValid == true) {		
			//ignore the first " "
			context.write(key,new Text(pointToCurLink.toString().substring(1, pointToCurLink.length())));
		}
	}
}