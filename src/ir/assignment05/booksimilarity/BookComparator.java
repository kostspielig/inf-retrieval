package ir.assignment05.booksimilarity;

import ir.assignment05.index.IndexEntry;
import ir.assignment05.index.IndexIterator;
import ir.assignment05.index.Posting;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class BookComparator {
	private static final int NUMBER_OF_BOOKS = 7;
	private String filePath;
	private SortedSet<String> mostFrequentTerms;
	
	
	
	public BookComparator(String path, int limit) {
		this.filePath = path;
		this.mostFrequentTerms = this.extractMostFrequentTerms(limit);
	}


	private SortedSet<String> extractMostFrequentTerms(int limit) {
		SortedSet<String> mostFrequentTerms = new TreeSet<String>();
		IndexIterator iterator = new IndexIterator(this.filePath);
		SortedMap<Integer,List<String>> freqToTerms = new TreeMap<Integer,List<String>>(Collections.reverseOrder()); 
		while (iterator.hasNext()){
			IndexEntry entry = iterator.next();
			int freq = 0;
			for (Posting p : entry.getPostings()) {
				freq += p.getPositions().size(); // getFrequency cannot be used, since only the tfidf is stored in the compressed representation of the final index
			}
			List<String> terms = freqToTerms.get(freq);
			if (terms == null) {
				terms = new LinkedList<String>();
			}
			terms.add(entry.getTerm());
			freqToTerms.put(freq, terms);
		}
		
		// only select "limit" terms
		for (List<String> terms : freqToTerms.values()) {
			if (mostFrequentTerms.size() < limit) {
				mostFrequentTerms.addAll(terms);
			} else {
				break;
			}
		}
		return mostFrequentTerms;
	}


	public double compare(int fstBookId, int sndBookId){
		IndexIterator iterator = new IndexIterator(this.filePath);
		
		// ensure that fst <= snd
		if (fstBookId > sndBookId) {
			int tmp = sndBookId;
			sndBookId = fstBookId;
			fstBookId = tmp;
		}
		
		double fstMagnSquared = 0.0;
		double sndMagnSquared = 0.0;
		double dotProduct = 0.0;
		while (iterator.hasNext()) {
			IndexEntry e = iterator.next();
			boolean isRelevantTerm = this.mostFrequentTerms.contains(e.getTerm());
			double fstTfIdf = 0.0;
			double sndTfIdf = 0.0;
			for (Posting p : e.getPostings()) {
				if (p.getId().equals(fstBookId)) {
					fstTfIdf = p.getTfIdf();
					fstMagnSquared += fstTfIdf * fstTfIdf;					
				} 
				if (p.getId().equals(sndBookId)) { // cannot be an elseif for the special case that fstId == sndId
					sndTfIdf = p.getTfIdf();
					sndMagnSquared += sndTfIdf * sndTfIdf;
					if (isRelevantTerm) {
						dotProduct += fstTfIdf * sndTfIdf;
					}
					break; // because sndBookId > fstBookId
				}				
			}
		}
		
		return dotProduct / (Math.sqrt(fstMagnSquared) * Math.sqrt(sndMagnSquared));
	}


	public static void main(String[] args) {
		//args[0] path to index file
		//args[1] limit of most frequent words
		
		BookComparator comparator = new BookComparator(args[0], Integer.valueOf(args[1]));
		for	(int i = 0; i <= NUMBER_OF_BOOKS; i++){
			for (int j = i+1; j <= NUMBER_OF_BOOKS; j++){
				double similarity = comparator.compare(i, j);
				System.out.println("similarity of book " + i + " and book " + j + " is " + similarity);
			}
		}
			
	}

}
