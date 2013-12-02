package de.tu_berlin.math.coga.common.localization;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DefaultLocalizationTest {
	
	public DefaultLocalizationTest() {
	}

	/**
	 * Tests the correct loading of class {@code DefaultLocalization}. The
	 * default localization class loads an empty bundle file localized in the
	 * same package.
	 */
	@Test
	public void testConstruction() {
		DefaultLocalization dl = new DefaultLocalization();
		assertEquals( null, "test value", dl.getString( "test value" ) );
	}
}