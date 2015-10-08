package epar.data;

import epar.util.SymbolPool;

/**
 *
 * @author p264360
 */
public class LexicalEntry {
    
    public static final LexicalEntry NONE = new LexicalEntry(SymbolPool.NONE, (short) 0);
    
    public final short category;
    
    public final short semantics;
    
    public LexicalEntry(short category, short semantics) {
        this.category = category;
        this.semantics = semantics;
    }
    
    public String toString() {
        String result = SymbolPool.getString(category);
        
        if (semantics != 0) {
            result += "-" + semantics;
        }
        
        return result;
    }
    
    public static LexicalEntry fromString(String string) {
        String[] parts = string.split("-");
        short category = SymbolPool.getID(parts[0]);
        short semantics;
        
        if (parts.length == 1) {
            semantics = 0;
        } else {
            semantics = Short.parseShort(parts[1]);
        }
        
        return new LexicalEntry(category, semantics);
    }
    
}
