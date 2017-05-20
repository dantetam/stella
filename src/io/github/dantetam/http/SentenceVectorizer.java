package io.github.dantetam.http;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.jna.platform.win32.Variant.VARIANT._VARIANT.__VARIANT;

import edu.stanford.nlp.trees.TypedDependency;
import io.github.dantetam.parser.ParseGrammarResult;
import io.github.dantetam.parser.StellaDependencyParser;

import java.util.Set;

public class SentenceVectorizer {
	
	/*public static String[] grammarRelations = {"root", "dep", "aux", "auxpass", 
			"cop", "arg", "agent", "comp", "acomp", "ccomp", "xcomp", "obj", 
			"dobj", "iobj", "pobj", "subj", "nsubj", "nsubjpass", "csubj", 
			"csubjpass", "cc", "conj", "expl", "mod", "amod", "appos", "advcl", "case",
			"det", "predet", "preconj", "vmod", "mwe", "mark", "advmod", "neg",
			"rcmod", "quantmod", "nn", "npadvmod", "tmod", "nmod", "nmod:tmod", "nummod", 
			"prep", "poss", "possessive", "prt", "parataxis", "orphan", 
			"reparandum", "goeswith", "punct", "ref", "sdep", "xsubj"};*/
	
	public static String[] grammarRelations = {
			"nsubj", "obj", "iobj", "csubj", "ccomp", "xcomp",
			"obl", "vocative", "expl", "dislocated", "advcl", "advmod", "discourse",
			"aux", "cop", "mark", "det", "clf", "case",
			"nmod", "nmod:tmod", "nmod:poss", "appos", "nummod", "acl", "amod", "det",
			"conj", "cc",
			"fixed", "flat", "compound",
			"list", "parataxis", "orphan", "goeswith", "reparandum", 
			"punct", "root", "dep"
	};
	
	public static void main(String[] args) {
		readInSentencesFromFile("./data/tweets_train.txt");
	}
	
	public static Map<String, Integer> createGrammarRelationMap() {
		Map<String, Integer> result = new HashMap<>();
		for (int i = 0; i < grammarRelations.length; i++) {
			result.put(grammarRelations[i], i);
		}
		return result;
	}
	
	public static List<String> readInSentencesFromFile(String fileName) {
		List<String> results = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine.replace("\"", ""));
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return results;
	}
	
	public static int[][] vectorizeSentences(List<String> listSentences) {
		int uniqueWordsLimit = 250;
		
		/*List<String> listSentences = new ArrayList<>();
		listSentences.add("You walk alone every night in the dark");
		listSentences.add("looking for every trace to your heart");*/
		Map<String, Integer> uniqueWordMap = uniqueWords(listSentences);
		Map<String, Integer> wordIdMap = findWordIdMap(uniqueWordMap, uniqueWordsLimit);
		
		Map<String, Integer> grammarMap = createGrammarRelationMap();
		
		int sentenceLen = lengthLongestSentence(listSentences);
		
		int[][] result = new int[listSentences.size()][uniqueWordsLimit + (sentenceLen + 1)*(sentenceLen + 1)];
		
		//int[] vector = encodeSentenceAsGrammarVec(grammarMap, "You walk alone every night in the dark", sentenceLen);
		/*for (int i = 0; i < 8+1; i++) {
			for (int j = 0; j < 8+1; j++) {
				int index = i*(8+1) + j;
				System.out.println(vector[index]);
			}
		}*/
		for (int sentenceIndex = 0; sentenceIndex < listSentences.size(); sentenceIndex++) {
			String sentence = listSentences.get(sentenceIndex);
			int[] wordsVec = encodeSentenceAsWordVec(wordIdMap, sentence);
			int[] grammarVec = encodeSentenceAsGrammarVec(grammarMap, sentence, sentenceLen);
			int[] totalVec = new int[wordsVec.length + grammarVec.length];
			for (int i = 0; i < wordsVec.length; i++) totalVec[i] = wordsVec[i];
			for (int i = 0; i < grammarVec.length; i++) totalVec[i + uniqueWordsLimit] = grammarVec[i];
			result[sentenceIndex] = totalVec;
			/*for (int num: totalVec)
				System.out.print(num + " ");
			System.out.println();*/
		}
		
		return result;
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
				String lowercaseWord = word.trim().toLowerCase();
				if (!result.containsKey(lowercaseWord)) {
					result.put(lowercaseWord, 0);
				}
				result.put(lowercaseWord, result.get(lowercaseWord) + 1);
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
		
		//for (String str)
		for (String word: words) {
			if (importantWords.containsKey(word)) {
				vector[importantWords.get(word)] = 1;
			}
		}
		return vector;
	}
	
	public static int[] encodeSentenceAsGrammarVec(Map<String, Integer> grammarRelationIds, String sentence, int sizeMaxSentence) {
		int[] vector = new int[(int) Math.pow(sizeMaxSentence + 1, 2)];
		ParseGrammarResult grammarResult = StellaDependencyParser.parseGrammarStructure(sentence).get(0);
		Collection<TypedDependency> dependencies = grammarResult.grammarStructure.allTypedDependencies();
		for (TypedDependency dependency: dependencies) {
			//System.out.println(dependency.gov() + " " + dependency.dep() + " " + dependency.reln());
			//System.out.println(dependency.gov().index() + " " + dependency.dep().index());
			if (!grammarRelationIds.containsKey(dependency.reln().toString())) {
				System.err.println(dependency.reln().toString());
			}
			int relationId = grammarRelationIds.get(dependency.reln().toString());
			int location = (sizeMaxSentence + 1) * (dependency.gov().index()) + (dependency.dep().index());
			//System.out.println(location);
			//System.out.println("-------------------------");
			vector[location] = relationId;
		}
		return vector;
	}
	
}
