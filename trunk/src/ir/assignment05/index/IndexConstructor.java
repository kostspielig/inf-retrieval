package ir.assignment05.index;

import ir.assignment05.utils.BookInfo;
import ir.assignment05.utils.Parser;
import ir.assignment05.utils.Stemmer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

/**
 * CS 121 Information Retrieval <br />
 * Assignment 05 <br />
 * Part 3 <br /><br />
 * 
 * Constructs and inverted index.
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 */
public class IndexConstructor {
	
	/**
	 * The maximum capacity of the index in memory.
	 */
	private static final int MAX_CAPACITY = 50000;
	private static final String DEFAULT_STOPWORDS_FILE = "stopwords.txt";
	private static final String FILE_ENCODING = "UTF-8";
	private static final String SPECIAL_CHARS = "[!\"§$%&/()=?`´{}\\[\\]\\^°*+~'#-_.:,;<>|]+";
	private static final String SEPARATOR = "\t";
	private String root;
	/**
	 * Iterates recursively over all documents in the root folder and its subfolders.
	 */
	private DocumentIterator docIterator;
	/**
	 * Tokenizes a passed document text.
	 */
	private Parser parser;
	
	/**
	 * the inverted index.
	 */
	private HashMap<String, List<Posting>> index;
	
	/**
	 * The Porter Stemmer used to stem tokens
	 */
	private Stemmer stemmer;
	/**
	 * The user-defined list of stopwords after stemming
	 */
	private Set<String> stopwords;
	/**
	 * The output path of the final result.
	 */
	private String out;
	/**
	 * The directory in which intermediate results are stored.
	 */
	private String intermediateOutPath;
	private String idMappingOut;
	/**
	 * true if output files shall be compressed
	 */
	private boolean compressionEnabled;
	private int fileNr = 0;
	/**
	 * incrementing number of documents
	 */
	private int docNr = 0;
	private Map<Integer,BookInfo> idToNameMapping = new HashMap<Integer,BookInfo>();
	/**
	 * Initializes a new object that is responsible for constructing the inverted index. 
	 * 
	 * @param root the path to the root directory
	 * @param enableCompression 
	 * @param outDir 
	 */
	public IndexConstructor(String root, String outDir, boolean enableCompression) {
		this.root = root;
		this.docIterator = new DocumentIterator(root);
		this.parser = new Parser();
		this.index = new HashMap<String, List<Posting>>(MAX_CAPACITY+1,1.0f);
		this.stemmer = new Stemmer();
		this.stopwords = loadStopwords();
		this.out = outDir + File.separator + "index_" + (enableCompression ? "compressed" : "plain") + ".txt";
		this.intermediateOutPath = outDir + File.separator + "intermediate" + File.separator;
		this.idMappingOut = outDir + File.separator + "idsToDocNames.txt";
		this.compressionEnabled = enableCompression;
	}
	
	/**
	 * Constructs the inverted index.
	 */
	public void construct() {
		while (this.docIterator.hasNext()) {
			File nextDoc = this.docIterator.next();
			CompleteBook book = new CompleteBook(nextDoc);
			Vector<String> tokens = this.parser.parse(book.getContent());
			String docName = extractDocName(nextDoc);
			this.idToNameMapping.put(this.docNr,new BookInfo(docName,tokens.size()));
			for(int i = 0; i < tokens.size(); i++) {
				indexToken(this.docNr, docName, tokens.get(i), i);
			}
			this.docNr++;
		}
		writeIntermediateIndex();
		mergeIntermediateResults();
		writeIdMapping();
	}

