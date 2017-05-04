package com.tinysearchengine.searchengine.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class IndexServlet extends HttpServlet {

	Configuration d_templateConfiguration;

	Template d_indexTemplate;

	public void init() throws ServletException {
		super.init();

		d_templateConfiguration = new Configuration();
		d_templateConfiguration.setDefaultEncoding("UTF-8");
		d_templateConfiguration.setTemplateExceptionHandler(
				TemplateExceptionHandler.RETHROW_HANDLER);

		InputStream idxFileStream =
			getClass().getResourceAsStream("index.ftlh");
		try {
			d_indexTemplate = new Template("index-template",
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
		root.put("time", new Date().toString());
		try {
			d_indexTemplate.process(root, response.getWriter());
		} catch (TemplateException e) {
			throw new ServletException(e);
		}
	}

}
