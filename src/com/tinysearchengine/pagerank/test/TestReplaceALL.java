package com.tinysearchengine.pagerank.test;

public class TestReplaceALL {

	public static void main(String args[]) {
		String html = "http://www.cs.princeton.edu/~yingyul/Vocabulary-based Hashing for Image Search.pdf";
		String htmlnew = html.replaceAll(" ", "%20");
		System.out.println(htmlnew);
		System.out.println(html);
	}
}
