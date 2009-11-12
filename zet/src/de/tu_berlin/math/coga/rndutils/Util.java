/*
 * Util.java
 * Created 03.03.2009, 20:48:59
 */

package de.tu_berlin.math.coga.rndutils;

/**
 * The class <code>Util</code> ...
 * @author Jan-Philipp Kappmeier
 */
public class Util {

	/** Aviods instancint the utility class. */
	public Util() { }

	/**
	 * Calculates the binomial coefficient n choose k.
	 * @param n parameter n
	 * @param k parameter k
	 * @return the binomak coefficient
	 */
	public static long bink( int n, int k ) {
		if( k < 0 || k > Math.abs( n ) )
			return 0;
		if( k == 1 )
			return 1;
		if( n > 0 ) {
			if( 2 * k > n )
				return bink( n, n-k );
			long ret = n;
			for( int i = 2; i <= k; i++ ) {
				ret *= (n + 1 - i);
				ret /= i;
			}
			return ret;
		} else
			throw new IllegalArgumentException( "Negative n are not implemented yet." );
	}
}
