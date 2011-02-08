package ir.assignment04;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class IndexConstructor {
	
	private static final int MAX_CAPACITY = 10000;
	private DocumentIterator docIterator;
	private Parser parser;
	
	private HashMap<String, List<Posting>> index;
	
	
	public IndexConstructor(String root) {
		this.docIterator = new DocumentIterator(root);
		this.parser = new Parser();
		this.index = new HashMap<String, List<Posting>>(MAX_CAPACITY+1,1.0f);
	}
	
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

	private String extractDocName(File nextDoc) {
		// TODO extract doc name
		return nextDoc.getName();
	}

	private void indexToken(String token, int pos, String docName) {
		// TODO: toLowercase
		// TODO Stem
		// TODO: stopwords
		// TODO: use docIDs instead of name (opt-in)
		// TODO: check capacity, write out to disk if full
		List<Posting> postings = this.index.get(token);
		if (postings == null){
			Posting p = new Posting(docName, pos);
			postings = new ArrayList<Posting>();
			postings.add(p);
			this.index.put(token, postings);
		} else {
			int i=0;
			for (Posting p : postings) {
				int comparison = p.getName().compareTo(docName);
				if (comparison == 0) {
					p.addPostion(pos);
					return;
				} else if (comparison > 0) {
					Posting newP = new Posting(docName, pos);
					postings.add(i, newP);
					return;
				}
				i++;
			}
			Posting newP = new Posting(docName, pos);
			postings.add(i, newP);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Please provide the path to the root folder.");
			System.exit(1);
		}
		
		IndexConstructor constructor = new IndexConstructor(args[0]);
		constructor.construct();
		
	}

}
