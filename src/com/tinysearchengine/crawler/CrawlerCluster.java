package com.tinysearchengine.crawler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.tuple.Pair;

import com.tinysearchengine.crawler.frontier.URLFrontier;
import com.tinysearchengine.utils.URLHelper;

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
			public Object handle(Request arg0, Response arg1) throws MalformedURLException {
				String strURL = arg0.params("url");
				LinkedHashMap<String, String> due = m_context.getDue();
				
				// DUE
				String fingerPrint = URLHelper.getFingerPrint(strURL);
				if (!due.containsKey(fingerPrint)) {
					due.put(fingerPrint, strURL);
					
					URL url = new URL(strURL);
					URLFrontier frontier = m_context.getFrontier();
					
					// put head request into URLFrontier
					URLFrontier.Request headRequest = new URLFrontier.Request();
					headRequest.url = url;
					headRequest.method = "HEAD";
					frontier.put(headRequest, URLFrontier.Priority.High, 5000);
					
					// put get request into URLFrontier
					frontier.put(url, URLFrontier.Priority.High, 5000);
				}
				return strURL;
			}
			
		});
	}
	
	private String getParamString(ArrayList<Pair<String, String>> parameters) {
		String params = new String();
		int c = 0;
		for (Pair<String, String> p : parameters) {
			if (c == 0)
				params += p.getLeft() + "=" + p.getRight();
			else
				params += "&" + p.getLeft() + "=" + p.getRight();
			c++;
		}
		return params;
	}
	
	private void sendData(String dest, String reqType, ArrayList<Pair<String, String>> parameters) throws IOException {
		URL url = new URL("http://" + dest + "/pushdata");
				
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod(reqType);
		
		if (reqType.equals("POST")) {
			OutputStream os = conn.getOutputStream();
			byte[] toSend = getParamString(parameters).getBytes();
			os.write(toSend);
			os.flush();
		}
		
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("Send data failed.");
		}
	}
	
	private int getURLWorkerIndex(String url) {
		// TODO
		return 0;
	}
	
	public void distributeURL(String url, ArrayList<Pair<String, String>> parameters) throws IOException {
		int workerIndex = getURLWorkerIndex(url);
		String dest = m_context.getWorkerByIndex(workerIndex);
		
		sendData(dest, "POST", parameters);
	}
}
