/*
 * CameraPosition.java
 * Created 30.09.2009, 11:22:36
 */

package ds;

import de.tu_berlin.math.coga.math.vectormath.Vector3;

/**
 * The class <code>CameraPosition</code> stores a camera position. The position
 * consists of vectors for position, up, view and a name.
 * @author Jan-Philipp Kappmeier
 */
public class CameraPosition {
	/** Direction of view (z-axis) */
	public Vector3 view = new Vector3( 1, -1, 0 );
	/** Direction of up (y-axis) */
	public Vector3 up = new Vector3( 0, 0, 1 );
		/** Position */
	public Vector3 pos = new Vector3( 0, 0, 100 );
	/** A description for the position. */
	public String text = "CameraPosition";

	/**
	 * Creates a new instance of <code>CameraPosition</code>.
	 */
	public CameraPosition() {	}
}
