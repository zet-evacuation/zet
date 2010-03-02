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
package de.tu_berlin.math.coga.math.vectormath;

import ds.z.PlanPoint;
import java.text.NumberFormat;
import java.text.ParseException;
import de.tu_berlin.math.coga.common.localization.Localization;

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
	 * @return the cross product of this vector and the vector {@code v}
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
	 * Computes the difference this vector - v and returns the result in a new {@code vector3} object.
	 * @param v the vector which is subtracted
	 * @return the result of the subtraction
	 */
	public Vector3 subtraction( Vector3 v ) {
		double newx = x - v.x;
		double newy = y - v.y;
		double newz = z - v.z;
		return new Vector3( newx, newy, newz );
	}

	/**
	 * Rotates this vector (as point) around a given axis by a given angle.
	 * @param angle the angle by which the point is rotated
	 * @param axis the axis
	 */
	public void rotate( final double angle, final Vector3 axis ) {
		// a temporary vector
		Vector3 tempVec = new Vector3();

		// sine and cosine of the angle
		final double cosTheta = Math.cos( angle * Math.PI / 180.0 );
		final double sinTheta = Math.sin( angle * Math.PI / 180.0 );

		// new x-position
		tempVec.x = ( cosTheta + ( 1 - cosTheta ) * axis.x * axis.x ) * this.x;
		tempVec.x += ( ( 1 - cosTheta ) * axis.x * axis.y - axis.z * sinTheta ) * this.y;
		tempVec.x += ( ( 1 - cosTheta ) * axis.x * axis.z + axis.y * sinTheta ) * this.z;

		// new y-position
		tempVec.y = ( ( 1 - cosTheta ) * axis.x * axis.y + axis.z * sinTheta ) * this.x;
		tempVec.y += ( cosTheta + ( 1 - cosTheta ) * axis.y * axis.y ) * this.y;
		tempVec.y += ( ( 1 - cosTheta ) * axis.y * axis.z - axis.x * sinTheta ) * this.z;

		// new z-position
		tempVec.z = ( ( 1 - cosTheta ) * axis.x * axis.z - axis.y * sinTheta ) * this.x;
		tempVec.z += ( ( 1 - cosTheta ) * axis.y * axis.z + axis.x * sinTheta ) * this.y;
		tempVec.z += ( cosTheta + ( 1 - cosTheta ) * axis.z * axis.z ) * this.z;

		this.x = tempVec.x;
		this.y = tempVec.y;
		this.z = tempVec.z;
	}

	/**
	 * Rotates a vector around an axis.
	 * @param angle the angle by which the point is rotated
	 * @param axis the axis around that the point is rotated
	 * @param oVec the point that is rotated
	 * @return the rotated vector (point)
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
	 * Calculates the euclidian length of the vector.
	 * @return the length of the vector
	 */
	public double length() {
		return Math.sqrt( x * x + y * y + z * z );
	}

	final static NumberFormat nfFloat = Localization.getInstance().getFloatConverter();
	
	@Override
	public String toString(){
		return "(" + nfFloat.format( x ) + "; " + nfFloat.format( y ) + "; " +  nfFloat.format( z ) + ")";
	}

	/**
	 * Reads a vector from a given string. The string must have the same format
	 * as it is printed using the {@link #toString()} method. Note that this depends from
	 * the {@link java.util.Locale} scheme that is selected. The string may be enclosed by
	 * brackets and/or spaces, also the numbers itself may be surrounded by
	 * spaces.
	 * @param value the vector as string
	 * @throws ParseException if an error occurs during parsing
	 */
	public void parse( String value ) throws ParseException {
		final int len = value.length();
		value = value.trim();
		if( value.startsWith( "(" ) )
			value = value.substring( 1 );
		if( value.endsWith( ")" ) )
			value = value.substring( 0, value.length()-1 );
		String a[] = value.split( ";" );
		try {
			x = nfFloat.parse( a[0].trim() ).doubleValue();
			y = nfFloat.parse( a[1].trim() ).doubleValue();
			z = nfFloat.parse( a[2].trim() ).doubleValue();
		} catch( IndexOutOfBoundsException ex ) {
			throw new ParseException( "String does not contain three coordinates.", len );
		}
	}
	
	public static Vector3 normal( Vector3 x, Vector3 y, Vector3 z ) {
		//N = (V1 - V2)x(V2 - V3)
		Vector3 t1 = x.subtraction( y );
		Vector3 t2 = y.subtraction( z );
		return t1.crossProduct( t2 );
	}

	/**
	 * Calculates the orientation between two vectors.
	 * @param v1 the first vector
	 * @param v2 the second vector
	 * @return a value indiciating the orientation between the two vectors
	 */
	public static int orientation( Vector3 v1, Vector3 v2 ) {
		PlanPoint p = new PlanPoint( v1.x, v1.y );
		PlanPoint q = new PlanPoint( 0, 0 );
		PlanPoint r = new PlanPoint( v2.x, v2.y);
		return PlanPoint.orientation(p,r,q);
	}
}
