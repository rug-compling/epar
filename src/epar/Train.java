package epar;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.model.Model;
import epar.oracle.MultiActionSequenceOracle;
import epar.oracle.Oracle;
import epar.parser.Agenda;
import epar.parser.Candidate;
import java.util.logging.Level;

public class Train {

    private final static Logger LOGGER = Logger.getLogger(Train.class.getName());

    public static Model train(int numIterations, List<Sentence> sentences, List<Oracle> oracles, Grammar grammar,
            String outputFilePrefix) throws IOException {
        Model model = new Model();
        int trainingSetSize = sentences.size();

        if (oracles.size() != trainingSetSize) {
            throw new IllegalArgumentException("Numbers of sentences and oracles don't match");
        }

        for (int i = 0; i < numIterations; i++) {
            for (int e = 0; e < trainingSetSize; e++) {
                LOGGER.log(Level.INFO, "Training iteration: {0}, sentence: {1}", new Object[]{i, e});
                Sentence sentence = sentences.get(e);
                Oracle oracle = oracles.get(e);
                Agenda agenda = Decode.decode(Agenda.initial(sentence), grammar, model, oracle);
                Candidate highestScoring;
                Candidate highestScoringCorrect;

                try {
                    highestScoring = agenda.getHighestScoring();
                } catch (IndexOutOfBoundsException y) {
                    LOGGER.warning("No candidates, can't update");
                    continue;
                }

                try {
                    highestScoringCorrect = agenda.getHighestScoringCorrect();
                } catch (IndexOutOfBoundsException y) {
                    LOGGER.warning("No correct candidates, can't update");
                    continue;
                }

                if (highestScoring == highestScoringCorrect) {
                    LOGGER.info("Highest-scoring candidate is correct, no update");
                    continue;
                }

                model.update(trainingSetSize * i + e, highestScoringCorrect, 1.0);
                model.update(trainingSetSize * i + e, highestScoring, -1.0);
            }

            if (outputFilePrefix != null) {
                model.saveAveraged((i + 1) * trainingSetSize, new File(outputFilePrefix + "." + (i + 1)));
            }
        }

        return model;
    }

    public static void main(String[] args) {
        if (args.length != 6) {
            System.err.println("USAGE: java Train SENTENCES ORACLES RULES.BIN RULES.UN NUM_ITER MODEL.OUT");
            System.exit(1);
        }

        try {
            // Populate symbol tables:
            List<Sentence> sentences = Sentence.readSentences(new File(args[0]));
            List<Oracle> oracles = MultiActionSequenceOracle.load(new File(args[1]));
            Grammar grammar = Grammar.load(new File(args[2]), new File(args[3]));

            // Process further command-line arguments:
            int numIterations = Integer.parseInt(args[4]);
            String outputModelFile = args[5];

            if (sentences.size() != oracles.size()) {
                System.err.println("ERROR: Lengths of SENTENCES and ORACLES" + " don't match");
                System.exit(1);
            }

            train(numIterations, sentences, oracles, grammar, outputModelFile);
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

}
