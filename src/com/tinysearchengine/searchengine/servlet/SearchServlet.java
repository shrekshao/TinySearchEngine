package com.tinysearchengine.searchengine.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.tinysearchengine.searchengine.othersites.AmazonItemResult;
import com.tinysearchengine.searchengine.othersites.RequestToOtherSites;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class SearchServlet extends HttpServlet {

	final int k_MAX_AMAZON_RESULTS = 15;

	Configuration d_templateConfiguration;

	Template d_searchResultTemplate;

	public class SearchResult {
		private String d_title;
		private String d_url;
		private String d_summary;

		public void setTitle(String title) {
			d_title = title;
		}

		public String getTitle() {
			return d_title;
		}

		public void setUrl(String url) {
			d_url = url;
		}

		public String getUrl() {
			return d_url;
		}

		public void setSummary(String summary) {
			d_summary = summary;
		}

		public String getSummary() {
			return d_summary;
		}

	}

	public class ThirdPartyResult {
		private String d_itemUrl;
		private String d_imgUrl;
		private String d_title;
		private String d_price;

		public ThirdPartyResult(AmazonItemResult amzr) {
			setItemUrl(amzr.itemUrl);
			setImageUrl(amzr.imgUrl);
			setTitle(amzr.title);
			setPrice(amzr.price);
		}

		public void setItemUrl(String url) {
			d_itemUrl = url;
		}

		public String getItemUrl() {
			return d_itemUrl;
		}

		public void setImageUrl(String url) {
			d_imgUrl = url;
		}

		public String getImageUrl() {
			return d_imgUrl;
		}

		public void setTitle(String title) {
			if (title.length() > 100) {
				title = title.substring(0, 97) + "...";
			}
			d_title = title;
		}

		public String getTitle() {
			return d_title;
		}

		public void setPrice(String p) {
			d_price = p;
		}

		public String getPrice() {
			return d_price;
		}
	}

	public void init() throws ServletException {
		super.init();

		d_templateConfiguration = new Configuration();
		d_templateConfiguration.setDefaultEncoding("UTF-8");
		d_templateConfiguration.setTemplateExceptionHandler(
				TemplateExceptionHandler.RETHROW_HANDLER);

		InputStream idxFileStream =
			getClass().getResourceAsStream("searchresult.ftlh");
		try {
			d_searchResultTemplate = new Template("searchresult-template",
					new InputStreamReader(idxFileStream),
					d_templateConfiguration);
		} catch (IOException e) {
			throw new ServletException(e);
		}

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		HashMap<String, Object> root = new HashMap<>();

		String queryTerm = request.getParameter("query");
		root.put("query", queryTerm);

		// Put the search results in
		ArrayList<SearchResult> results = new ArrayList<>();

		for (int i = 0; i < 10; ++i) {
			SearchResult r = new SearchResult();
			r.setTitle("This is foobar" + i);
			r.setUrl("http://www.foobar" + i + ".com");
			r.setSummary(String.join("",
					Collections.nCopies(20, "This is the content of foobar.")));
			results.add(r);
		}

		root.put("searchResults", results);

		// Put the amazon results in
		List<ThirdPartyResult> thirdPartyResults = new ArrayList<>();
		final List<ThirdPartyResult> collectedResults = thirdPartyResults;
		RequestToOtherSites.getAmazonResult(queryTerm).forEach((item) -> {
			collectedResults.add(new ThirdPartyResult(item));
		});
		if (thirdPartyResults.size() > k_MAX_AMAZON_RESULTS) {
			thirdPartyResults =
				thirdPartyResults.subList(0, k_MAX_AMAZON_RESULTS);
		}

		root.put("thirdPartyResults", thirdPartyResults);

		try {
			d_searchResultTemplate.process(root, response.getWriter());
		} catch (TemplateException e) {
			throw new ServletException(e);
		}

	}
}