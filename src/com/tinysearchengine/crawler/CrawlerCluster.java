package com.tinysearchengine.crawler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import com.tinysearchengine.crawler.RobotInfoCache.RobotInfo;
import com.tinysearchengine.crawler.frontier.URLFrontier;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

/**
 * @author MIAO
 *
 */
public class CrawlerCluster {
	private int m_myIndex = -1;
	private URLFrontier m_frontier = null;
	private RobotInfoCache m_cache = null;
	private Map<URL, Boolean> m_due = null;
	private String[] m_workerConfig = null;

	public CrawlerCluster(int port,
			URLFrontier frontier,
			RobotInfoCache cache,
			Map<URL, Boolean> due,
			String[] workerConfig,
			int myIndex) {
		m_frontier = frontier;
		m_cache = cache;
		m_due = due;
		m_workerConfig = workerConfig;
		m_myIndex = myIndex;

		Spark.port(port);
		Spark.post("/pushdata", new Route() {
			@Override
			public Object handle(Request req, Response resp) {
				URL url = null;
				try {
					url = new URL(req.body());
				} catch (MalformedURLException e) {
					Logger logger = Logger.getLogger(CrawlerCluster.class);
					logger.warn(req.body());
					logger.warn(req.params("url") + " is a bad url!");
					logger.warn(e.getMessage());
					return "";
				}
				Map<URL, Boolean> due = m_due;

				// DUE
				if (!due.containsKey(url)) {
					due.put(url, true);

					URLFrontier frontier = m_frontier;

					// put head request into URLFrontier
					URLFrontier.Request headRequest = new URLFrontier.Request();
					headRequest.url = url;
					headRequest.method = "HEAD";

					long lastScheduledTime =
						frontier.lastScheduledTime(url.getAuthority());
					RobotInfo info = m_cache.getInfoForUrl(url);

					int crawlDelaySeconds = 2000;
					if (info != null) {
						crawlDelaySeconds =
							info.getCrawlDelay(Crawler.k_USER_AGENT);
					}

					frontier.put(headRequest,
							URLFrontier.Priority.Medium,
							lastScheduledTime + crawlDelaySeconds * 1000);
				}
				return url.toString();
			}
		});
	}

	private static String
			getParamString(ArrayList<Pair<String, String>> parameters) {
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

	private void sendData(String dest, String reqType, byte[] body)
			throws IOException {
		URL url = new URL("http://" + dest + "/pushdata");

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		try {
			conn.setDoOutput(true);
			conn.setRequestMethod(reqType);

			if (reqType.equals("POST")) {
				OutputStream os = conn.getOutputStream();
				os.write(body);
				os.flush();
			}

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				Logger logger = Logger.getLogger(CrawlerCluster.class);
				logger.error("Failed to pushdata to: " + url.toString());
			}
		} finally {
			conn.disconnect();
		}
	}

	private int getURLWorkerIndex(URL url) {
		return url.hashCode() % m_workerConfig.length;
	}

	public synchronized void distributeURL(URL url) throws IOException {
		int workerIndex = getURLWorkerIndex(url);
		if (workerIndex == m_myIndex) {
			if (m_due.containsKey(url)) {
				m_due.put(url, true);
				URLFrontier.Request headRequest = new URLFrontier.Request();
				headRequest.url = url;
				headRequest.method = "HEAD";

				long lastScheduledTime =
					m_frontier.lastScheduledTime(url.getAuthority());
				RobotInfo info = m_cache.getInfoForUrl(url);
				int crawlDelaySeconds = 0;
				if (info != null) {
					crawlDelaySeconds =
						info.getCrawlDelay(Crawler.k_USER_AGENT);
				}

				if (RobotInfoCache.canCrawl(info, url, Crawler.k_USER_AGENT)) {
					m_frontier.put(headRequest,
							URLFrontier.Priority.Medium,
							lastScheduledTime + crawlDelaySeconds * 1000);
				}
			}
		} else {
			String dest = m_workerConfig[workerIndex];
			sendData(dest, "POST", url.toString().getBytes());
		}
	}
}
