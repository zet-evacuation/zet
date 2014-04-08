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
 * ZToGraphRasterSquare.java
 * 
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.zet.converter.RoomRasterSquare;
import ds.graph.Node;
import de.tu_berlin.coga.zet.model.AssignmentArea;
import de.tu_berlin.coga.zet.model.Room;

/**
 * The class {@code ZToGraphRasterSquare} represents a raster square used to
 * store a rasterized room in a raster for the conversion from Z to Z-Graph. 
 * It extends the {@code RoomRaster} class by adding a node attribute that is
 * used to define a mapping of raster squares to nodes in a graph.
 */
public class ZToGraphRasterSquare extends RoomRasterSquare {
	/** The node this raster square is mapped to. */
	private Node node = null;
	/** Private boolean flag telling whether there could be people on this square. */
	private boolean isSource;
	/** A marker to mark processed raster squares. */
	private boolean marked = false;

	/**
	 * Creates a new {@code RasterSquare} connected to the room it lies in
	 * and to a matrix that represents this room and includes this square.
	 * The parameter {@code raster} is the width / height of the square.
	 * @param r the room the square belongs to in the original Z-Plan.
	 * @param column the column of this square in the corresponding room matrix.
	 * @param row the row of this square in the corresponding room matrix.
	 * @param raster the width / height of the square.
	 */
	public ZToGraphRasterSquare( Room r, int column, int row, int raster ) {
		super( r, column, row, raster );
		isSource = false;
		check();
	}

	/**
	 * Sets the node this square is mapped to.
	 * @param node the node this square is mapped to.
	 */
	public void setNode( Node node ) {
		this.node = node;
	}

	/**
	 * Returns the node this square is mapped to.
	 * @return the node this square is mapped to.
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * Returns the {@code x}-coordinate of the upper left corner of this raster square in the global coordinate system.
	 * @return the {@code x}-coordinate of the upper left corner of this raster in the global coordinate system.
	 */
	public int getXOffset() {
		return super.getX();
	}

	/**
	 * Returns the {@code y}-coordinate of the upper left corner of this raster square in the global coordinate system.
	 * @return the {@code y}-coordinate of the upper left corner of this raster in the global coordinate system.
	 */
	public int getYOffset() {
		return super.getY();
	}

	/**
	 * Returns whether this raster square is marked.
	 * @return whether this raster square is marked.
	 */
	public boolean isMarked() {
		return marked;
	}

	/**
	 * Marks this raster square.
	 */
	public void mark() {
		marked = true;
	}

	/**
	 * Unmarks this raster square.
	 */
	public void unmark() {
		marked = false;
	}

	/**
	 * Adds the information about sources
	 * to the raster square.
	 */
	private void check() {
		Room r = (Room) getPolygon();
		for( AssignmentArea assignmentArea : r.getAssignmentAreas() )
			isSource = isSource || (isAccessible() && assignmentArea.contains( this.getSquare() ));
	}

	/**
	 * Returns whether there can be people on this square.
	 * @return whether there can be people on this square.
	 */
	public boolean isSource() {
		return isSource;
	}
}
