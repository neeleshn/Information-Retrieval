package com;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;

public class Indexer {
	public static HashMap<String, HashMap<Integer, Integer>> invertedIndexMap = new HashMap<String, HashMap<Integer, Integer>>();
	public static HashMap<Integer, Integer> docCountMap = new HashMap<Integer, Integer>();

	public static void main(String[] args) throws IOException {
		String readfile = null;
		String outfile = null;
		try{
			readfile = args[0];
			outfile = args[1];
		} catch(Exception e){
			readfile = "tccorpus.txt";
			outfile = "index.out";
		}
		BufferedReader br = new BufferedReader(new FileReader(readfile));
		String eachLine = br.readLine();
		int docId = 0;
		Double d = 0.0;
		int wordCounter = 0;
		while(eachLine != null){
			String[] allWords = eachLine.split(" ");
			if(eachLine.charAt(0) == '#' && eachLine.charAt(1) == ' '){
				if(docId != 0){
					docCountMap.put(docId, wordCounter);
				}
				docId = Integer.parseInt(allWords[1]);
				wordCounter = 0;
			} else {
				for(int i=0; i<allWords.length; i++){
					try{
						d = Double.parseDouble(allWords[i]);
						//System.out.println(d);
					} catch (Exception e){
						wordCounter++;
						if(invertedIndexMap.containsKey(allWords[i])){
							HashMap<Integer, Integer> docMap = invertedIndexMap.get(allWords[i]);
							if(docMap.containsKey(docId)){
								int wordCount = docMap.get(docId);
								docMap.put(docId, ++wordCount);
								invertedIndexMap.put(allWords[i], docMap);
							} else {
								docMap.put(docId, 1);
								invertedIndexMap.put(allWords[i], docMap);
							}
						} else {
							HashMap<Integer, Integer> docMap = new HashMap<Integer, Integer>();
							docMap.put(docId, 1);
							invertedIndexMap.put(allWords[i], docMap);
						}
					}
				}
			}
			eachLine = br.readLine();
		}
		docCountMap.put(docId, wordCounter);
		br.close();
		
		String cur_dir = System.getProperty("user.dir");
		FileOutputStream fos = new FileOutputStream(cur_dir + "//"+outfile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(invertedIndexMap);
		oos.writeObject(docCountMap);
		oos.close();
		fos.close();
		System.out.println("***The End***");
	}

}
