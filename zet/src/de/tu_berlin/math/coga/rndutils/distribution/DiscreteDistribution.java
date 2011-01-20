/*
 * DiscreteDistribution.java
 * Created 03.03.2009, 18:27:49
 */

package de.tu_berlin.math.coga.rndutils.distribution;

/**
 * The class {@code DiscreteDistribution} represents a discrete probability
 * distribution.
 * @author Jan-Philipp Kappmeier
 */
public abstract class DiscreteDistribution extends Distribution<Integer> {

	/**
	 * Creates a new instance of {@code DiscreteDistribution}.
	 * @param min
	 * @param max
	 */
	public DiscreteDistribution( int min, int max ) {
		super( min, max );
	}

	/**
	 * {@inheritDoc}
	 * @throws IllegalArgumentException if min is greater than max
	 */
	@Override
	public void setParameter( Integer min, Integer max ) throws IllegalArgumentException	{
		if( min > max ) {
			throw new IllegalArgumentException ( "Minimum value is greater than maximum." );
		}
		this.min = min;
		this.max = max;
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "DiscreteDistribution";
	}
}
