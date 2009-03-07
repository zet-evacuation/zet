package gui.visualization.control.ca;

import ds.ca.Cell;
import ds.ca.Room;
import gui.visualization.control.AbstractControl;
import gui.visualization.control.GLControl;
import gui.visualization.control.GLControl.CellInformationDisplay;
import gui.visualization.draw.ca.GLCell;
import gui.visualization.draw.ca.GLRoom;
import io.visualization.CAVisualizationResults;
import java.util.HashMap;

public class GLRoomControl extends AbstractControl<GLRoom, Room, CAVisualizationResults, GLCell, GLCellControl> {

	private HashMap<ds.ca.Cell, GLCellControl> cellControls;

	private GLCAFloorControl glCAFloorControlObject;  // the corresponding GLCAFloorControl of this object
    
	private double xPosition;
	private double yPosition;

	public GLRoomControl( CAVisualizationResults caVisResults, Room room, GLCAFloorControl glCAFloorControl, GLControl glControl) {
		super( room, caVisResults, glControl );
		xPosition = caVisResults.get(room).x;
		yPosition = caVisResults.get(room).y;
		this.glCAFloorControlObject = glCAFloorControl;
		cellControls = new HashMap<ds.ca.Cell, GLCellControl>();
		
		for( Cell cell : room.getAllCells() ) {
			GLCellControl cellControl = new GLCellControl( caVisResults, cell, this, glControl );
		    cellControls.put(cell, cellControl);
			add(cellControl);	    
		}

		this.setView( new GLRoom( this ) );
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
	
	GLCellControl getCellControl(ds.ca.Cell cell){
		return cellControls.get(cell);
	}
	
	public int getWidth() {
		return getControlled().getWidth();
	}
	
	public int getHeight() {
		return getControlled().getHeight();
		
	}
	
	/**
	 * Returns the corresponding GLCAFloorControl-Object which created this GLRoomControl Object.
	 * @return The corresponding GLCAFloorControl-Object which created this GLRoomControl Object.
	 */
	public GLCAFloorControl getGLCAFloorControl()
	{
		return this.glCAFloorControlObject;
	}

	void setPotentialDisplay(CellInformationDisplay potentialDisplay){
        for(GLCellControl cellControl : cellControls.values()){
            cellControl.setPotentialDisplay(potentialDisplay);
        }
    }
}