package epar.projection;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author p264360
 */
public class TranslationUnit {
    
    public final List<Integer> sourcePositions;
    
    public final List<Integer> targetPositions;
    
    public TranslationUnit(List<Integer> sourcePositions,
            List<Integer> targetPositions) {
        this.sourcePositions = Collections.unmodifiableList(sourcePositions);
        this.targetPositions = Collections.unmodifiableList(targetPositions);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.sourcePositions);
        hash = 79 * hash + Objects.hashCode(this.targetPositions);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TranslationUnit other = (TranslationUnit) obj;
        if (!Objects.equals(this.sourcePositions, other.sourcePositions)) {
            return false;
        }
        if (!Objects.equals(this.targetPositions, other.targetPositions)) {
            return false;
        }
        return true;
    }
    
}
