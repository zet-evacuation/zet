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

import gui.visualization.control.graph.GLFlowGraphControl;
import gui.visualization.control.graph.GLGraphFloorControl;
import javax.media.opengl.GL;
import opengl.framework.abs.AbstractDrawable;

/**
 * <p>This class draws a floor in the graph (which does not explicitly exist
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
public class GLGraphFloor extends AbstractDrawable<GLNode, GLGraphFloorControl> {

	public GLGraphFloor( GLGraphFloorControl control ) {
		super( control );
//		super( control, new CullingShapeCube() );
		this.position.x = control.getXPosition() * GLFlowGraphControl.sizeMultiplicator;
		this.position.y = control.getYPosition() * GLFlowGraphControl.sizeMultiplicator;
	}

	@Override
	public void update() { 
		repaint = true;
	}
	
	@Override
	public void performDrawing( GL gl ) {
		super.performDrawing( gl );
		if( repaint )
			performStaticDrawing( gl );
		gl.glCallList( displayList );
	}
	
	@Override
	public void performStaticDrawing( GL gl ) {
		// Erzeuge eine display-Liste falls nicht schon längst gemacht
		if( displayList <= 0 )
			gl.glDeleteLists( displayList, 1 );
		displayList = gl.glGenLists( 1 );
		gl.glNewList( displayList, GL.GL_COMPILE );
		staticDrawAllChildren( gl );
		gl.glEndList();
		repaint = false;
	}
}
