package ir.assignment04;

import java.util.LinkedList;
import java.util.List;

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
