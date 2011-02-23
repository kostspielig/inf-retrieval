package ir.assignment05.search;

import java.util.LinkedList;
import java.util.List;

public class Query {
	
	private List<String> terms;
	
	public Query() {
		this.terms = new LinkedList<String>();
	}
	
	public void addTerm(String term) {
		terms.add(term);
	}
	
}
