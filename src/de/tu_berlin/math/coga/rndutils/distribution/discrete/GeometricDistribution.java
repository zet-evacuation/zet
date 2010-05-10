/*
 * GeometricDistribution.java
 * Created 04.03.2009, 22:35:19
 */

package de.tu_berlin.math.coga.rndutils.distribution.discrete;

import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.rndutils.distribution.DiscreteDistribution;

/**
 * The class <code>GeometricDistribution</code> represents a geometrically
 * distributed random variable.
 * @author Jan-Philipp Kappmeier
 */
public class GeometricDistribution extends DiscreteDistribution {
	/** The parameter for the geometric distribution. */
	private double p;
	private double q;

	/**
	 * Creates a new instance of <code>GeometricDistribution</code>.
	 * @param min the minimal value for the random value
	 * @param max the maximal value for the random value
	 */
	public GeometricDistribution( int min, int max ) {
		this( min, max, 0.5 );
	}

	/**
	 * Creates a new instance of <code>GeometricDistribution</code>.
	 * @param min the minimal value for the random value
	 * @param max the maximal value for the random value
	 * @param p the parameter for the distribution the 
	 */		
	public GeometricDistribution( int min, int max, double p ) {
		super( min, max );
		this.p = p;
		this.q = 1-p;
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "GeometricDistribution";
	}

	@Override
	public double getDensityAt( double x ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public Integer getNextRandom() {
		double u = RandomUtils.getInstance().getRandomGenerator().nextDouble();
		double z = Math.log( u )/Math.log( q );
		int k = (int)Math.floor( z );
		return k + 1;
	}
}
