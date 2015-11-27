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


public class Task1IR {    
	
	static void parseJsonFile(File file, boolean isTest) throws Exception{
		FileReader f = new FileReader(file);
		BufferedReader br = new BufferedReader(f);
		String s = "";
		JSONParser parser=new JSONParser();
		
		while((s=br.readLine())!=null){			
			try{	
			  Object obj= parser.parse(s);
			  if(isTest)
				  EvaluateReview.buildTestReview(obj);
			  else
				  TrainReview.buildHashCategory(obj); 
			 }
			catch(Exception e){}
			
		}
	}
    
    public static void main(String args[]) throws Exception{
    	GenerateIndex.buildSetUp();
    	GenerateIndex.setDirPath("corpus1");
    	GenerateIndex.setFileExt("review_training.json");
    	GenerateIndex.genIndex(GenerateIndex.getDirPath(), "json", new StandardAnalyzer());
    	
    	System.out.println("Review Training Index created...");
    	
		TrainReview.initSetUp();
		TrainReview.buildDataModel();
		
		System.out.println("Data Model created...");
		GenerateIndex.genIndex(GenerateIndex.getDirPath(), "text", new StandardAnalyzer());
		
		System.out.println("Search Index created...");
		TrainReview.hashReview.clear();
		EvaluateReview.initSetUp();
		EvaluateReview.evalAlgos();
		
		float precision = (float)EvaluateReview.truePos/EvaluateReview.predictedDocs;
		float recall = (float)EvaluateReview.truePos/EvaluateReview.relevantDocs;
		
		System.out.println("Precision: " + precision);
		System.out.println("Recall: " + recall);
		
		int beta = 2;
		float f2_measure = (1 + (float)Math.pow(beta,2)) * precision * recall / ((float)Math.pow(beta,2) * precision + recall);
		System.out.println("F2 Measure: " + f2_measure);
	}
	
}

