package epar;

import epar.data.LexicalItem;
import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.oracle.NoFragmentsOracle;
import epar.oracle.Oracle;
import epar.projection.Alignment;
import epar.projection.TranslationUnit;
import epar.util.ListUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

/**
 * Takes source and target sentence pairs, together with word alignments, as
 * input. Produces a lexicon for the target language, based on categories
 * projected via the alignments.
 *
 * @author p264360
 */
public class ProjectLexicon {

    public static void main(String[] args) {
        if (args.length != 7) {
            System.err.println(
                    "USAGE: java epar.ProjectCategories SRCTRG.ALIGN TRGSRC.ALIGN NBEST SENTENCES.SRC GRAMMAR.SRC SENTENCES.TRG LEXICON.TRG");
            // where SENTENCES.SRC contains categories and interpretations, SENTENCES.TRG does not
            System.exit(1);
        }

        try {
            List<List<Alignment>> sourceTargetAlignments = Alignment.read(new File(args[0]));
            List<List<Alignment>> targetSourceAlignments = Alignment.read(new File(args[1]));
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

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(args[6])), "utf-8"))) {
                // For every sentence pair
                for (int i = 0; i < sourceSentences.size(); i++) {
                    Sentence sourceSentence = sourceSentences.get(i);
                    Sentence targetSentence = targetSentences.get(i);
                    List<Alignment> sentenceSourceTargetAlignments = sourceTargetAlignments.get(i);
                    List<Alignment> sentenceTargetSourceAlignments = targetSourceAlignments.get(i);
                    sentenceSourceTargetAlignments = sentenceSourceTargetAlignments.subList(0, Math.min(sentenceSourceTargetAlignments.size(), nBest));
                    sentenceTargetSourceAlignments = sentenceTargetSourceAlignments.subList(0, Math.min(sentenceTargetSourceAlignments.size(), nBest));

                    for (Alignment alignment : sentenceSourceTargetAlignments) {
                        for (TranslationUnit tu : alignment.translationUnits) {
                            LexicalItem item = tu.project(sourceSentence, targetSentence, sourceGrammar);

                            if (item != null) {
                                item.write(writer);
                            }
                        }
                    }

                    for (Alignment alignment : sentenceTargetSourceAlignments) {
                        alignment = alignment.invert();

                        for (TranslationUnit tu : alignment.translationUnits) {
                            LexicalItem item = tu.project(sourceSentence, targetSentence, sourceGrammar);

                            if (item != null) {
                                item.write(writer);
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println("ERROR: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }

}
