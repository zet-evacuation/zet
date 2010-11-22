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

import opengl.bsp.Sign;

/**
 * Represents a plane in the three dimensional space.
 * @author Jan-Philipp Kappmeier
 */
public class Plane {
	/** The normal vector of the plane. */
	private Vector3 normal;
	/** One point of the plane. */
	private Vector3 point;
	/** The d in a plane equation. Also the length of the anchor point projected on the normal line. */
	private double d;
  double a;
  double b;
  double c;
	
	/**
	 * Initialize with the {@code x}-{@code y}-plane.
	 */
	public Plane() {
		point = new Vector3();
		normal = new Vector3( 0, 0, 1 );
		d = -( normal.dotProduct( point ) );
	}

	/**
	 * Initialize the plane by three points on the plane.
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
		final Vector3 aux1 = v1.clone();
		final Vector3 aux2 = v3.clone();
		aux1.subTo( v2 );
		aux2.subTo( v2 );
		normal = aux2.crossProduct( aux1 );
		normal.normalize();
		point = v2.clone();
		a = normal.x;
		b = normal.y;
		c = normal.z;
		d = -( normal.dotProduct( point ) );
	}
	
/**
 * Sets up the plane defined through the four coefficients of an equation of 
 * the type ax_1 + b_x2 + c_x_3 + d = 0.
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

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getD() {
        return d;
    }

	/** Determines whether pos is on the positive side of plane
	Returns true if pos is in the positive side of plane, false otherwise */
//	static boolean isInPositiveSide( Plane plane, Vector3 pos ) {
//		return (plane.getA()*pos.x + plane.getB()*pos.y + plane.getC()*pos.z + plane.getD() > 0);
//	}

	/**
	 * Determines, if a point lies in the positive half of the half space defined
	 * by this edge.
	 * @param pos the point that is checked
	 * @return {@code true}, if {@code pos} lies in the positive half space, {@code false} otherwise
	 */
	public boolean isInPositiveSide( Vector3 pos ) {
		return a*pos.x + b*pos.y + c*pos.z + d > 0;
		//return isInPositiveSide( this, pos );
	}

	public boolean isInNegativeSide( Vector3 pos ) {
		return a*pos.x + b*pos.y + c*pos.z + d < 0;
		//return isInPositiveSide( this, pos );
	}

	/** Determines if an edge bounded by (x1,y1,z1)->(x2,y2,z2) intersects
	the plane.
	If there's an intersection, the sign of (x1,y1,z1), NEGATIVE or POSITIVE,
	w.r.t. the plane is returned with the intersection (ix,iy,iz) updated.
	Otherwise ZERO is returned. */
	public Vector3 intersectionPoint( Vector3 v1, Vector3 v2 ) {
		final double eps = 0.0074;// todo set eps somehow
		int sign1, sign2;		/* must be int since gonna do a bitwise ^ */

		/* get signs */
		double temp = a * v1.x + b * v1.y + c * v1.z + d;
		if( temp < -eps )
			sign1 = -1;
		else if( temp > eps )
			sign1 = 1;
		else {
			/* edges beginning with a 0 sign are not considered valid intersections
			 * case 1 & 6 & 7, see Gems III.
			 */
			assert (Math.abs( temp ) < eps);
			return null;
		}

		temp = (a * v2.x) + (b * v2.y) + (c * v2.z) + d;
		if( temp < -eps )
			sign2 = -1;
		else if( temp > eps )
			sign2 = 1;
		else {			/* case 8 & 9, see Gems III */
			assert (Math.abs( temp ) < eps);// IS_EQ( temp2, 0.0 ));
			return new Vector3( v2.x, v2.y, v2.z );
		}

		/* signs different?
		 * recall: -1^1 == 1^-1 ==> 1    case 4 & 5, see Gems III
		 *         -1^-1 == 1^1 ==> 0    case 2 & 3, see Gems III
		 */
		if( (sign1 ^ sign2) != 0 ) {
			double dx, dy, dz;
			double denom, tt;

			/* compute intersection point */
			dx = v2.x - v1.x;
			dy = v2.y - v1.y;
			dz = v2.z - v1.z;

			denom = (a * dx) + (b * dy) + (c * dz);
			tt = -((a * v1.x) + (b * v1.y) + (c * v1.z) + d) / denom;

			assert (sign1 == 1 || sign1 == -1);

			return new Vector3( v1.x + (tt * dx), v1.y + (tt * dy), v1.z + (tt * dz) );
		} else
			return null;
	}

	public Sign getSign( Vector3 v2 ) {
		final double eps = 0.0074;// todo set eps somehow
		final double val = a * v2.x + b * v2.y + c * v2.z + d;
		return val < -eps ? Sign.Negative : val > eps ? Sign.Positive : Sign.Zero;
	}
}
