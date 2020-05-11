/* zet evacuation tool copyright © 2007-20 zet evacuation team
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
package gui.visualization.draw.ca;

import gui.visualization.control.ca.GLCellControl;
import gui.visualization.control.ZETGLControl.CellInformationDisplay;
import gui.visualization.VisualizationOptionManager;
import javax.media.opengl.GL2;
import org.zetool.opengl.drawingutils.GLColor;
import org.zetool.opengl.drawingutils.GLVector;
import org.zetool.opengl.framework.abs.AbstractDrawable;
import org.zetool.common.util.Direction8;

public class GLCell extends AbstractDrawable<GLCell, GLCellControl> {
	private static GLVector ul = null;	// upper Left
	private static GLVector ur;	// upper Right
	private static GLVector ll;	// lower Left
	private static GLVector lr;	// lower Right
	protected GLColor color;
	private GLColor defaultColor;
	protected static GLColor wallColor = VisualizationOptionManager.getCellWallColor();

	public GLCell( GLCellControl control ) {
    //this( control, VisualizationOptionManager.getCellFloorColor() );
    this( control, VisualizationOptionManager.getCellWallColor() );
  }

	public GLCell( GLCellControl control, GLColor color ) {
		super( control );
		this.position.x = control.getXPosition();
		this.position.y = control.getYPosition();
		if( ul == null ) {
			ul = new GLVector( control.getOffset(), -control.getOffset(), 0 );
			ur = new GLVector( control.getWidth(), -control.getOffset(), 0 );
			ll = new GLVector( control.getOffset(), -control.getWidth(), 0 );
			lr = new GLVector( control.getWidth(), -control.getWidth(), 0 );
		}
		this.color = color;
		this.defaultColor = color;
	}

	@Override
	public void performDrawing( GL2 gl ) {
		if( VisualizationOptionManager.smoothCellVisualization() ) {
			boolean lighting = gl.glIsEnabled( GL2.GL_LIGHTING );
			gl.glBegin( GL2.GL_QUADS );
			gl.glNormal3d( 0, 0, 1 );
		 	getControl().mixColorWithNeighbours( Direction8.TopLeft ).draw( gl, lighting );
			ul.draw( gl );
			getControl().mixColorWithNeighbours( Direction8.TopRight ).draw( gl, lighting );
			ur.draw( gl );
			getControl().mixColorWithNeighbours( Direction8.DownLeft ).draw( gl, lighting );
			lr.draw( gl );
			getControl().mixColorWithNeighbours( Direction8.DownRight ).draw( gl, lighting );
			ll.draw( gl );
			gl.glEnd();
		} else {
			color.draw( gl );
			gl.glBegin( GL2.GL_QUADS );
			gl.glNormal3d( 0, 0, 1 );
			ul.draw( gl );
			ur.draw( gl );
			lr.draw( gl );
			ll.draw( gl );
			gl.glEnd();
		}
	}

	/**
	 * Updates the graphical representation of the cell. The current floor color
	 * is calculated.
	 */
	protected void updateFloorColor() {
		if( control.getDisplayMode() == CellInformationDisplay.NoPotential )
			this.color = getDefaultColor();
		else
			this.color = potentialToColor( control.getCellInformation( control.getDisplayMode() ), control.getMaxCellInformation( control.getDisplayMode() ), VisualizationOptionManager.getCellInformationLowColor( control.getDisplayMode() ), VisualizationOptionManager.getCellInformationHighColor( control.getDisplayMode() ) );
	}

	protected final GLColor potentialToColor( long potential, long maxPotential, GLColor lowColor, GLColor highColor ) {
		if( control.isPotentialValid() )
			return lowColor.blend( highColor, potential / (double)maxPotential );
		else
			return VisualizationOptionManager.getInvalidPotentialColor();
	}

	public final GLColor getDefaultColor() {
		return defaultColor;
	}

	@Override
	public void update() {
		updateFloorColor();
	}

	@Override
	public String toString() {
		return "GLCell";
	}

	public GLColor getColor() {
		return color;
	}
}

