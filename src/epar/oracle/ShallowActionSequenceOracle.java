package epar.oracle;

import java.util.List;
import java.util.logging.Logger;

import epar.action.Action;
import epar.action.IdleAction;
import epar.parser.Item;

public class ShallowActionSequenceOracle implements Oracle {

    private final static Logger LOGGER = Logger.getLogger(ShallowActionSequenceOracle.class.getName());

    private final List<Action> goldSequence;

    public ShallowActionSequenceOracle(List<Action> goldSequence) {
        this.goldSequence = goldSequence;
    }

    @Override
    public boolean accept(int generation, Item item) {
        if (generation >= goldSequence.size()) {
            return item.action.equals(IdleAction.INSTANCE);
        }

        return goldSequence.get(generation).equals(item.action);
    }

}
