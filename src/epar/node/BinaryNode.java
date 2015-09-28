package epar.node;

import java.util.ArrayList;
import java.util.List;

import epar.data.Word;
import epar.grammar.BinaryRule;
import epar.grammar.Grammar;
import epar.parser.Action;
import epar.util.SymbolPool;

public class BinaryNode extends Node {

    public final Node leftChild;

    public final Node rightChild;

    public final BinaryRule rule;

    public BinaryNode(short category, Word lexicalHead, Node leftChild,
            Node rightChild, BinaryRule rule) {
        super(category, lexicalHead);
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.rule = rule;
    }

    @Override
    public List<Action> actionSequence() {
        List<Action> actions = leftChild.actionSequence();
        actions.addAll(rightChild.actionSequence());
        actions.add(Action.binary(category));
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

    @Override
    public Node regrammaticalize(Short cat, Grammar grammar) {
        BinaryRule straightenedRule = rule.straighten();
        List<BinaryRule> candidates = new ArrayList<>();

        for (BinaryRule candidate : grammar.getBinaryRules()) { // TODO linear search, inefficient
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

        BinaryRule newRule = candidates.get(0);
        return new BinaryNode(newRule.parentCategory, lexicalHead,
                leftChild.regrammaticalize(newRule.leftChildCategory, grammar),
                rightChild.regrammaticalize(newRule.rightChildCategory, grammar),
                newRule);
    }

}
