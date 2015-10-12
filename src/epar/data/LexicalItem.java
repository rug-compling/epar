package epar.data;

import epar.util.StringUtil;
import epar.util.SymbolPool;
import java.util.Scanner;

/**
 *
 * @author p264360
 */
public class LexicalItem {
    
    public static final LexicalItem NONE = new LexicalItem(1, SymbolPool.NONE, SymbolPool.NONE, SymbolPool.NONE, SymbolPool.NONE);
    
    public final int length;
    
    public final short form;
    
    public final short pos;
    
    public final short category;
    
    public final short semantics;
    
    public LexicalItem(int length, short form, short pos, short category, short semantics) {
        this.length = length;
        this.form = form;
        this.pos = pos;
        this.category = category;
        this.semantics = semantics;
    }
    
}
