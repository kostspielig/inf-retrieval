package ir.assignment04;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

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



	private Integer id;
	private String name;
	private int frequency;
	private List<Integer> positions;
	
	public Posting(int docID, String name, int pos){
		this.name = name;
		this.positions = new LinkedList<Integer>();
		this.positions.add(pos);
		this.frequency = 1;
		this.id = docID;
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

	public int getFrequency() {
		return frequency;
	}

	public List<Integer> getPositions() {
		return positions;
	}

	public Integer getID() {
		return id;
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
			strBuilder.append(encodePosting(p.getName(), p.getFrequency(), p.getPositions()));
			strBuilder.append(DELIMITER_POSTING);
		}
		
		return strBuilder.toString();
	}

	private static String compressListOfPostings(List<Posting> postings) {
		StringBuilder strBuilder = new StringBuilder();
		Integer oldID = null;
		int compressedID;
		
		for (Posting p : postings) {
			if (oldID != null){
				compressedID = p.getID() - oldID;
			} else {
				compressedID = p.getID();
			}
			oldID = p.getID();
			strBuilder.append(encodePosting(String.valueOf(compressedID), p.getFrequency(), deltaEncoding(p.getPositions())));
			strBuilder.append(DELIMITER_POSTING);
		}
		
		return strBuilder.toString();
	}

	private static List<Integer> deltaEncoding(List<Integer> positions) {
		List<Integer> result = new LinkedList<Integer>();
		Integer oldPosition = null;
		int compressedPosition;
		
		for (Integer pos : positions) {
			if (oldPosition != null){
				compressedPosition = pos - oldPosition;
			} else {
				compressedPosition = pos;
			}
			result.add(compressedPosition);
			oldPosition = pos;
		}
		
		return result;
	}

	private static String encodePosting(String identifier, int frequency, List<Integer> positions){
		StringBuilder strBuilder = new StringBuilder();
		
		strBuilder.append(identifier);
		strBuilder.append(DELIMITER_POSTINGPART);
		strBuilder.append(frequency);
		strBuilder.append(DELIMITER_POSTINGPART);
		
		int i = 0;
		for (Integer pos : positions) {
			strBuilder.append(pos);
			if (i < positions.size()-1) {
				strBuilder.append(DELIMITER_POSITIONS);
			}
			i++;
		}
		
		return strBuilder.toString();
	}

	public static List<Posting> decodeAndMergePostings(List<String> encodedPostings) {
		List<Posting> result = new ArrayList<Posting>();
		
		SortedMap<String,List<Integer>> posPerDocument = new TreeMap<String,List<Integer>>();
		
		for (String encoded : encodedPostings) {
			Posting p = decode(encoded);
			List<Integer> positions = posPerDocument.get(p.getName());
			if (positions == null) {
				posPerDocument.put(p.getName(), p.getPositions());
			} else {
				// TODO: merge positions
			}
		}
		
		return result;
	}

	private static Posting decode(String encoded) {
		// TODO decode textified posting
		return null;
	}
	
}
