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

/*
 * JOrthoPanel.java
 * Created on 30.01.2008, 22:00:52
 */

package opengl.framework;

import java.awt.event.KeyEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import opengl.helper.ProjectionHelper;
import de.tu_berlin.math.coga.math.vectormath.Vector3;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JOrthoPanel extends JOpenGLCanvas {
	Vector3 pos = new Vector3();
	int viewportWidth = 0;
	int viewportHeight = 0;
	private boolean updateProjection = false;
	private double canvasWidth = 100;
	private double canvasHeight = 100;
	private double depth = 2000;
	private double currentWidth = 100;
	private double currentHeight = 100;
	private double zoomFactor = 0.1;
	
	private boolean printMode = false;

	/**
	 * 
	 */
	public JOrthoPanel() {
		super();
	}

	/**
	 * 
	 * @param caps
	 */
	public JOrthoPanel( GLCapabilities caps ) {
		super( caps );
	}

	@Override
	public void updateViewport( GLAutoDrawable drawable, int x, int y, int width, int height ) {
		super.updateViewport( drawable, x, y, width, height );	// calculate viewport
		GL gl = drawable.getGL();
		
		this.viewportWidth = width;
		this.viewportHeight = height;
		
		setProjection( gl );
		gl.glLoadIdentity();									// Load identity matrix
	}
	
	private void setProjection( GL gl ) {
		ProjectionHelper.setViewOrthogonal(gl, viewportWidth, viewportHeight, pos.x, pos.y, pos.z, currentWidth, currentHeight, depth  );
		// We need to reset the model/view matrix (and swith to the mode!)
		gl.glMatrixMode(GL.GL_MODELVIEW);			// Set model/view matrix-mode
		updateProjection = false;
	}

	@Override
	public void display( GLAutoDrawable drawable ) {
		super.display( drawable );	// let clear the screen
		if( updateProjection )
			setProjection( gl );
	}
	
	public void switchToPrintScreen( GL gl ) {
		printMode = true;
		ProjectionHelper.setPrintScreenProjection( gl, viewportWidth, viewportHeight );
	}
	
	public void switchToOrthoScreen( GL gl ) {
		printMode = false;
		ProjectionHelper.resetProjection( gl );
	}

	public void zoomIn() {
		currentWidth *= 1-zoomFactor;
		currentHeight *= 1-zoomFactor;
		updateProjection = true;
		repaint();
	}

	public void zoomOut() {
		currentWidth /= 1-zoomFactor;
		currentHeight /= 1-zoomFactor;
		updateProjection = true;
		repaint();
	}
	
	@Override
	public void keyPressed( KeyEvent e ) {
//		if( printMode )
//			return;
		switch( e.getKeyCode() ) {
			case KeyEvent.VK_LEFT:
				pos.x -= 0.1;
				break;
			case KeyEvent.VK_RIGHT:
				pos.x += 0.1;
				break;
			case KeyEvent.VK_UP:
				pos.y += 0.1;
				break;
			case KeyEvent.VK_DOWN:
				pos.y -= 0.1;
				break;
			case KeyEvent.VK_PLUS:
				zoomIn();
				break;
			case KeyEvent.VK_MINUS:
				zoomOut();
				break;
		}
		updateProjection = true;
		repaint();
	}
	
	// Setter and getter
	public Vector3 getPos() {
		return pos;
	}
	
	public void setPos( Vector3 pos ) {
		this.pos = pos;
	}
	
	public double getCanvasWidth() {
		return canvasWidth;
	}
	
	public void setCanvasWidth( double canvasWidth ) {
		this.canvasWidth = canvasWidth;
	}
	
	public double getCanvasHeight() {
		return canvasHeight;
	}
	
	public void setCanvasHeight( double canvasHeight ) {
		this.canvasHeight = canvasHeight;
	}

	public double getDepth() {
		return depth;
	}
	
//	public void setDepth( double depth ) {
//		this.depth = depth;
//	}
	
	public double getZoomFactor() {
		return zoomFactor;
	}
	
	public void setZoomFactor( double zoomFactor ) {
		this.zoomFactor = zoomFactor;
	}
	
	public double getZoom() {
		return currentWidth / canvasWidth;
	}
	
	public void setZoom( double absoluteZoomFactor ) {
		currentWidth = canvasWidth * absoluteZoomFactor;
		currentHeight = canvasHeight * absoluteZoomFactor;
		updateProjection = true;
		repaint();
	}

}
