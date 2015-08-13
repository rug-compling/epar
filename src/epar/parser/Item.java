package epar.parser;

import java.util.ArrayList;
import java.util.List;

import epar.data.Sentence;
import epar.data.Word;
import epar.grammar.BinaryRule;
import epar.grammar.Grammar;
import epar.node.BinaryNode;
import epar.node.LexicalNode;
import epar.node.Node;
import epar.node.UnaryNode;
import epar.util.EStack;
import epar.util.NEStack;
import epar.util.Stack;

public class Item {

	public final Action action;

	public final Stack<Node> stack;

	public final Stack<Word> queue;

	public final boolean finished;

	private static final Word NONE_WORD = new Word(null, "NONE_TAG", null);

	private static final Node NONE_NODE = new LexicalNode("SENTENCE_BEGIN", NONE_WORD);

	private Item(Action action, Stack<Node> stack, Stack<Word> queue,
			boolean finished) {
		this.action = action;
		this.stack = stack;
		this.queue = queue;
		this.finished = finished;
	}

	public List<Item> successors(Grammar grammar) {
		List<Item> successors = new ArrayList<Item>();
		shift(successors);
		binary(successors, grammar);
		unary(successors, grammar);
		finish(successors);
		idle(successors);
		return successors;
	}

	private void binary(List<Item> successors, Grammar grammar) {
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

	private void shift(List<Item> successors) {
		if (queue.isEmpty()) {
			return;
		}

		Word word = queue.getFirst();
		Stack<Word> newQueue = queue.getRest();

		for (String category : word.categories) {
			Node newNode = new LexicalNode(category, word);
			Stack<Node> newStack = stack.push(newNode);
			Action newAction = Action.shift(category);
			successors.add(new Item(newAction, newStack, newQueue, false));
		}
	}

	private void finish(List<Item> successors) {
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

	public List<String> extractFeatures() {
		List<String> features = new ArrayList<String>();

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
		Word Q1 = queue.get(0, NONE_WORD);
		Word Q2 = queue.get(0, NONE_WORD);
		Word Q3 = queue.get(0, NONE_WORD);
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
			features.add(f("S0wp", S0.lexicalHead.form, S0.lexicalHead.pos));
			features.add(f("S0c", S0.category));
			features.add(f("S0pc", S0.lexicalHead.pos, S0.category));
			features.add(f("S0wc", S0.lexicalHead.form, S0.category));
		}

		if (S1 != NONE_NODE) {
			features.add(f("S1wp", S1.lexicalHead.form, S1.lexicalHead.pos));
			features.add(f("S1c", S1.category));
			features.add(f("S1pc", S1.lexicalHead.pos, S1.category));
			features.add(f("S1wc", S1.lexicalHead.form, S1.category));
		}

		if (S2 != NONE_NODE) {
			features.add(f("S2pc", S2.lexicalHead.pos, S2.category));
			features.add(f("S2wc", S2.lexicalHead.form, S2.category));
		}

		if (S3 != NONE_NODE) {
			features.add(f("S3pc", S3.lexicalHead.pos, S3.category));
			features.add(f("S3wc", S3.lexicalHead.form, S3.category));
		}

		// Group 2

		if (Q0 != NONE_WORD) {
			features.add(f("Q0wp", Q0.form, Q0.pos));
		}

		if (Q1 != NONE_WORD) {
			features.add(f("Q1wp", Q1.form, Q1.pos));
		}

		if (Q2 != NONE_WORD) {
			features.add(f("Q2wp", Q2.form, Q2.pos));
		}

		if (Q3 != NONE_WORD) {
			features.add(f("Q3wp", Q3.form, Q3.pos));
		}

		// Group 3

		if (S0L != NONE_NODE) {
			features.add(f("S0Lpc", S0L.lexicalHead.pos, S0L.category));
			features.add(f("S0Lwc", S0L.lexicalHead.form, S0L.category));
		}

		if (S0R != NONE_NODE) {
			features.add(f("S0Rpc", S0R.lexicalHead.pos, S0R.category));
			features.add(f("S0Rwc", S0R.lexicalHead.form, S0R.category));
		}

		if (S0U != NONE_NODE) {
			features.add(f("S0Upc", S0U.lexicalHead.pos, S0U.category));
			features.add(f("S0Uwc", S0U.lexicalHead.form, S0U.category));
		}

		if (S1L != null) {
			features.add(f("S1Lpc", S1L.lexicalHead.pos, S1L.category));
			features.add(f("S1Lwc", S1L.lexicalHead.form, S1L.category));
		}

		if (S1R != NONE_NODE) {
			features.add(f("S1Rpc", S1R.lexicalHead.pos, S1R.category));
			features.add(f("S1Rwc", S1R.lexicalHead.form, S1R.category));
		}

		if (S1U != NONE_NODE) {
			features.add(f("S1Upc", S1U.lexicalHead.pos, S1U.category));
			features.add(f("S1Uwc", S1U.lexicalHead.form, S1U.category));
		}

		// Group 4

		if (S1 != NONE_NODE) {
			features.add(f("S0wcS1wc", S0.lexicalHead.form, S0.lexicalHead.pos,
					S1.lexicalHead.form, S1.lexicalHead.pos));
			features.add(f("S0cS1w", S0.category, S1.lexicalHead.form));
			features.add(f("S0wS1c", S0.lexicalHead.form, S1.category));
		}

		if (S0 != NONE_NODE && Q0 != NONE_WORD) {
			features.add(f("S0wcQ0wp", S0.lexicalHead.form, S0.category,
					Q0.form, Q0.pos));
			features.add(f("S0cQ0wp", S0.category, Q0.form, Q0.pos));
			features.add(f("S0wcQ0p", S0.lexicalHead.form, S0.category, Q0.pos));
			features.add(f("S0cQ0p", S0.category, Q0.pos));
		}

		if (S1 != NONE_NODE && Q0 != NONE_WORD) {
			features.add(f("S1wcQ0wp", S1.lexicalHead.form, S1.category,
					Q0.form, Q0.pos));
			features.add(f("S1cQ0wp", S1.category, Q0.form, Q0.pos));
			features.add(f("S1wcQ0p", S1.lexicalHead.form, S1.category, Q0.pos));
			features.add(f("S1cQ0p", S1.category, Q0.pos));
		}

		// Group 5: trigrams

		// Here we also refer to the (dummy) tags and categories of non-existing
		// nodes and words, just not for the focus item (middle of trigram) and
		// not to word forms.

		// S1 S0 Q0
		if (S0 != null) {
			features.add(f("S0wcS1cQ0p", S0.lexicalHead.form, S0.category,
					S1.category, Q0.pos));
			if (S1 != NONE_NODE) {
				features.add(f("S0cS1wcQ0p", S0.category, S1.lexicalHead.form,
						S1.category, Q0.pos));
			}
			if (Q0 != NONE_WORD) {
				features.add(f("S0cS1cQ0wp", S0.category, S1.category, Q0.form,
						Q0.pos));
			}
			features.add(f("S0cS1cQ0p", S0.category, S1.category, Q0.pos));
			features.add(f("S0pS1pQ0p", S0.lexicalHead.pos, S1.lexicalHead.pos,
					Q0.pos));
		}

		// S0 Q0 Q1
		if (Q0 != NONE_WORD) {
			if (S0 != NONE_NODE) {
				features.add(f("S0wcQ0pQ1p", S0.lexicalHead.form, S0.category,
						Q0.pos, Q1.pos));
			}
			features.add(f("S0cQ0wpQ1p", S0.category, Q0.form, Q0.pos, Q1.pos));
			if (Q1 != NONE_WORD) {
				features.add(f("S0cQ0pQ1wp", S0.category, Q0.pos, Q1.form,
						Q1.pos));
			}
			features.add(f("S0cQ0pQ1p", S0.category, Q0.pos, Q1.pos));
			features.add(f("S0pQ0pQ1p", S0.lexicalHead.pos, Q0.pos, Q1.pos));
		}

		// S2 S1 S0
		if (S1 != NONE_NODE) {
			features.add(f("S0wcS1cS2c", S0.lexicalHead.form, S0.category,
					S1.category, S2.category));
			features.add(f("S0cS1wcS2c", S0.category, S1.lexicalHead.form,
					S1.category, S2.category));
			if (S2 != NONE_NODE) {
				features.add(f("S0cS1cS2wc", S0.category, S1.category,
						S2.lexicalHead.form, S2.category));
			}
			features.add(f("S0cS1cS2c", S0.category, S1.category, S2.category));
			features.add(f("S0pS1pS2p", S0.lexicalHead.pos, S1.lexicalHead.pos,
					S2.lexicalHead.pos));
		}

		// Group 6

		// TODO We are not extracting any features referring to nonexisting
		// nodes here, is this right?

		if (S0L != NONE_NODE) {
			features.add(f("S0cS0HcS0Lc", S0.category, S0H.category,
					S0L.category));
		}

		if (S0R != NONE_NODE) {
			features.add(f("S0cS0HcS0Rc", S0.category, S0H.category,
					S0R.category));
		}

		if (S1R != NONE_NODE) {
			features.add(f("S1cS1HcS1Rc", S1.category, S1H.category, S1R.category));
		}
		
		if (S0R != NONE_NODE && Q0 != NONE_WORD) {
			features.add(f("S0cS0RcQ0p", S0.category, S0R.category, Q0.pos));
			features.add(f("S0cS0RcQ0w", S0.category, S0R.category, Q0.form));
		}
		
		if (S0L != NONE_NODE && S1 != NONE_NODE) {
			features.add(f("S0cS0LcS1c", S0.category, S0L.category, S1.category));
			features.add(f("S0cS0LcS1c", S0.category, S0L.category, S1.lexicalHead.form));
		}
		
		if (S1R != NONE_NODE) {
			features.add(f("S0cS1cS1Rc", S0.category, S1.category, S1R.category));
			features.add(f("S0wS1cS1Rc", S0.lexicalHead.form, S1.category, S1R.category));
		}

		return features;
	}

	private String f(String template, String a) {
		return template + "(" + a + ")";
	}

	private String f(String template, String a, String b) {
		return template + "(" + a + "," + b + ")";
	}

	private String f(String template, String a, String b, String c) {
		return template + "(" + a + "," + b + "," + c + ")";
	}

	private String f(String template, String a, String b, String c, String d) {
		return template + "(" + a + "," + b + "," + c + "," + d + ")";
	}

	private Node getLeftNonHeadChild(Node node) {
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

	private Node getRightNonHeadChild(Node node) {
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

	private Node getHeadChild(Node node) {
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

	private Node getUnaryChild(Node node) {
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
		Stack<Word> queue = new EStack<Word>();
		
		for (int i = sentence.words.size() - 1; i >= 0; i--) {
			queue = new NEStack<Word>(sentence.words.get(i), queue);
		}
		
		return new Item(Action.INIT, new EStack<Node>(), queue, false);
	}
	
	public String toString() {
		return "(" + action + ", " + stack.size() + ", " + queue.size() + ", " + finished + ")";
	}

}
