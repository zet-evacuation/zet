/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/**
 * Class GLCAFloor
 * Created 25.04.2008, 01:01:24
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
	private List<GLIndividualControl> individuals;
	private int floorID;
    
    public GLCAFloor(GLCAFloorControl control ) {
		super(control );
//		super(control, new CullingShapeCube() );
	    this.position.x = control.getXPosition();
	    this.position.y = control.getYPosition();
	    this.position.z = (control.getFloorNumber() - 1) * VisualizationOptionManager.getFloorDistance() * VisualizationConstants.SIZE_MULTIPLICATOR;
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
