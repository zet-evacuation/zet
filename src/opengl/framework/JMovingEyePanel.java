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
/**
 * Class JMovingEyePanel
 * Erstellt 10.04.2008, 19:43:34
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
import de.tu_berlin.math.coga.math.vectormath.Vector3;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JMovingEyePanel extends JOpenGLCanvas {
	protected Vector3 view = new Vector3( 0, 0, 1 );			// direction of view (z-axis)
	protected Vector3 up = new Vector3( 0, 1, 0 );				// direction of up   (y-axis)
	protected Vector3 pos = new Vector3( 0, 0, 0 );			// position
	private int initMouseX;
	private int initMouseY;
	private boolean mouseMove;
	private Vector3 initView;
	private Vector3 initUp = up;
	double speed = 0.05;
	double speedStep = 0.01;
	double minSpeed = 0;
	double maxSpeed = 1;

	/**
	 * 
	 */
	public JMovingEyePanel() {
		super();
		useKeyListener();
		useMouseListener();
		useMouseMotionListener();
	}

	/**
	 * 
	 * @param caps
	 */
	public JMovingEyePanel( GLCapabilities caps ) {
		super( caps );
		useKeyListener();
		useMouseListener();
		useMouseMotionListener();
}

	@Override
	public void updateViewport( GLAutoDrawable drawable, int x, int y, int width, int height ) {
		super.updateViewport( drawable, x, y, width, height );	// calculate viewport
		gl = drawable.getGL();
		GLU glu = new GLU();

		if( height <= 0 ) // avoid a divide by zero error!
			height = 1;
		final float aspect = (float) width / (float) height;
		gl.glMatrixMode( GL.GL_PROJECTION );
		gl.glLoadIdentity();
		glu.gluPerspective( 45.0, aspect, 1, 1000 );
		gl.glMatrixMode( GL.GL_MODELVIEW );
	}

	//@Override
	//public void renderScene( GLAutoDrawable drawable ) {
	//	super.renderScene( drawable );	// let clear the screen
//
	//}

	// look-method
	public void look() {
		GLU glu = new GLU();
		// set eye position and direction of view
		glu.gluLookAt( pos.x, pos.y, pos.z, pos.x - view.x, pos.y - view.y, pos.z - view.z, up.x, up.y, up.z );
	}

	@Override
	public void keyPressed( KeyEvent e ) {
		switch( e.getKeyCode() ) {
			case KeyEvent.VK_LEFT:
				stepLeft();
				break;
			case KeyEvent.VK_RIGHT:
				stepRight();
				break;
			case KeyEvent.VK_UP:
				stepUp();
				break;
			case KeyEvent.VK_DOWN:
				stepDown();
				break;
			case KeyEvent.VK_PLUS:
				accelerate();
				break;
			case KeyEvent.VK_MINUS:
				decelerate();
				break;
		}
		repaint();
	}

	@Override
	public void mousePressed( MouseEvent e ) {
		initMouseX = e.getX();
		initMouseY = e.getY();
		initView = view;
		initUp = up;
		if( e.getButton() == MouseEvent.BUTTON1 ) {
			mouseMove = true;
		}
	}

	@Override
	public void mouseReleased( MouseEvent e ) {
		if( e.getButton() == MouseEvent.BUTTON1 ) {
			mouseMove = false;
		}
	}

	@Override
	public void mouseDragged( MouseEvent e ) {
		if( !mouseMove ) {
			return;
		}
		int y = e.getY();
		int x = e.getX();
		Dimension size = e.getComponent().getSize();
		
		// Look up/down and right/left
		up = initUp;
		view = initView;
		yaw( 90.0f * ( (float)(initMouseX-x)/(float)size.width ) );				// x-direction
		pitch( -90.0f * ( (float)(initMouseY-y)/(float)size.height ) );		// y-direction
		
		repaint();
	}

	@Override
	public void mouseWheelMoved( MouseWheelEvent e ) {
		pos.addTo( view.scalarMultiplicate( e.getWheelRotation() * speed ) );
		repaint();
	}

	protected void pitch( double angle ) {
	  Vector3 xa;

		xa = view.crossProduct( initUp ); // view * up;
		view = Vector3.rotateVector( angle, xa, view );
		up = Vector3.rotateVector( angle, xa, initUp );
	}

	protected void yaw( double angle ) {
		view = Vector3.rotateVector( angle, up, view );
	}
	
	protected void roll( double angle ) {
		up = Vector3.rotateVector( angle, view, up );
	}

	public void stepRight() {
		pos.addTo( view.crossProduct( up ).scalarMultiplicate( -speed ) );
	}
	
	public void stepLeft() {
		pos.addTo( view.crossProduct( up ).scalarMultiplicate( speed ) );
	}
	
	public void stepFor() {
		pos.addTo( view.scalarMultiplicate( -speed ) );
	}
	
	public void stepBack() {
		pos.addTo( view.scalarMultiplicate( speed ) );
	}
	
	public void stepUp() {
		pos.addTo( up.scalarMultiplicate( speed ) );
	}

	public void stepDown() {
		pos.addTo( up.scalarMultiplicate( -speed ) );
	}
	
	public void accelerate() {
		speed = Math.min( speed + speedStep, maxSpeed );
	}
	
	public void decelerate() {
		speed = Math.max( speed - speedStep, minSpeed );
	}
	
	// Setter and getter
	public Vector3 getPos() {
		return pos;
	}

	public void setPos( Vector3 pos ) {
		this.pos = pos;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed( double speed ) {
		this.speed = speed;
	}

	public double getSpeedStep() {
		return speedStep;
	}

	public void setSpeedStep( double speedStep ) {
		if( speedStep < minSpeed )
			speedStep = minSpeed;
		else if( speedStep > maxSpeed )
			speedStep = maxSpeed;
		else
			this.speedStep = speedStep;
	}
}
