package com.tinysearchengine.pagerank;

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

import com.tinysearchengine.crawler.URLExtractor;
import com.tinysearchengine.database.*;

public class PageRankGetURLs {
	
	static DdbConnector d_connector = new DdbConnector();
	static List<DdbDocument> docs = new ArrayList<DdbDocument>();
 	
	public static void main(String args[]) {
		docs = d_connector.getAllDocumentsLazily();
		Iterator<DdbDocument> docIt = docs.iterator();
		int sum = 0;
		//int counter = 50000;
		try {
			//File statText = new File("C:/Users/xueyin/Documents/GitHub/TinySearchEngine/pagerankinput/myinput1.txt");
			File statText = new File("/Users/owner/TinySearchEngine/pagerankinput/macinput.txt");
			FileOutputStream is = new FileOutputStream(statText);
			OutputStreamWriter osw = new OutputStreamWriter(is);    
			Writer w = new BufferedWriter(osw);
			while (docIt.hasNext()) {
				DdbDocument doc = docIt.next();
				w.write(doc.getUrlAsString() + "	Score|1.0");
				if (doc.getLinks() == null) {  
					byte[] curByte = doc.getContent();
					if(curByte == null) {
						sum++;
						continue;
					}
					String[] links = URLExtractor.extract(curByte);
					for(String link: links) {
						if(link.startsWith("https") || link.startsWith("http")) {
							new URL(link, parsedUrlStr).toString();
							if(link.contains(" ")) {
								link = link.replaceAll(" ", "%20");
							}
							w.write(" " + link); //delete mailto: and other cases 
						}					
					}
				} else {
					for(String link : doc.getLinks()) {
						if(link.startsWith("https") || link.startsWith("http")) {
							if(link.contains(" ")) {
								link = link.replaceAll(" ", "%20");
							}
							w.write(" " +  link); //delete " "
						}
					}
				}
				w.write("\r\n");
				//counter--;
				sum++;
				if (sum % 500 == 0) {
					System.out.println("Have already written :" + sum/500 + " * 500 of files");
				}
			}
			w.close();
			System.out.println("PageRank Test File Formation Finished :)");
        } catch (IOException e) {
        	e.printStackTrace();
        }
	}
}
