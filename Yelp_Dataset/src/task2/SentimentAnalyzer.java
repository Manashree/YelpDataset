package task2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class SentimentAnalyzer {
	
	public static void analyzeSentence() throws IOException, InterruptedException{
		ProcessBuilder pb = new ProcessBuilder("java", "-cp", "*", "-Xmx2g", "edu.stanford.nlp.pipeline.StanfordCoreNLP", 
                                               "-props", "config.properties", "-file", "input.txt");
		
		pb.directory(new File("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\stanford-corenlp-full-2015-04-20"));
		Process process = pb.start();

		int errCode = process.waitFor();
		if(errCode!=0)
			System.out.println("Error Analyzing Sentence...");
		else
			System.out.println("Review parsed successfully");
	}
	
	public static void createNLPInputFile(String business_id) throws IOException, InterruptedException{
		FileWriter fw = new FileWriter("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\stanford-corenlp-full-2015-04-20\\input.txt");
		fw.write(TestData.hashTestReview.get(business_id));
		fw.close();
	}
	
	public static void displayOpinionList(String business_id){
		Set<String> set = XMLParser.hashOpinionList.keySet();
		Iterator<String> it = set.iterator();
		System.out.println("\nBusiness Id: "+business_id);
		
		while(it.hasNext()){
			String feature = it.next();
			
			System.out.print("Feature: "+feature+", Opinion: ");
			
			Set<String> setOpinion = XMLParser.hashOpinionList.get(feature);
			for(String s : setOpinion)
				System.out.print(s+", ");
			System.out.println();
		}
	}
	
	public static void performRecommendation() throws Exception{
		Set<String> set = TestData.hashTestReview.keySet();
		Iterator<String> it = set.iterator();
		FileWriter fw = new FileWriter("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\stanford-corenlp-full-2015-04-20\\config.properties");
		fw.write("annotators = tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		fw.close();
		
		while(it.hasNext()){
			String business_id = it.next();
			createNLPInputFile(business_id);
			
			System.out.println("Parsing the review for business id: "+business_id);
			analyzeSentence();
			File file = new File("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\stanford-corenlp-full-2015-04-20\\input.txt.xml");
			XMLParser.parseXML(file, true);
			
			displayOpinionList(business_id);
			RecommendItems.identifySentence(XMLParser.words, XMLParser.hashOpinionList);
			
			System.out.println("------------------------");
			XMLParser.hashOpinionList.clear();
		}
	}
}
