package epar.node;

import epar.data.LexicalEntry;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import epar.data.Word;
import epar.grammar.BinaryRule;
import epar.grammar.Grammar;
import epar.grammar.UnaryRule;
import epar.parser.Action;
import epar.util.RecUtil;
import epar.util.SymbolPool;

public abstract class Node {

    public final short category;

    public final Word lexicalHead;

    public Node(short category, Word lexicalHead) {
        this.category = category;
        this.lexicalHead = lexicalHead;
    }

    public static List<Node> readTrees(File file) throws FileNotFoundException {
        List<Node> trees = new ArrayList<Node>();
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            trees.add(readTree(scanner.nextLine()));
        }

        scanner.close();
        return trees;
    }

    public static Node readTree(String line) {
        Scanner scanner = new Scanner(line);
        Node node = readTree(scanner);
        assert !scanner.hasNext();
        scanner.close();
        return node;
    }

    private static Node readTree(Scanner scanner) {
        RecUtil.expect("(", scanner);
        String category = scanner.next();
        String head = scanner.next();
        Node node;

        if ("l".equals(head)) {
            Node leftChild = readTree(scanner);
            Node rightChild = readTree(scanner);
            node = new BinaryNode(SymbolPool.getID(category),
                    leftChild.lexicalHead, leftChild,
                    rightChild, new BinaryRule(leftChild.category,
                    rightChild.category, SymbolPool.getID(category),
                    BinaryRule.HeadPosition.LEFT));
        } else if ("r".equals(head)) {
            Node leftChild = readTree(scanner);
            Node rightChild = readTree(scanner);
            node = new BinaryNode(SymbolPool.getID(category),
                    rightChild.lexicalHead, leftChild,
                    rightChild, new BinaryRule(leftChild.category,
                    rightChild.category, SymbolPool.getID(category),
                    BinaryRule.HeadPosition.RIGHT));
        } else if ("s".equals(head)) {
            Node child = readTree(scanner);
            node = new UnaryNode(SymbolPool.getID(category), child.lexicalHead,
                    child, new UnaryRule(child.category,
                    SymbolPool.getID(category)));
        } else if ("c".equals(head)) {
            LexicalEntry entry = LexicalEntry.fromString(category);
            short pos = SymbolPool.getID(scanner.next());
            short form = SymbolPool.getID(scanner.next());
            Word word = new Word(form, pos, null); // TODO ugh.
            node = new LexicalNode(entry, word);
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
