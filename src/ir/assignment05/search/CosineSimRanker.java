package ir.assignment05.search;

import ir.assignment05.index.IndexEntry;
import ir.assignment05.index.IndexIterator;
import ir.assignment05.index.Posting;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;


/**
 * CS 121 Information Retrieval <br />
 * Assignment 05 <br />
 * Part 3 <br /><br />
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 */
public class CosineSimRanker extends RankingMethod {
	
	private Map<Integer, Double> docIDtoMagnitudeSquared;
	
	public CosineSimRanker(String filepath) {
		super(filepath);
	}

	@Override
	protected void calculateScores(Iterable<SearchResult> searchResults) {
		
		//copied from tfidfranker
		for (SearchResult s : searchResults) {
			double sumTfIdf = 0;
			for (Hit h : s.getHits()) {
				sumTfIdf += h.getTfIdf();
			}
			double score = sumTfIdf / Math.sqrt(this.docIDtoMagnitudeSquared.get(s.getDocID()));
			s.setScore(score);
		}
	}

	
	public PriorityQueue<SearchResult> search(Query q) {

		Map<Integer,SearchResult> docIDtoSearchResult = new HashMap<Integer,SearchResult>();
		this.docIDtoMagnitudeSquared = new HashMap<Integer, Double>();
		
		IndexIterator it  = new IndexIterator(this.file_path);

		while (it.hasNext()) {
			IndexEntry i = it.next();
			boolean isRelevantTerm = q.containsTerm(i.getTerm());
			for (Posting p : i.getPostings()) {
				increaseMagnitude(docIDtoMagnitudeSquared, p);
				
				if (isRelevantTerm) {
					updateSearchResult(docIDtoSearchResult, i, p);
				}
			}
		}
		
		Collection<SearchResult> searchResults = docIDtoSearchResult.values();
		
		calculateScores(searchResults);
		
		PriorityQueue <SearchResult> ranking = new PriorityQueue<SearchResult> ((searchResults.size() == 0) ? 1 : searchResults.size(),Collections.reverseOrder()); 
		ranking.addAll(searchResults);
		return ranking;	
		
	}

	/**
	 * @param docIDtoSearchResult
	 * @param i
	 * @param p
	 */
	private void updateSearchResult(
			Map<Integer, SearchResult> docIDtoSearchResult, IndexEntry i,
			Posting p) {
		SearchResult s = docIDtoSearchResult.get(p.getId());
		if (s == null) {
			s = new SearchResult(p.getId());
		}
		s.addHit(new Hit(p.getTfIdf(),i.getTerm(), p.getPositions()));
		docIDtoSearchResult.put(p.getId(), s);
	}


	/**
	 * @param docIDtoMagnitude
	 * @param p
	 */
	private void increaseMagnitude(Map<Integer, Double> docIDtoMagnitude,
			Posting p) {
		Double currentMagnitude = docIDtoMagnitude.get(p.getId());
		if (currentMagnitude == null){
			currentMagnitude = 0.0;
		} 
		docIDtoMagnitude.put(p.getId(), currentMagnitude + (p.getTfIdf()*p.getTfIdf()));
	}
}