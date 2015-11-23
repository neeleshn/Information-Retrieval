import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.lang.Math;

public class PageRankMainV0 {

	public static HashMap<String, ArrayList<String>> inLinksMap = new HashMap<String,ArrayList<String>>();
	public static HashMap<String, ArrayList<String>> outLinksMap = new HashMap<String,ArrayList<String>>();
	public static HashMap<String, Double> pageRankMap = new HashMap<String, Double>();
	public static HashMap<String, Double> pageRankMapClone = new HashMap<String, Double>();
	public static HashSet<String> allNodes = new HashSet<String>();
	public static HashSet<String> outLinkNodes = new HashSet<String>();
	public static HashSet<String> sinkNodes = new HashSet<String>();
	public static HashSet<String> sourceNodes = new HashSet<String>();
	public static ArrayList<Double> perplexList = new ArrayList<Double>();
	public static ArrayList<String> q1Ans = new ArrayList<String>();
	public static double d = 0.85;
	public static int flag = 0;
	public static int counter = -1;
	public static int fileCreated = 0;
	
	
	public static boolean isConverged() throws FileNotFoundException, UnsupportedEncodingException{
		//System.out.println("in converged");
		double entropy = 0;
		for ( String s : allNodes){
			double pageRank = pageRankMap.get(s);
			double logger = Math.log(pageRank)/Math.log(2.0);
			entropy += pageRank*logger;
		}
		entropy = -1*entropy;
		double perplexity = Math.pow(2, entropy);
		perplexList.add(perplexity);
		if(perplexList.size()<4) {
			return false;
		}
		double perplex1 = Math.abs(perplexList.get(perplexList.size()-1) - perplexList.get(perplexList.size()-2));
		double perplex2 = Math.abs(perplexList.get(perplexList.size()-2) - perplexList.get(perplexList.size()-3));
		double perplex3 = Math.abs(perplexList.get(perplexList.size()-3) - perplexList.get(perplexList.size()-4));
		if(perplex1<1 && perplex2 <1 && perplex3<1) {
			PrintWriter writer = new PrintWriter("Q2Ans.txt", "UTF-8");
			for(int i = 0; i<perplexList.size(); i++){
				//System.out.println(perplexList.get(i));
				writer.println(perplexList.get(i));
			}
			writer.close();
			ArrayList<String> q3a = new ArrayList<String>();
			q3a.add("Top 50 Pages sorter by their PageRank");
			for(int i=0; i<50; i++){
				double maxPageRank = 0;
				String maxPrKey = null;
				for (HashMap.Entry<String, Double> entry : pageRankMapClone.entrySet()) {
					if(entry.getValue() > maxPageRank) {
						maxPageRank=entry.getValue();
						maxPrKey = entry.getKey();
					}
				}
				q3a.add(maxPrKey+" - "+maxPageRank);
				pageRankMapClone.put(maxPrKey, 0.0);
			}
			
			q3a.add("");
			q3a.add("");
			q3a.add("Top 50 Pages sorter by their count of Inlinks");
			HashMap<String, ArrayList<String>> inLinksMapClone = new HashMap<String, ArrayList<String>>(inLinksMap);
			for(int i=0; i<50; i++){
				int maxinLinks = 0;
				ArrayList<String> emptyArrayList = new ArrayList<String>();
				String maxinLinksKey = null;
				for (HashMap.Entry<String, ArrayList<String>> entry : inLinksMapClone.entrySet()) {
					if(entry.getValue().size() > maxinLinks) {
						maxinLinks=entry.getValue().size();
						maxinLinksKey = entry.getKey();
					}
				}
				q3a.add(maxinLinksKey+" - "+maxinLinks);
				inLinksMapClone.put(maxinLinksKey, emptyArrayList);
			}
			
			q3a.add("");
			q3a.add("");
			double sinkNodeSize = sinkNodes.size();
			double sourceNodesize = sourceNodes.size();
			double allNodesSize = allNodes.size();
			double inlinksprop = sourceNodesize/allNodesSize;
			double outlinksprop = sinkNodeSize/allNodesSize;
			
			q3a.add("Proportion of Pages with no in links is : "+inlinksprop);
			q3a.add("");
			q3a.add("");
			q3a.add("Proportion of Pages with no out links is : "+outlinksprop);
			q3a.add("");
			q3a.add("");
			
			double PrLessCount = 0.0;
			for (HashMap.Entry<String, Double> entry : pageRankMap.entrySet()) {
				if(entry.getValue() < 1.0/allNodes.size()) {
					PrLessCount++;
				}
			}
			double prLessProp = PrLessCount/allNodesSize;
			q3a.add("Proportion of pages whose PageRank is less than their initial, uniform values is : "+prLessProp);
			q3a.add("");
			q3a.add("");
			writer = new PrintWriter("Q3Ans.txt", "UTF-8");
			for(int i=0; i<q3a.size();i++){
				writer.println(q3a.get(i));
				//System.out.println(q3a.get(i));
			}
			writer.close();
			/*System.out.println("\n -- In links Map");
			for (HashMap.Entry<String, ArrayList<String>> entry : inLinksMap.entrySet()) {
				System.out.println(entry.getKey()+" - "+entry.getValue());
			}
			System.out.println("\n -- Out links Map");
			for (HashMap.Entry<String, ArrayList<String>> entry : outLinksMap.entrySet()) {
				System.out.println(entry.getKey()+" - "+entry.getValue());
			}
			System.out.println("\n -- All nodes");
			for(String s : allNodes)
				System.out.println(s);
			System.out.println("\n -- Sink nodes");
			for(String s : sinkNodes)
				System.out.println(s);
			System.out.println("\n -- Source nodes");
			for(String s : sourceNodes)
				System.out.println(s);
			*/
			for (HashMap.Entry<String, Double> entry : pageRankMap.entrySet()) {
				System.out.println(entry.getKey()+" - "+entry.getValue());
			}
			
			return true;
		}
		else return false;
	}
	
