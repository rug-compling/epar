package epar;

import java.io.File;
import java.io.IOException;
import java.util.List;

import epar.grammar.Grammar;
import epar.node.Node;

public class Regrammaticalize {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("USAGE: java Regrammaticalize TREES RULES.BIN RULES.UN");
            System.exit(1);
        }

        try {
            List<Node> trees = Node.readTrees(new File(args[0]));
            File binaryRuleFile = new File(args[1]);
            File unaryRuleFile = new File(args[2]);
            Grammar grammar = Grammar.load(binaryRuleFile, unaryRuleFile);
            
            for (Node node : trees) {
                System.out.println(grammar.regrammaticalize(node, null));
            }
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

}
