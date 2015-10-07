package epar.oracle;

import java.util.List;
import java.util.logging.Logger;

import epar.parser.Action;
import epar.parser.Candidate;
import epar.parser.Item;

public class ShallowActionSequenceOracle implements Oracle {

    private final static Logger LOGGER = Logger.getLogger(ShallowActionSequenceOracle.class.getName());

    private final List<Action> goldSequence;

    public ShallowActionSequenceOracle(List<Action> goldSequence) {
        this.goldSequence = goldSequence;
    }

    @Override
    public boolean accept(int generation, Candidate candidate, Item item) {
        if (generation >= goldSequence.size()) {
            return item.action.equals(Action.IDLE);
        }

        return goldSequence.get(generation).equals(item.action);
    }

}
