/**
 * FormatterTest.java
 * Created: Nov 18, 2010, 1:33:30 PM
 */
package math.coga.common.util;

import de.tu_berlin.math.coga.common.util.Formatter;
import de.tu_berlin.math.coga.common.util.Formatter.TimeUnits;
import de.tu_berlin.math.coga.datastructure.Tupel;
import junit.framework.TestCase;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FormatterTest extends TestCase {

	/** Creates a new instance of {@code FormatterTest}. */	
	public FormatterTest() { }
	
	/**
	 * Tests the formatting for time units.
	 */
	public void testTimeUnits() {
		Tupel<Double,TimeUnits> ret;
		final int secs = 31557600;
		// check if the number of seconds of a year is computed correct (note that it is not the multiple of 365!
		ret = Formatter.timeUnit( secs, TimeUnits.Seconds );
		assertEquals( 1.0, ret.u );
		assertEquals( ret.v, TimeUnits.Years );
		System.out.println( Formatter.formatTimeUnit( secs, TimeUnits.Seconds, 2 ) );
		
		// what if, if they are minutes?
		ret = Formatter.timeUnit( secs, TimeUnits.Minutes );
		assertEquals( 60.0, ret.u );
		assertEquals( ret.v, TimeUnits.Years );
		System.out.println( Formatter.formatTimeUnit( secs, TimeUnits.Minutes, 2 ) );
		
		//1 micro second = 1000 nano seconds = 0,000 001 seconds
		ret = Formatter.timeUnit( 1000, TimeUnits.NanoSeconds );
		assertEquals( 1.0, ret.u );
		assertEquals( ret.v, TimeUnits.Microsecond );
		System.out.println( Formatter.formatTimeUnit( 1000, TimeUnits.NanoSeconds, 4 ) );
		ret = Formatter.timeUnit( 0.000001, TimeUnits.Seconds );
		assertEquals( 1.0, ret.u );
		assertEquals( ret.v, TimeUnits.Microsecond );
		System.out.println( Formatter.formatTimeUnit( 0.000001, TimeUnits.Seconds, 4 ) );

		//1 pico second = 0,000 000 000 001 seconds
		ret = Formatter.timeUnit( 1001, TimeUnits.PicoSeconds );
		assertEquals( 1.001, ret.u );
		assertEquals( ret.v, TimeUnits.NanoSeconds );
		System.out.println( Formatter.formatTimeUnit( 1001, TimeUnits.PicoSeconds, 4 ) );
		ret = Formatter.timeUnit( 0.000000000001, TimeUnits.Seconds );
		assertTrue( Math.abs(1.0-ret.u) < 0.0000000001 );	// caution! here we run into inaccuracy problems
		assertEquals( ret.v, TimeUnits.PicoSeconds );
		System.out.println( Formatter.formatTimeUnit( 0.000000000001, TimeUnits.Seconds, 4 ) );
		ret = Formatter.timeUnit( 0.999, TimeUnits.PicoSeconds );
		assertEquals( 0.999, ret.u );
		assertEquals( ret.v, TimeUnits.PicoSeconds );
		System.out.println( Formatter.formatTimeUnit( 0.999, TimeUnits.PicoSeconds, 4 ) );
	}
}
