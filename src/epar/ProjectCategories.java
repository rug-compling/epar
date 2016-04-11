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
import java.util.Arrays;
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
        if (args.length != 7) {
            System.err.println(
                    "USAGE: java epar.ProjectCategories SRCTRG.ALIGN TRGSRC.ALIGN NBEST SENTENCES.SRC GRAMMAR.SRC SENTENCES.TRG LOCLEXINPUT.TRG");
            // where SENTENCES.SRC contains categories and interpretations, SENTENCES.TRG does not
            System.exit(1);
        }

        try {
            List<List<MultiAlignment>> sourceTargetAlignments = MultiAlignment.read(new File(args[0]));
            List<List<MultiAlignment>> targetSourceAlignments = MultiAlignment.read(new File(args[1]));
            int nBest = Integer.parseInt(args[2]);

            List<Sentence> sourceSentences = Sentence.readSentences(new File(args[3]));

            if (sourceTargetAlignments.size() != sourceSentences.size()) {
                throw new IllegalArgumentException("Numbers of source-target alignments and source sentences don't match.");
            }

            if (targetSourceAlignments.size() != sourceSentences.size()) {
                throw new IllegalArgumentException("Numbers of target-source alignments and source sentences don't match.");
            }

            Grammar sourceGrammar = Grammar.load(new File(args[4]));

            List<Sentence> targetSentences = Sentence.readSentences(new File(args[5]));

            if (sourceSentences.size() != targetSentences.size()) {
                throw new IllegalArgumentException("Numbers of source and target sentences don't match.");
            }

            Oracle oracle = new NoFragmentsOracle();

            // For every sentence pair
            for (int i = 0; i < sourceSentences.size(); i++) {
                Sentence sourceSentence = sourceSentences.get(i);
                Sentence targetSentence = targetSentences.get(i);
                List<MultiAlignment> sourceTargetAlignment = sourceTargetAlignments.get(i);
                List<MultiAlignment> targetSourceAlignment = targetSourceAlignments.get(i);
                
                // Aggregate the translation units we want to use:
                MultiAlignment sourceTargetMultiAlignment = MultiAlignment.union(sourceTargetAlignment.subList(0, Math.min(sourceTargetAlignment.size(), nBest)));
                MultiAlignment targetSourceMultiAlignment = MultiAlignment.union(targetSourceAlignment.subList(0, Math.min(targetSourceAlignment.size(), nBest))).invert();
                MultiAlignment multiAlignment = MultiAlignment.union(Arrays.asList(sourceTargetMultiAlignment, targetSourceMultiAlignment));
                
                // For every position in the target sentence
                for (int j = 0; j < targetSentence.positions.size(); j++) {
                    // Translation units with aligned source words
                    for (TranslationUnit tu : multiAlignment.translationUnits) {
                        if (!tu.targetPositions.isEmpty()
                                && !tu.sourcePositions.isEmpty()
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
                                break; // only add the first
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
