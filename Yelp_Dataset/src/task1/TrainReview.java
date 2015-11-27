package task1;

/* Name: Shrijit Pillai
 * Username: pillaish
 */

import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document; 
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class TrainReview {
	static IndexReader reader;
	static IndexSearcher searcher;
	static Analyzer analyzer;
	static QueryParser parser;
	static String curDir;
	static HashMap<String, List<String>> hashCategory;
	static HashMap<String, String> hashReview;

	static int maxDocs;

	
	static void initSetUp() throws IOException{
	    curDir = System.getProperty("user.dir");  
		reader = DirectoryReader.open(FSDirectory.open(Paths.get(curDir+"\\review_training.json")));
		searcher = new IndexSearcher(reader);
		analyzer = new StandardAnalyzer();
		parser = new QueryParser("REVIEW", analyzer);
		hashCategory = new HashMap<String, List<String>>();
		hashReview = new HashMap<String, String>();
		maxDocs = reader.maxDoc();
     }
	
	
	static void buildHashCategory(Object obj) throws Exception{ //Called from parseJsonFile in Task1.java
	    String business_id = "";
	    List<String> list = new ArrayList<String>();
	    
		JSONObject jObject = (JSONObject)obj;
		
		business_id = jObject.get("business_id") + "";
		JSONArray catObj = (JSONArray)jObject.get("categories");
		
		for(int i=0;i<catObj.size();i++)
			list.add(catObj.get(i)+"");
		
		hashCategory.put(business_id, list);
	}
		   
		
	static void buildHashReview() throws Exception{	
		String business_id = "";
		String review = "";
		
		for(int docId=0;docId<maxDocs;docId++){
			Document doc = searcher.doc(docId);
			
			business_id = doc.get("BUSID");
			review = doc.get("REVIEW");  
			
			List<String> list_category = hashCategory.get(business_id);
			
			for(String category : list_category){	
				if(hashReview.containsKey(category)){
				    hashReview.put(category, hashReview.get(category)+". "+review);
				    //System.out.println(hashReview.get(category));
				 }
				else
				   hashReview.put(category, review);	   
			}
			
		    list_category.clear();
		  }
		
	}
	
	
	static void buildDataModel() throws Exception{
		String fileName = curDir+"\\corpus1\\business_training.json";
		File file = new File(fileName);
		Task1IR.parseJsonFile(file, false);
	   
		buildHashReview();  // Hash of category to its review
		reader.close();
	}
	
	
}

