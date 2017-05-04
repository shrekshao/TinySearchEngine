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

	public void init() throws ServletException {
		super.init();

		d_templateConfiguration = new Configuration();
		d_templateConfiguration.setDefaultEncoding("UTF-8");
		d_templateConfiguration.setTemplateExceptionHandler(
				TemplateExceptionHandler.RETHROW_HANDLER);
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		InputStream idxFileStream =
			getClass().getResourceAsStream("index.ftlh");
		Template template = new Template("index-template",
				new InputStreamReader(idxFileStream),
				d_templateConfiguration);
		HashMap<String, Object> root = new HashMap<>();
		root.put("time", new Date().toString());
		try {
			template.process(root, response.getWriter());
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
