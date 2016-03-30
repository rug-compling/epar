package epar;

import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.oracle.SemanticOracle;
import epar.parser.ForceAgenda;
import epar.parser.action.Action;
import epar.sem.Interpretation;
import epar.util.StringUtil;
import java.io.File;
import java.io.FileNotFoundException;
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
        if (args.length != 4) {
            System.err.println("Usage: java epar.ProjectDerivations INPUT.TRG GRAMMAR.TRG TARGET_INTERPRETATIONS NUM_CPUS");
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
            
            for(Future<List<List<Action>>> parse : parses) {
                List<List<Action>> actionSequences = parse.get();
                List<String> actionSequenceStrings = new ArrayList<>(
                                actionSequences.size());

                for (List<Action> actionSequence : actionSequences) {
                    actionSequenceStrings.add(Action.sequenceToString(
                            actionSequence));
                }

                System.out.println(StringUtil.join(actionSequenceStrings, " || "));
            }
        } catch (FileNotFoundException ex) {
            System.err.println("ERROR: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }

}
