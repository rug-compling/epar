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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Logger;

import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.model.Model;
import epar.node.Node;
import epar.oracle.AcceptAllOracle;
import epar.oracle.Oracle;
import epar.parser.Agenda;
import epar.parser.Candidate;
import epar.util.ListUtil;
import epar.util.Stack;
import epar.util.StringUtil;

public class Decode {

	private final static Logger LOGGER = Logger.getLogger(Decode.class.getName());

	public static Agenda decode(Agenda agenda, Grammar grammar, Model model, Oracle oracle) {
		LOGGER.info("Input beam: " + agenda.getBeam());

		Agenda nextAgenda = agenda.nextAgenda(grammar, model, oracle);

		if (nextAgenda.noneCorrectWithinBeam()) {
			LOGGER.info("Early update in generation " + nextAgenda.generation);
			return nextAgenda;
		}

		if (nextAgenda.allFinishedWithinBeam()) {
			LOGGER.info("Parsing finished in generation " + nextAgenda.generation);
			return nextAgenda;
		}

		return decode(nextAgenda, grammar, model, oracle);
	}
	
	private static Stack<Node> selectParse(Agenda finalAgenda) {
		// Try to return the highest-scoring non-fragmentary analysis:
		for (Candidate candidate : finalAgenda.getBeam()) {
			if (candidate.item.stack.size() == 1) {
				return candidate.item.stack;
			}
		}
		
		// If none exists, return the highest-scoring fragmentary analysis:
		return finalAgenda.getHighestScoring().item.stack;
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		if (args.length < 5) {
			System.err.println(
					"USAGE: java Decode SENTENCES.IN RULES.BIN RULES.UN NUMCPUS TREES.OUT MODEL1 MODEL2 ... MODELN");
			System.exit(1);
		}

		try {
			List<Sentence> inputSentences = Sentence.readSentences(new File(args[0]));
			final Grammar grammar = Grammar.load(new File(args[1]), new File(args[2]));
			int numCPUs = Integer.parseInt(args[3]);
			File outputFile = new File(args[4]);
			List<File> modelFiles = new ArrayList<File>(args.length - 5);

			for (int i = 5; i < args.length; i++) {
				modelFiles.add(new File(args[i]));
			}

			final Model model = Model.load(modelFiles);
			final Oracle oracle = new AcceptAllOracle();
			
			List<Future<String>> parses = new ArrayList<Future<String>>(inputSentences.size());
			ForkJoinPool pool;
			
			if (numCPUs <= 0) {
				pool = new ForkJoinPool();
			} else {
				pool = new ForkJoinPool(numCPUs);
			}
			
			// Schedule sentences for parsing
			for (final Sentence sentence : inputSentences) {
				@SuppressWarnings("serial")
				ForkJoinTask<String> parse = new RecursiveTask<String>() {

					@Override
					protected String compute() {
						Agenda finalAgenda = decode(Agenda.initial(sentence), grammar, model, oracle);
						List<Node> nodes = ListUtil.listFromIterable(selectParse(finalAgenda));
						Collections.reverse(nodes);
						return StringUtil.join(nodes, " ") + "\n";
					}
					
				};
				
				pool.execute(parse);
				parses.add(parse);
			}
			
			// Retrieve parses and write them to STDOUT
			try (Writer writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"))) {
				int i = 0;
				for (Future<String> parse : parses) {
					i++;
					
					try {
						LOGGER.info("At sentence " + i);
						writer.write(parse.get());
					} catch (InterruptedException | ExecutionException e) {
						throw e;
					}
				}
			}
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getLocalizedMessage());
			System.exit(1);
		}
	}

}
