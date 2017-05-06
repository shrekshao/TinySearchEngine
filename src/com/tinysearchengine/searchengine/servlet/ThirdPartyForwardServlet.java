package com.tinysearchengine.searchengine.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinysearchengine.searchengine.othersites.AmazonItemResult;
import com.tinysearchengine.searchengine.othersites.EbayItemResult;
import com.tinysearchengine.searchengine.othersites.RequestToOtherSites;

public class ThirdPartyForwardServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String query = request.getParameter("query");
		if (name.equals("amazon")) {
			ArrayList<AmazonItemResult> result = RequestToOtherSites.getAmazonResult(query);
			PrintWriter writer = response.getWriter();
			
			ObjectMapper mapper = new ObjectMapper();
			String jsonContent = mapper.writeValueAsString(result);
			writer.write(jsonContent);
			writer.close();
		} else if (name.equals("ebay")) {
			ArrayList<EbayItemResult> result = RequestToOtherSites.getEbayResult(query);
//			System.out.println(result.size());
			PrintWriter writer = response.getWriter();
			
			ObjectMapper mapper = new ObjectMapper();
			String jsonContent = mapper.writeValueAsString(result);
			writer.write(jsonContent);
			writer.close();
		}
	}
}
