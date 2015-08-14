package epar.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import epar.data.Word;
import epar.parser.Action;

public class LexicalNode extends Node {

	public LexicalNode(String category, Word word) {
		super(category, word);
	}

	@Override
	public List<Action> actionSequence() {
		List<Action> actions = new ArrayList<Action>();
		actions.add(Action.shift(category));
		return actions;
	}

	@Override
	public String toString() {
		return "( " + category + " c " + lexicalHead.pos + " "
				+ lexicalHead.form + " )";
	}

	@Override
	public List<Node> descendants() {
		return Collections.<Node>singletonList(this);
	}

}
