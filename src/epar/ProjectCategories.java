package epar;

import epar.data.LexicalItem;
import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.node.Node;
import epar.oracle.NoFragmentsOracle;
import epar.oracle.Oracle;
import epar.parser.ForceAgenda;
import epar.parser.Item;
import epar.projection.MultiAlignment;
import epar.projection.TranslationUnit;
import epar.sem.Interpretation;
import epar.util.ListUtil;
import epar.util.SymbolPool;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Takes source and target sentence pairs, together with word alignments, as
 * input. Produces as output a parser queue for each target sentence, with
 * possible lexical items based on the aligned source words.
 *
 * @author p264360
 */
public class ProjectCategories {

    public static void main(String[] args) {
        if (args.length != 8) {
            System.err.println(
                    "USAGE: java epar.ProjectCategories SRCTRG.ALIGN TRGSRC.ALIGN NBEST.INPUT NBEST.OUTPUT SENTENCES.SRC GRAMMAR.SRC SENTENCES.TRG LOCLEXINPUT.TRG");
            // where SENTENCES.SRC contains categories and interpretations, SENTENCES.TRG does not
            System.exit(1);
        }

        try {
            List<MultiAlignment> multiAlignments = MultiAlignment.read(
                    new File(args[0]), new File(args[1]),
                    Integer.parseInt(args[2]), Integer.parseInt(args[3]));

            List<Sentence> sourceSentences = Sentence.readSentences(new File(args[4]));

            if (multiAlignments.size() != sourceSentences.size()) {
                throw new IllegalArgumentException("Numbers of multialignments and source sentences don't match.");
            }

            Grammar sourceGrammar = Grammar.load(new File(args[5]));

            List<Sentence> targetSentences = Sentence.readSentences(new File(args[6]));

            if (multiAlignments.size() != targetSentences.size()) {
                throw new IllegalArgumentException("Numbers of multialignments and target sentences don't match.");
            }

            Oracle oracle = new NoFragmentsOracle();

            // For every sentence pair
            for (int i = 0; i < multiAlignments.size(); i++) {
                MultiAlignment multiAlignment = multiAlignments.get(i);
                Sentence sourceSentence = sourceSentences.get(i);
                Sentence targetSentence = targetSentences.get(i);

                // For every position in the target sentence
                for (int j = 0; j < targetSentence.positions.size(); j++) {
                    // Translation units with aligned source words
                    for (TranslationUnit tu : multiAlignment.translationUnits) {
                        if (!tu.targetPositions.isEmpty()
                                && tu.sourcePositions.isEmpty()
                                && tu.targetPositions.get(0) == j
                                && ListUtil.isContiguous(tu.targetPositions)
                                && ListUtil.isContiguous(tu.sourcePositions)) {
                            // Extract target multiword
                            int length = tu.targetPositions.size();
                            int form = SymbolPool.join(targetSentence.formsAt(tu.targetPositions), " ");
                            int pos = SymbolPool.join(targetSentence.posAt(tu.targetPositions), " ");

                            // Parse the source multiword
                            Sentence multiword = new Sentence(sourceSentence.positionsAt(
                                    tu.sourcePositions));
                            ForceAgenda parseResult = ForceAgenda.findAllParses(
                                    multiword, sourceGrammar, oracle);

                            // Add lexical items
                            for (Item item : parseResult.getItems()) {
                                Node node = item.stack.getFirst();
                                targetSentence.positions.get(j).lexicalItems.add(
                                        new LexicalItem(length, form, pos, straightenSlashes(node.category),
                                                node.interpretation));
                            }
                        }
                    }

                    // Translation without aligned source words
                    for (TranslationUnit tu : multiAlignment.translationUnits) {
                        if (!tu.targetPositions.isEmpty()
                                && tu.sourcePositions.isEmpty()
                                && tu.targetPositions.get(0) == j) {
                            // Extract target multiword
                            int length = tu.targetPositions.size();
                            int form = SymbolPool.join(targetSentence.formsAt(tu.targetPositions), " ");
                            int pos = SymbolPool.join(targetSentence.posAt(tu.targetPositions), " ");

                            // Add SKIP item
                            targetSentence.positions.get(j).lexicalItems.add(
                                    new LexicalItem(length, form, pos,
                                            SymbolPool.getID("SKIP"),
                                            Interpretation.DUMMY));
                        }
                    }
                }
            }

            Sentence.writeSentences(targetSentences, new File(args[6]));
        } catch (IOException ex) {
            System.err.println("ERROR: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }

    private static int straightenSlashes(int category) {
        return SymbolPool.getID(SymbolPool.getString(category).replace("\\", "/"));
    }

}
