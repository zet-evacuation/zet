/*
 * CombinatoricsTest.java
 * Created 22.02.2010, 21:24:23
 */
package math;

import de.tu_berlin.math.coga.math.Combinatorics;
import de.tu_berlin.math.coga.math.PrimeSieve;
import junit.framework.TestCase;

/**
 * The class <code>CombinatoricsTest</code> ...
 * @author Jan-Philipp Kappmeier
 */
public class CombinatoricsTest extends TestCase {

	/**
	 * Creates a new instance of <code>CombinatoricsTest</code>.
	 */
	public CombinatoricsTest() {
	}

	public static long naiveBinomial( int n, int k ) {
		long fn = Combinatorics.factorial( n );
		long fk = Combinatorics.factorial( k );
		long fnk = Combinatorics.factorial( n - k );
		return fn / (fk * fnk);
	}

	public static long fastBinomial( int n, int k ) {
		if( 0 > k || k > n )
			throw new IllegalArgumentException( "Binomial: 0 <= k and k <= n required, but n was " + n + " and k was " + k );

		if( (k == 0) || (k == n) )
			return 1;

		if( k > n / 2 )
			k = n - k;

		int fi = 0, nk = n - k;

		int rootN = (int) Math.floor( Math.sqrt( n ) );

		PrimeSieve primeSieve = new PrimeSieve( n );
		primeSieve.computeLuschny();
		int[] primes = primeSieve.getPrimes(); //new PrimeSieve(n).GetPrimeCollection(2, n).ToArray();

		for( int i = 0; i < primeSieve.getPrimeCount(); ++i ) {
			int prime = primes[i];
//  foreach (int prime in primes) // Equivalent to a nextPrime() function.

			// {
			if( prime > nk ) {
				primes[fi++] = prime;
				continue;
			}

			if( prime > n / 2 )
				continue;

			if( prime > rootN ) {
				if( n % prime < k % prime )
					primes[fi++] = prime;
				continue;
			}

			int r = 0, N = n, K = k, p = 1;

			while(N > 0) {
				r = ((N % prime) < (K % prime + r)) ? 1 : 0;
				if( r == 1 )
					p *= prime;

				N /= prime;
				K /= prime;
			}
			primes[fi++] = p;
		}

		long ret = 1;
		for( int i = 0; i < fi; ++i )
			ret *= primes[i];
		return ret;
	}

	public static long betterBinomial( int n, int k ) {
		if( k < 0 || k > java.lang.Math.abs( n ) )
			return 0;
		if( k == 1 )
			return n;
		if( k == 0 )
			return 1;
		if( n > 0 ) {
			if( 2 * k > n )
				return betterBinomial( n, n-k );
			long ret = n;
			for( int i = 2; i <= k; i++ ) {
				ret *= (n + 1 - i);
				ret /= i;
			}
			return ret;
		} else
			throw new IllegalArgumentException( "Negative n are not implemented yet." );
	}

	public void testFactorial() {
		// Fakultätstest
		int i = 0;
		long last;
		long res = -1;
		do {
			last = res;
			res = Combinatorics.factorial( i++ );
			System.out.println( "i = " + i + ": " + res );
		} while(last < res + 1);
	}

	public void testBinomial() {
		long bcounter = 0;
		long fcounter = 0;
		long ncounter = 0;
		int max = 61;
		long start;
		long end;
		for( int n = 1; n <= max; ++n ) {
			for( int k = 0; k <= n; ++k ) {
				bcounter = 0;
				fcounter = 0;
				for( int i = 1; i < 20000; i++ ) {
					start = System.nanoTime();
					//long nb = naiveBinomial( n, k );
					end = System.nanoTime();
					ncounter += (end - start);

					start = System.nanoTime();
					long bb = betterBinomial( n, k );
					end = System.nanoTime();
					bcounter += (end - start);

					start = System.nanoTime();
					long fb = fastBinomial( n, k );
					end = System.nanoTime();
					fcounter += (end - start);
					//System.out.println( n + " über " + k + ": " + nb + " - " + bb + " - " + fb );
					//System.out.println( n + " über " + k + ": " + bb + " - " + fb );
				}
				//System.out.println( "Better: " + bcounter );
				//System.out.println( "Fast:   " + fcounter );
				System.out.println( n + " über " + k + " ratio: " + ((double)fcounter/(double)bcounter) );
			}
		}
		System.out.println( "Naive:  " + ncounter );

//		System.out.println( naiveBinomial( 4, 2 ) );
//		System.out.println( fastBinomial( 4, 2 ) );
//		System.out.println( betterBinomial( 4, 2 ) );
	}
}
