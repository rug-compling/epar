package epar;

import java.io.File;
import java.io.IOException;
import java.util.List;

import epar.grammar.Grammar;
import epar.node.Node;

public class ExtractGrammar {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.err.println("USAGE: java ExtractGrammar TREES GRAMMAR");
			System.exit(1);
		}

		try {
			List<Node> trees = Node.readTrees(new File(args[0]));
			File grammarFile = new File(args[1]);
			Grammar.extract(trees).save(grammarFile);
		} catch (IOException e) {
			System.err.println("ERROR: " + e.getLocalizedMessage());
			System.exit(1);
		}
	}

}
