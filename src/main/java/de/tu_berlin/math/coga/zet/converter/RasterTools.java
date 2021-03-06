/* zet evacuation tool copyright © 2007-20 zet evacuation team
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

import java.util.LinkedList;
import java.util.List;

/**
 * A class that provides methods used during conversion of cellular automaton.
 * @author Jan-Philipp Kappmeier
 */
public final class RasterTools {

	/**
	 * No instantiating of {@code ConversationTools} possible.
	 */
	private RasterTools() {
	}

	/**
	 * Converts coordinates of the z-format in millimeters to coordinates on
	 * the raster of a room.
	 * @param polyCoord A coordinate, given in millimeters
	 * @param offset The offset of a room
	 * @param raster
	 * @return The coordinate of the cell that contains the given coordinate.
	 * If you supply an x-coordinate, you will get the x-coordinate of the
	 * cell; if you supply a y-coordinate, you will get the y-coordinate
	 * of the cell.
	 */
	public final static int polyCoordToRasterCoord( int polyCoord, int offset, RoomRaster<?> raster ) {
		polyCoord -= offset;
		return (int) (Math.floor( polyCoord / (double) raster.getRaster() ));
	}


	/**
	 * Checks if a square at a given position exists and adds it to a list of
	 * adjacent suqares.
	 * @param <T> the type of squares
	 * @param adjacentSquares the list of already known adjacent squares
	 * @param raster the square that is tested
	 * @param a the {@code x}-position of that the square should be
	 * @param b the {@code y}-position of that the square should be
	 */
	private static <T extends RoomRasterSquare> void addIfExistent( LinkedList<T> adjacentSquares, RoomRaster<T> raster, int a, int b ) {
		if( raster.isValid( a, b ) && raster.getSquare( a, b ).isAccessible() ) {
			T square = raster.getSquare( a, b );
			adjacentSquares.add( square );
		}
	}

	/**
	 * Gets all squares that are adjacent to an edge that lies
	 * on the raster (i.e. for e={(x1,y1), (x2,y2)}, x1==x2 or
	 * y1==y2 holds). The squares are guaranteed to be in the
	 * following order:
	 * Let e be an edge from (x1, y1) to (y1, y2) that lies on
	 * the raster. For simplicity, assume that y1==y2 and that
	 * x1 &lt; x2. Otherwise, relabel the coordinate system and/or
	 * relabel x1 and x2.
	 * Then the squares that lie above the edge (i.e. those that
	 * have a y-coordinate &gt; y1) will be at the beginning of
	 * the list in increasing order with respect to their
	 * x-coordinate (i.e. from left to right). They are
	 * followed by the squares below the edge (i.e. those that
	 * have a y-coordinate &lt; y1) in increasing order (i.e. also
	 * from left to right).
	 *
	 * @param <T>
	 * @param edge An edge of a room
	 * @param raster
	 * @return A list of all squares adjacent to the edge.
	 */
	public static <T extends RoomRasterSquare> List<T> getSquaresAlongEdge( de.zet_evakuierung.model.PlanEdge edge, RoomRaster<T> raster ) {
		LinkedList<T> adjacentSquares = new LinkedList<>();

		adjacentSquares.addAll( getSquaresAboveEdge( edge, raster ) );
		adjacentSquares.addAll( getSquaresBelowEdge( edge, raster ) );
		adjacentSquares.addAll( getSquaresLeftOfEdge( edge, raster ) );
		adjacentSquares.addAll( getSquaresRightOfEdge( edge, raster ) );

		return adjacentSquares;
	}

	/**
	 * Given a raster and an edge on the raster, this method returns all squares
	 * in the raster that have at least two common points with the edge and whose
	 * upper left corner has a y-coordinate that is strictly greater than the
	 * maximum of the y-coordinates of the endpoints of the edge.
	 *
	 * @param <T> A {@code RoomRasterSquare}-Type
	 * @param edge An edge that lies on the {@code raster}
	 * @param raster a rastering of a room.
	 * @return All adjacent raster squares above this edge. If the edge is parallel to
	 * the y-axis, the returned list is empty.
	 */
	public final static <T extends RoomRasterSquare> List<T> getSquaresAboveEdge( de.zet_evakuierung.model.PlanEdge edge, RoomRaster<T> raster ) {
		int rasterX1 = RasterTools.polyCoordToRasterCoord( edge.boundLeft(), raster.getXOffset(), raster );
		int rasterX2 = RasterTools.polyCoordToRasterCoord( edge.boundRight(), raster.getXOffset(), raster );

		int rasterY1 = RasterTools.polyCoordToRasterCoord( edge.boundUpper(), raster.getYOffset(), raster );
		int rasterY2 = RasterTools.polyCoordToRasterCoord( edge.boundLower(), raster.getYOffset(), raster );

		LinkedList<T> adjacentSquares = new LinkedList<>();

		if( rasterX1 == rasterX2 )
			return adjacentSquares;

		if( rasterY1 != rasterY2 )
			throw new IllegalArgumentException( "The edge does not lie on the raster!" );

		for( int x = rasterX1; x < rasterX2; x++ )
			addIfExistent( adjacentSquares, raster, x, rasterY1 - 1 );

		return adjacentSquares;
	}

