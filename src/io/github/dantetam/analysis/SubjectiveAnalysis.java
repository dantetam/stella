package io.github.dantetam.analysis;

import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;
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
			score += phraseBiasMap.get(taggedWord.tag());
		}
		return score / taggedWords.size();
	}

}
