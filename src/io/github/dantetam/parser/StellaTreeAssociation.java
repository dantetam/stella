package io.github.dantetam.parser;

import java.awt.Window.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;

/*
 * Experimental class to find associations between words in a grammar structure
 * The base model being an unknown set of weights for certain dependencies (0.8 for nmod, 0.6 for amod, etc.),
 * multiplied by the inverse of the distance of the connection.
 * A set of associations is calculated for every NN, NNP, and NNS.
 */
public class StellaTreeAssociation {
	
	private Map<String, Double> grammarRelationScores;
	
	public StellaTreeAssociation() {
		grammarRelationScores = new HashMap<>();
	}
	
	/*
	 * Takes in a ParseGrammarResult, which is a list of words combined together 
	 * in a semi-connected grammatical structure. It returns a DirectedMultiGraph,
	 * which can be analyzed for summary, word associations, etc.
	 */
	public StellaWordAssociationGraph processText(ParseGrammarResult parseGrammarResult) {
		StellaWordAssociationGraph resultGraph = new StellaWordAssociationGraph();
		
		List<TaggedWord> words = parseGrammarResult.taggedWords;
		GrammaticalStructure gs = parseGrammarResult.grammarStructure;
		
		for (TaggedWord word: words) {
			resultGraph.addVertex(word);
		}
		
 		/*for (int i = 0; i < words.size(); i++) {
			for (int j = 0; j < words.size(); j++) {
				if (i == j) continue;
				System.out.println(i + " " + j + " " + words.size());
				System.out.println(words.get(0).tag());
				System.out.println(words.get(1).tag());
				System.out.println(words.get(2).tag());
				System.out.println(words.get(words.size() - 1).tag());
				System.out.println(words.get(0).toString());
				System.out.println(words.get(1).toString());
				System.out.println(words.get(2).toString());
				System.out.println(gs.);
				System.out.println(gs.getGrammaticalRelation(1,2));
				GrammaticalRelation relation = gs.getGrammaticalRelation(i, j);
				//System.out.println(i + " " + j + " " + words.size());
				if (relation == null) continue;
				
				String depAbbr = relation.toString();
				if (!grammarRelationScores.containsKey(depAbbr)) {
					grammarRelationScores.put(depAbbr, 1.0d);
				}
				double score = grammarRelationScores.get(depAbbr);
				
				TaggedWord vertexU = words.get(i), vertexV = words.get(j);
				resultGraph.add(vertexU, vertexV, new RelationAndScore(relation, score));
			}
		}*/
		
		Collection<TypedDependency> deps = gs.allTypedDependencies();
		for (TypedDependency dep: deps) {
			GrammaticalRelation reln = dep.reln();
			String depAbbr = reln.toString();
			if (!grammarRelationScores.containsKey(depAbbr)) {
				grammarRelationScores.put(depAbbr, 1.0d);
			}
			double score = grammarRelationScores.get(depAbbr);
			
			resultGraph.add(dep.gov(), dep.dep(), new RelationAndScore(reln, score));
		}
		
 		return resultGraph;
	}
	
}









