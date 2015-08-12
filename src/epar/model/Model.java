package epar.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import epar.parser.Action;
import epar.parser.Candidate;

public class Model {

	private static final Map<String, Map<Action, Weight>> weights = new HashMap<String, Map<Action, Weight>>();

	public int score(List<String> stateFeatures, Action action) {
		int score = 0;

		for (String feature : stateFeatures) {
			if (!weights.containsKey(feature)
					|| !weights.get(feature).containsKey(action)) {
				continue;
			}

			score += weights.get(feature).get(action).weight;
		}

		return score;
	}

	public Weight getWeight(String feature, Action action) {
		Map<Action, Weight> scoreByAction = weights.get(feature);

		if (scoreByAction == null) {
			scoreByAction = new HashMap<Action, Weight>();
			// TODO different initial capacity?
			weights.put(feature, scoreByAction);
		}

		Weight score = scoreByAction.get(action);

		if (score == null) {
			score = new Weight();
			scoreByAction.put(action, score);
		}

		return score;
	}

	public void update(Candidate candidate, double delta) {
		while (candidate != null) {
			for (String feature : candidate.stateFeatures) {
				getWeight(feature, candidate.item.action).weight += delta;
			}

			candidate = candidate.parent;
		}
	}

	public void save(File file) throws IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "utf-8"))) {
			for (String feature : weights.keySet()) {
				for (Action action : weights.get(feature).keySet()) {
					writer.write(feature + " " + action + " "
							+ weights.get(feature).get(action).weight + "\n");
				}
			}
		}
	}

	/**
	 * Loads a model from a file. If multiple model files are given, the
	 * averaged model will be loaded.
	 * 
	 * @param modelFiles
	 * @return
	 * @throws FileNotFoundException
	 */
	public static Model load(Collection<? extends File> modelFiles)
			throws FileNotFoundException {
		int size = modelFiles.size();
		Model model = new Model();

		for (File file : modelFiles) {
			try (Scanner scanner = new Scanner(file, "utf-8")) {
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					String[] parts = line.split(" ");
					String feature = parts[0];
					Action action = Action.fromString(parts[1]);
					double weight = Double.parseDouble(parts[2]);

					// Averaging
					if (size > 1) {
						weight /= size;
					}

					model.getWeight(feature, action).weight += weight;
				}
			}
		}

		return model;
	}

}