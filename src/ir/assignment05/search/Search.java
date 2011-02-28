package ir.assignment05.search;

import java.util.PriorityQueue;

import com.sleepycat.je.rep.impl.node.Feeder.ExitException;

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
	
	public static PriorityQueue<SearchResult> search(Query q, RankingMethod method) {
		return method.search(q);
	}
	
	
	public static void main(String[] args) {
		String filePath = args[0];
		int corpusSize = 0;
		try {
			corpusSize = Integer.valueOf(args[1]);
		} catch (NumberFormatException e) {
			System.out.println("Please specify the corpus size.");
			System.exit(1);
		}
		RankingMethod m1 = new TfIdfRanker(filePath, corpusSize);
		RankingMethod m2 = new CosineSimRanker(filePath, corpusSize); // TODO: order is inversed
		RankingMethod m3 = new ProximityRanker(filePath, corpusSize);
		QueryBuilder qb = new QueryBuilder ();
		Query q = qb.construct("summer night");
		
		System.out.println("tfidf");
		PriorityQueue<SearchResult> r1 = Search.search(q, m1);
		SearchResult res;
		while ((res = r1.poll()) != null)
			System.out.println(res.toString());
		
		System.out.println("#######################");
		
		System.out.println("cosine");
		PriorityQueue<SearchResult> r2 = Search.search(q, m2);
		while ((res = r2.poll()) != null)
			System.out.println(res.toString());
		
		System.out.println("#######################");
		
		System.out.println("proximity");
		PriorityQueue<SearchResult> r3 = Search.search(q, m3);
		while ((res = r3.poll()) != null)
			System.out.println(res.toString());
	}
}


