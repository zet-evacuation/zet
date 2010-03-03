/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/**
 * Class Plane
 * Erstellt 03.05.2008, 16:38:42
 */

package de.tu_berlin.math.coga.math.vectormath;

/**
 * Reprensentates a plane in the three dimensional space.
 * @author Jan-Philipp Kappmeier
 */
public class Plane {
	/** The normal vector of the plane. */
	private Vector3 normal;
	/** One point of the plane. */
	private Vector3 point;
	/** Help value for distance computation. The length of the anchor point projected on the normal line. */
	private double d;
	
	/**
	 * Initialize with the {@code x}-{@code y}-plane.
	 */
	public Plane() {
		point = new Vector3();
		normal = new Vector3( 0, 0, 1 );
		d = -( normal.dotProduct( point ) );
	}

	/**
	 * Initialize the plane by trhee points on the plane.
	 * @param v1 the first point
	 * @param v2 the second point
	 * @param v3 the third point
	 */
  public Plane( Vector3 v1, Vector3 v2, Vector3 v3 ) {
		setPlane( v1, v2, v3 );
	}

	/**
	 * Initialize the plane by a point on the plane and its normal vector.
	 * @param normal the normal vector
	 * @param point the point in IR³
	 */
	public Plane( Vector3 normal, Vector3 point) {
		setPlane( normal, point );
	}

	/**
	 * Sets up the plane defined through its normal and one point on the plane.
	 * @param normal the planes normal
	 * @param point one point on the plane
	 */
  public void setPlane(  Vector3 normal, Vector3 point ) {
		this.normal = normal.clone();
		this.point = point.clone();
		this.normal.normalize();
		d = -(this.normal.dotProduct( point ) );
	}
	
/**
 * Sets up the plane defined through three points on it. 
 * @param v1 the first point 
 * @param v2 the second point
 * @param v3 the third point 
 */
  public void setPlane( Vector3 v1, Vector3 v2, Vector3 v3 ) {
		Vector3 aux1 = v1.clone();
		Vector3 aux2 = v3.clone();
		aux1.sub( v2 );
		aux2.sub( v2 );
		normal = aux2.crossProduct( aux1 );
		normal.normalize();
		point = v2.clone();
		d = -( normal.dotProduct( point ) );
	}
	
/**
 * Sets up the plane defined through the four coefficients of an equation of 
 * the type ax_1 + b_x2 + cx_3 + d = 0.
 * @param a the first parameter
 * @param b the second parameter
 * @param c the third parameter
 * @param d the fourth parameter
 */
  public void setPlane( float a, float b, float c, float d ) {
  // set the normal vector
  normal = new Vector3( a, b, c );
  // divide d by the length as well
  this.d = d/normal.length();
	normal.normalize();
	}
	
/**
 * Computes the signed distance between a point and the plane using the
 * inner (or dot) product.
 * @param p the point that should be tested.
 * @return the distance with sign
 */
	public double distance( Vector3 p ) {
		return d + normal.dotProduct( p ); // d = distance to the plane
	}
	
  /**
	 * Returns the normal of the plane.
	 * @return the normal vector
	 */
	public Vector3 getNormal( ) {
		return this.normal;
		// TODO COpy or const
	}
}
