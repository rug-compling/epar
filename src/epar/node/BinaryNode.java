package epar.node;

import epar.data.LexicalItem;
import java.util.ArrayList;
import java.util.List;

import epar.grammar.BinaryRule;
import epar.grammar.Grammar;
import epar.parser.action.Action;
import epar.parser.action.BinaryAction;
import epar.util.SymbolPool;

public class BinaryNode extends Node {

    public final Node leftChild;

    public final Node rightChild;

    public final BinaryRule rule;

    public BinaryNode(int category, LexicalItem lexicalHead, Node leftChild,
            Node rightChild, BinaryRule rule) {
        super(category, lexicalHead);
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.rule = rule;
    }

    @Override
    public List<Action> actionSequence(Grammar grammar) {
        if (!grammar.contains(rule)) {
            throw new IllegalArgumentException("No action sequence for " + this + " according to " + grammar + " because of missing rule " + rule);
        }

        List<Action> actions = leftChild.actionSequence(grammar);
        actions.addAll(rightChild.actionSequence(grammar));
        actions.add(new BinaryAction(rule.schemaName, rule.headPosition, category));
        return actions;
    }

    @Override
    public String toString() {
        return "( " + SymbolPool.getString(category) + " " + rule.headPosition + " " + leftChild
                + " " + rightChild + " )";
    }

    @Override
    public List<Node> descendants() {
        List<Node> descendants = new ArrayList<>();
        descendants.add(this);
        descendants.addAll(leftChild.descendants());
        descendants.addAll(rightChild.descendants());
        return descendants;
    }

}
