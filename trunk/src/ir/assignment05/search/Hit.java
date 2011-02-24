package ir.assignment05.search;

import java.util.List;

/**
 * CS 121 Information Retrieval <br />
 * Assignment 05 <br />
 * Part 3 <br /><br />
 * 
 * 
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 *
 */
public class Hit {
	private String term;
	private List<Integer> positions;
	
	public Hit(String term, List<Integer> pos) {
		this.term = term;
		this.positions = pos;
	}

	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}

	/**
	 * @return the positions
	 */
	public List<Integer> getPositions() {
		return positions;
	}
	
	
}
