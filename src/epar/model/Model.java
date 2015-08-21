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

public class Model {

    public static int WEIGHT_VECTOR_SIZE = 268435456; // -> 1 GiB float array

    private final float[] weights = new float[WEIGHT_VECTOR_SIZE];

    private final float[] sumsForAverage = new float[WEIGHT_VECTOR_SIZE];

    private final int[] stateCountsForAverage = new int[WEIGHT_VECTOR_SIZE];

    public double score(StateFeatures stateFeatures, Action action) {
        double score = 0;
        int[] values = stateFeatures.values;
        int actionHash = action.hashCode();

        for (int i = 0; i < StateFeatures.NUMBER_OF_TEMPLATES; i++) {
            int hash = computeHash(i, values, actionHash);
            score += weights[hash];
        }

        return score;
    }

    private int computeHash(int templateID, int[] stateHashes, int actionHash) {
        int hash = stateHashes[templateID];
        hash = 29 * hash + templateID; // include feature template ID in hash
        hash = 29 * hash + actionHash; // include action
        
        if ((WEIGHT_VECTOR_SIZE & (WEIGHT_VECTOR_SIZE - 1)) == 0) { // check if power of two; evaluated at compile-time
            return hash & (WEIGHT_VECTOR_SIZE - 1); // positive modulo of a power of two
        } else {
            return (hash % WEIGHT_VECTOR_SIZE + WEIGHT_VECTOR_SIZE) % WEIGHT_VECTOR_SIZE; // positive modulo of an arbitrary number
        }
    }

    private void updateAverage(int currentStateCount, int hash) {
        int missedStates = currentStateCount - stateCountsForAverage[hash];
        sumsForAverage[hash] += missedStates * weights[hash];
        stateCountsForAverage[hash] = currentStateCount;

    }

    public void update(int currentStateCount, Candidate candidate, double delta) {
        while (candidate.parent != null) {
            int[] stateHashes = candidate.parent.item.extractFeatures().values;
            int actionHash = candidate.item.action.hashCode();

            for (int i = 0; i < StateFeatures.NUMBER_OF_TEMPLATES; i++) {
                int hash = computeHash(i, stateHashes, actionHash);
                updateAverage(currentStateCount, hash);
                weights[hash] += delta;

            }

            candidate = candidate.parent;
        }
    }

    public void saveAveraged(int currentStateCount, File file) throws IOException {
        try (ObjectOutput output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            for (int i = 0; i < WEIGHT_VECTOR_SIZE; i++) {
                updateAverage(currentStateCount, i);
                output.writeFloat(weights[i] / sumsForAverage[i]);
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

}
