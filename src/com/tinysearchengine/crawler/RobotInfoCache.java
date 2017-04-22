package com.tinysearchengine.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * This class implements a robot.txt cache, and provides methods for testing
 * whether a given URL is allowed to be crawled.
 * 
 * @author hengchu
 *
 */
public class RobotInfoCache {
	
	static Logger logger = Logger.getLogger(RobotInfoCache.class);
	
	public static class RobotInfo {
		Map<String, List<String>> d_disallowedLinks = new HashMap<>();
		Map<String, Integer> d_crawlDelay = new HashMap<>();
		Set<String> d_userAgents = new HashSet<>();

		/**
		 * Add a disallowed link for a particular user agent.
		 * 
		 * @param userAgent
		 * @param link
		 */
		public void addDisallowedLink(String userAgent, String link) {
			List<String> links = d_disallowedLinks.get(userAgent);
			if (links == null) {
				links = new ArrayList<>();
			}
			links.add(link);
			d_disallowedLinks.put(userAgent, links);
			d_userAgents.add(userAgent);
		}

		/**
		 * Adds a record of crawl-delay for a particular user agent. The delay
		 * parameter is in seconds.
		 * 
		 * @param userAgent
		 * @param delay
		 */
		public void addCrawlDelay(String userAgent, int delay) {
			d_crawlDelay.put(userAgent, delay);
			d_userAgents.add(userAgent);
		}

		/**
		 * Return the list of disallowed links for this userAgent. If nothing is
		 * disallowed, returns null.
		 * 
		 * @param userAgent
		 * @return
		 */
		public List<String> getDisallowedLinks(String userAgent) {
			return d_disallowedLinks.get(userAgent);
		}

		/**
		 * Return the crawlDelay specified for this userAgent. If no delay is
		 * specified, return 0.
		 * 
		 * @param userAgent
		 * @return
		 */
		public int getCrawlDelay(String userAgent) {
			Integer delay = d_crawlDelay.get(userAgent);
			Integer delayAny = d_crawlDelay.get("*");
			if (delay == null && delayAny == null) {
				return 0;
			} else if (delay != null){
				return delay.intValue();
			} else {
				return delayAny.intValue();
			}
		}
	}

	/**
	 * Store up to 5000 robot.txt in memory.
	 */
	private final static int k_MAX_CACHE_SIZE = 5000;

	/**
	 * The LRU Cache of robot.txt objects.
	 */
	LinkedHashMap<String, RobotInfo> d_lruCache =
		new LinkedHashMap<String, RobotInfo>(2000) {
			private static final long serialVersionUID = 4334986080089388200L;

			@Override
			protected boolean
					removeEldestEntry(Map.Entry<String, RobotInfo> entry) {
				if (size() > k_MAX_CACHE_SIZE) {
					return true;
				}

				return false;
			}
		};

	/**
	 * A method for parsing robot.txt. Return null if the string can't be parsed
	 * as a robot.txt file.
	 * 
	 * @param content
	 * @return
	 */
	public static RobotInfo parserRobotTxt(String content) {
		BufferedReader reader = new BufferedReader(new StringReader(content));
		String line = null;
		try {
			RobotInfo info = new RobotInfo();
			String currUserAgent = null;
			while ((line = reader.readLine()) != null) {
				String lowercaseLine = line.toLowerCase();
				boolean shouldProcess = lowercaseLine.startsWith("user-agent")
						|| lowercaseLine.startsWith("disallow")
						|| lowercaseLine.startsWith("crawl-delay");
				if (!shouldProcess) {
					continue;
				}

				String parts[] = line.split("\\s*:\\s*");
				if (parts.length < 2) {
					continue;
				}

				String key = parts[0];
				if (key.equalsIgnoreCase("User-agent")) {
					currUserAgent = parts[1].trim();
					continue;
				}

				if (key.equalsIgnoreCase("Disallow") && currUserAgent != null) {
					info.addDisallowedLink(currUserAgent, parts[1]);
					continue;
				}

				if (key.equalsIgnoreCase("Crawl-delay")
						&& currUserAgent != null) {
					try {
						int delay = Integer.parseInt(parts[1]);
						info.addCrawlDelay(currUserAgent, delay);
					} catch (NumberFormatException e) {
						// Just silence it.
					}
					continue;
				}
			}

			return info;
		} catch (IOException e) {
			logger.debug(e.getStackTrace());
			return null;
		}
	}

	/**
	 * A thread-safe getter for robot.txt info for a given url. Note that this
	 * method implements caching based on a LRU cache. So repeated retrieval for
	 * a url of the same domain will not be expensive.
	 * 
	 * @param url
	 * @return
	 */
	public RobotInfo getInfoForUrl(URL url) {
		synchronized (this) {
			if (d_lruCache.containsKey(url.getAuthority())) {
				return d_lruCache.get(url.getAuthority());
			}
		}

		try {
			URL robotTxtUrl = new URL(url, "/robots.txt");
			URLConnection conn = robotTxtUrl.openConnection();
			if (!(conn instanceof HttpURLConnection)) {
				return null;
			}

			HttpURLConnection connection = (HttpURLConnection) conn;
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(2000);
			connection.setDoInput(true);

			StringBuilder output = new StringBuilder();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				output.append(line);
				output.append('\n');
			}

			RobotInfo info = parserRobotTxt(output.toString());
			synchronized (this) {
				d_lruCache.put(url.getAuthority(), info);
			}
			return info;

		} catch (MalformedURLException e) {
			logger.debug(e.getStackTrace());
			return null;
		} catch (ProtocolException e) {
			logger.debug(e.getStackTrace());
			return null;
		} catch (IOException e) {
			logger.debug(e.getStackTrace());
			return null;
		}
	}

	private static boolean canCrawlImpl(String pattern, URL url) {
		String filePath = url.getFile();
		if ((filePath + "/").startsWith(pattern)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Return true if the URL can be crawled for the given agent name.
	 * Otherwise, return false.
	 * 
	 * @param info
	 * @param url
	 * @return
	 */
	public static boolean canCrawl(RobotInfo info, URL url, String agentName) {
		if (info == null) {
			return true;
		}

		List<String> disallowedAny = info.getDisallowedLinks("*");
		List<String> disallowed = info.getDisallowedLinks(agentName);
		if (disallowedAny == null && disallowed == null) {
			return true;
		}

		if (disallowedAny != null) {
			for (String pattern : disallowedAny) {
				if (!canCrawlImpl(pattern, url)) {
					return false;
				}
			}
		}

		if (disallowed != null) {
			for (String pattern : disallowed) {
				if (!canCrawlImpl(pattern, url)) {
					return false;
				}
			}
		}

		return true;
	}
}
