package com.tinysearchengine.searchengine.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class SearchServlet extends HttpServlet {

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
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		HashMap<String, Object> root = new HashMap<>();
		try {
			ArrayList<SearchResult> results = new ArrayList<>();

			for (int i = 0; i < 10; ++i) {
				SearchResult r = new SearchResult();
				r.setTitle("This is foobar" + i);
				r.setUrl("http://www.foobar" + i + ".com");
				r.setSummary(String.join("",
						Collections.nCopies(20,
								"This is the content of foobar.")));
				results.add(r);
			}

			root.put("searchResults", results);
			d_searchResultTemplate.process(root, response.getWriter());
		} catch (TemplateException e) {
			throw new ServletException(e);
		}
	}
}