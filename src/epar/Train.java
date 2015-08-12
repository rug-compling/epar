package epar;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.model.Model;
import epar.node.Node;
import epar.oracle.Oracle;
import epar.oracle.ShallowActionSequenceOracle;
import epar.parser.Agenda;
import epar.parser.Candidate;
import epar.util.Pair;

public class Train {

	public static void trainForOneIteration(
			Iterable<Pair<Sentence, Node>> examples, Grammar grammar,
			Model model) {
		// TODO shuffle examples? Should be done offline for replicable results.
		for (Pair<Sentence, Node> example : examples) {
			trainOnOneExample(example.fst, example.snd, grammar, model);
		}
	}

	private static void trainOnOneExample(Sentence sentence, Node goldTree,
			Grammar grammar, Model model) {
		Oracle oracle = new ShallowActionSequenceOracle(goldTree);
		Agenda agenda = Decode.decode(Agenda.initial(sentence), grammar, model,
				oracle);
		Candidate highestScoring = agenda.getHighestScoring();
		Candidate highestScoringCorrect = agenda.getHighestScoringCorrect();

		if (highestScoring != highestScoringCorrect) {
			model.update(highestScoringCorrect, 1);
			model.update(highestScoring, -1);
		}
	}

	public static void main(String[] args) {
		if (args.length != 6) {
			System.err.println("USAGE: java Train SENTENCES GOLDTREES " +
					"RULES.BIN RULES.UN MODEL.IN MODEL.OUT");
			System.exit(1);
		}

		try {
			List<Sentence> sentences = Sentence
					.readSentences(new File(args[0]));
			List<Node> goldTrees = Node.readTrees(new File(args[1]));
			Grammar grammar = Grammar
					.load(new File(args[2]), new File(args[3]));
			Model model = Model.load(Collections.singletonList(new File(
					args[4])));
			File outputModelFile = new File(args[5]);

			if (sentences.size() != goldTrees.size()) {
				System.err.println("ERROR: Lengths of SENTENCES and GOLDTREES"
						+ " don't match");
				System.exit(1);
			}

			trainForOneIteration(Pair.zip(sentences, goldTrees), grammar, model);
			model.save(outputModelFile);
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getLocalizedMessage());
			System.exit(1);
		}
	}

}
