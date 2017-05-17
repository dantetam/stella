package io.github.dantetam.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.stanford.nlp.trees.TypedDependency;
import io.github.dantetam.parser.ParseGrammarResult;
import io.github.dantetam.parser.StellaDependencyParser;

import java.util.Set;

public class SentenceVectorizer {

	public static final void main(String[] args) {
		List<String> listSentences = new ArrayList<>();
		listSentences.add("Hello, this is a test this is a test of the outdoor warning system system test test test");
		listSentences.add("a is");
		Map<String, Integer> results = uniqueWords(listSentences);
		findWordIdMap(results, 5);
		
		encodeSentenceAsGrammarVec(null, "You walk alone every night in the dark", 0);
	}
	
	public static int lengthLongestSentence(List<String> sentences) {
		int maxLen = -1;
		for (String sentence: sentences) {
			String[] words = sentence.split(" ");
			if (maxLen == -1 || words.length > maxLen) {
				maxLen = words.length;
			}
		}
		return maxLen;
	}
	
	public static Map<String, Integer> uniqueWords(List<String> sentences) {
		Map<String, Integer> result = new HashMap<>();
		for (String sentence: sentences) {
			String[] words = sentence.split(" ");
			for (String word: words) {
				if (!result.containsKey(word)) {
					result.put(word, 0);
				}
				result.put(word, result.get(word) + 1);
			}
		}
		return result;
	}
	
	public static Map<String, Integer> findWordIdMap(Map<String, Integer> uniqueWords, int desiredNum) {
		if (desiredNum > uniqueWords.size()) {
			desiredNum = uniqueWords.size();
		}
		Map<String, Integer> sorted = MapUtil.sortByValueDescending(uniqueWords);
		Map<String, Integer> results = new LinkedHashMap<>();
		int i = 0;
		for (Entry<String, Integer> entry: sorted.entrySet()) {
			if (i >= desiredNum) {
				break;
			}
			results.put(entry.getKey(), i);
			i++;
		}
		return results;
	}
	
	public static int[] encodeSentenceAsWordVec(Map<String, Integer> importantWords, String sentence) {
		int vecLength = importantWords.size();
		
		String[] words = sentence.split(" ");
		int[] vector = new int[vecLength];
		for (int i = 0; i < vecLength; i++) {
			vector[i] = 0;
		}
		
		//for (String str)
		for (String word: words) {
			if (importantWords.containsKey(word)) {
				vector[importantWords.get(word)] = 1;
			}
		}
		return vector;
	}
	
	public static int[] encodeSentenceAsGrammarVec(Map<String, Integer> grammarRelationIds, String sentence, int sizeMaxSentence) {
		int[] vector = new int[sizeMaxSentence*sizeMaxSentence];
		ParseGrammarResult grammarResult = StellaDependencyParser.parseGrammarStructure(sentence).get(0);
		Collection<TypedDependency> dependencies = grammarResult.grammarStructure.allTypedDependencies();
		for (TypedDependency dependency: dependencies) {
			System.out.println(dependency.gov() + " " + dependency.dep() + " " + dependency.reln());
		}
		return vector;
	}
	
}
