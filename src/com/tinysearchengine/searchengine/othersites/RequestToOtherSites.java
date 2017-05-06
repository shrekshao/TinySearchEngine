package com.tinysearchengine.searchengine.othersites;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
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

				Elements itemImgUrlElement = aElements.select("img[src]");
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
		// pay attention to this Safari User-Agent header
		request.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_3) AppleWebKit/537.75.14 (KHTML, like Gecko) Version/7.0.3 Safari/7046A194A");
		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		CloseableHttpResponse httpResponse = httpClient.execute(request);
		HttpEntity entity = httpResponse.getEntity();
		String content = EntityUtils.toString(entity);
//		PrintWriter writer = new PrintWriter("ebay.html", "UTF-8");
//		writer.println(content);
//		writer.close();
		//System.out.println(content);
		content = FileUtils.readFileToString(new File("miaoTestHtml/ebay-non.html"), "utf-8");

		Document doc = Jsoup.parse(content);
		
		ArrayList<EbayItemResult> itemList = new ArrayList<EbayItemResult>();
		Element ul = doc.getElementById("ListViewInner");
		if (ul != null) {
			//System.out.println(ul.toString());
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
				Elements price = e.select("li[class=\"lvprice prc\"]").select("span[class=\"bold\"]");
				if (!price.isEmpty()) {
					item.price = price.text();
				}
				itemList.add(item);
			}
			//System.out.println("Branch0");
		} else {
			Elements lis = doc.select("li[class=\"s-item\"]");
//			boolean f = false;
			for (Element e : lis) {
				EbayItemResult item = new EbayItemResult();
				Elements img = e.select("img");
				//System.out.println(img.toString());
				if (!img.isEmpty()) {
					String imgurl0 = img.attr("src");
					String imgurl1 = img.attr("data-src");
					if (!imgurl0.isEmpty() && !imgurl0.endsWith(".gif"))
						item.imgUrl = imgurl0;
					if (!imgurl1.isEmpty() && !imgurl1.endsWith(".gif"))
						item.imgUrl = imgurl1;
//					System.out.println(item.imgUrl);
				}
				Elements title = e.select("h3[class=\"s-item__title\"]");
				//System.out.println(title);
				if (!title.isEmpty()) {
					item.title = title.text();
				}
				Elements price = e.select("span[class=\"s-item__price\"]");
				if (!price.isEmpty()) {
					item.price = price.text();
				}
				Elements itemUrl = e.select("a[class=\"s-item__link s-item__link--visited-color\"]");
//				if (!f) {
//				System.out.println(e.toString());
//				f= true;
//				}
				if (!itemUrl.isEmpty()) {
					item.itemUrl = itemUrl.attr("href");
				}
				itemList.add(item);
			}
//			System.out.println("Branch1");
//			PrintWriter writere = new PrintWriter("ebay-non.html", "UTF-8");
//			writere.println(content);
//			writere.close();
		}
		return itemList;
	}
	
	static public ArrayList<YoutubeItemResult> getYoutubeResult(String[] keywords) throws ClientProtocolException, IOException {
		return getYoutubeResult(Arrays.toString(keywords));
	}
	
	static public ArrayList<YoutubeItemResult> getYoutubeResult(String keyword) throws ClientProtocolException, IOException {
		ArrayList<YoutubeItemResult> itemList = new ArrayList<YoutubeItemResult>();
		
		keyword = URLEncoder.encode(keyword, "UTF-8");
		final HttpGet request = new HttpGet(
				"https://www.youtube.com/results?search_query=" + keyword);
		request.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.81 Safari/537.36");
		request.setHeader("Accept", "text/html");
		CloseableHttpResponse httpResponse = httpClient.execute(request);
		HttpEntity entity = httpResponse.getEntity();
		String content = EntityUtils.toString(entity);
//		PrintWriter writere = new PrintWriter("miaoTestHtml/youtube.html", "UTF-8");
//		writere.println(content);
//		writere.close();
		Document doc = Jsoup.parse(content);
//		System.out.println(content);
		Elements lis = doc.select("ol[class=\"item-section\"]");
//		System.out.println(lis.toString());
		lis = lis.select("h3[class=\"yt-lockup-title\"]");
		
		for (Element li : lis) {
			YoutubeItemResult item = new YoutubeItemResult();
			Elements a = li.select("a[title]");
			if (!a.isEmpty()) {
				String url = a.attr("href");
				if (url != null) {
					item.url = "https://www.youtube.com" + url;
					item.embedUrl = "https://www.youtube.com/embed/" + url.replace("/watch?v=", "");
				}
				String title = a.attr("title");
				if (title != null) {
					item.title = title;
				}
			}
			itemList.add(item);
//			System.out.println(item.url + " : " + item.title);
		}
		return itemList;
	}
}
