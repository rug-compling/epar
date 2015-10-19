package epar.node;

import epar.data.LexicalItem;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import epar.data.SentencePosition;
import epar.grammar.BinaryRule;
import epar.grammar.Grammar;
import epar.grammar.UnaryRule;
import epar.action.Action;
import epar.util.RecUtil;
import epar.util.SymbolPool;

public abstract class Node {

    public final short category;

    public final LexicalItem lexicalHead;

    public Node(short category, LexicalItem lexicalHead) {
        this.category = category;
        this.lexicalHead = lexicalHead;
    }

    public static List<Node> readTrees(File file) throws FileNotFoundException {
        List<Node> trees = new ArrayList<>();
        
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                trees.add(readTree(scanner.nextLine()));
            }
        }
        
        return trees;
    }

    public static Node readTree(String line) {
        Node node;
        
        try (Scanner scanner = new Scanner(line)) {
            node = readTree(scanner);
            assert !scanner.hasNext();
        }
        
        return node;
    }

    private static Node readTree(Scanner scanner) {
        // TODO the ZPar tree format doesn't handle schema names
        RecUtil.expect("(", scanner);
        short category = SymbolPool.getID(scanner.next());
        String head = scanner.next();
        Node node;

        if ("l".equals(head)) {
            Node leftChild = readTree(scanner);
            Node rightChild = readTree(scanner);
            node = new BinaryNode(category,
                    leftChild.lexicalHead, leftChild,
                    rightChild, new BinaryRule(leftChild.category,
                    rightChild.category, category,
                    BinaryRule.HeadPosition.LEFT, "dummy"));
        } else if ("r".equals(head)) {
            Node leftChild = readTree(scanner);
            Node rightChild = readTree(scanner);
            node = new BinaryNode(category,
                    rightChild.lexicalHead, leftChild,
                    rightChild, new BinaryRule(leftChild.category,
                    rightChild.category, category,
                    BinaryRule.HeadPosition.RIGHT, "dummy"));
        } else if ("s".equals(head)) {
            Node child = readTree(scanner);
            node = new UnaryNode(category, child.lexicalHead,
                    child, new UnaryRule(child.category,
                    category, "dummy"));
        } else if ("c".equals(head)) {
            // TODO the ZPar tree format doesn't handle semantics or multiwords
            short pos = SymbolPool.getID(scanner.next());
            short form = SymbolPool.getID(scanner.next());
            LexicalItem item = new LexicalItem(1, form, pos, category,
                    SymbolPool.NONE);
            SentencePosition word = new SentencePosition(form, pos, null); // TODO ugh.
            node = new LexicalNode(item);
        } else {
            scanner.close();
            throw new RuntimeException("Invalid head indicator: " + head);
        }

        RecUtil.expect(")", scanner);
        return node;
    }

    public abstract List<Action> actionSequence(Grammar grammar);

    public abstract List<Node> descendants();

}
