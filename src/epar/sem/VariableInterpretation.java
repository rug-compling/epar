package epar.sem;

/**
 *
 * @author p264360
 */
public class VariableInterpretation implements Interpretation {

    @Override
    public boolean canLeadTo(Interpretation targetInterpretation) {
        return true;
    }

    @Override
    public Interpretation substitute(VariableInterpretation variable,
            Interpretation term) {
        if (variable == this ) {
            return term;
        } else {
            return this;
        }
    }

    @Override
    public boolean containsTermSubsumedBy(Interpretation subsumer) {
        return subsumer instanceof VariableInterpretation;
    }

    @Override
    public boolean subsumes(Interpretation subsumee) {
        return true;
    }
    
}
