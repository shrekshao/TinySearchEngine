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
import com.tinysearchengine.searchengine.othersites.RequestToOtherSites;

public class ThirdPartyForwardServlet extends HttpServlet {
//	private String encodeQuote(String content) {
//		return content.replace("\"", "\\\"").replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n");
//	}
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String name = request.getParameter("name");
		String query = request.getParameter("query");
		//System.out.println(encodeQuote("http://cnm.com/\"helo\""));
		if (name.equals("amazon")) {
			// my own json converter
//			ArrayList<AmazonItemResult> result = RequestToOtherSites.getAmazonResult(query);
//			PrintWriter writer = response.getWriter();
//			writer.write("[");
//			int length = result.size();
//			for (int i = 0; i < length; i++) {
//				writer.write("{");
//				writer.write("\"price\":" + "\"" + encodeQuote(result.get(i).price) + "\",");
//				writer.write("\"itemUrl\":" + "\"" + encodeQuote(result.get(i).itemUrl) + "\",");
//				writer.write("\"imgUrl\":" + "\"" + encodeQuote(result.get(i).imgUrl) + "\",");
//				writer.write("\"title\":" + "\"" + encodeQuote(result.get(i).title) + "\"");
//				writer.write("}");
//				if (i != length - 1)
//					writer.write(",");
//			}	
//			writer.write("]");
//			writer.close();
			
			// use ObjectMapper instead
			ArrayList<AmazonItemResult> result = RequestToOtherSites.getAmazonResult(query);
			PrintWriter writer = response.getWriter();
			
			ObjectMapper mapper = new ObjectMapper();
			String jsonContent = mapper.writeValueAsString(result);
			writer.write(jsonContent);
			writer.close();
		} else if (name.equals("ebay")) {
			
		}
	}
}
