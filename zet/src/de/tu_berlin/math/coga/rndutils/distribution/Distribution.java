/*
 * Distribution.java
 * Created on 26. November 2007, 21:32
 */

package de.tu_berlin.math.coga.rndutils.distribution;

/**
 * Represents a random variable with an arbitrary distribution that can take values between a
 * given minimum and maximum values. The abstract {@link #getNextRandom()}method needs to be
 * implemented in subclasses to retain realizations of the variable.
 * @param <T> The type of the created random numbers
 * @author Jan-Philipp Kappmeier
 */
public abstract class Distribution<T extends Number> implements Cloneable {
	/** The minimal value that this distribution can take. */
	protected T min;
	/** The maximal value that this distribution can take. */
	protected T max;
	
	/**
	 * Creates a new instance of <code>Distribution</code>
	 * @param min the minimal value that can be taken by the random variable
	 * @param max the maximal value that can be taken by the random variable
	 * @throws java.lang.IllegalArgumentException if min is smaller than max
	 */
	public Distribution ( T min, T max ) throws IllegalArgumentException {
		setParameter ( min, max );
	}
	
	/**
	 * Returns the value of the density function for this probability distribution
	 * at a given point. Values that are below min and higher than max are
	 * returned as zero.
	 * @param x the point
	 * @return the value of the density function
	 */
	abstract public double getDensityAt( double x );

	/**
	 * Returns the value of the density function for this probability distribution
	 * at a given point. It can be decided, if the true distribution should be
	 * used or the one which has borders min and max. By default, a value of
	 * {@code true} gives an exception.
	 * @param x the point
	 * @param ignoreBounds decides wheather a true distribution should be returned
	 * @return the (maybe true) density at a given point
	 * @throws UnsupportedOperationException if {@code false} is passed.
	 */
	public double getDensityAt( double x, boolean ignoreBounds ) throws UnsupportedOperationException {
		if( !ignoreBounds )
			return getDensityAt( x );
		throw new UnsupportedOperationException( "Not supported." );
	}


	/**
	 * Returns the currently set maximal value that the random variable can take.
	 * @return the maximal value
	 */
	public T getMax () {
		return max;
	}
	
	/**
	 * Returns the currently set minimal value that the random variable can take.
	 * @return the minimal value
	 */
	public T getMin () {
		return min;
	}
	
	/**
	 * Sets a new maximal value for the random variable.
	 * @param val the maximal value that can be taken by the random variable
	 * @throws IllegalArgumentException if min is smaller than the new value for max
	 */
	public void setMax ( T val ) throws IllegalArgumentException {
		setParameter ( min, val );
	}
	
	/**
	 * Sets a new minimal value for the random variable.
	 * @param val the minimal value that can be taken by the random variable
	 * @throws IllegalArgumentException if max is smaller than the new value for min
	 */
	public void setMin ( T val ) throws IllegalArgumentException {
		setParameter ( val, max );
	}
	
	/**
	 * Sets both bounding parameters for the distribution at the same time.
	 * @param min the minimal value that can be taken by the random variable
	 * @param max the maximal value that can be taken by the random variable
	 * @throws IllegalArgumentException if max is smaller than the new value for min or vice versa
	 */
	public abstract void setParameter ( T min, T max ) throws IllegalArgumentException;
	
	/**
	 * Gets the next random number according to this distribution. The generated value
	 * has to be between the values returned by {@link #getMin()} and {@link #getMax()}.
	 * @return the random value
	 */
	public abstract T getNextRandom ();
}
