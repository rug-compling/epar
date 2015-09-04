package epar;

import epar.grammar.BinaryRule;
import java.io.File;
import java.io.IOException;

import epar.grammar.Grammar;
import epar.grammar.UnaryRule;
import epar.util.SymbolPool;

public class StraightenGrammar {

    public static void main(String[] args) {
        if (args.length != 4) {
            System.err.println("USAGE: java StraightenGrammar RULES.BIN.IN RULES.UN.IN RULES.BIN.OUT RULES.UN.OUT");
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
                short leftCat = SymbolPool.getID(SymbolPool.getString(rule.leftChildCategory).replace('\\', '|').replace('/', '|'));
                short rightCat = SymbolPool.getID(SymbolPool.getString(rule.rightChildCategory).replace('\\', '|').replace('/', '|'));
                short parentCat = SymbolPool.getID(SymbolPool.getString(rule.parentCategory).replace('\\', '|').replace('/', '|'));
                grammarOut.add(new BinaryRule(leftCat, rightCat, parentCat, rule.headPosition));
                grammarOut.add(new BinaryRule(rightCat, leftCat, parentCat, rule.headPosition.flip()));
            }

            for (UnaryRule rule : grammarIn.getUnaryRules()) {
                short childCat = SymbolPool.getID(SymbolPool.getString(rule.childCategory).replace('\\', '|').replace('/', '|'));
                short parentCat = SymbolPool.getID(SymbolPool.getString(rule.parentCategory).replace('\\', '|').replace('/', '|'));
                grammarOut.add(new UnaryRule(childCat, parentCat));
            }

            grammarOut.save(binaryRuleFileOut, unaryRuleFileOut);
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

}
