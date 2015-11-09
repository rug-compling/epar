package epar.parser.action;

import epar.grammar.BinaryRule.HeadPosition;
import epar.util.StringUtil;
import epar.util.SymbolPool;

import java.util.ArrayList;
import java.util.List;

public abstract class Action {

    // STATIC CONSTANTS
    public static final short TYPE_INIT = 1;

    public static final short TYPE_SHIFT = 2;

    public static final short TYPE_BINARY = 3;

    public static final short TYPE_UNARY = 4;

    public static final short TYPE_FINISH = 5;

    public static final short TYPE_IDLE = 6;

    public static final short TYPE_SKIP = 7;
    
    public abstract int getType();

    public abstract int getCategory();

    public static Action fromString(String actionString) {
        String[] parts = actionString.split("-");

        switch (parts[0]) {
            case "INIT":
                checkArgs(actionString, parts, 0);
                return InitAction.INSTANCE;
            case "SHIFT":
                checkArgs(actionString, parts, 3);
                return new ShiftAction(Integer.parseInt(parts[1]),
                        SymbolPool.getID(parts[2]), SymbolPool.getID(parts[3]));
            case "BINARY":
                checkArgs(actionString, parts, 3);
                return new BinaryAction(SymbolPool.getID(parts[1]),
                        HeadPosition.fromActionString(parts[2]),
                        SymbolPool.getID(parts[3]));
            case "UNARY":
                checkArgs(actionString, parts, 2);
                return new UnaryAction(SymbolPool.getID(parts[1]),
                        SymbolPool.getID(parts[2]));
            case "FINISH":
                checkArgs(actionString, parts, 0);
                return FinishAction.INSTANCE;
            case "IDLE":
                checkArgs(actionString, parts, 0);
                return IdleAction.INSTANCE;
            case "SKIP":
                checkArgs(actionString, parts, 0);
                return SkipAction.INSTANCE;
            default:
                throw new IllegalArgumentException("Invalid action type " +
                        parts[0] + " in action string " + actionString);
        }
    }

    private static void checkArgs(String actionString, String[] parts,
            int expectedNumArgs) {
        int actualNumArgs = parts.length - 1;

        if (actualNumArgs != expectedNumArgs) {
            throw new IllegalArgumentException("Invalid action " + actionString
                    + ": expected " + expectedNumArgs + " arguments for " +
                    parts[0] + ", got " + actualNumArgs);
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

    public static String sequenceToString(List<Action> actionSequence) {
        return StringUtil.join(actionSequence, " ");
    }

}
