///* zet evacuation tool copyright (c) 2007-09 zet evacuation team
// *
// * This program is free software; you can redistribute it and/or
// * as published by the Free Software Foundation; either version 2
// * of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
// */
///*
// * NormalDistribution.java
// * Created on 26. November 2007, 21:32
// */
//
//package util.random.distributions;
//
//import com.thoughtworks.xstream.annotations.XStreamAlias;
//import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
//import de.tu_berlin.math.coga.rndutils.RandomUtils;
//
///**
// * Represents a normally distributed random variable. This distribution is
// * well-defined through the parameters expected value and variance. The calculated random values have
// * to be bounded within a certain range.
// * If the variance is set to zero, the variable will deterministically take the value determinded through
// * the expected value. A positive variance determines how wide the random values are spreaded around
// * the expected value.
// * @author Jan-Philipp Kappmeier
// * @see Distribution
// */
//@XStreamAlias("normalDistribution") // Saved to XML only as Parameter of Z.AssignmentType
////public class NormalDistribution extends de.tu_berlin.math.coga.rndutils.distribution.continuous.NormalDistribution {
//public class NormalDistribution extends Distribution {
//  /** The variance of the distribution. */
//  @XStreamAsAttribute()
//  private double variance;
//  /** The expected value of the distribution */
//  @XStreamAsAttribute()
//  private double expectedValue;
//
//  /**
//   * Creates a new instance of {@link NormalDistribution}. All needed values have to be passed
//   * to the constructor.
//   *
//   * @param expectedValue the expected value of the random variable. Can be every double value.
//   * @param variance the variance of the random variable. Has to be greater or equal to zero.
//   * @param min the minimal value that can be taken by the random variable
//   * @param max the maximal value that can be taken by the random variable
//   */
//  public NormalDistribution( double expectedValue, double variance, double min, double max ) {
//    super( min, max );
//    setParameter( expectedValue, variance, min, max );
//  }
//
//  /**
//   * Creates a new instance of {@link NormalDistribution} with variance 1 and expected value 0. This represents a standard normally
//   * distributed random variable.
//   * @param min the minimal value that can be taken by the random variable
//   * @param max the maximal value that can be taken by the random variable
//   * @throws IllegalArgumentException if min is greater than zero or max is less than zero
//   */
//  public NormalDistribution( double min, double max ) throws IllegalArgumentException {
//    super( min, max );
//    setParameter( 0, 1, min, max );
//  }
//
//  /**
//   * Creates a new instance of <code>NormalDistribution</code> with variance 1 and expected value 0. This represents a standard normally
//   * distributed random variable. The limits are automatically set to -3 and 3, all results lie in this interval with a
//   * probability of 99%.
//   */
//  public NormalDistribution( ) {
//    super( -3, 3 );
//    setParameter( 0, 1, -3, 3 );
//  }
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public double getDensityAt( double x ) {
//		if( x < getMin() || x > getMax() )
//			return 0;
//		else if( variance == 0 && x == expectedValue )
//			return 1;
//		else if( variance == 0 && x != expectedValue )
//			return 0;
//		else
//			return 1/(variance * Math.sqrt(2 * Math.PI)) * Math.exp( -0.5 *((x-expectedValue)/variance)*((x-expectedValue)/variance));
//	}
//
//	/**
//   * Returns the expected value for this distribution.
//   * @return the expected value for this distribution
//   */
//  public double getExpectedValue() {
//    return expectedValue;
//  }
//
//  /**
//   * Returns variance for the distribution.
//   * @return variance for the distribution
//   */
//  public double getVariance() {
//    return variance;
//  }
//
//  /**
//   * Sets a new value for the variance. It has to match the currently set values for min and max.
//   * @param val the variance
//   * @throws IllegalArgumentException if variance is less than zero.
//   */
//  public void setExpectedValue( double val ) throws IllegalArgumentException {
//    setParameter( val, variance, this.getMin(), this.getMax() );
//  }
//
//  /**
//   * Sets all parameters of a normally distributed random variable.
//   * @param expectedValue the expected value
//   * @param variance the variance, it has to be greater or equal to zero
//   * @param min the minimal value that can be taken
//   * @param max the maximal value that can be taken
//   * @throws IllegalArgumentException if min is greater than max, variance is less then zero or the expected value is not in the range between min and max
//   */
//  public void setParameter( double expectedValue, double variance, double min, double max ) throws IllegalArgumentException {
//    if( variance < 0 )
//      throw new IllegalArgumentException( "Variance has to be zero or greater." );
//    if( expectedValue < min || expectedValue > max )
//      throw new IllegalArgumentException( "Expected value has to be within minimum and maximum values." );
//
//    this.expectedValue = expectedValue;
//    this.variance = variance;
//    //Place this call at the end - this way the event is thrown when change is complete
//    super.setParameter( min, max );
//  }
//
//  /**
//   * Sets a new value for the variance.
//   * @param val the variance
//   * @throws IllegalArgumentException if the new variance is less than zero
//   */
//  public void setVariance( double val ) throws IllegalArgumentException {
//    setParameter( expectedValue, val, this.getMin(), this.getMax() );
//  }
//
//  /**
//   * Gets the next random number according to this distribution. The generated
//   * value normally distributed between the values returned by {@link #getMin()} and
//   * {@link #getMax()} and has the  variance and expected value as specified.
//   * @return the normally distributed random value
//   */
//  public double getNextRandom() {
//    double val;
//		if( getMin() == getMax() )
//			return getMin();
//    do {
//			double randomNumber = RandomUtils.getInstance().getRandomGenerator().nextGaussian();
//			RandomUtils.getInstance().getRandomGenerator().nextGaussian();
//			//System.out.println( "Randomnumber " + randomNumber + " in getNextRandom/NormalDistribution" );
//			val = getExpectedValue() + randomNumber * Math.sqrt( getVariance() );
//		} while( val < getMin() | val > getMax() );
//    return val;
////    return getMin() + (getMax() - getMin()) *
////            (RandomUtils.getInstance().getRandomGenerator().nextGaussian() *
////            Math.sqrt( getVariance() + getExpectedValue() ));
//  }
//
//	@Override
//	public Object clone() throws CloneNotSupportedException {
//		NormalDistribution d = (NormalDistribution)super.clone();
//		//NormalDistribution b = new NormalDistribution( this.getExpectedValue(), this.getVariance(), this.getMin(), this.getMax() );
//		return d;
//	}
//}