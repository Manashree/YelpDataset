package task2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class SentimentAnalyzer {
	
	// Below method calls the CoreNLP parser to generate the output .xml file
	public static void analyzeSentence(boolean msgYN) throws IOException, InterruptedException{
		ProcessBuilder pb = new ProcessBuilder("java", "-cp", "*", "-Xmx2g", "edu.stanford.nlp.pipeline.StanfordCoreNLP", 
                                               "-props", "config.properties", "-file", "input.txt");
		
		pb.directory(new File("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\stanford-corenlp-full-2015-04-20"));
		Process process = pb.start();

		int errCode = process.waitFor();
		if(errCode!=0)
		   System.out.println("Error Analyzing Sentence...");
		else if(msgYN)
		       System.out.println("Review parsed successfully");
	}
	
	
	// Below method creates the input file to be given to the CoreNLP parser
	public static void createNLPInputFile(String business_id) throws IOException, InterruptedException{
		FileWriter fw = new FileWriter("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\stanford-corenlp-full-2015-04-20\\input.txt");
		fw.write(TestData.hashTestReview.get(business_id));
		fw.close();
	}
	
	
	// Below method displays the Featues and the Opinions
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
	
	
	// Below method performs the recommendation
	public static void performRecommendation() throws Exception{
		Set<String> set = TestData.hashTestReview.keySet();
		Iterator<String> it = set.iterator();
		FileWriter fw = new FileWriter("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\stanford-corenlp-full-2015-04-20\\config.properties");
		fw.write("annotators = tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		fw.close();
		
		while(it.hasNext()){
			String business_id = it.next();
			createNLPInputFile(business_id);  // creates the input file for NLP parser
			
			System.out.println("Parsing the review for business id: "+business_id);
			analyzeSentence(true);  // calls the NLP parser to generate the output(input.txt.xml) file
			File file = new File("C:\\Users\\Shrijit\\Documents\\IU\\Fall2015\\Advanced NLP\\stanford-corenlp-full-2015-04-20\\input.txt.xml");
			XMLParser.parseXML(file, true); // parses the xml file and generates the features and sentiments
			
			displayOpinionList(business_id);
			RecommendItems.identifySentence(XMLParser.words, XMLParser.hashOpinionList, null); // builds list of recommended and non-recommend features
			
			System.out.println("------------------------");
			XMLParser.hashOpinionList.clear();
		}
	}
}
