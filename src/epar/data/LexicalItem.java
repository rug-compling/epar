package epar.data;

import epar.util.SymbolPool;

/**
 *
 * @author p264360
 */
public class LexicalItem {
    
    public static final LexicalItem NONE = new LexicalItem(1, SymbolPool.NONE, SymbolPool.NONE, SymbolPool.NONE, SymbolPool.NONE);
    
    public final int length;
    
    public final int form;
    
    public final int pos;
    
    public final int category;
    
    public final int semantics;
    
    public LexicalItem(int length, int form, int pos, int category, int semantics) {
        this.length = length;
        this.form = form;
        this.pos = pos;
        this.category = category;
        this.semantics = semantics;
    }
    
}
