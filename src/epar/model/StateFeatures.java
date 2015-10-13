package epar.model;

import epar.data.LexicalItem;
import epar.data.SentencePosition;
import epar.grammar.BinaryRule;
import epar.node.BinaryNode;
import epar.node.LexicalNode;
import epar.node.Node;
import epar.node.UnaryNode;
import epar.parser.Action;
import epar.parser.Item;
import epar.util.SymbolPool;

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

    private static final SentencePosition NONE_SENTENCE_POSITION =
            new SentencePosition(SymbolPool.NONE, SymbolPool.NONE, null);

    private static final Node NONE_NODE = new LexicalNode(LexicalItem.NONE);

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
            // We do *not* use the action's hashCode but are specifically
            // interested only in the type, category and semantics.
            featureHash = 29 * featureHash + action.type; // include action
            featureHash = 29 * featureHash + action.category;
            result.hashes[templateID] = featureHash;
        }

        return result;
    }

    public static StateFeatures extractFeatures(Item item) {
        StateFeatures features = new StateFeatures();

        Node S0 = item.stack.get(0, NONE_NODE);

        // ZPar does not extract any features from the initial item. Why? Oh
        // well. Note that this makes every instance of if (S0 != NONE_NODE)
        // below redundant, but we keep them around in case we want to change
        // this.
        if (S0 == NONE_NODE) {
            return features;
        }

        Node S1 = item.stack.get(1, NONE_NODE);
        Node S2 = item.stack.get(2, NONE_NODE);
        Node S3 = item.stack.get(3, NONE_NODE);
        SentencePosition Q0 = item.queue.get(0, NONE_SENTENCE_POSITION);
        SentencePosition Q1 = item.queue.get(1, NONE_SENTENCE_POSITION);
        SentencePosition Q2 = item.queue.get(2, NONE_SENTENCE_POSITION);
        SentencePosition Q3 = item.queue.get(3, NONE_SENTENCE_POSITION);
        Node S0L = getLeftNonHeadChild(S0);
        Node S0R = getRightNonHeadChild(S0);
        Node S0H = getHeadChild(S0);
        Node S0U = getUnaryChild(S0);
        Node S1L = getLeftNonHeadChild(S1);
        Node S1R = getRightNonHeadChild(S1);
        Node S1H = getHeadChild(S1);
        Node S1U = getUnaryChild(S1);

        // Group 1
        if (S0 != NONE_NODE) {
            features.hashes[StateFeatures.S0wp] = hash(S0.lexicalHead.form, S0.lexicalHead.pos);
            features.hashes[StateFeatures.S0wp] = hash(S0.lexicalHead.form, S0.lexicalHead.pos);
            features.hashes[StateFeatures.S0c] = hash(S0.category);
            features.hashes[StateFeatures.S0pc] = hash(S0.lexicalHead.pos, S0.category);
            features.hashes[StateFeatures.S0wc] = hash(S0.lexicalHead.form, S0.category);
        }

        if (S1 != NONE_NODE) {
            features.hashes[StateFeatures.S1wp] = hash(S1.lexicalHead.form, S1.lexicalHead.pos);
            features.hashes[StateFeatures.S1c] = hash(S1.category);
            features.hashes[StateFeatures.S1pc] = hash(S1.lexicalHead.pos, S1.category);
            features.hashes[StateFeatures.S1wc] = hash(S1.lexicalHead.form, S1.category);
        }

        if (S2 != NONE_NODE) {
            features.hashes[StateFeatures.S2pc] = hash(S2.lexicalHead.pos, S2.category);
            features.hashes[StateFeatures.S2wc] = hash(S2.lexicalHead.form, S2.category);
        }

        if (S3 != NONE_NODE) {
            features.hashes[StateFeatures.S3pc] = hash(S3.lexicalHead.pos, S3.category);
            features.hashes[StateFeatures.S3wc] = hash(S3.lexicalHead.form, S3.category);
        }

        // Group 2
        if (Q0 != NONE_SENTENCE_POSITION) {
            features.hashes[StateFeatures.Q0wp] = hash(Q0.form, Q0.pos);
        }

        if (Q1 != NONE_SENTENCE_POSITION) {
            features.hashes[StateFeatures.Q1wp] = hash(Q1.form, Q1.pos);
        }

        if (Q2 != NONE_SENTENCE_POSITION) {
            features.hashes[StateFeatures.Q2wp] = hash(Q2.form, Q2.pos);
        }

        if (Q3 != NONE_SENTENCE_POSITION) {
            features.hashes[StateFeatures.Q3wp] = hash(Q3.form, Q3.pos);
        }

        // Group 3
        if (S0L != NONE_NODE) {
            features.hashes[StateFeatures.S0Lpc] = hash(S0L.lexicalHead.pos, S0L.category);
            features.hashes[StateFeatures.S0Lwc] = hash(S0L.lexicalHead.form, S0L.category);
        }

        if (S0R != NONE_NODE) {
            features.hashes[StateFeatures.S0Rpc] = hash(S0R.lexicalHead.pos, S0R.category);
            features.hashes[StateFeatures.S0Rwc] = hash(S0R.lexicalHead.form, S0R.category);
        }

        if (S0U != NONE_NODE) {
            features.hashes[StateFeatures.S0Upc] = hash(S0U.lexicalHead.pos, S0U.category);
            features.hashes[StateFeatures.S0Uwc] = hash(S0U.lexicalHead.form, S0U.category);
        }

        if (S1L != null) {
            features.hashes[StateFeatures.S1Lpc] = hash(S1L.lexicalHead.pos, S1L.category);
            features.hashes[StateFeatures.S1Lwc] = hash(S1L.lexicalHead.form, S1L.category);
        }

        if (S1R != NONE_NODE) {
            features.hashes[StateFeatures.S1Rpc] = hash(S1R.lexicalHead.pos, S1R.category);
            features.hashes[StateFeatures.S1Rwc] = hash(S1R.lexicalHead.form, S1R.category);
        }

        if (S1U != NONE_NODE) {
            features.hashes[StateFeatures.S1Upc] = hash(S1U.lexicalHead.pos, S1U.category);
            features.hashes[StateFeatures.S1Uwc] = hash(S1U.lexicalHead.form, S1U.category);
        }

        // Group 4
        if (S1 != NONE_NODE) {
            features.hashes[StateFeatures.S0wcS1wc] = hash(S0.lexicalHead.form, S0.lexicalHead.pos,
                    S1.lexicalHead.form, S1.lexicalHead.pos);
            features.hashes[StateFeatures.S0cS1w] = hash(S0.category, S1.lexicalHead.form);
            features.hashes[StateFeatures.S0wS1c] = hash(S0.lexicalHead.form, S1.category);
        }

        if (S0 != NONE_NODE && Q0 != NONE_SENTENCE_POSITION) {
            features.hashes[StateFeatures.S0wcQ0wp] = hash(S0.lexicalHead.form, S0.category,
                    Q0.form, Q0.pos);
            features.hashes[StateFeatures.S0cQ0wp] = hash(S0.category, Q0.form, Q0.pos);
            features.hashes[StateFeatures.S0wcQ0p] = hash(S0.lexicalHead.form, S0.category, Q0.pos);
            features.hashes[StateFeatures.S0cQ0p] = hash(S0.category, Q0.pos);
        }

        if (S1 != NONE_NODE && Q0 != NONE_SENTENCE_POSITION) {
            features.hashes[StateFeatures.S1wcQ0wp] = hash(S1.lexicalHead.form, S1.category,
                    Q0.form, Q0.pos);
            features.hashes[StateFeatures.S1cQ0wp] = hash(S1.category, Q0.form, Q0.pos);
            features.hashes[StateFeatures.S1wcQ0p] = hash(S1.lexicalHead.form, S1.category, Q0.pos);
            features.hashes[StateFeatures.S1cQ0p] = hash(S1.category, Q0.pos);
        }

        // Group 5: trigrams
        // Here we also refer to the (dummy) tags and categories of non-existing
        // nodes and words, just not for the focus item (middle of trigram) and
        // not to word forms.
        // S1 S0 Q0
        if (S0 != null) {
            features.hashes[StateFeatures.S0wcS1cQ0p] = hash(S0.lexicalHead.form, S0.category, S1.category, Q0.pos);
            if (S1 != NONE_NODE) {
                features.hashes[StateFeatures.S0cS1wcQ0p] = hash(S0.category, S1.lexicalHead.form, S1.category, Q0.pos);
            }
            if (Q0 != NONE_SENTENCE_POSITION) {
                features.hashes[StateFeatures.S0cS1cQ0wp] = hash(S0.category, S1.category, Q0.form, Q0.pos);
            }
            features.hashes[StateFeatures.S0cS1cQ0p] = hash(S0.category, S1.category, Q0.pos);
            features.hashes[StateFeatures.S0pS1pQ0p] = hash(S0.lexicalHead.pos, S1.lexicalHead.pos, Q0.pos);
        }

        // S0 Q0 Q1
        if (Q0 != NONE_SENTENCE_POSITION) {
            assert S0 != null;
            if (S0 != NONE_NODE) {
                features.hashes[StateFeatures.S0wcQ0pQ1p] = hash(S0.lexicalHead.form, S0.category, Q0.pos, Q1.pos);
            }
            features.hashes[StateFeatures.S0cQ0wpQ1p] = hash(S0.category, Q0.form, Q0.pos, Q1.pos);
            if (Q1 != NONE_SENTENCE_POSITION) {
                features.hashes[StateFeatures.S0cQ0pQ1wp] = hash(S0.category, Q0.pos, Q1.form, Q1.pos);
            }
            features.hashes[StateFeatures.S0cQ0pQ1p] = hash(S0.category, Q0.pos, Q1.pos);
            features.hashes[StateFeatures.S0pQ0pQ1p] = hash(S0.lexicalHead.pos, Q0.pos, Q1.pos);
        }

        // S2 S1 S0
        if (S1 != NONE_NODE) {
            assert S0 != null;
            features.hashes[StateFeatures.S0wcS1cS2c] = hash(S0.lexicalHead.form, S0.category, S1.category, S2.category);
            features.hashes[StateFeatures.S0cS1wcS2c] = hash(S0.category, S1.lexicalHead.form, S1.category, S2.category);
            if (S2 != NONE_NODE) {
                features.hashes[StateFeatures.S0cS1cS2wc] = hash(S0.category, S1.category, S2.lexicalHead.form, S2.category);
            }
            features.hashes[StateFeatures.S0cS1cS2c] = hash(S0.category, S1.category, S2.category);
            features.hashes[StateFeatures.S0pS1pS2p] = hash(S0.lexicalHead.pos, S1.lexicalHead.pos,
                    S2.lexicalHead.pos);
        }

        // Group 6
        // TODO We are not extracting any features referring to nonexisting
        // nodes here, is this right?
        if (S0L != NONE_NODE) {
            assert S0 != null;
            features.hashes[StateFeatures.S0cS0HcS0Lc] = hash(S0.category, S0H.category, S0L.category);
        }

        if (S0R != NONE_NODE) {
            assert S0 != null;
            features.hashes[StateFeatures.S0cS0HcS0Rc] = hash(S0.category, S0H.category, S0R.category);
        }

        if (S1R != NONE_NODE) {
            features.hashes[StateFeatures.S1cS1HcS1Rc] = hash(S1.category, S1H.category, S1R.category);
        }

        if (S0R != NONE_NODE && Q0 != NONE_SENTENCE_POSITION) {
            assert S0 != null;
            features.hashes[StateFeatures.S0cS0RcQ0p] = hash(S0.category, S0R.category, Q0.pos);
            features.hashes[StateFeatures.S0cS0RcQ0w] = hash(S0.category, S0R.category, Q0.form);
        }

        if (S0L != NONE_NODE && S1 != NONE_NODE) {
            assert S0 != null;
            features.hashes[StateFeatures.S0cS0LcS1c] = hash(S0.category, S0L.category, S1.category);
            features.hashes[StateFeatures.S0cS0LcS1c] = hash(S0.category, S0L.category, S1.lexicalHead.form);
        }

        if (S1R != NONE_NODE) {
            assert S0 != null;
            features.hashes[StateFeatures.S0cS1cS1Rc] = hash(S0.category, S1.category, S1R.category);
            features.hashes[StateFeatures.S0wS1cS1Rc] = hash(S0.lexicalHead.form, S1.category, S1R.category);
        }

        return features;
    }

    private static int hash(short a) {
        return hash(a, (short) 0, (short) 0, (short) 0);
    }

    private static int hash(short a, short b) {
        return hash(a, b, (short) 0, (short) 0);
    }

    private static int hash(short a, short b, short c) {
        return hash(a, b, c, (short) 0);
    }

    private static int hash(short a, short b, short c, short d) {
        // Pack four shorts into two ints, then aggregate these in the standard
        // way.
        int firstInt = (a << 16) | b;
        int secondInt = (c << 16) | d;
        return 29 * firstInt + secondInt;
    }

    private static Node getLeftNonHeadChild(Node node) {
        if (node == NONE_NODE) {
            return NONE_NODE;
        }

        if (!(node instanceof BinaryNode)) {
            return NONE_NODE;
        }

        BinaryNode binaryNode = (BinaryNode) node;

        if (binaryNode.rule.headPosition == BinaryRule.HeadPosition.LEFT) {
            return NONE_NODE;
        }

        return binaryNode.leftChild;
    }

    private static Node getRightNonHeadChild(Node node) {
        if (node == NONE_NODE) {
            return NONE_NODE;
        }

        if (!(node instanceof BinaryNode)) {
            return NONE_NODE;
        }

        BinaryNode binaryNode = (BinaryNode) node;

        if (binaryNode.rule.headPosition == BinaryRule.HeadPosition.RIGHT) {
            return NONE_NODE;
        }

        return binaryNode.rightChild;
    }

    private static Node getHeadChild(Node node) {
        if (node == NONE_NODE) {
            return NONE_NODE;
        }

        if (!(node instanceof BinaryNode)) {
            return NONE_NODE;
        }

        BinaryNode binaryNode = (BinaryNode) node;

        if (binaryNode.rule.headPosition == BinaryRule.HeadPosition.LEFT) {
            return binaryNode.leftChild;
        } else {
            return binaryNode.rightChild;
        }
    }

    private static Node getUnaryChild(Node node) {
        if (node == NONE_NODE) {
            return NONE_NODE;
        }

        if (!(node instanceof UnaryNode)) {
            return NONE_NODE;
        }

        UnaryNode unaryNode = (UnaryNode) node;
        return unaryNode.child;
    }

}
