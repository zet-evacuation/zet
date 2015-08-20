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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package gui.visualization.control.ca;

import ds.ca.evac.EvacCell;
import ds.ca.evac.Room;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.control.ZETGLControl.CellInformationDisplay;
import gui.visualization.draw.ca.GLRoom;
import io.visualization.EvacuationSimulationResults;
import java.util.HashMap;

public class GLRoomControl extends AbstractZETVisualizationControl<GLCellControl, GLRoom, GLCellularAutomatonControl> {
	private HashMap<ds.ca.evac.EvacCell, GLCellControl> cellControls;
	private GLCAFloorControl glCAFloorControlObject;  // the corresponding GLCAFloorControl of this object
	private double xPosition;
	private double yPosition;
	Room controlled;

	public GLRoomControl( EvacuationSimulationResults caVisResults, Room room, GLCAFloorControl glCAFloorControl, GLCellularAutomatonControl glControl ) {
		super( glControl );
		System.out.println( "Create room " + room.getIdentificationForStatistic() );
		controlled = room;
		xPosition = caVisResults.get( room ).x * mainControl.scaling;
		yPosition = caVisResults.get( room ).y * mainControl.scaling;
		this.glCAFloorControlObject = glCAFloorControl;
		cellControls = new HashMap<>();

		for( EvacCell cell : room.getAllCells() ) {
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

	GLCellControl getCellControl( ds.ca.evac.EvacCell cell ) {
		return cellControls.get( cell );
	}

	public double getWidth() {
		return controlled.getWidth() * mainControl.scaling * 400;
	}

	public double getHeight() {
		return controlled.getHeight() * mainControl.scaling * 400;
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
