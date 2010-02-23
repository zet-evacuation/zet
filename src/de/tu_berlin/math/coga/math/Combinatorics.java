/* copyright 2010
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * Combinatorics.java
 * Created 22.02.2010, 21:11:16
 */

package de.tu_berlin.math.coga.math;

/**
 * The class <code>Combinatorics</code> is a utility class that provides
 * several combinatorial methods.
 * @author Jan-Philipp Kappmeier
 */
public class Combinatorics {

	/**
	 * Aviods instancint the utility class with combinatorial methods.
	 */
	private Combinatorics() {
	}

	/**
	 * Computes the binomial coefficient {@code n} choose {@code k}.
	 * @param n parameter {@code n}
	 * @param k parameter {@code k}
	 * @return the binomial coefficient
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

	private static long N;

	/**
	 * Computes the factorial of a natural number {@code n}.
	 * @param n the number whose factorial is computed
	 * @return the factorial of {@code n}
	 */
	public static long factorial( int n ) {
		if( n < 0 )
			throw new IllegalArgumentException( "n must not be at least zero." );
		if( n < 2 )
			return 1;

		long p = 1;
		long r = 1;
		N = 1;

		int log2n = 31 - Integer.numberOfLeadingZeros( n );
		int h = 0, shift = 0, high = 1;

		while( h != n ) {
			shift += h;
			h = n >>> log2n--;
			int len = high;
			high = (h & 1) == 1 ? h : h - 1;
			len = (high - len) / 2;

			if( len > 0 )
				r = r * (p *= product( len ));
		}
		return r << shift;
	}

	/**
	 * Private helping method used to compute the factorial.
	 * @param n
	 * @return
	 */
	private static long product( int n ) {
		int m = n / 2;
		if( m == 0 )
			return N += 2;
		if( n == 2 )
			return (N += 2) * (N += 2);
		return product( n - m ) * product( m );
	}
}
