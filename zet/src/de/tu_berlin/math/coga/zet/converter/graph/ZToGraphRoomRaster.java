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
/*
 * ZToGraphRoomRaster.java
 * 
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.zet.converter.RoomRaster;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.coga.zet.model.Room;

/**
 * The class {@code ZToGraphRoomRaster} represents a rastered room.
 * It extends the class {@code RoomRaster} by specifying the generic type
 * of raster squares to {@code ZToGraphRasterSquare}.
 *
 */
public class ZToGraphRoomRaster extends RoomRaster<ZToGraphRasterSquare> {
	/**
	 * Creates a new {@code ZToGraphRoomRaster} object connected with the
	 * room {@code room}.
	 * @param room the room connected to the new object.
	 */
	public ZToGraphRoomRaster( Room room ) {
		super( ZToGraphRasterSquare.class, room );
	}

	@Override
	/**
	 * Returns the {@code ZToGraphRasterSquare} at position {@code (x,y)}.
	 * @return the {@code ZToGraphRasterSquare} at position {@code (x,y)}.
	 */
	public ZToGraphRasterSquare getSquare( int x, int y ) {
		return super.getSquare( x, y );
	}

	/**
	 * Returns the {@code ZToGraphRasterSquare} that includes the point (x,y)
	 * (global coordinates).
	 * @return  the {@code ZToGraphRasterSquare} that includes the point (x,y)
	 * (global coordinates).
	 */
	public ZToGraphRasterSquare getSquareWithGlobalCoordinates( int x, int y ) {
		int localX = x - this.getXOffset();
		int localY = y - this.getYOffset();
		int rasterX = (int) Math.floor( ((double) localX / (double) getRaster()) );
		int rasterY = (int) Math.floor( ((double) localY / (double) getRaster()) );
		return getSquare( rasterX, rasterY );
	}

	@Override
	/**
	 * Returns the number of columns of the {@code RoomRaster}.
	 * @return the number of columns of the {@code RoomRaster}.
	 */
	public int getColumnCount() {
		return super.getColumnCount();
	}

	@Override
	/**
	 * Returns the number of rows of the {@code RoomRaster}.
	 * @return the number of rows of the {@code RoomRaster}.
	 */
	public int getRowCount() {
		return super.getRowCount();
	}

	/**
	 * Sets the mark flag of all squares in this rastered room to {@code false}.
	 */
	public void unmarkAllSquares() {
		for( int i = 0; i < getColumnCount(); i++ )
			for( int j = 0; j < getRowCount(); j++ )
				getSquare( i, j ).unmark();
	}

	@Override
	/**
	 * Returns a String containing a description of the underlying {@code RoomRaster}.
	 * @return a String containing a description of the underlying {@code RoomRaster}.
	 */
	public String toString() {
		String result = "";
		for( int i = 0; i < rasterSquares[0].length + 2; i++ )
			result += "-";
		result += "\n";
		for( int j = 0; j < rasterSquares[0].length; j++ ) {
			result += "|";
			for( int i = 0; i < rasterSquares.length; i++ ) {
				ZToGraphRasterSquare square = getSquare( i, j );
				if( square.isAccessible() ) {
					Node node = square.getNode();
					if( node != null ) {
						if( node.id() < 10 )
							result += " ";
						result += node.toString() + " ";
					} else
						result += "  ";
				} else
					result += " X ";
			}
			result += "|\n";
		}
		for( int i = 0; i < rasterSquares.length + 2; i++ )
			result += "-";
		result += "\n";
		return result;
	}

	public String superToString() {
		String result = "";
		for( int i = 0; i < rasterSquares[0].length + 2; i++ )
			result += "-";
		result += "\n";
		for( int j = 0; j < rasterSquares[0].length; j++ ) {
			result += "|";
			for( int i = 0; i < rasterSquares.length; i++ )
				if( getSquare( i, j ).isAccessible() )
					if( getSquare( i, j ).isExit() )
						result += "e";
					else
						if( getSquare( i, j ).isSave() )
							result += "s";
						else
							if( getSquare( i, j ).isSource() )
								result += "a";
							else
								if( getSquare( i, j ).getSpeedFactor() < 1.0 )
									result += "d";
								else
									result += " ";
				else
					result += "X";
			result += "|\n";
		}
		for( int i = 0; i < rasterSquares.length + 2; i++ )
			result += "-";
		result += "\n";
		return result;
	}
}
