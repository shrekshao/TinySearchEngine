package com.tinysearchengine.pagerank;

import org.apache.hadoop.util.ToolRunner;

public class PageRankMain {

	static int iterations = 10;
	
	public static void main(String args[]) throws Exception {
		int flag = 0;
		if(args.length != 2) {
			System.err.println("The input parameters are not correct.");
			return;
		}
		//we get the correct parameters to start out program
		String input = args[0];
		String output = args[1];
		String iterIn = "";
		String iterOut = "";
		
		//Start Main PageRank Procedure
		for (int i = 0; i < iterations; i++) {
			iterIn = (i == 0? input : iterOut);
			iterOut = output + "/Step1_PageRank" + String.valueOf(i);
			System.out.println("Current is Round :" + i);
			String[] paras = new String[] { iterIn, iterOut };
			flag = ToolRunner.run(new PageRankMainDriver(), paras);
			if (flag == 1) { //we need flag = 0
				System.err.println("PageRank failed at Iteration: " + i);
				System.exit(flag);
			}
		}

		//Run PageRank Finalize Driver
		String[] lastParas = new String[] { iterOut , output + "/output" };
		flag = ToolRunner.run(new PageRankFinalizeDriver(), lastParas);
		if (flag == 1) { System.exit(flag); }
		
		System.out.println("Finishing PageRank Correctly :)");
		System.exit(0);
	}
}
