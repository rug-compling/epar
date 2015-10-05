package epar.parser;

import epar.util.SymbolPool;

public class Action {
    
    private static final short TYPE_INIT = 1;
    
    private static final short TYPE_SHIFT = 2;
    
    private static final short TYPE_BINARY = 3;
    
    private static final short TYPE_UNARY = 4;
    
    private static final short TYPE_FINISH = 5;
    
    private static final short TYPE_IDLE = 6;
    
    private static final short TYPE_SKIP = 7;
    
    /**
     * The action type: 1 for init, 2 for shift, 3 for binary, 4 for unary, 5
     * for finish and 6 for idle, 7 for skip.
     */
    public final short type;
    
    /**
     * The category, or {@link SymbolPool.NONE} for action types that are not
     * associated with a category.
     */
    public final short category;

    public static final Action INIT = new Action(TYPE_INIT, SymbolPool.NONE);

    public static final Action FINISH = new Action(TYPE_FINISH, SymbolPool.NONE);

    public static final Action IDLE = new Action(TYPE_IDLE, SymbolPool.NONE);

    public static final Action SKIP = new Action(TYPE_SKIP, SymbolPool.NONE);

    public static Action shift(short category) {
        return new Action(TYPE_SHIFT, category);
    }

    public static Action binary(short category) {
        return new Action(TYPE_BINARY, category);
    }

    public static Action unary(short category) {
        return new Action(TYPE_UNARY, category);
    }

    private Action(short type, short category) {
        this.type = type;
        this.category = category;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + this.type;
        hash = 17 * hash + this.category;
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
        final Action other = (Action) obj;
        if (this.type != other.type) {
            return false;
        }
        if (this.category != other.category) {
            return false;
        }
        return true;
    }

}
