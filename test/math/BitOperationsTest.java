/*
 * BitOperationsTest.java
 * Created 22.02.2010, 21:18:59
 */
package math;

import de.tu_berlin.math.coga.math.BitOperations;
import junit.framework.TestCase;

/**
 * The class {@code BitOperationsTest} ...
 * @author Jan-Philipp Kappmeier
 */
public class BitOperationsTest extends TestCase {
	/**
	 * Creates a new instance of {@code BitOperationsTest}.
	 */
	public BitOperationsTest() {
	}

	public static int bitLen( int n ) {
		return n > 0 ? n >= 1 << 15 ? n >= 1 << 23 ? n >= 1 << 27 ? n >= 1 << 29 ? n >= 1 << 30 ? 30 : 29
						: n >= 1 << 28 ? 28 : 27
						: n >= 1 << 25 ? n >= 1 << 26 ? 26 : 25
						: n >= 1 << 24 ? 24 : 23
						: n >= 1 << 19 ? n >= 1 << 21 ? n >= 1 << 22 ? 22 : 21
						: n >= 1 << 20 ? 20 : 19
						: n >= 1 << 17 ? n >= 1 << 18 ? 18 : 17
						: n >= 1 << 16 ? 16 : 15
						: n >= 1 << 7 ? n >= 1 << 11 ? n >= 1 << 13 ? n >= 1 << 14 ? 14 : 13
						: n >= 1 << 12 ? 12 : 11
						: n >= 1 << 9 ? n >= 1 << 10 ? 10 : 9
						: n >= 1 << 8 ? 8 : 7
						: n >= 1 << 3 ? n >= 1 << 5 ? n >= 1 << 6 ? 6 : 5
						: n >= 1 << 4 ? 4 : 3
						: n >= 1 << 1 ? n >= 1 << 2 ? 2 : 1
						: 0
						: (n & (1 << 31)) == 0 ? -1 : 31;
	}

	/**
	 * This method will fail for the second representation of zero.
	 * @param n
	 * @return
	 */
	public static int bitLenFor( int n ) {
		if( n < 0 )
			return 31;
		if( (n & 1 << 30) != 0 )
			return 30;
		if( n == 0 )
			return -1;
		int l = 31;
		int r = 0;
		int mid;
		while( true ) {
			if( l - r <= 1 )
				return n >= 1 << l ? l : r;
			if( n >= 1 << (mid = (l + r) / 2) )
				r = mid;
			else
				l = mid - 1;
		}
	}

	public static int bitLenTrivial( int n ) {
		for( int i = 31; i >= 0; --i )
			if( (n & 1 << i) != 0 )
				return i;
		return n == 0 ? -1 : 0;
	}

	public static int bitLenIntern( int n ) {
		return 32 - Integer.numberOfLeadingZeros( n );
	}

	private static void bitLenTest( int n, int r ) {
		System.out.println( n + " soll sein: " + r + " - for: " + bitLenFor( n ) + " - trivial:" + bitLenTrivial( n ) + " - intern: " + bitLenIntern( n ) + " - opt:" + bitLen( n ) + " - binary: " + Integer.toBinaryString( n ) );
	}

	public void testBitLen() {
		// Tests
		for( int i = 0; i <= 31; i++ )
			bitLenTest( BitOperations.maxNumber( i ), i );

		System.out.println();
		for( int i = 0; i <= 31; i++ )
			bitLenTest( 1 << i, i );

		System.out.println();
		for( int i = 1; i <= 32; i++ )
			bitLenTest( (1 << i) - 1, i - 1 );
	}
}
