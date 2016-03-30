package epar.oracle;

import epar.parser.Item;

public class NoFragmentsOracle implements Oracle {

    @Override
    public boolean accept(int generation, Item item) {
        if (item.finished) {
            return item.stack.size() == 1;
        }
        
        return true;
    }

}
