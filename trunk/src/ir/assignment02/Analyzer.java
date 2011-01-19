package ir.assignment02;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.StyleContext.SmallAttributeSet;

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
		List<String> longestPalindrom = detectLongestPalindrom(DEFAULT_INPUT);
		List<String> longestLipograms = detectLongestLipogram(DEFAULT_INPUT, DEFAULT_LIPOGRAM_LETTER);

		// write results
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(new File(DEFAULT_OUTPUT)));
			//w.write(longestPalindrom);
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
		System.out.println("Runtime: " + (endTime - startTime));
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
	 * Detects the longest palindrom in the provided text file.
	 * 
	 * @param filePath the path to the text file
	 * @return the longest palindrom
	 */
	private static List<String> detectLongestPalindrom(String filePath) {
		
		String text = IO.getFileContent(filePath);
		List<String> longestPalindrom = new LinkedList<String>();
		StringBuffer pal = new StringBuffer(); 
		
		int textLength = text.length();
		int nrOfIgnoredChrs = 0; 
		int maxLength = 0;
		
		int i, j;
		i = j = 0;
		boolean first = true;
		for (int k = 1; k < textLength -1; k++ ) 
		{
			j = k + 1;
			i = k - 1;
			nrOfIgnoredChrs=0;
			first = true;
			
		 	while (i >= 0 && j < textLength-1)
		 	{
		 		char iChar = Character.toLowerCase(text.charAt(i));
		 		char jChar = Character.toLowerCase(text.charAt(j));
		 		char kChar = Character.toLowerCase(text.charAt(k));
		 		while(!legalChar(iChar) && i > 0){ //ignore character
		 			i--;
		 			nrOfIgnoredChrs++;
		 			iChar = Character.toLowerCase(text.charAt(i));
		 			}
		 		while(!legalChar(jChar) && j < textLength-1){
		 			j++;
		 			nrOfIgnoredChrs++;
		 			jChar = Character.toLowerCase(text.charAt(j));
		 		}

		 		if (first) {
		 			if(iChar == kChar ) {
		 				j = k;
		 				jChar = kChar;
		 			}
		 			first = false;
		 		}
		 		
		 		if (iChar == jChar){  
		 			j++; i--;
		 		} else break;
		 		
		 		maxLength = pal.length();
		 		int len = (j - (i+1));
		 		if (len >= maxLength && (((double) (nrOfIgnoredChrs/len)) < THRESHOLD)){
		 			pal = new StringBuffer();
		 			pal.append(text.substring(i+1,j));
		 			
		 			if ((j-(i+1)) > maxLength)
		 				longestPalindrom.clear();	
					longestPalindrom.add(pal.toString());
		 			
		 		}
		 		first = false;
		 	}
		}
		
		return longestPalindrom;
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
				if (currentChr == lcLetter || currentChr == ucLetter) { // forbidden character -> terminate the current lipogram
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
			if (lipogram.length() >= maxLength && ((((double) nrOfIgnoredChrs/lipogram.length())) < THRESHOLD)) { // set new longest lipogram if longer and less than 30% of characters have been ignored
				if (lipogram.length() > maxLength) { // delete the lipograms stored so far
					maxLength = lipogram.length();
					longestLipograms.clear();
				}
			longestLipograms.add(lipogram.toString());
		}
		return maxLength;
	}
	

}
