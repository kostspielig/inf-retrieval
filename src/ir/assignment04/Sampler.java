package ir.assignment04;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Sampler {

	private static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * Extracts a sample from a text file. Takes input file path, output file path, offset, and desired number of lines as input.
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws NumberFormatException, IOException {
		extractSample(new File(args[0]), new File(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]));
	}

	private static void extractSample(File in, File out, Integer offset, Integer size) throws IOException {
		if (!in.isDirectory()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(in), DEFAULT_ENCODING));
			
			File parentDir = out.getParentFile();
			if(! parentDir.exists()) {
				parentDir.mkdirs();
			}
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), DEFAULT_ENCODING));
			
			String line;
			int limit = offset + size;
			int counter = 1;
			while ((line = br.readLine()) != null) {
				if (counter >= offset && counter <= limit) {
					bw.write(line);
					bw.newLine();
				}
				counter++;
			}
			
			bw.close();
			br.close();
		}
	}

}
