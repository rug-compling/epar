package epar.grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import epar.data.Word;
import epar.node.BinaryNode;
import epar.node.Node;
import epar.node.UnaryNode;

public class Grammar {

	private final Map<String, List<UnaryRule>> unaryRulesMap;

	private final Map<String, Map<String, List<BinaryRule>>> binaryRulesMap;

	public Grammar(List<UnaryRule> unaryRules, List<BinaryRule> binaryRules) {
		unaryRulesMap = new HashMap<String, List<UnaryRule>>();

		for (UnaryRule rule : unaryRules) {
			if (!unaryRulesMap.containsKey(rule.childCategory)) {
				unaryRulesMap.put(rule.childCategory,
						new ArrayList<UnaryRule>());
			}

			unaryRulesMap.get(rule.childCategory).add(rule);
		}

		binaryRulesMap = new HashMap<String, Map<String, List<BinaryRule>>>();

		for (BinaryRule rule : binaryRules) {
			if (!binaryRulesMap.containsKey(rule.leftChildCategory)) {
				binaryRulesMap.put(rule.leftChildCategory,
						new HashMap<String, List<BinaryRule>>());
			}

			if (!binaryRulesMap.get(rule.leftChildCategory).containsKey(
					rule.rightChildCategory)) {
				binaryRulesMap.get(rule.leftChildCategory).put(
						rule.rightChildCategory, new ArrayList<BinaryRule>());
			}

			binaryRulesMap.get(rule.leftChildCategory)
					.get(rule.rightChildCategory).add(rule);
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
			
			possibleParents.add(new UnaryNode(rule.parentCategory,
					child.lexicalHead, child, rule));
		}

		return possibleParents;
	}

	public List<Node> binary(Node leftChild, Node rightChild) {
		Map<String, List<BinaryRule>> rulesByRightChildCategory = binaryRulesMap
				.get(leftChild.category);

		if (rulesByRightChildCategory == null) {
			return Collections.emptyList();
		}

		List<BinaryRule> rules = rulesByRightChildCategory
				.get(rightChild.category);

		if (rules == null) {
			return Collections.emptyList();
		}

		List<Node> possibleParents = new ArrayList<Node>();

		for (BinaryRule rule : rules) {
			Word lexicalHead = rule.headPosition == BinaryRule.HeadPosition.LEFT ? leftChild.lexicalHead
					: rightChild.lexicalHead;
			possibleParents.add(new BinaryNode(rule.parentCategory,
					lexicalHead, leftChild, rightChild, rule));
		}

		return possibleParents;
	}

	public static Grammar load(File binaryFile, File unaryFile)
			throws FileNotFoundException {
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
}
