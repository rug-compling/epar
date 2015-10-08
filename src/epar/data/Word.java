package epar.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import epar.util.SymbolPool;

public class Word {

    public final short form;

    public final short pos;

    public final List<LexicalEntry> lexicalEntries;

    public Word(short form, short pos, List<LexicalEntry> lexicalEntries) {
        this.form = form;
        this.pos = pos;
        this.lexicalEntries = lexicalEntries;
    }

    public static Word read(String line) {
        short form;
        short pos;
        List<LexicalEntry> lexicalEntries;
        
        try (Scanner scanner = new Scanner(line)) {
            form = SymbolPool.getID(scanner.next());
            pos = SymbolPool.getID(scanner.next());
            lexicalEntries = new ArrayList<>();

            while (scanner.hasNext()) {
                lexicalEntries.add(LexicalEntry.fromString(scanner.next()));
            }
        }

        return new Word(form, pos, lexicalEntries);
    }

}
