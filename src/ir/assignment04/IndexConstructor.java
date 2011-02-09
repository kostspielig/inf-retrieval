package ir.assignment04;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
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
	/**
	 * The user-defined list of stopwords after stemming
	 */
	private Set<String> stopwords;
	/**
	 * The output path of the final result.
	 */
	private String out;
	/**
	 * The directory in which intermediate results are stored.
	 */
	private String intermediateOut;
	/**
	 * true if output files shall be compressed
	 */
	private boolean enableCompression;
	private int fileNr = 0;
	/**
	 * incrementing number of documents
	 */
	private int docNr = 0;
	
	/**
	 * Initializes a new object that is responsible for constructing the inverted index. 
	 * 
	 * @param root the path to the root directory
	 * @param enableCompression 
	 * @param outDir 
	 */
	public IndexConstructor(String root, String outDir, boolean enableCompression) {
		this.docIterator = new DocumentIterator(root);
		this.parser = new Parser();
		this.index = new HashMap<String, List<Posting>>(MAX_CAPACITY+1,1.0f);
		this.stemmer = new Stemmer();
		this.stopwords = loadStopwords();
		this.out = outDir;
		this.intermediateOut = this.out + File.separator + "intermediate" + File.separator;
		this.enableCompression = enableCompression;
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
				indexToken(this.docNr, extractDocName(nextDoc), tokens.get(i), i);
			}
			this.docNr++;
		}
		writeToDisk();
		mergeIntermediateResults();
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
	 * @param docID the ID of the document the token occurs in
	 * @param docName the name of the document the token occurs in
	 * @param token the token to store
	 * @param pos the position of the token in the document
	 */
	private void indexToken(int docID, String docName, String token, int pos) {
		// TODO: use docIDs instead of name (if compression flag is set)
		String stemmed = stem(token);
		if (isValidToken(stemmed)) {
			List<Posting> postings = this.index.get(stemmed);
			if (postings == null){ // token not yet included in the index 
				flushIndexToDiskIfNecessary();
				Posting p = new Posting(docID, docName, pos);
				postings = new ArrayList<Posting>();
				postings.add(p);
				this.index.put(stemmed, postings);
			} else { // token is already included, update posting or create new posting
				int i=0;
				for (Posting p : postings) {
					int comparison;
					if (this.enableCompression) {
						comparison = p.getID().compareTo(docID);
					} else {
						comparison = p.getName().compareTo(docName);
					}
					if (comparison == 0) { // posting for the document exists already --> update
						p.addPostion(pos);
						return;
					} else if (comparison > 0) { // posting for the doc does not exist --> insert inbetween
						Posting newP = new Posting(docID, docName, pos);
						postings.add(i, newP);
						return;
					}
					i++;
				}
				Posting newP = new Posting(docID, docName, pos); // posting for the doc does not exist & all other docNames are smaller --> insert at the end
				postings.add(i, newP);
			}
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
			writeToDisk();
		}
	}

	/**
	 * Writes the intermediate index to disk.
	 */
	private void writeToDisk() {
		// sort hash maps based on keys
		SortedMap<String, List<Posting>> sorted = new TreeMap<String, List<Posting>>(this.index);
		
		File output = new File(this.intermediateOut + "intermediate_" + this.fileNr++);
		File parentDir = output.getParentFile();
		if(! parentDir.exists()) {
			parentDir.mkdirs();
		}
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), FILE_ENCODING));
			
			for (Map.Entry<String, List<Posting>> entry : sorted.entrySet()) {
				bw.write(entry.getKey() + "\t" + Posting.encodePostings(entry.getValue(), this.enableCompression));
				bw.newLine();
			}
			
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.index.clear();
	}

	/**
	 * Reads the intermediate results from disk and merges them into a new output file.
	 */
	private void mergeIntermediateResults() {
		// TODO implement merging		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Please provide the path to the root folder, the path to the output directory, and a flag indicating whether compression shall be enabled.");
			System.exit(1);
		}
		
		String rootDir = args[0];
		String outDir = args[1];
		boolean enableCompression;
		try {
			enableCompression = Integer.valueOf(args[2]) == 1;
		} catch (NumberFormatException e) {
			enableCompression = false;
		}
		
		IndexConstructor constructor = new IndexConstructor(rootDir, outDir, enableCompression);
		constructor.construct();
		
	}

}
