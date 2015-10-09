package epar.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Sentence {

    public final List<SentencePosition> positions;

    private Sentence(List<SentencePosition> words) {
        this.positions = words;
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

}
