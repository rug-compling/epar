package epar.node;

import epar.data.LexicalEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import epar.data.Word;
import epar.grammar.Grammar;
import epar.parser.Action;
import epar.util.SymbolPool;

public class LexicalNode extends Node {

    public final short semantics;

    public LexicalNode(LexicalEntry entry, Word word) {
        super(entry.category, word);
        this.semantics = entry.semantics;
    }

    @Override
    public List<Action> actionSequence(Grammar grammar) {
        List<Action> actions = new ArrayList<>();
        actions.add(Action.shift(category, semantics));
        return actions;
    }

    @Override
    public String toString() {
        return "( " + (new LexicalEntry(category, semantics)) + " c " +
                SymbolPool.getString(lexicalHead.pos) + " " +
                SymbolPool.getString(lexicalHead.form) + " )";
    }

    @Override
    public List<Node> descendants() {
        return Collections.<Node>singletonList(this);
    }

}
