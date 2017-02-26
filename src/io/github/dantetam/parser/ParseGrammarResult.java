package io.github.dantetam.parser;

import java.util.List;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.GrammaticalStructure;

//A wrapper class for this result from parseGrammarStructure(String text) in StellaTreeAssociation.java
public class ParseGrammarResult {

	public List<TaggedWord> taggedWords;
	public GrammaticalStructure grammarStructure;
	
	public ParseGrammarResult(List<TaggedWord> ltw, GrammaticalStructure gs) {
		taggedWords = ltw;
		grammarStructure = gs;
	}
	
}
