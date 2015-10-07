package epar;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.List;

import epar.grammar.Grammar;
import epar.node.Node;
import epar.oracle.Oracle;
import epar.parser.Action;

public class TreesToActionSequences {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("USAGE: java TreesToActionSequences GOLDTREES RULES.BIN RULES.UN");
            System.exit(1);
        }

        try {
            List<Node> goldTrees = Node.readTrees(new File(args[0]));
            Grammar grammar = Grammar.load(new File(args[1]), new File(args[2]));
            
            List<Oracle> oracles = new ArrayList<>(goldTrees.size());
            
            for (Node tree : goldTrees) {
                List<Action> actionSequence = tree.actionSequence(grammar);
                actionSequence.add(Action.FINISH);
                System.out.println(Action.sequenceToString(actionSequence));
            }
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

}
