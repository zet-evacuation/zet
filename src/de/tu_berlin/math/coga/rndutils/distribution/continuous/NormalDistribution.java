/*
 * NormalDistribution.java
 * Created on 26. November 2007, 21:32
 */

package de.tu_berlin.math.coga.rndutils.distribution.continuous;

import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.rndutils.distribution.ContinousDistribution;
import de.tu_berlin.math.coga.rndutils.distribution.Distribution;

/**
 * Represents a normally distributed random variable. This distribution is
 * well-defined through the parameters expected value and variance. The calculated random values have
 * to be bounded within a certain range.
 * If the variance is set to zero, the variable will deterministically take the value determinded through
 * the expected value. A positive variance determines how wide the random values are spreaded around
 * the expected value.
 * @author Jan-Philipp Kappmeier
 * @see Distribution
 */
public class NormalDistribution extends ContinousDistribution {
  /** The variance of the distribution. */
  private double variance;
  /** The expected value of the distribution */
  private double expectedValue;
  
  /**
   * Creates a new instance of {@link NormalDistribution}. All needed values have to be passed
   * to the constructor.
   *
   * @param expectedValue the expected value of the random variable. Can be every double value.
   * @param variance the variance of the random variable. Has to be greater or equal to zero.
   * @param min the minimal value that can be taken by the random variable
   * @param max the maximal value that can be taken by the random variable
   */
  public NormalDistribution( double expectedValue, double variance, double min, double max ) {
    super( min, max );
    setParameter( expectedValue, variance, min, max );
  }
  
  /**
   * Creates a new instance of {@link NormalDistribution} with variance 1 and expected value 0. This represents a standard normally
   * distributed random variable.
   * @param min the minimal value that can be taken by the random variable
   * @param max the maximal value that can be taken by the random variable
   * @throws IllegalArgumentException if min is greater than zero or max is less than zero
   */
  public NormalDistribution( double min, double max ) throws IllegalArgumentException {
    super( min, max );
    setParameter( 0, 1, min, max );
  }
  
  /**
   * Creates a new instance of <code>NormalDistribution</code> with variance 1 and expected value 0. This represents a standard normally
   * distributed random variable. The limits are automatically set to -3 and 3, all results lie in this interval with a
   * probability of 99%.
   */
  public NormalDistribution( ) {
    super( -3, 3 );
    setParameter( 0, 1, -3, 3 );
  }
  
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getDensityAt( double x ) {
		if( x < getMin() || x > getMax() )
			return 0;
		else if( variance == 0 && x == expectedValue )
			return 1;
		else if( variance == 0 && x != expectedValue )
			return 0;
		else
			return 1/(variance * CPI) * Math.exp( -0.5 *((x-expectedValue)/variance)*((x-expectedValue)/variance));
	}

	final static double CPI = Math.sqrt(2 * Math.PI);
	@Override
	public double getDensityAt( double x, boolean ignoreBounds ) {
		if( !ignoreBounds )
			return getDensityAt( x );
		if( variance == 0 && x == expectedValue )
			return 1;
		else if( variance == 0 && x != expectedValue )
			return 0;
		else
			return 1/(variance * CPI) * Math.exp( -0.5 *((x-expectedValue)/variance)*((x-expectedValue)/variance));
	}


	/**
   * Returns the expected value for this distribution.
   * @return the expected value for this distribution
   */
  public double getExpectedValue() {
    return expectedValue;
  }
  
  /**
   * Returns variance for the distribution.
   * @return variance for the distribution
   */
  public double getVariance() {
    return variance;
  }
  
  /**
   * Sets a new value for the variance. It has to match the currently set values for min and max.
   * @param val the variance
   * @throws IllegalArgumentException if variance is less than zero.
   */
  public void setExpectedValue( double val ) throws IllegalArgumentException {
    setParameter( val, variance, this.getMin(), this.getMax() );
  }
  
  /**
   * Sets all parameters of a normally distributed random variable.
   * @param expectedValue the expected value
   * @param variance the variance, it has to be greater or equal to zero
   * @param min the minimal value that can be taken
   * @param max the maximal value that can be taken
   * @throws IllegalArgumentException if min is greater than max, variance is less then zero or the expected value is not in the range between min and max
   */
  public void setParameter( double expectedValue, double variance, double min, double max ) throws IllegalArgumentException {
    if( variance < 0 )
      throw new IllegalArgumentException( "Variance has to be zero or greater." );
    if( expectedValue < min || expectedValue > max )
      throw new IllegalArgumentException( "Expected value has to be within minimum and maximum values." );
    
    this.expectedValue = expectedValue;
    this.variance = variance;
    //Place this call at the end - this way the event is thrown when change is complete
    super.setParameter( min, max );
  }
  
