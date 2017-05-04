package com.tinysearchengine.searchengine.othersites.test;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;

import com.tinysearchengine.searchengine.othersites.AmazonItemResult;
import com.tinysearchengine.searchengine.othersites.RequestToOtherSites;

public class TestRequestToOtherSites {
	public static void main(String args[]) throws ClientProtocolException, IOException {
		ArrayList<AmazonItemResult> result = RequestToOtherSites.getAmazonResult(URLEncoder.encode("Head dryer", "UTF-8"));
		
		int c = 0;
		for (AmazonItemResult item : result) {
			System.out.println("Item " + (c++) + ":");
			item.print();
		}
	}
}
