/*
 * DefaultParameterSetTest.java
 * Created 03.05.2010, 21:34:43
 */

package algo.ca.parameter;

import de.tu_berlin.math.coga.rndutils.RandomUtils;
import de.tu_berlin.math.coga.rndutils.distribution.continuous.NormalDistribution;
import junit.framework.TestCase;

/**
 * The class <code>DefaultParameterSetTest</code> tests the speed distribution.
 * @author Jan-Philipp Kappmeier
 */
public class DefaultParameterSetTest extends TestCase {

	/**
	 * Creates a new instance of <code>DefaultParameterSetTest</code>.
	 */
	public DefaultParameterSetTest() {

	}

	public void testAgeSpeed() {
		NormalDistribution age = new NormalDistribution( 50, 20, 10, 85);
		DefaultParameterSet dps = new DefaultParameterSet();

		RandomUtils.getInstance().setSeed( System.nanoTime() );

		for( int i = 1; i <= 1000; ++i ) {
			double ret = age.getNextRandom();
			System.out.print( "age: " + ret + " " );
			double speed = dps.getSpeedFromAge( ret );
			System.out.println( "Speed: " + speed );
		}
		System.out.println( dps.cumulativeFemale/dps.counterFemale );
		System.out.println( dps.cumulativeMale/dps.counterMale );
		System.out.println( (dps.cumulativeFemale+dps.cumulativeMale)/(dps.counterFemale+dps.counterMale) );

	}
}
