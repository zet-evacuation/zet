/*
 * JFLyingEyePanel.java
 * Created on 30.01.2008, 22:01:38
 */

package opengl.framework;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;

// Needs to be implemented, if really neccessary, i don't think so!
import javax.media.opengl.glu.GLU;
// for implementation details see demo 03.
import util.vectormath.Vector3;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JFlyingEyePanel extends JOpenGLPanel {
	Vector3 pos = new Vector3();								// position
	Vector3 view = new Vector3( 0, 0, 1 );			// direction of view (z-axis)
	Vector3 up = new Vector3( 0, 1, 0 );				// direction of up   (y-axis)
	private int initMouseX;
	private int initMouseY;
	private boolean mouseMove;
	private Vector3 initView;
	private Vector3 initUp;
	double speed = 0.05;
	double speedStep = 0.01;
	double minSpeed = 0;
	double maxSpeed = 1;

	/**
	 * 
	 */
	public JFlyingEyePanel() {
		super();
		useKeyListener();
		useMouseListener();
		useMouseMotionListener();
	}

	/**
	 * 
	 * @param caps
	 */
	public JFlyingEyePanel( GLCapabilities caps ) {
		super( caps );
		useKeyListener();
		useMouseListener();
		useMouseMotionListener();
	}

	@Override
	public void updateViewport( GLAutoDrawable drawable, int x, int y, int width, int height ) {
		super.updateViewport( drawable, x, y, width, height );	// calculate viewport
		GL gl = drawable.getGL();
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
	//}

	// look-method
	public void look() {
		GLU glu = new GLU();
		// set eye position and direction of view
		glu.gluLookAt( pos.x, pos.y, pos.z, pos.x - view.x, pos.y - view.y, pos.z - view.z, up.x, up.y, up.z );
	}

}
