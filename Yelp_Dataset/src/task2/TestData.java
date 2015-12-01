package task2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class TestData {
	static String curDir;
	static HashMap<String, String> hashCat;
	static HashMap<String, String> hashBusiness;
	static HashMap<String, String> hashTestReview;
	
	static void init(){
		curDir = System.getProperty("user.dir");
		hashCat = new HashMap<String, String>();
		hashBusiness = new HashMap<String, String>();
		hashTestReview = new HashMap<String, String>();
	}
	
	static void buildSetup() throws Exception{
		init();
		buildHashCat();
	}
	
	static void parse(String fileName) throws Exception{
		fileName = curDir+"\\corpus1\\"+fileName; 
		File file = new File(fileName);
		parseJsonFile(file);
	}
	
	static void buildHashCat() throws Exception{
		FileReader fr = new FileReader(curDir+"\\corpus1\\categories.txt");
		BufferedReader br = new BufferedReader(fr);
		String s = "";
		
		while((s=br.readLine())!=null)
			hashCat.put(s, s);
		
		br.close();
		
	}
	
	static void buildHashBusiness(Object obj) throws Exception{
		JSONObject jObject = (JSONObject)obj;
		Integer review_count = Integer.parseInt(jObject.get("review_count")+"");
		String business_id = jObject.get("business_id") + ""; 
		
		if(review_count <= 5){
			JSONArray catObj = (JSONArray)jObject.get("categories");
			for(int i=0;i<catObj.size();i++){
				if(hashCat.containsKey(catObj.get(i))){
					hashBusiness.put(business_id,business_id);
					break;
				}
			}
		
		}
		
	}
	
	static void buildTestReview(Object obj) throws Exception{
		JSONObject jObject = (JSONObject)obj;
		String business_id = jObject.get("business_id") + "";
		String review = jObject.get("text") + "";
		
		if(hashBusiness.containsKey(business_id)){
			if(!hashTestReview.containsKey(business_id))
			   hashTestReview.put(business_id, review);
			else
			   hashTestReview.put(business_id, hashTestReview.get(business_id)+" "+review);
		}
		
	}
	
	static void parseJsonFile(File file) throws Exception{
		FileReader f = new FileReader(file);
		BufferedReader br = new BufferedReader(f);
		JSONParser parser=new JSONParser();
		String s = "";
		String fileName = file.getName();
		
		while((s=br.readLine())!=null){			
		  try{	
			   Object obj= parser.parse(s);
			   
			   if(fileName.equals("business_training.json"))
			      buildHashBusiness(obj);
			   else if(fileName.equals("review_training.json"))
				       buildTestReview(obj);
			 }
		  
		  catch(Exception e){}
		
		}
		
		br.close();
		
	}
	
}
