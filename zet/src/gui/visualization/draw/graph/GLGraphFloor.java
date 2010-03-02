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
 * Class GLGraphFloor
 * Erstellt 08.05.2008, 01:30:50
 */
package gui.visualization.draw.graph;

import gui.visualization.control.graph.GLGraphFloorControl;
import gui.visualization.control.graph.GLNodeControl;
import gui.visualization.util.VisualizationConstants;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import opengl.framework.abs.AbstractDrawable;

/**
 * <p>This class draws a floor in the graph (which does not explecitly exist
 * in the graph itself). It consists of the nodes belonging to rooms on one
 * floor in a {@link ds.Project}.</p>
 * <p>The nodes are stored in a display list to speed up the visualization,
 * the display list is created if {@link #performStaticDrawing(javax.media.opengl.GLAutoDrawable)} is called.
 * During normal visualization (when {@link #performStaticDrawing(javax.media.opengl.GLAutoDrawable)} is
 * called) the display list is executed. The display list is rebuilt when some
 * settings are updated.</p>
 * @see AbstractDrawable
 * @author Jan-Philipp Kappmeier
 */
//public class GLGraphFloor extends AbstractDrawable<CullingShapeCube, GLNode, GLGraphFloorControl, GLNodeControl> {
public class GLGraphFloor extends AbstractDrawable<GLNode, GLGraphFloorControl, GLNodeControl> {

	public GLGraphFloor( GLGraphFloorControl control ) {
		super( control );
//		super( control, new CullingShapeCube() );
		this.position.x = control.getXPosition() * VisualizationConstants.SIZE_MULTIPLICATOR;
		this.position.y = control.getYPosition() * VisualizationConstants.SIZE_MULTIPLICATOR;
	}

	@Override
	public void update() { 
		repaint = true;
	}
	
	@Override
	public void performDrawing( GLAutoDrawable drawable ) {
		super.performDrawing( drawable );
		if( repaint )
			performStaticDrawing( drawable );
		drawable.getGL().glCallList( displayList );
	}
	
	@Override
	public void performStaticDrawing( GLAutoDrawable drawable ) {
		// Erzeuge eine display-Liste falls nicht schon längst gemacht
		GL gl = drawable.getGL();
		if( displayList <= 0 )
			gl.glDeleteLists( displayList, 1 );
		displayList = gl.glGenLists( 1 );
		gl.glNewList( displayList, GL.GL_COMPILE );
		staticDrawAllChildren( drawable );
		gl.glEndList();
		repaint = false;
	}
}
