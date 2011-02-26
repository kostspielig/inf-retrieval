package ir.assignment05.utils;

public class Pair<T1 extends Comparable<T1>,T2 extends Comparable<T2>> implements Comparable<Pair<T1,T2>> {

	private T1 first;
	private T2 second;
	
	public Pair(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * @return the first
	 */
	public T1 getFirst() {
		return first;
	}

	/**
	 * @return the second
	 */
	public T2 getSecond() {
		return second;
	}

	@Override
	public int compareTo(Pair p) {    
		if (this == p) {
			return 0;
		}
		
		if (p == null) {
			throw new NullPointerException("The pair to compare with shall not be null.");
		}
		
		int comparison = this.first.compareTo((T1) p.getFirst());
		if (comparison != 0) {
			return comparison;
		} else { // fst component is equal --> compare second
			return this.second.compareTo((T2) p.getSecond());
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair<T1,T2> other = (Pair<T1,T2>) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}
	
	
}
