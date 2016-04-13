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
public class Alignment {

    public final LinkedHashSet<TranslationUnit> translationUnits;
    
    public Alignment() {
        this(new LinkedHashSet<TranslationUnit>());
    }

    public Alignment(LinkedHashSet<TranslationUnit> translationUnits) {
        this.translationUnits = translationUnits;
    }
    
    public static Alignment union(Iterable<Alignment> alignments) {
        LinkedHashSet<TranslationUnit> translationUnits = new LinkedHashSet<>();
        
        for (Alignment alignment : alignments) {
            translationUnits.addAll(alignment.translationUnits);
        }
        
        return new Alignment(translationUnits);
    }
    
    public Alignment invert() {
        LinkedHashSet<TranslationUnit> invertedTranslationUnits = new LinkedHashSet<>();
        
        for (TranslationUnit tu : translationUnits) {
            invertedTranslationUnits.add(new TranslationUnit(tu.targetPositions, tu.sourcePositions));
        }
        
        return new Alignment(invertedTranslationUnits);
    }
    
    /**
     * Reads a GIZA++ n-best alignment output file.
     * @param file
     * @return A list with one element for each sentence pair, each element a
     * list of alignments.
     * @throws java.io.FileNotFoundException
     */
    public static List<List<Alignment>> read(File file) throws FileNotFoundException {
        List<List<Alignment>> result = new ArrayList<>();
        
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String commentLine = scanner.nextLine();
                assert commentLine.startsWith("# Sentence pair (");
                int pairNumber = Integer.parseInt(commentLine.substring(17, commentLine.indexOf(")")));
                
                if (pairNumber == result.size()) {
                    // OK
                } else if (pairNumber == result.size() + 1) {
                    result.add(new ArrayList<Alignment>());
                } else {
                    throw new IllegalArgumentException("Invalid sentence pair number sequence: " + pairNumber + " follows " + result.size());
                }
                
                scanner.nextLine(); // skip source language sentence
                Scanner alignmentScanner = new Scanner(scanner.nextLine());
                LinkedHashSet<TranslationUnit> translationUnits = new LinkedHashSet<>();
                RecUtil.expect("NULL", alignmentScanner);
                RecUtil.expect("({", alignmentScanner);
                
                // Add "translation units" for unaligned target-language tokens
                while (alignmentScanner.hasNextInt()) {
                    translationUnits.add(new TranslationUnit(Collections.EMPTY_LIST, Collections.singletonList(alignmentScanner.nextInt() - 1)));
                }
                
                RecUtil.expect("})", alignmentScanner);
                int sourcePosition = 0;
                
                // Add regular translation units
                while (alignmentScanner.hasNext()) {
                    alignmentScanner.next(); // skip source-language token
                    RecUtil.expect("({", alignmentScanner);
                    List<Integer> targetPositions = new ArrayList<>();
                    
                    while (alignmentScanner.hasNextInt()) {
                        targetPositions.add(alignmentScanner.nextInt() - 1);
                    }
                    
                    translationUnits.add(new TranslationUnit(Collections.singletonList(sourcePosition), targetPositions));
                    RecUtil.expect("})", alignmentScanner);
                    sourcePosition++;
                }
                
                result.get(pairNumber - 1).add(new Alignment(translationUnits));
            }
        }
        
        return result;
    }

}
