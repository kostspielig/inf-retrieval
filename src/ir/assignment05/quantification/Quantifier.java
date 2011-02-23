package ir.assignment05.quantification;

import java.util.Vector;

import ir.assignment02.IO;
import ir.assignment04.DocumentIterator;
import ir.assignment04.Parser;


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
public class Quantifier {

	private DocumentIterator docIterator;
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Please specify the file where the book is stored.");
			return;
		}
		String book = IO.getFileContent(args[0]);
		int totalWords = tokenizeWords(book).size();
		System.out.println("Total words of " + args[0] + " is " + totalWords);
		
		// built index
		int occurencesOfLove;
		int occurencesOfAdventure;
		String[] topWords = new String[5];
	}

	private static Vector<String> tokenizeWords(String book) {
		Parser parser = new Parser();
		Vector<String> tokens = parser.parse(book);
		return tokens;
	}
}
