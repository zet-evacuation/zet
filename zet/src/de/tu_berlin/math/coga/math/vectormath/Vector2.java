package de.tu_berlin.math.coga.math.vectormath;


import org.zetool.common.localization.LocalizationManager;
import de.tu_berlin.coga.geom.Point;
import org.zetool.math.Conversion;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Implements a two dimensional vector.
 * @author Jan-Philipp Kappmeier
 */
public class Vector2 implements Cloneable, Point {
	/** The first or {@code x}-coordinate of the vector. */
	private double x = 0;
	/** The second or {@code y}-coordinate of the vector. */
	private double y = 0;

	/**
	 * Initializes a zero vector in the origin.
	 */
	public Vector2() {
		x = 0;
		y = 0;
	}

	/**
	 * Creates a vector that is a copy of another one.
	 * @param v the original vector
	 */
	public Vector2( Vector2 v ) {
		x = v.x;
		y = v.y;
	}

	/**
	 * Initializes a 2-dimensional vector by two real coordinates.
	 * @param x the first or {@code x}-coordinate
	 * @param y the second or {@code y}-coordinate
	 */
	public Vector2( double x, double y ) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates a copy of this vector.
	 * @return a copy of this vector
	 */
	@Override
	public Vector2 clone() {
		return new Vector2( x, y );
	}

	/**
	 * Returns the dimension of the vector.
	 * @return the dimension of the vector
	 */
	public int getDimension() {
		return 2;
	}

	/**
	 * Inverts the direction of the vector. Equals the multiplication with the
	 * scalar value -1.
	 */
	public void invert() {
		x = -x;
		y = -y;
	}

	/**
	 * Sets both coordinates at once.
	 * @param x the first or {@code x}-coordinate
	 * @param y the second or {@code y}-coordinate
	 */
	public void setLocation( double x, double y ) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets both coordinates at once.
	 * @param p a point whose coordinates are used
	 */
	public void setLocation( Vector2 p ) {
		setLocation( p.x, p.y );
	}

	/**
	 * Computes the distance between two points.
	 * @param x1 the {@code x}-coordinate of the first point
	 * @param y1 the {@code y}-coordinate of the first point
	 * @param x2 the {@code x}-coordinate of the second point
	 * @param y2 the {@code y}-coordinate of the second point
	 * @return the distance between the two points
	 */
	public static double distance( double x1, double y1, double x2, double y2 ) {
		return Math.sqrt( (x2-x1) * (x2-x1) + (y2-y1) * (y2-y1) );
	}

	/**
	 * Computes the distance between this point and another point.
	 * @param v the other point
	 * @return the distance between this point and the other point
	 */
	public double distance( Vector2 v ) {
		return distance( x, y, v.x, v.y );
	}

	public void translate( double x, double y ) {
		this.x += x;
		this.y += y;
	}

	public double getX() {
		return x;
	}

	/**
	 * Sets only a new x coordinate.
	 * @param x the new value
	 */
	public void setX( double x ) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	/**
	 * Sets only a new y coordinate.
	 * @param y the new value
	 */
	public void setY( double y ) {
		this.y = y;
	}

