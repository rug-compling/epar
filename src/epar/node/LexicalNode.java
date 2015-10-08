package epar.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import epar.data.Word;
import epar.grammar.Grammar;
import epar.parser.Action;
import epar.util.SymbolPool;

public class LexicalNode extends Node {
    
    public final short semantics;

    public LexicalNode(short category, short semantics, Word word) {
        super(category, word);
        this.semantics = semantics;
    }

    @Override
    public List<Action> actionSequence(Grammar grammar) {
        List<Action> actions = new ArrayList<>();
        actions.add(Action.shift(category, semantics));
        return actions;
    }

    @Override
    public String toString() { // TODO add semantics somehow
        return "( " + SymbolPool.getString(category) + " c " + SymbolPool.getString(lexicalHead.pos) + " "
                + SymbolPool.getString(lexicalHead.form) + " )";
    }

    @Override
    public List<Node> descendants() {
        return Collections.<Node>singletonList(this);
    }

}
