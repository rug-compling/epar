package epar.parser;

import epar.data.Sentence;
import epar.grammar.Grammar;
import epar.oracle.Oracle;
import epar.parser.action.Action;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author p264360
 */
public class ForceAgenda {

    private final static Logger LOGGER = Logger.getLogger(
            ForceAgenda.class.getName());
    
    public final int generation;
    
    private final List<Item> items;
    
    private ForceAgenda(int generation, List<Item> items) {
        this.generation = generation;
        this.items = items;
    }
    
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean allFinished() {
        for (Item item : items) {
            if (!item.finished) {
                return false;
            }
        }

        return true;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public ForceAgenda nextAgenda(Grammar grammar, Oracle oracle) {
        List<Item> successors = new ArrayList<>();
        
        for (Item item : items) {
            for (Item successor  : item.successors(grammar)) {
                if (oracle.accept(generation, item)) {
                    successors.add(successor);
                }
            }
        }
        
        return new ForceAgenda(generation + 1, successors);
    }
    
    public static ForceAgenda initial(Sentence sentence) {
        return new ForceAgenda(0,
                Collections.singletonList(Item.initial(sentence)));
    }
    
    /**
     * 
     * @param sentence
     * @param grammar
     * @param oracle
     * @param spaceout
     * @return All action sequences leading to a finished item for this sentence
     * such that all items are accepted by the oracle - except if some agenda
     * on the way is larger than spaceout, in this case returns an empty list.
     */
    public static List<List<Action>> forceDecode(Sentence sentence, Grammar
            grammar, Oracle oracle, int spaceout) {
        ForceAgenda agenda = ForceAgenda.initial(sentence);
        
        while (!agenda.isEmpty()) {
            if (agenda.getItems().size() > spaceout) {
                LOGGER.log(Level.WARNING,
                        "Forced decoding spaced out in generation {0}",
                        agenda.generation);
                return Collections.EMPTY_LIST;
            }
            
            if (agenda.allFinished()) {
                List<List<Action>> result = new ArrayList<>(
                        agenda.getItems().size());
                
                for (Item item : agenda.getItems()) {
                    result.add(item.actionSequence());
                }
                
                return result;
            }
            
            agenda = agenda.nextAgenda(grammar, oracle);
        }
        
        return Collections.EMPTY_LIST;
    }
    
}
