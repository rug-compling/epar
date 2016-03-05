package epar.sem;

/**
 *
 * @author p264360
 */
public class ApplicationInterpretation implements Interpretation {

    public final Interpretation functor;

    public final Interpretation argument;

    public ApplicationInterpretation(Interpretation functor,
            Interpretation argument) {
        this.functor = functor;
        this.argument = argument;
    }
    
    @Override
    public Interpretation substitute(VariableInterpretation variable,
            Interpretation term) {
        return new ApplicationInterpretation(functor.substitute(variable, term),
                argument.substitute(variable, term));
    }

    @Override
    public boolean canLeadTo(Interpretation targetInterpretation) {
        return targetInterpretation.containsTermSubsumedBy(this);
    }

    @Override
    public boolean containsTermSubsumedBy(Interpretation subsumer) {
        return subsumer.subsumes(this) ||
                functor.containsTermSubsumedBy(subsumer) ||
                argument.containsTermSubsumedBy(subsumer);
    }

    @Override
    public boolean subsumes(Interpretation subsumee) {
        if (!(subsumee instanceof ApplicationInterpretation)) {
            return false;
        }
        
        ApplicationInterpretation subsumedApplication =
                (ApplicationInterpretation) subsumee;
        
        return functor.subsumes(subsumedApplication.functor) &&
                argument.subsumes(subsumedApplication.argument);
    }

}
