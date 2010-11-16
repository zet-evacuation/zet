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
public class Triangle {
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
	}

	void computePlane() {
   //Plane plane = t.plane;
		plane.setPlane( v[0], v[1], v[2] );
	}

	@Override
	public String toString() {
		return v[0].toString() + " " + v[1].toString() + " " + v[2].toString();
	}


}
