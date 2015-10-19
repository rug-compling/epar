package epar;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.List;

import epar.grammar.Grammar;
import epar.node.Node;
import epar.oracle.Oracle;
import epar.action.Action;
import epar.action.FinishAction;

public class TreesToActionSequences {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("USAGE: java TreesToActionSequences GOLDTREES GRAMMAR");
            System.exit(1);
        }

        try {
            List<Node> goldTrees = Node.readTrees(new File(args[0]));
            Grammar grammar = Grammar.load(new File(args[1]));
            
            List<Oracle> oracles = new ArrayList<>(goldTrees.size());
            
            for (Node tree : goldTrees) {
                List<Action> actionSequence = tree.actionSequence(grammar);
                actionSequence.add(FinishAction.INSTANCE);
                System.out.println(Action.sequenceToString(actionSequence));
            }
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

}
