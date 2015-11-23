package com;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class Bm25  {
	
	public static final Double k1 = 1.2;
	public static final Double k2 = 100.0;
	public static final Double b = 0.75;
	public static final Double ri = 0.0;
	public static final Double R = 0.0;
	
	public static HashMap<String, HashMap<Integer, Integer>> invIndexMap = new HashMap<String, HashMap<Integer,Integer>>();
	public static HashMap<String, HashMap<Integer, Integer>> queryIndexMap = new HashMap<String, HashMap<Integer,Integer>>();
	public static HashMap<Integer, Integer> docCountMap = new HashMap<Integer,Integer>();
	public static HashMap<Integer, Double> bm25Map = new HashMap<Integer, Double>();
		
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		String indexfile = null;
		String queryfile = null;
		int topDocs = 100;
		String outfile = null;
		
		try{
			indexfile = args[0];
			queryfile = args[1];
			topDocs = Integer.parseInt(args[2]);
			System.out.println(args[3]);
			outfile = args[4];
		} catch(Exception e){
			indexfile = "index.out";
			queryfile = "queries.txt";
			topDocs = 100;
			outfile = "results.eval";
		}
		
		String cur_dir = System.getProperty("user.dir"); 
		FileInputStream fis = new FileInputStream(cur_dir+"/"+indexfile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		invIndexMap = (HashMap) ois.readObject();
		docCountMap = (HashMap) ois.readObject();
		
		int tokenCount = 0;
		int totalDocLength = 0;
		for (HashMap.Entry<String, HashMap<Integer, Integer>> entry1 : invIndexMap.entrySet()) {
			for (HashMap.Entry<Integer, Integer> entry2 : entry1.getValue().entrySet()) {
		    	totalDocLength+=entry2.getValue();
		    }
			tokenCount++;
		}
		
		
		Double avdl = totalDocLength*1.0/tokenCount;
		BufferedReader br = new BufferedReader(new FileReader(queryfile));
		String eachLine = br.readLine();
		PrintWriter writer = new PrintWriter(outfile, "UTF-8");
		int queryId = 0;
		while (eachLine != null) {
			queryId++;
			String[] splitEachLine = eachLine.split(" ");
			for(String eachWord : splitEachLine) {
				queryIndexMap.put(eachWord, invIndexMap.get(eachWord));
			}

			for (HashMap.Entry<String, HashMap<Integer, Integer>> eachQueryWord : queryIndexMap.entrySet()) {
				HashMap<Integer, Integer> DocPosMap = eachQueryWord.getValue();
				for (HashMap.Entry<Integer, Integer> eachDoc : DocPosMap.entrySet()) {
					int fi = eachDoc.getValue();
					int N = tokenCount;
					int ni = eachQueryWord.getValue().size();
					Double qfi = 0.0;
					for(String eachWord : splitEachLine) {
						if(eachWord.equals(eachQueryWord.getKey()))
							qfi++;
					}
					
					Double K = k1*(1-b)+b*(docCountMap.get(eachDoc.getKey())*1.0/avdl);
					Double nuA = ri+0.5;
					Double nuB = (R-ri+0.5);
					Double deA = (ni-ri+0.5);
					Double deB = (N-ni-R+ri+0.5);
					Double term1 = Math.log((nuA/nuB)/(deA/deB));
					Double term2 = (k1+1)*fi*1.0/(K+fi);
					Double term3 = (k2+1)*qfi*1.0/(k2+qfi);
					Double total = term1*term2*term3;
					
					if(bm25Map.containsKey(eachDoc.getKey())){
						Double total1 = bm25Map.get(eachDoc.getKey());
						total1+=total;
						bm25Map.put(eachDoc.getKey(), total1);
					} else {
						bm25Map.put(eachDoc.getKey(), total);
					}
					
				}
			}
			
			List<HashMap.Entry<Integer, Double>> list =
					new LinkedList<HashMap.Entry<Integer, Double>>(bm25Map.entrySet());

			Collections.sort(list, new Comparator<HashMap.Entry<Integer, Double>>() {
				public int compare(HashMap.Entry<Integer, Double> o1, HashMap.Entry<Integer, Double> o2) {
					return (o2.getValue()).compareTo(o1.getValue());
				}
			});

			int counter = 1;
			for(HashMap.Entry<Integer, Double> entry:list){
				writer.println(queryId+" \t Q0 \t "+entry.getKey()+" \t "+counter+" \t "+entry.getValue()+" \t unidane");
				//System.out.println(queryId+" Q0 "+entry.getKey()+" "+counter+" "+entry.getValue()+" unidane");
				if(counter==topDocs){
					break;
				}
				counter++;
			}
			bm25Map.clear();
			
			System.out.println(queryId);
			eachLine = br.readLine();
		}
		ois.close();
		br.close();
		writer.close();
	}
}