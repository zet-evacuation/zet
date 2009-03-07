/**
 * Class ErlangDistribution
 * Erstellt 18.09.2008, 15:23:10
 */

package util.random.distributions;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import util.Helper;

/**
 * Represents an Erlang-k-distributed random variable which is basically
 * the sum of k {@link ExponentialDistribution} random variables.
 * @author Jan-Philipp Kappmeier
 */
@XStreamAlias("erlangDistribution")
public class ErlangDistribution extends ExponentialDistribution {
	 /** The n parameter of the distribution. */
  @XStreamAsAttribute()
	private int n;

  /**
   * Creates a new instance of <code>ErlangDistribution</code> with parameters
	 * lambda = 1 and k = 1.
   */
  public ErlangDistribution() {
    this( 1, 1 );
  }
	
  /**
   * Creates a new instance of <code>ErlangDistribution</code> within a given interval.
   * @param lambda the parameter describing the failure rate
   * @param k the parameter describing the number of used exponential distributed values
   */
	public ErlangDistribution( double lambda, int k ) {
		super( lambda );
		n = k;
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
		else {
			double ret = 1;
			//double z = Math.pow( getLambda()*x, n-1 );
			for( int i = 1; i < n; i++) {
				ret *=  (getLambda() * x / i);
			}
			ret *= super.getDensityAt( x );
			return ret;
		}
			//return (Math.pow( getLambda()*x, n-1 ) / Helper.faculty(n-1)) * super.getDensityAt( x );//getLambda() * Math.exp( -getLambda() * x-getMin());
	}
	
	/**
	 * Returns the value of the parameter k
	 * @return the parameter value
	 */
	public int getK() {
		return n;
	}

  /**
   * Sets a new value for the parameter lambda.
   * @param k the parameter describing the number of replications
   */
	public void setK( int k ) {
		if( n <= 0 )
			throw new IllegalArgumentException( "k has to be at least 1." );
		n = k;
	}

	/**
	 * Returns the next erlang distributed value. The random value is
	 * calculated using k exponential distributed values that are added up.
	 * @return an erlang distributed random value less than the maximal value.
	 */
	@Override
	public double getNextRandom() {
		while( true ) {
			double ret = 0;
			for( int i = 0; i < n; i++ )
				ret += super.getNextRandom();
			if( ret < getMax() )
				return ret;
		}
	}	
}
