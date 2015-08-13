package epar.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import epar.util.Logging;

public class Sentence {

	public final List<Word> words;

	private Sentence(List<Word> words) {
		this.words = words;
	}

	public static Sentence readSentence(Scanner scanner) {
		List<Word> words = new ArrayList<Word>();

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			Logging.fine("Read input line: " + line);

			if ("".equals(line)) {
				break;
			}

			words.add(Word.read(line));
		}

		return new Sentence(words);
	}

	public static List<Sentence> readSentences(File file)
			throws FileNotFoundException {
		List<Sentence> sentences = new ArrayList<Sentence>();
		Scanner scanner = new Scanner(file);

		while (scanner.hasNextLine()) {
			sentences.add(readSentence(scanner));
		}

		return sentences;
	}

}
