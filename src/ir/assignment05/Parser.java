package ir.assignment05;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * CS 121 Information Retrieval <br />
 * Assignment 04 <br />
 * Part 3 <br /><br />
 * 
 * Tokenizes a given text.
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 */
public class Parser {

	/**
	 * Delimiters used for tokenizing the content
	 */
	private static final String TOKEN_DELIMITER = " \t\n\r\f\".,:;-/!?'~@#*+§$%&()=`´{[]}|<>";
	
	
	public Vector<String> parse(String text) {

		Vector<String> tokens = new Vector<String>();
		StringTokenizer tokenizer = new StringTokenizer(text,TOKEN_DELIMITER); // tokenize content
		
		while (tokenizer.hasMoreElements()) {
			tokens.add(tokenizer.nextToken());
		}
		
		return tokens;
	}

	
}
