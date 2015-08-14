package epar.node;

import java.util.ArrayList;
import java.util.List;

import epar.data.Word;
import epar.grammar.UnaryRule;
import epar.parser.Action;

public class UnaryNode extends Node {
	
	public final Node child;
	
	public final UnaryRule rule;

	public UnaryNode(String category, Word lexicalHead, Node child,
			UnaryRule rule) {
		super(category, lexicalHead);
		this.child = child;
		this.rule = rule;
	}

	@Override
	public List<Action> actionSequence() {
		List<Action> actions = child.actionSequence();
		actions.add(Action.unary(category));
		return actions;
	}
	
	@Override
	public String toString() {
		return "( " + category + " s " + child + " )"; 
	}

	@Override
	public List<Node> descendants() {
		List<Node> descendants = new ArrayList<Node>();
		descendants.add(this);
		descendants.addAll(child.descendants());
		return descendants;
	}

}
