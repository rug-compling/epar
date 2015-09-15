package epar.grammar;

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

import epar.data.Word;
import epar.grammar.BinaryRule.HeadPosition;
import epar.node.BinaryNode;
import epar.node.Node;
import epar.node.UnaryNode;
import epar.util.SymbolPool;

public class Grammar {

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
            Word lexicalHead = rule.headPosition == BinaryRule.HeadPosition.LEFT ? leftChild.lexicalHead
                    : rightChild.lexicalHead;
            possibleParents.add(new BinaryNode(rule.parentCategory, lexicalHead, leftChild, rightChild, rule));
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

    public static Grammar load(File binaryFile, File unaryFile) throws FileNotFoundException {
        Grammar grammar = new Grammar();

        try (Scanner scanner = new Scanner(binaryFile, "utf-8")) {
            while (scanner.hasNextLine()) {
                for (BinaryRule rule : BinaryRule.read(scanner.nextLine())) {
                    grammar.add(rule);
                }
            }
        }

        try (Scanner scanner = new Scanner(unaryFile, "utf-8")) {
            while (scanner.hasNextLine()) {
                for (UnaryRule rule : UnaryRule.read(scanner.nextLine())) {
                    grammar.add(rule);
                }
            }
        }

        return grammar;
    }

    public void save(File binaryFile, File unaryFile)
            throws UnsupportedEncodingException, FileNotFoundException, IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(binaryFile), "utf-8"))) {
            for (Map<Short, List<BinaryRule>> map : binaryRulesMap.values()) {
                for (List<BinaryRule> rules : map.values()) {
                    writeBinaryRules(writer, rules);
                }
            }
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(unaryFile), "utf-8"))) {
            for (List<UnaryRule> rules : unaryRulesMap.values()) {
                writeUnaryRules(writer, rules);
            }

        }
    }

    private void writeBinaryRules(Writer writer, List<BinaryRule> rules) throws IOException {
        writer.write(SymbolPool.getString(rules.get(0).leftChildCategory));
        writer.write(" , ");
        writer.write(SymbolPool.getString(rules.get(0).rightChildCategory));
        writer.write(" : [ REDUCE BINARY ");
        writeHeadPosition(writer, rules.get(0).headPosition);
        writer.write(" ");
        writer.write(SymbolPool.getString(rules.get(0).parentCategory));
        writer.write(" ");

        for (BinaryRule rule : rules.subList(1, rules.size())) {
            writer.write(", REDUCE BINARY ");
            writeHeadPosition(writer, rule.headPosition);
            writer.write(" ");
            writer.write(SymbolPool.getString(rule.parentCategory));
            writer.write(" ");
        }

        writer.write("]\n");
    }

    private void writeHeadPosition(Writer writer, HeadPosition headPosition) throws IOException {
        if (headPosition.equals(BinaryRule.HeadPosition.LEFT)) {
            writer.write("LEFT");
        } else {
            writer.write("RIGHT");
        }
    }

    private void writeUnaryRules(Writer writer, List<UnaryRule> rules) throws IOException {
        writer.write(SymbolPool.getString(rules.get(0).childCategory));
        writer.write(" : [ REDUCE UNARY ");
        writer.write(SymbolPool.getString(rules.get(0).parentCategory));
        writer.write(" ");

        for (UnaryRule rule : rules.subList(1, rules.size())) {
            writer.write(", REDUCE UNARY ");
            writer.write(SymbolPool.getString(rule.parentCategory));
            writer.write(" ");
        }

        writer.write("]\n");
    }
}
