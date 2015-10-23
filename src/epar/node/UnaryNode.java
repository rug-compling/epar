package epar.node;

import epar.data.LexicalItem;
import java.util.ArrayList;
import java.util.List;

import epar.grammar.Grammar;
import epar.grammar.UnaryRule;
import epar.parser.action.Action;
import epar.parser.action.UnaryAction;
import epar.util.SymbolPool;

public class UnaryNode extends Node {

    public final Node child;

    public final UnaryRule rule;

    public UnaryNode(short category, LexicalItem lexicalHead, Node child,
            UnaryRule rule) {
        super(category, lexicalHead);
        this.child = child;
        this.rule = rule;
    }

    @Override
    public List<Action> actionSequence(Grammar grammar) {
        if (!grammar.contains(rule)) {
            throw new IllegalArgumentException("No action sequence for " + this + " according to " + grammar + " because of missing rule " + rule);
        }

        List<Action> actions = child.actionSequence(grammar);
        actions.add(new UnaryAction(rule.schemaName, category));
        return actions;
    }

    @Override
    public String toString() {
        return "( " + SymbolPool.getString(category) + " s " + child + " )";
    }

    @Override
    public List<Node> descendants() {
        List<Node> descendants = new ArrayList<Node>();
        descendants.add(this);
        descendants.addAll(child.descendants());
        return descendants;
    }

}
