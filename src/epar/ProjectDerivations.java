package epar;

import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.oracle.SemanticOracle;
import epar.parser.ForceAgenda;
import epar.parser.action.Action;
import epar.sem.Interpretation;
import epar.util.StringUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author p264360
 */
public class ProjectDerivations {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java epar.ProjectDerivations INPUT.TRG GRAMMAR.TRG TARGET_INTERPRETATIONS");
            System.exit(1);
        }

        try {
            List<Sentence> sentences = Sentence.readSentences(new File(args[0]));
            Grammar grammar = Grammar.load(new File(args[1]));
            List<Interpretation> targetInterpretations = Interpretation.read(new File(args[2]));
            
            if (sentences.size() != targetInterpretations.size()) {
                throw new IllegalArgumentException("Numbers of sentences and target interpretations don't match.");
            }

            for (int i = 0; i < sentences.size(); i++) {
                List<List<Action>> actionSequences = ForceAgenda.forceDecode(
                        sentences.get(i), grammar, new SemanticOracle(
                                targetInterpretations.get(i)), 256);
                List<String> actionSequenceStrings = new ArrayList<>(
                        actionSequences.size());
                
                for (List<Action> actionSequence : actionSequences) {
                    actionSequenceStrings.add(Action.sequenceToString(
                            actionSequence));
                }
                
                System.out.println(StringUtil.join(actionSequenceStrings, " || "));
            }
        } catch (FileNotFoundException ex) {
            System.err.println("ERROR: " + ex.getLocalizedMessage());
            System.exit(1);
        }
    }

}
