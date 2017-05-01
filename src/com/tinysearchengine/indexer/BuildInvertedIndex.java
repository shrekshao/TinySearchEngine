package com.tinysearchengine.indexer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.ToolRunner;

//import com.amazonaws.services.elasticmapreduce.model.Configuration;

public class BuildInvertedIndex {
//	public static void main(String[] args) 
//            throws Exception {
//		Configuration conf = new Configuration();
//		
//		Job job =  Job.getInstance(conf, "inverted index");
//		job.setJarByClass(BuildInvertedIndex.class);
//		
//		
//		job.setMapOutputKeyClass(Text.class); //Set Mapper Output
//	    job.setMapOutputValueClass(Text.class);
//		job.setOutputKeyClass(Text.class);
//		job.setOutputValueClass(Text.class);
//		
//		job.setMapperClass(BuildInvertedIndexMapper.class);
//		job.setReducerClass(BuildInvertedIndexReducer.class);
//		
////		job.setInputFormatClass(TextInputFormat.class);
////		job.setOutputFormatClass(TextOutputFormat.class);
//		
////		FileInputFormat.addInputPath(job, new Path(args[0]));
////		FileOutputFormat.setOutputPath(job, new Path(args[1]));
//		FileInputFormat.setInputPaths(job, new Path("filesForTest/invertedInderxer/input"));
//	    FileOutputFormat.setOutputPath(job, new Path("filesForTest/invertedInderxer/output"));
//		
//		job.waitForCompletion(true);
//	}
	
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new BuildInvertedIndexDriver(), args);
		System.exit(res);		
	}
}
