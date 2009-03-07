package gui.visualization.control.ca;

import ds.ca.Room;
import gui.visualization.control.AbstractControl; 
import gui.visualization.control.GLControl;
import gui.visualization.control.GLControl.CellInformationDisplay;
import gui.visualization.draw.ca.GLCAFloor; 
import gui.visualization.draw.ca.GLIndividual;
import gui.visualization.draw.ca.GLRoom;
import io.visualization.CAVisualizationResults;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class GLCAFloorControl extends AbstractControl<GLCAFloor, Integer, CAVisualizationResults, GLRoom, GLRoomControl> {
	
	private HashMap<ds.ca.Room, GLRoomControl> roomControls;
    
  private double xPosition = 0.0d;
	private double yPosition = 0.0d;
	
	private int floorNumber = 0;
	
	public GLCAFloorControl( CAVisualizationResults caVisResults, Collection<Room> roomsOnTheFloor, int floorID, GLControl glControl ) {
		super( floorID, caVisResults, glControl );
		
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
		for( GLRoomControl roomControl : roomControls.values() ) {
			roomControl.setPotentialDisplay(potentialDisplay);
		}
	}

	public List<GLIndividualControl> getIndividualControls() {
		return mainControl.getIndividualControls();
	} 		
}
