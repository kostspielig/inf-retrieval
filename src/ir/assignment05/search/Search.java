package ir.assignment05.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * CS 121 Information Retrieval <br />
 * Assignment 05 <br />
 * Part 3 <br /><br />
 * 
 * @author María Carrasco
 * @author Fabian Lindenberg
 * @author Lea Voget
 */
public class Search {
	
	private static final String SEPARATOR = "\t";
	private static final String FILE_ENCODING = "UTF-8";
	
	private Map<Integer, String> idToNameMapping;
	
	public Search(String mappingFilePath) {
		this.idToNameMapping = readIdToNameMapping(mappingFilePath);
	}
	
	public PriorityQueue<SearchResult> search(Query q, RankingMethod method) {
		return method.search(q);
	}
	
	
	public String prettyPrintResults(PriorityQueue<SearchResult> results) {
		SearchResult sr;
		int precision = 100000;
		
		StringBuilder strBuilder = new StringBuilder();
		int rank = 1;
		while ((sr = results.poll()) != null) {
			strBuilder.append(rank);
			strBuilder.append(".\t");
			String docName = idToNameMapping.get(sr.getDocID());
			if (docName != null) {
				strBuilder.append(docName);
				strBuilder.append(" ");
			}
			strBuilder.append("(id:");
			strBuilder.append(sr.getDocID()); 
			strBuilder.append(")\tScore: ");
			strBuilder.append((double) Math.round(sr.getScore()*precision)/(double)precision);
			strBuilder.append("\n");
			rank++;
		}
		
		return strBuilder.toString();
	}


	private Map<Integer, String> readIdToNameMapping(String filepath) {
		Map<Integer, String> mapping = new HashMap<Integer, String>();
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(new File(filepath)), FILE_ENCODING));
			
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(SEPARATOR);
				mapping.put(Integer.valueOf(parts[0]), parts[1]);
			}
			
			return mapping;
		} catch (Exception e) {
			// error reading file, return empty map instead
			return new HashMap<Integer, String>();
		}
	}


	public static void main(String[] args) {
		String indexFilePath = args[0];
		String idMappingFilePath = args[1];
		int corpusSize = 0;
		try {
			corpusSize = Integer.valueOf(args[2]);
		} catch (NumberFormatException e) {
			System.out.println("Please specify the corpus size.");
			System.exit(1);
		}
		
		Search s = new Search(idMappingFilePath);
		
		RankingMethod m1 = new TfIdfRanker(indexFilePath, corpusSize);
		RankingMethod m2 = new CosineSimRanker(indexFilePath, corpusSize); // TODO: order is inversed
		RankingMethod m3 = new ProximityRanker(indexFilePath, corpusSize);
		RankingMethod[] methods = {m1,m2,m3};
		
		String[] queries = {"summer night",
							"great expectation",
							"mystic conversation",
							"sentimental reasons",
							"fall in love",
							"sister mother father"};
		
		for (String queryString : queries) {
			QueryBuilder qb = new QueryBuilder ();
			Query q = qb.construct(queryString);
			
			System.out.println("Query: " + queryString + "\n");
			
			for (RankingMethod m : methods) {
				System.out.println(m.getName());
				PriorityQueue<SearchResult> results = s.search(q, m);
				System.out.println(s.prettyPrintResults(results));
			}
			System.out.println("#######################\n");
		}
	}
}


