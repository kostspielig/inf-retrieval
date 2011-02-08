package ir.assignment04;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	
	
	/**
	 * Initializes a new object that is responsible for constructing the inverted index. 
	 * 
	 * @param root the path to the root directory
	 */
	public IndexConstructor(String root) {
		this.docIterator = new DocumentIterator(root);
		this.parser = new Parser();
		this.index = new HashMap<String, List<Posting>>(MAX_CAPACITY+1,1.0f);
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
	private void indexToken(String token, int pos, String docName) {
		// TODO: toLowercase
		// TODO Stem
		// TODO: stopwords (compare assignment 03)
		// TODO: use docIDs instead of name (if compression flag is set)
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
