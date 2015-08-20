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
package gui.visualization.draw.ca;

import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.ca.GLRoomControl;
import javax.media.opengl.GL;
import org.zetool.opengl.drawingutils.GLVector;
import org.zetool.opengl.framework.abs.AbstractDrawable;

/**
 * Draws a room. That is, it draws a ground rectangle for the room.
 * @author Jan-Philipp Kappmeier
 */
public class GLRoom extends AbstractDrawable<GLCell, GLRoomControl> {
	/** Upper left coordinate of the room. */
	private GLVector ul;
	/** Upper right coordinate of the room. */
	private GLVector ur;
	/** Lower left coordinate of the room. */
	private GLVector ll;
	/** Lower right coordinate of the room. */
	private GLVector lr;

	public GLRoom( GLRoomControl control ) {
		super( control );
		this.position.x = control.getXPosition();
		this.position.y = control.getYPosition();
		if( VisualizationOptionManager.showSpaceBetweenCells() ) {
			ul = new GLVector( 0, 0, -0.1 );
			ur = new GLVector( control.getWidth(), 0 , -0.1 );
			ll = new GLVector( 0, -control.getHeight(), -0.1 );
			lr = new GLVector( control.getWidth(), -control.getHeight(), -0.1 );
		}
	}

	@Override
	public void update() {
	}

	@Override
	public void performDrawing( GL gl ) {
		super.performDrawing( gl );
		if( VisualizationOptionManager.showSpaceBetweenCells() ) {
			// draw a floor
			VisualizationOptionManager.getCellSeperationColor().draw( gl );
			gl.glBegin( GL.GL_QUADS );
			gl.glNormal3d( 0, 0, 1 );
			ul.draw( gl );
			ur.draw( gl );
			lr.draw( gl );
			ll.draw( gl );
			gl.glEnd();
		}
	}

	@Override
	public String toString() {
		return "GLRoom";
	}
}