package epar.oracle;

import java.util.List;
import java.util.logging.Logger;

import epar.parser.Action;
import epar.parser.Candidate;
import epar.parser.Item;

public class DeepActionSequenceOracle implements Oracle {

    private final static Logger LOGGER = Logger.getLogger(DeepActionSequenceOracle.class.getName());

    private final List<Action> goldSequence;
    
    private final Oracle shallowOracle;

    public DeepActionSequenceOracle(List<Action> goldSequence) {
        this.goldSequence = goldSequence;
        this.shallowOracle = new ShallowActionSequenceOracle(goldSequence);
    }

    @Override
    public boolean accept(int generation, Candidate candidate, Item item) {
        while (candidate != null) {
            if (!shallowOracle.accept(generation, candidate, item)) {
                return false;
            }
            
            item = candidate.item;
            candidate = candidate.parent;
            generation--;
        }
        
        return true;
    }

}
