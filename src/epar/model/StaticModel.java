package epar.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import epar.parser.Action;

public class StaticModel implements Model {

	private final Map<String, Map<Action, Double>> weights = new HashMap<String, Map<Action, Double>>();

	public double score(List<String> stateFeatures, Action action) {
		int score = 0;

		for (String feature : stateFeatures) {
			if (!weights.containsKey(feature) || !weights.get(feature).containsKey(action)) {
				continue;
			}

			score += weights.get(feature).get(action);
		}

		return score;
	}

	private void setWeight(String feature, Action action, double weight) {
		if (!weights.containsKey(feature)) {
			weights.put(feature, new HashMap<Action, Double>());
		}
		
		weights.get(feature).put(action, weight);
	}

	public static StaticModel load(Collection<? extends File> modelFiles) throws FileNotFoundException {
		StaticModel model = new StaticModel();

		for (File file : modelFiles) {
			try (Scanner scanner = new Scanner(file, "utf-8")) {
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					String[] parts = line.split(" ");
					String feature = parts[0];
					Action action = Action.fromString(parts[1]);
					double weight = Double.parseDouble(parts[2]);
					model.setWeight(feature, action, weight);
				}
			}
		}

		return model;
	}

}
