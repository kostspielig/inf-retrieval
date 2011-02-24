package ir.assignment05.index;

import java.util.List;

/**
 * CS 121 Information Retrieval <br />
 * Assignment 05 <br />
 * Part 3 <br /><br />
 * 
 * Represents an entry of the index.
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 */
public class IndexEntry {

	private List<Posting> postings;
	private String term;

	public IndexEntry(String term, List<Posting> postings) {
		this.term = term;
		this.postings = postings;
	}

	/**
	 * @return the postings
	 */
	public List<Posting> getPostings() {
		return postings;
	}

	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}
	
}
