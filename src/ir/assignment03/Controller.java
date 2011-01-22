package ir.assignment03;

import edu.uci.ics.crawler4j.crawler.CrawlController;

/**
 * Copyright (C) 2010.
 * 
 * @author Yasser Ganjisaffar <yganjisa at uci dot edu>
 */
public class Controller {

		public static void main(String[] args) throws Exception {
			if (args.length < 2) {
				System.out.println("Please specify 'root folder' and 'number of crawlers'.");
				return;
			}
			String rootFolder = args[0];
			int numberOfCrawlers = Integer.parseInt(args[1]);
			
			CrawlController controller = new CrawlController(rootFolder);		
			controller.addSeed("http://en.wikipedia.org/wiki/Category:Bing");
			controller.addSeed("http://en.wikipedia.org/wiki/Category:Google");
			
			// Be polite:
			controller.setPolitenessDelay(1000);
			
			// Do you need to set a proxy?
			// If so, you can uncomment the following line
			// controller.setProxy("proxyserver.example.com", 8080);
			// OR
			// controller.setProxy("proxyserver.example.com", 8080, username, password);
			
			controller.start(WikiCrawler.class, numberOfCrawlers);
		}

}


