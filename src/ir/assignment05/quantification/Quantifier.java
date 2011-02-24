package ir.assignment05.quantification;

import ir.assignment05.index.CompleteBook;
import ir.assignment05.utils.Stemmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Map.Entry;

public class Quantifier {
	//TODO BUG with stopwords and stemming adventure occurs suddenly 0 times and not 2
	private static final String TOKEN_DELIMITER = " \t\n\r\f\".,:;-/!?'~@#*+§$%&()=`´{[]}|<>";
	private static final String DEFAULT_STOPWORDS_FILE = "stopwords.txt";
	private static final String FILE_ENCODING = "UTF-8";
	private static final String SPECIAL_CHARS = "[!\"§$%&/()=?`´{}\\[\\]\\^°*+~'#-_.:,;<>|]+";
	private  Stemmer stemmer;
	private  Set<String> stopwords;

	Quantifier(){
		this.stemmer = new Stemmer();
		this.stopwords = loadStopwords();
	}
	
	
	private HashMap<String, Integer> fillWordFrequencies(
			StringTokenizer tokenizer) {
		HashMap<String, Integer> wordFrequencies = new HashMap<String, Integer>();
		while (tokenizer.hasMoreElements()) {
			String rawToken = tokenizer.nextToken();
			String token = this.stem(rawToken);
			if (this.isValidToken(token)){
				if (wordFrequencies.containsKey(token)){
					wordFrequencies.put(token, wordFrequencies.get(token) + 1);
				}
				else wordFrequencies.put(this.stem(token), 1);
			}
		}
		return wordFrequencies;
	}

	
	private HashMap<String, Integer> fillWordFrequenciesWOStemmingAndStopwords(
			StringTokenizer tokenizer) {
		HashMap<String, Integer> wordFrequencies = new HashMap<String, Integer>();
		while (tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
			//String token = this.stem(rawToken);
			//if (this.isValidToken(token)){
			if (wordFrequencies.containsKey(token)){
				wordFrequencies.put(token, wordFrequencies.get(token) + 1);
			}
			else wordFrequencies.put(token, 1);
			//}
		}
		return wordFrequencies;
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

	
	public static void main(String[] args) throws IOException {
		Quantifier quantifier = new Quantifier();
		//right now books are processed one by one
		HashMap<String, Integer> wordFrequencies = new HashMap<String, Integer>();
		HashMap<String, Integer> mostFrequent = new HashMap<String, Integer>();
		int smallestValue;
		int newSmallestValue;
		ArrayList<String> smallestEntries = new ArrayList<String>();




		if (args.length < 1) {
			System.out.println("Please specify the file where the book is stored.");
			return;
		}
		
		File bookFile = new File(args[0]);
		CompleteBook book = new CompleteBook(bookFile);
		StringTokenizer tokenizer = new StringTokenizer(book.getContent().toLowerCase(),TOKEN_DELIMITER);
		
		
		//total words of book
		System.out.println("Total words of " + args[0] + " is " + tokenizer.countTokens());

		//wordFrequencies = quantifier.fillWordFrequencies(tokenizer);
		wordFrequencies = quantifier.fillWordFrequenciesWOStemmingAndStopwords(tokenizer);
		
		
		
		// occurences of love
		System.out.println("The word love occured " + 
					(wordFrequencies.containsKey("love") ? wordFrequencies.get("love") : 0) + " times.");
		
		// occurences of adventure
		System.out.println("The word adventure occured " + 
					(wordFrequencies.containsKey("adventure") ? wordFrequencies.get("adventure") : 0) + " times.");
		

		
		// top 5 most frequent words
		smallestValue = Integer.MAX_VALUE;
		
		for (Entry<String, Integer> entry : wordFrequencies.entrySet()) {
			
			smallestEntries.clear();
			newSmallestValue = Integer.MAX_VALUE;

			//fill mostFrequent hashmap in the beginning
			if (mostFrequent.size()< 5) {
				mostFrequent.put(entry.getKey(), entry.getValue());
				if (smallestValue > entry.getValue()) smallestValue = entry.getValue();
			}

			//update top5 and smallestValue if necessary
			//if the smallest value in the top5 occurs multiple times, all words are kept
			else if (entry.getValue() > smallestValue) {
					mostFrequent.put(entry.getKey(), entry.getValue());
						
					for (Entry<String, Integer> currentEntry : mostFrequent.entrySet()) {
						if (currentEntry.getValue() == smallestValue){
							smallestEntries.add(currentEntry.getKey());
						}
						else if (currentEntry.getValue() < smallestValue){
							smallestEntries.clear();
							smallestEntries.add(currentEntry.getKey());
							newSmallestValue = smallestValue;
							smallestValue = currentEntry.getValue();
						}
						else if (currentEntry.getValue()< newSmallestValue){
							newSmallestValue = currentEntry.getValue();
						}
					}

					if ((mostFrequent.size() - smallestEntries.size()) == 5){
						for (String key: smallestEntries){
							mostFrequent.remove(key);
						}
						smallestValue = newSmallestValue;
					}
			}

			else if(entry.getValue() == smallestValue){
				mostFrequent.put(entry.getKey(), entry.getValue());
			}
		}
		
		System.out.println("the top 5 words are: ");
		//TODO sort results
		for (Entry<String, Integer> entry : mostFrequent.entrySet()) {
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
 
	}
	
}
