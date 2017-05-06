package com.tinysearchengine.pagerank.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

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
		
		SnowballStemmer d_stemmer = new englishStemmer();
		d_stemmer.setCurrent("chocolate");
		d_stemmer.stem();
		System.out.println(d_stemmer.getCurrent());
		

//		String queryTerm = "Chocolate Milk";
//		String[] terms = queryTerm.split("\\s+"); //get each term in the query string
//		HashMap<String, String> queryAndStem = new HashMap<String, String>(); //query and stem hashset
//		List<String> stemmedTerms = new LinkedList<String>();
//		for (String term : terms) { //traverse through whole terms
//			if (StopWordList.stopwords.contains(term)) {
//				queryAndStem.put(term, term);
//				continue;
//			} 
//			d_stemmer.setCurrent(term);
//			System.out.println("Cur Term is:" + term);
//			d_stemmer.stem();
//			queryAndStem.put(d_stemmer.getCurrent(), term);
//			stemmedTerms.add(d_stemmer.getCurrent());
//			System.out.println("Debug: Print stemmer" + d_stemmer.getCurrent());
//		}
	}
}
