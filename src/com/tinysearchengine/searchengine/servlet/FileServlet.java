package com.tinysearchengine.searchengine.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileServlet extends HttpServlet {
	// Be careful about the directory.
	final String dir = System.getProperty("user.dir") + "/build/webpages";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		File file = new File(dir + request.getRequestURI());
		// return the requested file if the request URI is a file
		if (file.isFile()) {
			InputStream is = new FileInputStream(file);
			OutputStream os = response.getOutputStream();
			int c = 0;
			byte[] buf = new byte[8192];
			while ((c = is.read(buf, 0, buf.length)) > 0) {
				os.write(buf, 0, c);
				os.flush();
			}
			os.close();
			is.close();
		}
		// else return 404
		else {
			PrintWriter out = response.getWriter();
			out.println("404 Not Found\n");
			response.sendError(404);
			out.close();
		}
	}
}
