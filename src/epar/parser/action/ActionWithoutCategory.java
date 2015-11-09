package epar.parser.action;

import epar.util.SymbolPool;

/**
 *
 * @author p264360
 */
public abstract class ActionWithoutCategory extends Action {
    
    @Override
    public int getCategory() {
        return SymbolPool.NONE;
    }
    
}
