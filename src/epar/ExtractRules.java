package epar;

import epar.node.BinaryNode;
import java.io.File;
import java.io.IOException;
import java.util.List;

import epar.node.Node;
import epar.node.UnaryNode;

/**
 * Lists each rule instance in a treebank, providing a suitable input for
 * frequency counting and so on.
 * @author p264360
 */
public class ExtractRules {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("USAGE: java ExtractGrammar TREES");
            System.exit(1);
        }

        try {
            List<Node> trees = Node.readTrees(new File(args[0]));
            
            for (Node tree : trees) {
                for (Node descendant : tree.descendants()) {
                    if (descendant instanceof BinaryNode) {
                        System.out.println(((BinaryNode) descendant).rule);
                    } else if (descendant instanceof UnaryNode) {
                        System.out.println(((UnaryNode) descendant).rule);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

}
