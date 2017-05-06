package com.tinysearchengine.pagerank.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExtractKeyWordList {
	
	static TestDdbConnector d_connector = new TestDdbConnector();
	static List<WordIdfDdbDocument> docs = new ArrayList<WordIdfDdbDocument>();
 	
	public static void main(String args[]) {
		docs = d_connector.getAllWordIdf();
		Iterator<WordIdfDdbDocument> docIt = docs.iterator();
		int sum = 0;
		//int counter = 50000;
		try {
			//File statText = new File("C:/Users/xueyin/Documents/GitHub/TinySearchEngine/pagerankinput/myinput1.txt");
			File statText = new File("/Users/owner/TinySearchEngine/pagerankinput/keywordlist.txt");
			FileOutputStream is = new FileOutputStream(statText);
			OutputStreamWriter osw = new OutputStreamWriter(is);    
			Writer w = new BufferedWriter(osw);
			while (docIt.hasNext()) {
				WordIdfDdbDocument doc = docIt.next();
				w.write(doc.getWord());
				w.write("\r\n");
				//counter--;
				sum++;
				if (sum % 500 == 0) {
					System.out.println("Have already written :" + sum/500 + " * 500 of files");
				}
			}
			w.close();
			System.out.println("Finish reading Keyword List :)");
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
}
