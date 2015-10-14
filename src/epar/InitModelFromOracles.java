package epar;

import epar.data.Sentence;
import epar.grammar.Grammar;
import java.io.File;

import epar.model.UpdatableModel;
import epar.oracle.MultiActionSequenceOracle;
import epar.oracle.Oracle;
import epar.parser.Action;
import epar.parser.Item;
import epar.parser.OracleAgenda;
import epar.util.Counter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This model initializer assigns every lexical item an initial score based on
 * its frequency in the training data, weighted by its length.
 *
 * @author p264360
 */
public class InitModelFromOracles {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("USAGE: java InitModelFromOracles SENTENCES ORACLES GRAMMAR MODEL");
            System.exit(1);
        }

        try {
            // Populate symbol pool. This has to be the same as for training.
            List<Sentence> sentences = Sentence.readSentences(new File(args[0]));
            List<Oracle> oracles = MultiActionSequenceOracle.load(new File(args[1]));
            Grammar grammar = Grammar.load(new File(args[2]));

            int trainingSetSize = sentences.size();

            if (oracles.size() != trainingSetSize) {
                throw new IllegalArgumentException(
                        "Numbers of sentences and oracles don't match");
            }
            
            Counter<Integer> counter = new Counter<>();

            for (int i = 0; i < trainingSetSize; i++) {
                List<List<Item>> goldSequences =
                        OracleAgenda.computeGoldItemSequences(sentences.get(i),
                        grammar, oracles.get(i));
                
                for (List<Item> sequence : goldSequences) {
                    for (Item item : sequence) {
                        if (item.action.type == Action.TYPE_SHIFT) {
                            counter.add(item.lexicalHash(), 1.0 * item.action.length);
                        }
                    }
                }
            }
            
            // We scale the weights so that the most common lexical item is
            // initialized to 10, similar to Kwiatkowski et al. (2010).
            double weightFactor = 10.0 / counter.getMaxCount();

            // Create model
            UpdatableModel model = new UpdatableModel();
            
            for (Map.Entry<Integer, Double> entry : counter.entrySet()) {
                model.update(entry.getKey(), weightFactor * entry.getValue());
            }

            // Save model
            model.save(new File(args[3]));
        } catch (IOException ex) {
            Logger.getLogger(InitModelFromOracles.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

}
