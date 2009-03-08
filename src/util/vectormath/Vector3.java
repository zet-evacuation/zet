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
/*
 * Vector.java
 * Created on 30.01.2008, 00:54:29
 */
package util.vectormath;

import ds.z.PlanPoint;

/**
 * Implements a three dimensional vector.
 * @author Jan-Philipp Kappmeier
 */
public class Vector3 {
	public double x = 0;
	public double y = 0;
	public double z = 0;

	public Vector3() {
		x = 0;
		y = 0;
		z = 0;
	}
	
	public Vector3( Vector3 v ) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public Vector3( double x, double y ) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	public Vector3( double x, double y, double z ) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Inverts the direction of the vector.
	 */
	public void invert() {
		x = -x;
		y = -y;
		z = -z;
	}
	
	/**
	 * Sets all three coordinates at once
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 */
	public void set( double x, double y, double z ) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
//	/**
//	 * Sets only a new x coordinate.
//	 * @param x the new value
//	 */
//	public void setx( double x ) {
//		this.x = x;
//	}
//
//	/**
//	 * Sets only a new y coordinate.
//	 * @param y the new value
//	 */
//	public void sety( double y ) {
//		this.y = y;
//	}
//
//	/**
//	 * Sets only a new z coordinate.
//	 * @param z the new value
//	 */
//	public void setz( double z ) {
//		this.z = z;
//	}

	/**
	 * Cross product.
	 * @param v
	 * @return 
	 */
	public Vector3 crossProduct( Vector3 v ) {
		Vector3 result = new Vector3();
		result.x = y * v.z - z * v.y;
		result.y = z * v.x - x * v.z;
		result.z = x * v.y - y * v.x;
		return result;
	}
	
	/**
	 * Calculates the dot product or scalar product of this vector and another vector..
	 * @param v the other vector
	 * @return the dot product
	 */
	public double dotProduct( Vector3 v ) {
		return x * v.x + y * v.y + z * v.z;
	}
	
	public Vector3 scalaryMultiplication( double scalar ) {
		Vector3 result = new Vector3();
		result.x = x * scalar;
		result.y = y * scalar;
		result.z = z * scalar;
		return result;
	}

	public void scalarMultiplicate( double scalar ) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}

	public void add( Vector3 v ) {
		x += v.x;
		y += v.y;
		z += v.z;
	}

	public Vector3 addition( Vector3 v ) {
		double newx = x + v.x;
		double newy = y + v.y;
		double newz = z + v.z;
		return new Vector3( newx, newy, newz );
	}

	public void sub( Vector3 v ) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}
	
	/**
	 * Calculates this vector - v and puts the result in a new vector object.
	 * @param v
	 * @return
	 */
	public Vector3 subtraction( Vector3 v ) {
		double newx = x - v.x;
		double newy = y - v.y;
		double newz = z - v.z;
		return new Vector3( newx, newy, newz );
	}
	
	public void rotate( double angle, Vector3 axis ) {
		Vector3 nVec = new Vector3();

		double x = axis.x;
		double y = axis.y;
		double z = axis.z;

		// Sinus und Cosinus des Winkels
		double cosTheta = Math.cos( angle * Math.PI / 180.0 );
		double sinTheta = Math.sin( angle * Math.PI / 180.0 );

		// Neue x-Position
		nVec.x = ( cosTheta + ( 1 - cosTheta ) * x * x ) * this.x;
		nVec.x += ( ( 1 - cosTheta ) * x * y - z * sinTheta ) * this.y;
		nVec.x += ( ( 1 - cosTheta ) * x * z + y * sinTheta ) * this.z;

		// Neue y-Position
		nVec.y = ( ( 1 - cosTheta ) * x * y + z * sinTheta ) * this.x;
		nVec.y += ( cosTheta + ( 1 - cosTheta ) * y * y ) * this.y;
		nVec.y += ( ( 1 - cosTheta ) * y * z - x * sinTheta ) * this.z;

		// Neue z-Position
		nVec.z = ( ( 1 - cosTheta ) * x * z - y * sinTheta ) * this.x;
		nVec.z += ( ( 1 - cosTheta ) * y * z + x * sinTheta ) * this.y;
		nVec.z += ( cosTheta + ( 1 - cosTheta ) * z * z ) * this.z;

		this.x = nVec.x;
		this.y = nVec.y;
		this.z = nVec.z;
	}

	/**
	 * Rotates a vector around an axis.
	 * @param angle Winkel um den rotiert werden soll
	 * @param axis Achse um die rotiert werden soll
	 * @param oVec Punkt der rotiert werden soll
	 * @return
	 */
	public static Vector3 rotateVector( double angle, Vector3 axis, Vector3 oVec ) {
		Vector3 nVec = new Vector3();

		double x = axis.x;
		double y = axis.y;
		double z = axis.z;

		// Sinus und Cosinus des Winkels
		double cosTheta = Math.cos( angle * Math.PI / 180.0 );
		double sinTheta = Math.sin( angle * Math.PI / 180.0 );

		// Neue x-Position
		nVec.x = ( cosTheta + ( 1 - cosTheta ) * x * x ) * oVec.x;
		nVec.x += ( ( 1 - cosTheta ) * x * y - z * sinTheta ) * oVec.y;
		nVec.x += ( ( 1 - cosTheta ) * x * z + y * sinTheta ) * oVec.z;

		// Neue y-Position
		nVec.y = ( ( 1 - cosTheta ) * x * y + z * sinTheta ) * oVec.x;
		nVec.y += ( cosTheta + ( 1 - cosTheta ) * y * y ) * oVec.y;
		nVec.y += ( ( 1 - cosTheta ) * y * z - x * sinTheta ) * oVec.z;

		// Neue z-Position
		nVec.z = ( ( 1 - cosTheta ) * x * z - y * sinTheta ) * oVec.x;
		nVec.z += ( ( 1 - cosTheta ) * y * z + x * sinTheta ) * oVec.y;
		nVec.z += ( cosTheta + ( 1 - cosTheta ) * z * z ) * oVec.z;

		return nVec;
	}
	
	
	/**
	 * Normalizes the vector.
	 */
	public void normalize() {
		double len;
		len = length();
		if( len != 0 ) {// only dvide if len is not equal to zero
			x /= len;
			y /= len;
			z /= len;
		}
	}

	/**
	 * Calculates the euclidian length of a vector. 
	 * @return the length of the vector
	 */
	public double length() {
		return Math.sqrt( x * x + y * y + z * z );
	}
	
	@Override
	public String toString(){
	    return "(" + x + ", " + y + ", " +  z + ")";
	}
	
	public static Vector3 normal( Vector3 x, Vector3 y, Vector3 z ) {
		//N = (V1 - V2)x(V2 - V3)
		Vector3 t1 = x.subtraction( y );
		Vector3 t2 = y.subtraction( z );
		return t1.crossProduct( t2 );
	}

	public static int orientation( Vector3 v1, Vector3 v2 ) {
			PlanPoint p = new PlanPoint( v1.x, v1.y );
			PlanPoint q = new PlanPoint( 0, 0 );
			PlanPoint r = new PlanPoint( v2.x, v2.y);
			return PlanPoint.orientation(p,r,q);
	}

}
