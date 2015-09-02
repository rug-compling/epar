package epar.model;

import epar.parser.Action;

/**
 *
 * @author ke293
 */
public class StateFeatures {

    public static final int NUMBER_OF_TEMPLATES = 64;

    // Define an ID for each template. It serves 1) as an index into the array
    // of state feature values of each item and 2) as an ingredient to the
    // complete feature hash.
    // Group 1
    public static final int S0wp = 0;

    public static final int S0c = 1;

    public static final int S0pc = 2;

    public static final int S0wc = 3;

    public static final int S1wp = 4;

    public static final int S1c = 5;

    public static final int S1pc = 6;

    public static final int S1wc = 7;

    public static final int S2pc = 8;

    public static final int S2wc = 9;

    public static final int S3pc = 10;

    public static final int S3wc = 11;

    // Group 2
    public static final int Q0wp = 12;

    public static final int Q1wp = 13;

    public static final int Q2wp = 14;

    public static final int Q3wp = 15;

    // Group 3
    public static final int S0Lpc = 16;

    public static final int S0Lwc = 17;

    public static final int S0Rpc = 18;

    public static final int S0Rwc = 19;

    public static final int S0Upc = 20;

    public static final int S0Uwc = 21;

    public static final int S1Lpc = 22;

    public static final int S1Lwc = 23;

    public static final int S1Rpc = 24;

    public static final int S1Rwc = 25;

    public static final int S1Upc = 26;

    public static final int S1Uwc = 27;

    // Group 4
    public static final int S0wcS1wc = 28;

    public static final int S0cS1w = 29;

    public static final int S0wS1c = 30;

    public static final int S0cS1c = 31;

    public static final int S0wcQ0wp = 32;

    public static final int S0cQ0wp = 33;

    public static final int S0wcQ0p = 34;

    public static final int S0cQ0p = 35;

    public static final int S1wcQ0wp = 36;

    public static final int S1cQ0wp = 37;

    public static final int S1wcQ0p = 38;

    public static final int S1cQ0p = 39;

    // Group 5
    public static final int S0wcS1cQ0p = 40;

    public static final int S0cS1wcQ0p = 41;

    public static final int S0cS1cQ0wp = 42;

    public static final int S0cS1cQ0p = 43;

    public static final int S0pS1pQ0p = 44;

    public static final int S0wcQ0pQ1p = 45;

    public static final int S0cQ0wpQ1p = 46;

    public static final int S0cQ0pQ1wp = 47;

    public static final int S0cQ0pQ1p = 48;

    public static final int S0pQ0pQ1p = 49;

    public static final int S0wcS1cS2c = 49;

    public static final int S0cS1wcS2c = 50;

    public static final int S0cS1cS2wc = 51;

    public static final int S0cS1cS2c = 52;

    public static final int S0pS1pS2p = 53;

    // Group 6
    public static final int S0cS0HcS0Lc = 55;

    public static final int S0cS0HcS0Rc = 56;

    public static final int S1cS1HcS1Rc = 57;

    public static final int S0cS0RcQ0p = 58;

    public static final int S0cS0RcQ0w = 59;

    public static final int S0cS0LcS1c = 60;

    public static final int S0cS0LcS1w = 61;

    public static final int S0cS1cS1Rc = 62;

    public static final int S0wS1cS1Rc = 63;

    public final int[] hashes = new int[NUMBER_OF_TEMPLATES];

    public StepFeatures pairWithAction(Action action) {
        StepFeatures result = new StepFeatures();

        for (int templateID = 0; templateID < StateFeatures.NUMBER_OF_TEMPLATES; templateID++) {
            int featureHash = hashes[templateID];

            if (featureHash == 0) {
                // We take 0 to mean the state doesn't have this feature.
                continue;
            }

            featureHash = 29 * featureHash + templateID; // include feature template ID in hash
            featureHash = 29 * featureHash + action.type; // include action
            featureHash = 20 * featureHash + action.category;
            result.hashes[templateID] = featureHash;
        }

        return result;
    }

}