	public static boolean checkWhile() throws FileNotFoundException, UnsupportedEncodingException{
		if(flag == 0){
			counter++;
			if(counter >= 100)
				return false;
			else return true;
		} else {
			return !isConverged();
		}
	}
	
	public static void printOutput() throws FileNotFoundException, UnsupportedEncodingException{
		if(flag == 0){
			if(counter==0 || counter==9 || counter==99){
				double sum = 0;
				int counter1 = counter + 1;
				q1Ans.add("PageRank values after iteration "+counter1);
				for (HashMap.Entry<String, Double> entry : pageRankMap.entrySet()) {
					System.out.println(entry.getKey()+" - "+entry.getValue());
					q1Ans.add(entry.getKey()+" - "+entry.getValue());
					sum+=entry.getValue();
				}
				System.out.println(sum);
				q1Ans.add("");
			}
			if(counter == 99){
				PrintWriter writer = new PrintWriter("Q1Ans.txt", "UTF-8");
				for(int i=0;i<q1Ans.size();i++){
					writer.println(q1Ans.get(i));
				}
				writer.close();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		String type = null;
		String readfile = null;
		try{
			type=args[0];
			flag=1;
			readfile = "wt2g_inlinks.txt";
		} catch(Exception e){
			flag=0;
			readfile = "linkFile.txt";
		}
		
		BufferedReader br = new BufferedReader(new FileReader(readfile));
		String eachLine = br.readLine();
		HashSet<String> initInlinks = new HashSet<String>();
		while (eachLine != null) {
			String[] nodeArray = eachLine.split(" ");
			//System.out.println(nodeArray.length);
			ArrayList<String> inLinksArray = new ArrayList<String>();
			for(int i=1; i<nodeArray.length;i++){
				allNodes.add(nodeArray[i]);
				if(!inLinksArray.contains(nodeArray[i]))
					inLinksArray.add(nodeArray[i]);
			}
			inLinksMap.put(nodeArray[0], inLinksArray);
			initInlinks.add(nodeArray[0]);
			allNodes.add(nodeArray[0]);
			if(nodeArray.length == 1)
				sourceNodes.add(nodeArray[0]);
			eachLine = br.readLine();
		}
		for(String s: allNodes){
			if(!initInlinks.contains(s))
				sourceNodes.add(s);
		}
		br.close();
		//System.out.println("source nodes length : "+sourceNodes);
		
		for (HashMap.Entry<String, ArrayList<String>> entry : inLinksMap.entrySet()) {
		    String inLinkKey = entry.getKey();
		    //System.out.println(inLinkKey+" --");
		    ArrayList<String> inLinkValues = entry.getValue();
		    for (int i=0; i<inLinkValues.size();i++){
		    	ArrayList<String> outLinkValues = new ArrayList<String>();
		    	if(outLinksMap.get(inLinkValues.get(i)) != null){
		    		outLinkValues = outLinksMap.get(inLinkValues.get(i));
		    	}
		    	//System.out.println("-- "+outLinkValues);
		    	outLinkValues.add(inLinkKey);
		    	outLinkNodes.add(inLinkValues.get(i));
		    	outLinksMap.put(inLinkValues.get(i),outLinkValues);
		    }
		}
		
		for (HashMap.Entry<String, ArrayList<String>> entry : outLinksMap.entrySet()) {
			//System.out.println(entry.getKey()+" -- "+entry.getValue());
		}
		
		sinkNodes=new HashSet<String>(allNodes);
		sinkNodes.removeAll(outLinkNodes);
		//System.out.println("Sink nodes : "+sinkNodes);
		for ( String s : allNodes){
			//System.out.println(s);
			pageRankMap.put(s, 1.0/allNodes.size());
			pageRankMapClone.put(s, 1.0/allNodes.size());
		}
		
		while(checkWhile()) {
			double sinkPr = 0.0;
			for ( String s : sinkNodes){
				sinkPr += pageRankMap.get(s);
			}
			for ( String s : allNodes){
				double newRank = (1-d)/allNodes.size();
				newRank += d*sinkPr/allNodes.size();
				ArrayList<String> outLinks1 = new ArrayList<String>();
				if(inLinksMap.containsKey(s)){
					outLinks1 = inLinksMap.get(s);
				}	
				//System.out.println(s);
				//System.out.println(outLinks1);
				for(int j=0; j<outLinks1.size();j++){
					//System.out.println("----- "+outLinks1.get(j));
					//System.out.println("----- "+outLinksMap.get(outLinks1.get(j)));
					//System.out.println("----- "+pageRankMap.get(outLinks1.get(j)));
					//System.out.println("-----------------------------------------");
					newRank += d*pageRankMap.get(outLinks1.get(j))/outLinksMap.get(outLinks1.get(j)).size();
				}
				pageRankMapClone.put(s, newRank);
			}
			
			for ( String s : allNodes){
				pageRankMap.put(s, pageRankMapClone.get(s));
			}
			printOutput();
			
		}
		System.out.println("The End");
	}

}
