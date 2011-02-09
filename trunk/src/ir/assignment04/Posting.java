package ir.assignment04;

import java.util.LinkedList;
import java.util.List;

/**
 * CS 121 Information Retrieval <br />
 * Assignment 04 <br />
 * Part 3 <br /><br />
 * 
 * Represents a posting in the inverted index.
 * 
 * @see IndexConstructor
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 */
public class Posting implements Comparable<Posting> {

	private static final String DELIMITER_POSITIONS = ",";
	private static final String DELIMITER_POSTING = "\t";
	private static final String DELIMITER_POSTINGPART = ":";

	public int getFrequency() {
		return frequency;
	}

	public List<Integer> getPositions() {
		return positions;
	}

	// TODO: introduce DocID
	private String name;
	private int frequency;
	private List<Integer> positions;
	
	public Posting(String name, int pos){
		this.name = name;
		this.positions = new LinkedList<Integer>();
		this.positions.add(pos);
		this.frequency = 1;
	}
	
	private void incrementFrequency(){
		this.frequency ++;
	}
	
	public void addPostion(int pos){
		this.positions.add(pos);
		incrementFrequency();
	}

	public String getName() {
		return name;
	}

	@Override
	public int compareTo(Posting o) {
		if (this == o) {
			return 0;
		}
		return this.name.compareTo(o.getName());
	}

	
	// CLASS methods
	
	public static String encodePostings(List<Posting> postings, boolean enableCompression) {
		String result;
		
		if (enableCompression) {
			result = compressListOfPostings(postings);
		} else {
			result = textifyListOfPostings(postings);
		}
		
		return result;
	}

	private static String textifyListOfPostings(List<Posting> postings) {
		StringBuilder strBuilder = new StringBuilder();
		
		for (Posting p : postings) {
			strBuilder.append(p.getName());
			strBuilder.append(DELIMITER_POSTINGPART);
			strBuilder.append(p.getFrequency());
			strBuilder.append(DELIMITER_POSTINGPART);
			int i = 0;
			for (Integer pos : p.getPositions()) {
				strBuilder.append(pos);
				if (i < p.getPositions().size()-1) {
					strBuilder.append(DELIMITER_POSITIONS);
				}
			}
			strBuilder.append(DELIMITER_POSTING);
		}
		
		return strBuilder.toString();
	}

	private static String compressListOfPostings(List<Posting> postings) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
