/*
 * JOpenGLPanel.java
 * Created on 29.01.2008, 01:01:28
 */

package opengl.framework;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import opengl.framework.abs.AbstractOpenGLPanel;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class JOpenGLPanel extends AbstractOpenGLPanel {

	/**
	 * 
	 */
	public JOpenGLPanel() {
		super();
	}

	/**
	 * 
	 * @param caps
	 */
	public JOpenGLPanel( GLCapabilities caps ) {
		super( caps );
	}

	//public void renderScene( GLAutoDrawable drawable ) {
	//	GL gl = drawable.getGL();
	//	
	//	// Clear the drawing area
	//	gl.glClear( getClearBits() );
	//}

	public void updateViewport( GLAutoDrawable drawable, int x, int y, int width, int height ) {
		GL gl = drawable.getGL();
		gl.glViewport(0,0, width, height);
	}

	public void initGFX( GLAutoDrawable drawable ) {
		// Get GL Context
		GL gl = drawable.getGL();
		gl.glClearDepth( 1.0f );																				// Initialize depth-buffer precision
		gl.glDepthFunc( GL.GL_LEQUAL );																	// Quality of depht-testing
		gl.glEnable( GL.GL_DEPTH_TEST );																// Enable depth-buffer. (z-buffer)
		gl.glShadeModel( GL.GL_SMOOTH );																// Activate smooth-shading (Gauraud)
		gl.glHint( GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST );		// Perspective calculations with high precision

		// Enable VSync
		gl.setSwapInterval(1);
		
		// Set clear screen color
		gl.glClearColor(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), getBackground().getAlpha());
		//gl.glShadeModel( GL.GL_SMOOTH );
	}

	public void animate( double deltaTime ) { }

	public void animate() { }
}
