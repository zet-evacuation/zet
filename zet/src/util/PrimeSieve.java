/**
 * PrimeSieve.java
 * input:
 * output:
 *
 * method:
 *
 * Created: Feb 15, 2010,2:29:27 PM
 */
package util;

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

		return (int) Math.floor( n / (Math.log10( n ) - 1.5) );
	}

	public void compute() {
		int root = (int)Math.sqrt( n );
		boolean[] working = new boolean[n/2];

		primes[0] = 2;
		int index = 0;
		int start = 3;
		int skip = 3;
		int pindex = 0;

		//for( int i = 0; i < root; i++ ) {
    while( start < n/2 ) {
			if( !working[index] ) {
				primes[++pindex] = (index<<1) + 3;
				// erase
				for( int j = start; j < n/2; j += skip) {
					working[j] = true;
				}
			}
				int a = 3;

				start += (++skip)<<1;
				skip++;
				index++;
		}
		
		// give out the rest
		while( index < n/2 ) {
			if( !working[index] ) {
				primes[++pindex] = (index<<1)+3;
			}
			index++;
		}
		primeCount = pindex;

	}

	public void compute2 () {
		boolean[] working = new boolean[n];


	}

	public static void main(String[] args) {
		PrimeSieve p = new PrimeSieve(113);
		p.compute();

		for( int i = 0; i < p.primeCount; ++i ) {
			System.out.println( p.primes[i] );
		}
		System.out.println( p.primes );

	}

}
