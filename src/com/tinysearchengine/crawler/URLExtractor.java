package com.tinysearchengine.crawler;

import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class URLExtractor {
	public static String[] extract(byte[] document) {
		Document doc = Jsoup.parse(new String(document));
		Elements link = doc.select("[href]");
		LinkedList<String> links = new LinkedList<String>();
		for (Element l : link) 
			links.add(l.attr("href"));
		String[] result = new String[links.size()];
		return links.toArray(result);
	}
}