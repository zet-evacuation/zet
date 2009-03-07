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
	public void updateViewport( GLAutoDrawable drawable, int x, int y, int width, int height );
	public void initGFX( GLAutoDrawable drawable );
	public void animate( );
}

// TODO interface animatable