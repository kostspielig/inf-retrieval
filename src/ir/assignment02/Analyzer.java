package ir.assignment02;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * CS 121 Information Retrieval
 * Assignment 02
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 *
 */
public class Analyzer {

	private static final double THRESHOLD = 0.3;
	private static final char DEFAULT_LIPOGRAM_LETTER = 'e';
	private static final String DEFAULT_INPUT = "in.txt";
	private static final String DEFAULT_OUTPUT = "out.txt";
	private static final String FILE_ENCODING = "UTF-8";
	private static final String SEPARATOR = ">==<";
	private static final String SMALL_SEPARATOR = "><";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		long endTime;

		// detect longest palindrom and lipogram, respectively
		List<String> longestPalindrom = detectLongestPalindrome(DEFAULT_INPUT);
		List<String> longestLipograms = detectLongestLipogram(DEFAULT_INPUT, DEFAULT_LIPOGRAM_LETTER);

		// write results
		try {
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(DEFAULT_OUTPUT), FILE_ENCODING));
			writeList(w, longestPalindrom);
			w.newLine();
			w.write(SEPARATOR);
			w.newLine();
			writeList(w, longestLipograms);
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		endTime = System.currentTimeMillis();
		System.out.println("Runtime: " + (endTime - startTime) + "ms");
	}

	/**
	 * Writes the content of the list using the passed BufferedWriter.
	 * Elements are separated by SMALL_SEPARATOR.
	 * 
	 * @param w
	 * @param list
	 * @throws IOException
	 */
	private static void writeList(BufferedWriter w, List<String> list)
			throws IOException {
		Iterator<String> it = list.iterator();
		if (it.hasNext()) {
			w.write(it.next());
		}
		while (it.hasNext()) {
			w.newLine();
			w.write(SMALL_SEPARATOR);
			w.newLine();
			w.write(it.next());
		}
	}	

	/**
	 * Detects the longest palindrome in the provided text file.
	 * 
	 * @param filePath the path to the text file
	 * @return the longest palindrome
	 */
	private static List<String> detectLongestPalindrome(String filePath) {
		String text = IO.getFileContent(filePath);
		List<String> longestPalindromes = new LinkedList<String>();
		
		int maxLength = 0;
	
		for (int k=0; k<text.length(); k++) {
			maxLength = findLongestPalindromeForIndices(text, longestPalindromes, maxLength, k-1, k); // first run checking for even palindromes
			maxLength = findLongestPalindromeForIndices(text, longestPalindromes, maxLength, k-1, k+1); // second run checking for even palindromes
		}
		
		return longestPalindromes;
	}

	/**
	 * Shifts the given pointers i and j stepwise to find the longest palindrome. 
	 * If j=i+1, palindromes of even length are found. For j=i+2, palindromes of odd length are found.
	 * 
	 * @param text
	 * @param longestPalindromes
	 * @param maxLength
	 * @param i
	 * @param j
	 * @return the length of the longest detected palindrome
	 */
	private static int findLongestPalindromeForIndices(String text,
			List<String> longestPalindromes, int maxLength, int i, int j) {
		int nrOfIgnoredChrs = 0;

		while (i >= 0 && j < text.length()) {

			char iChar = Character.toLowerCase(text.charAt(i));
			char jChar = Character.toLowerCase(text.charAt(j));

			while(!legalChar(iChar) && i > 0){ //ignore character // alternative: if non ASCII shall not be skipped but rather split, check for !isAscii(iChar) first
				i--;
				nrOfIgnoredChrs++;
				iChar = Character.toLowerCase(text.charAt(i));
			}
			while(!legalChar(jChar) && j < text.length()-1){
				j++;
				nrOfIgnoredChrs++;
				jChar = Character.toLowerCase(text.charAt(j));
			}

			if (iChar == jChar) { // extend palindrome
				int len = j - i + 1;
				if (len >= maxLength && (((double) (nrOfIgnoredChrs/len)) < THRESHOLD)) {
					if (len > maxLength) {
						maxLength = len;
						longestPalindromes.clear();	
					}
					longestPalindromes.add(text.substring(i,j+1));
				}

				i--;
				j++;
			} else { 
				break; 
			}
		}
		return maxLength;
	}
	
	private static boolean isAscii(char c)
	{
		int value = (int) c;
		
		return (value <= 127);
	}
	/**
	 * Detects whether the given char is legal [A-Za-z]
	 * 
	 * @param c character
	 * @return 
	 */
	private static boolean legalChar (char c)
	{
		return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
	}
	
	/**
	 * Detects the longest lipogram in the provided text file. A lipogram is a sequence of characters that does not contain a particular letter. <br /> 
	 * <br />
	 * Both uppercase and lowercase representation of the specified letter will be avoided.
	 * If two or more lipograms of equal size exist, all are returned in the order of occurrence.
	 * 
	 * @param filePath the path to the text file
	 * @param letter the letter that shall not be contained in the lipogram 
	 * @return the longest lipogram
	 */
	private static List<String> detectLongestLipogram(String filePath, char letter) {
		List<String> longestLipograms = new LinkedList<String>();
		
		char lcLetter = Character.toLowerCase(letter); 
		char ucLetter = Character.toUpperCase(letter);
		
		try {
			// initialize reader
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(filePath), FILE_ENCODING));
			
			int currentChrValue; // integer value of the character read
			int maxLength = 0; // the current length of the longest palindrom(s) 
			int nrOfIgnoredChrs = 0; // number of irrelevant characters that have been ignored
			StringBuffer strBuffer = new StringBuffer(); // the current lipogram
			
			while ((currentChrValue = br.read()) != -1) {
				char currentChr = (char) currentChrValue;
				if (currentChr == lcLetter || currentChr == ucLetter) { // forbidden character -> terminate the current lipogram // if non-ASCII chars shall not be skipped but rather split, check also for !isAscii(iChar)
					maxLength = updateLongestLipograms(strBuffer,
							nrOfIgnoredChrs, maxLength, longestLipograms);
					strBuffer = new StringBuffer();
					nrOfIgnoredChrs = 0;
				} else if (legalChar (currentChr)) { // valid character -> append to current lipogram
					strBuffer.append(currentChr);
				} else { // irrelevant character
					strBuffer.append(currentChr);
					nrOfIgnoredChrs++;
				}
			}

			updateLongestLipograms(strBuffer, nrOfIgnoredChrs, maxLength, longestLipograms); // in case the longest lipogram is at the very end of the file
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return longestLipograms;
	}

	/**
	 * Checks whether the passed <code>lipogram</code> is longer than or equally long as the ones stored in the <code>longestLipograms</code>. If so, the list is updated.
	 * 
	 * @param lipogram
	 * @param nrOfIgnoredChrs
	 * @param maxLength
	 * @param longestLipograms
	 * @return the length of the longest lipogram
	 */
	private static int updateLongestLipograms(StringBuffer lipogram,
			int nrOfIgnoredChrs, int maxLength, List<String> longestLipograms) {
		if (lipogram.length() > 0 && lipogram.length() >= maxLength && ((((double) nrOfIgnoredChrs/lipogram.length())) < THRESHOLD)) { // set new longest lipogram if longer and less than 30% of characters have been ignored
			if (lipogram.length() > maxLength) { // delete the lipograms stored so far
				maxLength = lipogram.length();
				longestLipograms.clear();
			}
			longestLipograms.add(lipogram.toString());
		}
		return maxLength;
	}
	

}
