package epar.parser;

import epar.data.LexicalEntry;
import epar.util.StringUtil;
import epar.util.SymbolPool;

import java.util.ArrayList;
import java.util.List;

public class Action {
    
    // STATIC CONSTANTS

    private static final short TYPE_INIT = 1;

    private static final short TYPE_SHIFT = 2;

    private static final short TYPE_BINARY = 3;

    private static final short TYPE_UNARY = 4;

    private static final short TYPE_FINISH = 5;

    private static final short TYPE_IDLE = 6;

    private static final short TYPE_SKIP = 7;

    public static final Action INIT = new Action(TYPE_INIT, SymbolPool.NONE);

    public static final Action FINISH = new Action(TYPE_FINISH, SymbolPool.NONE);

    public static final Action IDLE = new Action(TYPE_IDLE, SymbolPool.NONE);

    public static final Action SKIP = new Action(TYPE_SKIP, SymbolPool.NONE);
    
    // INSTANCE CONSTANTS

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
    
    public final short semantics;
    
    // CONSTRUCTORS
    
    private Action(short type, short category) {
        this(type, category, SymbolPool.NONE);
    }

    private Action(short type, short category, short semantics) {
        this.type = type;
        this.category = category;
        this.semantics = semantics;
    }
    
    // FACTORY METHODS

    public static Action shift(short category, short semantics) {
        return new Action(TYPE_SHIFT, category, semantics);
    }

    public static Action binary(short category) {
        return new Action(TYPE_BINARY, category);
    }

    public static Action unary(short category) {
        return new Action(TYPE_UNARY, category);
    }
    
    // INSTANCE METHODS
    
    @Override
    public String toString() {
        String string;
        
        switch (type) {
            case TYPE_INIT:
                string = "INIT";
                break;
            case TYPE_SHIFT:
                string = "SHIFT";
                break;
            case TYPE_BINARY:
                string = "BINARY";
                break;
            case TYPE_UNARY:
                string = "UNARY";
                break;
            case TYPE_FINISH:
                string = "FINISH";
                break;
            case TYPE_IDLE:
                string = "IDLE";
                break;
            case TYPE_SKIP:
                string = "SKIP";
                break;
            default:
                throw new IllegalArgumentException("Action with unknown type code " + type);
        }
        
        if (type == TYPE_SHIFT || type == TYPE_BINARY || type == TYPE_UNARY) {
            string += "-" + (new LexicalEntry(category, semantics));
        }
        
        return string;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + this.type;
        hash = 13 * hash + this.category;
        hash = 13 * hash + this.semantics;
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
        if (this.semantics != other.semantics) {
            return false;
        }
        return true;
    }
    
    // STATIC METHODS

    public static Action fromString(String actionString) {
        String[] parts = actionString.split("-", 2);

        switch (parts[0]) {
            case "INIT":
                checkArgs(actionString, parts, 0);
                return INIT;
            case "SHIFT":
                checkArgs(actionString, parts, 1);
                LexicalEntry entry = LexicalEntry.fromString(parts[1]);
                return shift(entry.category, entry.semantics);
            case "BINARY":
                checkArgs(actionString, parts, 1);
                return binary(SymbolPool.getID(parts[1]));
            case "UNARY":
                checkArgs(actionString, parts, 1);
                return unary(SymbolPool.getID(parts[1]));
            case "FINISH":
                checkArgs(actionString, parts, 0);
                return FINISH;
            case "IDLE":
                checkArgs(actionString, parts, 0);
                return IDLE;
            case "SKIP":
                checkArgs(actionString, parts, 0);
                return SKIP;
            default:
                throw new IllegalArgumentException("Invalid action type: " + parts[0]);
        }
    }

    public static List<Action> sequenceFromString(String sequenceString) {
        String[] actionStrings = sequenceString.split(" ");
        List<Action> actionSequence = new ArrayList<>(actionStrings.length);

        for (String actionString : actionStrings) {
            actionSequence.add(Action.fromString(actionString));
        }

        return actionSequence;
    }

    private static void checkArgs(String actionString, String[] parts, int expectedNumArgs) {
        int actualNumArgs = parts.length - 1;

        if (actualNumArgs != expectedNumArgs) {
            throw new IllegalArgumentException("Invalid action " + actionString + ": expected " + expectedNumArgs + " arguments for " + parts[0] + ", got " + actualNumArgs);
        }
    }

    public static String sequenceToString(List<Action> actionSequence) {
        return StringUtil.join(actionSequence, " ");
    }

}
