package task2;

import task1.EvaluateReview;

public class Task2RecommendItems {
	
	static void setUpTestData() throws Exception{
		TestData.buildSetup();
		TestData.parse("business_training.json");
		TestData.parse("review_training.json");
		XMLParser.init();
		EvaluateReview.initSetUp();
		RecommendItems.init();
		
	}
	
	public static void main(String args[]) throws Exception{
		System.out.println("Initializing the Recommendation System...");
		setUpTestData();
		
		System.out.println("Commencing Recommendation...");
		SentimentAnalyzer.performRecommendation();
		
	}
}
