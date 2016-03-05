package epar.parser;

import epar.parser.action.Action;
import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.oracle.Oracle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An agenda represents one generation of parse items. This implementation
 * drops all items not accepted by some given oracle.
 * @author p264360
 */
public class OracleAgenda {
    
    public final int generation;
    
    private final List<Item> items;

    private OracleAgenda(int generation, List<Item> items) {
        this.generation = generation;
        this.items = items;
    }

    public static OracleAgenda initial(Sentence sentence) {
        return new OracleAgenda(0, Collections.singletonList(
                Item.initial(sentence)));
    }
    
    public OracleAgenda nextAgenda(Grammar grammar, Oracle oracle) {
        List<Item> successors = new ArrayList<>();
        
        for (Item item : items) {
            for (Item successor : item.successors(grammar)) {
                if (oracle.accept(generation, successor)) {
                    successors.add(successor);
                }
            }
        }
        
        // TODO spaceout
        
        return new OracleAgenda(generation + 1, successors);
    }
    
    public boolean allFinished() {
        for (Item item : items) {
            if (!item.finished) {
                return false;
            }
        }
        
        return true;
    }
    
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }
    
    public static List<List<Item>> computeGoldItemSequences(Sentence sentence,
            Grammar grammar, Oracle oracle) {
        OracleAgenda agenda = OracleAgenda.initial(sentence);
        
        while (!agenda.allFinished()) {
            agenda = agenda.nextAgenda(grammar, oracle);
        }
        
        List<List<Item>> sequences = new ArrayList<>(agenda.items.size());
        
        // For each finished item on the final agenda, extract the sequence that
        // lead up to it.
        for (Item item : agenda.items) {
            List<Item> ecneuqes = new ArrayList<>();
            
            // Follow the backpointers and put items in list
            while (item != null) {
                if (!(item.action.getType() == Action.TYPE_IDLE)) {
                    ecneuqes.add(item);
                }
                
                item = item.predecessor;
            }
            
            List<Item> sequence = new ArrayList<>();
            sequences.add(sequence);
            
            // Reverse list
            for (int i = ecneuqes.size() - 1; i >= 0; i--) {
                sequence.add(ecneuqes.get(i));
            }
        }
        
        return sequences;
    }
    
}
