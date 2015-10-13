package epar.parser;

import epar.data.LexicalItem;
import java.util.ArrayList;
import java.util.List;

import epar.data.Sentence;
import epar.data.SentencePosition;
import epar.grammar.Grammar;
import epar.node.LexicalNode;
import epar.node.Node;
import epar.util.EStack;
import epar.util.NEStack;
import epar.util.Stack;

public class Item {

    public final Action action;

    public final Stack<Node> stack;

    public final Stack<SentencePosition> queue;

    public final boolean finished;

    private Item(Action action, Stack<Node> stack, Stack<SentencePosition> queue,
            boolean finished) {
        this.action = action;
        this.stack = stack;
        this.queue = queue;
        this.finished = finished;
    }

    public List<Item> successors(Grammar grammar) {
        List<Item> successors = new ArrayList<>();
        shift(successors);
        binary(successors, grammar);
        unary(successors, grammar);
        finish(successors);
        idle(successors);
        skip(successors);
        return successors;
    }

    private void binary(List<Item> successors, Grammar grammar) {
        if (finished) {
            return;
        }

        if (stack.isEmpty()) {
            return;
        }

        Node rightChild = stack.getFirst();
        Stack<Node> rest = stack.getRest();

        if (rest.isEmpty()) {
            return;
        }

        Node leftChild = rest.getFirst();
        Stack<Node> restRest = rest.getRest();

        for (Node parent : grammar.binary(leftChild, rightChild)) {
            Action newAction = Action.binary(parent.category);
            Stack<Node> newStack = restRest.push(parent);
            successors.add(new Item(newAction, newStack, queue, false));
        }
    }

    private void unary(List<Item> successors, Grammar grammar) {
        if (finished) {
            return;
        }

        if (stack.isEmpty()) {
            return;
        }

        Node child = stack.getFirst();
        Stack<Node> rest = stack.getRest();

        for (Node parent : grammar.unary(child)) {
            Action newAction = Action.unary(parent.category);
            Stack<Node> newStack = rest.push(parent);
            successors.add(new Item(newAction, newStack, queue, false));
        }
    }

    private void skip(List<Item> successors) {
        if (finished) {
            return;
        }

        if (stack.isEmpty()) {
            return;
        }

        if (stack.getFirst().category != Grammar.SKIP_CATEGORY) {
            return;
        }

        successors.add(new Item(Action.SKIP, stack.getRest(), queue, false));
    }

    private void shift(List<Item> successors) {
        if (queue.isEmpty()) {
            return;
        }

        SentencePosition sentencePosition = queue.getFirst();

        for (LexicalItem item : sentencePosition.lexicalItems) {
            Action newAction = Action.shift(item.length, item.category,
                    item.semantics);
            Node newNode = new LexicalNode(item);
            Stack<Node> newStack = stack.push(newNode);
            Stack<SentencePosition> newQueue = queue;
            
            // Pop as many positions from the queue as the item is long (> 1 if
            // it is a multiword;
            for (int i = 0; i < item.length; i++) {
                newQueue = newQueue.getRest();
            }
            
            successors.add(new Item(newAction, newStack, newQueue, false));
        }
    }

    // Could perhaps conflate FINISH and IDLE.
    private void finish(List<Item> successors) {
        if (finished) {
            return;
        }

        if (!queue.isEmpty()) {
            return;
        }

        successors.add(new Item(Action.FINISH, stack, queue, true));
    }

    private void idle(List<Item> successors) {
        if (!finished) {
            return;
        }

        successors.add(new Item(Action.IDLE, stack, queue, true));
    }

    public static Item initial(Sentence sentence) {
        Stack<SentencePosition> queue = new EStack<>();

        for (int i = sentence.positions.size() - 1; i >= 0; i--) {
            queue = new NEStack<>(sentence.positions.get(i), queue);
        }

        return new Item(Action.INIT, new EStack<Node>(), queue, false);
    }

    @Override
    public String toString() {
        return "(" + action + ", " + stack.size() + ", " + queue.size() + ", " + finished + ")";
    }

}
