package task2;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONObject;

public class CitiesTestModel {
	static String curDir;
	static HashMap<String, String> hashTestCityReview;
	static HashMap<String, Set<String>> hashInfluentialFactors;
	
	public static void init() throws IOException{
		hashTestCityReview = new HashMap<String, String>();
		hashInfluentialFactors = new HashMap<String, Set<String>>();
		
		CitiesTrainModel.hashCity = new HashMap<String, String>();
		CitiesTrainModel.hashBusiness = new HashMap<String, String>();
		
		CitiesTrainModel.hashCity.put("Las Vegas", "Las Vegas");
		CitiesTrainModel.hashCity.put("Phoenix", "Phoenix");
	}
	
	public static void buildCitiesTestModel(Object obj){
		JSONObject jObject = (JSONObject)obj;
		String business_id = jObject.get("business_id") + "";
		String review = jObject.get("text") + "";
		
		if(CitiesTrainModel.hashBusiness.containsKey(business_id))
			hashTestCityReview.put(business_id, review);
			
	}
	
	public static void getInfluentialFactors(){
		Set<String> set = XMLParser.hashOpinionList.keySet();
		Iterator<String> it = set.iterator();		
		
		while(it.hasNext()){
			String key = it.next();
			
			if(CitiesTrainModel.hashModelFeature.containsKey(key)){
				Set<String> setOpinion = XMLParser.hashOpinionList.get(key);
				
				if(!hashInfluentialFactors.containsKey(key))
			    	hashInfluentialFactors.put(key, setOpinion);
			    else{
			      	Set<String> temp = hashInfluentialFactors.get(key);
			      	temp.addAll(setOpinion);
			      	hashInfluentialFactors.put(key, temp);
			    }
				
			}
		}
		
	}
	
	public static void evalTestReview() throws Exception{
		Set<String> set = hashTestCityReview.keySet();
		Iterator<String> it = set.iterator();
		int count = 0;
		String suffix = "th";
		
		while(it.hasNext()){
			if(count>=10)
				break;
			
			count++;
			
			if(count%10==1)
				suffix = "st";
			else if(count%10==2)
				suffix = "nd";
			else if(count%10==3)
				suffix = "rd";
			else
				suffix = "th";
			
			System.out.println("Parsing "+ count + suffix + " Test Reviews");
			
			CitiesTrainModel.writeNLPInputFile(null, hashTestCityReview.get(it.next()));
			SentimentAnalyzer.analyzeSentence();
			
			File file = new File("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\"+CitiesTrainModel.getNlpDir()+"\\"+CitiesTrainModel.getNlpOutputFile());
			XMLParser.parseXML(file, false);
			getInfluentialFactors();
			
		}
	}
	
}
