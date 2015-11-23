package com.assignments.assignment1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WebCrawlerMain {
	public static ArrayList<String> urlArray = new ArrayList<String>();
	public static ArrayList<Integer> depthArray = new ArrayList<Integer>();
	public static HashSet<String> urlSet = new HashSet<String>();
	public static HashSet<String> totalUrlSet = new HashSet<String>();
	public static String keyphrase = null;
	public static Document anchorDoc;
	
	
	
	public static void addToArrays(String completeUrl, int depth){
		urlArray.add(completeUrl);
		depthArray.add(depth);
		urlSet.add(completeUrl);
		System.out.println(urlArray.size()+" --- "+depth+" --- "+completeUrl);
	}
	
	
	
	public static void logger(ArrayList<String> urlArray) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer;
		
		if (keyphrase == null)
			writer = new PrintWriter("unfocused.txt", "UTF-8");
		else {
			writer = new PrintWriter("concordance.txt", "UTF-8");
			writer.println("Total Urls crawled are "+totalUrlSet.size()+". Out of which "+urlSet.size()+" are Relevant, which reached a depth of "+depthArray.get(depthArray.size()-1));
		}
		
		for(int i = 0; i<urlArray.size();i++){
			writer.println(urlArray.get(i));
		}
		writer.close();
	}
	
	
	
	public static String getFinalUrl(String href) throws IOException, InterruptedException{
		String completeUrl = "https://en.wikipedia.org"+href;
		Thread.sleep(1000);
		anchorDoc = Jsoup.connect(completeUrl).get();
		completeUrl = anchorDoc.head().select("link[rel=canonical]").attr("href");
		completeUrl = completeUrl.split("#")[0];
		return completeUrl;
	}
	
	
	
	public static void parser(String parseUrl, int depth){
		depth++;
		String completeUrl=null;
		try {
			Thread.sleep(1000);
			Document doc = Jsoup.connect(parseUrl).get();
			Elements anchors = doc.select("a");
			for(Element anchor : anchors){
				String anchorHref = anchor.attr("href");
				
				if(anchorHref.indexOf(':') == -1 && anchorHref.indexOf("/wiki/") == 0){
					completeUrl = getFinalUrl(anchorHref);
					if(urlSet.contains(completeUrl))
						continue;
					if(keyphrase != null){
						totalUrlSet.add(completeUrl);
						Thread.sleep(1000);
						anchorDoc = Jsoup.connect(completeUrl).get();
						String fullText = anchorDoc.body().text().toLowerCase();
						if(fullText.indexOf(keyphrase) < 0){
							continue;
						}
					}
					addToArrays(completeUrl, depth);
				}
				if(urlArray.size()>=1000)
					break;
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		keyphrase = "concordance";
		
		addToArrays("https://en.wikipedia.org/wiki/Hugh_of_Saint-Cher", 1);
		totalUrlSet.add("https://en.wikipedia.org/wiki/Hugh_of_Saint-Cher");
		
		for(int i=0;i<urlArray.size();i++){
			if(urlArray.size()>=1000 || depthArray.get(i) >= 5)
				break;
			parser(urlArray.get(i), depthArray.get(i));
		}
		
		logger(urlArray);
		System.out.println("***The End***");
	}
}
