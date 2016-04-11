package epar;

import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.oracle.SemanticOracle;
import epar.parser.ForceAgenda;
import epar.parser.action.Action;
import epar.sem.Interpretation;
import epar.util.StringUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 *
 * @author p264360
 */
public class ProjectDerivations {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        if (args.length != 5) {
            System.err.println("Usage: java epar.ProjectDerivations INPUT.TRG GRAMMAR.TRG INTERPRETATIONS NUM_CPUS ORACLES.TRG");
            System.exit(1);
        }

        try {
            final List<Sentence> sentences = Sentence.readSentences(new File(args[0]));
            final Grammar grammar = Grammar.load(new File(args[1]));
            final List<Interpretation> targetInterpretations = Interpretation.read(new File(args[2]));
            int numCPUs = Integer.parseInt(args[3]);

            if (sentences.size() != targetInterpretations.size()) {
                throw new IllegalArgumentException("Numbers of sentences and target interpretations don't match.");
            }

            List<Future<List<List<Action>>>> parses = new ArrayList<>(sentences.size());
            ForkJoinPool pool;

            if (numCPUs <= 0) {
                pool = new ForkJoinPool();
            } else {
                pool = new ForkJoinPool(numCPUs);
            }

            for (int i = 0; i < sentences.size(); i++) {
                final Sentence sentence = sentences.get(i);
                final Interpretation targetInterpretation = targetInterpretations.get(i);

                ForkJoinTask<List<List<Action>>> parse = new RecursiveTask<List<List<Action>>>() {

                    @Override
                    protected List<List<Action>> compute() {
                        return ForceAgenda.forceDecode(sentence, grammar,
                                new SemanticOracle(targetInterpretation), 256);
                    }

                };

                pool.execute(parse);
                parses.add(parse);
            }

            try (Writer writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(new File(args[4])), "utf-8"))) {
                for (Future<List<List<Action>>> parse : parses) {
                    List<List<Action>> actionSequences = parse.get();
                    List<String> actionSequenceStrings = new ArrayList<>(
                            actionSequences.size());

                    for (List<Action> actionSequence : actionSequences) {
                        actionSequenceStrings.add(Action.sequenceToString(
                                actionSequence));
                    }

                    writer.write(StringUtil.join(actionSequenceStrings, " || "));
                    writer.write("\n");
                }
            }
        } catch (IOException ex) {
            System.err.println("ERROR: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }

}
