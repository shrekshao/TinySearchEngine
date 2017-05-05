package com.tinysearchengine.searchengine.othersites;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RequestToOtherSites {
	
	static CloseableHttpClient httpClient = HttpClients.createDefault();
	
	static public ArrayList<AmazonItemResult> getAmazonResult(String[] keywords)
			throws ClientProtocolException, IOException {
		return getAmazonResult(Arrays.toString(keywords));
	}

	static public ArrayList<AmazonItemResult> getAmazonResult(String keyword)
			throws ClientProtocolException, IOException {
		//System.out.println("Enter***************************");
		keyword = URLEncoder.encode(keyword, "UTF-8");
		final HttpGet request = new HttpGet(
				"https://www.amazon.com/s/ref=nb_sb_ss_c_1_3?url=search-alias%3Daps&field-keywords=" + keyword);
		request.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
		request.setHeader("Accept", "text/html");
		CloseableHttpResponse httpResponse = httpClient.execute(request);
		HttpEntity entity = httpResponse.getEntity();
		String content = EntityUtils.toString(entity);

		Document doc = Jsoup.parse(content);
		
//		PrintWriter writer = new PrintWriter("response.html", "UTF-8");
//		writer.println(content);
//		writer.close();
		//System.out.println(content);
		int total = 0;
		ArrayList<AmazonItemResult> itemList = new ArrayList<AmazonItemResult>();
		while (true) {
			Element e = doc.getElementById("result_" + total);
			if (e == null)
				break;
			AmazonItemResult item = new AmazonItemResult();

			Elements aElements = e.getElementsByTag("a");
			if (!aElements.isEmpty()) {

				String title = aElements.attr("title");
				if (title != null)
					item.title = title;

				Element itemUrlElement = aElements.get(0);
				String itemUrl = itemUrlElement.attr("href");
				if (itemUrl != null)
					item.itemUrl = itemUrl;

				Elements itemImgUrlElement = itemUrlElement.getElementsByTag("img");
				if (!itemImgUrlElement.isEmpty()) {
					String imgUrl = itemImgUrlElement.attr("src");
					if (imgUrl != null)
						item.imgUrl = imgUrl;
				}

			}
			Elements tmp0 = e.getElementsByClass("sx-price-currency");
			Elements tmp1 = e.getElementsByClass("sx-price-whole");
			Elements tmp2 = e.getElementsByClass("sx-price-fractional");

			if (!tmp0.isEmpty() && !tmp1.isEmpty() && !tmp2.isEmpty()) {
				Element itemCurrency = tmp0.get(0);
				Element itemWhole = tmp1.get(0);
				Element itemFractional = tmp2.get(0);
				item.price = itemCurrency.text() + itemWhole.text() + "." + itemFractional.text();
			}

			itemList.add(item);
			total++;
		}

		return itemList;
	}
	
	static public ArrayList<EbayItemResult> getEbayResult(String[] keywords)
			throws ClientProtocolException, IOException {
		return getEbayResult(Arrays.toString(keywords));
	}
	
	static public ArrayList<EbayItemResult> getEbayResult(String keyword) throws ClientProtocolException, IOException {
		keyword = URLEncoder.encode(keyword, "UTF-8");
		final HttpGet request = new HttpGet(
				"http://www.ebay.com/sch/i.html?_nkw=" + keyword);
		request.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
		request.setHeader("Accept", "text/html");
		CloseableHttpResponse httpResponse = httpClient.execute(request);
		HttpEntity entity = httpResponse.getEntity();
		String content = EntityUtils.toString(entity);

		Document doc = Jsoup.parse(content);
		
		ArrayList<EbayItemResult> itemList = new ArrayList<EbayItemResult>();
		Element ul = doc.getElementById("ListViewInner");
		if (ul != null) {
			Elements lis = ul.select("li[id]").select("[_sp]").select("[class]").select("[listingid]").select("[r]");
			for (Element e : lis) {
				EbayItemResult item = new EbayItemResult();
				Elements lvtitle = e.select("h3[class=\"lvtitle\"]").select("a");
				if (!lvtitle.isEmpty()) {
					item.title = lvtitle.text();
					item.itemUrl = lvtitle.attr("href");
				}
				Elements img = e.select("img[src]");
				//Elements img = e.select("div[class=\"lvpicinner full-width picW\"]").select("img[src]").select("[class=\"img\"]").select("[alt]");
				if (!img.isEmpty()) {
					// sometimes the image url is in src attribute and sometimes is in imgurl
					String imgurl0 = img.attr("src");
					String imgurl1 = img.attr("imgurl");
					if (!imgurl0.isEmpty() && !imgurl0.endsWith(".gif"))
						item.imgUrl = imgurl0;
					if (!imgurl1.isEmpty() && !imgurl1.endsWith(".gif"))
						item.imgUrl = imgurl1;
				}
				Elements price = e.select("li[class=\"lvprice prc\"]");
				if (!price.isEmpty()) {
					item.price = price.text();
				}
				itemList.add(item);
			}
		}
		return itemList;
	}
}
