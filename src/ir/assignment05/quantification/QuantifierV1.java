package ir.assignment05.quantification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import ir.assignment02.IO;
import ir.assignment04.DocumentIterator;
import ir.assignment04.Parser;
import ir.assignment05.index.CompleteBook;
import ir.assignment05.index.IndexConstructor;


/**
 * CS 121 Information Retrieval <br />
 * Assignment 05 <br />
 * Part 2 <br /><br />
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 *
 */
public class QuantifierV1 {

	private DocumentIterator docIterator;
	private static final String POSTINGSSEPARATOR = "\t";

	
	public static void main(String[] args) throws IOException {
		//input file with all books? TODO change args[0]
		//args[0] = file with all books
		// args[1] = output file
		if (args.length < 1) {
			System.out.println("Please specify the file where the book is stored.");
			return;
		}
//		File bookFile = new File(args[0]);
//		CompleteBook book = new CompleteBook(bookFile);
//		//TODO take real number of books!
		int numberOfBooks = 0;
//		int totalWords = tokenizeWords(book.getContent()).size();
//		System.out.println("Total words of " + args[0] + " is " + totalWords);
//		
		
		//TODO build index for books
		IndexConstructor indexConstructor = new IndexConstructor(args[0], args[1], false);
		indexConstructor.construct();
		
		BufferedReader index = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[1]), "UTF-8"));
		
		String line = index.readLine();
		//<term>[\t<doc>:<tf-idf>:<position>[,<position>]*]+

		List<HashMap<String, Integer>> top5WordsForEachDoc = 
									new ArrayList<HashMap<String, Integer>>();
		
		Integer [] smallestValues= new Integer[numberOfBooks];

		for (int i = 0; i < numberOfBooks; i++){
			smallestValues[i] = 0;
		}
		
		String[] rawLine = line.split("\t");
		//result: Term, multiple Postings
		String term = rawLine[0];
		
		//TODO check for love and adventure
		
		for (int i = 1; i <rawLine.length; i++){
			String[] rawPosting = rawLine[i].split(":");
			// result: Docname, tf-idf, multiple position
			int docId = Integer.valueOf(rawPosting[0]);
			String[] positions = rawPosting[2].split(",");
			int termFrequency = positions.length;
			if (top5WordsForEachDoc.get(docId).size() < 5){
			
				smallestValues[docId] = getSmallestValue(top5WordsForEachDoc.get(docId), termFrequency, smallestValues[docId]);
				top5WordsForEachDoc.get(docId).put(term, termFrequency);
			
			}
			else if (smallestValues[docId] < termFrequency){
			//TODO allow top 6/7/8 if some keys have the same value?
	
				//remove key with smallest value
				HashMap<String, Integer> copyTop5 = new HashMap<String, Integer>(top5WordsForEachDoc.get(docId));
										
				for (Entry<String, Integer> entry : copyTop5.entrySet()) {
					if (entry.getValue() == smallestValues[docId]) {
							top5WordsForEachDoc.get(docId).remove(entry.getKey());
							break;
					}
				}
				top5WordsForEachDoc.get(docId).put(term, termFrequency);
				
				//smallestValue
				smallestValues[docId] = getSmallestValue(top5WordsForEachDoc.get(docId), termFrequency, smallestValues[docId]);
			}
		}
	



		// do all books contain those two words?
		// top 5 words in each book that aren't stopwords
		
		int occurencesOfLove;
		int occurencesOfAdventure;
		String[] topWords = new String[5];
	}

	private static Vector<String> tokenizeWords(String book) {
		Parser parser = new Parser();
		Vector<String> tokens = parser.parse(book);
		return tokens;
	}
	
	private static int getSmallestValue(HashMap<String, Integer> top5, int termFrequency, int smallestValue){
		int sameSmallestValue = 0;

		for (Entry<String, Integer> entry : top5.entrySet()) {
			if (entry.getValue() < termFrequency) {
				sameSmallestValue += 1;
			}
		}
		if (sameSmallestValue == 1) smallestValue = termFrequency;
		return smallestValue;
}
}
