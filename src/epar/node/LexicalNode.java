package epar.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import epar.data.Word;
import epar.grammar.Grammar;
import epar.parser.Action;
import epar.util.SymbolPool;

public class LexicalNode extends Node {

    public LexicalNode(short category, Word word) {
        super(category, word);
    }

    @Override
    public List<Action> actionSequence(Grammar grammar) {
        List<Action> actions = new ArrayList<>();
        actions.add(Action.shift(category));
        return actions;
    }

    @Override
    public String toString() {
        return "( " + SymbolPool.getString(category) + " c " + SymbolPool.getString(lexicalHead.pos) + " "
                + SymbolPool.getString(lexicalHead.form) + " )";
    }

    @Override
    public List<Node> descendants() {
        return Collections.<Node>singletonList(this);
    }

}
