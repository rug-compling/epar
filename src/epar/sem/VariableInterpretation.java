package epar.sem;

import java.util.Map;

/**
 *
 * @author p264360
 */
public class VariableInterpretation extends Interpretation {

    @Override
    public boolean canLeadTo(Interpretation targetInterpretation) {
        return true;
    }

    @Override
    public Interpretation substitute(VariableInterpretation variable,
            Interpretation term) {
        if (variable == this) {
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

    @Override
    public Interpretation applyTo(Interpretation argument) {
        return new ApplicationInterpretation(this, argument);
    }

    @Override
    String toProlog(Map<VariableInterpretation, String> variableNames) {
        // Names variables from A to Z, we shouldn't be needing more
        
        if (!variableNames.containsKey(this)) {
            if (variableNames.size() >= 26) {
                throw new IllegalArgumentException("Only 26 variables supported");
            }

            variableNames.put(this, String.valueOf(
                    (char) (65 + variableNames.size()) + ""));
        }

        return variableNames.get(this);
    }

}
