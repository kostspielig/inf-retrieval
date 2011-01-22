package ir.assignment03;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * CS 121 Information Retrieval
 * Assignment 03
 * 
 * The {@link WikiProcessor} processes web pages crawled by {@link WikiCrawler}.
 * 
 * @author Mar�a Carrasco
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
	private static final String TOKEN_DELIMITER = " \t\n\r\f\".,:;!?'~@#*+�$%&()=`�{[]}|<>";
	private static final String DEFAULT_STOPWORDS_FILE = "stopwords.txt";
	private static final String FILE_ENCODING = "UTF-8";
	
	/**
	 * The full path leading to articles (combination of host and relative path)
	 */
	private String articlePath;
	/**
	 * The Porter Stemmer used to stem tokens
	 */
	private Stemmer stemmer;
	private Set<String> stopwords;

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
		this.stopwords = loadStopwords();
	}

	/**
	 * Loads and stems user-defined stopwords.
	 * 
	 * @return stemmed stopwords
	 */
	private Set<String> loadStopwords() {
		try {
			Set<String> stopwords = new TreeSet<String>();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(DEFAULT_STOPWORDS_FILE), FILE_ENCODING));
			String line;
			while ((line = br.readLine()) != null) {
				char[] word = line.toCharArray();
				this.stemmer.add(word, word.length);
				this.stemmer.stem();
				stopwords.add(stemmer.toString());
			}
			return stopwords;
		} catch (Exception e) { // problems reading file, return empty set instead
			e.printStackTrace();
			return new TreeSet<String>();
		}		
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
				if (!stopwords.contains(this.stemmer.toString())) { // ignore stop words
					System.out.println(String.valueOf(token) + " - " + this.stemmer.toString());	
				}
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
