package epar.sem;

/**
 *
 * @author p264360
 */
public interface Interpretation {
    
    public boolean canLeadTo(Interpretation targetInterpretation);
    
    public boolean containsTermSubsumedBy(Interpretation subsumer);

    public boolean subsumes(Interpretation subsumee);
    
    public Interpretation substitute(VariableInterpretation variable,
            Interpretation term);
    
}
