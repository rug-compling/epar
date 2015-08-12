package epar.oracle;

import java.util.List;

import epar.node.Node;
import epar.parser.Action;
import epar.parser.Item;

public class ShallowActionSequenceOracle implements Oracle {
	
	private static final Action IDLE = Action.idle();
	
	private final List<Action> goldSequence;
	
	public ShallowActionSequenceOracle(Node goldTree) {
		List<Action> actions = goldTree.actionSequence();
		actions.add(Action.finish());
		this.goldSequence = actions;
	}
	
	// TODO avoid instanceof by factoring this code into Node subclasses
	/*private static List<Action> computeGoldSequence(Node node) {
		if (node instanceof LexicalNode) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(Action.shift(node.category));
			return actions;
		} else if (node instanceof BinaryNode) {
			List<Action> actions = computeGoldSequence(explanation.leftChild);
			actions.addAll(computeGoldSequence(explanation.rightChild));
			actions.add(Action.binary(node.category));
			return actions;
		} else {
			UnaryExplanation explanation = (UnaryExplanation) node.explanation;
			List<Action> actions = computeGoldSequence(explanation.child);
			actions.add(Action.unary(node.category));
			return actions;
		}
	}*/

	@Override
	public boolean accept(int generation, Item item) {
		if (generation >= goldSequence.size()) {
			return item.action.equals(IDLE);
		}
		
		return goldSequence.get(generation).equals(item.action);
	}

}
