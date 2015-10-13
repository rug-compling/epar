package epar.model;

import static epar.model.Model.WEIGHT_VECTOR_SIZE;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import epar.parser.Candidate;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class UpdatableModel extends Model {
    
    // Each weight is saved together with the average of its values after each
    // training example in each iteration so far, which will be used in decoding
    // instead of the final weight (averaged perceptron). For efficiency, we
    // store averages indirectly: stateCountsForAverage contains a reference
    // state count for each weight (this is the state after which it was last
    // updated), sumsForAverage contains the sum of values up until the
    // reference state count, and stateCount indicates the current state count.
    // Before we update a weight and for each weight before we save the model,
    // we normalize the representation of the average so that the reference
    // state count is the same for each weight, namely the current state count.

    private final float[] sumsForAverage = new float[WEIGHT_VECTOR_SIZE];

    private final int[] stateCountsForAverage = new int[WEIGHT_VECTOR_SIZE];
    
    private int stateCount = 0;

    private void normalizeAverageRepresentationForWeight(int index) {
        int missedStates = stateCount - stateCountsForAverage[index];
        sumsForAverage[index] += missedStates * weights[index];
        stateCountsForAverage[index] = stateCount;
    }

    public void update(Candidate candidate, double delta) {
        while (candidate.parent != null) { // Iterate over all steps leading up to the candidate
            StepFeatures stepFeatures = new StepFeatures(
                    candidate.parent.item.extractFeatures(), candidate.item);

            for (int templateID = 0; templateID < StepFeatures.NUMBER_OF_TEMPLATES; templateID++) {
                int hash = stepFeatures.hashes[templateID];

                if (hash != 0) {
                    int index = index(hash);
                    normalizeAverageRepresentationForWeight(index);
                    weights[index] += delta;
                }
            }

            candidate = candidate.parent;
        }
        
        stateCount++;
    }
    
    public void save(File file) throws IOException {
        try (ObjectOutput output = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(file))))) {
            output.writeInt(stateCount);
            
            for (int i = 0; i < WEIGHT_VECTOR_SIZE; i++) {
                normalizeAverageRepresentationForWeight(i);
                output.writeFloat(weights[i]);
                output.writeFloat(sumsForAverage[i]);
            }
        }
    }

    public static UpdatableModel load(File file) throws IOException {
        UpdatableModel model = new UpdatableModel();

        try (final ObjectInput input = new ObjectInputStream(
                new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))))) {
            model.stateCount = input.readInt();

            for (int i = 0; i < WEIGHT_VECTOR_SIZE; i++) {
                model.stateCountsForAverage[i] = model.stateCount;
                model.weights[i] = input.readFloat();
                model.sumsForAverage[i] = input.readFloat();
            }
        }

        return model;
    }

}
