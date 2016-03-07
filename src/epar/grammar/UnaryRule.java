package epar.grammar;

import epar.sem.ApplicationInterpretation;
import epar.sem.AtomicInterpretation;
import epar.sem.Interpretation;
import epar.sem.LambdaAbstractionInterpretation;
import epar.sem.VariableInterpretation;
import epar.util.SymbolPool;

public class UnaryRule {

    private static final int FTR = SymbolPool.getID("ftr");

    private static final int BTR = SymbolPool.getID("btr");

    private static final int TC = SymbolPool.getID("tc");

    private static final int DUMMY = SymbolPool.getID("dummy");

    public final int childCategory;

    public final int parentCategory;

    public final int schemaName;

    private AtomicInterpretation typeChangingInterpretation;

    public UnaryRule(int childCategory, int parentCategory,
            int schemaName) {
        this.childCategory = childCategory;
        this.parentCategory = parentCategory;
        this.schemaName = schemaName;

        if (schemaName == TC) {
            this.typeChangingInterpretation = new AtomicInterpretation(
                    SymbolPool.getID("tc-"
                            + SymbolPool.getString(childCategory) + "-"
                            + SymbolPool.getString(parentCategory)));
        }
    }

    public Interpretation interpret(Interpretation childInterpretation) {
        if (schemaName == DUMMY) {
            return Interpretation.DUMMY;
        }
        
        if (schemaName == FTR || schemaName == BTR) {
            VariableInterpretation variable = new VariableInterpretation();
            return new LambdaAbstractionInterpretation(variable,
                    variable.applyTo(childInterpretation));
        }

        if (schemaName == TC) {
            return typeChangingInterpretation.applyTo(childInterpretation);
        }

        throw new IllegalArgumentException(
                "Don't know how to interpret unary rule: "
                + SymbolPool.getString(schemaName));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + this.childCategory;
        hash = 11 * hash + this.parentCategory;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UnaryRule other = (UnaryRule) obj;
        if (this.childCategory != other.childCategory) {
            return false;
        }
        if (this.parentCategory != other.parentCategory) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return SymbolPool.getString(childCategory) + "\t"
                + SymbolPool.getString(parentCategory) + "\t" + schemaName;
    }

}
