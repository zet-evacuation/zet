/**
 * GLGraphControl.java
 * Created: Aug 18, 2010,3:21:46 PM
 */
package gui.visualization.control.graph;

import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.graph.Graph;
import ds.graph.Node;
import gui.visualization.draw.graph.GLGraph;
import opengl.framework.abs.AbstractControl;
import opengl.framework.abs.DrawableControlable;
import opengl.helper.Frustum;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLGraphControl extends AbstractControl<GLSimpleNodeControl, GLGraph> implements DrawableControlable {
	protected NodePositionMapping nodePositionMapping;
	protected Graph graph;

	public GLGraphControl( Graph graph, NodePositionMapping nodePositionMapping ) {
		this( graph, nodePositionMapping, true );
	}

public GLGraphControl( Graph graph, NodePositionMapping nodePositionMapping, boolean setUpNodes ) {
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
