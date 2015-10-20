package epar.grammar;

import epar.util.SymbolPool;

public class UnaryRule {

    public final short childCategory;

    public final short parentCategory;
    
    public final short schemaName;

    public UnaryRule(short childCategory, short parentCategory,
            short schemaName) {
        this.childCategory = childCategory;
        this.parentCategory = parentCategory;
        this.schemaName = schemaName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + this.childCategory;
        hash = 11 * hash + this.parentCategory;
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
        final UnaryRule other = (UnaryRule) obj;
        if (this.childCategory != other.childCategory) {
            return false;
        }
        if (this.parentCategory != other.parentCategory) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return SymbolPool.getString(childCategory) + "\t" +
                SymbolPool.getString(parentCategory) + "\t" + schemaName;
    }

}
