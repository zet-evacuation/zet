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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

/**
 * Class GLCAFloor
 * Created 25.04.2008, 01:01:24
 */
package gui.visualization.draw.ca;

import org.zetool.opengl.framework.abs.AbstractDrawable;
import gui.visualization.control.ca.GLCAFloorControl;
import gui.visualization.control.ca.GLIndividualControl;
import java.util.List;
import javax.media.opengl.GL;

/**
 *  @author Jan-Philipp Kappmeier
 */
public class GLCAFloor extends AbstractDrawable<GLRoom, GLCAFloorControl> {

	private List<GLIndividualControl> individuals;
	private int floorID;

	public GLCAFloor( GLCAFloorControl control ) {
		super( control );
		this.position.x = control.getXPosition();
		this.position.y = control.getYPosition();
		this.position.z = control.getZPosition();
		floorID = control.getFloorNumber();
	}

	@Override
	public void update() {
	}

	public void setIndividuals( List<GLIndividualControl> li ) {
		individuals = li;
	}

	@Override
	public String toString() {
		return "GLCAFloor";
	}

	@Override
	public void performDrawing( GL gl ) {
		super.performDrawing( gl );
		
		for( GLIndividualControl ic : control.getIndividualControls() )
			if( ic.onFloor() == floorID )
				ic.getView().performDrawing( gl );
	}
}
