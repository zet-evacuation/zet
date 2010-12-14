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
 * JPolarViewPanel.java
 * Created on 30.01.2008, 22:01:49
 */

package opengl.framework;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.glu.GLU;
import opengl.helper.ProjectionHelper;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JPolarViewPanel extends JOpenGLCanvas {
	private double twistAngle = 0;
	private double azimAngle = 0;
	private double incAngle = 0;
	private double distance = 10;
	private int viewportWidth = 0;
	private int viewportHeight = 0;
	private boolean updateProjection = false;
	private double minDistance = 20;
	private double maxDistance = 1;
	private double speed = 0.1;
	private double nearDist = 0.1;
	private double farDist = 2000;
	private int initMouseX;
	private int initMouseY;
	private boolean mouseMove = false;
	
	/**
	 * 
	 */
	public JPolarViewPanel() {
		super();
	}

	/**
	 * 
	 * @param caps
	 */
	public JPolarViewPanel( GLCapabilities caps ) {
		super( caps );
	}


	@Override
	public void updateViewport( GLAutoDrawable drawable, int x, int y, int width, int height ) {
		super.updateViewport( drawable, x, y, width, height );	// calculate viewport
		GL gl = drawable.getGL();

		if( height <= 0 ) // avoid a divide by zero error!
			height = 1;

		this.viewportWidth = width;
		this.viewportHeight = height;

		if( distance < 0 )
			distance = nearDist + (farDist - nearDist) / 2.0;

		setProjection( gl );
		gl.glLoadIdentity();									// Load identity matrix
	}

	private void setProjection( GL gl ) {
		// Set up projection matrices for 2d-like orthogonal projection
		final float aspect = (float)viewportWidth / (float)viewportHeight;
		GLU glu = new GLU();
		gl.glMatrixMode( GL.GL_PROJECTION );
		gl.glLoadIdentity();
		glu.gluPerspective( 45.0, aspect, nearDist, farDist );
		ProjectionHelper.setViewPolar( gl, distance, azimAngle, incAngle, twistAngle );
		
		// We need to reset the model/view matrix (and swith to the mode!)
		gl.glMatrixMode(GL.GL_MODELVIEW);			// Set model/view matrix-mode
		updateProjection = false;
	}

	/**

	 * @param drawable
	 */
	@Override
	public void display( GLAutoDrawable drawable ) {
		super.display( drawable );	// let clear the screen

		if( updateProjection )
			setProjection( gl );
	}

	@Override
	public void keyPressed( KeyEvent e ) {
		final double angleStep = 2.5;
		switch( e.getKeyCode() ) {
			case KeyEvent.VK_LEFT:
				azimAngle = ( azimAngle + angleStep ) % 360.0;
				break;
			case KeyEvent.VK_RIGHT:
				azimAngle = ( azimAngle - angleStep ) % 360.0;
				break;
			case KeyEvent.VK_UP:
				incAngle = ( incAngle + angleStep ) % 360.0;
				break;
			case KeyEvent.VK_DOWN:
				incAngle = ( incAngle - angleStep ) % 360.0;
				break;
			case KeyEvent.VK_T:
				twistAngle = ( twistAngle + angleStep ) % 360.0;
				break;
			case KeyEvent.VK_Z:
				twistAngle = ( twistAngle - angleStep ) % 360.0;
				break;
			case KeyEvent.VK_PLUS:
				distance -= speed;
				break;
			case KeyEvent.VK_MINUS:
				distance += speed;
				break;
		}
		updateProjection = true;
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
    initMouseX = e.getX();
    initMouseY = e.getY();
    //if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
		if( e.getButton() == MouseEvent.BUTTON1 )
      mouseMove = true;
    //}
  }

	@Override
  public void mouseReleased(MouseEvent e) {
    //if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
		if( e.getButton() == MouseEvent.BUTTON1 )
      mouseMove = false;
    //}
  }
	
	@Override
  public void mouseDragged(MouseEvent e) {
		if( !mouseMove )
			return;
    int x = e.getX();
    int y = e.getY();
    Dimension size = e.getComponent().getSize();

    float twist = 360.0f * ( (float)(x-initMouseX)/(float)size.width);	// Y-Move
    float inc = 360.0f * ( (float)(initMouseY-y)/(float)size.height);		// X-Move
    
    initMouseX = x;
    initMouseY = y;

		twistAngle += twist;
		incAngle += inc;
		
		updateProjection = true;
  }
	
	@Override
	public void mouseWheelMoved( MouseWheelEvent e ) {
		distance += speed * e.getUnitsToScroll();
		updateProjection = true;
		repaint();
	}
	
	// Getter and setter
	public double getMinDistance() {
		return minDistance;
	}
	
	public void setMinDistance( double minDistnace ) {
		this.minDistance = minDistnace;
	}

	public double getMaxDistance() {
		return maxDistance;
	}
	
	public void setMaxDistance( double maxDistnace ) {
		this.maxDistance = maxDistnace;
	}
	
	public double getSpeed() {
		return speed;
	}
	
	public void setSpeed( double speed ) {
		this.speed = speed;
	}
	
	public double getFarDist() {
		return farDist;
	}

	public void setFarDist( double farDist ) {
		this.farDist = farDist;
	}

	public double getNearDist() {
		return nearDist;
	}

	public void setNearDist( double nearDist ) {
		this.nearDist = nearDist;
	}
	
}
