package epar.grammar;

import epar.util.SymbolPool;
import java.util.Objects;

public class BinaryRule {

    public static enum HeadPosition {

        LEFT, RIGHT;

        /**
         * String representation as used in ZPar trees
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
        return SymbolPool.getString(leftChildCategory) + "\t" +
                SymbolPool.getString(rightChildCategory) + "\t" +
                SymbolPool.getString(parentCategory) + "\t"+ 
                headPosition.toActionString() + "\t" + schemaName;
    }

}
