/**
 * GLNashNodeControl.java
 * Created: 30.08.2010 16:35:05
 */
package gui.visualization.control.graph;

import de.tu_berlin.math.coga.zet.viewer.NashFlowEdgeData;
import de.tu_berlin.math.coga.zet.viewer.NashFlowVisualization;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import org.zetool.graph.Edge;
import org.zetool.graph.Graph;
import org.zetool.container.mapping.IdentifiableObjectMapping;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Node;
import gui.visualization.draw.graph.GLNashNode;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLNashNodeControl extends GLSimpleNodeControl {
	private final IdentifiableObjectMapping<Edge, NashFlowEdgeData> nashFlowMapping;
	private final NashFlowVisualization nfv;

	public GLNashNodeControl( DirectedGraph graph, Node node, NodePositionMapping nodePositionMapping, IdentifiableObjectMapping<Edge, NashFlowEdgeData> nashFlowMapping, NashFlowVisualization nfv ) {
		super( graph, node, nodePositionMapping, false );
		this.nashFlowMapping = nashFlowMapping;
		this.nfv = nfv;
		setUpEdges();
	}

	@Override
	protected void setUpEdges() {
		// add outgoing edges as children
		for( Edge edge : graph.outgoingEdges( node ) ) {
			GLEdgeControl edgeControl = new GLNashFlowEdgeControl( nodePositionMapping, edge, nashFlowMapping.get( edge ), nfv );
			add( edgeControl );
		}

		setView( new GLNashNode( this ) );
		for( GLEdgeControl edgeControl : this ) {
			view.addChild( edgeControl.getView() );
		}
	}

}
