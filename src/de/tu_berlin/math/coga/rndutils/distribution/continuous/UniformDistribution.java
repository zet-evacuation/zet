/*
 * UniformDistribution.java
 * Created on 26. November 2007, 21:32
 */

package de.tu_berlin.math.coga.rndutils.distribution.continuous;

import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.rndutils.distribution.ContinousDistribution;
import de.tu_berlin.math.coga.rndutils.distribution.Distribution;

/**
 * Represents a uniformly distributed random variable. All values are uniformly distributed within the
 * interval between min and max.
 * @see Distribution
 * @author Jan-Philipp Kappmeier
 */
public class UniformDistribution extends ContinousDistribution {
  
  /**
   * Creates a new instance of <code>UniformDistribution</code> with values in the interval 0 and 1.
   */
  public UniformDistribution() {
    super( 0, 1 );
  }
  
  /**
   * Creates a new instance of <code>UniformDistribution</code> within a given interval.
   * @param min the minimal value that can be taken by the random variable
   * @param max the maximal value that can be taken by the random variable
   * @throws IllegalArgumentException if min is smaller than max
   */
  public UniformDistribution( double min, double max ) throws IllegalArgumentException {
    super( min, max );
  }
  
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getDensityAt( double x ) {
		if( x <= getMin() || x >= getMax() )
			return 0;
		else
			return 1/(getMax() - getMin());
	}
	
  /**
   * Gets the next random number according to this distribution. The generated
   * value is uniformly distributed in the interval defined by the values {@link #getMin()} and {@link #getMax()}.
   * @return the random value
   */
  public Double getNextRandom() {
		double randomNumber = RandomUtils.getInstance().getRandomGenerator().nextDouble();
		return getMin() + (getMax() - getMin()) * randomNumber;
  }

	@Override
	public Object clone() throws CloneNotSupportedException {
		UniformDistribution d = (UniformDistribution)super.clone();
		return d;
	}
}
