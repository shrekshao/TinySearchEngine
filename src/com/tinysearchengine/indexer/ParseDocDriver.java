package com.tinysearchengine.indexer;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

public class ParseDocDriver extends Configured implements Tool {

	@Override
	public int run(String[] arg0) throws Exception {
		Job job = Job.getInstance();
		job.setJobName("ParseDoc");
		job.setJarByClass(BuildInvertedIndexDriver.class);
		job.getConfiguration().set("mapreduce.output.basename", "ParsedDoc");
		job.getConfiguration().set("mapreduce.output.textoutputformat.separator", " ~~ ");
		
		job.setMapperClass(ParseDocMapper.class);
//		job.setReducerClass(IdentityReducer.class);
		job.setReducerClass(ParseDocReducer.class);
	    
	    job.setMapOutputKeyClass(Text.class); //Set Mapper Output
	    job.setMapOutputValueClass(Text.class);
	    job.setOutputKeyClass(Text.class); //Set Reducer Output
	    job.setOutputValueClass(Text.class);

	    FileInputFormat.setInputPaths(job, new Path(arg0[0]));
	    FileOutputFormat.setOutputPath(job, new Path(arg0[1]));
	    
		return job.waitForCompletion(true) ? 0 : 1;
	}

}
