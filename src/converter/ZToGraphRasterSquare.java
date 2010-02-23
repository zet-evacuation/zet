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

package converter;

import converter.RoomRasterSquare.Property;
import ds.graph.Node;
import ds.z.AssignmentArea;
import ds.z.Room;

/**
 * The class <code>ZToGraphRasterSquare</code> represents a raster square used to
 * store a rasterized room in a raster for the conversion from Z to Z-Graph. 
 * It extends the <code>RoomRaster</code> class by adding a node attribute that is
 * used to define a mapping of raster squares to nodes in a graph.
 */
public class ZToGraphRasterSquare extends RoomRasterSquare {

	/**
	 * The node this raster square is mapped to.
	 */
	private Node node = null;
	
	/**
	 * Private boolean flag telling whether there 
	 * could be people on this square.
	 */
	private boolean isSource;
	
	/**
	 * A marker to mark processed raster squares.
	 */
	private boolean marked = false;
	
	/**
	 * Creates a new <code>RasterSquare</code> connected to the room it lies in
	 * and to a matrix that represents this room and includes this square.
	 * The parameter <code>raster</code> is the width / height of the square.
	 * @param r the room the square belongs to in the original Z-Plan.
	 * @param column the column of this square in the corresponding room matrix.
	 * @param row the row of this square in the corresponding room matrix.
	 * @param raster the width / height of the square.
	 */
	public ZToGraphRasterSquare( Room r, int column, int row, int raster ) {
		super(r, column, row, raster);
	}
	
	/**
	 * Sets the node this square is mapped to.
	 * @param node the node this square is mapped to.
	 */
	public void setNode(Node node){
		this.node = node;
	}
	
	/**
	 * Returns the node this square is mapped to.
	 * @return the node this square is mapped to.
	 */
	public Node getNode(){
		return node;
	}
	
	@Override
    /**
     * Returns the speed factor of this square. The speed factor describes how fast
     * the square can be traversed (averaged). It lies between zero and one and is 
     * one if the square has no delay factor.
     */
	public double getSpeedFactor(){
		return super.getSpeedFactor();
	}
	
	/**
	 * Returns whether (the majority of) this square is accessible in the original 
	 * Z-Plan. 
	 * @return whether (the majority of) this square is accessible in the original 
	 * Z-Plan. 
	 */
	public boolean isAccessible(){
		return super.accessible();
	}	
	
	/**
	 * Returns the <code>x</code>-coordinate of the upper left corner of
	 * this raster square in the global coordinate system.
	 * @return the <code>x</code>-coordinate of the upper left corner of
	 *         this raster in the global coordinate system.
	 */
	public int getXOffset() {
		return super.getX();
	}
	
	/**
	 * Returns the <code>y</code>-coordinate of the upper left corner of
	 * this raster square in the global coordinate system.
	 * @return the <code>y</code>-coordinate of the upper left corner of
	 *         this raster in the global coordinate system.
	 */
	public int getYOffset(){
		return super.getY();
	}
	
	/**
	 * Returns whether this raster square is marked.
	 * @return whether this raster square is marked.
	 */
	boolean isMarked(){
		return marked;
	}
	
	/**
	 * Marks this raster square.
	 */
	void mark(){
		marked = true;
	}
	
	/**
	 * Unmarks this raster square.
	 */
	void unmark(){
		marked = false;
	}
	
	/**
	 * Needed for support of the <code>isSquare</code>
	 * boolean flag.
	 */
	@Override
	protected void callAtConstructorStart(){
		super.callAtConstructorStart();
		isSource = false;
	}
	
	/**
	 * Adds the information about sources
	 * to the raster square.
	 */
	@Override
	protected void check() {
		super.check();
		Room r = (Room) getPolygon();
		for (AssignmentArea assignmentArea : r.getAssignmentAreas()) {
			isSource = isSource || (accessible() && assignmentArea.contains(this.getSquare()));
		}
	}
	
	/**
	 * Returns whether there can be people on this square.
	 * @return whether there can be people on this square.
	 */
	protected boolean isSource(){
		return isSource;
	}

}
