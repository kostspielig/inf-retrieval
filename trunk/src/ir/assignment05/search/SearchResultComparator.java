package ir.assignment05.search;

import java.util.Comparator;

public class SearchResultComparator implements Comparator<SearchResult> {

	@Override
	public int compare(SearchResult x, SearchResult y) {
		
		if (x.getScore()!= null && y.getScore() == null)
			return -1;
		if(x.getScore()== null && y.getScore() != null)
			return 1;
		if (x.getScore()== null && y.getScore() == null)
			return 0;
		
		return x.getScore().compareTo(y.getScore());
	}

}
