package epar.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import epar.parser.Action;
import epar.parser.Candidate;

public class AveragingModel implements Model {

	private final Map<String, Map<Action, AveragingWeight>> weights = new HashMap<String, Map<Action, AveragingWeight>>();

	public double score(List<String> stateFeatures, Action action) {
		double score = 0;

		for (String feature : stateFeatures) {
			if (!weights.containsKey(feature) || !weights.get(feature).containsKey(action)) {
				continue;
			}

			score += weights.get(feature).get(action).getCurrentValue();
		}

		return score;
	}

	public AveragingWeight getWeight(String feature, Action action) {
		Map<Action, AveragingWeight> scoreByAction = weights.get(feature);

		if (scoreByAction == null) {
			scoreByAction = new HashMap<Action, AveragingWeight>();
			weights.put(feature, scoreByAction);
		}
		
		AveragingWeight score = scoreByAction.get(action);

		if (score == null) {
			score = new AveragingWeight();
			scoreByAction.put(action, score);
		}

		return score;
	}

	public void update(int currentStateCount, Candidate candidate, double delta) {
		while (candidate.parent != null) {
			for (String feature : candidate.parent.item.extractFeatures()) {
				getWeight(feature, candidate.item.action).update(currentStateCount, delta);
			}

			candidate = candidate.parent;
		}
	}

	private List<String> toLines(int currentStateCount) {
		List<String> result = new ArrayList<String>();

		for (String feature : weights.keySet()) {
			for (Action action : weights.get(feature).keySet()) {
				double weight = weights.get(feature).get(action).getAveragedValue(currentStateCount);

				//if (weight != 0.0) {
					result.add(feature + " " + action + " " + weight +"\n");
				//}
			}
		}

		Collections.sort(result);
		return result;
	}

	public void saveAveraged(int currentStateCount, File file) throws IOException {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))) {
			for (String line : toLines(currentStateCount)) {
				writer.write(line);
			}
		}
	}

}