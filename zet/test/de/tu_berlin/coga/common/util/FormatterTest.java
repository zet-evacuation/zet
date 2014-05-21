/**
 * FormatterTest.java
 * Created: Nov 18, 2010, 1:33:30 PM
 */
package de.tu_berlin.coga.common.util;

import de.tu_berlin.coga.common.util.units.TimeUnits;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FormatterTest {

	private final static double EPS = 0.000000000000001;

	/** Creates a new instance of {@code FormatterTest}. */
	public FormatterTest() { }

	/**
	 * Tests the formatting for time units.
	 */
	@Test
	public void testUnits() {
		Quantity<TimeUnits> ret;
		final int secondsOfYear = 31557600;
		// check if the number of seconds of a year is computed correct (note that it is not the multiple of 365!
		ret = Formatter.unit( secondsOfYear, TimeUnits.Seconds );
		assertEquals( 1.0, ret.getValue(), EPS );
		assertEquals( TimeUnits.Years, ret.getUnit() );

		// what if, if they are minutes?
		ret = Formatter.unit( secondsOfYear, TimeUnits.Minutes );
		assertEquals( 60.0, ret.getValue(), EPS );
		assertEquals( TimeUnits.Years, ret.getUnit() );


		//1 micro second = 1000 nano seconds = 0,000 001 seconds
		ret = Formatter.unit( 1000, TimeUnits.NanoSeconds );
		assertEquals(  1.0, ret.getValue(), EPS );
		assertEquals( TimeUnits.Microsecond, ret.getUnit() );

		ret = Formatter.unit( 0.000001, TimeUnits.Seconds );
		assertEquals( 1.0, ret.getValue(), EPS );
		assertEquals( TimeUnits.Microsecond, ret.getUnit() );


		//1 pico second = 0,000 000 000 001 seconds
		ret = Formatter.unit( 1001, TimeUnits.PicoSeconds );
		assertEquals( 1.001, ret.getValue(), EPS );
		assertEquals( ret.getUnit(), TimeUnits.NanoSeconds );

		ret = Formatter.unit( 0.000000000001, TimeUnits.Seconds );
		assertEquals( "Converting seconds to pico seconds", 1.0, ret.getValue(), EPS );
		assertEquals( TimeUnits.PicoSeconds, ret.getUnit() );

		ret = Formatter.unit( 0.999, TimeUnits.PicoSeconds );
		assertEquals( 0.999, ret.getValue(), EPS );
		assertEquals( TimeUnits.PicoSeconds, ret.getUnit() );

	}
}
