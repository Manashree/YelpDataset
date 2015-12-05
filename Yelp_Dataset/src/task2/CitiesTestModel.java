package task2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CitiesTestModel {
	static String curDir;
	static List<HashMap<String, Integer>> listFeatureFreq;
	static List<HashMap<String, Set<String>>> listNonRecBus;
	static List<HashMap<String, Set<String>>> listRecBus;
	static List<HashMap<String, Float>> listBusinessRating;
	static List<FeatureObj> listTopFeatureObj;
	
	public static void init() throws IOException{		
		listFeatureFreq = new ArrayList<HashMap<String, Integer>>();
		listNonRecBus = new ArrayList<HashMap<String, Set<String>>>();
		listRecBus = new ArrayList<HashMap<String, Set<String>>>();
		listBusinessRating = new ArrayList<HashMap<String, Float>>();
		listTopFeatureObj = new ArrayList<FeatureObj>();
	}
	
	// Below method sorts the features according to its frequency(number of times it is mentioned in the review)
	static void getTopFeatures(){
		Set<String> set = listFeatureFreq.get(0).keySet();
		Iterator<String> it = set.iterator();
		
		while(it.hasNext()){
			String feature = it.next();
			int freq = listFeatureFreq.get(0).get(feature);
			FeatureObj fo = new FeatureObj(feature, freq);
			listTopFeatureObj.add(fo);
		 }
		
		Collections.sort(listTopFeatureObj, new Comparator<FeatureObj>(){
			@Override
			public int compare(FeatureObj f1, FeatureObj f2){
				if(f1.freq == f2.freq)
					return 0;
				return f2.freq > f1.freq ? 1 : -1; 
			}
		});
			
	}
	
	// Below method shows a comparison of the influential factors in the training model city and the test city
	static void getInfluentialFactors(){
		float avgPosRating = 0;
		float avgNegRating = 0;
		float netRating = 0;
		
		for(int i=0;i<10;i++){
		   String feature = listTopFeatureObj.get(i).feature;
		   System.out.println("Feature: "+feature);
		   HashMap<String, Set<String>> hashCity1RecBusList = listRecBus.get(0);
		   HashMap<String, Set<String>> hashCity1NonRecBusList = listNonRecBus.get(0);
		   HashMap<String, Float> hashCity1BusRating = listBusinessRating.get(0);
		   
		   HashMap<String, Set<String>> hashCity2RecBusList = listRecBus.get(1);
		   HashMap<String, Set<String>> hashCity2NonRecBusList = listNonRecBus.get(1);
		   HashMap<String, Float> hashCity2BusRating = listBusinessRating.get(1);
		   
		   if(hashCity1RecBusList.containsKey(feature)){
			  Set<String> setBusiness = hashCity1RecBusList.get(feature);
				
			  for(String business_id : setBusiness){
				  System.out.println("Madison Rec: "+business_id);
				  avgPosRating += hashCity1BusRating.get(business_id);
			  }
			}
		   
		   if(hashCity1NonRecBusList.containsKey(feature)){
			   Set<String> setBusiness = hashCity1NonRecBusList.get(feature);
				
			   for(String business_id : setBusiness){
				   System.out.println("Madison Non Rec: "+business_id);
				   avgNegRating += hashCity1BusRating.get(business_id);
			   }
		   }
		   
		  netRating = avgPosRating - avgNegRating;
		  System.out.println("Net Rating for Madison: "+netRating);
		  
		  avgPosRating = 0;
		  avgNegRating = 0;
		  
		  if(hashCity2RecBusList.containsKey(feature)){
			  Set<String> setBusiness = hashCity2RecBusList.get(feature);
				
			  for(String business_id : setBusiness){
				  System.out.println("Phoenix Rec: "+business_id);
				  avgPosRating += hashCity2BusRating.get(business_id);
			  }
			}
		  
		  if(hashCity2NonRecBusList.containsKey(feature)){
			  Set<String> setBusiness = hashCity2NonRecBusList.get(feature);
				
			  for(String business_id : setBusiness){
				  System.out.println("Phoenix Non Rec: "+business_id);
				  avgNegRating += hashCity2BusRating.get(business_id);
			  }
		   }
		  
		  System.out.println("Net Rating for Phoenix: "+(avgPosRating - avgNegRating));
		  System.out.println("\n------------------------\n");
		}
	}
	
}


class FeatureObj{
	String feature;
	int freq;
	
	FeatureObj(String feature, int freq){
		this.feature = feature;
		this.freq = freq;
	}
}


