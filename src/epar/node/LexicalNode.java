package epar.node;

import epar.data.LexicalItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import epar.grammar.Grammar;
import epar.action.Action;
import epar.action.ShiftAction;
import epar.util.SymbolPool;

public class LexicalNode extends Node {

    public LexicalNode(LexicalItem item) {
        super(item.category, item);
    }

    @Override
    public List<Action> actionSequence(Grammar grammar) {
        List<Action> actions = new ArrayList<>();
        actions.add(new ShiftAction(lexicalHead.length, lexicalHead.category,
                lexicalHead.semantics));
        return actions;
    }

    @Override
    public String toString() {
        return "( " + SymbolPool.getString(category) + " c " +
                //SymbolPool.getString(lexicalHead.semantics) + " " +
                SymbolPool.getString(lexicalHead.pos) + " " +
                SymbolPool.getString(lexicalHead.form) + " )";
    }

    @Override
    public List<Node> descendants() {
        return Collections.<Node>singletonList(this);
    }

}
