package com.tinysearchengine.pagerank;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

public class PageRankUrlProcessDriver extends Configured implements Tool{
	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub

		//Start A Job
	    Job job = new Job();	    
	    job.setJobName("Step 1: Extract links and Invalidate");
	    
	    //Set Related Classes: driver, mapper, reducer
	    job.setJarByClass(PageRankUrlProcessDriver.class);   
	    job.setMapperClass(PageRankUrlProcessMapper.class);
	    job.setReducerClass(PageRankUrlProcessReducer.class);
	    
	    //Set Input Key-Value Pair Format
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(Text.class);
	   
	    //Set Output Key-Value Pair Format
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(Text.class);
	  
	    //Intermediate Save
	    FileInputFormat.setInputPaths(job, new Path(arg0[0]));
	    FileOutputFormat.setOutputPath(job, new Path(arg0[1]));
	    
		return job.waitForCompletion(true)?0:1;
	}
}