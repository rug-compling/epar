package epar.grammar;

import epar.data.LexicalItem;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import epar.node.BinaryNode;
import epar.node.Node;
import epar.node.UnaryNode;
import epar.util.SymbolPool;

public class Grammar {

    public static final short SKIP_CATEGORY = SymbolPool.getID("SKIP");

    private final Map<Short, List<UnaryRule>> unaryRulesMap;

    private final Map<Short, Map<Short, List<BinaryRule>>> binaryRulesMap;

    private List<UnaryRule> getUnaryRules(short childCategory) {
        if (!unaryRulesMap.containsKey(childCategory)) {
            unaryRulesMap.put(childCategory, new ArrayList<UnaryRule>());
        }

        return unaryRulesMap.get(childCategory);
    }

    private List<BinaryRule> getBinaryRules(short leftChildCategory, short rightChildCategory) {
        if (!binaryRulesMap.containsKey(leftChildCategory)) {
            binaryRulesMap.put(leftChildCategory, new HashMap<Short, List<BinaryRule>>());
        }

        if (!binaryRulesMap.get(leftChildCategory).containsKey(rightChildCategory)) {
            binaryRulesMap.get(leftChildCategory).put(rightChildCategory, new ArrayList<BinaryRule>());
        }

        return binaryRulesMap.get(leftChildCategory).get(rightChildCategory);
    }

    /**
     * Creates a grammar initially containing no rules.
     */
    public Grammar() {
        unaryRulesMap = new HashMap<>();
        binaryRulesMap = new HashMap<>();
    }

    public void add(UnaryRule rule) {
        List<UnaryRule> rules = getUnaryRules(rule.childCategory);

        if (!rules.contains(rule)) {
            rules.add(rule);
        }
    }

    public void add(BinaryRule rule) {
        List<BinaryRule> rules = getBinaryRules(rule.leftChildCategory, rule.rightChildCategory);

        if (!rules.contains(rule)) {
            rules.add(rule);
        }
    }

    public List<UnaryRule> getUnaryRules() {
        List<UnaryRule> rules = new ArrayList<>();

        for (short childCategory : unaryRulesMap.keySet()) {
            rules.addAll(unaryRulesMap.get(childCategory));
        }

        return rules;
    }

    public List<BinaryRule> getBinaryRules() {
        List<BinaryRule> rules = new ArrayList<>();

        for (short leftChildCategory : binaryRulesMap.keySet()) {
            Map<Short, List<BinaryRule>> map = binaryRulesMap.get(leftChildCategory);

            for (short rightChildCategory : map.keySet()) {
                rules.addAll(map.get(rightChildCategory));
            }
        }

        return rules;
    }

    public List<Node> unary(Node child) {
        List<UnaryRule> rules = unaryRulesMap.get(child.category);

        if (rules == null) {
            return Collections.emptyList();
        }

        List<Node> possibleParents = new ArrayList<>();

        for (UnaryRule rule : rules) {
            // Disallow identity productions
            if (rule.childCategory == rule.parentCategory) {
                continue;
            }

            possibleParents.add(new UnaryNode(rule.parentCategory, child.lexicalHead, child, rule));
        }

        return possibleParents;
    }

    public List<Node> binary(Node leftChild, Node rightChild) {
        Map<Short, List<BinaryRule>> rulesByRightChildCategory = binaryRulesMap.get(leftChild.category);

        if (rulesByRightChildCategory == null) {
            return Collections.emptyList();
        }

        List<BinaryRule> rules = rulesByRightChildCategory.get(rightChild.category);

        if (rules == null) {
            return Collections.emptyList();
        }

        List<Node> possibleParents = new ArrayList<>();

        for (BinaryRule rule : rules) {
            LexicalItem lexicalHead
                    = rule.headPosition == BinaryRule.HeadPosition.LEFT
                            ? leftChild.lexicalHead : rightChild.lexicalHead;
            possibleParents.add(new BinaryNode(rule.parentCategory, lexicalHead,
                    leftChild, rightChild, rule));
        }

        return possibleParents;
    }

    public static Grammar extract(Iterable<Node> trees) {
        Grammar grammar = new Grammar();

        for (Node tree : trees) {
            grammar.extractRules(tree);
        }

        return grammar;
    }

    private void extractRules(Node tree) {
        for (Node node : tree.descendants()) {
            if (node instanceof BinaryNode) {
                BinaryNode binaryNode = (BinaryNode) node;
                List<BinaryRule> rules = getBinaryRules(binaryNode.rule.leftChildCategory,
                        binaryNode.rule.rightChildCategory);
                add(binaryNode.rule);
            } else if (node instanceof UnaryNode) {
                UnaryNode unaryNode = (UnaryNode) node;
                List<UnaryRule> rules = getUnaryRules(unaryNode.rule.childCategory);
                add(unaryNode.rule);
            }
        }
    }

    public static Grammar load(File grammarFile) throws FileNotFoundException {
        Grammar grammar = new Grammar();

        try (Scanner scanner = new Scanner(grammarFile, "utf-8")) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("\t");
                
                if (parts.length == 3) {
                    short daughterCat = SymbolPool.getID(parts[0]);
                    short motherCat = SymbolPool.getID(parts[1]);
                    String schemaName = parts[2];
                    grammar.add(new UnaryRule(daughterCat, motherCat,
                            schemaName));
                } else if (parts.length == 5) {
                    short leftCat = SymbolPool.getID(parts[0]);
                    short rightCat = SymbolPool.getID(parts[1]);
                    short motherCat = SymbolPool.getID(parts[2]);
                    BinaryRule.HeadPosition headPosition =
                            BinaryRule.HeadPosition.fromActionString(parts[3]);
                    String schemaName = parts[4];
                    grammar.add(new BinaryRule(leftCat, rightCat, motherCat,
                            headPosition, schemaName));
                } else {
                    throw new IllegalArgumentException("Invalid rule: " +
                            line);
                }
            }
        }

        return grammar;
    }

    public void save(File grammarFile)
            throws UnsupportedEncodingException, FileNotFoundException, IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(grammarFile), "utf-8"))) {
            for (UnaryRule rule : getUnaryRules()) {
                writer.write(rule.toString());
                writer.write("\n");
            }
            
            for (BinaryRule rule : getBinaryRules()) {
                writer.write(rule.toString());
                writer.write("\n");
            }
        }
    }

    public boolean contains(BinaryRule rule) {
        return getBinaryRules(rule.leftChildCategory, rule.rightChildCategory).contains(rule);
    }

    public boolean contains(UnaryRule rule) {
        return getUnaryRules(rule.childCategory).contains(rule);
    }
}
