/*
 * CombinatoricsTest.java
 * Created 22.02.2010, 21:24:23
 */
package math;

import de.tu_berlin.math.coga.math.Combinatorics;
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

	public void testFactorial() {
		// Fakultätstest
		int i = 0;
		long last;
		long res = -1;
		do {
			last = res;
			res = Combinatorics.factorial( i++ );
			System.out.println( "i = " + i + ": " + res );
		} while( last < res + 1 );

	}
}
