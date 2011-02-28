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
public class TfIdfRanker extends RankingMethod {

	public TfIdfRanker(String filepath, int corpusSize) {
		super(filepath, corpusSize);
	}

	@Override
	protected void calculateScores(Iterable<SearchResult> searchResults) {
		for (SearchResult s : searchResults) {
			double sumTfIdf = 0;
			for (Hit h : s.getHits()) {
				sumTfIdf += h.getTfIdf();
			}
			s.setScore(sumTfIdf);
		}
	}

	@Override
	protected String initializeName() {
		return "Tf-idf Ranker";
	}

}
