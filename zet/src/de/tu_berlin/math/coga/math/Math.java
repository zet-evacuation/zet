/*
 * Math.java
 * Created 22.02.2010, 21:26:06
 */

package de.tu_berlin.math.coga.math;

/**
 * The class <code>Math</code> is a utility clas that provides some additional
 * mathematical methods.
 * @author Jan-Philipp Kappmeier
 */
public class Math {

	/**
	 * Creates a new instance of <code>Math</code>.
	 */
	private Math() {
	}

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
	 * @return the logarithm to the basis 2 rounded down
	 */
	public static int log2Ceil( int n ) {
		if( n <= 0 )
			throw new IllegalArgumentException( "n > 0 required" );
		return 32 - Integer.numberOfLeadingZeros( n-1 );
	}
}
