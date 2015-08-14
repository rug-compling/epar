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

public class Grammar {

	private final Map<String, List<UnaryRule>> unaryRulesMap;

	private final Map<String, Map<String, List<BinaryRule>>> binaryRulesMap;

	private List<UnaryRule> getUnaryRules(String childCategory) {
		if (!unaryRulesMap.containsKey(childCategory)) {
			unaryRulesMap.put(childCategory, new ArrayList<UnaryRule>());
		}

		return unaryRulesMap.get(childCategory);
	}

	private List<BinaryRule> getBinaryRules(String leftChildCategory, String rightChildCategory) {
		if (!binaryRulesMap.containsKey(leftChildCategory)) {
			binaryRulesMap.put(leftChildCategory, new HashMap<String, List<BinaryRule>>());
		}

		if (!binaryRulesMap.get(leftChildCategory).containsKey(rightChildCategory)) {
			binaryRulesMap.get(leftChildCategory).put(rightChildCategory, new ArrayList<BinaryRule>());
		}

		return binaryRulesMap.get(leftChildCategory).get(rightChildCategory);
	}

	private Grammar() {
		unaryRulesMap = new HashMap<String, List<UnaryRule>>();
		binaryRulesMap = new HashMap<String, Map<String, List<BinaryRule>>>();
	}

	public Grammar(List<UnaryRule> unaryRules, List<BinaryRule> binaryRules) {
		this();

		for (UnaryRule rule : unaryRules) {
			getUnaryRules(rule.childCategory).add(rule);
		}

		for (BinaryRule rule : binaryRules) {
			getBinaryRules(rule.leftChildCategory, rule.rightChildCategory).add(rule);
		}
	}

	public List<Node> unary(Node child) {
		List<UnaryRule> rules = unaryRulesMap.get(child.category);

		if (rules == null) {
			return Collections.emptyList();
		}

		List<Node> possibleParents = new ArrayList<Node>();

		for (UnaryRule rule : rules) {
			// Disallow identity productions
			if (rule.childCategory.equals(rule.parentCategory)) {
				continue;
			}

			possibleParents.add(new UnaryNode(rule.parentCategory, child.lexicalHead, child, rule));
		}

		return possibleParents;
	}

	public List<Node> binary(Node leftChild, Node rightChild) {
		Map<String, List<BinaryRule>> rulesByRightChildCategory = binaryRulesMap.get(leftChild.category);

		if (rulesByRightChildCategory == null) {
			return Collections.emptyList();
		}

		List<BinaryRule> rules = rulesByRightChildCategory.get(rightChild.category);

		if (rules == null) {
			return Collections.emptyList();
		}

		List<Node> possibleParents = new ArrayList<Node>();

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

				if (!rules.contains(binaryNode.rule)) {
					rules.add(binaryNode.rule);
				}
			} else if (node instanceof UnaryNode) {
				UnaryNode unaryNode = (UnaryNode) node;
				List<UnaryRule> rules = getUnaryRules(unaryNode.rule.childCategory);

				if (!rules.contains(unaryNode.rule)) {
					rules.add(unaryNode.rule);
				}
			}
		}
	}

	public static Grammar load(File binaryFile, File unaryFile) throws FileNotFoundException {
		List<BinaryRule> binaryRules = new ArrayList<BinaryRule>();
		List<UnaryRule> unaryRules = new ArrayList<UnaryRule>();

		try (Scanner scanner = new Scanner(binaryFile, "utf-8")) {
			while (scanner.hasNextLine()) {
				binaryRules.addAll(BinaryRule.read(scanner.nextLine()));
			}
		}

		try (Scanner scanner = new Scanner(unaryFile, "utf-8")) {
			while (scanner.hasNextLine()) {
				unaryRules.addAll(UnaryRule.read(scanner.nextLine()));
			}
		}

		return new Grammar(unaryRules, binaryRules);
	}

	public void save(File binaryFile, File unaryFile)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(binaryFile), "utf-8"))) {
			for (Map<String, List<BinaryRule>> map : binaryRulesMap.values()) {
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
		writer.write(rules.get(0).leftChildCategory);
		writer.write(" , ");
		writer.write(rules.get(0).rightChildCategory);
		writer.write(" : [ REDUCE BINARY ");
		writeHeadPosition(writer, rules.get(0).headPosition);
		writer.write(" ");
		writer.write(rules.get(0).parentCategory);
		writer.write(" ");
		
		for (BinaryRule rule : rules.subList(1, rules.size())) {
			writer.write(", REDUCE BINARY ");
			writeHeadPosition(writer, rule.headPosition);
			writer.write(" ");
			writer.write(rule.parentCategory);			
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
		writer.write(rules.get(0).childCategory);
		writer.write(" : [ REDUCE UNARY ");
		writer.write(rules.get(0).parentCategory);
		writer.write(" ");
		
		for (UnaryRule rule : rules.subList(1, rules.size())) {
			writer.write(", REDUCE UNARY ");
			writer.write(rule.parentCategory);
			writer.write(" ");
		}
		
		writer.write("]\n");
	}
}