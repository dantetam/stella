package io.github.dantetam.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SentenceVectorizer {

	public static final void main(String[] args) {
		List<String> listSentences = new ArrayList<>();
		listSentences.add("Hello, this is a test this is a test of the outdoor warning system system test test test");
		Map<String, Integer> results = uniqueWords(listSentences);
		findWordIdMap(results, 5);
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
		System.out.println(sorted.entrySet());
		int i = 0;
		for (Entry<String, Integer> entry: sorted.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
			results.put(entry.getKey(), entry.getValue());
			i++;
			if (i == desiredNum) {
				break;
			}
		}
		return results;
	}
	
	public int[] encodeSentenceAsWordVec(String sentence) {
		String[] words = sentence.split(" ");
		int[] vector = new int[words.length];
		//for (String str)
		return vector;
	}
	
}
