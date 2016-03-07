package epar.sem;

/**
 *
 * @author p264360
 */
public class AtomicInterpretation extends Interpretation {
    
    public final int symbol;
    
    public AtomicInterpretation(int symbol) {
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
    
}
