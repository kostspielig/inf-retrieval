package ir.assignment05.search;

import ir.assignment05.index.IndexConstructor;
import ir.assignment05.utils.Parser;
import ir.assignment05.utils.Stemmer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * CS 121 Information Retrieval <br />
 * Assignment 05 <br />
 * Part 3 <br /><br />
 * 
 * Processes the passed query string and constructs a query object.
 * Must do the same processing steps as the {@link IndexConstructor}.
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 */
public class QueryBuilder {
	
	private static final String DEFAULT_STOPWORDS_FILE = "stopwords.txt";
	private static final String FILE_ENCODING = "UTF-8";
	private static final String SPECIAL_CHARS = "[!\"§$%&/()=?`´{}\\[\\]\\^°*+~'#-_.:,;<>|]+";
	
	/**
	 * Tokenizes a passed query string.
	 */
	private Parser parser;
	/**
	 * The Porter Stemmer used to stem tokens
	 */
	private Stemmer stemmer;
	/**
	 * The user-defined list of stopwords after stemming
	 */
	private Set<String> stopwords;

	public QueryBuilder() {
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
				stopwords.add(stem(line));
			}
			return stopwords;
		} catch (Exception e) { // problems reading file, return empty set instead
			e.printStackTrace();
			return new TreeSet<String>();
		}		
	}
	
	/**
	 * Converts passed word to lowercase representation and stems it.
	 * 
	 * @param word
	 * @return the stemmed word
	 */
	private String stem(String word) {
		char[] wordChrAr = word.toLowerCase().toCharArray();
		this.stemmer.add(wordChrAr,wordChrAr.length);
		this.stemmer.stem();
		return this.stemmer.toString();
	}
	
	public Query construct(String query) {
		Query q = new Query();
		
		Vector<String> tokens = this.parser.parse(query);
		for(String t : tokens) {
			String term = stem(t);
			if (isValidToken(term)) {
				q.addTerm(term);
			}
		}
		return q;
	}
	
	/**
	 * Checks whether the token is longer than one character and consists not only of special characters.
	 * Makes sure the token isn't a stopword
	 * 
	 * @param token
	 * @return true if it is longer than one char and does NOT consist only of special characters
	 */
	private boolean isValidToken(String token) {
		return token.length() > 1 && !stopwords.contains(token) && !token.matches(SPECIAL_CHARS);
	}
	
}
