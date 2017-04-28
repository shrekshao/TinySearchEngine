package com.tinysearchengine.pagerank;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;


public class PageRankMainDriver extends Configured implements Tool  {
	@Override
	public int run(String[] arg0) throws Exception {
		Job job = Job.getInstance();
		job.setJobName("PageRankMain");
		job.setJarByClass(PageRankMainDriver.class);
		job.getConfiguration().set("mapreduce.output.basename", "PageRank_Result");
	    
		job.setMapperClass(PageRankMainMapper.class); //Set Mapper Class
		job.setReducerClass(PageRankMainReducer.class); //Set Reducer Class
	    
	    job.setMapOutputKeyClass(Text.class); //Set Mapper Output
	    job.setMapOutputValueClass(Text.class);
	    job.setOutputKeyClass(Text.class); //Set Reducer Output
	    job.setOutputValueClass(Text.class);
	  
	    FileInputFormat.setInputPaths(job, new Path(arg0[0]));
	    FileOutputFormat.setOutputPath(job, new Path(arg0[1]));
	    
		return job.waitForCompletion(true) ? 0 : 1;
    }
}
