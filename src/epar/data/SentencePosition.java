package epar.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import epar.util.SymbolPool;

public class SentencePosition {

    public final short form;

    public final short pos;

    public final List<LexicalItem> lexicalItems;

    public SentencePosition(short form, short pos, List<LexicalItem> lexicalEntries) {
        this.form = form;
        this.pos = pos;
        this.lexicalItems = lexicalEntries;
    }

    public static SentencePosition read(String line) {
        short form;
        short pos;
        List<LexicalItem> lexicalEntries;
        
        try (Scanner scanner = new Scanner(line)) {
            form = SymbolPool.getID(scanner.next());
            pos = SymbolPool.getID(scanner.next());
            lexicalEntries = new ArrayList<>();

            while (scanner.hasNext()) {
                lexicalEntries.add(LexicalItem.read(scanner));
            }
        }

        return new SentencePosition(form, pos, lexicalEntries);
    }

}
