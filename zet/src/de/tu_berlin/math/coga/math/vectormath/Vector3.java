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

package de.tu_berlin.math.coga.math.vectormath;

import de.tu_berlin.coga.common.localization.LocalizationManager;
import de.tu_berlin.math.coga.zet.ZETLocalization2;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Implements a three dimensional vector.
 * @author Jan-Philipp Kappmeier
 */
public class Vector3 implements Cloneable {
	/** The first or {@code x}-coordinate of the vector. */
	public double x = 0;
	/** The second or {@code y}-coordinate of the vector. */
	public double y = 0;
	/** The third or {@code z}-coordinate of the vector. */
	public double z = 0;

	/**
	 * Initializes a zero vector in the origin.
	 */
	public Vector3() {
		x = 0;
		y = 0;
		z = 0;
	}

	/**
	 * Creates a vector that is a copy of another one.
	 * @param v the original vector
	 */
	public Vector3( Vector3 v ) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	/**
	 * Initializes a 2-dimensional vector, that means the third coordinate remains
	 * zero.
	 * @param x the first or {@code x}-coordinate
	 * @param y the second or {@code y}-coordinate
	 */
	public Vector3( double x, double y ) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	/**
	 * Initializes the vector by three real coordinates.
	 * @param x the first or {@code x}-coordinate
	 * @param y the second or {@code y}-coordinate
	 * @param z the third or {@code z}-coordinate
	 */
	public Vector3( double x, double y, double z ) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Creates a copy of this vector.
	 *
	 * @return a copy of the vector
	 */
	@Override
	public Vector3 clone() {
		return new Vector3( x, y, z );
	}

	/**
	 * Returns the dimension of the vector.
	 * @return the dimension of the vector
	 */
	public int getDimension() {
		return 3;
	}

	/**
	 * Inverts the direction of the vector. Equals the multiplication with the
	 * scalar value -1.
	 */
	public void invert() {
		x = -x;
		y = -y;
		z = -z;
	}
	
	/**
	 * Sets all three coordinates at once
	 * @param x the first or {@code x}-coordinate
	 * @param y the second or {@code y}-coordinate
	 * @param z the third or {@code z}-coordinate
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
	 * <p>Computes the cross product of this vector and another vector.</p>
	 * <p>The product of two vectors {@code a = (a_x, a_y, a_z)} and
	 * {@code b = (b_x, b_y, b_z)} is defined as
	 * {@code a \cross b = (a_yb_z - a_zb_y, a_zb_x - a_xb_z, a_xb_y - a_yb_x)}.
	 * </p>
	 * @param v the vector
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
	 * <p>Computes the dot, inner or scalar product of this vector and another
	 * vector.</p>
	 * <p>
	 * The scalar product of two vectors {@code a = (a_x, a_y, a_z)} and
	 * {@code b = (b_x, b_y, b_z)} is defined as
	 * {@code a * b = (a_xb_y + a_yb_y + a_zb_z)}. Note that the result is a
	 * scalary value.
	 * </p>
	 * <p>The multiplication with a scalary value, which returns a vector again,
	 * is defined in {@link #scalarMultiplicateTo(double)} and
	 * {@link #scalarMultiplicate(double) </p>.}
	 * @param v the other vector
	 * @return the dot product
	 */
	public final double dotProduct( Vector3 v ) {
		return x * v.x + y * v.y + z * v.z;
	}
	
	/**
	 * @see #dotProduct(de.tu_berlin.math.coga.math.vectormath.Vector3)
	 * @param v the other vector
	 * @return the scalar product
	 */
	public final double scalarProduct( Vector3 v ) {
		return dotProduct( v );
	}

	/**
	 * Returns a copy of this vector whose coordinates are multiplicated by a
	 * scalar value.
	 * @param scalar the scalar value
	 * @return the vector multiplicated with a scalar
	 */
	public final Vector3 scalarMultiplicate( double scalar ) {
		return new Vector3( x * scalar, y * scalar, z * scalar );
	}

