package epar.oracle;

import epar.parser.Item;

public interface Oracle {

    boolean accept(int generation, Item item);

}
