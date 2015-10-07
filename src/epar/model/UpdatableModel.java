package epar.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import epar.parser.Candidate;
import java.io.BufferedOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPOutputStream;

public class UpdatableModel extends Model {

    private final float[] sumsForAverage = new float[WEIGHT_VECTOR_SIZE];

    private final int[] stateCountsForAverage = new int[WEIGHT_VECTOR_SIZE];

    private void updateAverage(int currentStateCount, int index) {
        int missedStates = currentStateCount - stateCountsForAverage[index];
        sumsForAverage[index] += missedStates * weights[index];
        stateCountsForAverage[index] = currentStateCount;
    }

    public void update(int currentStateCount, Candidate candidate, double delta) {
        while (candidate.parent != null) { // Iterate over all steps leading up to the candidate
            StepFeatures stepFeatures = candidate.parent.item.extractFeatures().pairWithAction(candidate.item.action);

            for (int templateID = 0; templateID < StateFeatures.NUMBER_OF_TEMPLATES; templateID++) {
                int hash = stepFeatures.hashes[templateID];

                if (hash != 0) {
                    int index = index(hash);
                    updateAverage(currentStateCount, index);
                    weights[index] += delta;
                }
            }

            candidate = candidate.parent;
        }
    }
    
    public void save(int currentStateCount, File file) throws IOException {
        try (ObjectOutput output = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(file))))) {
            output.writeInt(currentStateCount);
            
            for (int i = 0; i < WEIGHT_VECTOR_SIZE; i++) {
                updateAverage(currentStateCount, i);
                output.writeFloat(weights[i]);
                output.writeFloat(sumsForAverage[i]);
            }
        }
    }

}
