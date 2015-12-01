package task2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class RecommendItems {
	private static String curDir;
    private static final String NOT = "not";
    private static final int WORD_DISTANCE = 4;
    private static Set<String> negativeAdj;
    
    static void init() throws FileNotFoundException {
    	curDir = System.getProperty("user.dir");
        negativeAdj = new HashSet<String>();
        setupNegAdjectives();
    }
    
    public static Set<String> getNegativeAdj(){
    	return negativeAdj;
    }
    
    public static void setupNegAdjectives() throws FileNotFoundException {

        File f = new File(curDir+"\\corpus1\\NegativeAdjectives.txt");
        BufferedReader br = new BufferedReader(new FileReader(f));
        String s = "";
        
        try {
            while((s=br.readLine())!=null){
            	negativeAdj.add(s.toLowerCase());    	
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}

     }

    public static void identifySentence(List<String> sentence, HashMap<String, Set<String>> nounToAdjectiveMapping) {
        int posCount = 0;
        int negCount = 0;
        
        for(Entry<String, Set<String>> entry : nounToAdjectiveMapping.entrySet()) {
            boolean flag = false;
            boolean negative = false;
            Set<String> setOpinion = entry.getValue();
            
            for(String s : setOpinion){
               if(negativeAdj.contains(s))
            	  negative = true;
            	
               int i = 0;
               for(; i < sentence.size(); i++) {                   
                  if(sentence.get(i).equalsIgnoreCase(s))
                     break;
                } 
                
               int start = (i - WORD_DISTANCE) < 0 ? 0 : i - WORD_DISTANCE;
               int end = (i + WORD_DISTANCE) < sentence.size()? (i + WORD_DISTANCE) : sentence.size() - 1;
               while(start <= end) {
                   if(sentence.get(start).equalsIgnoreCase(NOT)) {
                       flag = true;
                       break;
                   }
                   start++;
               }
               
               if((negative && flag) || (!negative && !flag)) {
                   posCount++;
               } else {
                  negCount++;
               }
               
               flag = false;
               negative = false;
                
            }
            	
            if(posCount > negCount)
               System.out.println("Recommended: " + entry.getKey());
            
            posCount = 0;
            negCount = 0;
        }
    }
    
}