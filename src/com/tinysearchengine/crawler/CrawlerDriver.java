package com.tinysearchengine.crawler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import java.net.URL;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class CrawlerDriver {

	static Logger logger = Logger.getLogger(CrawlerDriver.class);

	public static void setUpLogging() throws IOException {
		ConsoleAppender appender = new ConsoleAppender();
		String pattern = "[%p] %c [%t] %d: %m%n";
		appender.setLayout(new PatternLayout(pattern));
		appender.setThreshold(Level.INFO);
		appender.activateOptions();

		if (!Logger.getRootLogger().getAllAppenders().hasMoreElements()) {
			Logger.getRootLogger().addAppender(appender);
		}

		long now = (new Date()).getTime();
		FileAppender fileAppender =
			new FileAppender(new PatternLayout(pattern), "crawler.log." + now);
		fileAppender.setThreshold(Level.DEBUG);
		fileAppender.activateOptions();
		
		Logger.getRootLogger().addAppender(fileAppender);
	}

	public static void main(String[] args)
			throws IOException, InterruptedException {
		if (args.length < 3) {
			System.out.println(
					"Usage: java -jar crawler.jar <index> <db> <cluster.config> <seed>");
			System.exit(1);
		}

		setUpLogging();

		int crawlerIdx = Integer.parseInt(args[0]);
		File configFile = new File(args[2]);

		ArrayList<String> config =
			ClusterConfigurationParser.parseConfiguration(configFile);
		logger.info("Starting with config: " + config.toString());
		logger.info("I am crawler number: " + crawlerIdx);
		logger.info("Storage: " + args[1]);

		if (config.size() <= crawlerIdx) {
			System.out.println("There is no config for crawler: " + crawlerIdx);
			System.exit(1);
		}

		String addr = config.get(crawlerIdx);
		String[] parts = addr.split(":");
		if (parts.length < 2) {
			System.out.println(
					"Config file is malformed for index: " + crawlerIdx);
			System.exit(1);
		}

		int port = Integer.parseInt(parts[1]);
		int nThread = 8;

		HashSet<URL> seeds = new HashSet<>();
		for (int i = 3; i < args.length; ++i) {
			logger.info("Seed: " + args[i]);
			seeds.add(new URL(args[i]));
		}

		Crawler crawler =
			new Crawler(args[1], port, nThread, seeds, config, crawlerIdx);
		crawler.start();

		while (true) {
			Thread.sleep(100000);
		}
	}
}
