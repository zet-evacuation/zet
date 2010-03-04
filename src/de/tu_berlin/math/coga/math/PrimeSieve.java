/**
 * PrimeSieve.java
 * Created: Feb 15, 2010,2:29:27 PM
 */
package de.tu_berlin.math.coga.math;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PrimeSieve {
	int n;
	int[] primes;
	int bound;
	int pointer;
	int primeCount = 0;

	/**
	 * Computes all prime numbers up to the bound n
	 * @param n
	 */
	public PrimeSieve( int n ) {
		this.n = n;
		this.bound = getPrimesUpperBound( n );
		primes = new int[bound];
	}

	private static int getPrimesUpperBound( long n ) {
		if( n < 17 )
			return 6;

		return (int)java.lang.Math.floor( n / (java.lang.Math.log10( n )) - 1.5 );
	}

	public int getPrimeCount() {
		return primeCount;
	}

	public int getPrime( int n ) {
		return primes[n-1];
	}

	public int[] getPrimes() {
		return primes;
	}

	public void compute() {
		n = (n%2 == 0) ? n-1: n;
		int root = (int)java.lang.Math.sqrt( n );
		boolean[] working = new boolean[n / 2];

		primes[0] = 2;
		int index = 0;
		int start = 3;
		int skip = 3;
		int pindex = 1;

		//for( int i = 0; i < root; i++ ) {
		while( start < n / 2 ) {
			if( !working[index] ) {
				primes[pindex++] = (index << 1) + 3;
				// erase
				for( int j = start; j < n / 2; j += skip )
					working[j] = true;
			}

			start += (++skip) << 1;
			skip++;
			index++;
		}

		// give out the rest
		while( index < n / 2 & (pindex) < bound ) {
			if( !working[index] )
				primes[pindex++] = (index << 1) + 3;
			index++;
		}
		primeCount = pindex;
	}

	public void computeADWopt() {
		boolean[] working = new boolean[n + 1];
		int i = 0;
		int k = 0;
		try {
			for( i = 2; i <= java.lang.Math.floor( java.lang.Math.sqrt( n ) ); ++i )
				for( k = n / i; k >= i; --k )
					working[i * k] = true;
		} catch( Exception e ) {
			int t = 3;
		}

		int count = 0;
		for( i = 2; i <= n; ++i )
			if( !working[i] )
				primes[count++] = i;
		primeCount = count;

	}

	public void computeOpt() {
		if( n == 2 ) {
			primes[0] = 2;
			primeCount = 1;
			return;
		}

		boolean[] working = new boolean[n/3];

		int d1 = 8;
		int d2 = 8;
		int p1 = 3;
		int p2 = 7;
		int s1 = 7;
		int s2 = 3;
		int thisN = 0;
		int len = working.length;
		boolean toggle = false;

		while(s1 < len) {// -- scan sieve
			if( !working[thisN++] ) { // -- if a prime is found
				// -- cancel its multiples
				int inc = p1 + p2;

				for( int k = s1; k < len; k += inc )
					working[k] = true;

				for( int k = s1 + s2; k < len; k += inc )
					working[k] = true;
			}

			if( toggle = !toggle ) { // Never mind, it's ok.
				s1 += d2;
				d1 += 16;
				p1 += 2;
				p2 += 2;
				s2 = p2;
			} else {
				s1 += d1;
				d2 += 8;
				p1 += 2;
				p2 += 6;
				s2 = p1;
			}
		}

		toggle = false;
		int p = 5, i = 0, j = 2;

		primes[0] = 2;
		primes[1] = 3;

		while(p <= n) {
			if( !working[i++] )
				primes[j++] = p;
			// -- never mind, it's ok.
			p += (toggle = !toggle) ? 2 : 4;
		}
		primeCount = j;
	}

	public static void main( String[] args ) {
		PrimeSieve p;
		int n = 1;
		for( int i = 1; i <= 40; ++i ) {
			n *= 2;
			System.out.print( "n = ;" + n );
			System.out.print( ";optimiertes PrimeSieve" );
			System.gc();
			p = new PrimeSieve( n );
			long start = System.nanoTime();
			p.compute();
			long end = System.nanoTime();
			System.out.print( ";" + (end - start) );
			//		for( int i = 0; i < p.primeCount; ++i ) {
			//			System.out.println( p.primes[i] );
			//		}


			System.out.print( ";OptAlgo3" );
			p = new PrimeSieve( n );
			System.gc();
			start = System.nanoTime();
			p.computeADWopt();
			end = System.nanoTime();
			System.out.print( ";" + (end - start) );

			System.out.print( ";Luschny" );
			p = new PrimeSieve( n );
			System.gc();
			start = System.nanoTime();
			p.computeOpt();
			end = System.nanoTime();
			System.out.print( ";" + (end - start) );
			System.out.println();

		}

//		for( int i = 0; i < p.primeCount; ++i ) {
//			System.out.println( p.primes[i] );
//		}


	}
}
