package ir.assignment05.search;

/**
 * CS 121 Information Retrieval <br />
 * Assignment 05 <br />
 * Part 3 <br /><br />
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 */
public class Search {

	private static final String DEFAULT_PATH = "";
	private static final RankingMethod DEFAULT_METHOD = new TfIdfRanker(DEFAULT_PATH);

	public void search(Query q) {
		search(q, DEFAULT_METHOD);
	}
	
	public void search(Query q, RankingMethod method) {
		// TODO: implement this :-)
	}
}
