package epar.data;

import epar.util.SymbolPool;

/**
 *
 * @author p264360
 */
public class LexicalEntry {
    
    public static final LexicalEntry NONE = new LexicalEntry(SymbolPool.NONE, SymbolPool.NONE);
    
    public final short category;
    
    public final short semantics;
    
    public LexicalEntry(short category, short semantics) {
        this.category = category;
        this.semantics = semantics;
    }
    
    @Override
    public String toString() {
        String result = SymbolPool.getString(category);
        
        if (semantics != SymbolPool.NONE) {
            result += "-" + SymbolPool.getString(semantics);
        }
        
        return result;
    }
    
    public static LexicalEntry fromString(String string) {
        String[] parts = string.split("-");
        short category = SymbolPool.getID(parts[0]);
        short semantics;
        
        if (parts.length == 1) {
            semantics = SymbolPool.NONE;
        } else {
            semantics = SymbolPool.getID(parts[1]);
        }
        
        return new LexicalEntry(category, semantics);
    }
    
}
