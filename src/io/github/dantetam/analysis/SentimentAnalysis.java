package io.github.dantetam.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class SentimentAnalysis {
	
	public static StanfordCoreNLP pipeline;
	public static boolean trained = false;
	
	public static void init() {
		Properties props = new Properties();
        props.setProperty("annotators",
                "tokenize, ssplit, pos, lemma, parse, sentiment");
		pipeline = new StanfordCoreNLP(props);
	}
	
	public static List<Tree> getBinaryGrammarTree(String text) {
		if (!trained) {
			trained = true;
			init();
		}
		
		List<Tree> binaryTrees = new ArrayList<>();
		Annotation annotation = pipeline.process(text);
        List<CoreMap> sentences = annotation
                .get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence: sentences) {
            //String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            binaryTrees.add(sentimentTree);
        }
        return binaryTrees;
	}
	
	public static int convertSentimentToInt(String sentiment) {
		switch (sentiment) {
		case "Very Negative": return -2;
		case "Negative": return -1;
		case "Neutral": return 0;
		case "Positive": return 1;
		default: return 2;
		}
	}
	
	public static float getSentimentSentence(String text) {
		List<String> temp = new ArrayList<>();
		temp.add(text);
		return getSentimentSentence(temp)[0];
	}
	public static float[] getSentimentSentence(List<String> texts) {
		if (!trained) {
			trained = true;
			init();
		}
	
		float[] result = new float[texts.size()];
		for (int i = 0; i < texts.size(); i++) {
			String text = texts.get(i);
			Annotation annotation = pipeline.process(text);
	        List<CoreMap> sentences = annotation
	                .get(CoreAnnotations.SentencesAnnotation.class);
	        float avgFeelingText = 0;
	        for (CoreMap sentence: sentences) {
	            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
	            //Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
	            avgFeelingText += convertSentimentToInt(sentiment);
	        }
	        avgFeelingText /= sentences.size();
	        result[i] = avgFeelingText;
		}
		return result;
	}

	public static void main(String[] args) throws IOException {
		if (!trained) {
			trained = true;
			init();
		}
		
        String text = "I am very happy.";

        Annotation annotation = pipeline.process(text);
        List<CoreMap> sentences = annotation
                .get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence: sentences) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            Tree sentimentTree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
            System.out.println(sentiment + "\t" + sentence);
            System.out.println(sentimentTree);
        }
    }
	
}
