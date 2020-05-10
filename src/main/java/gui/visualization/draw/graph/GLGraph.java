/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
package gui.visualization.draw.graph;

import gui.visualization.control.graph.GLGraphControl;

import javax.media.opengl.GL2;

import org.zetool.opengl.framework.abs.AbstractDrawable;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLGraph extends AbstractDrawable<GLSimpleNode, GLGraphControl> {

	public GLGraph( GLGraphControl control ) {
		super( control );
	}

	@Override
	public void performDrawing( GL2 gl ) {
		super.performDrawing( gl );
		if( repaint ) {
			performStaticDrawing( gl );
		}
		gl.glCallList( displayList );
	}

	@Override
	public void performStaticDrawing( GL2 gl ) {
		// Erzeuge eine display-Liste falls nicht schon längst gemacht
		if( displayList <= 0 )
			gl.glDeleteLists( displayList, 1 );
		displayList = gl.glGenLists( 1 );
		gl.glNewList( displayList, GL2.GL_COMPILE );
		staticDrawAllChildren( gl );
		gl.glEndList();
		repaint = false;
	}


	@Override
	public void update() {
		
	}

}
