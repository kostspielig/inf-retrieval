package ir.assignment03;

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

	private static final int LIMIT_FOR_CRAWLED_PAGES = 500;
	private static final int REPORT_STEPS = 50;

	private static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
			+ "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf"
			+ "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	private static AtomicInteger NUMBER_CRAWLED_PAGES = new AtomicInteger();
	
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
		if (NUMBER_CRAWLED_PAGES.get() >= LIMIT_FOR_CRAWLED_PAGES){
			this.proc.report();
			System.exit(0);
		}
		
		String href = url.getURL().toLowerCase();
		if (href.startsWith(this.host)) {
			return true;
		}
		if (FILTERS.matcher(href).matches()) {
			return false;
		}
		return this.proc.shouldVisit(href);
	}
	
	// This function is called by controller before finishing the job.
	public void onBeforeExit() {
		// do nothing
	}

	public void visit(Page page) {
//		int docid = page.getWebURL().getDocid();
        String url = page.getWebURL().getURL();         
        String text = page.getText();
//        ArrayList<WebURL> links = page.getURLs();
//		int parentDocid = page.getWebURL().getParentDocid();
		
//		System.out.println("Docid: " + docid);
//		System.out.println("URL: " + url);
//		System.out.println("Text length: " + text.length());
//		System.out.println("Number of links: " + links.size());
//		System.out.println("Docid of parent page: " + parentDocid);
//		System.out.println("=============");
		
		this.proc.process(url, text);
		NUMBER_CRAWLED_PAGES.incrementAndGet();
		
		if (NUMBER_CRAWLED_PAGES.get() % REPORT_STEPS == 0) {
			System.out.println("Pages crawled so far: " + NUMBER_CRAWLED_PAGES);
		}
		
	}	
	
}

