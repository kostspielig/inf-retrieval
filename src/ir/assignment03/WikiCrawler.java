package ir.assignment03;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * Copyright (C) 2010.
 * 
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */
public class WikiCrawler extends WebCrawler {

	private static final String DEFAULT_HOST = "http://en.wikipedia.org/";

	private static Pattern filters = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
			+ "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf"
			+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	
	private static AtomicInteger numberCrawledPages = new AtomicInteger();
	
	private String host;
	private WikiProcessor proc;


	public WikiCrawler() {
		this(DEFAULT_HOST);
	}
	
	public WikiCrawler(String hostname) {  
		this.host = hostname; 
		this.proc = new WikiProcessor(hostname, this.getMyId());
	}

	public boolean shouldVisit(WebURL url) {
		if (numberCrawledPages.get() >= 5){
			System.exit(0);
		}
		String href = url.getURL().toLowerCase();
		if (filters.matcher(href).matches()) {
			return false;
		}
		if (href.startsWith(this.host)) {
			return true;
		}
		return false;
	}
	
	// This function is called by controller before finishing the job.
	public void onBeforeExit() {
		this.proc.report();
	}

	public void visit(Page page) {
		int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();         
        String text = page.getText();
        ArrayList<WebURL> links = page.getURLs();
		int parentDocid = page.getWebURL().getParentDocid();
		
		System.out.println("Docid: " + docid);
		System.out.println("URL: " + url);
		System.out.println("Text length: " + text.length());
		System.out.println("Number of links: " + links.size());
		System.out.println("Docid of parent page: " + parentDocid);
		System.out.println("=============");
		
		if (this.proc.process(url, text))
			numberCrawledPages.incrementAndGet();
	}	
	
}

