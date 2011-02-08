package ir.assignment04;

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
public class Email {

	private static final String FILE_ENCODING = "UTF-8";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String HEADER_END = "X-FileName:.*";
	private static final Pattern SUBJECT_LINE = Pattern.compile("Subject:\\s((([Rr][eE])|([Ff][Ww][Dd])):\\s)*(.*)");
	private String header;
	private String body;
	private String subject;
	
	public Email(File f){
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
					String subject = extractSubject(line);
					if (subject != null) {
						this.subject = subject;
					}
				} else {
					bodyBuilder.append(line);
					bodyBuilder.append(LINE_SEPARATOR);
				}
			}
			
			this.header = headerBuilder.toString();
			this.body = bodyBuilder.toString();
			
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String extractSubject(String line) {
		String result = null;
		Matcher m = SUBJECT_LINE.matcher(line);
		if(m.find()) {
			result = m.group(5);
		}
		return result;
	}

	public String getSubject() {
		return this.subject;
	}
	
	public String getBody() {
		return this.body;
	}
	

	
	public static void main(String[] args) {
		File f = new File("C:\\Users\\kostspielig\\Documents\\InfoRetrieval\\Ass4\\maildir\\allen-p\\inbox\\1");
		Email e = new Email(f);
		
		System.out.println("Header: " + e.header);
		System.out.println("Subject: " + e.subject);
		System.out.println("Body: " + e.body);
	}
	
	
}
