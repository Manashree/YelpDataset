package task2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.HashMap;

// Implementation class
public class Task2Cities {
	
	public static void init() throws Exception{
		CitiesTrainModel.buildSetup();
		RecommendItems.init();
		CitiesTestModel.init();
		RecommendItems.setupNegAdjectives();
		modifyNlpConfigFile();
	}
	
	public static void resetHashMaps(){
		CitiesTrainModel.hashCity.clear();
		CitiesTrainModel.hashBusiness.clear();
		CitiesTrainModel.hashBusinessReview.clear();
		XMLParser.hashOpinionList.clear();
		XMLParser.hashFeatureFreq.clear();
		RecommendItems.getHashRecBusList().clear();
		RecommendItems.getHashNonRecBusList().clear();
	}
	
	public static void modifyNlpConfigFile() throws IOException{
		FileWriter fw = new FileWriter("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\"+CitiesTrainModel.getNlpDir()+"\\"+CitiesTrainModel.getNlpConfigFile());
		fw.write("annotators = tokenize, ssplit, pos");
		fw.close();
	}
	
	@SuppressWarnings("unchecked")
	public static void saveCityInfo(){
		CitiesTestModel.listFeatureFreq.add((HashMap<String, Integer>) XMLParser.hashFeatureFreq.clone());
		CitiesTestModel.listNonRecBus.add((HashMap<String, Set<String>>) RecommendItems.getHashNonRecBusList().clone());
		CitiesTestModel.listRecBus.add((HashMap<String, Set<String>>) RecommendItems.getHashRecBusList().clone());
		CitiesTestModel.listBusinessRating.add((HashMap<String, Float>) CitiesTrainModel.hashBusiness.clone());
	}
	
	/*public static void displayRecFeatures(){
		List<FeatureObj> set = CitiesTestModel.listTopFeatureObj;
		
		for(FeatureObj f : set){
			System.out.println(f.feature);
		}
	}*/
	
	public static void main(String args[]) throws Exception{
		
		System.out.println("Initializing Training Model...");
		init();
		
		FileWriter fw = new FileWriter(CitiesTrainModel.curDir+"\\corpus1\\restaurants.json");
		CitiesTrainModel.extractRecords("business_training.json", 3, fw);
		fw.close();
		
		CitiesTrainModel.extractRecords("restaurants.json", 4, null);
		System.out.println("Hash Business Size: "+CitiesTrainModel.hashBusiness.size());
		
		CitiesTrainModel.buildBusinessReview();
		CitiesTrainModel.buildFeatureList();
		
		//System.out.println("hashBusinessReview size: "+CitiesTrainModel.hashBusinessReview.size());
		//System.out.println("Freq Feature Size: "+XMLParser.hashFeatureFreq.size());
		
		saveCityInfo();
		
		//System.out.println("City1 Rec Size: "+RecommendItems.getHashRecBusList().size());
		//System.out.println("City1 Non Rec Size: "+RecommendItems.getHashNonRecBusList().size());
		
		resetHashMaps();
		
		//System.out.println("Feat Size: "+CitiesTestModel.listFeatureFreq.get(0).size());
		
		CitiesTrainModel.hashCity.clear();
		CitiesTrainModel.hashCity.put("Phoenix", "Phoenix");
		
		CitiesTrainModel.extractRecords("restaurants.json", 4, null);
		//System.out.println("Hash Business2 Size: "+CitiesTrainModel.hashBusiness.size());
		CitiesTrainModel.buildBusinessReview();
		CitiesTrainModel.buildFeatureList();
		
		//System.out.println("hashBusinessReview2 size: "+CitiesTrainModel.hashBusinessReview.size());
		//System.out.println("Freq Feature2 Size: "+XMLParser.hashFeatureFreq.size());
		
		saveCityInfo();
		
		//System.out.println("City2 Rec Size: "+RecommendItems.getHashRecBusList().size());
		//System.out.println("City2 Non Rec Size: "+RecommendItems.getHashNonRecBusList().size());
		
		CitiesTestModel.getTopFeatures();
		//displayRecFeatures();
		CitiesTestModel.getInfluentialFactors();		
		
	}
}