	/**
	 * <p>Computes the cross product of this vector and another vector.</p>
	 * <p>Note, that the cross product in general computes an orthogonal vector
	 * to {@code n-1} {@code n}-dimensional vectors. This is not possible for one
	 * 2-dimensional vector. This method implicitly assumes the third coordinate
	 * to be zero. In that case the usual 3-dimensional cross product returns a
	 * value in the third coordinate, whose absolute value equals the volume of
	 * the parallelogram defined by the two 2-dimensional vectors.</p>
	 * <p>The product of two vectors {@code a = (a_x, a_y, 0)} and
	 * {@code b = (b_x, b_y, 0)} is
	 * {@code a \cross b = (0, 0, c)}, where {@code |c|} is the volume of the
	 * parallelogram.</p>
	 * @param v the vector
	 * @return the volume of the paralellogram defined by this vector and the passed one.
	 */
	public double crossProduct( Vector2 v ) {
		return Math.abs( Vector2.normal( this, v ).z );
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
	public final double dotProduct( Vector2 v ) {
		return x * v.x + y * v.y;
	}

	/**
	 * @see #dotProduct(de.tu_berlin.math.coga.math.vectormath.Vector3)
	 * @param v the other vector
	 * @return the scalar product
	 */
	public final double scalarProduct( Vector2 v ) {
		return dotProduct( v );
	}

	/**
	 * Returns a copy of this vector whose coordinates are multiplicated by a
	 * scalar value.
	 * @param scalar the scalar value
	 * @return the vector multiplicated with a scalar
	 */
	public final Vector2 scalarMultiplicate( double scalar ) {
		return new Vector2( x * scalar, y * scalar );
	}

	/**
	 * Multiplicates this vector with a scalar value.
	 * @param scalar the scalar value
	 */
	public void scalarMultiplicateTo( double scalar ) {
		x *= scalar;
		y *= scalar;
	}

	/**
	 * Returns a copy of this vector to which another vector is added.
	 * @param v the vector
	 * @return the sum of this vector and the other vector
	 */
	public final Vector2 add( Vector2 v ) {
		return new Vector2( x + v.x, y + v.y );
	}

	/**
	 * Adds a {@code Vector2} to this vector.
	 * @param v the added vector
	 */
	public final void addTo( Vector2 v ) {
		x += v.x;
		y += v.y;
	}

	/**
	 * Computes the difference this vector - v and returns the result in a new
	 * {@code vector2} object.
	 * @param v the vector which is subtracted
	 * @return the result of the sub
	 */
	public final Vector2 sub( Vector2 v ) {
		return new Vector2( x - v.x, y - v.y );
	}

	/**
	 * Subtracts a {@code Vector2} from this vector.
	 * @param v the subtracted vector
	 */
	public void subTo( Vector2 v ) {
		x -= v.x;
		y -= v.y;
	}

	/**
	 * Normalizes the vector. The length of a normalized vector is 1.
	 */
	public void normalize() {
		final double len = length();
		if( len != 0 ) {// only dvide if len is not equal to zero
			x /= len;
			y /= len;
		}
	}

	/**
	 * Computes the euclidian length of the vector.
	 * @return the length of the vector
	 */
	public double length() {
		return Math.sqrt( x * x + y * y );
	}

	/**
	 * Returns a {@link String}-representation of the vector that is of the type
	 * (x; y; z). The numbers in the representation are formatted in the current
	 * locale.
	 * @return a string representation of the vector
	 */
	@Override
	public String toString(){
		return toString( LocalizationManager.getManager().getFloatConverter() );
	}

	/**
	 * Returns a {@link String}-representation of the vector that is of the type
	 * (x; y; z). The numbers in the representation are formatted in the submitted
	 * number format.
	 * @param nf the number format used to format the numbers
	 * @return a string representation of the vector
	 */
	public String toString( NumberFormat nf ) {
		return "(" + nf.format( x ) + "; " + nf.format( y ) + ")";
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
		parse( value, LocalizationManager.getManager().getFloatConverter() );
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
	 * @return the normal to the three vectors.
	 */
	public static Vector3 normal( Vector2 x, Vector2 y ) {
		return new Vector3( x.x, x.y ).crossProduct( new Vector3( y.x, y.y ) );
	}

	/**
	 * Computes the orientation between two vectors.
	 * @param v1 the first vector
	 * @param v2 the second vector
	 * @return a value indicating the orientation between the two vectors in the plane
	 */
	public static int orientation( Vector2 v1, Vector2 v2 ) {
		return (int) Math.signum( v2.x * v1.y - v2.y * v1.x );
	}

	/**
	 * Computes the orientation of three points.
	 * @param p the first vector
	 * @param q the second vector
	 * @param r the third vector
	 * @return the value indicates the orientation between the two vectors
	 */
	public static int orientation( Vector2 p, Vector2 q, Vector2 r ) {
		final Vector2 u = p.sub( q );
		final Vector2 v = r.sub( q );
		return (int) Math.signum( v.x * u.y - v.y * u.x ); // return sign of determinancy
	}

	public static double orientationE( Vector2 p, Vector2 q, Vector2 r ) {
		final Vector2 u = p.sub( q );
		final Vector2 v = r.sub( q );
		return v.x * u.y - v.y * u.x; // return determinancy
	}

	/**
	 * Creates a vector orthogonal to the given vector. (By rotating the vector
	 * 90 degrees counter clockwise)
	 * @return an orthogonal vector
	 */
	public Vector2 orthogonal() {
		return new Vector2( y, -x );
	}

	/**
	 * Calculates the smaller angle between to vectors a and b, going from a to b.
	 * Lies always between 0 and 90 !
	 * Uses the dot product to calculate the cosine,
	 * the angle is then calculated with the arcus cosine.
	 * @param a a vector.
	 * @param b another vector.
	 * @return the smaller angle between the two vectors.
	 */
	final public double getAngleBetween( Vector2 a, Vector2 b ) {
		final double cosine = (a.dotProduct( b ) / a.length()) / b.length();
		final double angle = Math.acos( cosine ) / Conversion.ANGLE2DEG;
		return angle > 90.0 ? 180 - angle : angle;
	}
}
