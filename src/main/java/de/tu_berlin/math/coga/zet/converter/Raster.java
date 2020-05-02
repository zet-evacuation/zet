/* zet evacuation tool copyright (c) 2007-14 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package de.tu_berlin.math.coga.zet.converter;

import de.tu_berlin.math.coga.zet.ZETLocalization2;
import de.zet_evakuierung.model.PlanPolygon;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import de.zet_evakuierung.util.ConversionTools;

/**
 * The {@code Raster} class provides basic rasterization of polygonal objects. These
 * object have to be of the type {@link de.zet_evakuierung.model.PlanPolygon}. The bounding of the polygon is
 * divided into squares, which can be inside or outside the polygon or intersect, if they
 * are not completely inside or outside.
 * <p>The type of the squares is defined through the generic parameter, which has to be
 * of the type {@link RasterSquare} or any subtypes. A {@code RasterSquare</codes> does all
 * checks itself and calculates all necessary values. Thus, new features can be provided
 * by a subclass.</p>
 * @param <T> the type of the {@link RasterSquare} objects that is used for rasterization
 * @param <P> the type of {@link PlanPolygon} that is rasterized
 * @author Jan-Philipp Kappmeier
 */
public class Raster<T extends RasterSquare > {

	/** The class-type of the raster squares. */
	private Class<T> squareClassType;
	/** The class-type of the rasterized polygon. */
	//protected Class<P> polygonClassType;
	/** *  Array containing the squares of the rasterized {@link de.zet_evakuierung.model.PlanPolygon} */
	protected T[][] rasterSquares;
	/** A list of squares intersecting the polygon. */
	private ArrayList<T> insideSquares;
	/** *  The rasterized {@link de.zet_evakuierung.model.PlanPolygon}*/
	protected PlanPolygon p;
	/** The width of the array for the squares. */
	private int width = 0;
	/** The height of the array for the squares. */
	private int height = 0;
	/** The size of the raster used during rasterization process. Defines width and height of a square. */
	private int raster = 400;

	public Raster( Class<T> squareClassType, /*Class<P> polygonClassType, */PlanPolygon p ) {
		this.squareClassType = squareClassType;
		//this.polygonClassType = polygonClassType;
		this.p = p;
	}

	/**
	 * Performs the rasterization of a simple polygon defined by {@link de.zet_evakuierung.model.PlanPolygon} inside the range of a
	 * bounding box of the polygon.
	 * <p>During the rasterization an array representing the individual squares of the grid is
	 * created. The array stores {@link RasterSquare}-objects representing all necessary information
	 * needed for rasterization. In addition it is possible not only to retrieve a special square
	 * at an indicated position but to receive all squares not lying outside the borders of the
	 * polygon.</p>
	 * <p>The grid pattern can be set to arbitrary positive values, only limited by space and time needed
	 * for storing the objects and calculation.</p>
	 * @param squareClassType the type of the class of the raster squares
	 * @param polygonClassType the type of the polygons that are rasterized
	 * @param p the polygon
	 * @param raster the size of the raster in millimeter
	 * @throws java.lang.IllegalArgumentException if raster is negative or zero
	 * @author Jan-Philipp Kappmeier
	 */
	public Raster( Class<T> squareClassType, /*Class<P> polygonClassType,*/ PlanPolygon p, int raster ) throws java.lang.IllegalArgumentException {
		if( raster < 1 ) {
			throw new java.lang.IllegalArgumentException (ZETLocalization2.loc.getString ("converter.NegativeRasterException"));
		}
		this.squareClassType = squareClassType;
		//this.polygonClassType = polygonClassType;
		setRaster( raster );
		this.p = p;
	}

	/**
	 * Performs the rasterization of a simple polygon defined by {@link de.zet_evakuierung.model.PlanPolygon} inside the range of a
	 * bounding box of the polygon.
	 * <p>During the rasterization an array representing the individual squares of the grid is
	 * created. The array stores {@link RasterSquare}-objects representing all necessary information
	 * needed for rasterization. In addition it is possible not only to retrieve a special square
	 * at an indicated position but to receive all squares not lying outside the borders of the
	 * polygon.</p>
	 * <p>The grid pattern can be set to arbitrary positive values, only limited by space and time needed
	 * for storing the objects and calculation.</p>
	 * @param squareClassType the type of the class of the raster squares
	 * @param polygonClassType the type of the polygons that are rasterized
	 * @param p the polygon
	 * @param raster the size of the raster in meter
	 * @throws java.lang.IllegalArgumentException if raster is negative or zero
	 * @author Jan-Philipp Kappmeier
	 */
	public Raster( Class<T> squareClassType, /*Class<P> polygonClassType,*/ PlanPolygon p, double raster ) throws java.lang.IllegalArgumentException {
		if( de.zet_evakuierung.util.ConversionTools.floatToInt( raster ) < 1 ) {
			throw new java.lang.IllegalArgumentException (ZETLocalization2.loc.getString ("converter.NegativeRasterException"));
		}
		this.squareClassType = squareClassType;
		//this.polygonClassType = polygonClassType;
		setRaster( raster );
		this.p = p;
	}

