package ir.assignment05.search;

import java.util.LinkedList;
import java.util.List;

/**
 * CS 121 Information Retrieval <br />
 * Assignment 05 <br />
 * Part 3 <br /><br />
 * 
 * Result for a search query
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 *
 */
public class SearchResult {
	
	private int docID;
	private List<Hit> hits;
	private Double score;
	
	public SearchResult (int id) {
		this.docID = id;
		this.hits = new LinkedList<Hit>();
		this.score = null;
	}
	
	public void addHit(Hit h) {
		this.hits.add(h);
	}

	/**
	 * @return the score
	 */
	public Double getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(Double score) {
		this.score = score;
	}

	/**
	 * @return the docID
	 */
	public int getDocID() {
		return docID;
	}

	/**
	 * @return the hits
	 */
	public List<Hit> getHits() {
		return hits;
	}

}
