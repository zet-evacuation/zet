/**
 * CameraTest.java
 * Created: 03.03.2010, 18:09:55
 */
package opengl.framework;

import junit.framework.TestCase;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class CameraTest extends TestCase {

	/**
	 * Checks wheather the setSpeed() method works correct, or not.
	 * @throws Exception
	 */
	public void testSpeed() throws Exception {
		Camera tc = new Camera();
		tc.setMinSpeed( -10 );
		tc.setMaxSpeed( 20 );
		assertEquals( "Speed initialized incorrect.", 20, tc.getSpeed(), 0.0 );

		tc.setSpeed( 10 );
		assertEquals( "Normal speed assignment incorrect.", 10, tc.getSpeed(), 0.0 );

		tc.setSpeed( -10 );
		assertEquals( "Speed exact min incorrect.", -10, tc.getSpeed(), 0.0 );

		tc.setSpeed( 20 );
		assertEquals( "Speed exact max incorrect.", 20, tc.getSpeed(), 0.0 );

		tc.setSpeed( -11 );
		assertEquals( "Speed to slow incorrect.", -10, tc.getSpeed(), 0.0 );

		tc.setSpeed( 21 );
		assertEquals( "Speed to high incorrect.", 20, tc.getSpeed(), 0.0 );
	}
}
