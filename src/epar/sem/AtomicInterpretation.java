package epar.sem;

import epar.util.SymbolPool;
import java.util.Map;

/**
 *
 * @author p264360
 */
public class AtomicInterpretation extends Interpretation {
    
    public final int symbol;
    
    public AtomicInterpretation(int symbol) {
        // TODO could make this constructor private and manage a pool of
        // atomic interpretations to avoid having many semantically equivalent
        // objects.
        this.symbol = symbol;
    }

    @Override
    public Interpretation substitute(VariableInterpretation variable,
            Interpretation term) {
        return this;
    }

    @Override
    public boolean canLeadTo(Interpretation targetInterpretation) {
        return targetInterpretation.containsTermSubsumedBy(this);
    }

    @Override
    public boolean containsTermSubsumedBy(Interpretation subsumer) {
        return subsumer.subsumes(this);
    }

    @Override
    public boolean subsumes(Interpretation subsumee) {
        return subsumee instanceof AtomicInterpretation &&
                ((AtomicInterpretation) subsumee).symbol == symbol;
    }

    @Override
    public Interpretation applyTo(Interpretation argument) {
        return new ApplicationInterpretation(this, argument);
    }

    @Override
    String toProlog(Map<VariableInterpretation, String> variableNames) {
        return SymbolPool.getString(symbol);
    }
    
}
