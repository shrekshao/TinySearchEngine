package com.tinysearchengine.searchengine.othersites;

public class EbayItemResult {
	public String price = new String();
	public String itemUrl = new String();
	public String imgUrl = new String();
	public String title = new String();
	
	public void print() {
		System.out.println("Price: " + price);
		System.out.println("itemUrl: " + itemUrl);
		System.out.println("imgUrl: " + imgUrl);
		System.out.println("title: " + title);
	}
}
