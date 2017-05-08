package com.tinysearchengine.indexer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;

public class ParseDocMain {
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new ParseDocDriver(), args);
		
		System.out.println("Test Done");
		
		System.exit(res);		
	}
}
