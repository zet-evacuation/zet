/*
 * MathTest.java
 * Created 10.03.2010, 00:21:45
 */

package math;

import junit.framework.TestCase;

/**
 * The class {@code MathTest} ...
 * @author Jan-Philipp Kappmeier
 */
public class MathTest extends TestCase {

	/** Creates a new instance of {@code MathTest}. */
	public MathTest() { }

	public void testFactorial() {

		for( int i = 63; i < 101; ++i ) {
			System.out.println( Math.sqrt( i ) + " - " + (int) Math.floor( Math.sqrt( i ) ) + " - " + de.tu_berlin.math.coga.math.Math.sqrt( i ) );
		}

		long start;
		long end;
		start = System.nanoTime();
		for( int i = 0; i < Integer.MAX_VALUE/8; ++i ) {
			int k = (int) Math.floor( Math.sqrt( i ) );
		}
		end = System.nanoTime();
		System.out.println( "Runden:     " + (end - start) );

		start = System.nanoTime();
		for( int i = 0; i < Integer.MAX_VALUE/8; ++i ) {
			int k = de.tu_berlin.math.coga.math.Math.sqrt( i );
		}
		end = System.nanoTime();
		System.out.println( "Ganzzahlig: " + (end - start) );


	}

}
