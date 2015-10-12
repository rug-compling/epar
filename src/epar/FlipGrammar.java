package epar;

import epar.grammar.BinaryRule;
import java.io.File;
import java.io.IOException;

import epar.grammar.Grammar;
import epar.grammar.UnaryRule;

// TODO port this to Python
public class FlipGrammar {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println(
                    "USAGE: java FlipGrammar GRAMMAR.IN GRAMMAR.OUT");
            System.exit(1);
        }

        try {
            File grammarFileIn = new File(args[0]);
            File grammarFileOut = new File(args[1]);
            Grammar grammarIn = Grammar.load(grammarFileIn);
            Grammar grammarOut = new Grammar();

            for (BinaryRule rule : grammarIn.getBinaryRules()) {
                grammarOut.add(rule);
                grammarOut.add(rule.flip());
            }

            for (UnaryRule rule : grammarIn.getUnaryRules()) {
                grammarOut.add(rule);
            }

            grammarOut.save(grammarFileOut);
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

}
