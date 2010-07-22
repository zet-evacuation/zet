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

import ds.ca.Room;
import gui.visualization.control.AbstractZETVisualizationControl;
import gui.visualization.control.GLControl.CellInformationDisplay;
import gui.visualization.draw.ca.GLCAFloor;
import io.visualization.CAVisualizationResults;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

//public class GLCAFloorControl extends AbstractControl<GLCAFloor, Integer, CAVisualizationResults, GLRoom, GLRoomControl, GLControl> {
public class GLCAFloorControl extends AbstractZETVisualizationControl<GLRoomControl, GLCAFloor, GLCellularAutomatonControl> {
	private HashMap<ds.ca.Room, GLRoomControl> roomControls;
    
  private double xPosition = 0.0d;
	private double yPosition = 0.0d;
	
	private int floorNumber = 0;
	
	public GLCAFloorControl( CAVisualizationResults caVisResults, Collection<Room> roomsOnTheFloor, int floorID, GLCellularAutomatonControl glControl ) {
		super( glControl );
		
		xPosition = caVisResults.get(floorID).x;
		yPosition = caVisResults.get(floorID).y;
		roomControls = new HashMap<ds.ca.Room, GLRoomControl>();
	    
		this.floorNumber = floorID;

		for( Room room : roomsOnTheFloor ) {
		  GLRoomControl roomControl = new GLRoomControl( caVisResults, room, this, glControl );
			roomControls.put(room, roomControl);
		  add( roomControl );
		}
				
		setView( new GLCAFloor( this ) );
		for( GLRoomControl room : this )
			view.addChild( room.getView() );
	}
		
	/**
	 * Returns the offset of this floor. The offset is in real (z-format) coordinates
	 * @return the y offset
	 */
	public double getXPosition() {
		return xPosition;
	}

	/**
	 * Returns the offset of this floor. The offset is in real (z-format) coordinates
	 * @return the x offset
	 */
	public double getYPosition() {
		return yPosition;
	}

	/**
	 * Returns the floor number of the floor. This corresponds to the level, the
	 * lowest floor is identified by 0.
	 * @return the floor number
	 */
	public int getFloorNumber() {
		return floorNumber;
	}
	
	GLRoomControl getRoomControl( ds.ca.Room room ) {
		return roomControls.get(room);
	}
	
	void setPotentialDisplay( CellInformationDisplay potentialDisplay ) {
		for( GLRoomControl roomControl : roomControls.values() )
			roomControl.setPotentialDisplay(potentialDisplay);
	}

	public List<GLIndividualControl> getIndividualControls() {
		return mainControl.getIndividualControls();
	} 		
}
