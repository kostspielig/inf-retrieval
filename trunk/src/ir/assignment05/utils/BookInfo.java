package ir.assignment05.utils;

public class BookInfo {
	private String name;
	private int length;
	
	public BookInfo(String docName, int length) {
		this.name = docName;
		this.length = length;
	}
	public String getName() {
		return name;
	}
	public int getLength() {
		return length;
	}

}
