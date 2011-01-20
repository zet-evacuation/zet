/**
 * Class ExponentialDistribution
 * Erstellt 05.05.2008, 00:07:55
 */

package de.tu_berlin.math.coga.rndutils.distribution.continuous;

import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.rndutils.distribution.ContinousDistribution;
import de.tu_berlin.math.coga.rndutils.distribution.Distribution;

/**
 * Represents an exponential distributed random variable.
 * @see Distribution
 * @author Jan-Philipp Kappmeier
 */
public class ExponentialDistribution extends ContinousDistribution {
  /** The lambda parameter of the distribution. */
	private double lambda;

  /**
   * Creates a new instance of {@code ExponentialDistribution}.
   */
  public ExponentialDistribution() {
    this( 1 );
  }
  
  /**
   * Creates a new instance of {@code ExponentialDistribution} within a given interval.
   * @param lambda the parameter describing the failure rate
   * @throws IllegalArgumentException if min is smaller than max
   */
  public ExponentialDistribution( double lambda ) throws IllegalArgumentException {
		super( 0, 5 );
    setLambda( lambda );
  }

	/**
	 * {@inheritDoc}
	 * <p>The density of the default exponential distribution is
	 * \lambda * exp( -\lambda * x). If the distribution shall not start at
	 * the origin and the minimum is used, the formula reads
	 * \lambda * exp( -\lambda * (x-min))</p>
	 */
	@Override
	public double getDensityAt( double x ) {
		if( x < getMin() || x > getMax() )
			return 0;
		else
			return lambda * Math.exp( -lambda * (x-getMin()) );
	}

	/**
	 * Returns the value of the parameter lambda
	 * @return the parameter value
	 */
	public double getLambda() {
		return lambda;
	}

  /**
   * Sets a new value for the parameter lambda.
   * @param lambda the parameter describing the failure rate
   * @throws IllegalArgumentException if lambda is less than zero
   */
	public void setLambda( double lambda ) {
		if( lambda <= 0 )
			throw new IllegalArgumentException( "Lambda has to be greater than zero." );
		this.lambda = lambda;
	}
	
	/**
	 * Returns the next exponential distributed value. The random value is
	 * calculated using inverse transformation from an standard uniformly
	 * distributed value. The initial value \frac{\ln(u)}{-\lambda} is added to
	 * the minimal value.
	 * @return a exponentially distributed random value less than the maximal value.
	 */
	@Override
	public Double getNextRandom() {
		while( true ) {
			double rnd = RandomUtils.getInstance().getRandomGenerator().nextDouble();
			double ret = getMin() + Math.log( rnd ) / -lambda;
			if( ret < getMax() )
				return ret;
		}
	}
}
