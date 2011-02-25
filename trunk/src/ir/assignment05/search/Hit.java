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
	private double tfIdf;
	private List<Integer> positions;
	
	public Hit(double tfIdf, String term, List<Integer> pos) {
		this.term = term;
		this.positions = pos;
		this.tfIdf = tfIdf;
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

	/**
	 * @return the tfIdf
	 */
	public double getTfIdf() {
		return tfIdf;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Hit [term=" + term + ", tfIdf=" + tfIdf + ", positions="
				+ positions + "]";
	}
	
}