  /**
   * Sets a new value for the variance.
   * @param val the variance
   * @throws IllegalArgumentException if the new variance is less than zero
   */
  public void setVariance( double val ) throws IllegalArgumentException {
    setParameter( expectedValue, val, this.getMin(), this.getMax() );
  }
  		int rounds = 0;

  /**
   * Gets the next random number according to this distribution. The generated
   * value normally distributed between the values returned by {@link #getMin()} and
   * {@link #getMax()} and has the  variance and expected value as specified.
   * @return the normally distributed random value
   */
//	final static Random rnd = new Random();
  public Double getNextRandom() {
    double val;
		rounds = 0;
		if( getMin() == getMax() )
			return getMin();
    do {
			rounds++;
			final double randomNumber = RandomUtils.getInstance().getRandomGenerator().nextGaussian();
			val = getExpectedValue() + randomNumber * getVariance();
		} while( val < getMin() | val > getMax() );
		//} while( val < 0 );
    return val;
  }

	public int getRounds() {
		return rounds;
	}

	
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		NormalDistribution d = (NormalDistribution)super.clone();
		//NormalDistribution b = new NormalDistribution( this.getExpectedValue(), this.getVariance(), this.getMin(), this.getMax() );
		return d;
	}

	public double getInverseCumulatedDensityFunction( double d ) {
		return getInverseCumulatedDensityFunction( d, false ) * variance + expectedValue;
	}

/* ********************************************
 * Original algorythm and Perl implementation can
 * be found at:
 * http://www.math.uio.no/~jacklam/notes/invnorm/index.html
 * Author:
 *  Peter J. Acklam
 *  jacklam@math.uio.no
 * ****************************************** */
  private static final double P_LOW  = 0.02425D;
  private static final double P_HIGH = 1.0D - P_LOW;

	// Coefficients in rational approximations.
	private static final double ICDF_A[] = {-3.969683028665376e+01, 2.209460984245205e+02, -2.759285104469687e+02, 1.383577518672690e+02, -3.066479806614716e+01, 2.506628277459239e+00};
	private static final double ICDF_B[] = {-5.447609879822406e+01, 1.615858368580409e+02, -1.556989798598866e+02, 6.680131188771972e+01, -1.328068155288572e+01};
	private static final double ICDF_C[] = {-7.784894002430293e-03, -3.223964580411365e-01, -2.400758277161838e+00, -2.549732539343734e+00, 4.374664141464968e+00, 2.938163982698783e+00};
	private static final double ICDF_D[] = {7.784695709041462e-03, 3.224671290700398e-01, 2.445134137142996e+00, 3.754408661907416e+00};

  public static double getInverseCumulatedDensityFunction( double d, boolean highPrecision ) {
    // Define break-points.
    // variable for result
    final double z;

		if( d == 0 )
			z = Double.NEGATIVE_INFINITY;
		else if( d == 1 )
			z = Double.POSITIVE_INFINITY;
		else if( Double.isNaN( d ) || d < 0 || d > 1 )
			z = Double.NaN;
		else if( d < P_LOW ) { // Rational approximation for lower region:
			final double q = Math.sqrt( -2 * Math.log( d ) );
			z = (((((ICDF_C[0] * q + ICDF_C[1]) * q + ICDF_C[2]) * q + ICDF_C[3]) * q + ICDF_C[4]) * q + ICDF_C[5]) / ((((ICDF_D[0] * q + ICDF_D[1]) * q + ICDF_D[2]) * q + ICDF_D[3]) * q + 1);
		} else if( P_HIGH < d ) { // Rational approximation for upper region:
			final double q = Math.sqrt( -2 * Math.log( 1 - d ) );
			z = -(((((ICDF_C[0] * q + ICDF_C[1]) * q + ICDF_C[2]) * q + ICDF_C[3]) * q + ICDF_C[4]) * q + ICDF_C[5]) / ((((ICDF_D[0] * q + ICDF_D[1]) * q + ICDF_D[2]) * q + ICDF_D[3]) * q + 1);
		} else { // Rational approximation for central region:
			final double q = d - 0.5D;
			final double r = q * q;
			z = (((((ICDF_A[0] * r + ICDF_A[1]) * r + ICDF_A[2]) * r + ICDF_A[3]) * r + ICDF_A[4]) * r + ICDF_A[5]) * q / (((((ICDF_B[0] * r + ICDF_B[1]) * r + ICDF_B[2]) * r + ICDF_B[3]) * r + ICDF_B[4]) * r + 1);
		}
    //if(highPrecision) z = refine(z, d);
    return z;
  }

}