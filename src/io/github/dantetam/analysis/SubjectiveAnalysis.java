package io.github.dantetam.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.Tree;
import io.github.dantetam.parser.ParseGrammarResult;
import io.github.dantetam.parser.StellaDependencyParser;

/*
 * Here we attempt to classify bodies of text as subjective or objective
 * This class starts off with a guess with the results of the paper
 * "Twitter as a Corpus for Sentiment Analysis and Opinion Mining",
 * by Pak and Paroubek.
 */
public class SubjectiveAnalysis {

	private Map<String, Double> phraseBiasMap;

	public SubjectiveAnalysis() {
		initMap();
	}

	private void initMap() {
		String[] phrases = new String[]{
				"NP$", "WP$", "PO$", "NP", "NNS", "IN", "VBN", "VBZ", "JJR", "RBS",
				 "TO", "NN", "JJS", "JJ", "DT", "VBG", "VBD", "WDT", "WP", "RP",
				 "MD", "VB", "RBR", "CC", "EX", "VBP", "WRB", "PP$", "RB", "PP",
				 "PDT", "UH"
		};
		double[] weights = new double[]{
				-0.8, -0.7, -0.5, -0.4, -0.3, -0.2, -0.2, 0, 0, 0,
				0, 0, 0, 0.1, 0.1, 0.2, 0.2, 0.2, 0.2, 0.2,
				0.3, 0.3, 0.3, 0.3, 0.3, 0.5, 0.6, 0.6, 0.6, 0.8,
				0.8, 0.9
		};
		phraseBiasMap = new HashMap<>();
		for (int i = 0; i < phrases.length; i++) {
			phraseBiasMap.put(phrases[i], weights[i]);
		}
	}
	
	/*
	 * Input is mostly sanitized text tagged by the CoreNLP part of speech tagger,
	 * where the dot product of its phrases is computed and normalized,
	 * where -1 is objective and 1 is subjective.
	 */
	public double subjectivityScore(String text) {
		List<TaggedWord> taggedWords = StellaDependencyParser.tagSentence(text);
		if (taggedWords.size() == 0) {
			return 0;
		}
		double score = 0;
		for (TaggedWord taggedWord: taggedWords) {
			if (!phraseBiasMap.containsKey(taggedWord.tag())) {
				System.out.println("Could not find tag: " + taggedWord.tag());
			}
			score += phraseBiasMap.get(taggedWord.tag());
		}
		return score / taggedWords.size();
	}
	
	/*
	 * TODO:
	 * A different method of analyzing subjectivity,
	 * using the parsed grammatical structure of the text,
	 * combined with weights for certain grammatical modifiers and depth levels.
	 */
	public double treeSubjectivityScore(String text) {
		List<Tree> binaryTrees = SentimentAnalysis.getBinaryGrammarTree(text);
		double score = 0;
		for (Tree tree: binaryTrees) {
			
		}
		return score;
	}
	
	public static void main(String[] args) {
		SubjectiveAnalysis sAnalysis = new SubjectiveAnalysis();
		System.out.println(sAnalysis.subjectivityScore("ISIS is the absolute worst terrorist group."));
	}

}
