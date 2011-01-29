package ir.assignment03;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


public class PostProcessor {

	private static final String SEPARATOR = "\t";
	private static final String FILE_ENCODING = "UTF-8";

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Please specify the input folder and the output path.");
			return;
		}
		
		String input = args[0];
		String output = args[1];
		System.out.println("checking");
		PostProcessor proc = new PostProcessor();
		proc.sortSeparateFiles(input);
		
		proc.mergeFiles(input, output);

	}
	
	private void sortSeparateFiles(String in) {
		File dir = new File(in);
		if (dir.isDirectory()) {
			String[] fileNames = dir.list();
			for (String fn : fileNames) {
				File file = new File(dir.getAbsolutePath() + File.separator + fn);
				sortFile(file);
			}
		}
	}

	private void sortFile(File file) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), FILE_ENCODING));
			SortedMap<String,Integer> sorted = new TreeMap<String,Integer>();
			String line;
			while ((line=br.readLine()) != null) {
				String[] pair = line.split(SEPARATOR);
				sorted.put(pair[0],Integer.valueOf(pair[1]));
			}
			br.close();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), FILE_ENCODING));
			for (Map.Entry<String, Integer> pair : sorted.entrySet()) {
				bw.write(pair.getKey() + SEPARATOR + pair.getValue());
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private void mergeFiles(String input, String output){
		try {
			File dir = new File(input);
			if (dir.isDirectory()) {
				String[] fileNames = dir.list();
				ArrayList<BufferedReader> readers = new ArrayList<BufferedReader>(fileNames.length);
				for (String fn : fileNames) {
					File file = new File(dir.getAbsolutePath() + File.separator + fn);

					readers.add(new BufferedReader(new InputStreamReader(
							new FileInputStream(file), FILE_ENCODING)));
				}
				
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(output)), FILE_ENCODING));
				
				String[][] currentRecords = new String[fileNames.length][2];
				String smallestStem = null;
				int countOfSmallestStem = 0;
				List<Integer> idxOfSmallestTerms = new LinkedList<Integer>();
				List<Integer> currentFiles = new ArrayList<Integer>();
				
				int nFiles = fileNames.length;
				
				for (int i = 0; i < nFiles; i++){
					idxOfSmallestTerms.add(i);
					currentFiles.add(i);
				}
				while (!currentFiles.isEmpty()) {
					for (int idx : idxOfSmallestTerms) {
						String line = readers.get(idx).readLine();
						if (line == null){
							currentFiles.remove(currentFiles.indexOf(idx));
						}else
							currentRecords[idx] = line.split(SEPARATOR);
					}
					if (currentFiles.isEmpty())
						break;
					idxOfSmallestTerms.clear();
					smallestStem = currentRecords[currentFiles.get(0)][0];

					for (Integer arr : currentFiles){
						int cmp = smallestStem.compareTo(currentRecords[arr][0]); 
						if ( cmp > 0 ){ 
							smallestStem = currentRecords[arr][0];
							idxOfSmallestTerms.clear();
							idxOfSmallestTerms.add(arr);
							countOfSmallestStem = Integer.valueOf(currentRecords[arr][1]);
						} else if(cmp == 0){
							idxOfSmallestTerms.add(arr);
							countOfSmallestStem += Integer.valueOf(currentRecords[arr][1]);
						}
					}

					bw.write(smallestStem + SEPARATOR + countOfSmallestStem);

					bw.newLine();
					countOfSmallestStem = 0;
				}
				
				/*boolean notEmpty = true;
				int j=0;
				while (notEmpty) {
					for (int i=0; i<fileNames.length; i++) {
						String[] record = currentRecords[i];
						if (record == null || record[0] == null) {
							String line = readers.get(i).readLine();
							if (line == null) {
								notEmpty = true;
								continue;
							}
							notEmpty = true || notEmpty;
							record = line.split(SEPARATOR);
							currentRecords[i] = record;
						}
						int comparison = smallestStem != null ? record[0].compareTo(smallestStem) : -1;
						if (comparison <= 0) {
							countOfSmallestStem += Integer.valueOf(record[1]);
							idxOfSmallestTerms.add(i);
						}
						if ( comparison < 0) {
							smallestStem = record[0];
						}
					} // end for
					bw.write(smallestStem + SEPARATOR + countOfSmallestStem);
					System.out.println("written out: " + ++j);
					bw.newLine();
					for (int idx : idxOfSmallestTerms) {
						currentRecords[idx] = null;
					}
					idxOfSmallestTerms.clear();
				} // end While */
				
				for(BufferedReader br: readers){
					br.close();
				}
				bw.close();
			}
			
		}	 catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
