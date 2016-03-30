package epar.sem;

import java.util.Map;

/**
 *
 * @author p264360
 */
public class LambdaAbstractionInterpretation extends Interpretation {
    
    public final VariableInterpretation variable;
    
    public final Interpretation body;
    
    public LambdaAbstractionInterpretation(VariableInterpretation variable,
            Interpretation body) {
        this.variable = variable;
        this.body = body;
    }
    
    @Override
    public Interpretation substitute(VariableInterpretation variable,
            Interpretation term) {
        return new LambdaAbstractionInterpretation(this.variable,
                body.substitute(variable, term));
    }

    @Override
    public boolean canLeadTo(Interpretation targetInterpretation) {
        return body.canLeadTo(targetInterpretation);
    }
    
    // TODO In an ideal world, target interpretations would contain no
    // lambdas... the below, for now, assumes, we live in an ideal world.

    @Override
    public boolean containsTermSubsumedBy(Interpretation subsumer) {
        // This implementation is loaded with assumptions about the set of
        // possible derivations, see package documentation.
        return subsumer.subsumes(this) || body.containsTermSubsumedBy(subsumer);
    }

    @Override
    public boolean subsumes(Interpretation subsumee) {
        // This implementation is loaded with assumptions about the set of
        // possible derivations, see package documentation.
        
        if (!(subsumee instanceof LambdaAbstractionInterpretation)) {
            return false;
        }
        
        LambdaAbstractionInterpretation subsumedAbstraction =
                (LambdaAbstractionInterpretation) subsumee;
        Interpretation converted = new LambdaAbstractionInterpretation(
                subsumedAbstraction.variable, body.substitute(variable,
                        subsumedAbstraction.variable));
        return converted.subsumes(subsumee);
    }

    @Override
    public Interpretation applyTo(Interpretation argument) {
        return body.substitute(variable, argument);
    }

    @Override
    String toProlog(Map<VariableInterpretation, String> variableNames) {
        return "lam(" + variable.toProlog(variableNames) + "," +
                body.toProlog(variableNames) + ")";
    }
    
}
