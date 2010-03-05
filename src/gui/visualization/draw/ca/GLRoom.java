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
package gui.visualization.draw.ca;

import gui.visualization.VisualizationOptionManager;
import gui.visualization.control.ca.GLRoomControl;
import gui.visualization.util.VisualizationConstants;
import javax.media.opengl.GL;
import opengl.drawingutils.GLVector;
import opengl.framework.abs.AbstractDrawable;

//public class GLRoom extends AbstractDrawable<GLCell, GLRoomControl, GLCellControl> {
public class GLRoom extends AbstractDrawable<GLCell, GLRoomControl> {

	private GLVector ul;	// upper left
	private GLVector ur;	// upper right
	private GLVector ll;	// lower left
	private GLVector lr;	// lower right
//public class GLRoom extends AbstractDrawable<CullingShapeCube, GLCell, GLRoomControl, GLCellControl> {

	public GLRoom( GLRoomControl control ) {
		super( control );
//			super(control, new CullingShapeCube() );
		this.position.x = control.getXPosition() * VisualizationConstants.SIZE_MULTIPLICATOR;
		this.position.y = control.getYPosition() * VisualizationConstants.SIZE_MULTIPLICATOR;
		if( VisualizationOptionManager.showSpaceBetweenCells() ) {
			ul = new GLVector( 0 * VisualizationConstants.SIZE_MULTIPLICATOR, 0 * VisualizationConstants.SIZE_MULTIPLICATOR, -1 * VisualizationConstants.SIZE_MULTIPLICATOR );
			ur = new GLVector( control.getWidth() * 40 * VisualizationConstants.SIZE_MULTIPLICATOR, 0 * VisualizationConstants.SIZE_MULTIPLICATOR, -1 * VisualizationConstants.SIZE_MULTIPLICATOR );
			ll = new GLVector( 0 * VisualizationConstants.SIZE_MULTIPLICATOR, control.getHeight() * -40 * VisualizationConstants.SIZE_MULTIPLICATOR, -1 * VisualizationConstants.SIZE_MULTIPLICATOR );
			lr = new GLVector( control.getWidth() * 40 * VisualizationConstants.SIZE_MULTIPLICATOR, control.getHeight() * -40 * VisualizationConstants.SIZE_MULTIPLICATOR, -1 * VisualizationConstants.SIZE_MULTIPLICATOR );
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

