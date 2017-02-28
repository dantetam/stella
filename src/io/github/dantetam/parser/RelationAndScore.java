package io.github.dantetam.parser;

import edu.stanford.nlp.trees.GrammaticalRelation;

public class RelationAndScore {
	public GrammaticalRelation relation;
	public double score;
	public RelationAndScore(GrammaticalRelation r, double s) {
		relation = r; score = s;
	}
}
