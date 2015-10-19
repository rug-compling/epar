package epar.action;

import epar.util.SymbolPool;
import java.util.Objects;

/**
 *
 * @author p264360
 */
public class UnaryAction extends ActionWithCategory {
    
    public final String schemaName;
    
    public UnaryAction(String schemaName, short category) {
        super(category);
        this.schemaName = schemaName;
    }
    
    @Override
    public String toString() {
        return String.format("UNARY-%s-%s", schemaName,
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
        if (!Objects.equals(this.schemaName, other.schemaName)) {
            return false;
        }
        return true;
    }

    @Override
    public short getType() {
        return Action.TYPE_UNARY;
    }
    
}
