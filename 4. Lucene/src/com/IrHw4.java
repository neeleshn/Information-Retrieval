package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.codecs.lucene45.Lucene45DocValuesConsumer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.SingleTermsEnum;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.w3c.dom.html.HTMLFieldSetElement;

public class IrHw4 {

	public static void main(String[] args) throws IOException, ParseException {
		
		String[] searchQueries = {"portable operating systems",
		                          "code optimization for space efficiency",
		                          "parallel algorithms",
		                          "parallel processor in information retrieval"};
		ArrayList<File> queue = new ArrayList<File>();
		IndexWriter writer;
		Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_47);
		String cur_dir = System.getProperty("user.dir");
		System.out.println(cur_dir);
		String indexDir = cur_dir+"/index";
		
		FSDirectory dir = FSDirectory.open(new File(indexDir));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47,analyzer);
		writer = new IndexWriter(dir, config);
		
		File f = new File(cur_dir+"/cacm");
		
		for(File eachFile : f.listFiles()){
			queue.add(eachFile);
		}
		
		for (File eachFile : queue) {
		    FileReader fr = null;
		    try {
				Document doc = new Document();
				fr = new FileReader(eachFile);
				String fileContent = Jsoup.parse(eachFile, null).text();
				doc.add(new TextField("contents", fileContent, Field.Store.YES));
				doc.add(new StringField("path", eachFile.getPath(), Field.Store.YES));
				doc.add(new StringField("filename", eachFile.getName(),Field.Store.YES));
				writer.addDocument(doc);
				//System.out.println("Added: " + eachFile);
		    } catch (Exception e) {
		    	System.out.println("Could not add: " + eachFile);
		    } finally {
		    	fr.close();
		    }
		}
		queue.clear();
		writer.close();
		
		IndexReader reader = DirectoryReader.open(dir);
		Terms terms = SlowCompositeReaderWrapper.wrap(reader).terms("contents");
		TermsEnum iterator = terms.iterator(null);
		BytesRef byteRef = null;
		HashMap<String, Long> termMap = new HashMap<String, Long>();
        while((byteRef = iterator.next()) != null) {
            //int docFreq = iterator.docFreq();
            //System.out.println(term+" ---- "+docFreq);
            //long docCount = reader.docFreq(termInstance);
			//System.out.println(term + " Term Frequency " + termFreq + " - Document Frequency " + docCount);
        	String term = byteRef.utf8ToString();
            Term termInstance = new Term("contents", term);
			long termFreq = reader.totalTermFreq(termInstance);
			termMap.put(term, termFreq);
            //System.out.println(term + " - " + termFreq);
        }
		
        
        List<HashMap.Entry<String, Long>> list =
				new LinkedList<HashMap.Entry<String, Long>>(termMap.entrySet());

		Collections.sort(list, new Comparator<HashMap.Entry<String, Long>>() {
			public int compare(HashMap.Entry<String, Long> o1, HashMap.Entry<String, Long> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		
		PrintWriter pw1 = new PrintWriter("termFrequency.txt", "UTF-8");
		//PrintWriter pw2 = new PrintWriter("termFrequency2.txt", "UTF-8");
		//PrintWriter pw3 = new PrintWriter("termFrequency3.txt", "UTF-8");
		int counter = 1;
		for(HashMap.Entry<String, Long> entry:list){
			pw1.println(entry.getKey()+" -\t "+entry.getValue());
			//System.out.println(entry.getKey()+" -\t "+entry.getValue());
			//pw2.println(counter);
			//pw3.println(entry.getValue());
			counter++;
		}
		pw1.close();
        //pw2.close();
        //pw3.close();
        
        PrintWriter pw = new PrintWriter("top100.txt", "UTF-8");
		for(String eachQuery : searchQueries){
			try {
				IndexSearcher searcher = new IndexSearcher(reader);
				TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
				Query q = new QueryParser(Version.LUCENE_47, "contents",analyzer).parse(eachQuery);
				searcher.search(q, collector);
				ScoreDoc[] hits = collector.topDocs().scoreDocs;
				pw.println(eachQuery);
				System.out.println(eachQuery + " - Found " + hits.length + " hits.");
				for (int i = 0; i < hits.length; ++i) {
				    int docId = hits[i].doc;
				    Document d = searcher.doc(docId);
				    //System.out.println((i + 1) + ". " + d.get("path") + " score=" + hits[i].score);
				    pw.println((i + 1) + ". " + docId + " - " + hits[i].score);
				}
		    } 
			catch (Exception e) {
				System.out.println("Error searching " + eachQuery + " : " + e);
				break;
		    }
		}
		pw.close();
	}
}
