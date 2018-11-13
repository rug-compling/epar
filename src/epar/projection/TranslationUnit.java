package epar.projection;

import epar.data.LexicalItem;
import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.node.Node;
import epar.oracle.NoFragmentsOracle;
import epar.oracle.Oracle;
import epar.parser.ForceAgenda;
import epar.parser.Item;
import epar.sem.Interpretation;
import epar.util.ListUtil;
import epar.util.SymbolPool;
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

    public LexicalItem project(Sentence sourceSentence,
            Sentence targetSentence, Grammar sourceGrammar) {
        Oracle oracle = new NoFragmentsOracle();

        if (!targetPositions.isEmpty()
                && !sourcePositions.isEmpty()
                && ListUtil.isContiguous(targetPositions)
                && ListUtil.isContiguous(sourcePositions)) {
            // Extract target multiword
            int length = targetPositions.size();
            int form = SymbolPool.join(targetSentence.formsAt(targetPositions), " ");
            int pos = SymbolPool.join(targetSentence.posAt(targetPositions), " ");

            // Parse the source multiword
            Sentence multiword = new Sentence(sourceSentence.positionsAt(
                    sourcePositions));
            ForceAgenda parseResult = ForceAgenda.findAllParses(
                    multiword, sourceGrammar, oracle);

            // Return the first lexical item
            for (Item item : parseResult.getItems()) {
                Node node = item.stack.getFirst();
                return new LexicalItem(length, form, pos, straightenSlashes(node.category),
                        node.interpretation);
            }
        }
        
        if (!targetPositions.isEmpty() && sourcePositions.isEmpty()) {
            // Extract target multiword
            int length = targetPositions.size();
            int form = SymbolPool.join(targetSentence.formsAt(targetPositions), " ");
            int pos = SymbolPool.join(targetSentence.posAt(targetPositions), " ");

            // Return SKIP item
            return new LexicalItem(length, form, pos, SymbolPool.getID("SKIP"),
                            Interpretation.IDENTITY);
        }
        
        return null;
    }

    private static int straightenSlashes(int category) {
        return SymbolPool.getID(SymbolPool.getString(category).replace("\\", "/"));
    }
    
    @Override
    public String toString() {
        return "<" + sourcePositions.toString() + ", " +
                targetPositions.toString() + ">";
    }

}
