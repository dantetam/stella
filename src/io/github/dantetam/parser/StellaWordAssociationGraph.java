package io.github.dantetam.parser;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.graph.DirectedMultiGraph;
import edu.stanford.nlp.ling.TaggedWord;
import io.github.dantetam.util.MapUtil;

//public class StellaWordAssociationMap extends HashMap<String, Map<String, Double>> {
	
public class StellaWordAssociationGraph extends DirectedMultiGraph<String, RelationAndScore> {

	public StellaWordAssociationGraph() {
		super();
	}
	
	/*
	 * Return a 2d matrix of size words*words, where every entry i to j,
	 * represents the correlation value calculated for word i as the governor and word j as the dependent.
	 */
	public static double[][] getFullAssociationMatrix(List<TaggedWord> taggedWords, StellaWordAssociationGraph graph) {
		double[][] result = new double[taggedWords.size()][taggedWords.size()];
		for (int i = 0; i < taggedWords.size(); i++) {
			for (int j = 0; j < taggedWords.size(); j++) {
				if (i <= j) continue;
				String vertexU = taggedWords.get(i).word(), vertexV = taggedWords.get(j).word();
				if (vertexU.equals(vertexV)) continue;
				//List<TaggedWord> shortestPathVertices = graph.getShortestPath(vertexU, vertexV);
				List<RelationAndScore> shortestPathEdges;
				try {
					shortestPathEdges = graph.getShortestPathEdges(vertexU, vertexV, false);
				} catch (NullPointerException e) {
					shortestPathEdges = null;
				}
				double finalScore = 1;
				//System.out.println(vertexU + " " + vertexV);
				/*if (shortestPathEdges == null) {
					shortestPathEdges = graph.getShortestPathEdges(vertexU, vertexV, false);
					finalScore *= 0.5d;
				}*/
				if (shortestPathEdges == null) {
					finalScore *= 0;
				}
				else {
					for (RelationAndScore relationAndScore: shortestPathEdges) {
						finalScore *= relationAndScore.score;
					}
					finalScore *= Math.pow(0.8d, shortestPathEdges.size());
				}
				result[i][j] = finalScore;
			}
		}
		return result;
	}
	
	/*
	 * Get a correlation matrix and list of words,
	 * and return a sorted map of high ranking correlated word pairs.
	 */
	public static LinkedHashMap<String[], Double> matrixFindBestCorrelation(List<TaggedWord> taggedWords, double[][] matrix) {
		Map<Integer, Double> total = new HashMap<>();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				total.put(i*matrix.length + j, matrix[i][j]);
			}
		}
		LinkedHashMap<Integer, Double> sorted = MapUtil.sortByValueDescending(total);
		LinkedHashMap<String[], Double> finalResult = new LinkedHashMap<>();
		for (Map.Entry<Integer, Double> entry : sorted.entrySet()) {
			int index = entry.getKey();
			int r = index / matrix.length, c = index % matrix.length;
			String wordI = taggedWords.get(r).toString(), wordJ = taggedWords.get(c).toString();
			finalResult.put(new String[]{wordI, wordJ}, entry.getValue());
		}
		return finalResult;
	}
	
}
