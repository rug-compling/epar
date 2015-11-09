package epar.parser.action;

import epar.util.SymbolPool;
import java.util.Objects;

/**
 *
 * @author p264360
 */
public class UnaryAction extends ActionWithCategory {
    
    public final int schemaName;
    
    public UnaryAction(int schemaName, int category) {
        super(category);
        this.schemaName = schemaName;
    }
    
    @Override
    public String toString() {
        return String.format("UNARY-%s-%s", SymbolPool.getString(schemaName),
                SymbolPool.getString(category));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + category;
        hash = 59 * hash + Objects.hashCode(this.schemaName);
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
        final UnaryAction other = (UnaryAction) obj;
        if (category != other.category) {
            return false;
        }
        if (this.schemaName != other.schemaName) {
            return false;
        }
        return true;
    }

    @Override
    public int getType() {
        return Action.TYPE_UNARY;
    }
    
}
