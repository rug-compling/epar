package epar.data;

import epar.sem.Interpretation;
import java.util.ArrayList;
import java.util.List;

import epar.util.SymbolPool;
import java.io.IOException;
import java.io.Writer;

public class SentencePosition {

    public final int form;

    public final int pos;

    public final List<LexicalItem> lexicalItems;

    public SentencePosition(int form, int pos, List<LexicalItem> lexicalItems) {
        this.form = form;
        this.pos = pos;
        this.lexicalItems = lexicalItems;
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
                    Interpretation.fromString(parts[i + 4])));
        }

        return new SentencePosition(form, pos, lexicalEntries);
    }

    public void write(Writer writer) throws IOException {
        writer.write(SymbolPool.getString(form));
        writer.write("\t");
        writer.write(SymbolPool.getString(pos));
        
        for (LexicalItem lexicalItem : lexicalItems) {
            writer.write("\t");
            writer.write(String.valueOf(lexicalItem.length));
            writer.write("\t");
            writer.write(SymbolPool.getString(lexicalItem.form));
            writer.write("\t");
            writer.write(SymbolPool.getString(lexicalItem.pos));
            writer.write("\t");
            writer.write(SymbolPool.getString(lexicalItem.category));
            writer.write("\t");
            writer.write(lexicalItem.interpretation.toProlog());
        }
        
        writer.write("\n");
    }

}
