package epar.model;

public class AveragingWeight {

	private double currentValue;

	private int currentStateCount;

	private double sumForAverage;

	private int stateCountForAverage;

	public AveragingWeight() {
		this(0.0, 0, 0.0, 0);
	}

	public AveragingWeight(double currentValue, int currentStateCount, double sumForAverage, int stateCountForAverage) {
		this.currentValue = currentValue;
		this.currentStateCount = currentStateCount;
		this.sumForAverage = sumForAverage;
		this.stateCountForAverage = stateCountForAverage;
	}

	private void updateAverage(int currentStateCount) {
		int missedStates = currentStateCount - stateCountForAverage;
		sumForAverage += missedStates * currentValue;
		stateCountForAverage = currentStateCount;
	}

	public void update(int currentStateCount, double delta) {
		updateAverage(currentStateCount);
		currentValue += delta;
	}

	public double getCurrentValue() {
		return currentValue;
	}

	@Override
	public String toString() {
		return currentValue + " " + currentStateCount + " " + sumForAverage + " " + stateCountForAverage;
	}

	public static AveragingWeight read(String string) {
		String[] fields = string.split(" ");
		return new AveragingWeight(Double.parseDouble(fields[0]), Integer.parseInt(fields[1]), Double.parseDouble(fields[2]),
				Integer.parseInt(fields[3]));
	}

	public double getAveragedValue(int currentStateCount) {
		updateAverage(currentStateCount);
		return sumForAverage / stateCountForAverage;
	}

}
