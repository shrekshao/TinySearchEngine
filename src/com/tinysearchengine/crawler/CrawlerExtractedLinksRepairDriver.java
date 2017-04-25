package com.tinysearchengine.crawler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import com.tinysearchengine.database.DdbConnector;
import com.tinysearchengine.database.DdbDocument;

public class CrawlerExtractedLinksRepairDriver {

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

		Date now = new Date();
		SimpleDateFormat dateFormat =
			new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		RollingFileAppender fileAppender =
			new RollingFileAppender(new PatternLayout(pattern),
					"repairer.log." + dateFormat.format(now));
		fileAppender.setThreshold(Level.INFO);
		fileAppender.setMaxFileSize("50MB");
		fileAppender.activateOptions();

		Logger.getRootLogger().addAppender(fileAppender);
	}

	public static void main(String[] args) throws IOException {
		setUpLogging();

		int delay = Math.max(100, Integer.parseInt(args[0]));
		logger.info("Running with delay: " + delay + " ms");
		DdbConnector connector = new DdbConnector();

		List<DdbDocument> docs = connector.getAllDocumentsLazily();
		Iterator<DdbDocument> docsIt = docs.iterator();

		while (docsIt.hasNext()) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				DdbDocument doc = docsIt.next();

				if (doc.getLinks() == null && !doc.getRepaired()) {
					HashSet<String> extractedLinks = new HashSet<>();
					logger.info("Repairing " + doc.getUrlAsString());
					byte[] content = doc.getContent();
					
					if (content == null) {
						logger.warn("No S3 content: " + doc.getUrlAsString());
						doc.setRepaired(true);
						connector.putDocument(doc);
						continue;
					}
					
					String[] links = URLExtractor.extract(content);
					for (String link : links) {
						try {
							URL resolvedUrl = new URL(doc.getUrl(), link);
							extractedLinks.add(resolvedUrl.toString());
						} catch (MalformedURLException e) {
							// Ignore
						}
					}

					doc.setLinks(extractedLinks);
					doc.setRepaired(true);
					connector.putDocument(doc);
				}
			} catch (Throwable e) {
				logger.error("Unexpected exception", e);
			}
		}
	}
}
