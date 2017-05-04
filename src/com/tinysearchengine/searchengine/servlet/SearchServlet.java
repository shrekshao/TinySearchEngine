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

import org.apache.commons.io.IOUtils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class SearchServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {
	}
}