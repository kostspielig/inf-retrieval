package ir.assignment03;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;


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
	private static final String TOKEN_DELIMITER = " \t\n\r\f\".,:;/!?'~@#*+§$%&()=`´{[]}|<>";
	private static final String SPECIAL_CHARS = "[!\"§$%&/()=?`´{}\\[\\]\\^°*+~'#-_.:,;<>|]+";
	private static final String DEFAULT_STOPWORDS_FILE = "stopwords.txt";
	private static final String FILE_ENCODING = "UTF-8";
	private static final int MAX_SIZE = 10; // TODO: FABS DO NOT FORGET TO CHANGE THISSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSsss
	private static final String FILE_PATH = "temp" + File.separator;
	private static final CharSequence[] REQUIRED_CHARS = {"c","l"};


	private int crawlerId;
	private int fileNumber;
	/**
	 * The full path leading to articles (combination of host and relative path)
	 */
	private String articlePath;
	/**
	 * The Porter Stemmer used to stem tokens
	 */
	private Stemmer stemmer;
	private Set<String> stopwords;

	private HashMap<String, Integer> frequencies = new HashMap<String, Integer>(MAX_SIZE,1);
	/**
	 * Initiates an instance of {@link WikiProcessor}.
	 * 
	 * @param hostname the name of host 
	 * @param crawlerId 
	 */
	public WikiProcessor(String hostname, int crawlerId) {
		this.crawlerId = crawlerId;
		this.fileNumber = 0;
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

	public boolean shouldVisit(String url) {
		String title = extractTitle(url);
		return (title != null && isContentPage(title) && isValidTitle(title));
	}
	
	/**
	 * Process the passed <code>content</code> of the web page with the URL <code>url</code>.
	 * 
	 * @param url the URL of the web page
	 * @param content the content of the web page
	 */
	public void process(String url, String content) {
		StringTokenizer tokenizer = new StringTokenizer(content,TOKEN_DELIMITER); // tokenize content
		while (tokenizer.hasMoreTokens()) { // stem tokens
			flushIfNecessary (); // write hash map to file, if necessary
			String token = tokenizer.nextToken().toLowerCase();
			if (isValidToken(token)) {
				char[] tokenChrAr = token.toCharArray();
				this.stemmer.add(tokenChrAr,tokenChrAr.length);
				this.stemmer.stem();
				String stem = this.stemmer.toString();
				if (!stopwords.contains(stem)) { // ignore stop words
					Integer freq = this.frequencies.get(stem);
					if (freq != null)
						this.frequencies.put(stem, ++freq);
					else
						this.frequencies.put(stem, 1);
				}
			}
		}
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
	 * Checks whether the title contains at least one of the required character sequences.
	 * 
	 * @param title
	 * @return true if the title contains at least one of REQUIRED_CHARS; false otherwise
	 */
	private boolean isValidTitle(String title) {
		title = title.toLowerCase();
		for (CharSequence c : REQUIRED_CHARS) {
			if (title.contains(c)) {
				return true;
			}
		}
		return false;
	}

	private void flushIfNecessary() {
		if(this.frequencies.size() >= MAX_SIZE){
			writeToFile();
			this.frequencies.clear();
		}
	}

	/**
	 * Checks whether the token is longer than one character and consists not only of special characters.
	 * 
	 * @param token
	 * @return true if it is longer than one char and does NOT consist only of special characters
	 */
	private boolean isValidToken(String token) {
		return token.length() > 1 && !token.matches(SPECIAL_CHARS);
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
	 * Print a final report of all processed pages.
	 */
	public void report() {
		writeToFile();
		this.frequencies.clear();
	}

	/**
	 * Writes content of hash map to a new file.
	 */
	private void writeToFile() 
	{
		try {
			File output = new File(FILE_PATH +"crawler" + crawlerId + "_" + fileNumber + ".txt");
			File parentDir = output.getParentFile();
			if(! parentDir.exists()) 
				parentDir.mkdirs();
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), FILE_ENCODING));
			for(Map.Entry<String, Integer> pair : this.frequencies.entrySet()) {
				w.write(pair.getKey()+"\t"+ pair.getValue());
				w.newLine();
			}
			w.close();
			this.fileNumber ++;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public String sortFrequencies(HashMap<String, Integer> f)
	{
		StringBuilder builder = new StringBuilder();


		return builder.toString();
	}
}
