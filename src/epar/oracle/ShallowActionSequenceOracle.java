package epar.oracle;

import java.util.List;
import java.util.logging.Logger;

import epar.node.Node;
import epar.parser.Action;
import epar.parser.Item;
import java.util.logging.Level;

public class ShallowActionSequenceOracle implements Oracle {

	private final static Logger LOGGER = Logger.getLogger(ShallowActionSequenceOracle.class.getName());

	private final List<Action> goldSequence;
	
	public ShallowActionSequenceOracle(Node goldTree) {
		List<Action> actions = goldTree.actionSequence();
		actions.add(Action.FINISH);
		LOGGER.log(Level.FINE, "Gold sequence: {0}", actions);
		this.goldSequence = actions;
	}

	@Override
	public boolean accept(int generation, Item item) {
		if (generation >= goldSequence.size()) {
			return item.action.equals(Action.IDLE);
		}
		
		return goldSequence.get(generation).equals(item.action);
	}

}
