package com.tinysearchengine.pagerank.test;

public class TestReplaceALL {

	public static void main(String args[]) {
		String html = "http://www.cs.princeton.edu/~yingyul/Vocabulary-based Hashing for Image Search.pdf";
		String htmlnew = html.replaceAll(" ", "%20");
//		System.out.println(htmlnew);
//		System.out.println(html);
		
		String myhtml = "http://www.cs.princeton.edu/~yingyul/Vocabulary-based Hashing for Image Search.pdf";
		myhtml = myhtml.substring(0, myhtml.length() - 1);
//		System.out.println(myhtml);
		
		String tml = "http://www.cs.princeton.edu/~yingyul/Vocabulary-based Hashing for Image Search.pdf    ";
		String tml1 = "http://www.cs.princeton.edu/~yingyul/Vocabulary-based Hashing for Image Search.pdf ";
		tml = tml.replaceAll("\\s+$", "");
		tml1 = tml1.replaceAll("\\s+$", "");
		
		System.out.println(tml);
		System.out.println(tml1);

		
	}
}
