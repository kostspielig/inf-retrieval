package ir.assignment04;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * 
 * @author Fabian Linderberg, Maria Carrasco
 *
 */
public class DocumentIterator implements Iterator<File> {

	private File root;
	private File next;
	private Queue<File> files;
	private List<DocumentIterator> iterators;
	
	public DocumentIterator(String root) {
		this(new File(root));
	}
	
	public DocumentIterator(File f){
		this.root = f;
		if(!this.root.isDirectory()) {
			throw new IllegalArgumentException("The path provided is not a directory.");
		}
		initialize();
		this.next = next();
	}
	
	private void initialize() {
		this.iterators = new LinkedList<DocumentIterator>();
		this.files = new LinkedList<File>();
		
		File[] files = this.root.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				this.iterators.add(new DocumentIterator(f));
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
		if (!this.files.isEmpty()) {
			cache = this.files.poll();
		} else {
			for (DocumentIterator it : this.iterators) {
				if (it.hasNext()) {
					cache = it.next();
					break;
				}
			}
		}
		
		// return the next element
		File result = this.next;
		this.next = cache;
		return result;
	}

	@Override
	public void remove() {
		throw new NotImplementedException();
	}
	
}
