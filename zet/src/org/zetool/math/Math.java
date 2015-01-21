/*
 * Math.java
 * Created 22.02.2010, 21:26:06
 */
package org.zetool.math;

/**
 * The class {@code Math} is a utility class that provides some additional
 * mathematical methods.
 * @author Jan-Philipp Kappmeier
 */
public class Math {
	/** Creates a new instance of {@code Math}. */
	private Math() { }

	/**
	 * Computes the rounded down logarithm to the basis 2 of an integral number.
	 * @param n the integral number
	 * @return the logarithm to the basis 2 rounded down
	 */
	public static int log2Floor( int n ) {
		if( n <= 0 )
			throw new IllegalArgumentException( "n > 0 required" );
		return 31 - Integer.numberOfLeadingZeros( n );
	}

	/**
	 * Computes the rounded up logarithm to the basis 2 of an integral number.
	 * @param n the integral number
	 * @return the logarithm to the basis 2 rounded up
	 */
	public static int log2Ceil( int n ) {
		if( n <= 0 )
			throw new IllegalArgumentException( "n > 0 required" );
		return 32 - Integer.numberOfLeadingZeros( n - 1 );
	}
	public static int bitLen(int w)
	{
		return w < 1 << 15 ? (w < 1 << 7 ? (w < 1 << 3 ? (w < 1 << 1 ? (w < 1 ? (w < 0 ? 32 : 0) : 1)
		: (w < 1 << 2 ? 2 : 3)) : (w < 1 << 5 ? (w < 1 << 4 ? 4 : 5) : (w < 1 << 6 ? 6 : 7)))
		: (w < 1 << 11 ? (w < 1 << 9 ? (w < 1 << 8 ? 8 : 9) : (w < 1 << 10 ? 10 : 11))
		: (w < 1 << 13 ? (w < 1 << 12 ? 12 : 13) : (w < 1 << 14 ? 14 : 15))))
		: (w < 1 << 23 ? (w < 1 << 19 ? (w < 1 << 17 ? (w < 1 << 16 ? 16 : 17) : (w < 1 << 18 ? 18 : 19))
		: (w < 1 << 21 ? (w < 1 << 20 ? 20 : 21) : (w < 1 << 22 ? 22 : 23)))
		: (w < 1 << 27 ? (w < 1 << 25 ? (w < 1 << 24 ? 24 : 25) : (w < 1 << 26 ? 26 : 27))
		: (w < 1 << 29 ? (w < 1 << 28 ? 28 : 29) : (w < 1 << 30 ? 30 : 31))));
	}

	public static int sqrt( int n ) {
		if( n < 0 )
			throw new IllegalArgumentException( "n has to be non-negative" );
		if( n == 0 )
			return 0;

//		int z = 0, w = 1, y = 1;
//		while( w <= n ) {
//			z++;
//			w += y + 2;
//			y +=2;
//		}
//		return z;

//		int lower, upper = BitOperations.bitLen( n );
		int lower, upper = bitLen( n );

		do {
			lower = n / upper;
			upper += lower;
			upper >>= 1; //upper = upper/2;
		} while( upper > lower );

		return upper*upper > n ? upper-1 : upper;
	}
}
