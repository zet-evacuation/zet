/**
 * Class J3dVisualizationPanel
 * Erstellt 25.04.2008, 01:01:24
 */
package gui.visualization.draw.ca;

import opengl.framework.abs.AbstractDrawable;
import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.ca.GLCAFloorControl;
import gui.visualization.control.ca.GLIndividualControl;
import gui.visualization.control.ca.GLRoomControl;

import gui.visualization.util.VisualizationConstants;
import java.util.List;
import javax.media.opengl.GLAutoDrawable;

/**
 *  @author Jan-Philipp Kappmeier
 */

//public class GLCAFloor extends AbstractDrawable<CullingShapeCube, GLRoom, GLCAFloorControl, GLRoomControl> {
public class GLCAFloor extends AbstractDrawable<GLRoom, GLCAFloorControl, GLRoomControl> {
	private static double FLOOR_HEIGHT = VisualizationOptionManager.getFloorHeight() * VisualizationConstants.SIZE_MULTIPLICATOR;
	private static double FLOOR_DISTANCE = VisualizationOptionManager.getFloorDistance() * VisualizationConstants.SIZE_MULTIPLICATOR;
	private List<GLIndividualControl> individuals;
	private int floorID;
    
    public GLCAFloor(GLCAFloorControl control ) {
		super(control );
//		super(control, new CullingShapeCube() );
	    this.position.x = control.getXPosition();
	    this.position.y = control.getYPosition();
	    this.position.z = (control.getFloorNumber() - 1) * (FLOOR_HEIGHT + FLOOR_DISTANCE);
			floorID = control.getFloorNumber();
	}
	
	@Override
  public void update() { }
	
	public void setIndividuals( List<GLIndividualControl> li ) {
		individuals = li;	
	}

	@Override
	public String toString() {
		return "GLCAFloor";
	}
	
	@Override
	public void performDrawing( GLAutoDrawable drawable ) {
		super.performDrawing( drawable );
		for( GLIndividualControl ic : control.getIndividualControls() ) {
			//individual.performDrawing( drawable );
			if( ic.onFloor() == floorID )
				ic.getView().performDrawing( drawable );
		}
		//GLIndividualControl ic = individuals.get(0);
		
	}
}
