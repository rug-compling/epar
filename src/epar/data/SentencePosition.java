package epar.data;

import java.util.ArrayList;
import java.util.List;

import epar.util.SymbolPool;

public class SentencePosition {

    public final int form;

    public final int pos;

    public final List<LexicalItem> lexicalItems;

    public SentencePosition(int form, int pos, List<LexicalItem> lexicalEntries) {
        this.form = form;
        this.pos = pos;
        this.lexicalItems = lexicalEntries;
    }

    public static SentencePosition read(String line) {
        int form;
        int pos;
        List<LexicalItem> lexicalEntries;

        String[] parts = line.split("\t");
        form = SymbolPool.getID(parts[0]);
        pos = SymbolPool.getID(parts[1]);
        lexicalEntries = new ArrayList<>();
        
        for (int i = 2; i < parts.length; i += 5) {
            lexicalEntries.add(new LexicalItem(Integer.parseInt(parts[i]),
                    SymbolPool.getID(parts[i + 1]),
                    SymbolPool.getID(parts[i + 2]),
                    SymbolPool.getID(parts[i + 3]),
                    SymbolPool.getID(parts[i + 4])));
        }

        return new SentencePosition(form, pos, lexicalEntries);
    }

}
