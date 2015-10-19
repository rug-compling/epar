package epar.oracle;

import java.util.List;
import java.util.logging.Logger;

import epar.action.Action;
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
    public boolean accept(int generation, Item item) {
        while (item.parent != null) {
            if (!shallowOracle.accept(generation, item)) {
                return false;
            }
            
            item = item.parent;
            generation--;
        }
        
        return true;
    }

}
