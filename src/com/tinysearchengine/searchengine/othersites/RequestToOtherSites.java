package com.tinysearchengine.searchengine.othersites;

import java.io.IOException;
import java.io.PrintWriter;
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
	static public ArrayList<AmazonItemResult> getAmazonResult(String[] keywords)
			throws ClientProtocolException, IOException {
		return getAmazonResult(Arrays.toString(keywords));
	}

	static public ArrayList<AmazonItemResult> getAmazonResult(String keyword)
			throws ClientProtocolException, IOException {
		final HttpGet request = new HttpGet(
				"https://www.amazon.com/s/ref=nb_sb_ss_c_1_3?url=search-alias%3Daps&field-keywords=" + keyword);
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = httpClient.execute(request);
		HttpEntity entity = httpResponse.getEntity();
		String content = EntityUtils.toString(entity);

		Document doc = Jsoup.parse(content);

		PrintWriter writer = new PrintWriter("response.html", "UTF-8");
		writer.println(content);
		writer.close();

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
}
