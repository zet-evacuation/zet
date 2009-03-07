/**
 * Class GLGraphFloor
 * Erstellt 08.05.2008, 01:30:50
 */
package gui.visualization.draw.graph;

import gui.visualization.control.graph.GLGraphFloorControl;
import gui.visualization.control.graph.GLNodeControl;
import gui.visualization.util.VisualizationConstants;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import opengl.framework.abs.AbstractDrawable;

/**
 * <p>This class draws a floor in the graph (which does not explecitly exist
 * in the graph itself). It consists of the nodes belonging to rooms on one
 * floor in a {@link ds.z.Project}.</p>
 * <p>The nodes are stored in a display list to speed up the visualization,
 * the display list is created if {@link performStaticDrawing()} is called.
 * During normal visualization (when {@linkperformDrawing( GLAutoDrawable )} is
 * called) the display list is executed. The display list is rebuilt when some
 * settings are updated.</p>
 * @see{AbstractDrawable}
 * @author Jan-Philipp Kappmeier
 */
//public class GLGraphFloor extends AbstractDrawable<CullingShapeCube, GLNode, GLGraphFloorControl, GLNodeControl> {
public class GLGraphFloor extends AbstractDrawable<GLNode, GLGraphFloorControl, GLNodeControl> {

	public GLGraphFloor( GLGraphFloorControl control ) {
		super( control );
//		super( control, new CullingShapeCube() );
		this.position.x = control.getXPosition() * VisualizationConstants.SIZE_MULTIPLICATOR;
		this.position.y = control.getYPosition() * VisualizationConstants.SIZE_MULTIPLICATOR;
	}

	@Override
	public void update() { 
		repaint = true;
	}
	
	@Override
	public void performDrawing( GLAutoDrawable drawable ) {
		super.performDrawing( drawable );
		if( repaint )
			performStaticDrawing( drawable );
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
}
