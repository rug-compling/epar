package epar.parser;

import epar.parser.action.Action;
import epar.parser.action.BinaryAction;
import epar.parser.action.FinishAction;
import epar.parser.action.IdleAction;
import epar.parser.action.InitAction;
import epar.parser.action.ShiftAction;
import epar.parser.action.SkipAction;
import epar.parser.action.UnaryAction;
import java.util.ArrayList;
import java.util.List;

import epar.data.LexicalItem;
import epar.data.Sentence;
import epar.data.SentencePosition;
import epar.grammar.BinaryRule;
import epar.grammar.Grammar;
import epar.model.StateFeatures;
import epar.node.BinaryNode;
import epar.node.LexicalNode;
import epar.node.Node;
import epar.node.UnaryNode;
import epar.util.EStack;
import epar.util.NEStack;
import epar.util.Stack;
import epar.util.SymbolPool;
import java.util.logging.Logger;

public class Item {

    private final static Logger LOGGER = Logger.getLogger(Item.class.getName());
    
    public final Item predecessor;

    public final Action action;

    public final Stack<Node> stack;

    public final Stack<SentencePosition> queue;

    public final boolean finished;

    private static final SentencePosition NONE_SENTENCE_POSITION =
            new SentencePosition(SymbolPool.NONE, SymbolPool.NONE, null);

    private static final Node NONE_NODE = new LexicalNode(LexicalItem.NONE);
    
    // HACK
    private static final int EMPTY_SEM = SymbolPool.getID("lam(A,A)");
    
    private static final int FA = SymbolPool.getID("fa");
    
    private static final int BA = SymbolPool.getID("ba");

    private Item(Item parent, Action action, Stack<Node> stack,
            Stack<SentencePosition> queue, boolean finished) {
        this.predecessor = parent;
        this.action = action;
        this.stack = stack;
        this.queue = queue;
        this.finished = finished;
    }

    public List<Item> successors(Grammar grammar) {
        List<Item> successors = new ArrayList<>();
        idle(successors);
        finish(successors);
        shift(successors);
        binary(successors, grammar);
        unary(successors, grammar);
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

        for (BinaryNode parent : grammar.binary(leftChild, rightChild)) {
            // HACK: Make sure semantically empty words are not taken as
            // arguments because we can't interpret that.
            
            if (parent.rule.schemaName == FA && rightChild instanceof
                    LexicalNode && ((LexicalNode)
                    rightChild).lexicalHead.lexicalSemantics == EMPTY_SEM) {
                continue;
            }
            
            if (parent.rule.schemaName == BA && leftChild instanceof
                    LexicalNode && ((LexicalNode)
                    leftChild).lexicalHead.lexicalSemantics == EMPTY_SEM) {
                continue;
            }
            
            // Create new item
            
            Action newAction = new BinaryAction(parent.rule.schemaName,
                    parent.rule.headPosition, parent.category);
            Stack<Node> newStack = restRest.push(parent);
            successors.add(new Item(this, newAction, newStack, queue, false));
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

        for (UnaryNode parent : grammar.unary(child)) {
            Action newAction = new UnaryAction(parent.rule.schemaName,
                    parent.category);
            Stack<Node> newStack = rest.push(parent);
            successors.add(new Item(this, newAction, newStack, queue, false));
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

        successors.add(new Item(this, SkipAction.INSTANCE, stack.getRest(),
                queue, false));
    }

    private void shift(List<Item> successors) {
        if (!stack.isEmpty() &&
                stack.getFirst().category == Grammar.SKIP_CATEGORY) {
            return;
        }
        
        if (queue.isEmpty()) {
            return;
        }

        SentencePosition sentencePosition = queue.getFirst();

        for (LexicalItem item : sentencePosition.lexicalItems) {
            Action newAction = new ShiftAction(item.length, item.category,
                    item.lexicalSemantics);
            Node newNode = new LexicalNode(item);
            Stack<Node> newStack = stack.push(newNode);
            Stack<SentencePosition> newQueue = queue;
            
            // Pop as many positions from the queue as the item is long (> 1 if
            // it is a multiword;
            for (int i = 0; i < item.length; i++) {
                newQueue = newQueue.getRest();
            }
            
            Item successor = new Item(this, newAction, newStack, newQueue,
                    false);
            successors.add(successor);
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
        
        if (!stack.isEmpty() &&
                stack.getFirst().category == Grammar.SKIP_CATEGORY) {
            return;
        }

        successors.add(new Item(this, FinishAction.INSTANCE, stack, queue,
                true));
    }

    private void idle(List<Item> successors) {
        if (!finished) {
            return;
        }

        successors.add(new Item(this, IdleAction.INSTANCE, stack, queue, true));
    }

    public static Item initial(Sentence sentence) {
        Stack<SentencePosition> queue = new EStack<>();

        for (int i = sentence.positions.size() - 1; i >= 0; i--) {
            queue = new NEStack<>(sentence.positions.get(i), queue);
        }

        return new Item(null, InitAction.INSTANCE, new EStack<Node>(), queue,
                false);
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
        SentencePosition Q0 = queue.get(0, NONE_SENTENCE_POSITION);
        SentencePosition Q1 = queue.get(1, NONE_SENTENCE_POSITION);
        SentencePosition Q2 = queue.get(2, NONE_SENTENCE_POSITION);
        SentencePosition Q3 = queue.get(3, NONE_SENTENCE_POSITION);
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
    
    public int lexicalHash() {
        if (action.getType() != Action.TYPE_SHIFT) {
            return 0;
        }
        
        LexicalItem lex = stack.getFirst().lexicalHead;
        return hash(lex.category, lex.lexicalSemantics, lex.form, lex.pos);
    }

    private static int hash(int a) {
        return hash(a, 0, 0, 0);
    }

    private static int hash(int a, int b) {
        return hash(a, b, 0, 0);
    }

    private static int hash(int a, int b, int c) {
        return hash(a, b, c, 0);
    }

    private static int hash(int a, int b, int c, int d) {
        int hash = 7;
        hash = 11 * a + hash;
        hash = 11 * b + hash;
        hash = 11 * c + hash;
        hash = 11 * d + hash;
        return hash;
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

    @Override
    public String toString() {
        return "(" + action + ", " + stack.size() + ", " + queue.size() + ", " + finished + ")";
    }

    /**
     * Returns the sequences of actions that lead up to this item, not
     * including the INIT action that created the initial item or IDLE actions
     * at the end.
     * @return 
     */
    public List<Action> actionSequence() {
        Item item = this;
        List<Action> sequence = new ArrayList<>();
        
        while (item.predecessor != null) {
            if (item.action.getType() != Action.TYPE_IDLE) {
                sequence.add(0, item.action);
            }
            
            item = item.predecessor;
        }
        
        return sequence;
    }

}
