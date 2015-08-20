/**
 * GLGraphControl.java
 * Created: Aug 18, 2010,3:21:46 PM
 */
package gui.visualization.control.graph;

import org.zetool.graph.DirectedGraph;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import org.zetool.graph.Graph;
import org.zetool.graph.Node;
import gui.visualization.draw.graph.GLGraph;
import org.zetool.opengl.framework.abs.AbstractControl;
import org.zetool.opengl.framework.abs.DrawableControlable;
import org.zetool.opengl.helper.Frustum;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLGraphControl extends AbstractControl<GLSimpleNodeControl, GLGraph> implements DrawableControlable {
	protected NodePositionMapping nodePositionMapping;
	protected DirectedGraph graph;

	public GLGraphControl( DirectedGraph graph, NodePositionMapping nodePositionMapping ) {
		this( graph, nodePositionMapping, true );
	}

public GLGraphControl( DirectedGraph graph, NodePositionMapping nodePositionMapping, boolean setUpNodes ) {
		this.nodePositionMapping = nodePositionMapping;
		this.graph = graph;
		if( setUpNodes )
			setUpNodes();
	}

	protected void setUpNodes() {
		for( Node n : graph.nodes() ) {
			GLSimpleNodeControl nodeControl = new GLSimpleNodeControl( graph, n, nodePositionMapping );
			add( nodeControl );
		}

		this.setView( new GLGraph( this ) );
		for( GLSimpleNodeControl nodeControl : this )
			view.addChild( nodeControl.getView() );
	}

	public void setFrustum( Frustum frustum ) {

	}

	public Frustum getFrustum() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public void update() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public void delete() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public void addTime( long timeNanoSeconds ) {

	}

	public void setTime( long time ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public void resetTime() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public boolean isFinished() {
		return false;
	}

}
