package ir.assignment03;

import java.util.StringTokenizer;

/**
 * CS 121 Information Retrieval
 * Assignment 03
 * 
 * The {@link WikiProcessor} processes web pages crawled by {@link WikiCrawler}.
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 */
public class WikiProcessor {

	/**
	 * The relative path of all Wikipedia articles 
	 */
	private static final String DEFAULT_PATH = "wiki/";
	/**
	 * Prefixes of all pages other than content articles
	 */
	private static final String DIFFERENT_TYPES = "(" +
													"(Category)" +
													"|(Category_talk)" +
													"|(File)" +
													"|(Image_talk)" +
													"|(Help)" +
													"|(Media)" +
													"|(MediaWiki)" +
													"|(MediaWiki_talk)" +
													"|(Portal)" +
													"|(Special)" +
													"|(Talk)" +
													"|(Template)" +
													"|(Template_talk)" +
													"|(User)" +
													"|(User_talk)" +
													"|(Wikipedia)" +
													"|(Wikipedia_talk)" +
													"):.*";
	/**
	 * Delimiters used for tokenizing the content
	 */
	private static final String TOKEN_DELIMITER = " \t\n\r\f\".,:;!?'~@#*+§$%&()=`´{[]}|<>";
	
	/**
	 * The full path leading to articles (combination of host and relative path)
	 */
	private String articlePath;
	/**
	 * The Porter Stemmer used to stem tokens
	 */
	private Stemmer stemmer;

	/**
	 * Initiates an instance of {@link WikiProcessor}.
	 * 
	 * @param hostname the name of host 
	 */
	public WikiProcessor(String hostname) {
		if (!hostname.endsWith("/")) {
			hostname += "/";
		}
		this.articlePath = hostname + DEFAULT_PATH;
		this.stemmer = new Stemmer();
	}

	/**
	 * Process the passed <code>content</code> of the web page with the URL <code>url</code>.
	 * 
	 * @param url the URL of the web page
	 * @param content the content of the web page
	 */
	public void process(String url, String content) {
		String title = extractTitle(url);
		if (title != null && isContentPage(title)) {
			StringTokenizer tokenizer = new StringTokenizer(content,TOKEN_DELIMITER); // tokenize content
			while (tokenizer.hasMoreTokens()) { // stem tokens
				char[] token = tokenizer.nextToken().toCharArray();
				this.stemmer.add(token,token.length);
				this.stemmer.stem();
//				System.out.println(String.valueOf(token) + " - " + this.stemmer.toString());
			}
		}
	}

	/**
	 * Extracts the title of the web page from the <code>url</code>
	 * 
	 * @param url the URL of the web page
	 * @return the title of the web page or <code>null</code> if no title could be extracted
	 */
	private String extractTitle(String url) {
		if (url.startsWith(articlePath)) {
			return url.substring(articlePath.length());
		}
		return null;
	}

	/**
	 * Determines whether the article is a content article based on the structure of its title.  
	 * 
	 * @param title the title of the article
	 * @return true if the article is a content article, false otherwise
	 */
	private boolean isContentPage(String title) {
		return !title.matches(DIFFERENT_TYPES);
	}

	/**
	 * Print a final report of all processed pages.
	 */
	public void report() {
		// TODO Auto-generated method stub
		System.out.println("final report");
	}

}
