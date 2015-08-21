package epar.parser;

import java.util.List;

import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.model.Model;
import epar.model.StateFeatures;
import epar.oracle.Oracle;

public class Candidate {

	public final Candidate parent;
	public final Item item;
	public final double score;
	public final boolean correct;

	private Candidate(Candidate parent, Item item, double score, boolean correct) {
		this.parent = parent;
		this.item = item;
		this.score = score;
		this.correct = correct;
	}

	public void findSuccessors(int generation, List<Candidate> successors, Grammar grammar, Model model,
			Oracle oracle) {
		StateFeatures stateFeatures = item.extractFeatures();
		
		for (Item successorItem : item.successors(grammar)) {
			double successorScore = score + model.score(stateFeatures, successorItem.action);
			boolean successorCorrect = correct && oracle.accept(generation, successorItem);
			successors
					.add(new Candidate(this, successorItem, successorScore, successorCorrect));		}
	}

	public static Candidate initial(Sentence sentence) {
		return new Candidate(null, Item.initial(sentence), 0, true);
	}

	@Override
	public String toString() {
		return item.toString() + "/" + correct;
	}

}