	/**
	 * Loads and stems user-defined stopwords.
	 * 
	 * @return stemmed stopwords
	 */
	private Set<String> loadStopwords() {
		try {
			Set<String> stopwords = new TreeSet<String>();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(DEFAULT_STOPWORDS_FILE), FILE_ENCODING));
			String line;
			while ((line = br.readLine()) != null) {
				stopwords.add(stem(line));
			}
			return stopwords;
		} catch (Exception e) { // problems reading file, return empty set instead
			e.printStackTrace();
			return new TreeSet<String>();
		}		
	}

	/**
	 * Extracts the document name from the path of the passed file.
	 * 
	 * @param doc
	 * @return the document name
	 */
	private String extractDocName(File doc) {
		return doc.getAbsolutePath().substring(this.root.length()+1);
	}

	/**
	 * Stores the token in the index.
	 * 
	 * @param docID the ID of the document the token occurs in
	 * @param docName the name of the document the token occurs in
	 * @param token the token to store
	 * @param pos the position of the token in the document
	 */
	private void indexToken(int docID, String docName, String token, int pos) {
		String stemmed = stem(token);
		if (isValidToken(stemmed)) {
			List<Posting> postings = this.index.get(stemmed);
			if (postings == null){ // token not yet included in the index 
				flushIndexToDiskIfNecessary();
				Posting p = new Posting(docID, docName, pos);
				postings = new ArrayList<Posting>();
				postings.add(p);
				this.index.put(stemmed, postings);
			} else { // token is already included, update posting or create new posting
				int i=0;
				for (Posting p : postings) {
					int comparison;
					if (this.compressionEnabled) {
						comparison = p.getId().compareTo(docID);
					} else {
						comparison = p.getName().compareTo(docName);
					}
					if (comparison == 0) { // posting for the document exists already --> update
						p.addPostion(pos);
						return;
					} else if (comparison > 0) { // posting for the doc does not exist --> insert inbetween
						Posting newP = new Posting(docID, docName, pos);
						postings.add(i, newP);
						return;
					}
					i++;
				}
				Posting newP = new Posting(docID, docName, pos); // posting for the doc does not exist & all other docNames are smaller --> insert at the end
				postings.add(i, newP);
			}
		}
	}
	
	
	/**
	 * Converts passed word to lowercase representation and stems it.
	 * 
	 * @param word
	 * @return the stemmed word
	 */
	private String stem(String word) {
		char[] wordChrAr = word.toLowerCase().toCharArray();
		this.stemmer.add(wordChrAr,wordChrAr.length);
		this.stemmer.stem();
		return this.stemmer.toString();
	}

	/**
	 * Checks whether the token is longer than one character and consists not only of special characters.
	 * Makes sure the token isn't a stopword
	 * 
	 * @param token
	 * @return true if it is longer than one char and does NOT consist only of special characters
	 */
	private boolean isValidToken(String token) {
		return token.length() > 1 && !stopwords.contains(token) && !token.matches(SPECIAL_CHARS);
	}
	

	/**
	 * Writes the intermediate index to disk if capacity is reached.
	 */
	private void flushIndexToDiskIfNecessary() {
		if (this.index.size() >= MAX_CAPACITY) {
			writeIntermediateIndex();
			System.out.println("Write out intermediate result: " + this.fileNr);
		}
	}

	/**
	 * Writes the intermediate index to disk.
	 */
	private void writeIntermediateIndex() {
		// sort hash maps based on keys
		SortedMap<String, List<Posting>> sorted = new TreeMap<String, List<Posting>>(this.index);
		
		File output = new File(this.intermediateOutPath + "intermediate_" + this.fileNr++ + ".txt");
		File parentDir = output.getParentFile();
		if(! parentDir.exists()) {
			parentDir.mkdirs();
		}
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), FILE_ENCODING));
			for (Map.Entry<String, List<Posting>> entry : sorted.entrySet()) {
				//intermediate files are always written compressed to disk
				bw.write(entry.getKey() + "\t" + Posting.encodePostings(entry.getValue(), true));
				bw.newLine();
			}
			
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.index.clear();
	}

	/**
	 * Reads the intermediate results from disk and merges them into a new output file.
	 */
	private void mergeIntermediateResults() {
		try {
			File dir = new File(this.intermediateOutPath);
			if (dir.isDirectory()) {
				// initialize readers for all intermediate results
				File[] files = dir.listFiles();
				ArrayList<BufferedReader> readers = new ArrayList<BufferedReader>(files.length);
				for (File file : files) {
					readers.add(new BufferedReader(new InputStreamReader(
							new FileInputStream(file), FILE_ENCODING)));
				}

				//initialize output writer
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(this.out)), FILE_ENCODING));

				//initialize data structure holding all files with mappings from name to positions
				String[][] currentRecords = new String[files.length][2];
				
				String smallestTerm = null;
				List<Integer> idxOfSmallestTerms = new LinkedList<Integer>();
				List<Integer> currentFileIndices = new ArrayList<Integer>();

				for (int i = 0; i < files.length; i++){
					idxOfSmallestTerms.add(i);
					currentFileIndices.add(i);
				}
								
				while (!currentFileIndices.isEmpty()) {
					for (int idx : idxOfSmallestTerms) {
						String line = readers.get(idx).readLine();
						if (line == null){
							currentFileIndices.remove(currentFileIndices.indexOf(idx));
						}else
							//TODO check if "2" is correct
							currentRecords[idx] = line.split(SEPARATOR,2);
					}
					
					// break if all files have been processed
					if (currentFileIndices.isEmpty())
						break;
					
					idxOfSmallestTerms.clear();
					smallestTerm = currentRecords[currentFileIndices.get(0)][0];

					List<String> postingsToMerge = new LinkedList<String>();
					for (Integer idxOfFile : currentFileIndices){
						int cmp = smallestTerm.compareTo(currentRecords[idxOfFile][0]); 
						if ( cmp > 0 ){ // found smaller term
							// choose new smallest term
							smallestTerm = currentRecords[idxOfFile][0];
							// add idx of smallest term
							idxOfSmallestTerms.clear();
							idxOfSmallestTerms.add(idxOfFile);
							// add posting list of smallest term
							postingsToMerge.clear();
							postingsToMerge.add(currentRecords[idxOfFile][1]);
						} else if(cmp == 0){ // found equally small term
							// add idx of equally small term
							idxOfSmallestTerms.add(idxOfFile); 
							// add posting list of equally small term
							postingsToMerge.add(currentRecords[idxOfFile][1]);
						}
					}
					List<Posting> merged = decodeAndMergePostingLists(postingsToMerge);
					calculateAndStoreTFIDF(merged);
					bw.write(smallestTerm + SEPARATOR + Posting.encodePostings(merged, compressionEnabled));

					bw.newLine();
				}

				for(BufferedReader br: readers){
					br.close();
				}
				bw.close();
			}

		}	 catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();		
		}
	}
	
	private void calculateAndStoreTFIDF(List<Posting> postings) {
		for (Posting p : postings) {
			BookInfo book = idToNameMapping.get(p.getId());
			p.calculateAndStoreTfIdf(book.getLength(), idToNameMapping.size(), postings.size());
		}
	}

	private List<Posting> decodeAndMergePostingLists(List<String> encodedPostingLists) {
		SortedMap<Integer,Posting> postingPerDocument = new TreeMap<Integer,Posting>();
		
		// for every encoded list of encoded postings
		for (String encodedList : encodedPostingLists) {
			Integer previousID = null;
			
			// for every encoded posting
			for (String encodedPosting : encodedList.split(SEPARATOR)) {
				// decode
				Posting p;
				//intermediate files are always compressed
				p = Posting.decodeAndDecompressPosting(encodedPosting, previousID);
				previousID = p.getId();
				
				
				// merge positions
				Posting existingPosting = postingPerDocument.get(p.getId());
				if (existingPosting == null) {
					existingPosting = p; 
				} else {
					existingPosting.merge(p);
				}
				// TODO: tfidf  calc must take place later on! move the next 2 code lines
//				
//				existingPosting.calculateAndStoreTfIdf(book.getLength(), idToNameMapping.size(), nrDocsContainingTerm)
				postingPerDocument.put(p.getId(), existingPosting);
			}
		}
		
		return new LinkedList<Posting>(postingPerDocument.values());
	}

	private void writeIdMapping() {
		if (compressionEnabled) {
			File output = new File(this.idMappingOut);
			File parentDir = output.getParentFile();
			if(! parentDir.exists()) {
				parentDir.mkdirs();
			}
			try {
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), FILE_ENCODING));

				for (Map.Entry<Integer, BookInfo> entry : idToNameMapping.entrySet()) {
					bw.write(entry.getKey() + "\t" + entry.getValue().getName());
					bw.newLine();
				}

				bw.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Please provide the path to the root folder, the path to the output directory, and a flag indicating whether compression shall be enabled.");
			System.exit(1);
		}
		
		String rootDir = args[0];
		String outDir = args[1];
		boolean enableCompression;
		try {
			enableCompression = Integer.valueOf(args[2]) == 1;
		} catch (NumberFormatException e) {
			enableCompression = false;
		}
		
		IndexConstructor constructor = new IndexConstructor(rootDir, outDir, enableCompression);
		constructor.construct();
		
	}

}
