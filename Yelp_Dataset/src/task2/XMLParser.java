package task2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import task1.EvaluateReview;

public class XMLParser {
	static HashMap<String, String> hashFeature;
	static HashMap<String, Set<String>> hashOpinionList;
	static HashMap<String, Integer> hashFeatureFreq;
	static List<String> words;
	
	static void init(){
		hashFeature = new HashMap<String, String>();
		hashFeatureFreq = new HashMap<String, Integer>();
		hashOpinionList = new HashMap<String, Set<String>>();
		words = new ArrayList<String>();
	}
	
	static boolean isFeatureValid(String word) throws Exception{
		int count = 0;
		EvaluateReview.hashEval.clear();
		EvaluateReview.evalQuery(word, null);

		Set<String> set = EvaluateReview.hashEval.keySet();
		Iterator<String> it = set.iterator();
		
		while(it.hasNext()){
			String category = it.next();
			
			if(TestData.hashCat.containsKey(category)){
				count++;
				/*if(word.equals("I"))
				   System.out.println("Count: "+count+", Category: "+category);*/
				if(count>=2)
				  return true;
			}
		}
		
		return false;
	}
	
	static void processTokens(NodeList tokenList, boolean singleOpinion_YN) throws Exception{
		String feature = "";
		String opinion = "";
		
		for(int j=0;j<tokenList.getLength();j++){
    		Element token = (Element)tokenList.item(j);
    		String word = token.getElementsByTagName("word").item(0).getChildNodes().item(0).getNodeValue().toLowerCase();
    		String POS = token.getElementsByTagName("POS").item(0).getChildNodes().item(0).getNodeValue();
    		boolean result = false;
    		
    		if(POS.equals("NN") || POS.equals("NNS")){
    		   result = isFeatureValid(word);
    		   
    		   if(result){
    			 //System.out.println("Feature: "+word);  
    			 feature = word;
    		   }
    		}
    		
    		if(POS.equals("JJ") || POS.equals("JJS")){
    			opinion = word;
    		}
    		
    		if(!feature.equals("") && !opinion.equals("")){
    		  if(!hashFeatureFreq.containsKey(feature))
    			  hashFeatureFreq.put(feature, 1);
    		  else
    			  hashFeatureFreq.put(feature, hashFeatureFreq.get(feature)+1);
    		  
    		  /*if(singleOpinion_YN){
    		    hashFeature.put(feature, opinion);
    		    System.out.println("Feature: "+feature+", Opinion: "+opinion);
    		  }
    		  else{*/ 
    			  if(!hashOpinionList.containsKey(feature)){
    				  Set<String> temp = new HashSet<String>(); 
    				  temp.add(opinion);
    				  hashOpinionList.put(feature, temp);
    			   }
    			  else{
    				  Set<String> temp = hashOpinionList.get(feature);
    				  temp.add(opinion);
    				  hashOpinionList.put(feature, temp);
    			  }
    				  
    		  //}
    			  
			  feature = "";
    		  opinion = "";
    		}
    		
    		words.add(word);
    	}
	}
	
	static void processDependencies(NodeList depList){
		String feature = "";
		String opinion = "";
		
		for(int j=0;j<depList.getLength();j++){
    		Node dep = depList.item(j);
    		Element tempDepElement = (Element)dep;
    		String dep_type = dep.getAttributes().getNamedItem("type").getNodeValue();
    		
    		if(dep_type.equals("nsubj")){
    			opinion = tempDepElement.getElementsByTagName("governor").item(0).getChildNodes().item(0).getNodeValue();
    			feature = tempDepElement.getElementsByTagName("dependent").item(0).getChildNodes().item(0).getNodeValue();
    			hashFeature.put(feature, opinion);
    		}
    	}
	}

	static void parseXML(File file, boolean depYN) throws Exception{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        
        NodeList sentencesList = document.getDocumentElement().getElementsByTagName("sentences");
        Element tempElement = (Element)sentencesList.item(0);
        NodeList senList = tempElement.getElementsByTagName("sentence");
        
        for(int i=0;i<senList.getLength();i++){
        	Element senElement = (Element)senList.item(i); 
        	NodeList tokenList = senElement.getElementsByTagName("token");
        	
        	processTokens(tokenList, depYN);
        	
        	/*if(depYN){
        	  Element depElement = (Element)senElement.getElementsByTagName("dependencies").item(0);
        	  NodeList depList = depElement.getElementsByTagName("dep");
              processDependencies(depList);
        	}*/
         }
	}

}