	/**
	 * Given a raster and an edge on the raster, this method returns all squares
	 * in the raster that have at least two common points with the edge and whose
	 * upper left corner has a y-coordinate that is equal to the
	 * maximum of the y-coordinates of the endpoints of the edge.
	 *
	 * @param <T> A {@code RoomRasterSquare}-Type
	 * @param edge An edge that lies on the {@code raster}
	 * @param raster a rastering of a room.
	 * @return All adjacent raster squares below this edge. If the edge is parallel to
	 * the y-axis, the returned list is empty.
	 */
	public final static <T extends RoomRasterSquare> List<T> getSquaresBelowEdge( de.zet_evakuierung.model.PlanEdge edge, RoomRaster<T> raster ) {
		int rasterX1 = RasterTools.polyCoordToRasterCoord( edge.boundLeft(), raster.getXOffset(), raster );
		int rasterX2 = RasterTools.polyCoordToRasterCoord( edge.boundRight(), raster.getXOffset(), raster );

		int rasterY1 = RasterTools.polyCoordToRasterCoord( edge.boundUpper(), raster.getYOffset(), raster );
		int rasterY2 = RasterTools.polyCoordToRasterCoord( edge.boundLower(), raster.getYOffset(), raster );

		LinkedList<T> adjacentSquares = new LinkedList<>();

		if( rasterX1 == rasterX2 )
			return adjacentSquares;

		if( rasterY1 != rasterY2 )
			throw new IllegalArgumentException( "The edge does not lie on the raster!" );

		for( int x = rasterX1; x < rasterX2; x++ )
			addIfExistent( adjacentSquares, raster, x, rasterY1 );

		return adjacentSquares;
	}

	/**
	 * Given a raster and an edge on the raster, this method returns all squares
	 * in the raster that have at least two common points with the edge and whose
	 * upper left corner has an x-coordinate that is strictly greater than the
	 * maximum of the x-coordinates of the endpoints of the edge.
	 *
	 * @param <T> A {@code RoomRasterSquare}-Type
	 * @param edge An edge that lies on the {@code raster}
	 * @param raster a rastering of a room.
	 * @return All adjacent raster squares left of this edge. If the edge is parallel to
	 * the x-axis, the returned list is empty.
	 */
	public final static <T extends RoomRasterSquare> List<T> getSquaresLeftOfEdge( de.zet_evakuierung.model.PlanEdge edge, RoomRaster<T> raster ) {
		int rasterX1 = RasterTools.polyCoordToRasterCoord( edge.boundLeft(), raster.getXOffset(), raster );
		int rasterX2 = RasterTools.polyCoordToRasterCoord( edge.boundRight(), raster.getXOffset(), raster );

		int rasterY1 = RasterTools.polyCoordToRasterCoord( edge.boundUpper(), raster.getYOffset(), raster );
		int rasterY2 = RasterTools.polyCoordToRasterCoord( edge.boundLower(), raster.getYOffset(), raster );

		LinkedList<T> adjacentSquares = new LinkedList<>();

		if( rasterY1 == rasterY2 )
			return adjacentSquares;

		if( rasterX1 != rasterX2 )
			throw new IllegalArgumentException( "The edge does not lie on the raster!" );

		for( int y = rasterY1; y < rasterY2; y++ )
			addIfExistent( adjacentSquares, raster, rasterX1 - 1, y );

		return adjacentSquares;
	}

	/**
	 * Given a raster and an edge on the raster, this method returns all squares
	 * in the raster that have at least two common points with the edge and whose
	 * upper left corner has an x-coordinate that is equal to the
	 * maximum of the x-coordinates of the endpoints of the edge.
	 *
	 * @param <T> A {@code RoomRasterSquare}-Type
	 * @param edge An edge that lies on the {@code raster}
	 * @param raster a rastering of a room.
	 * @return All adjacent raster squares right of this edge. If the edge is parallel to
	 * the x-axis, the returned list is empty.
	 */
	public final static <T extends RoomRasterSquare> List<T> getSquaresRightOfEdge( de.zet_evakuierung.model.PlanEdge edge, RoomRaster<T> raster ) {
		int rasterX1 = RasterTools.polyCoordToRasterCoord( edge.boundLeft(), raster.getXOffset(), raster );
		int rasterX2 = RasterTools.polyCoordToRasterCoord( edge.boundRight(), raster.getXOffset(), raster );

		int rasterY1 = RasterTools.polyCoordToRasterCoord( edge.boundUpper(), raster.getYOffset(), raster );
		int rasterY2 = RasterTools.polyCoordToRasterCoord( edge.boundLower(), raster.getYOffset(), raster );

		LinkedList<T> adjacentSquares = new LinkedList<>();

		if( rasterY1 == rasterY2 )
			return adjacentSquares;

		if( rasterX1 != rasterX2 )
			throw new IllegalArgumentException( "The edge does not lie on the raster!" );

		for( int y = rasterY1; y < rasterY2; y++ )
			addIfExistent( adjacentSquares, raster, rasterX1, y );

		return adjacentSquares;
	}

}
