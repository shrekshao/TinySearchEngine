package com.tinysearchengine.crawler;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.tidy.*;


public class URLExtractor {
	public static String[] extract(byte[] document) {
		Tidy tidy = new Tidy();
		tidy.setShowWarnings(false);
		tidy.setQuiet(true);
		ByteArrayInputStream is = new ByteArrayInputStream(document);
		Document dom = tidy.parseDOM(is, null);

		if (dom == null) {
			return null;
		} else {
			LinkedList<String> links = new LinkedList<String>();
			NodeList anchors = dom.getElementsByTagName("a");
			for (int i = 0; i < anchors.getLength(); ++i) {
				org.w3c.dom.Node anchor = anchors.item(i);
				NamedNodeMap attrs = anchor.getAttributes();
				org.w3c.dom.Node link = attrs.getNamedItem("href");
				if (link != null) {
					String linkValue = link.getNodeValue();
					if (linkValue != null) {
						links.add(linkValue);
					}
				}
			}

			String[] result = new String[links.size()];
			return links.toArray(result);
		}
	}
}