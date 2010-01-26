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
///**
// * Class HyperExponentialDistribution
// * Erstellt 18.09.2008, 16:06:48
// */
//
//package util.random.distributions;
//
//import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
//import de.tu_berlin.math.coga.rndutils.RandomUtils;
//
///**
// * Represents a hyper-exponential distribution based on two exponential
// * distributions and a probability of choosing the first one of <code>p</code>
// * and the second one of <code>1-p</code>.
// * @author Jan-Philipp Kappmeier
// */
//public class HyperExponentialDistribution extends Distribution {
//	@XStreamAsAttribute()
//	private double p;
//	private ExponentialDistribution e1;
//	private ExponentialDistribution e2;
//
//	/**
//   * Creates a new instance of <code>HyperExponentialDistributionstribution</code>.
//	 * with parameters \lambda_1 = 1, \lambda_2 = 2 and p = 0.5.
//   */
//  public HyperExponentialDistribution() {
//    this( 1, 2, 0.5 );
//  }
//
//  /**
//   * Creates a new instance of <code>HyperExponentialDistribution</code>.
//   * @param lambda1 the parameter describing the failure rate of exponential distribution 1
//   * @param lambda2 the parameter describing the failure rate of exponential distribution 2
//	 * @param p the probability of choosing exponential distribution 1
//   */
//  public HyperExponentialDistribution( double lambda1, double lambda2, double p ) {
//		super( 0, 5 );
//		e1.setLambda( lambda1 );
//		e2.setLambda( lambda2 );
//		this.p = p;
//  }
//
//	/**
//	 * {@inheritDoc}
//	 * <p>The density of the default exponential distribution is
//	 * \lambda * exp( -\lambda * x). If the distribution shall not start at
//	 * the origin and the minimum is used, the formula reads
//	 * \lambda * exp( -\lambda * (x-min))</p>
//	 */
//	@Override
//	public double getDensityAt( double x ) {
//		if( x < getMin() || x > getMax() )
//			return 0;
//		else
//			return e1.getDensityAt( x ) * p + e2.getDensityAt( x ) * (1-p);
//	}
//
//	/**
//	 * Returns the value of the parameter lambda
//	 * @return the parameter value
//	 */
//	public double getLambda1() {
//		return e1.getLambda();
//	}
//
//  /**
//   * Sets a new value for the parameter lambda.
//   * @param lambda the parameter describing the failure rate
//   * @throws IllegalArgumentException if lambda is less than zero
//   */
//	public void setLambda1( double lambda ) {
//		e1.setLambda( lambda );
//	}
//
//	/**
//	 * Returns the value of the parameter lambda
//	 * @return the parameter value
//	 */
//	public double getLambda2() {
//		return e2.getLambda();
//	}
//
//  /**
//   * Sets a new value for the parameter lambda.
//   * @param lambda the parameter describing the failure rate
//   * @throws IllegalArgumentException if lambda is less than zero
//	 */
//	public void setLambda2( double lambda ) {
//		e2.setLambda( lambda );
//	}
//
//	/**
//	 * Returns the parameter <code>p</code> that describes the probability of
//	 * choosing exponential distribution 1.
//	 * @return the parameter p
//	 */
//	public double getP() {
//		return p;
//	}
//
//	/**
//	 * Sets the parameter <code>p</code> that describes the probability of
//	 * choosing exponential distribution 1.
//	 * @param p
//	 * @throws java.lang.IllegalArgumentException if p is smaller than zero or greater than one
//	 */
//	public void setP( double p ) {
//		if( p < 0 || p > 1 )
//			throw new java.lang.IllegalArgumentException( "p must be within one and zero.");
//		this.p = p;
//	}
//
//	/**
//	 * {@inheritDoc}
//	 * @throws IllegalArgumentException if max is smaller than the new value for min or vice versa
//	 */
//	@Override
//	public void setParameter ( double min, double max ) throws IllegalArgumentException {
//		if( e1 == null ) {
//			e1 = new ExponentialDistribution();
//			e2 = new ExponentialDistribution();
//		}
//		super.setParameter( min, max);
//		e1.setParameter( min, max );
//		e2.setParameter( min, max );
//	}
//
//	/**
//	 * Returns the next hyper-exponential distributed value. The random value is
//	 * calculated using two exponential distributions with probability <code>p</code> and
//	 * <code>1-p</code>.
//	 * @return a hyper-exponentially distributed random value less than the maximal value.
//	 */
//	@Override
//	public double getNextRandom() {
//		while( true ) {
//			double rnd = RandomUtils.getInstance().getRandomGenerator().nextDouble();
//			if( p == 1 || p > rnd )
//				return e1.getNextRandom();
//			else
//				return e2.getNextRandom();
//		}
//	}
//}
