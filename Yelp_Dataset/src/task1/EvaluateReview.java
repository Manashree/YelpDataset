package task1;

/* Name: Shrijit Pillai
 * Username: pillaish
 */

import java.nio.file.Paths;
import java.io.File;
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
import org.apache.lucene.store.FSDirectory;

import org.json.simple.JSONObject;

public class EvaluateReview {
	public static IndexReader reader;
	public static IndexSearcher searcher;
	public static Analyzer analyzer;
	public static QueryParser parser;
	public static String curDir;
	public static HashMap<String, String> hashEval;
	static int truePos;
	static int relevantDocs;
	static int predictedDocs;
	
	public static void initSetUp() throws IOException{
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
    
    // Below method retrieves the top predicted categories based on the similarity algorithm used...
    public static void evalQuery(String queryStr, FileWriter fw, int n) throws Exception{
    	Query query = parser.parse(QueryParser.escape(queryStr));
    	TopDocs results = searcher.search(query, n);
    	String category = "";
    	float maxScore = 0f;
    	
		ScoreDoc[] hits = results.scoreDocs;
		
		if(fw!=null)
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
		   
		   if(fw!=null)
		     fw.write(category+" - "+hits[i].score+",   ");
		   
		   hashEval.put(category, category);
			   
		}
		
		if(fw!=null)
		  fw.write("\n");
		
    }
    
    // Below method computes the list of predicted and relevant categories by calling the above evalQuery method
    static void evalAlgos(String algo, int noTopDocs) throws Exception{
    	String query = "";
    	List<String> list_category = new ArrayList<String>();
    	FileWriter fw = new FileWriter(curDir+"\\output.txt");
    	
    	String fileName = curDir+"\\corpus1\\review_testing.json";
		File file = new File(fileName);
		
		Task1IR.parseJsonFile(file, 1, null);
		
		if(algo.equals("D"))
    	    searcher.setSimilarity(new DefaultSimilarity());
		else if(algo.equals("B"))
			searcher.setSimilarity(new BM25Similarity());
		else if(algo.equals("L"))
			searcher.setSimilarity(new LMDirichletSimilarity());
		
    	//query = "Hello, AZ Biltmore! When I heard my husband's cousin was having her wedding reception here, I was thrilled!\n\nIt's a beautiful resort! I love the architecture from Frank Lloyd Wright. The multiple fire pits make for a cozy and enjoyable visit with friends. \n\nThe ballroom was perfect! Nice sized dance floor, the wedding party was set up on a stage so everyone could have a view of the bride and groom. The dining service was excellent. \n\nThe Biltmore has it down and executes catering to parties seamless and smooth. The staff is very friendly, helpful, and respectful. I felt like a VIP the entire night.";
    	//evalQuery(query, fw);
    	
    	Set<String> set = TrainReview.hashReview.keySet();
    	Iterator<String> it = set.iterator();
    	
    	while(it.hasNext()){
    		String bus_id = it.next();
    		query = TrainReview.hashReview.get(bus_id);
    		evalQuery(query, fw, noTopDocs);
    		
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
    	}
    	
    	fw.close();
    	
     }
	
}