	/**
	 * Multiplicates this vector with a scalar value.
	 * @param scalar the scalar value
	 */
	public void scalarMultiplicateTo( double scalar ) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
	}

	/**
	 * Returns a copy of this vector to which another vector is added.
	 * @param v the vector
	 * @return the sum of this vector and the other vector
	 */
	public Vector3 add( Vector3 v ) {
		return new Vector3( x + v.x, y + v.y, z + v.z );
	}

	/**
	 * Adds a {@code Vector3} to this vector.
	 * @param v the added vector
	 */
	public final void addTo( Vector3 v ) {
		x += v.x;
		y += v.y;
		z += v.z;
	}

	/**
	 * Computes the difference this vector - v and returns the result in a new
	 * {@code vector3} object.
	 * @param v the vector which is subtracted
	 * @return the result of the sub
	 */
	public final Vector3 sub( Vector3 v ) {
		return new Vector3( x - v.x, y - v.y, z - v.z );
	}

	/**
	 * Subtracts a {@code Vector3} from this vector.
	 * @param v the subtracted vector
	 */
	public void subTo( Vector3 v ) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}
	
	/**
	 * Rotates this vector (as point) around a given axis by a given angle.
	 * @param angle the angle by which the point is rotated
	 * @param axis the axis
	 */
	public void rotate( final double angle, final Vector3 axis ) {
		final Vector3 tempVec = rotateVector( angle, axis, this );
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
		Vector3 tempVec = new Vector3(); // a temporary vector

		// sine and cosine of the angle
		final double cosTheta = Math.cos( angle * Math.PI / 180.0 );
		final double sinTheta = Math.sin( angle * Math.PI / 180.0 );

		// new x-position
		tempVec.x = ( cosTheta + ( 1 - cosTheta ) * axis.x * axis.x ) * oVec.x;
		tempVec.x += ( ( 1 - cosTheta ) * axis.x * axis.y - axis.z * sinTheta ) * oVec.y;
		tempVec.x += ( ( 1 - cosTheta ) * axis.x * axis.z + axis.y * sinTheta ) * oVec.z;

		// new y-position
		tempVec.y = ( ( 1 - cosTheta ) * axis.x * axis.y + axis.z * sinTheta ) * oVec.x;
		tempVec.y += ( cosTheta + ( 1 - cosTheta ) * axis.y * axis.y ) * oVec.y;
		tempVec.y += ( ( 1 - cosTheta ) * axis.y * axis.z - axis.x * sinTheta ) * oVec.z;

		// new z-position
		tempVec.z = ( ( 1 - cosTheta ) * axis.x * axis.z - axis.y * sinTheta ) * oVec.x;
		tempVec.z += ( ( 1 - cosTheta ) * axis.y * axis.z + axis.x * sinTheta ) * oVec.y;
		tempVec.z += ( cosTheta + ( 1 - cosTheta ) * axis.z * axis.z ) * oVec.z;

		return tempVec;
	}
	
	
	/**
	 * Normalizes the vector. The length of a normalized vector is 1.
	 */
	public void normalize() {
		final double len = length();
		if( len != 0 ) {// only dvide if len is not equal to zero
			x /= len;
			y /= len;
			z /= len;
		}
	}

	/**
	 * Computes the euclidian length of the vector.
	 * @return the length of the vector
	 */
	public double length() {
		return Math.sqrt( x * x + y * y + z * z );
	}

	/**
	 * Returns a {@link String}-representation of the vector that is of the type
	 * (x; y; z). The numbers in the representation are formatted in the current
	 * locale.
	 * @return a string representation of the vector
	 */
	@Override
	public String toString(){
		return toString( LocalizationManager.getSingleton().getFloatConverter() );
	}

	/**
	 * Returns a {@link String}-representation of the vector that is of the type
	 * (x; y; z). The numbers in the representation are formatted in the submitted
	 * number format.
	 * @param nf the number format used to format the numbers
	 * @return a string representation of the vector
	 */
	public String toString( NumberFormat nf ) {
		return "(" + nf.format( x ) + "; " + nf.format( y ) + "; " +  nf.format( z ) + ")";
	}

	/**
	 * Reads a vector from a given string. The string must have the same format
	 * as it is printed using the {@link #toString()} method. Note that this
	 * depends from the {@link java.util.Locale} scheme that is selected. The
	 * string may be enclosed by brackets and/or spaces, also the numbers itself
	 * may be surrounded by spaces.
	 * @param value the vector as string
	 * @throws ParseException if an error occurs during parsing
	 */
	public void parse( String value ) throws ParseException {
		parse( value, LocalizationManager.getSingleton().getFloatConverter() );
	}

	/**
	 * Reads a vector from a given string. The string must have the same format as
	 * it is printed using the {@link #toString()} method. The format of the
	 * numbers has to fit to the given number format.
	 * @param value the vector as string
	 * @param nf the number format used for the numbers in the string
	 * @throws ParseException if an error occurs during parsing
	 */
	public void parse( String value, NumberFormat nf ) throws ParseException {
		final int len = value.length();
		value = value.trim();
		if( value.startsWith( "(" ) )
			value = value.substring( 1 );
		if( value.endsWith( ")" ) )
			value = value.substring( 0, value.length()-1 );
		String a[] = value.split( ";" );
		try {
			x = nf.parse( a[0].trim() ).doubleValue();
			y = nf.parse( a[1].trim() ).doubleValue();
			z = nf.parse( a[2].trim() ).doubleValue();
		} catch( IndexOutOfBoundsException ex ) {
			throw new ParseException( "String does not contain three coordinates.", len );
		}
	}

	/**
	 * Computes a normal to a plane defined by three vectors or points. It is
	 * computed using the formula {@code n = (x - y) \cross (y - z)}, where
	 * \cross denotes the {@link #crossProduct(de.tu_berlin.math.coga.math.vectormath.Vector3)}.
	 * @param x the first vector
	 * @param y the second vector
	 * @param z the third vector
	 * @return the normal to the three vectors.
	 */
	public static Vector3 normal( Vector3 x, Vector3 y, Vector3 z ) {
		return x.sub( y ).crossProduct( y.sub( z ) );
	}

	/**
	 * Computes the orientation between two vectors. The third coordinate of the
	 * vectors is ignored.
	 * @param v1 the first vector
	 * @param v2 the second vector
	 * @return a value indicating the orientation between the two vectors
	 */
	public static int orientation( Vector3 v1, Vector3 v2 ) {
		return (int) Math.signum( v2.x * v1.y - v2.y * v1.x );
	}

	/**
	 * Computes the orientation of three points.
	 * @param p the first vector
	 * @param q the second vector
	 * @param r the second vector
	 * @return a value indicating the orientation between the two vectors
	 */
	public static int orientation( Vector3 p, Vector3 q, Vector3 r ) {
		final Vector3 u = p.sub( q );
		final Vector3 v = r.sub( q );
		return (int) Math.signum( v.x * u.y - v.y * u.x ); // return sign of determinancy
	}

	public boolean equals( Vector3 v, double eps ) {
		return Math.abs( x - v.x ) < eps && Math.abs( y - v.y) < eps && Math.abs( z - v.z ) < eps;
	}
}
