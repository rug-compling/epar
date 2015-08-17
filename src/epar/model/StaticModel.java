package epar.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import epar.feature.Feature;
import epar.parser.Action;
import epar.util.StringPool;

public class StaticModel implements Model {

	private final Map<Feature, Map<Action, Double>> weights = new HashMap<Feature, Map<Action, Double>>();

	public double score(List<Feature> stateFeatures, Action action) {
		int score = 0;

		for (Feature feature : stateFeatures) {
			if (!weights.containsKey(feature) || !weights.get(feature).containsKey(action)) {
				continue;
			}

			score += weights.get(feature).get(action);
		}

		return score;
	}

	private void setWeight(Feature feature, Action action, double weight) {
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
					
					for (int i = 0; i < parts.length - 1; i++) {
						parts[i] = StringPool.get(parts[i]);
					}
					
					Feature feature = Feature.fromParts(Arrays.asList(parts).subList(0, parts.length - 1));
					Action action = Action.fromString(parts[1]);
					double weight = Double.parseDouble(parts[2]);
					model.setWeight(feature, action, weight);
				}
			}
		}

		return model;
	}

}
