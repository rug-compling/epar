/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epar.model;

import epar.parser.Action;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.logging.Logger;

/**
 *
 * @author p264360
 */
public class Model {
    
    protected static final Logger LOGGER = Logger.getLogger(UpdatableModel.class.getName());
    
    public static int WEIGHT_VECTOR_SIZE = 268435456; // -> 1 GiB float array
    
    public final float[] weights = new float[WEIGHT_VECTOR_SIZE];

    public static Model load(File file) throws IOException {
        try (final ObjectInput input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            Model model = new Model();
            
            for (int i = 0; i < WEIGHT_VECTOR_SIZE; i++) {
                model.weights[i] = input.readFloat();
            }
            
            // TODO should do sanity check that input is exhausted, how?
            return model;
        }
    }

    public double score(StateFeatures stateFeatures, Action action) {
        double score = 0;
        StepFeatures stepFeatures = stateFeatures.pairWithAction(action);
        
        for (int templateID = 0; templateID < StateFeatures.NUMBER_OF_TEMPLATES; templateID++) {
            int hash = stepFeatures.hashes[templateID];
            
            if (hash != 0) {
                score += weights[index(hash)];
            }
        }
        
        return score;
    }

    /**
     * Modulo the signed hash into an unsigned index within the range of the
     * weight vector.
     *
     * @param hash
     * @return
     */
    protected int index(int hash) {
        if ((WEIGHT_VECTOR_SIZE & (WEIGHT_VECTOR_SIZE - 1)) == 0) {
            // check if power of two; evaluated at compile-time
            return hash & (WEIGHT_VECTOR_SIZE - 1); // positive modulo of a power of two
        } else {
            return (hash % WEIGHT_VECTOR_SIZE + WEIGHT_VECTOR_SIZE) % WEIGHT_VECTOR_SIZE; // positive modulo of an arbitrary number
        }
    }
    
}
