package com.tinysearchengine.crawler;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class CrawlerCluster {
	
	public CrawlerCluster(int port) {
		Spark.setPort(port);
		
		Spark.post(new Route("/pushdata") {

			@Override
			public Object handle(Request arg0, Response arg1) {
				String url = arg0.params("url");
				
				// DUE
				
				// put into frontier
				
				return null;
			}
			
		});
	}
}
