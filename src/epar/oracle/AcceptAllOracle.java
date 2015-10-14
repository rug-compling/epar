package epar.oracle;

import epar.parser.Item;

public class AcceptAllOracle implements Oracle {

    @Override
    public boolean accept(int generation, Item item) {
        return true;
    }

}
