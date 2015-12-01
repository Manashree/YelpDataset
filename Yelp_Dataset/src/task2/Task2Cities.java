package task2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class Task2Cities {
	
	public static void resetHashMaps(){
		CitiesTrainModel.hashCity.clear();
		CitiesTrainModel.hashBusiness.clear();
		XMLParser.hashOpinionList.clear();
		XMLParser.hashFeatureFreq.clear();
	}
	
	public static void modifyNlpConfigFile() throws IOException{
		FileWriter fw = new FileWriter("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\"+CitiesTrainModel.getNlpDir()+"\\"+CitiesTrainModel.getNlpConfigFile());
		fw.write("annotators = tokenize, ssplit, pos");
		fw.close();
	}
	
	public static void displayInfluentialFactors(){
		Set<String> setFactors = CitiesTestModel.hashInfluentialFactors.keySet();
		Iterator<String> it = setFactors.iterator();
		
		while(it.hasNext()){
			String key = it.next();
			System.out.print("Factor: "+key+",  Opinion: ");
			Set<String> setOpinion = XMLParser.hashOpinionList.get(key);
			
			for(String s : setOpinion){
				System.out.print(s+", ");
			}
			System.out.println();
		}
	}
	
	public static void main(String args[]) throws Exception{
		System.out.println("Initializing Training Model...");
		CitiesTrainModel.buildSetup();
		modifyNlpConfigFile();
		
		FileWriter fw = new FileWriter(CitiesTrainModel.curDir+"\\corpus1\\restaurants.json");
		CitiesTrainModel.extractRecords("business_training.json", 3, fw);
		fw.close();
		//System.out.println("business training.json parsed");
		
		CitiesTrainModel.extractRecords("restaurants.json", 4, null);
		//System.out.println("restaurants.json parsed");
		
		System.out.println("Parsing reviews in chunks...\n");
		CitiesTrainModel.buildCityReview();
		
		CitiesTrainModel.buildHashModelFeature();
		System.out.println("\nTraining Model created...\n");
		
		resetHashMaps();
		
		System.out.println("Creating Testing Model...\n");
		CitiesTestModel.init();
		CitiesTrainModel.extractRecords("restaurants.json", 4, null);		
		CitiesTrainModel.extractRecords("review_testing.json", 5, null);
		
		RecommendItems.init();
		RecommendItems.setupNegAdjectives();
		
		System.out.println("Evaluating testing set reviews...");
		CitiesTestModel.evalTestReview();
		
		System.out.println("\nInfluential Factors...\n");
		displayInfluentialFactors();
		
	}
}
