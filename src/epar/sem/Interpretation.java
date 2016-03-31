package epar.sem;

import epar.util.RecUtil;
import epar.util.SymbolPool;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * doing purely syntactic parsing without semantics.
     */
    public static final Interpretation DUMMY
            = new AtomicInterpretation(SymbolPool.getID("dummy"));

    /**
     * Split string left and right of parens and commas
     */
    private static final Pattern DELIM
            = Pattern.compile("(?<=[(),])|(?=[(),])");

    private static final Pattern APP = Pattern.compile("app");

    private static final Pattern LAM = Pattern.compile("lam");

    /**
     * Semantic atoms are represented by integers in our Prolog-based
     * serialization.
     */
    private static final Pattern ATOM = Pattern.compile("[0-9]+");

    private static final Pattern TCNAME = Pattern.compile("tc_[A-Z]+_[A-Z]+");

    private static final Pattern TCNAME_START = Pattern.compile("'tc_.*");

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
        if ("nil".equals(string)) {
            return DUMMY;
        }

        Scanner scanner = new Scanner(string);
        scanner.useDelimiter(DELIM);
        Map<String, VariableInterpretation> varMap = new HashMap<>();
        Interpretation result = parse(scanner, varMap);
        RecUtil.expectEnd(scanner);
        return result;
    }

    public static List<Interpretation> read(File file) throws FileNotFoundException {
        List<Interpretation> result = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                result.add(fromString(scanner.nextLine()));
            }
        }

        return result;
    }

    private static Interpretation parse(Scanner scanner, Map<String, VariableInterpretation> varMap) {
        if (scanner.hasNext(ATOM)) {
            return new AtomicInterpretation(SymbolPool.getID(scanner.next()));
        }

        // Hacky parsing of possibly quoted Prolog atoms representing
        // the semantics of type-changing rules
        if (scanner.hasNext(TCNAME)) {
            return new AtomicInterpretation(SymbolPool.getID(scanner.next()));
        }

        if (scanner.hasNext(TCNAME_START)) {
            String tcName = scanner.next();

            while (!tcName.endsWith("'")) {
                tcName += scanner.next();
            }

            return new AtomicInterpretation(SymbolPool.getID(tcName));
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
            VariableInterpretation variable
                    = (VariableInterpretation) parse(scanner, varMap);
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

    public final String toProlog() {
        Map<VariableInterpretation, String> variableNames = new HashMap<>();
        return toProlog(variableNames);
    }

    abstract String toProlog(Map<VariableInterpretation, String> variableNames);

}
