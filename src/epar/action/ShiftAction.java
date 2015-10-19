package epar.action;

import epar.util.SymbolPool;

/**
 *
 * @author p264360
 */
public class ShiftAction extends ActionWithCategory {
    
    public final int length;
    
    public final short semantics;
    
    public ShiftAction(int length, short category, short semantics) {
        super(category);
        this.length = length;
        this.semantics = semantics;
    }
    
    @Override
    public String toString() {
        return String.format("SHIFT-%s-%s-%s", length,
                SymbolPool.getString(category),
                semantics == SymbolPool.NONE ? "0" :
                SymbolPool.getString(semantics));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + this.category;
        hash = 17 * hash + this.length;
        hash = 17 * hash + this.semantics;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ShiftAction other = (ShiftAction) obj;
        if (this.category != other.category) {
            return false;
        }
        if (this.length != other.length) {
            return false;
        }
        if (this.semantics != other.semantics) {
            return false;
        }
        return true;
    }

    @Override
    public short getType() {
        return Action.TYPE_SHIFT;
    }
    
}
