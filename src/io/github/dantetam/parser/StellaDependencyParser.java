package io.github.dantetam.parser;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.util.logging.Redwood;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/*
 * Train a grammar structure dependency parser with the Stanford classes and model data
 * so that it can be used in the future to process large volumes of text.
 */
public class StellaDependencyParser {

	/** A logger for this class */
	private static Redwood.RedwoodChannels log = Redwood.channels(StellaDependencyParser.class);

	//Keep the trained models in memory
	private static MaxentTagger tagger;
	private static DependencyParser parser;
	
	private static boolean trained = false;
	
	private static void train() {
		String modelPath = DependencyParser.DEFAULT_MODEL;
		String taggerPath = "edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger";

		tagger = new MaxentTagger(taggerPath);
		parser = DependencyParser.loadFromModelFile(modelPath);
	}
	
	/*
	 * Collate the parsed data of some text, which includes a list of words and 
	 * the grammatical structure containing those words.
	 */
	public static List<ParseGrammarResult> parseGrammarStructure(String text) {
		if (!trained) {
			train();
		}
		List<ParseGrammarResult> results = new ArrayList<>();
		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader(text));
		for (List<HasWord> sentence : tokenizer) {
			List<TaggedWord> tagged = tagger.tagSentence(sentence);
			
			/*for (TaggedWord taggedWord: tagged) {
				System.out.println(taggedWord.toString());
			}*/
			
			GrammaticalStructure gs = parser.predict(tagged);

			// Print typed dependencies
			//log.info(gs);
			results.add(new ParseGrammarResult(tagged, gs));
		}
		return results;
	}
	
	public static void main(String[] args) {
		parseGrammarStructure("I can always tell when they use fake dinosaurs in movies.");
	}

}
