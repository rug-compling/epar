package epar.model;

import java.util.List;

import epar.parser.Action;

public interface Model {

	public double score(List<String> stateFeatures, Action action);

}