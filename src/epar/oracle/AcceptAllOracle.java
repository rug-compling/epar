package epar.oracle;

import epar.parser.Candidate;
import epar.parser.Item;

public class AcceptAllOracle implements Oracle {

    @Override
    public boolean accept(int generation, Candidate candidate, Item successorItem) {
        return true;
    }

}
