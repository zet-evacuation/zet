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
/*
 * Created on 19.06.2008
 *
 */
package gui.visualization.draw.building;

import gui.visualization.control.building.GLBuildingControl;
import gui.visualization.control.building.GLWallControl;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import opengl.framework.abs.AbstractDrawable;

/**
 * @author Daniel Pluempe, Jan-Philipp Kappmeier
 *
 */
public class GLBuilding extends AbstractDrawable<GLWall, GLBuildingControl, GLWallControl> {

	/**
	 * @param control
	 */
	public GLBuilding( GLBuildingControl control ) {
		super( control );
		callChildren = false;
	}

	@Override
	public void performDrawing( GLAutoDrawable drawable ) {
		super.performDrawing( drawable );
		if( repaint ) {
			performStaticDrawing( drawable );
		}
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
	
	/**
	 * {@inheritDoc}
	 * @see opengl.framework.abs.AbstractDrawable#update()
	 */
	@Override
	public void update() {
		repaint = true;
	}
}
