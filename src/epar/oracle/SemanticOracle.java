package epar.oracle;

import epar.grammar.Grammar;
import epar.parser.Item;
import epar.sem.Interpretation;

/**
 *
 * @author p264360
 */
public class SemanticOracle implements Oracle {

    private final Interpretation targetInterpretation;

    public SemanticOracle(Interpretation targetInterpretation) {
        this.targetInterpretation = targetInterpretation;
    }

    @Override
    public boolean accept(int generation, Item item) {
        // Only need to check the semantics of the topmost stack node.
        // Others are guaranteed to already have been checked in previous
        // generations.
        
        if (item.stack.getFirst().category == Grammar.SKIP_CATEGORY) {
            return true;
        }
        
        Interpretation interpretation = item.stack.getFirst().interpretation;
        
        if (item.finished) {
            return item.stack.size() == 1 &&
                    targetInterpretation.subsumes(interpretation) &&
                    interpretation.subsumes(targetInterpretation);
        } else {
            return interpretation.canLeadTo(targetInterpretation);
        }
    }

}
