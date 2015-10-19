package epar.model;

import epar.parser.Item;

/**
 * Contains the hashes of all features firing for one particular transition.
 * @author p264360
 */
public class StepFeatures {
    
    public static final int NUMBER_OF_TEMPLATES = StateFeatures.NUMBER_OF_TEMPLATES + 1;

    public final int[] hashes = new int[NUMBER_OF_TEMPLATES];
    
    /**
     * Takes the state features of an item and pairs them with the action
     * leading to the successor item to obtain the final features. For SHIFT
     * actions, also set the special lexical feature hash.
     * @param stateFeatures
     * @param successor 
     */
    public StepFeatures(StateFeatures stateFeatures, Item successor) {
        for (int templateID = 0; templateID < StateFeatures.NUMBER_OF_TEMPLATES; templateID++) {
            int featureHash = stateFeatures.hashes[templateID];

            if (featureHash == 0) {
                // We take 0 to mean the state doesn't have this feature.
                continue;
            }

            featureHash = 29 * featureHash + templateID; // include feature template ID in hash
            // We do *not* use the action's hashCode but are specifically
            // interested only in the type, category and semantics.
            featureHash = 29 * featureHash + successor.action.getType(); // include action
            featureHash = 29 * featureHash + successor.action.getCategory();
            hashes[templateID] = featureHash;
        }
        
        hashes[StateFeatures.NUMBER_OF_TEMPLATES] = successor.lexicalHash();
    }

}
