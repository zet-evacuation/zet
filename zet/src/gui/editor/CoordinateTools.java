/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package gui.editor;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

/** Computes screen coordinates from the model values and vice versa.
 *
 * There are two types of methods that this class offers. The zoom- and
 * zoomInverse-methods <i>scale</i> the object that is given to them to screen
 * size, whereas the translateToScreen / translateToModel methods first
 * <i>copy</i> the given parameter object, <i>translate</i> it to the screen
 * coordinate origin and then apply the appropriate zoom-method.
 *
 * @author Timon Kelter, Jan-Philipp Kappmeier
 */
public class CoordinateTools {
	private static int xOffset = 0;
	private static int yOffset = 0;
	private static double zoomFactor = 0.1;

	/** Private constructor. */
	private CoordinateTools() {
	}

	public static double getZoomFactor() {
		return zoomFactor;
	}

	/** Sets the offsets that will be used in all following conversion operations
	 *
	 * @param x X Offset (in the model coordinate space - THIS IS NO GUI OFFSET)
	 * @param y Y Offset (in the model coordinate space - THIS IS NO GUI OFFSET)
	 */
	public static void setOffsets( int x, int y ) {
		xOffset = -x;
		yOffset = -y;
	}

	public static void setZoomFactor( double zoomFactor ) {
		if( zoomFactor <= 0 )
			throw new java.lang.IllegalArgumentException( "Zoomfactor less than zero" );
		CoordinateTools.zoomFactor = zoomFactor;
	}

	public static Point translateToModel( Point p ) {
		return translateToModel( p.x, p.y );
	}

	public static Point translateToModel( int x, int y ) {
		return new Point( zoomInverse( x ) - xOffset, zoomInverse( y ) - yOffset );
	}

	/** Convenience method, calls {@link #zoomInverse (int )}.
	 * @param length
	 * @return the model length of a given length
	 * @see CoordinateTools#zoomInverse(double)
	 */
	public static int translateToModel( int length ) {
		return zoomInverse( length );
	}

	public static double translateToModelW( int length ) {
		return zoomInverseW( length );
	}

	/**
	 * Returns a NEW  point that represents the specified point on the screen.
	 * @param p the point
	 * @return the point
	 */
	public static Point translateToScreen( Point p ) {
		return zoom( new Point( xOffset + p.x, yOffset + p.y ) );
	}

	/**
	 * Returns a NEW rectangle that represents the specified rectangle on the screen.
	 * @param r the rectangle
	 * @return the rectangle
	 */
	public static Rectangle translateToScreen( Rectangle r ) {
		return zoom( new Rectangle( xOffset + r.x, yOffset + r.y, r.width, r.height ) );
	}

	/**
	 * Returns a NEW polygon that represents a specified polygon on the screen.
	 * @param p the polygon
	 * @return the polygon
	 */
	public static Polygon translateToScreen( Polygon p ) {
		Polygon result = new Polygon( p.xpoints, p.ypoints, p.npoints );
		result.translate( xOffset, yOffset );
		zoom( result );
		return result;
	}

	/** Returns the on-screen length of a line of the given length.
	 * @param length A length in the model measured in millimeters.
	 * @return the on-screen length
	 */
	public static int translateToScreen( double length ) {
		return zoom( length );
	}

	public static double translateToScreenW( double length ) {
		return zoomW( length );
	}

	public static int translateToScreen( int length ) {
		return zoom( length );
	}

	public static Point zoom( Point p ) {
		p.x = zoom( p.x );
		p.y = zoom( p.y );
		return p;
		//return new Point( zoom( p.x ), zoom( p.y ) );
	}

	public static Rectangle zoom( Rectangle r ) {
		// Attention: If we round down 2 times while converting the x or y
		// values we may loose more than a half pixel of exactness, whereas
		// single points, which round each x/y value only 1 time, can have a
		// maximum loss of exactness of 1/2. So, if we don't correct this
		// rounding error here this will yield to painting errors, because
		// some edges may no longer be inside their polygons.

		boolean double_rounding_error_x =
						(Math.abs( (r.x + r.width) - zoom( r.x + r.width ) ) > 0.5);
		boolean double_rounding_error_y =
						(Math.abs( (r.y + r.height) - zoom( r.y + r.height ) ) > 0.5);

		r.x = zoom( r.x );
		r.y = zoom( r.y );
		r.width = zoom( r.width );
		r.height = zoom( r.height );

		if( double_rounding_error_x )
			r.width++;
		if( double_rounding_error_y )
			r.height++;

		return r;
		//return new Rectangle( zoom( r.x ), zoom( r.y ), zoom( r.width ), zoom( r.height ) );
	}

	public static Polygon zoom( Polygon r ) {
		for( int i = 0; i < r.npoints; i++ ) {
			r.xpoints[i] = (int) Math.round( r.xpoints[i] * zoomFactor );
			r.ypoints[i] = (int) Math.round( r.ypoints[i] * zoomFactor );
		}
		return r;
	}

	public static int zoom( double val ) {
		return (int) Math.round( val * zoomFactor );
	}

	public static double zoomW( double val ) {
		return val * zoomFactor;
	}

	/** Inverse method for {@link CoordinateTools#zoom(double)}. Except for
	 * rounding faults {@code zoomInverse (zoom (x)) == x} should hold true.
	 *
	 * @param val
	 * @return
	 */
	public static int zoomInverse( double val ) {
		return (int) Math.round( val / zoomFactor );
	}

	public static double zoomInverseW( double val ) {
		return val / zoomFactor;
	}
	private static double pictureZoomFactor = 1.;

	public static double getPictureZoomFactor() {
		return pictureZoomFactor;
	}

	public static void setPictureZoomFactor( double pictureZoomFactor ) {
		if( pictureZoomFactor <= 0 )
			throw new java.lang.IllegalArgumentException( "Zoomfactor less than zero" );
		CoordinateTools.pictureZoomFactor = pictureZoomFactor;
	}
}
