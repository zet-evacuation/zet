/*
 * ContinousDistribution.java
 * Created 03.03.2009, 18:27:56
 */

package de.tu_berlin.math.coga.rndutils.distribution;

/**
 * The class {@code ContinousDistribution} rperesents a continous
 * probability distribution with values in the real numbers.
 * @author Jan-Philipp Kappmeier
 */
public abstract class ContinousDistribution extends Distribution<Double> {

	/**
	 * Creates a new instance of {@code ContinousDistribution}.
	 * @param min
	 * @param max
	 */
	public ContinousDistribution( double min, double max ) {
		super( min, max );
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "ContinousDistribution";
	}

	/**
	 * {@inheritDoc}
	 * @throws IllegalArgumentException if min is greater than max
	 */
	@Override
	public void setParameter( Double min, Double max ) throws IllegalArgumentException	{
		if( min > max ) {
			throw new IllegalArgumentException ( "Minimum value is greater than maximum." );
		}
		this.min = min;
		this.max = max;
	}
}
