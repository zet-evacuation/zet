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

package org.zetool.math;

/**
 * The class {@code Combinatorics} is a utility class that provides
 * several combinatorial methods.
 * @author Jan-Philipp Kappmeier
 */
public class Combinatorics {

	/** Avoids instantiation of the utility class with combinatorial methods. */
	private Combinatorics() { }

	/**
	 * Computes the binomial coefficient {@code n} choose {@code k}. This is very
	 * slow!
	 * @param n parameter {@code n}
	 * @param k parameter {@code k}
	 * @return the binomial coefficient
	 */
	public static long bink( final int n, final int k ) {
		if( k < 0 || k > java.lang.Math.abs( n ) )
			return 0;
		if( k == 1 )
			return n;
		if( k == 0 )
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
	public static long factorial( final int n ) {
		if( n < 0 )
			throw new IllegalArgumentException( "n must not be at least zero." );
		if( n < 2 )
			return 1;

		long p = 1;
		long r = 1;
		N = 1;

		int log2n = Math.log2Floor( n );
		int h = 0, shift = 0, high = 1;

		while( h != n ) {
			shift += h;
			h = n >>> log2n--;
			int len = high;
			high = (h & 1) == 1 ? h : h - 1;
			len = (high - len) / 2;

			if( len > 0 )
				r = r * (p *= factorialProduct( len ));
		}
		return r << shift;
	}

	/**
	 * Private auxiliary method used to compute the factorial.
	 * @param n
	 * @return
	 */
	private static long factorialProduct( final int n ) {
		final int m = n / 2;
		if( m == 0 )
			return N += 2;
		if( n == 2 )
			return (N += 2) * (N += 2);
		return factorialProduct( n - m ) * factorialProduct( m );
	}
	static double[] table = {1,1,2,6,24,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	static int ntop = 4;
//	{
//		table[0] = 1.0;
//		table[1] = 1.0;
//		table[2] = 2.0;
//		table[3] = 6.0;
//		table[4] = 24.0;
//	}

		static final double[] cof = {
			76.18009172947146,
			-86.50532032941677,
			24.01409824083091,
			-1.231739572450155,
			0.1208650973866179e-2,
			-0.5395239384953e-5
		};

		public static double gammaln( final double xx ) {
		int j;
		double x,y,tmp=0,ser;
		y = x=xx;
		tmp = x+5.5;
		tmp -= (x+0.5)*java.lang.Math.log(tmp);
		ser = 1.000000000190015;
		for( j = 0; j < 6; ++j )
			ser += cof[j]/++y;
		return -tmp+java.lang.Math.log(2.5066282746310005*ser/x);
		}

	public static double factrl( final int n ) {
		if( n > 32 )
			return java.lang.Math.exp( gammaln(n+1.0));
			int j;
		while( ntop < n ) {
			j = ntop++;
			table[ntop] = table[j]*ntop;
		}
		return table[n];
	}

	public static void main( String[] a ) {
//		for( int i = 1; i < 50; ++i ) {
//			System.out.println( factrl(i ) );
//		}
		for( int i = 1; i <= 20; ++i ) {
			System.out.println( (long)bico( 2*i, i) + " - " + bink(2*i,i) );
		}
	}

	public static double bico( final int n, final int k ) {
		return java.lang.Math.floor( 0.5+java.lang.Math.exp(factln(n)-factln(k)-factln(n-k)));
	}

	static double a[] = new double[101];

	public static double factln( final int n ) {
		if( n <= 1 )
			return 0.0;
		if( n <= 100 )
			return (a[n] != 0.0 ? a[n] : (a[n] = gammaln(n+1.0)));
		else
			return gammaln(n+1.0);
	}
}
