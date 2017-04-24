package com.tinysearchengine.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ClusterConfigurationParser {
	public static ArrayList<String> parseConfiguration(File configFile)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(configFile));
		try {
			String line = null;
			ArrayList<String> addrs = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				addrs.add(line);
			}

			return addrs;
		} finally {
			reader.close();
		}
	}
}
