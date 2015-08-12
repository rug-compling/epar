package epar;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.model.Model;
import epar.node.Node;
import epar.oracle.AcceptAllOracle;
import epar.oracle.Oracle;
import epar.parser.Agenda;
import epar.util.ListUtil;
import epar.util.StringUtil;

public class Decode {

	private final static Logger LOGGER = Logger.getLogger(Decode.class
			.getName());

	public static Agenda decode(Agenda agenda, Grammar grammar, Model model,
			Oracle oracle) {
		//LOGGER.info("Input agenda: " + agenda.getCandidates());
		
		Agenda nextAgenda = agenda.nextAgenda(grammar, model, oracle);

		if (nextAgenda.noneCorrect()) {
			return agenda;
		}

		if (nextAgenda.allFinished()) {
			return nextAgenda;
		}

		return decode(nextAgenda, grammar, model, oracle);
	}

	public static void main(String[] args) {
		if (args.length < 5) {
			System.err.println("USAGE: java Decode SENTENCES.IN RULES.BIN "
					+ "RULES.UN TREES.OUT MODEL1 MODEL2 ... MODELN");
			System.exit(1);
		}

		try {
			List<Sentence> inputSentences = Sentence.readSentences(new File(
					args[0]));
			Grammar grammar = Grammar
					.load(new File(args[1]), new File(args[2]));
			File outputFile = new File(args[3]);
			List<File> modelFiles = new ArrayList<File>(args.length - 4);

			for (int i = 4; i < args.length; i++) {
				modelFiles.add(new File(args[i]));
			}

			Model model = Model.load(modelFiles);
			Oracle oracle = new AcceptAllOracle();

			try (Writer writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputFile), "utf-8"))) {
				// TODO concurrency
				int i = 1;
				
				for (Sentence sentence : inputSentences) {
					LOGGER.info("Decoding sentence " + i++);
					
					Agenda result = decode(Agenda.initial(sentence), grammar,
							model, oracle);
					List<Node> nodes = ListUtil.listFromIterable(result
							.getHighestScoring().item.stack);
					Collections.reverse(nodes);
					writer.write(StringUtil.join(nodes, " ") + "\n");
				}
			}
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getLocalizedMessage());
			System.exit(1);
		}
	}

}
