package com.tinysearchengine.pagerank;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

public class PageRankS3PrepareDriver extends Configured implements Tool{
	@Override
	public int run(String[] arg0) throws Exception {
		// TODO Auto-generated method stub
		Job job = Job.getInstance(); 
	    job.setJobName("PageRankReadFromS3");
	    job.setJarByClass(PageRankS3PrepareDriver.class);
	     
	    job.setMapperClass(PageRankS3PrepareMapper.class); //Set Mapper Class
	    job.setReducerClass(PageRankS3PrepareReducer.class); //Set Reducer Class 
	    
	    job.setMapOutputKeyClass(Text.class); //Set Mapper Output
	    job.setMapOutputValueClass(Text.class);
	    job.setOutputKeyClass(Text.class); //Set Reducer Output
	    job.setOutputValueClass(Text.class);
	  
	    FileInputFormat.setInputPaths(job, new Path(arg0[0]));
	    FileOutputFormat.setOutputPath(job, new Path(arg0[1]));
	    
		return job.waitForCompletion(true) ? 0 : 1;
	}
}
 
