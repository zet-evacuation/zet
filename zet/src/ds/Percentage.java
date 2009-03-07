package ds;

import localization.Localization;

/**
 * Represents a percentage value, i.e. the contained double value
 * must lie in [0,100].
 */
public class Percentage {
	
	/**
	 * The contained double value.
	 */
	private double percentage;
	
	/**
	 * Creates a new <code>Percentage</code> object containing
	 * the double value <code>percentage</code>. 
	 * If <code>percentage</code> is not within [0,100],
	 * an exception is thrown.
	 */
	public Percentage(double percentage){
		if (percentage < 0){
			throw new AssertionError(Localization.getInstance (
			).getString ("ds.PercentageNegativeException"));
		}
		if (percentage > 100){
			throw new AssertionError(Localization.getInstance (
			).getString ("ds.PercentageGreater100Exception"));
		}
		this.percentage = percentage;
	}
	
	/**
	 * Returns the contained double value. Result is guaranteed to be
	 * within [0,100].
	 * @return the contained double value lying in [0,100].
	 */
	public double getValue(){
		return percentage;
	}

}
