package ir.assignment05.search;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Query {
	
	private List<String> terms;
	
	public Query() {
		this.terms = new LinkedList<String>();
	}
	
	/**
	 * Adds the passed term to the query. Duplicates will not be considered.
	 * 
	 * @param term
	 */
	public void addTerm(String term) {
		if (!terms.contains(term)) {
			terms.add(term);
		}
	}
	
	public boolean containsTerm (String term) {
		return terms.contains(term);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Query [terms=" + terms + "]";
	}
	
	/**
	 * @return an unmodifiable list of the query terms
	 */
	public List<String> getQueryTerms () {
		return Collections.unmodifiableList(terms);
	}
	
	public int size() {
		return terms.size();
	}
	
}
