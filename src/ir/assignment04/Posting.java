package ir.assignment04;

import java.util.Collections;
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
public class Posting {

	private static final String DELIMITER_POSITIONS = ",";
	private static final String DELIMITER_POSTING = "\t";
	private static final String DELIMITER_POSTINGPART = ":";



	private Integer id;
	private String name;
	private int frequency;
	private List<Integer> positions;
	
	public Posting(Integer docID, String name, int pos){
		this.id = docID;
		this.name = name;
		this.positions = new LinkedList<Integer>();
		this.positions.add(pos);
		this.frequency = 1;
	}
	
	private Posting() {	}

	private void incrementFrequency(){
		this.frequency ++;
	}
	
	public void merge(Posting p) {
		if ((this.id == null || !this.id.equals(p.getId()))
				&& (this.name == null || !this.name.equals(p.getName()))) {
			throw new IllegalArgumentException("The postings can not be merged, since they belong to different documents.");
		}
		this.frequency += p.getFrequency();
		this.positions.addAll(p.getPositions());
		Collections.sort(this.positions);
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

	public Integer getId() {
		return id;
	}
	
	// CLASS methods
	
	private void setId(Integer id) {
		this.id = id;
	}

	private void setName(String name) {
		this.name = name;
	}

	private void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	private void setPositions(List<Integer> positions) {
		this.positions = positions;
	}

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
				compressedID = p.getId() - oldID;
			} else {
				compressedID = p.getId();
			}
			oldID = p.getId();
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

	public static Posting decodePosting(String encoded) {
		Posting p = new Posting();

		String[] parts = encoded.split(DELIMITER_POSTINGPART);	
		
		try {
			p.setId(Integer.valueOf(parts[0])); // ID
		} catch (NumberFormatException e) {
			p.setName(parts[0]); // name
		}
		
		p.setFrequency(Integer.valueOf(parts[1]));
		
		List<Integer> positions = new LinkedList<Integer>();
		for (String pos : parts[2].split(DELIMITER_POSITIONS)) {
			positions.add(Integer.valueOf(pos));
		}
		p.setPositions(positions);
		
		return p;
	}

	public static Posting decodeAndDecompressPosting(String encoded, Integer offsetID) {
		Posting p = new Posting();

		String[] parts = encoded.split(DELIMITER_POSTINGPART);	
		
		// decode either ID or name; the ID needs to be decompressed
		try {
			Integer id = Integer.valueOf(parts[0]); // ID
			if (offsetID != null) {
				id += offsetID;
			}
			p.setId(id);
		} catch (NumberFormatException e) {
			p.setName(parts[0]); // name
		}
		
		// decode frequency
		p.setFrequency(Integer.valueOf(parts[1]));
		
		// decode and decompress frequencies
		List<Integer> positions = new LinkedList<Integer>();
		Integer previousPos = null;
		for (String posStr : parts[2].split(DELIMITER_POSITIONS)) {
			Integer pos = Integer.valueOf(posStr);
			if (previousPos != null) {
				pos += previousPos;
			}
			positions.add(pos);
			previousPos = pos;
		}
		p.setPositions(positions);
		
		return p;
	}
	
}
