package epar.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import epar.util.SymbolPool;

public class Word {

    public final short form;

    public final short pos;

    public final List<Short> categories;

    public Word(short form, short pos, List<Short> categories) {
        this.form = form;
        this.pos = pos;
        this.categories = categories;
    }

    public static Word read(String line) {
        short form;
        short pos;
        List<Short> categories;
        try (Scanner scanner = new Scanner(line)) {
            form = SymbolPool.get(scanner.next());
            pos = SymbolPool.get(scanner.next());
            categories = new ArrayList<>();
            while (scanner.hasNext()) {
                categories.add(SymbolPool.get(scanner.next()));
            }
        }
        return new Word(form, pos, categories);
    }

}
