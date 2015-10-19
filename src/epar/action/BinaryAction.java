package epar.action;

import epar.grammar.BinaryRule;
import epar.grammar.BinaryRule.HeadPosition;
import epar.util.SymbolPool;
import java.util.Objects;

/**
 *  
 * @author p264360
 */
public class BinaryAction extends ActionWithCategory {
    
    public final BinaryRule.HeadPosition headPosition;
    
    public final String schemaName;
    
    public BinaryAction(String schemaName, HeadPosition headPosition,
            short category) {
        super(category);
        this.schemaName = schemaName;
        this.headPosition = headPosition;
    }    
    
    @Override
    public String toString() {
        return String.format("BINARY-%s-%s-%s", schemaName,
                headPosition.toActionString(), SymbolPool.getString(category));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + category;
        hash = 59 * hash + Objects.hashCode(this.headPosition);
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
        final BinaryAction other = (BinaryAction) obj;
        if (this.category != other.category) {
            return false;
        }
        if (this.headPosition != other.headPosition) {
            return false;
        }
        if (!Objects.equals(this.schemaName, other.schemaName)) {
            return false;
        }
        return true;
    }

    @Override
    public short getType() {
        return Action.TYPE_BINARY;
    }
    
}
