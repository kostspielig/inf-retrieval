package ir.assignment04;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * CS 121 Information Retrieval <br />
 * Assignment 04 <br />
 * Part 3 <br /><br />
 * 
 * Constructs and inverted index.
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 */
public class IndexConstructor {
	
	/**
	 * The maximum capacity of the index in memory.
	 */
	private static final int MAX_CAPACITY = 10000;
	/**
	 * Iterates recursively over all documents in the root folder and its subfolders.
	 */
	private DocumentIterator docIterator;
	/**
	 * Tokenizes a passed document text.
	 */
	private Parser parser;
	
	/**
	 * the inverted index.
	 */
	private HashMap<String, List<Posting>> index;
	
	private static final String DEFAULT_STOPWORDS_FILE = "stopwords.txt";
	private static final String FILE_ENCODING = "UTF-8";
	
	private static final String SPECIAL_CHARS = "[!\"§$%&/()=?`´{}\\[\\]\\^°*+~'#-_.:,;<>|]+";

	
	/**
	 * The Porter Stemmer used to stem tokens
	 */
	private Stemmer stemmer;
	private Set<String> stopwords;

	
	/**
	 * Initializes a new object that is responsible for constructing the inverted index. 
	 * 
	 * @param root the path to the root directory
	 */
	public IndexConstructor(String root) {
		this.docIterator = new DocumentIterator(root);
		this.parser = new Parser();
		this.index = new HashMap<String, List<Posting>>(MAX_CAPACITY+1,1.0f);
		this.stemmer = new Stemmer();
		this.stopwords = loadStopwords();
		// TODO: initialize stop words (compare Assignment 03)
	}
	
	/**
	 * Constructs the inverted index.
	 */
	public void construct() {
		while (this.docIterator.hasNext()) {
			File nextDoc = this.docIterator.next();
			Email e = new Email(nextDoc);
			Vector<String> tokens = this.parser.parse(e.getBody());
			for(int i = 0; i < tokens.size(); i++) {
				indexToken(tokens.get(i), i, extractDocName(nextDoc));
			}
		}
		
	}

	/**
	 * Extracts the document name from the path of the passed file.
	 * 
	 * @param nextDoc
	 * @return the document name
	 */
	private String extractDocName(File nextDoc) {
		// TODO extract doc name (the path beginning in the root directory)
		return nextDoc.getName();
	}

	/**
	 * Stores the token in the index.
	 * 
	 * @param token the token to store
	 * @param pos the position of the token in the document
	 * @param docName the name of the document the token occurs in
	 */
	private void indexToken(String rawToken, int pos, String docName) {
		// TODO Stem
		// TODO: stopwords (compare assignment 03)
		// TODO: use docIDs instead of name (if compression flag is set)
		String longToken = rawToken.toLowerCase();
		if (isValidToken(longToken)) {
			char[] tokenChrAr = longToken.toCharArray();
			this.stemmer.add(tokenChrAr,tokenChrAr.length);
			this.stemmer.stem();
			String token = this.stemmer.toString();
			List<Posting> postings = this.index.get(token);

			if (postings == null){ // token not yet included in the index 
				flushIndexToDiskIfNecessary();
				Posting p = new Posting(docName, pos);
				postings = new ArrayList<Posting>();
				postings.add(p);
				this.index.put(token, postings);
			} else { // token is already included, update posting or create new posting
				int i=0;
				for (Posting p : postings) {
					int comparison = p.getName().compareTo(docName);
					if (comparison == 0) { // posting for the document exists already --> update
						p.addPostion(pos);
						return;
					} else if (comparison > 0) { // posting for the doc does not exist --> insert inbetween
						Posting newP = new Posting(docName, pos);
						postings.add(i, newP);
						return;
					}
					i++;
				}
				Posting newP = new Posting(docName, pos); // posting for the doc does not exist & all other docNames are smaller --> insert at the end
				postings.add(i, newP);
			}
		}
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
	 * Checks whether the token is longer than one character and consists not only of special characters.
	 * Makes sure the token isn't a stopword
	 * 
	 * @param token
	 * @return true if it is longer than one char and does NOT consist only of special characters
	 */
	private boolean isValidToken(String token) {
		return token.length() > 1 && !stopwords.contains(token) && !token.matches(SPECIAL_CHARS);
	}
	

	/**
	 * Writes the intermediate index to disk if capacity is reached.
	 */
	private void flushIndexToDiskIfNecessary() {
		if (this.index.size() >= MAX_CAPACITY) {
			// TODO: sort hash maps based on keys
			// TODO: write out sorted hash map to disk (in the output directory passed to the constructor)
			// TODO: make format dependent on whether compression flag is set or not
			// this.index.clear(); // TODO: empty hashmap
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO: provide output directory as an argument
		// TODO: provide flag for compression as an argument (e.g., 0 if compression shall be disabled, != 0 otherwise)
		if (args.length < 1) {
			System.out.println("Please provide the path to the root folder.");
			System.exit(1);
		}
		
		IndexConstructor constructor = new IndexConstructor(args[0]); // TODO: pass output dir and pass boolean flag for compression
		constructor.construct();
		
	}

}
