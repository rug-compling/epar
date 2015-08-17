package epar.model;

import java.util.List;

import epar.feature.Feature;
import epar.parser.Action;

public interface Model {

	public double score(List<Feature> stateFeatures, Action action);

}