
package gui.visualization.draw.graph;

import gui.visualization.control.graph.GLNashNodeControl;
import javax.media.opengl.GL;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLNashNode extends GLSimpleNode {

	public GLNashNode( GLNashNodeControl control ) {
		super( control );
		
	}

	@Override
	public void performDrawing( GL gl ) {
		super.drawNode( gl );
		super.performDrawing( gl );
	}

}
