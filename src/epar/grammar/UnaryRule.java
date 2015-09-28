package epar.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import epar.util.RecUtil;
import epar.util.SymbolPool;

public class UnaryRule {

    public final short childCategory;

    public final short parentCategory;

    public UnaryRule(short childCategory, short parentCategory) {
        this.childCategory = childCategory;
        this.parentCategory = parentCategory;
    }

    public UnaryRule straighten() {
        return new UnaryRule(SymbolPool.straighten(childCategory), SymbolPool.straighten(parentCategory
        ));
    }

    public static List<UnaryRule> read(String line) {
        List<UnaryRule> rules = new ArrayList<>();
        try (Scanner scanner = new Scanner(line)) {
            short childCategory = SymbolPool.getID(scanner.next());
            RecUtil.expect(":", scanner);
            RecUtil.expect("[", scanner);

            while (true) {
                RecUtil.expect("REDUCE", scanner);
                RecUtil.expect("UNARY", scanner);

                short parentCategory = SymbolPool.getID(scanner.next());
                rules.add(new UnaryRule(childCategory, parentCategory));

                String token = scanner.next();

                if ("]".equals(token)) {
                    break;
                } else {
                    RecUtil.expect(",", token);
                }
            }
        }
        return rules;
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
        return SymbolPool.getString(parentCategory) + " -> " + SymbolPool.getString(childCategory);
    }

}
