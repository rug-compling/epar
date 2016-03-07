package epar.sem;

import epar.util.RecUtil;
import epar.util.SymbolPool;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 *
 * @author p264360
 */
public abstract class Interpretation {
    
    /**
     * This is the dummy interpretation we associate with nodes when we are
     * doing purely syntactic parsing without semantics. Conventionally
     * represented by the string "0".
     */
    public static final Interpretation DUMMY =
            new AtomicInterpretation(SymbolPool.NONE);
    
    private static final Pattern APP = Pattern.compile("app");
    
    private static final Pattern LAM = Pattern.compile("lam");
    
    /**
     * Semantic atoms are represented by integers in our Prolog-based
     * serialization.
     */
    private static final Pattern ATOM = Pattern.compile("[0-9]+");
    
    /**
     * Variables are sequences of capital letters in our Prolog-based
     * serialization.
     */
    private static final Pattern VAR = Pattern.compile("[A-Z]+");
    
    public abstract boolean canLeadTo(Interpretation targetInterpretation);
    
    public abstract boolean containsTermSubsumedBy(Interpretation subsumer);

    public abstract boolean subsumes(Interpretation subsumee);
    
    public abstract Interpretation substitute(VariableInterpretation variable,
            Interpretation term);
    
    public abstract Interpretation applyTo(Interpretation argument);
    
    public static Interpretation fromString(String string) {
        Scanner scanner = new Scanner(string);
        // Split string left and right of parens and commas
        scanner.useDelimiter("(?<=[(),])\\|(?=[(),])");
        Map<String, VariableInterpretation> varMap = new HashMap<>();
        Interpretation result = parse(scanner, varMap);
        RecUtil.expectEnd(scanner);
        return result;
    }

    private static Interpretation parse(Scanner scanner, Map<String, VariableInterpretation> varMap) {
        if (scanner.hasNext(ATOM)) {
            return new AtomicInterpretation(SymbolPool.getID(scanner.next()));
        }
        
        if (scanner.hasNext(VAR)) {
            String var = scanner.next();
            
            if (!varMap.containsKey(var)) {
                varMap.put(var, new VariableInterpretation());
            }
            
            return varMap.get(var);
        }
        
        if (scanner.hasNext(LAM)) {
            scanner.next();
            RecUtil.expect("(", scanner);
            VariableInterpretation variable =
                    (VariableInterpretation) parse(scanner, varMap);
            RecUtil.expect(",", scanner);
            Interpretation body = parse(scanner, varMap);
            RecUtil.expect(")", scanner);
            return new LambdaAbstractionInterpretation(variable, body);
        }
        
        if (scanner.hasNext(APP)) {
            scanner.next();
            RecUtil.expect("(", scanner);
            Interpretation functor = parse(scanner, varMap);
            RecUtil.expect(",", scanner);
            Interpretation argument = parse(scanner, varMap);
            RecUtil.expect(")", scanner);
            return functor.applyTo(argument);
        }
        
        throw new IllegalArgumentException(
                "Unexpected token in semantic expression: " + scanner.next());
    }
    
}
