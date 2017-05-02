package com.tinysearchengine.pagerank.test;

import java.net.MalformedURLException;
import java.net.URL;

public class TestResolvedURL {
	
	public static void main(String args[]) throws MalformedURLException {
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
		}

}
