/**
 * GLGraph.java
 * Created: Aug 18, 2010,3:24:54 PM
 */
package gui.visualization.draw.graph;

import gui.visualization.control.graph.GLGraphControl;
import javax.media.opengl.GL;
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
	public void performDrawing( GL gl ) {
		super.performDrawing( gl );
		if( repaint ) {
			performStaticDrawing( gl );
		}
		gl.glCallList( displayList );
	}

	@Override
	public void performStaticDrawing( GL gl ) {
		// Erzeuge eine display-Liste falls nicht schon längst gemacht
		if( displayList <= 0 )
			gl.glDeleteLists( displayList, 1 );
		displayList = gl.glGenLists( 1 );
		gl.glNewList( displayList, GL.GL_COMPILE );
		staticDrawAllChildren( gl );
		gl.glEndList();
		repaint = false;
	}


	@Override
	public void update() {
		
	}

}
