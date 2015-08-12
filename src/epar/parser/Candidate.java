package epar.parser;

import java.util.Collections;
import java.util.List;

import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.model.Model;
import epar.oracle.Oracle;

public class Candidate {

	public final int generation;
	public final Candidate parent;
	public final Item item;
	public final List<String> stateFeatures;
	public final int score;
	public final boolean correct;

	private Candidate(int generation, Candidate parent, Item item,
			List<String> stateFeatures, int score, boolean correct) {
		this.generation = generation;
		this.parent = parent;
		this.item = item;
		this.stateFeatures = stateFeatures;
		this.score = score;
		this.correct = correct;
	}

	public void findSuccessors(List<Candidate> successors, Grammar grammar,
			Model model, Oracle oracle) {
		for (Item successorItem : item.successors(grammar)) {
			List<String> successorStateFeatures = item.extractFeatures();
			int successorScore = score
					+ model.score(stateFeatures, successorItem.action);
			boolean successorCorrect = correct
					&& oracle.accept(generation, successorItem);
			successors.add(new Candidate(generation + 1, this, successorItem,
					successorStateFeatures, successorScore, successorCorrect));
		}
	}

	public static Candidate initial(Sentence sentence) {
		return new Candidate(0, null, Item.initial(sentence),
				Collections.<String> emptyList(), 0, true);
	}
	
	@Override
	public String toString() {
		return item.toString();
	}

}
