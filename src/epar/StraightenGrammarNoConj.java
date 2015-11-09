package epar;

import epar.grammar.BinaryRule;
import java.io.File;
import java.io.IOException;

import epar.grammar.Grammar;
import epar.grammar.UnaryRule;
import epar.util.SymbolPool;

// TODO port this to Python
public class StraightenGrammarNoConj {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("USAGE: java StraightenGrammarNoConj GRAMMAR.IN GRAMMAR.OUT");
            System.exit(1);
        }

        try {
            File grammarFileIn = new File(args[0]);
            File grammarFileOut = new File(args[1]);
            Grammar grammarIn = Grammar.load(grammarFileIn);
            Grammar grammarOut = new Grammar();

            for (BinaryRule rule : grammarIn.getBinaryRules()) {
                int leftCat = SymbolPool.getID(SymbolPool.getString(rule.leftChildCategory).replace('\\', '/'));
                int rightCat = SymbolPool.getID(SymbolPool.getString(rule.rightChildCategory).replace('\\', '/'));
                int parentCat = SymbolPool.getID(SymbolPool.getString(rule.parentCategory).replace('\\', '/'));
                BinaryRule newRule = new BinaryRule(leftCat, rightCat, parentCat, rule.headPosition, rule.schemaName);
                grammarOut.add(newRule);
                
                if (!isConj(newRule)) {
                    grammarOut.add(newRule.flip());
                }
            }

            for (UnaryRule rule : grammarIn.getUnaryRules()) {
                int childCat = SymbolPool.getID(SymbolPool.getString(rule.childCategory).replace('\\', '/'));
                int parentCat = SymbolPool.getID(SymbolPool.getString(rule.parentCategory).replace('\\', '/'));
                grammarOut.add(new UnaryRule(childCat, parentCat, rule.schemaName));
            }

            grammarOut.save(grammarFileOut);
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

    private static boolean isConj(BinaryRule rule) {
        return SymbolPool.getString(rule.rightChildCategory).contains("conj") ||
                SymbolPool.getString(rule.leftChildCategory).contains("conj") ||
                SymbolPool.getString(rule.parentCategory).contains("conj'");
    }

}
