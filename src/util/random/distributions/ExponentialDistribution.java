/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/**
 * Class ExponentialDistribution
 * Erstellt 05.05.2008, 00:07:55
 */

package util.random.distributions;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import util.random.RandomUtils;

/**
 * Represents an exponential distributed random variable.
 * @see Distribution
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias("exponentialDistribution") // Saved to XML only as Parameter of Z.AssignmentType
public class ExponentialDistribution extends Distribution {
  /** The lambda parameter of the distribution. */
  @XStreamAsAttribute()
	private double lambda;

  /**
   * Creates a new instance of <code>ExponentialDistribution</code>.
   */
  public ExponentialDistribution() {
    this( 1 );
  }
  
  /**
   * Creates a new instance of <code>ExponentialDistribution</code> within a given interval.
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
	public double getNextRandom() {
		while( true ) {
			double rnd = RandomUtils.getInstance().getRandomGenerator().nextDouble();
			double ret = getMin() + Math.log( rnd ) / -lambda;
			if( ret < getMax() )
				return ret;
		}
	}
}
