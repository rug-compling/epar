package epar;

import epar.grammar.BinaryRule;
import java.io.File;
import java.io.IOException;

import epar.grammar.Grammar;
import epar.grammar.UnaryRule;

public class FlipGrammar {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("USAGE: java FlipGrammar RULES.BIN.IN RULES.UN.IN RULES.BIN.OUT RULES.UN.OUT");
            System.exit(1);
        }

        try {
            File binaryRuleFileIn = new File(args[0]);
            File unaryRuleFileIn = new File(args[1]);
            File binaryRuleFileOut = new File(args[2]);
            File unaryRuleFileOut = new File(args[3]);
            Grammar grammarIn = Grammar.load(binaryRuleFileIn, unaryRuleFileIn);
            Grammar grammarOut = new Grammar();

            for (BinaryRule rule : grammarIn.getBinaryRules()) {
                grammarOut.add(rule);
                grammarOut.add(rule.flip());
            }

            for (UnaryRule rule : grammarIn.getUnaryRules()) {
                grammarOut.add(rule);
            }

            grammarOut.save(binaryRuleFileOut, unaryRuleFileOut);
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

}
