package task2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.lucene.document.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import task1.EvaluateReview;
import task1.Task1IR;
import task1.TrainReview;

public class CitiesTrainModel {
	static String curDir;
	final static String nlpDir = "stanford-corenlp-full-2015-04-20";
	final static String nlpOutputFile = "input.txt.xml";
	final static String nlpInputFile = "input.txt";
	final static String nlpConfigFile = "config.properties";
	final static String restaurantCategoryFile = "categories.txt";
	static List<String> listCities;
	static HashMap<String, String> hashCat;
	static HashMap<String, String> hashCity;
	static HashMap<String, String> hashBusiness;
	static HashMap<String, String> hashCityReview;
	static HashMap<String, Set<String>> hashModelFeature;
	
	public static String getCurDir(){
		return curDir;
	}
	
	public static String getNlpDir(){
		return nlpDir;
	}

	public static String getNlpOutputFile(){
		return nlpOutputFile;
	}
	
	public static String getNlpInputFile(){
		return nlpInputFile;
	}
	
	public static String getNlpConfigFile(){
		return nlpConfigFile;
	}
	
	static void init(){
		curDir = System.getProperty("user.dir");
		hashCity = new HashMap<String, String>();
		hashCityReview = new HashMap<String, String>();
		hashCat = new HashMap<String, String>();
		hashBusiness = new HashMap<String, String>();
		hashModelFeature = new HashMap<String, Set<String>>();
		
		hashCity.put("Madison", "Madison");
		hashCity.put("Pittsburgh", "Pittsburgh");
		hashCity.put("Urbana-Champaign", "Urbana-Champaign");
		
	}
	
	static void buildSetup() throws Exception{
		init();
		TrainReview.initSetUp();
		EvaluateReview.initSetUp();
		TestData.buildSetup();
		XMLParser.init();
		buildHashCat();	
	}
	
	static void buildHashCat() throws Exception{
		FileReader fr = new FileReader(curDir+"\\corpus1\\"+restaurantCategoryFile);
		BufferedReader br = new BufferedReader(fr);
		String s = "";
		
		while((s=br.readLine())!=null)
			hashCat.put(s, s);
		
		br.close();
	}
	
	static void buildCityReview() throws Exception{
		String review = "";
		String city = "";
		int startDoc = 0;
		int endDoc = 500;
		int count = 0;
		
		while(endDoc <= 5000){
			for(int docId=startDoc;docId<endDoc;docId++){
				Document doc = TrainReview.getIndexSearcher().doc(docId);
				String business_id = doc.get("BUSID");
				
				if(hashBusiness.containsKey(business_id)){
					review = doc.get("REVIEW"); 
					city = hashBusiness.get(business_id);
					
					if(!hashCityReview.containsKey(city))
						hashCityReview.put(city, review);
					else
						hashCityReview.put(city, hashCityReview.get(city)+"\n "+review);	
				}
			}
			
			count++;
			
			writeNLPInputFile(hashCityReview, null);
			System.out.println("Input file written");
			System.out.println("Chunk: "+count);
			
			SentimentAnalyzer.analyzeSentence();
			File file = new File("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\"+nlpDir+"\\"+nlpOutputFile);
			
			XMLParser.parseXML(file, false);
			hashCityReview.clear();
			
			startDoc += 500;
			endDoc += 500;
		}
			
	}
	
	public static void buildHashBusiness(Object obj) throws Exception{
		JSONObject jObject = (JSONObject)obj;
		String business_id = jObject.get("business_id") + "";
		String city = jObject.get("city") + "";
		
		if(hashCity.containsKey(city))
			hashBusiness.put(business_id, city);
		
	}
	
	public static boolean isRestaurant(Object obj){
		JSONObject jObject = (JSONObject)obj;
		JSONArray catObj = (JSONArray)jObject.get("categories");
		
		for(int i=0;i<catObj.size();i++){
			if(hashCat.containsKey(catObj.get(i))){
				return true;
			}
		}
		
		return false;
	}
	
	public static void writeNLPInputFile(HashMap<String, String> hash, String review) throws IOException{
		
		FileWriter fw = new FileWriter("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\"+nlpDir+"\\"+nlpInputFile);
		
		if(hash!=null){
		   Set<String> set = hash.keySet();
		   Iterator<String> it = set.iterator();
			
			
		   while(it.hasNext()){
			  String key = it.next();
			  fw.write(hash.get(key)+"\n");
		   }
		 }
		else{
			fw.write(review+"\n");
		}
		
		fw.close();
	}
	
	
	public static void extractRecords(String fileName, int option, FileWriter fw) throws Exception{
		File file = new File(curDir+"\\corpus1\\"+fileName);
		Task1IR.parseJsonFile(file, option, fw);
	}
	
	
	public static void buildHashModelFeature(){
		Set<String> set = XMLParser.hashFeatureFreq.keySet();
		Iterator<String> it = set.iterator();
		
		while(it.hasNext()){
			String key = it.next();
			int freq = XMLParser.hashFeatureFreq.get(key);
			
			if(XMLParser.hashOpinionList.containsKey(key) && freq>=5){				
				Set<String> temp = XMLParser.hashOpinionList.get(key);
			    hashModelFeature.put(key, temp);
			}
				
		 }
	}

}
