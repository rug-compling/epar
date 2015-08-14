package epar.node;

import java.util.ArrayList;
import java.util.List;

import epar.data.Word;
import epar.grammar.BinaryRule;
import epar.parser.Action;

public class BinaryNode extends Node {

	public final Node leftChild;

	public final Node rightChild;

	public final BinaryRule rule;

	public BinaryNode(String category, Word lexicalHead, Node leftChild,
			Node rightChild, BinaryRule rule) {
		super(category, lexicalHead);
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		this.rule = rule;
	}

	@Override
	public List<Action> actionSequence() {
		List<Action> actions = leftChild.actionSequence();
		actions.addAll(rightChild.actionSequence());
		actions.add(Action.binary(category));
		return actions;
	}

	@Override
	public String toString() {
		return "( " + category + " " + rule.headPosition + " " + leftChild
				+ " " + rightChild + " )";
	}

	@Override
	public List<Node> descendants() {
		List<Node> descendants = new ArrayList<Node>();
		descendants.add(this);
		descendants.addAll(leftChild.descendants());
		descendants.addAll(rightChild.descendants());
		return descendants;
	}

}
