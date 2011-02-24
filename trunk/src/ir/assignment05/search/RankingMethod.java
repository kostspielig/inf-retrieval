package ir.assignment05.search;

import ir.assignment05.index.IndexEntry;
import ir.assignment05.index.IndexIterator;
import ir.assignment05.index.Posting;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * CS 121 Information Retrieval <br />
 * Assignment 05 <br />
 * Part 3 <br /><br />
 * 
 * Interface common to all ranking methods that can be passed to the {@link Search} interface
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 *
 */
public abstract class RankingMethod {

	private String file_path;

	public RankingMethod(String filepath) {
		this.file_path = filepath;
	}
	
	public Iterable<SearchResult> search (Query q) {
		
		Map<Integer,SearchResult> docIDtoSearchResult = new HashMap<Integer,SearchResult>();
		
		IndexIterator it  = new IndexIterator(this.file_path);
		
		while (it.hasNext()) {
			IndexEntry i = it.next();
			if (q.containsTerm(i.getTerm())) {
				for (Posting p : i.getPostings()) {
					SearchResult s = docIDtoSearchResult.get(p.getId());
					if (s == null) {
						s = new SearchResult(p.getId());
					}
					s.addHit(new Hit(i.getTerm(), p.getPositions()));
					docIDtoSearchResult.put(p.getId(), s);
				}
			}
		}
		
		calculateScores (docIDtoSearchResult.values());
		//TODO: give how to compare search results
		PriorityQueue<SearchResult> ranking = new PriorityQueue<SearchResult> (); 
		ranking.addAll(docIDtoSearchResult.values());
		return ranking;
	}

	protected abstract void calculateScores(Collection<SearchResult> searchResults) ;
}