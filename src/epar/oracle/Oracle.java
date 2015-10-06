package epar.oracle;

import epar.parser.Candidate;
import epar.parser.Item;

public interface Oracle {

    boolean accept(int generation, Candidate candidate, Item successorItem);

}
