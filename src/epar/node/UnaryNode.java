package epar.node;

import java.util.ArrayList;
import java.util.List;

import epar.data.Word;
import epar.grammar.BinaryRule;
import epar.grammar.Grammar;
import epar.grammar.UnaryRule;
import epar.parser.Action;
import epar.util.SymbolPool;

public class UnaryNode extends Node {

    public final Node child;

    public final UnaryRule rule;

    public UnaryNode(short category, Word lexicalHead, Node child,
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
        actions.add(Action.unary(category));
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

    @Override
    public Node regrammaticalize(Short cat, Grammar grammar) {
        UnaryRule straightenedRule = rule.straighten();
        List<UnaryRule> candidates = new ArrayList<>();

        for (UnaryRule candidate : grammar.getUnaryRules()) { // TODO linear search, inefficient
            if (candidate.straighten().equals(straightenedRule) && (cat == null || candidate.parentCategory == cat)) {
                candidates.add(candidate);
            }
        }

        if (candidates.isEmpty()) {
            throw new IllegalArgumentException("No rule found corresponding to "
                    + rule);
        }

        if (candidates.size() > 1) {
            throw new IllegalArgumentException("More than one rule corresponding to " + rule + ": " + candidates);
        }

        UnaryRule newRule = candidates.get(0);
        return new UnaryNode(newRule.parentCategory, lexicalHead,
                child.regrammaticalize(newRule.childCategory, grammar),
                newRule);
    }

}
