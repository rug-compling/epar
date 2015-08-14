package epar.node;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import epar.data.Word;
import epar.grammar.BinaryRule;
import epar.grammar.UnaryRule;
import epar.parser.Action;
import epar.util.RecUtil;
import epar.util.StringPool;

public abstract class Node {

	public final String category;

	public final Word lexicalHead;

	public Node(String category, Word lexicalHead) {
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
		String category = StringPool.get(scanner.next());
		String head = scanner.next();
		Node node;

		if ("l".equals(head)) {
			Node leftChild = readTree(scanner);
			Node rightChild = readTree(scanner);
			node = new BinaryNode(category, leftChild.lexicalHead, leftChild,
					rightChild, new BinaryRule(leftChild.category,
							rightChild.category, category,
							BinaryRule.HeadPosition.LEFT));
		} else if ("r".equals(head)) {
			Node leftChild = readTree(scanner);
			Node rightChild = readTree(scanner);
			node = new BinaryNode(category, rightChild.lexicalHead, leftChild,
					rightChild, new BinaryRule(leftChild.category,
							rightChild.category, category,
							BinaryRule.HeadPosition.RIGHT));
		} else if ("s".equals(head)) {
			Node child = readTree(scanner);
			node = new UnaryNode(category, child.lexicalHead, child,
					new UnaryRule(child.category, category));
		} else if ("c".equals(head)) {
			String form = StringPool.get(scanner.next());
			String pos = StringPool.get(scanner.next());
			Word word = new Word(form, pos, null); // TODO ugh.
			node = new LexicalNode(category, word);
		} else {
			scanner.close();
			throw new RuntimeException("Invalid head indicator: " + head);
		}

		RecUtil.expect(")", scanner);
		return node;
	}

	public abstract List<Action> actionSequence();
	
	public abstract List<Node> descendants();

}
