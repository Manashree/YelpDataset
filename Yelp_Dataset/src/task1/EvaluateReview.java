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
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class EvaluateReview {
	static IndexReader reader;
	static IndexSearcher searcher;
	static Analyzer analyzer;
	static QueryParser parser;
	static String curDir;
	static HashMap<String, String> hashEval;
	static int truePos;
	static int relevantDocs;
	static int predictedDocs;
	
	static void initSetUp() throws IOException{
	    curDir = System.getProperty("user.dir");  
		reader = DirectoryReader.open(FSDirectory.open(Paths.get(curDir+"\\search_training.json")));
		searcher = new IndexSearcher(reader);
		analyzer = new StandardAnalyzer();
		parser = new QueryParser("REVIEW", analyzer);	
		hashEval = new HashMap<String, String>();
	}
	
	static void buildTestReview(Object obj){
		JSONObject jObject = (JSONObject)obj;
		
		String business_id = jObject.get("business_id") + "";
		String review_id = jObject.get("text") + "";
		
		if(!TrainReview.hashReview.containsKey(business_id))
		   TrainReview.hashReview.put(business_id, review_id);
	}
    
    // Below method writes the retrieved results to file based on the query type - short or long
    static void evalQuery(String queryStr, FileWriter fw) throws Exception{
    	Query query = parser.parse(QueryParser.escape(queryStr));
    	TopDocs results = searcher.search(query, 3);
    	String category = "";
    	float maxScore = 0f;
    	
		ScoreDoc[] hits = results.scoreDocs;
		fw.write("---Predicted---\n");
		
		for(int i=0;i<hits.length;i++){
		   Document doc=searcher.doc(hits[i].doc);
		   
		   /*if(i==0)
		      maxScore = hits[i].score;   
		   else{
			   if(hits[i].score < maxScore - 0.5)
				   continue;
			}*/
		   
		   category = doc.get("CATEGORY")+"";
		   fw.write(category+" - "+hits[i].score+",   ");
		   hashEval.put(category, category);
			   
		}
		fw.write("\n");
		
    }
    
    // Below method evaluates the query based on the algorithm
    static void evalAlgos() throws Exception{
    	String query = "";
    	int count = 0;
    	List<String> list_category = new ArrayList<String>();
    	FileWriter fw = new FileWriter(curDir+"\\output.txt");
    	
    	String fileName = curDir+"\\corpus1\\review_testing.json";
		File file = new File(fileName);
		
		Task1IR.parseJsonFile(file, true);
		
    	searcher.setSimilarity(new DefaultSimilarity());
    	//query = "Hello, AZ Biltmore! When I heard my husband's cousin was having her wedding reception here, I was thrilled!\n\nIt's a beautiful resort! I love the architecture from Frank Lloyd Wright. The multiple fire pits make for a cozy and enjoyable visit with friends. \n\nThe ballroom was perfect! Nice sized dance floor, the wedding party was set up on a stage so everyone could have a view of the bride and groom. The dining service was excellent. \n\nThe Biltmore has it down and executes catering to parties seamless and smooth. The staff is very friendly, helpful, and respectful. I felt like a VIP the entire night.";
    	//evalQuery(query, fw);
    	
    	Set<String> set = TrainReview.hashReview.keySet();
    	Iterator<String> it = set.iterator();
    	
    	while(it.hasNext()){
    		//if(count>100)
    		  //break;
    		
    		String bus_id = it.next();
    		query = TrainReview.hashReview.get(bus_id);
    		evalQuery(query, fw);
    		
    		fw.write("-----Actual----\n");
    		list_category = TrainReview.hashCategory.get(bus_id);
    		for(String cat : list_category){
    			if(hashEval.containsKey(cat))
    				truePos++;
    			
    			fw.write(cat+",   ");
    		}
    		
    		predictedDocs += hashEval.size();
    		relevantDocs += list_category.size();
    		hashEval.clear();
    		
    		fw.write("\n\n");
    		//count++;
    	}
    	
    	fw.close();
    	
     }
	
}

