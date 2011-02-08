package ir.assignment04;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * 
 * @author Fabian Linderberg
 * @author Maria Carrasco
 *
 */
public class Parser {

	/**
	 * Delimiters used for tokenizing the content
	 */
	private static final String TOKEN_DELIMITER = " \t\n\r\f\".,:;/!?'~@#*+§$%&()=`´{[]}|<>";
	
	
	public Vector<String> parse(String text) {

		Vector<String> tokens = new Vector<String>();
		StringTokenizer tokenizer = new StringTokenizer(text,TOKEN_DELIMITER); // tokenize content
		
		while (tokenizer.hasMoreElements()) {
			tokens.add(tokenizer.nextToken());
		}
		
		return tokens;
	}

	
}
