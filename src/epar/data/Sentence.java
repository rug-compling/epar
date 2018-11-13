package epar.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Sentence {

    public final List<SentencePosition> positions;

    public Sentence(List<SentencePosition> positions) {
        this.positions = positions;
    }

    public static Sentence readSentence(Scanner scanner) {
        List<SentencePosition> words = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if ("".equals(line)) {
                break;
            }

            words.add(SentencePosition.read(line));
        }

        return new Sentence(words);
    }

    public static List<Sentence> readSentences(File file)
            throws FileNotFoundException {
        List<Sentence> sentences = new ArrayList<>();
        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            sentences.add(readSentence(scanner));
        }

        return sentences;
    }
    
    public List<SentencePosition> positionsAt(List<Integer> offsets) {
        List<SentencePosition> result = new ArrayList<>(offsets.size());
        
        for (int offset : offsets) {
            result.add(positions.get(offset));
        }
        
        return result;
    }
        
    public List<Integer> formsAt(List<Integer> offsets) {
        List<Integer> result = new ArrayList<>(offsets.size());
        
        for (int offset : offsets) {
            result.add(positions.get(offset).form);
        }
        
        return result;
    }
        
    public List<Integer> posAt(List<Integer> offsets) {
        List<Integer> result = new ArrayList<>(offsets.size());
        
        for (int offset : offsets) {
            result.add(positions.get(offset).pos);
        }
        
        return result;
    }

    public static void writeSentences(List<Sentence> sentences, File file) throws IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))) {
            for (Sentence sentence : sentences) {
                for (SentencePosition position : sentence.positions) {
                    position.write(writer);
                }
                
                writer.write("\n");
            }
        }
    }
    
    @Override
    public String toString() {
        return positions.toString();
    }

}
