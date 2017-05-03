package com.tinysearchengine.pagerank.test;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class TestResolvedURL {
	
	public static void main(String args[]) throws MalformedURLException, UnsupportedEncodingException {
		String curUrl = "https://plumbr.eu/outofmemoryerror/java-heap-space";
		String mailto = "mailto:support@plumbr.eu";
		String rel = "/use-case-production";
		String base = "http://www.cs.princeton.edu/~yingyul/";
		String space = "Vocabulary-based Hashing for Image Search.pdf";
		URL CURURL = new URL(curUrl);		
		URL BASE = new URL(base);
		URL resolvedUrl = new URL(CURURL, mailto);
		System.out.println(resolvedUrl.toString());
		URL resolvedUrl2 = new URL(CURURL, rel); 
		System.out.println(resolvedUrl2.toString());	
		URL resolvedUrl3 = new URL(BASE, space);
		System.out.println(resolvedUrl3.toString());
		String str = new String("http://www.baidu.com/h|ello-mmm Score|1.0");
		for (String s : str.split("|")) {
			System.out.println("1:" + s);
		}
		for (String s : str.split("\\|")) {
			System.out.println("2:" + s);
		}
		String sss = new String("https://physics.stanford.edu/sites/default/files/TA_Responsibilities_July_2012_0.pdf	Score|0.16328125000000002");
		for (String ssss : sss.split("\t", 2)) {
			System.out.println(ssss);
		}
		for (String ssss : sss.split("\\t", 2)) {
			System.out.println(ssss);
		}
	}

}
