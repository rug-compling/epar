package epar.data;

import epar.sem.Interpretation;
import epar.util.SymbolPool;

/**
 *
 * @author p264360
 */
public class LexicalItem {
    
    public static final LexicalItem NONE = new LexicalItem(1, SymbolPool.NONE, SymbolPool.NONE, SymbolPool.NONE, Interpretation.IDENTITY);
    
    public final int length;
    
    public final int form;
    
    public final int pos;
    
    public final int category;
    
    public final Interpretation interpretation;
    
    public final int lexicalSemantics;
    
    public LexicalItem(int length, int form, int pos, int category, Interpretation interpretation) {
        this.length = length;
        this.form = form;
        this.pos = pos;
        this.category = category;
        this.interpretation = interpretation;
        this.lexicalSemantics = SymbolPool.getID(interpretation.toProlog());
    }
    
}
