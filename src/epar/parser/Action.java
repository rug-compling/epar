package epar.parser;

import epar.util.SymbolPool;
import java.util.Objects;

public class Action { // TODO get rid of this class?

    public static final Action INIT = new Action(ActionType.INIT, SymbolPool.NONE);

    public static final Action FINISH = new Action(ActionType.FINISH, SymbolPool.NONE);

    public static final Action IDLE = new Action(ActionType.IDLE, SymbolPool.NONE);

    public static Action shift(short category) {
        return new Action(ActionType.SHIFT, category);
    }

    public static Action binary(short category) {
        return new Action(ActionType.BINARY, category);
    }

    public static Action unary(short category) {
        return new Action(ActionType.UNARY, category);
    }

    private static enum ActionType {

        INIT, SHIFT, BINARY, UNARY, FINISH, IDLE;

        public static ActionType fromString(String string) {
            switch (string) {
                case "INIT":
                    return INIT;
                case "SHIFT":
                    return SHIFT;
                case "BINARY":
                    return BINARY;
                case "UNARY":
                    return UNARY;
                case "FINISH":
                    return FINISH;
                case "IDLE":
                    return IDLE;
                default:
                    throw new IllegalArgumentException("Not an action type: "
                            + string);
            }
        }
    };

    private final ActionType type;

    private final short category;

    private Action(ActionType type, short category) {
        this.type = type;
        this.category = category;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.type);
        hash = 67 * hash + this.category;
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
