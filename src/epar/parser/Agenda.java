package epar.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.model.Model;
import epar.oracle.Oracle;

public class Agenda {

	private static final int BEAM_WIDTH = 16; // TODO make configurable

	public final int generation;

	private final List<Candidate> candidates;

	private Agenda(int generation, List<Candidate> candidates) {
		this.generation = generation;
		this.candidates = candidates;
	}

	public Agenda nextAgenda(Grammar grammar, Model model, Oracle oracle) {
		List<Candidate> successors = new ArrayList<Candidate>();

		// Find all successors
		for (Candidate candidate : candidates) {
			candidate.findSuccessors(generation, successors, grammar, model, oracle);
		}

		// Sort by score, descending
		Collections.sort(successors, new Comparator<Candidate>() {

			@Override
			public int compare(Candidate arg0, Candidate arg1) {
				return arg1.score - arg0.score;
			}

		});

		// Keep the best candidates
		List<Candidate> nextCandidates = new ArrayList<Candidate>(
				successors.subList(0, Math.min(BEAM_WIDTH, successors.size())));

		// Also keep the best finished candidate, if any
		boolean gotFinishedCandidate = false;
		for (Candidate nextCandidate : nextCandidates) {
			if (nextCandidate.item.finished) {
				gotFinishedCandidate = true;
				break;
			}
		}
		if (!gotFinishedCandidate && successors.size() > BEAM_WIDTH) {
			for (Candidate successor : successors.subList(BEAM_WIDTH, successors.size())) {
				if (successor.item.finished) {
					nextCandidates.add(successor);
				}
			}
		}

		return new Agenda(generation + 1, nextCandidates);
	}

	public boolean noneCorrect() {
		for (Candidate candidate : candidates) {
			if (candidate.correct) {
				return false;
			}
		}

		return true;
	}

	public boolean allFinished() {
		for (Candidate candidate : candidates) {
			if (!candidate.item.finished) {
				return false;
			}
		}

		return true;
	}

	public Candidate getHighestScoring() {
		return candidates.get(0);
	}

	public Candidate getHighestScoringCorrect() {
		for (Candidate candidate : candidates) {
			if (candidate.correct) {
				return candidate;
			}
		}

		throw new IndexOutOfBoundsException("No correct candidate");
	}

	public static Agenda initial(Sentence sentence) {
		return new Agenda(0, Collections.singletonList(Candidate.initial(sentence)));
	}

	public List<Candidate> getCandidates() {
		return Collections.unmodifiableList(candidates);
	}

}
