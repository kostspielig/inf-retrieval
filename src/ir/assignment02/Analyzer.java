package ir.assignment02;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * CS 121 Information Retrieval
 * Assignment 02
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 *
 */
public class Analyzer {

	private static final char DEFAULT_LIPOGRAM_LETTER = 'e';
	private static final String DEFAULT_INPUT = "in.txt";
	private static final String DEFAULT_OUTPUT = "out.txt";
	private static final String FILE_ENCODING = "UTF-8";
	private static final String SEPARATOR = ">==<";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		long endTime;

		// detect longest palindrom and lipogram, respectively
		String longestPalindrom = detectLongestPalindrom(DEFAULT_INPUT);
		String longestLipogram = detectLongestLipogram(DEFAULT_INPUT, DEFAULT_LIPOGRAM_LETTER);

		// write results
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(DEFAULT_OUTPUT)));
			w.write(longestPalindrom);
			w.newLine();
			w.write(SEPARATOR);
			w.newLine();
			w.write(longestLipogram);
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		endTime = System.currentTimeMillis();
		System.out.println("Runtime: " + (endTime - startTime));
	}

	/**
	 * Detects the longest palindrom in the provided text file.
	 * 
	 * @param filePath the path to the text file
	 * @return the longest palindrom
	 */
	private static String detectLongestPalindrom(String filePath) {
		String longestPalindrom = "The detection of the longest palindrom is not yet implemented.";
		return longestPalindrom;
	}
	
	/**
	 * Checks whether the passed {@link String} is a palindrom. Empty {@link String}s are regarded as palindroms.
	 * 
	 * @param str
	 * @return true if the passed {@link String} is a palindrom, false otherwise.
	 */
	private static boolean isPalindrom(String str) {
		int i=0;
		int j=str.length()-1;
		while (i<j) {
			if (str.charAt(i) != str.charAt(j)) {
				return false;
			}
			i++;
			j--;
		}
		return true;
	}

	/**
	 * Detects the longest lipogram in the provided text file. A lipogram is a sequence of characters that does not contain a particular letter. <br /> 
	 * <br />
	 * Both uppercase and lowercase representation of the specified letter will be avoided.
	 * If two lipograms of equal size exist, the first one is returned.
	 * 
	 * @param filePath the path to the text file
	 * @param letter the letter that shall not be contained in the lipogram 
	 * @return the longest lipogram
	 */
	private static String detectLongestLipogram(String filePath, char letter) {
		// TODO: if two or more lipograms of equal size exist, return them all
		String longestLipogram = new String();
		
		char lcLetter = Character.toLowerCase(letter); 
		char ucLetter = Character.toUpperCase(letter);
		
		try {
			// initialize reader
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(filePath), FILE_ENCODING));
			
			int currentChrValue; // integer value of the character read
			int nrOfIgnoredChrs = 0; // number of irrelevant characters that have been ignored
			StringBuffer strBuffer = new StringBuffer(); // the current lipogram
			
			while ((currentChrValue = br.read()) != -1) {
				char currentChr = (char) currentChrValue;
				if (currentChrValue > 127 || currentChr == lcLetter || currentChr == ucLetter) { // forbidden character -> terminate the current lipogram
					if (strBuffer.length() > longestLipogram.length() && (((double) (nrOfIgnoredChrs/strBuffer.length())) < 0.3)) { // set new longest lipogram if longer and less than 30% of characters have been ignored
						longestLipogram = strBuffer.toString();
					}
					strBuffer = new StringBuffer();
					nrOfIgnoredChrs = 0;
				} else if ((currentChr >= 'A' && currentChr <= 'Z') || (currentChr >= 'a' && currentChr <= 'z')) { // valid character -> append to current lipogram
					strBuffer.append(currentChr);
				} else { // irrelevant character
					strBuffer.append(currentChr);
					nrOfIgnoredChrs++;
				}
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return longestLipogram;
	}

}
