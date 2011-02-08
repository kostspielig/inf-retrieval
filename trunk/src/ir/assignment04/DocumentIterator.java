package ir.assignment04;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * CS 121 Information Retrieval <br />
 * Assignment 04 <br />
 * Part 3 <br /><br />
 * 
 * Iterates recursively over the files in a root directory and its subfolders. 
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 */
public class DocumentIterator implements Iterator<File> {

	/**
	 * The root directory
	 */
	private File root;
	/**
	 * The cached next file
	 */
	private File next;
	/**
	 * All files in the root directory
	 */
	private Queue<File> files;
	/**
	 * All subfolders in the root directory
	 */
	private Queue<File> directories;
	/**
	 * The iterator for the currently chosen subfolder
	 */
	private DocumentIterator subfolderIterator;
	
	public DocumentIterator(String root) {
		this(new File(root));
	}
	
	public DocumentIterator(File f){
		this.root = f;
		if(!this.root.isDirectory()) {
			throw new IllegalArgumentException("The path provided is not a directory.");
		}
		initialize();
		next();
	}
	
	private void initialize() {
		this.directories = new LinkedList<File>();
		this.files = new LinkedList<File>();
		
		File[] files = this.root.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				this.directories.add(f);
			} else {
				this.files.add(f);
			}
		}
	}

	@Override
	public boolean hasNext() {
		return this.next != null;
	}

	@Override
	public File next() {
		// cache the element after the next
		File cache = null;
		if (!this.files.isEmpty()) { // iterate over own files first
			cache = this.files.poll();
		} else { // if no files remain, utilize iterator for subfolders
			chooseNewIteratorIfNecessary();
			if (this.subfolderIterator != null && this.subfolderIterator.hasNext()) {
				cache = this.subfolderIterator.next();
			} 
		}
		
		// return the next element
		File result = this.next;
		this.next = cache;
		return result;
	}

	/**
	 * Chooses new iterator if none is initialized or if the current one does not offer a next element.
	 * 
	 * If no appropriate iterator can be found, the subfolderIterator is set to null.
	 */
	private void chooseNewIteratorIfNecessary() {
		boolean success = false;
		if (this.subfolderIterator == null || !this.subfolderIterator.hasNext()) {
			while (!this.directories.isEmpty()) {
				this.subfolderIterator = new DocumentIterator(this.directories.poll());
				if (this.subfolderIterator.hasNext()) {
					success = true;
					break;
				}
			}
			if (!success) {
				this.subfolderIterator = null;
			}
		}
	}

	@Override
	public void remove() {
		throw new NotImplementedException();
	}
	
}
