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

/**
 * GLCell.java
 */

package gui.visualization.draw.ca;

import gui.visualization.control.ca.GLCellControl;
import gui.visualization.control.GLControl.CellInformationDisplay;
import gui.visualization.VisualizationOptionManager;
import javax.media.opengl.GL;
import opengl.drawingutils.GLColor;
import opengl.drawingutils.GLVector;
import opengl.framework.abs.AbstractDrawable;
import de.tu_berlin.math.coga.common.util.Direction;
import gui.visualization.control.ca.GLCellularAutomatonControl;

public class GLCell extends AbstractDrawable<GLCell, GLCellControl> {
	// Vorlaeufige Konstanten bis Verwaltungsklasse fertig // TODO
	private static GLVector ul = null;	// upper Left
	private static GLVector ur;	// upper Right
	private static GLVector ll;	// lower Left
	private static GLVector lr;	// lower Right
	protected GLColor color;
	private GLColor defaultColor;
	protected static GLColor wallColor = VisualizationOptionManager.getCellWallColor();

	public GLCell( GLCellControl control ) {
		super( control );
		this.position.x = control.getXPosition() * GLCellularAutomatonControl.sizeMultiplicator;
		this.position.y = control.getYPosition() * GLCellularAutomatonControl.sizeMultiplicator;
		if( ul == null )
			if( VisualizationOptionManager.showSpaceBetweenCells() ) {
				ul = new GLVector( 1 * GLCellularAutomatonControl.sizeMultiplicator, -1 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
				ur = new GLVector( 39 * GLCellularAutomatonControl.sizeMultiplicator, -1 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
				ll = new GLVector( 1 * GLCellularAutomatonControl.sizeMultiplicator, -39 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
				lr = new GLVector( 39 * GLCellularAutomatonControl.sizeMultiplicator, -39 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
			} else {
				ul = new GLVector( 0 * GLCellularAutomatonControl.sizeMultiplicator, 0 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
				ur = new GLVector( 40 * GLCellularAutomatonControl.sizeMultiplicator, 0 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
				ll = new GLVector( 0 * GLCellularAutomatonControl.sizeMultiplicator, -40 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
				lr = new GLVector( 40 * GLCellularAutomatonControl.sizeMultiplicator, -40 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
			}
		// Do not set color it should already be set by super call
		this.defaultColor = VisualizationOptionManager.getCellFloorColor();
	}

	public GLCell( GLCellControl control, GLColor color ) {
		super( control );
		this.position.x = control.getXPosition() * GLCellularAutomatonControl.sizeMultiplicator;
		this.position.y = control.getYPosition() * GLCellularAutomatonControl.sizeMultiplicator;
		if( ul == null )
			if( VisualizationOptionManager.showSpaceBetweenCells() ) {
				ul = new GLVector( 1 * GLCellularAutomatonControl.sizeMultiplicator, -1 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
				ur = new GLVector( 39 * GLCellularAutomatonControl.sizeMultiplicator, -1 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
				ll = new GLVector( 1 * GLCellularAutomatonControl.sizeMultiplicator, -39 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
				lr = new GLVector( 39 * GLCellularAutomatonControl.sizeMultiplicator, -39 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
			} else {
				ul = new GLVector( 0 * GLCellularAutomatonControl.sizeMultiplicator, 0 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
				ur = new GLVector( 40 * GLCellularAutomatonControl.sizeMultiplicator, 0 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
				ll = new GLVector( 0 * GLCellularAutomatonControl.sizeMultiplicator, -40 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
				lr = new GLVector( 40 * GLCellularAutomatonControl.sizeMultiplicator, -40 * GLCellularAutomatonControl.sizeMultiplicator, 0 );
			}
		this.color = color;
		this.defaultColor = color;
	}

	@Override
	public void performDrawing( GL gl ) {
		if( VisualizationOptionManager.smoothCellVisualization() ) {
			boolean lighting = gl.glIsEnabled( GL.GL_LIGHTING );
			gl.glBegin( GL.GL_QUADS );
			gl.glNormal3d( 0, 0, 1 );
			getControl().mixColorWithNeighbours( Direction.TopLeft ).draw( gl, lighting );
			ul.draw( gl );
			getControl().mixColorWithNeighbours( Direction.TopRight ).draw( gl, lighting );
			ur.draw( gl );
			getControl().mixColorWithNeighbours( Direction.DownLeft ).draw( gl, lighting );
			lr.draw( gl );
			getControl().mixColorWithNeighbours( Direction.DownRight ).draw( gl, lighting );
			ll.draw( gl );
			gl.glEnd();
		} else {
			color.draw( gl );
			gl.glBegin( GL.GL_QUADS );
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
		if( control.isPotentialValid() ) {
			double blending = potential / (double)maxPotential;
			return lowColor.blend( highColor, blending );
		} else
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
