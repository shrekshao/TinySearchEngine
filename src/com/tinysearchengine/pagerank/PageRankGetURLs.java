package com.tinysearchengine.pagerank;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tinysearchengine.crawler.URLExtractor;
import com.tinysearchengine.database.*;

public class PageRankGetURLs {
	
	static DdbConnector d_connector = new DdbConnector();
	static List<DdbDocument> docs = new ArrayList<DdbDocument>();
 	
	public static void main(String args[]) {
		docs = d_connector.getAllDocumentsLazily();
		Iterator<DdbDocument> docIt = docs.iterator();
		int counter = 500;
		try {
			File statText = new File("C:/Users/xueyin/Documents/GitHub/TinySearchEngine/pagerankinput/input1.txt");
			FileOutputStream is = new FileOutputStream(statText);
			OutputStreamWriter osw = new OutputStreamWriter(is);    
			Writer w = new BufferedWriter(osw);
			while (counter > 0 && docIt.hasNext()) {
				DdbDocument doc = docIt.next();
				w.write(doc.getUrlAsString() + "	Score|1.0");
				if (doc.getLinks() == null) { 
					byte[] curByte = doc.getContent();
					String[] links = URLExtractor.extract(curByte);
					for(String link: links) {
						if(link.startsWith("https") || link.startsWith("http")) {
							w.write(" " + link); //delete mailto: and other cases 
						}					
					}
				} else {
					for(String link : doc.getLinks()) {
						if(link.startsWith("https") || link.startsWith("http")) {
							w.write(" " +  link);
						}
					}
				}
				w.write("\r\n");
				counter--;
			}
			w.close();
			System.out.println("PageRank Test File Formation Finished :)");
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
	
	
	
	
	
	
	

}
