package ir.assignment05.search;

import ir.assignment05.index.IndexEntry;
import ir.assignment05.index.IndexIterator;
import ir.assignment05.index.Posting;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

	protected String file_path;

	public RankingMethod(String filepath) {
		this.file_path = filepath;
	}
	
	public PriorityQueue<SearchResult> search(Query q) {
		
		Map<Integer,SearchResult> docIDtoSearchResult = new HashMap<Integer,SearchResult>();
		
		IndexIterator it  = new IndexIterator(this.file_path);
		
		List<String> query = q.getQuery();
		
		while (it.hasNext()) {
			IndexEntry i = it.next();
			if (q.containsTerm(i.getTerm())) {
				query.remove(i.getTerm());
				for (Posting p : i.getPostings()) {
					SearchResult s = docIDtoSearchResult.get(p.getId());
					if (s == null) {
						s = new SearchResult(p.getId());
					}
					s.addHit(new Hit(p.getTfIdf(),i.getTerm(), p.getPositions()));
					docIDtoSearchResult.put(p.getId(), s);
				}
			}
			if (query.isEmpty())
				break;
			// TODO: OPTIMIZE: break if all terms of query have been processed -- Done but check it! :)
		}
		Collection<SearchResult> searchResults = docIDtoSearchResult.values();
		
		calculateScores (searchResults);
//		Comparator<SearchResult> comparator = new SearchResultComparator();
//		PriorityQueue <SearchResult> ranking = new PriorityQueue<SearchResult> ((searchResults.size() == 0) ? 1 : searchResults.size(),comparator); 
		// TODO: check ordering of results (previous bug)
		PriorityQueue<SearchResult> ranking = new PriorityQueue<SearchResult> ((searchResults.size() == 0) ? 1 : searchResults.size(),Collections.reverseOrder());
		ranking.addAll(searchResults);
		return ranking;
	}

	protected abstract void calculateScores(Iterable<SearchResult> searchResults) ;
}
