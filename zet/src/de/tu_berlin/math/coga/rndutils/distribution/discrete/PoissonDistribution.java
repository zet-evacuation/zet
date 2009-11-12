/*
 * PoissonDistribution.java
 * Created 04.03.2009, 22:35:26
 */

package de.tu_berlin.math.coga.rndutils.distribution.discrete;

import de.tu_berlin.math.coga.rndutils.distribution.DiscreteDistribution;

/**
 * The class <code>PoissonDistribution</code> represents a poisson distributed
 * random variable.
 * @author Jan-Philipp Kappmeier
 */
public class PoissonDistribution extends DiscreteDistribution {
	private double lambda = 1;

	/**
	 * Creates a new instance of <code>PoissonDistribution</code>.
	 * @param lambda 
	 */
	public PoissonDistribution( double lambda ) {
		super( 0, Integer.MAX_VALUE );
	}

	/**
	 *
	 * @param min
	 * @param lambda
	 */
	public PoissonDistribution( int min, double lambda ) {
		super( min, Integer.MAX_VALUE );
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "PoissonDistribution";
	}

	@Override
	public double getDensityAt( double x ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public Integer getNextRandom() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}
}
