package epar.projection;

import epar.util.RecUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;

/**
 * This class represents the output of a bidirectional word alignment process
 * for a sentence pair, with an arbitrary number of n-best alignments.
 *
 * @author p264360
 */
public class MultiAlignment {

    private final LinkedHashSet<TranslationUnit> translationUnits;

    public MultiAlignment(LinkedHashSet<TranslationUnit> translationUnits) {
        this.translationUnits = translationUnits;
    }

    /**
     * Reads the multi alignment from two GIZA++ n-best alignment output files,
     * one for each alignment direction.
     *
     * @param sourceTargetFile
     * @param targetSourceFile
     * @param nBestInput How many n-best alignments there are for each sentence
     * pair in the input
     * @param nBestOutput How many n-best alignments should be used
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static MultiAlignment read(File sourceTargetFile,
            File targetSourceFile, int nBestInput, int nBestOutput)
            throws FileNotFoundException {
        LinkedHashSet<TranslationUnit> translationUnits = new LinkedHashSet<>();

        try (Scanner sourceTargetScanner = new Scanner(sourceTargetFile);
                Scanner targetSourceScanner = new Scanner(targetSourceFile)) {
            for (int i = 0; i < nBestOutput; i++) {
                read(sourceTargetScanner, translationUnits, false);
                read(targetSourceScanner, translationUnits, true);
            }

            for (int i = nBestOutput; i < nBestInput; i++) {
                // Skip further alignments
                sourceTargetScanner.nextLine();
                sourceTargetScanner.nextLine();
                sourceTargetScanner.nextLine();
                targetSourceScanner.nextLine();
                targetSourceScanner.nextLine();
                targetSourceScanner.nextLine();
            }
        }
        
        return new MultiAlignment(translationUnits);
    }

    private static void read(Scanner sourceTargetScanner,
            LinkedHashSet<TranslationUnit> translationUnits, boolean invert) {
        sourceTargetScanner.nextLine(); // skip comment
        sourceTargetScanner.nextLine(); // skip target-language sentence
        Scanner lineScanner = new Scanner(sourceTargetScanner.nextLine());
        RecUtil.expect("NULL", lineScanner);
        RecUtil.expect("({", lineScanner);

        // Add "unaligned" translation units
        while (lineScanner.hasNextInt()) {
            if (invert) {
                translationUnits.add(new TranslationUnit(
                        Collections.singletonList(lineScanner.nextInt() - 1),
                        Collections.EMPTY_LIST));

            } else {
                translationUnits.add(new TranslationUnit(Collections.EMPTY_LIST,
                        Collections.singletonList(lineScanner.nextInt() - 1)));
            }
        }

        RecUtil.expect("})", lineScanner);
        int sourcePosition = 0;

        while (lineScanner.hasNext()) {
            lineScanner.next(); // skip source-language token
            RecUtil.expect("({", lineScanner);
            List<Integer> sourcePositions
                    = Collections.singletonList(sourcePosition);
            List<Integer> targetPositions = new ArrayList<>();

            while (lineScanner.hasNextInt()) {
                targetPositions.add(lineScanner.nextInt() - 1);
            }

            RecUtil.expect("})", lineScanner);

            if (invert) {
                translationUnits.add(new TranslationUnit(targetPositions,
                        sourcePositions));
            } else {
                translationUnits.add(new TranslationUnit(sourcePositions,
                        targetPositions));
            }

            sourcePosition++;
        }
    }

}
