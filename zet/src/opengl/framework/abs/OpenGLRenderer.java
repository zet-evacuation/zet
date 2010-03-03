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
 * OpenGLComponent.java
 * Created on 29.01.2008, 17:59:42
 */

package opengl.framework.abs;

import javax.media.opengl.GLAutoDrawable;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface OpenGLRenderer {
	
	//public void renderScene( GLAutoDrawable drawable );
public void display( GLAutoDrawable drawable );	// inherited from GLEventListener

public void updateViewport( GLAutoDrawable drawable, int x, int y, int width, int height );

	public void initGFX( GLAutoDrawable drawable );
	//public void animate( );
}