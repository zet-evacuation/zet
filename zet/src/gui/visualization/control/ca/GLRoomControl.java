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
package gui.visualization.control.ca;

import ds.ca.Cell;
import ds.ca.Room;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.control.GLControl.CellInformationDisplay;
import gui.visualization.draw.ca.GLRoom;
import io.visualization.CAVisualizationResults;
import java.util.HashMap;

//public class GLRoomControl extends AbstractControl<GLRoom, Room, CAVisualizationResults, GLCell, GLCellControl, GLControl> {
public class GLRoomControl extends AbstractZETVisualizationControl<GLCellControl, GLRoom, GLCAControl> {

	private HashMap<ds.ca.Cell, GLCellControl> cellControls;
	private GLCAFloorControl glCAFloorControlObject;  // the corresponding GLCAFloorControl of this object
	private double xPosition;
	private double yPosition;
	Room controlled;

	public GLRoomControl( CAVisualizationResults caVisResults, Room room, GLCAFloorControl glCAFloorControl, GLCAControl glControl ) {
		super( glControl );
		controlled = room;
		xPosition = caVisResults.get( room ).x;
		yPosition = caVisResults.get( room ).y;
		this.glCAFloorControlObject = glCAFloorControl;
		cellControls = new HashMap<ds.ca.Cell, GLCellControl>();

		for( Cell cell : room.getAllCells() ) {
			GLCellControl cellControl = new GLCellControl( caVisResults, cell, this, glControl );
			cellControls.put( cell, cellControl );
			add( cellControl );
		}
		this.setView( new GLRoom( this ) );
		for( GLCellControl cell : this )
			view.addChild( cell.getView() );
	}

	/**
	 * Returns the offset of this room. The offset is in real (z-format) coordinates
	 * @return the y offset
	 */
	public double getXPosition() {
		return xPosition;
	}

	/**
	 * Returns the offset of this room. The offset is in real (z-format) coordinates
	 * @return the x offset
	 */
	public double getYPosition() {
		return -yPosition;
	}

	GLCellControl getCellControl( ds.ca.Cell cell ) {
		return cellControls.get( cell );
	}

	public int getWidth() {
		return controlled.getWidth();
	}

	public int getHeight() {
		return controlled.getHeight();

	}

	/**
	 * Returns the corresponding GLCAFloorControl-Object which created this GLRoomControl Object.
	 * @return The corresponding GLCAFloorControl-Object which created this GLRoomControl Object.
	 */
	public GLCAFloorControl getGLCAFloorControl() {
		return this.glCAFloorControlObject;
	}

	void setPotentialDisplay( CellInformationDisplay potentialDisplay ) {
		for( GLCellControl cellControl : cellControls.values() )
			cellControl.setPotentialDisplay( potentialDisplay );
	}
}
