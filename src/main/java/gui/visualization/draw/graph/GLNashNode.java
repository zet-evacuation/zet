
package gui.visualization.draw.graph;

import gui.visualization.control.graph.GLNashNodeControl;

import javax.media.opengl.GL2;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLNashNode extends GLSimpleNode {

	public GLNashNode( GLNashNodeControl control ) {
		super( control );
		
	}

	@Override
	public void performDrawing( GL2 gl ) {
		super.drawNode( gl );
		super.performDrawing( gl );
	}

}
