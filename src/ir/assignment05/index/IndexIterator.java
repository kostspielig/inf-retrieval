package ir.assignment05.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * CS 121 Information Retrieval <br />
 * Assignment 05 <br />
 * Part 3 <br /><br />
 * 
 * Iterates over the inverted index.
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 */
public class IndexIterator implements Iterator<IndexEntry> {

	private static final String FILE_ENCODING = "UTF-8";
	private static final String SEPARATOR = "\t";
	
	private BufferedReader br;
	private IndexEntry next;

	public IndexIterator(String filePath) {
		File indexFile = new File(filePath);
		try {
			this.br = new BufferedReader(new InputStreamReader(new FileInputStream(indexFile), FILE_ENCODING));
		} catch (Exception e) {
			// error opening file
			this.br = null;
			e.printStackTrace();
		}
		next();
	}
	
	@Override
	public boolean hasNext() {
		return this.next != null;
	}

	@Override
	public IndexEntry next() {
		IndexEntry current = next;
		IndexEntry cached = null;
		if (br != null) {
			String line;
			try {
				line = br.readLine();
				if (line != null) {
					String[] parts = line.split(SEPARATOR,2);
					cached = new IndexEntry(parts[0],Posting.decodeAndDecompressPostingList(parts[1]));
				} else {
					br.close();
				}
			} catch (IOException e) {
				// error reading index file
				e.printStackTrace();
			}

		}
		
		this.next = cached;
		return current;
	}

	@Override
	public void remove() {
		throw new NotImplementedException();
	}

}