	/**
	 * Returns the number of columns created during the rasterization.
	 * @return the number of columns created during the rasterization
	 */
	public int getColumnCount() {
		return width;
	}

	/**
	 * Returns the grid size of the raster in millimeter
	 * @return the grid size of the raster
	 */
	public int getRaster() {
		return this.raster;
	}

	/**
	 * Returns the grid size of the raster in meter
	 * @return the grid size of the raster
	 */
	public double getRasterMeter() {
		return de.zet_evakuierung.util.ConversionTools.roundScale3( raster );
	}

	/**
	 * Returns the number of rows created during the rasterization
	 * @return the number of rows created during the rasterization
	 */
	public int getRowCount() {
		return height;
	}

	/**
	 * Returns whether (x,y) is a valid position in the raster,
	 * i.e. whether the square at row x and column y exists.
	 * @param x x-part of the asked position
	 * @param y y-part of the asked position
	 * @return whether (x,y) is a valid position in the raster,
	 * i.e. whether the square at row x and column y exists.
	 */
	public boolean isValid(int x, int y){
		return (x>=0 && x < width && y >= 0 && y < height);
	}

	/**
	 * Returns the square at the specified position.
	 * @param x the column of the square in the array of squares
	 * @param y the row of the square in the array of squares
	 * @return the square at the specified position
	 */
	public T getSquare( int x, int y ) {
		return rasterSquares[x][y];
	}

	/**
	 * Returns the squares intersecting the polygon.
	 * @return the squares intersecting the polygon
	 */
	public List<T> insideSquares() {
		return Collections.unmodifiableList( insideSquares );
	//return insideSquares;
	}

	/**
	 * Performs the rasterization of a polygon. The results are stored in some arrays
	 * and lists for further use.
	 * <p>The polygon and the raster have to be set before calling the method.</p>
	 */
	// TODO exception
	public void rasterize(){
		// Calculate Array width and height (TODO better calculation to avoid +1 in some cases)
		//width = (int) Math.floor( p.getWidth() / raster ) + 1;
		//height = (int) Math.floor( p.getHeight() / raster ) + 1;

		//p.recomputeBounds();

		width = (int) Math.ceil( p.getWidth() / raster );
		height = (int) Math.ceil(p.getHeight() / raster) ;

		// Initialize Array
		rasterSquares = (T[][]) Array.newInstance( squareClassType, width, height );
		insideSquares = new ArrayList<T>( ( width * height ) );  // initialize with maximal size. fast but wastes space.

		// Rasterize one row
		for( int j = 0; j < height; j++ ) {
			for( int i = 0; i < width; i++ ) {
				// Create square polygon to check
				T square = null;
				try {
					square = squareClassType.getConstructor( PlanPolygon.class, int.class, int.class, int.class ).newInstance( p, i, j, raster );
				} catch( java.lang.NoSuchMethodException e ) {
					System.err.println("NoSuchMethodException in Raster.java at creation of squares in rasterize().");
				} catch( java.lang.InstantiationException e ) {
					System.err.println("InstantiationException in Raster.java at creation of squares in rasterize().");
				} catch( java.lang.IllegalAccessException e ) {
					System.err.println("IllegalAccessException in Raster.java at creation of squares in rasterize().");
				} catch( java.lang.reflect.InvocationTargetException e ) {
					System.err.println("InvocationTargetException in Raster.java at creation of squares in rasterize().");
				}
				rasterSquares[i][j] = square;

				if( square.getIntersectType() != RasterSquare.FieldIntersectType.Outside ) {
					insideSquares.add( square );
				}
			}
		}
	}

	/**
	 * Returns a String containing the information about every {@code RasterSquare}
	 * of this {@code Raster}.
	 * @return a String containing the information about every {@code RasterSquare}
	 * of this {@code Raster}.
	 */
	@Override
	public String toString() {
		String result = "";
		for( int i = 0; i < rasterSquares.length; i++ ) {
			for( int j = 0; j < rasterSquares[i].length; j++ ) {
				result += rasterSquares[i][j].toString() + "\t";
			}
			result += "\n";
		}
		result += "\n";
		return result;
	}

	/**
	 * Sets the raster size used for rasterization. The size is given in millimeters,
	 * which is the base unit.
	 * @param raster the size
	 * @see util.ConversionTools
	 */
	public void setRaster( int raster ) {
		this.raster = raster;
	}

	/**
	 * Sets the raster size used for rasterization. The size is given in meter. The
	 * size is converted to the base unit millimeter.
	 * @param raster the size
	 * @see util.ConversionTools
	 */
	public void setRaster( double raster ) {
		this.raster = ConversionTools.floatToInt( raster );
	}

	/**
	 * Returns the {@code x}-coordinate of the upper left corner of this raster in the global coordinate system.
	 * @return the {@code x}-coordinate of the upper left corner of this raster in the global coordinate system.
	 */
	public int getXOffset() {
		return p.getxOffset();
	}

	/**
	 * Returns the {@code y}-coordinate of the upper left corner of this raster in the global coordinate system.
	 * @return the {@code y}-coordinate of the upper left corner of this raster in the global coordinate system.
	 */
	public int getYOffset() {
		return p.getyOffset();
	}
}
