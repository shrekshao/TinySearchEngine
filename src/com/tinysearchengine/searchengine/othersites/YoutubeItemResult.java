package com.tinysearchengine.searchengine.othersites;

public class YoutubeItemResult {
	public String title = new String();
	public String url = new String();
	public String embedUrl = new String();
	
	public void print() {
		System.out.println("title: " + title);
		System.out.println("url: " + url);
		System.out.println("embedUrl: " + embedUrl);
	}
}
