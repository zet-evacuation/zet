/**
 * GLNashGraphControl.java
 * Created: 30.08.2010 16:31:02
 */
package gui.visualization.control.graph;

import de.tu_berlin.math.coga.zet.viewer.NashFlowEdgeData;
import de.tu_berlin.math.coga.zet.viewer.NashFlowVisualization;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Node;
import gui.visualization.draw.graph.GLNashGraph;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLNashGraphControl extends GLGraphControl {
	private final IdentifiableObjectMapping<Edge, NashFlowEdgeData> nashFlowMapping;
	private final NashFlowVisualization nfv;

	public GLNashGraphControl( Graph graph, NodePositionMapping nodePositionMapping, IdentifiableObjectMapping<Edge, NashFlowEdgeData> nashFlowMapping, NashFlowVisualization nfv ) {
		super( graph, nodePositionMapping, false );
		this.nashFlowMapping = nashFlowMapping;
		this.nfv = nfv;
		setUpNodes();
	}

	@Override
	protected void setUpNodes() {
		for( Node n : graph.nodes() ) {
			GLNashNodeControl nodeControl = new GLNashNodeControl( graph, n, nodePositionMapping, nashFlowMapping, nfv );
			add( nodeControl );
		}

		this.setView( new GLNashGraph( this ) );
		for( GLSimpleNodeControl nodeControl : this )
			view.addChild( nodeControl.getView() );
	}

	int count = 300;

	@Override
	public boolean isFinished() {
		if( true )
			return false;
		count--;
		if( count > 0 )
			return false;
		else
			return true;
	}
}
