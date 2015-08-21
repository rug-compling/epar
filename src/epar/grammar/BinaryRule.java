package epar.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import epar.util.RecUtil;
import epar.util.SymbolPool;
import java.util.Objects;

public class BinaryRule {

    public static enum HeadPosition {

        LEFT, RIGHT;

        @Override
        public String toString() {
            if (this == LEFT) {
                return "l";
            } else {
                return "r";
            }
        }
    }

    public final short leftChildCategory;

    public final short rightChildCategory;

    public final short parentCategory;

    public final BinaryRule.HeadPosition headPosition;

    public BinaryRule(short leftChildCategory, short rightChildCategory,
            short parentCategory, BinaryRule.HeadPosition headPosition) {
        this.leftChildCategory = leftChildCategory;
        this.rightChildCategory = rightChildCategory;
        this.parentCategory = parentCategory;
        this.headPosition = headPosition;
    }

    public static List<BinaryRule> read(String line) {
        List<BinaryRule> rules = new ArrayList<BinaryRule>();
        Scanner scanner = new Scanner(line);
        short leftChildCategory = SymbolPool.get(scanner.next());
        RecUtil.expect(",", scanner);
        short rightChildCategory = SymbolPool.get(scanner.next());
        RecUtil.expect(":", scanner);
        RecUtil.expect("[", scanner);

        while (true) {
            RecUtil.expect("REDUCE", scanner);
            RecUtil.expect("BINARY", scanner);
            String head = scanner.next();
            HeadPosition headPosition;

            if ("LEFT".equals(head)) {
                headPosition = HeadPosition.LEFT;
            } else if ("RIGHT".equals(head)) {
                headPosition = HeadPosition.RIGHT;
            } else {
                scanner.close();
                throw new RuntimeException("Invalid head position indicator: "
                        + head);
            }

            short parentCategory = SymbolPool.get(scanner.next());
            rules.add(new BinaryRule(leftChildCategory, rightChildCategory,
                    parentCategory, headPosition));

            String token = scanner.next();

            if ("]".equals(token)) {
                break;
            } else {
                RecUtil.expect(",", token);
            }
        }

        scanner.close();
        return rules;
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

}
