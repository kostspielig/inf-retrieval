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
	
	public static Iterable<SearchResult> search(Query q, RankingMethod method) {
		return method.search(q);
	}
	
	
	public static void main(String[] args) {
		RankingMethod m = new TfIdfRanker(args[0]);
		QueryBuilder qb = new QueryBuilder ();
		
		Iterable<SearchResult> r = Search.search(qb.construct("maria"), m);
		
		for (SearchResult res : r)
			System.out.println(res.toString());
	}
}


