/*
 * Triangle.java
 * Created: 14.11.2010, 18:38:09
 */

package opengl.bsp;

import de.tu_berlin.math.coga.math.vectormath.Plane;
import de.tu_berlin.math.coga.math.vectormath.Vector3;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Triangle implements Face {
	Vector3 v[] = new Vector3[3];
	Plane plane;
	Vector3 faceNormal;

	public Triangle() {
		// gefährlich ;)
	}

	public Triangle( Vector3 v0, Vector3 v1, Vector3 v2 ) {
		v[0] = v0;
		v[1] = v1;
		v[2] = v2;
		computePlane();
	}

	/**
	 * Recomputes the plane equations and the normal. This may be called in the
	 * case, the vertex coordinates have been changed. Additionally, this should
	 * be called in case the normal and plane haven't been computed yet.
	 */
	public void computePlane() {
		if( plane == null )
			plane = new Plane( v[0], v[1], v[2] );
		else
			plane.setPlane( v[0], v[1], v[2] );
		//BspMain.planes.add( plane )
		faceNormal = plane.getNormal();
	}

	@Override
	public String toString() {
		return v[0].toString() + " " + v[1].toString() + " " + v[2].toString();
	}
}
