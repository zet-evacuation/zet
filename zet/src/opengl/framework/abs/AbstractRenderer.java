/*
 * AbstractRenderer.java
 * Created on 30.01.2008, 23:50:54
 */

package opengl.framework.abs;

import javax.media.opengl.GLAutoDrawable;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
abstract public class AbstractRenderer implements OpenGLRenderer {
	public final void updateViewport( GLAutoDrawable drawable, int x, int y, int width, int height ) { }

	public final void initGFX( GLAutoDrawable drawable ) { }
	
	public void animate( double deltaTime ) { }
}
