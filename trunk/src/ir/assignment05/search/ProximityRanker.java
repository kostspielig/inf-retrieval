package ir.assignment05.search;

import ir.assignment05.utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * CS 121 Information Retrieval <br />
 * Assignment 05 <br />
 * Part 3 <br /><br />
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 */
public class ProximityRanker extends RankingMethod {

	private static final double BASE = 1.5;
	private static final int MAX_WINDOW_SIZE = 10;

	public ProximityRanker(String filepath, int corpusSize) {
		super(filepath, corpusSize);
	}

	@Override
	protected void calculateScores(Iterable<SearchResult> searchResults) {
		for (SearchResult sr : searchResults) { // BEGIN iterate over searchresults (=over documents)
			double totalScore = 0.0;

			ArrayList<Pair<Integer,Integer>> wordPositions = generateWordPositions(sr);
			int currentIdx = 0;
			for (Pair<Integer,Integer> pair : wordPositions) { // BEGIN iterate over wordPosition pairs
				// score for one single term
				totalScore += score(1,1); // frequency, window size
				
				// find larger term combinations
				int[] numberOfWindows = new int[sr.getHits().size()+1]; // each index represents the number of windows containing (index) query terms
				Arrays.fill(numberOfWindows, 0); // initially set to 0
				for (int i = currentIdx+1; i<wordPositions.size(); i++) { // BEGIN move forward over wordPosition pairs
					
					Pair<Integer,Integer> otherPair = wordPositions.get(i);
					int wSize = otherPair.getFirst()-pair.getFirst();
					if (wSize<=MAX_WINDOW_SIZE) {
						if (otherPair.getSecond() != pair.getSecond()) { // represent different terms
							int previouslyGenerated = 0;
							// generate new combinations based on previous ones
							for (int j=2; j<numberOfWindows.length; j++) { // BEGIN generate triples, quadruples, ...
								numberOfWindows[j-1] += previouslyGenerated;
								if (numberOfWindows[j] > 0) { // generate numberOfWindows[j] additional combinations
									totalScore += numberOfWindows[j] * score(j+1,wSize);
									previouslyGenerated = numberOfWindows[j];
								} else {
									break;
								}
							} // END generate triples, quadruples, ...
							// score the new tuple
							totalScore += score(2, wSize);
							numberOfWindows[2] += 1;
						}
					} else {
						break;
					}
					
				} // END move forward over wordPosition pairs
				currentIdx++;
			} // END iterate over wordPosition pairs  
			
			sr.setScore(totalScore);
		} // END iterate over search results
	}

	private double score(int freq, int wSize) {
		double frequency = (double) freq;
		double windowSize = (double) wSize;
		return frequency/windowSize * Math.pow(BASE,frequency);
	}

	/**
	 * @param sr
	 */
	private ArrayList<Pair<Integer,Integer>> generateWordPositions(SearchResult sr) {
		ArrayList<Pair<Integer,Integer>> wordPositions = new ArrayList<Pair<Integer,Integer>>();
		int idxTerm = 0;
		for (Hit h : sr.getHits()) {
			for (Integer pos : h.getPositions()) {
				wordPositions.add(new Pair<Integer, Integer>(pos,idxTerm));
			}
			idxTerm++;
		}
		Collections.sort(wordPositions);
		return wordPositions;
	}

	@Override
	protected String initializeName() {
		return "Proximity Ranker";
	}

}
