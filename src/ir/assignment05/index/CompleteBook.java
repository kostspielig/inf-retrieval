package ir.assignment05.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CS 121 Information Retrieval <br />
 * Assignment 04 <br />
 * Part 3 <br /><br />
 * 
 * Represents an e-mail.
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 */
public class CompleteBook {

	private static final String FILE_ENCODING = "UTF-8";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String HEADER_END = "*** START OF THIS PROJECT GUTENBERG EBOOK.*";
	private String header;
	private String content;
	
	public CompleteBook(File f){
		separateFile(f);
	}

	/**
	 * Separates the file content into header, subject line, and body.
	 * 
	 * @param f
	 */
	private void separateFile(File f) {
		try {
			BufferedReader br = new BufferedReader(
										new InputStreamReader(
												new FileInputStream(f), FILE_ENCODING));
			
			StringBuilder headerBuilder = new StringBuilder();
			StringBuilder bodyBuilder = new StringBuilder();
			
			String line;
			boolean isHeader = true;
			while((line = br.readLine()) != null) {
				if (isHeader) {
					isHeader = !line.matches(HEADER_END);
					headerBuilder.append(line);
					headerBuilder.append(LINE_SEPARATOR);
				} else {
					bodyBuilder.append(line);
					bodyBuilder.append(LINE_SEPARATOR);
				}
			}
			
			this.header = headerBuilder.toString();
			this.content = bodyBuilder.toString();
			
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public String getContent() {
		return this.content;
	}
	

	
	public static void main(String[] args) {
		File f = new File("C:\\Users\\kostspielig\\Documents\\InfoRetrieval\\Ass4\\maildir\\allen-p\\inbox\\1");
		CompleteBook e = new CompleteBook(f);
		
		System.out.println("Header: " + e.header);
		System.out.println("Content: " + e.content);
	}
	
	
}
