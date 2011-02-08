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

	
}
