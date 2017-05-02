package com.tinysearchengine.pagerank.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TestParserS3 {
	public static void main(String args[]) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("C:/Users/xueyin/Desktop/testreal01.txt"));
		try {
		    String line = br.readLine();
		    while (line != null) {
		    	String[] parts = line.split("\001");
		        String curUrl = parts[0];
		        System.out.println("Cur Url Is :" + curUrl);
		        String indexer = parts[1];
		        System.out.println("Di Part Is :" + indexer);
		        StringBuffer bf = new StringBuffer();
		        String complete = "";
		        if(parts.length <= 2) {
		        	System.out.println("No Outlinks, need to parse by yourself");
		        	continue;
		        }
		        String[] myurls = parts[2].split("\002");
		        System.out.println("wtf is this" + myurls.length);
//		        while(i < parts.length) {
//		        	bf.append(parts[i]);
//		        	complete += parts[i];
//		        	bf.append(" ");
//		        	complete += " ";
//		        	System.out.println(i);
//		        	i++;
//		        }
//		        System.out.println("Parts.length is: " + parts.length);
//		        String myoutlinks = bf.toString();
//		        System.out.println("My Outlinks Are: " + myoutlinks);
//		        System.out.println(complete);
		        line = br.readLine();
		    }
		} finally {
		    br.close();
		}
	}

}
