package epar.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import epar.parser.Action;
import epar.parser.Candidate;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Model {

    private final static Logger LOGGER = Logger.getLogger(Model.class.getName());

    public static int WEIGHT_VECTOR_SIZE = 268435456; // -> 1 GiB float array

    public final float[] weights = new float[WEIGHT_VECTOR_SIZE];

    private final float[] sumsForAverage = new float[WEIGHT_VECTOR_SIZE];

    private final int[] stateCountsForAverage = new int[WEIGHT_VECTOR_SIZE];

    private void updateAverage(int currentStateCount, int index) {
        int missedStates = currentStateCount - stateCountsForAverage[index];
        sumsForAverage[index] += missedStates * weights[index];
        stateCountsForAverage[index] = currentStateCount;
    }

    public void update(int currentStateCount, Candidate candidate, double delta) {
        int updateCount = 0;

        while (candidate.parent != null) {
            ActionFeatures actionFeatures = candidate.parent.item.extractFeatures().pairWithAction(candidate.item.action);

            for (int templateID = 0; templateID < StateFeatures.NUMBER_OF_TEMPLATES; templateID++) {
                int hash = actionFeatures.hashes[templateID];

                if (hash != 0) {
                    int index = index(hash);
                    updateAverage(currentStateCount, index);
                    weights[index] += delta;
                    updateCount++;
                }
            }

            candidate = candidate.parent;
        }

        LOGGER.log(Level.FINE, "{0} individual feature updates", updateCount);
    }

    public double score(StateFeatures stateFeatures, Action action) {
        double score = 0;
        ActionFeatures actionFeatures = stateFeatures.pairWithAction(action);

        for (int templateID = 0; templateID < StateFeatures.NUMBER_OF_TEMPLATES; templateID++) {
            int hash = actionFeatures.hashes[templateID];

            if (hash != 0) {
                score += weights[index(hash)];
            }
        }

        return score;
    }

    public void saveAveraged(int currentStateCount, File file) throws IOException {
        try (ObjectOutput output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            for (int i = 0; i < WEIGHT_VECTOR_SIZE; i++) {
                updateAverage(currentStateCount, i);
                output.writeFloat(sumsForAverage[i] / stateCountsForAverage[i]);
            }
        }
    }

    public static Model load(File file) throws IOException {
        try (ObjectInput input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            Model model = new Model();

            for (int i = 0; i < WEIGHT_VECTOR_SIZE; i++) {
                model.weights[i] = input.readFloat();
            }

            // TODO should do sanity check that input is exhausted, how?
            return model;
        }
    }

    /**
     * Modulo the signed hash into an unsigned index within the range of the
     * weight vector.
     * @param hash
     * @return 
     */
    private int index(int hash) {
        if ((WEIGHT_VECTOR_SIZE & (WEIGHT_VECTOR_SIZE - 1)) == 0) { // check if power of two; evaluated at compile-time
            return hash & (WEIGHT_VECTOR_SIZE - 1); // positive modulo of a power of two
        } else {
            return (hash % WEIGHT_VECTOR_SIZE + WEIGHT_VECTOR_SIZE) % WEIGHT_VECTOR_SIZE; // positive modulo of an arbitrary number
        }
    }

}
