package epar;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.model.UpdatableModel;
import epar.oracle.MultiActionSequenceOracle;
import epar.oracle.Oracle;
import epar.parser.Agenda;
import epar.parser.Candidate;
import java.util.logging.Level;

public class Train {

    private final static Logger LOGGER = Logger.getLogger(Train.class.getName());

    // TODO should be 1 normally
    private final static int DWELL = 1;

    public static void train(List<Sentence> sentences, List<Oracle> oracles, Grammar grammar,
            UpdatableModel model) throws IOException {
        int trainingSetSize = sentences.size();

        if (oracles.size() != trainingSetSize) {
            throw new IllegalArgumentException("Numbers of sentences and oracles don't match");
        }

        for (int e = 0; e < trainingSetSize; e++) {
            Sentence sentence = sentences.get(e);
            Oracle oracle = oracles.get(e);

            // Dwell on the same training example up to DWELL times.
            for (int a = 0; a < DWELL; a++) {
                Agenda agenda = Decode.decode(Agenda.initial(sentence, 16, Integer.MAX_VALUE), grammar, model, oracle);
                Candidate highestScoring;
                Candidate highestScoringCorrect;

                try {
                    highestScoring = agenda.getHighestScoring();
                } catch (IndexOutOfBoundsException y) {
                    LOGGER.log(Level.WARNING, "Example {0}, attempt {1}: no candidates, no update", new Object[]{e, a});
                    break;
                }

                try {
                    highestScoringCorrect = agenda.getHighestScoringCorrect();
                } catch (IndexOutOfBoundsException y) {
                    LOGGER.log(Level.WARNING, "Example {0}, attempt {1}: no correct candidates, no update", new Object[]{e, a});
                    break;
                }

                if (highestScoring == highestScoringCorrect) {
                    LOGGER.log(Level.INFO, "Example {0}, attempt {1}: highest-scoring candidate is correct, empty update", new Object[]{e, a});
                    model.update(null, null);
                    break;
                }

                LOGGER.log(Level.INFO, "Example {0}, attempt {1}: performing udpate", new Object[]{e, a});
                model.update(highestScoringCorrect, highestScoring);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 5) {
            System.err.println("USAGE: java Train SENTENCES ORACLES GRAMMAR MODEL.IN MODEL.OUT");
            System.exit(1);
        }

        try {
            // Populate symbol tables:
            List<Sentence> sentences = Sentence.readSentences(new File(args[0]));
            List<Oracle> oracles = MultiActionSequenceOracle.load(new File(args[1]));
            Grammar grammar = Grammar.load(new File(args[2]));

            // Process further command-line arguments:
            UpdatableModel model = UpdatableModel.load(new File(args[3]));
            File outputModelFile = new File(args[4]);

            if (sentences.size() != oracles.size()) {
                System.err.println("ERROR: Lengths of SENTENCES and ORACLES" + " don't match");
                System.exit(1);
            }

            train(sentences, oracles, grammar, model);
            model.save(outputModelFile);
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

}
