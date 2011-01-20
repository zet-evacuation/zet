/*
 * BinomialDistribution.java
 * Created 03.03.2009, 20:02:24
 */

package de.tu_berlin.math.coga.rndutils.distribution.discrete;

import de.tu_berlin.math.coga.math.Combinatorics;
import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.rndutils.distribution.DiscreteDistribution;

/**
 * The class {@code BinomialDistribution} represents a binomial distributed
 * random variable.
 * @author Jan-Philipp Kappmeier
 */
public class BinomialDistribution extends DiscreteDistribution {
	double p;
	double q;
	int n;

	/**
	 * Creates a new instance of {@code BinomialDistribution}.
	 * @param n
	 */
	public BinomialDistribution( int n ) {
		this( 0, n, 0.5 );
	}

	/**
	 * Creates a new instance of {@code BinomialDistribution}.
	 * @param n
	 * @param p
	 */
	public BinomialDistribution( int n, double p ) {
		this( 0, n, p );
	}

	/**
	 *
	 * @param min the minimal binomial value
	 * @param n the
	 * @param p
	 */
	public BinomialDistribution( int min, int n, double p ) {
		super( min, n + min );
		this.p = p;
		this.q = 1-p;
		this.n = n;
	}

	@Override
	public double getDensityAt( double x ) {
		return getProbability( (int)Math.floor( x ) );
	}

	@Override
	public Integer getNextRandom() {
		double inv = RandomUtils.getInstance().getRandomGenerator().nextDouble();
		double sum = 0;
		int I = -1;
		do
			sum += getProbability( ++I );
		while( sum < inv && I < n );
		return I + min;
	}

	public double getProbability( int k ) {
		double a = Math.pow( p, k );
		double b = Math.pow( q, n-k );
		long c = Combinatorics.bink( n, k );
		return a * b * c;
	}

	/**
	 * Returns the name of the class.
	 * @return the name of the class
	 */
	@Override
	public String toString() {
		return "BinomialDistribution";
	}
}
