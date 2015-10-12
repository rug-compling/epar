package epar.oracle;

import epar.grammar.BinaryRule;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import epar.parser.Action;
import epar.parser.Candidate;
import epar.parser.Item;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Scanner;

/**
 * An oracle that accepts actions which are part of one of a fixed collection of
 * action sequences.
 * @author p264360
 */
public class MultiActionSequenceOracle implements Oracle {
    
    private final List<Oracle> oracles;
    
    public MultiActionSequenceOracle(Collection<List<Action>> goldSequences) {
        oracles = new ArrayList<>(goldSequences.size());
        
        for (List<Action> sequence : goldSequences) {
            Oracle subOracle;
            
            if (sequence.size() == 1) {
                // If there is only one allowed action sequence, we only need
                // shallow checks.
                subOracle = new ShallowActionSequenceOracle(sequence);
            } else {
                subOracle = new DeepActionSequenceOracle(sequence);
            }
            
            oracles.add(subOracle);
        }
    }

    @Override
    public boolean accept(int generation, Candidate candidate, Item successorItem) {
        // Check action and its lineage against all possible sequences. That we
        // do this every time instead of somehow remembering which action
        // sequences each candidate might be part of is suboptimal, but I don't
        // think it slows things down much.
        
        for (Oracle oracle : oracles) {
            if (oracle.accept(generation, candidate, successorItem)) {
                return true;
            }
        }
        
        return false;
    }
    
    public static MultiActionSequenceOracle fromString(String line) {
        if ("".equals(line)) {
            return new MultiActionSequenceOracle(
                    Collections.<List<Action>>emptyList());
        }
        
        String[] parts = line.split(" \\|\\| ");
        List<List<Action>> actionSequences = new ArrayList<>(parts.length);
        
        for (String part : parts) {
            actionSequences.add(Action.sequenceFromString(part));
        }
        
        return new MultiActionSequenceOracle(actionSequences);
    }
    
    public static List<Oracle> load(File file) throws FileNotFoundException {
        List<Oracle> oracles = new ArrayList<>();
        
        try (Scanner scanner = new Scanner(file, "utf-8")) {
            while (scanner.hasNextLine()) {
                oracles.add(fromString(scanner.nextLine()));
            }
        }
        
        return oracles;
    }
    
}
