/**
 * PrimeSieve.java
 * Created: Feb 15, 2010,2:29:27 PM
 */
package org.zetool.math;

import org.zetool.common.util.Formatter;
import org.zetool.common.util.units.TimeUnits;
import java.util.Arrays;

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
		if( (n & 1) == 0 )
			n--;
		boolean[] working = new boolean[n >> 1];

		primes[0] = 2;
		int index = -1;
		int start = 3;
		int skip = 3;
		int pindex = 1;

		while( start < n / 2 ) {
			if( !working[++index] ) {
				primes[pindex++] = (index << 1) + 3;
				// erase
				for( int j = start; j < n / 2; j += skip )
					working[j] = true;
			}
			start += (++skip) << 1;
			skip++;
		}

		// give out the rest
		while( ++index < n / 2 && (pindex) < bound )
			if( !working[index] )
				primes[pindex++] = (index << 1) + 3;
		primeCount = pindex;
	}

	public void computeLowMem() {
		if( (n & 1) == 0 )
			n -= 1;
//		n = (n%2 == 0) ? n-1: n;
		int[] working = new int[(n >> 6)+1];

		primes[0] = 2;
		int index = -1;
		int start = 3;
		int skip = 3;
		int pindex = 1;

		while( start < n / 2 ) {
//			if( (working[++index >> 5] & (1 << index % 32)) == 0 ) {
			if( (working[++index >> 5] & (1 << (index & 31))) == 0 ) {
				primes[pindex++] = (index << 1) + 3;
				// erase
				for( int j = start; j < n / 2; j += skip )
					//working[j] = true;
//					working[j >> 5] |= (1 << j % 32 );
					working[j >> 5] |= (1 << (j & 31) );
			}
			start += (++skip) << 1;
			skip++;
		}

		// give out the rest
		while( ++index < n / 2 && (pindex) < bound )
//			if( (working[index >> 5] & (1 << index % 32)) == 0 )
			if( (working[index >> 5] & (1 << (index & 31))) == 0 )
				primes[pindex++] = (index << 1) + 3;
		primeCount = pindex;
	}

	public void computeThird() {
		if( (n & 1) == 0 )
			n--;
		boolean[] working = new boolean[n];

		boolean last = false;

		primes[0] = 2;
		int index = -1;
		int start = 3;
		int skip = 3;
		int pindex = 1;

		while( start < n / 2 ) {
			if( !working[++index] ) {
				primes[pindex++] = skip;//(index << 1) + 3;
				// erase
				//System.out.println( "Index: " + index + " start: " + start + " skip: " + skip );
// original:
//				for( int j = start; j < n / 2; j += skip )
//					working[j] = true;
//				System.err.println( "start: " + start + " index+skip: " + (index+skip) );
				for( int j = start; j < n / 2; j += skip )
					working[j] = true;
			}
			start += (++skip) << 1;

			skip++;
			System.err.println( "Index: " + ((index<<1)+3) + " skip " + skip );
		}

		// give out the rest
		while( ++index < n / 2 && (pindex) < bound )
			if( !working[index] )
				primes[pindex++] = (index << 1) + 3;
		primeCount = pindex;
	}
	public void computeADW3() {
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

	public void computeADW3Half() {
		boolean[] working = new boolean[n/2 + 1];
		int i = 0;
		int k = 0;
		try {
			for( i = 3; i <= java.lang.Math.floor( java.lang.Math.sqrt( n ) ); i+=2 )
				for( k = n / i; k >= i; k-=2 )
					working[(i * k) >> 1] = true;
		} catch( Exception e ) {

		}

		int count = 0;
		primes[count++] = 2;
		for( i = 1; i < n/2; ++i )
			if( !working[i] )
				primes[count++] = (i<<1) + 1;
		primeCount = count;
	}

	static private int[][] offsetCorrection = {{-1, -3, -1},{-2, 0, 0}};
	static private int[] swap = {0,0,4,0,2};

	public void computeADW3Third() {
		boolean[] working = new boolean[n/3 + 1];
		int i = 0;
		int k = 0;
		int c = 4;

		try {
			for( i = 5; i <= java.lang.Math.floor( java.lang.Math.sqrt( n ) ); i += c ) {
				//c = c == 2 ? 4 : 2;
				c = swap[c];
				int first = n/i;
				first += offsetCorrection[first%2][first%3];

				int c2 = first%3 == 1 ? 4 : 2;

				for( k = first; k >= i; k -= c2 ) {
					//c2 = c2 == 2 ? 4 : 2;
					c2 = swap[c2];
					//working[((i*k)-5) / 3 + (((i*k)-5)%3 == 0 ? 0 : 1)] = true;
					// experiments show that the following (which does the same) is slower:
					working[(int)java.lang.Math.ceil( ((i*k)-5) / 3.0 )] = true;
				}
			}
		} catch( Exception e ) {

		}

		int count = 0;
		primes[count++] = 2;
		if( n <= 2 ) {
			primeCount = 1;
			return;
		}
		primes[count++] = 3;
		int start = 5;
		i = -1;
		while( (start += ((i++ % 2 + 1) << 1)) <= n ) {
			if( !working[i] )
				primes[count++] = start;
		}
		primeCount = count;
	}

	public void computeADW3ThirdLowMem() {
		int[] working = new int[((n/3)>>5) + 1];
		int i = 0;
		int k = 0;
		int c = 4;

		try {
			for( i = 5; i <= java.lang.Math.floor( java.lang.Math.sqrt( n ) ); i += c ) {
				//c = c == 2 ? 4 : 2;
				c = swap[c];
				int first = n/i;
				first += offsetCorrection[first%2][first%3];

				int c2 = first%3 == 1 ? 4 : 2;

				for( k = first; k >= i; k -= c2 ) {
					//c2 = c2 == 2 ? 4 : 2;
					c2 = swap[c2];
					//final int pos = ((i*k)-5) / 3 + (((i*k)-5)%3 == 0 ? 0 : 1);
					//working[pos>>5] |= (1 << pos % 32);
					working[((i*k)-5) / 3 + (((i*k)-5)%3 == 0 ? 0 : 1)>>5] |= (1 << ((i*k)-5) / 3 + (((i*k)-5)%3 == 0 ? 0 : 1) % 32);
					// experiments show that the following (which does the same) is slower:
					//working[(int)java.lang.Math.ceil( ((i*k)-5) / 3.0 )] = true;
				}
			}
		} catch( Exception e ) {

		}

		int count = 0;
		primes[count++] = 2;
		if( n <= 2 ) {
			primeCount = 1;
			return;
		}
		primes[count++] = 3;
		int start = 5;
		i = -1;
		while( (start += ((i++ % 2 + 1) << 1)) <= n ) {
			if( (working[i >> 5] & (1 << i % 32)) == 0 )
			//if( !working[i] )
				primes[count++] = start;
		}
		primeCount = count;
	}

	public void computeLuschny() {
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

	public void computeAtkin() {
		boolean working[] = new boolean[n+1];
		int root = (int)java.lang.Math.sqrt( n );

		//"bilde das kartesische Produkt aus x und y"
		//dabei sind x und y alle Zahlen von 1 bis Wurzel aus Limit
		for( int x = 1; x <= root; ++x )
			for( int y = 1; y <= root; ++y ) {

				//Wenn die Zahl an Lösungen für diese Gleichung ungerade ist
				//und n Modulo 12 ist 1 oder 5, dann muss die Zahl prim sein
				{
//					final int test = 4 * x * x + y * y;
//					if( test <= n && (test % 12 == 1 || test % 12 == 5) )
//						working[test] = !working[test];
					if( 4 * x * x + y * y <= n && ((4 * x * x + y * y) % 12 == 1 || (4 * x * x + y * y) % 12 == 5) )
						working[4 * x * x + y * y] = !working[4 * x * x + y * y];
				/*hier wird das Ergebnis invertiert und damit
				sichergestellt, dass nur die Zahlen prim sind,
				für die eine ungerade Zahl an Lösungen für die
				obere Gleichung existiert */
				}
				//Wenn die Zahl an Lösungen für diese Gleichung ungerade ist
				//und n Modulo 12 ist 1 oder 5, dann muss die Zahl prim sein
				{
//					final int test = 3 * x * x + y * y;
//				if( test <= n && test % 12 == 7 )
//					working[test] = !working[test];
				if( 3 * x * x + y * y <= n && 3 * x * x + y * y % 12 == 7 )
					working[3 * x * x + y * y] = !working[3 * x * x + y * y];
				}
				/*hier wird das Ergebnis invertiert und damit
				sichergestellt, dass nur die Zahlen prim sind,
				für die eine ungerade Zahl an Lösungen für die
				obere Gleichung existiert */

				//das gleiche Prinzip wie oben
				{
//					final int test = 3 * x * x - y * y;
//				if( x > y && test <= n && test % 12 == 11 )
//					working[test] = !working[test];
				if( x > y && 3 * x * x - y * y <= n && 3 * x * x - y * y % 12 == 11 )
					working[3 * x * x - y * y] = !working[3 * x * x - y * y];
				}
			}

//nun sollen alle Quadrate und alle Vielfachen der Quadrate
//der gefundenen Primzahlen gestrichen werden
		for( int l = 5; l <= root; l++ )
			if( working[l] )
				for( int k = 1; k * l * l <= n; k++ )
					working[k * l * l] = false;


		primes[0] = 2;
		primes[1] = 3;
		int counter = 2;
		for( int k = 5; k <= n; k++ )
			if( working[k] )
				primes[counter++] = k;
		primeCount = counter;
	}

	public void sort() {
		primes = Arrays.copyOf( primes, primeCount );
		Arrays.sort( primes );
	}

	public boolean isPrime( int n ) {
		return Arrays.binarySearch( primes, n ) >= 0;
	}

	public static void main( String[] args ) {
		PrimeSieve p = null;
		int count=-1;
		int n = 2;
		long total = 0;
		for( int i = 1; i <= count+2; ++i ) {
			n = (int)(n*1.5);
			//n = 136216567;
			n = 2000000;
			System.out.print( "n = ;" + n );
			long start, end;
			System.out.print( ";optimiertes PrimeSieve" );
			System.gc();
			p = new PrimeSieve( n );
			start = System.nanoTime();
			p.compute();
			end = System.nanoTime();
			//System.out.print( ";" + (end - start) );
			System.out.print( "; " + Formatter.formatUnit( (end - start), TimeUnits.NanoSeconds) );
			if( i > 2 )
				total += (end-start);

//			System.out.print( ";optimiertes PrimeSieve LowMem" );
//			System.gc();
//			p = new PrimeSieve( n );
//			start = System.nanoTime();
//			p.computeLowMem();
//			end = System.nanoTime();
//			//System.out.print( ";" + (end - start) );
//			System.out.print( "; " + Formatter.formatTimeUnit( (end - start), Formatter.TimeUnits.NanoSeconds) );
//			if( i > 2 )
//				total += (end-start);

//			System.out.print( ";OptAlgo3" );
//			p = new PrimeSieve( n );
//			System.gc();
//			start = System.nanoTime();
//			p.computeADW3();
//			end = System.nanoTime();
//			//System.out.print( ";" + (end - start) );
//			System.out.print( "; " + Formatter.formatTimeUnit( (end - start), Formatter.TimeUnits.NanoSeconds) );

			//			System.out.print( ";OptAlgo3Half" );
//			p = new PrimeSieve( n );
//			System.gc();
//			start = System.nanoTime();
//			p.computeADW3Half();
//			end = System.nanoTime();
//			System.out.print( ";" + (end - start) );

//			System.out.print( ";Luschny" );
//			p = new PrimeSieve( n );
//			System.gc();
//			start = System.nanoTime();
//			p.computeLuschny();
//			end = System.nanoTime();
//			//System.out.print( ";" + (end - start) );
//			System.out.print( "; " + Formatter.formatTimeUnit( (end - start), Formatter.TimeUnits.NanoSeconds) );

//			System.out.print( ";Atkin" );
//			p = new PrimeSieve( n );
//			System.gc();
//			start = System.nanoTime();
//			p.computeAtkin();
//			end = System.nanoTime();
//			//System.out.print( ";" + (end - start) );
//			System.out.print( "; " + Formatter.formatTimeUnit( (end - start), Formatter.TimeUnits.NanoSeconds) );


//			System.out.print( ";OptAlgo3Third" );
//			p = new PrimeSieve( n );
//			System.gc();
//			start = System.nanoTime();
//			p.computeADW3Third();
//			end = System.nanoTime();
//			System.out.print( ";" + (end - start) );
//			System.out.print( "; " + Formatter.formatTimeUnit( (end - start), Formatter.TimeUnits.NanoSeconds) );

//			System.out.print( ";OptAlgo3ThirdLowMem" );
//			System.out.println( "Memory: " + Runtime.getRuntime().totalMemory() + " " );
//			p = new PrimeSieve( n );
//			System.gc();
//			System.out.println( "Memory: " + Runtime.getRuntime().totalMemory() + " " );
//			start = System.nanoTime();
//			p.computeADW3ThirdLowMem();
//			System.out.println( "Memory: " + Runtime.getRuntime().totalMemory()+ " " );
//			end = System.nanoTime();
//			System.out.print( "; " + Formatter.formatTimeUnit( (end - start), Formatter.TimeUnits.NanoSeconds) );

//			long sum = 0;
//			for( int j : p.getPrimes() ) {
//				sum += j;
//			}
//			System.out.println( sum );
//
			System.out.println();
		}
		System.out.println( "Total: "+ Formatter.formatUnit( (total/count), TimeUnits.NanoSeconds) );
		System.out.println( p.getPrimeCount() );
		System.out.println( p.getPrimes()[1] );
		System.out.println( p.getPrimes()[10001] );
		System.out.println( p.getPrimes()[10000] );


//		for( int i = 0; i < p.primeCount; ++i ) {
//			System.out.println( p.primes[i] );
//		}


	}
}
