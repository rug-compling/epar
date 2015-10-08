package epar.parser;

import java.util.ArrayList;
import java.util.List;

import epar.data.Sentence;
import epar.data.Word;
import epar.model.StateFeatures;
import epar.grammar.BinaryRule;
import epar.grammar.Grammar;
import epar.node.BinaryNode;
import epar.node.LexicalNode;
import epar.node.Node;
import epar.node.UnaryNode;
import epar.util.EStack;
import epar.util.NEStack;
import epar.util.Stack;
import epar.util.SymbolPool;

public class Item {

    public final Action action;

    public final Stack<Node> stack;

    public final Stack<Word> queue;

    public final boolean finished;

    private static final Word NONE_WORD = new Word(SymbolPool.NONE, SymbolPool.NONE, null);

    private static final Node NONE_NODE = new LexicalNode(SymbolPool.NONE, NONE_WORD);

    private Item(Action action, Stack<Node> stack, Stack<Word> queue,
            boolean finished) {
        this.action = action;
        this.stack = stack;
        this.queue = queue;
        this.finished = finished;
    }

    public List<Item> successors(Grammar grammar) {
        List<Item> successors = new ArrayList<>();
        shift(successors);
        binary(successors, grammar);
        unary(successors, grammar);
        finish(successors);
        idle(successors);
        skip(successors);
        return successors;
    }

    private void binary(List<Item> successors, Grammar grammar) {
        if (finished) {
            return;
        }

        if (stack.isEmpty()) {
            return;
        }

        Node rightChild = stack.getFirst();
        Stack<Node> rest = stack.getRest();

        if (rest.isEmpty()) {
            return;
        }

        Node leftChild = rest.getFirst();
        Stack<Node> restRest = rest.getRest();

        for (Node parent : grammar.binary(leftChild, rightChild)) {
            Action newAction = Action.binary(parent.category);
            Stack<Node> newStack = restRest.push(parent);
            successors.add(new Item(newAction, newStack, queue, false));
        }
    }

    private void unary(List<Item> successors, Grammar grammar) {
        if (finished) {
            return;
        }

        if (stack.isEmpty()) {
            return;
        }

        Node child = stack.getFirst();
        Stack<Node> rest = stack.getRest();

        for (Node parent : grammar.unary(child)) {
            Action newAction = Action.unary(parent.category);
            Stack<Node> newStack = rest.push(parent);
            successors.add(new Item(newAction, newStack, queue, false));
        }
    }

    private void skip(List<Item> successors) {
        if (finished) {
            return;
        }

        if (stack.isEmpty()) {
            return;
        }

        if (stack.getFirst().category != Grammar.SKIP_CATEGORY) {
            return;
        }

        successors.add(new Item(Action.SKIP, stack.getRest(), queue, false));
    }

    private void shift(List<Item> successors) {
        if (queue.isEmpty()) {
            return;
        }

        Word word = queue.getFirst();
        Stack<Word> newQueue = queue.getRest();

        for (short category : word.categories) {
            Node newNode = new LexicalNode(category, word);
            Stack<Node> newStack = stack.push(newNode);
            Action newAction = Action.shift(category, ((short) 0)); // TODO copy semantics
            successors.add(new Item(newAction, newStack, newQueue, false));
        }
    }

    // Could perhaps conflate FINISH and IDLE.
    private void finish(List<Item> successors) {
        if (finished) {
            return;
        }

        if (!queue.isEmpty()) {
            return;
        }

        successors.add(new Item(Action.FINISH, stack, queue, true));
    }

    private void idle(List<Item> successors) {
        if (!finished) {
            return;
        }

        successors.add(new Item(Action.IDLE, stack, queue, true));
    }

    public StateFeatures extractFeatures() {
        StateFeatures features = new StateFeatures();

        Node S0 = stack.get(0, NONE_NODE);

        // ZPar does not extract any features from the initial item. Why? Oh
        // well. Note that this makes every instance of if (S0 != NONE_NODE)
        // below redundant, but we keep them around in case we want to change
        // this.
        if (S0 == NONE_NODE) {
            return features;
        }

        Node S1 = stack.get(1, NONE_NODE);
        Node S2 = stack.get(2, NONE_NODE);
        Node S3 = stack.get(3, NONE_NODE);
        Word Q0 = queue.get(0, NONE_WORD);
        Word Q1 = queue.get(1, NONE_WORD);
        Word Q2 = queue.get(2, NONE_WORD);
        Word Q3 = queue.get(3, NONE_WORD);
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
        if (Q0 != NONE_WORD) {
            features.hashes[StateFeatures.Q0wp] = hash(Q0.form, Q0.pos);
        }

        if (Q1 != NONE_WORD) {
            features.hashes[StateFeatures.Q1wp] = hash(Q1.form, Q1.pos);
        }

        if (Q2 != NONE_WORD) {
            features.hashes[StateFeatures.Q2wp] = hash(Q2.form, Q2.pos);
        }

        if (Q3 != NONE_WORD) {
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

        if (S0 != NONE_NODE && Q0 != NONE_WORD) {
            features.hashes[StateFeatures.S0wcQ0wp] = hash(S0.lexicalHead.form, S0.category,
                    Q0.form, Q0.pos);
            features.hashes[StateFeatures.S0cQ0wp] = hash(S0.category, Q0.form, Q0.pos);
            features.hashes[StateFeatures.S0wcQ0p] = hash(S0.lexicalHead.form, S0.category, Q0.pos);
            features.hashes[StateFeatures.S0cQ0p] = hash(S0.category, Q0.pos);
        }

        if (S1 != NONE_NODE && Q0 != NONE_WORD) {
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
            if (Q0 != NONE_WORD) {
                features.hashes[StateFeatures.S0cS1cQ0wp] = hash(S0.category, S1.category, Q0.form, Q0.pos);
            }
            features.hashes[StateFeatures.S0cS1cQ0p] = hash(S0.category, S1.category, Q0.pos);
            features.hashes[StateFeatures.S0pS1pQ0p] = hash(S0.lexicalHead.pos, S1.lexicalHead.pos, Q0.pos);
        }

        // S0 Q0 Q1
        if (Q0 != NONE_WORD) {
            assert S0 != null;
            if (S0 != NONE_NODE) {
                features.hashes[StateFeatures.S0wcQ0pQ1p] = hash(S0.lexicalHead.form, S0.category, Q0.pos, Q1.pos);
            }
            features.hashes[StateFeatures.S0cQ0wpQ1p] = hash(S0.category, Q0.form, Q0.pos, Q1.pos);
            if (Q1 != NONE_WORD) {
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

        if (S0R != NONE_NODE && Q0 != NONE_WORD) {
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

    public static Item initial(Sentence sentence) {
        Stack<Word> queue = new EStack<>();

        for (int i = sentence.words.size() - 1; i >= 0; i--) {
            queue = new NEStack<>(sentence.words.get(i), queue);
        }

        return new Item(Action.INIT, new EStack<Node>(), queue, false);
    }

    @Override
    public String toString() {
        return "(" + action + ", " + stack.size() + ", " + queue.size() + ", " + finished + ")";
    }

}
