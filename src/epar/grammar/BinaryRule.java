package epar.grammar;

import epar.sem.Interpretation;
import epar.sem.LambdaAbstractionInterpretation;
import epar.sem.VariableInterpretation;
import epar.util.SymbolPool;
import java.util.Objects;

public class BinaryRule {

    private static final int FA = SymbolPool.getID("fa");

    private static final int BA = SymbolPool.getID("ba");

    private static final int FC = SymbolPool.getID("fc");

    private static final int BC = SymbolPool.getID("bc");

    private static final int FXC = SymbolPool.getID("fxc");

    private static final int BXC = SymbolPool.getID("bxc");

    private static final int GFC2 = SymbolPool.getID("gfc(2)");

    private static final int GBC2 = SymbolPool.getID("gbc(2)");

    private static final int GFXC2 = SymbolPool.getID("gfxc(2)");

    private static final int GBXC2 = SymbolPool.getID("gbxc(2)");

    private static final int GFC3 = SymbolPool.getID("gfc(3)");

    private static final int GBC3 = SymbolPool.getID("gbc(3)");

    private static final int GFXC3 = SymbolPool.getID("gfxc(3)");

    private static final int GBXC3 = SymbolPool.getID("gbxc(3)");

    private static final int GFC4 = SymbolPool.getID("gfc(4)");

    private static final int GBC4 = SymbolPool.getID("gbc(4)");

    private static final int GFXC4 = SymbolPool.getID("gfxc(4)");

    private static final int GBXC4 = SymbolPool.getID("gbxc(4)");

    private static final int DUMMY = SymbolPool.getID("dummy");
    
    private static final int CONJ = SymbolPool.getID("conj");

    public static enum HeadPosition {

        LEFT, RIGHT;

        /**
         * String representation as used in ZPar trees
         *
         * @return @code{@code "l"} or {@code "r"}
         */
        @Override
        public String toString() {
            if (this == LEFT) {
                return "l";
            } else {
                return "r";
            }
        }

        public HeadPosition flip() {
            if (this == LEFT) {
                return RIGHT;
            } else {
                return LEFT;
            }
        }

        /**
         * String representation as used in ZPar grammars
         *
         * @return @code{@code "LEFT"} or {@code "RIGHT"}
         */
        public String toActionString() {
            if (this == LEFT) {
                return "LEFT";
            } else {
                return "RIGHT";
            }
        }

        public static HeadPosition fromActionString(String actionString) {
            if ("LEFT".equals(actionString)) {
                return LEFT;
            } else if ("RIGHT".equals(actionString)) {
                return RIGHT;
            } else {
                throw new IllegalArgumentException("Invalid head position: "
                        + actionString);
            }
        }
    }

    public final int leftChildCategory;

    public final int rightChildCategory;

    public final int parentCategory;

    public final BinaryRule.HeadPosition headPosition;

    public final int schemaName;

    public BinaryRule(int leftChildCategory, int rightChildCategory,
            int parentCategory, BinaryRule.HeadPosition headPosition,
            int schemaName) {
        this.leftChildCategory = leftChildCategory;
        this.rightChildCategory = rightChildCategory;
        this.parentCategory = parentCategory;
        this.headPosition = headPosition;
        this.schemaName = schemaName;
    }

    public BinaryRule flip() {
        return new BinaryRule(rightChildCategory, leftChildCategory,
                parentCategory, headPosition.flip(), schemaName);
    }

    public Interpretation interpret(Interpretation leftChildInterpretation,
            Interpretation rightChildInterpretation) {
        if (schemaName == DUMMY) {
            return Interpretation.IDENTITY;
        }

        if (schemaName == FA) {
            return compose(0, leftChildInterpretation,
                    rightChildInterpretation);
        }

        if (schemaName == BA) {
            return compose(0, rightChildInterpretation,
                    leftChildInterpretation);
        }
        
        if (schemaName == FC || schemaName == FXC) {
            return compose(1, leftChildInterpretation,
                    rightChildInterpretation);
        }
        
        if (schemaName == BC || schemaName == BXC) {
            return compose(1, rightChildInterpretation,
                    leftChildInterpretation);
        }
        
        if (schemaName == GFC2 || schemaName == GFXC2) {
            return compose(2, leftChildInterpretation,
                    rightChildInterpretation);
        }
        
        if (schemaName == GBC2 || schemaName == GBXC2) {
            return compose(2, rightChildInterpretation,
                    leftChildInterpretation);
        }
        
        if (schemaName == GFC3 || schemaName == GFXC3) {
            return compose(3, leftChildInterpretation,
                    rightChildInterpretation);
        }
        
        if (schemaName == GBC3 || schemaName == GBXC3) {
            return compose(3, rightChildInterpretation,
                    leftChildInterpretation);
        }
        
        if (schemaName == GFC4 || schemaName == GFXC4) {
            return compose(4, leftChildInterpretation,
                    rightChildInterpretation);
        }
        
        if (schemaName == GBC4 || schemaName == GBXC4) {
            return compose(4, rightChildInterpretation,
                    leftChildInterpretation);
        }
        
        if (schemaName == CONJ) {
            return leftChildInterpretation.applyTo(rightChildInterpretation);
            // TODO This interpretation is always appropriate in the C&C/Boxer
            // grammar, it seems. If we ever encountered "coordination à la
            // Hockenmaier" or some exotic flavor of coordination, we would
            // interpret it wrongly and be doooooooooooooooooooooooooooooooomed.
        }

        throw new IllegalArgumentException(
                "Don't know how to interpret binary rule: "
                + SymbolPool.getString(schemaName));
    }

    private static Interpretation compose(int degree, Interpretation functor,
            Interpretation argument) {
        VariableInterpretation[] variables = new VariableInterpretation[degree];

        // Create variables for lambda abstractions
        for (int i = 0; i < degree; i++) {
            variables[i] = new VariableInterpretation();
        }

        // Create delayed applications
        for (int i = 0; i < degree; i++) {
            argument = argument.applyTo(variables[i]);
        }

        Interpretation result = functor.applyTo(argument);

        // Create lambda abstractions
        for (int i = degree - 1; i >= 0; i--) {
            result = new LambdaAbstractionInterpretation(variables[i], result);
        }

        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.leftChildCategory;
        hash = 29 * hash + this.rightChildCategory;
        hash = 29 * hash + this.parentCategory;
        hash = 29 * hash + Objects.hashCode(this.headPosition);
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
        final BinaryRule other = (BinaryRule) obj;
        if (this.leftChildCategory != other.leftChildCategory) {
            return false;
        }
        if (this.rightChildCategory != other.rightChildCategory) {
            return false;
        }
        if (this.parentCategory != other.parentCategory) {
            return false;
        }
        if (this.headPosition != other.headPosition) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return SymbolPool.getString(leftChildCategory) + "\t"
                + SymbolPool.getString(rightChildCategory) + "\t"
                + SymbolPool.getString(parentCategory) + "\t"
                + headPosition.toActionString() + "\t" + schemaName;
    }

}
