package ir.assignment04;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * CS 121 Information Retrieval <br />
 * Assignment 04 <br />
 * Part 2 <br /><br />
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 *
 */
public class Quantifier {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Please specify the root directory of the enron mails.");
			return;
		}
		File rootDir = new File(args[0]);
		if (!rootDir.isDirectory()) {
			System.out.println("The specified path is not a directory.");
			return;
		}
		
		int numberTargeted = countTargetedPeople(rootDir);
		System.out.println("Number of people targeted in the enron data set: " + numberTargeted);

		int nrIndividualFiles = countIndividualFiles(rootDir);
		System.out.println("Number of individual data files: " + nrIndividualFiles);
		
		int nrSentFiles = countIndividualFiles(rootDir,"(.*[^A-Za-z])?sent([^A-Za-z].*)?");
		System.out.println("Number of sent data files: " + nrSentFiles);
		
		System.out.println("Number of data files in inboxes (=total-sent): " + (nrIndividualFiles-nrSentFiles));
		
		List<String> pWithLargestNrOfFiles = determinePersonsWithLargestNrOfFiles(rootDir, 10);
		System.out.println("The ten persons with the largest number of files: " + pWithLargestNrOfFiles);
	}

	/**
	 * Counts the people targeted, i.e. the number of folders in the root directory.
	 * 
	 * @param rootDir the root folder
	 * @return the number of direct subfolders
	 */
	private static int countTargetedPeople(File rootDir) {
		int result = 0;
		
		for (File f : rootDir.listFiles()) {
			if (f.isDirectory()) {
				result++;
			}
		}
		
		return result;
	}
	
	/**
	 * Counts the number of files recursively.
	 * 
	 * @param rootDir
	 * @return the number of individual files
	 */
	private static int countIndividualFiles(File rootDir) {
		return countIndividualFiles(rootDir, null, true, true);
	}

	/**
	 * Counts the number of files recursively considering only directories that match the passed regular expression.
	 * 
	 * @param rootDir
	 * @param dirRegEx 
	 * @return the number of individual files
	 */
	private static int countIndividualFiles(File rootDir, String dirRegEx) {
		return countIndividualFiles(rootDir, dirRegEx, false, true);
	}
	
	/**
	 * Recursive implementation
	 * 
	 * @param rootDir
	 * @param dirRegEx 
	 * @return the number of individual files
	 */
	private static int countIndividualFiles(File rootDir, String dirRegEx, boolean ignoreRegEx, boolean firstLevel) {
		int result = 0;
		
		for (File f : rootDir.listFiles()) {
			if (f.isDirectory() && (ignoreRegEx || firstLevel || f.getName().matches(dirRegEx))) {
				result += countIndividualFiles(f, dirRegEx, ignoreRegEx, false);
			} else {
				result++;
			}
		}
		
		return result;
	}

	/**
	 * Counts the number of files each person (i.e. direct subfolder of the root folder)
	 * stores and returns the names of the <code>limit</code> largest folders.<br /><br />
	 * 
	 * If multiple folders with rank <code>limit</code> exist, all are returned (i.e. the returned list can have more than <code>limit</code> entries). 
	 * 
	 * @param rootDir
	 * @param limit the limit of the ranking
	 * @return the names of the largest direct subfolders of rootDir
	 */
	private static List<String> determinePersonsWithLargestNrOfFiles(File rootDir, int limit) {
		// count number of files for each person, order according to number of files
		SortedMap<Integer, List<String>> namesSortedByNrFiles = new TreeMap<Integer,List<String>>(Collections.reverseOrder());
		for (File f : rootDir.listFiles()) {
			if (f.isDirectory()) {
				int nrFiles = countIndividualFiles(f);
				List<String> names = namesSortedByNrFiles.get(nrFiles);
				if (names == null) {
					names = new LinkedList<String>();
				}
				names.add(f.getName());
				namesSortedByNrFiles.put(nrFiles, names);
			}
		}
		
		// select the top names
		List<String> topNames = new LinkedList<String>();
		int count = 0;
		for (List<String> names : namesSortedByNrFiles.values()) {
			if (count < limit) {
				topNames.addAll(names);
				count += names.size();
			} else {
				break;
			}
		}
		return topNames;
	}
	
}
