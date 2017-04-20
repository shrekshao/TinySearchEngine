package com.tinysearchengine.crawler;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

/**
 * @author MIAO
 *
 */
public class CrawlerCluster {
	private CrawlerContext m_context = null;
	public CrawlerCluster(int port, CrawlerContext context) {
		m_context = context;

		Spark.port(port);
		Spark.post("/pushdata", new Route() {

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
