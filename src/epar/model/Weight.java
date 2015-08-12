package epar.model;

/**
 * Just a mutable wrapper around a number so we can update scores without
 * creating new objects.
 * 
 * @author ke293
 * 
 */
public class Weight {

	public double weight;
	
	public Weight(double weight) {
		this.weight = weight;
	}

	public Weight() {
		this(0);
	}

}
