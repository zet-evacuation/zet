/**
 * GLSimpleNodeControl.java
 * Created: Aug 18, 2010,3:32:10 PM
 */
package gui.visualization.control.graph;

import de.tu_berlin.math.coga.math.vectormath.Vector3;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.Node;
import gui.visualization.control.FlowHistroryTriple;
import gui.visualization.draw.graph.GLEdge;
import gui.visualization.draw.graph.GLSimpleNode;
import gui.visualization.util.FlowCalculator;
import java.util.ArrayList;
import opengl.framework.abs.AbstractControl;

/**
 * A class that controls a node of a graph, used by its visualization glass
 * {@link GLSimpleNode}.
 * @author Jan-Philipp Kappmeier
 */
public class GLSimpleNodeControl extends AbstractControl<GLEdgeControl, GLSimpleNode> {

	Vector3 position;
	private int capacity;
	private FlowCalculator flowCalculator;
	// TODO transfer the scaling parameter somewhere else (probably into the graphVisResults)
	private static final double Z_TO_OPENGL_SCALING = 0.1d;
	private double time;
	private int index;
	private ArrayList<FlowHistroryTriple> graphHistory;
	private boolean isEvacuationNode, isSourceNode, isDeletedSourceNode = false;
	private int duration;
	private int startTime;
	private boolean gridVisible = true;

	public GLSimpleNodeControl( Graph graph, Node node, NodePositionMapping nodePositionMapping ) {
		this.position = nodePositionMapping.get( node );

		// add outgoing edges as children
		for( Edge edge : graph.outgoingEdges( node ) ) {
			//if( edge.start().id() != glControl.superSinkID() && edge.end().id() != glControl.superSinkID() ) {
			//	int nodeFloor1 = graphVisResult.getNodeToFloorMapping().get( edge.start() );
			//	int nodeFloor2 = graphVisResult.getNodeToFloorMapping().get( edge.end() );
			//	if( nodeFloor1 != nodeFloor2 && !showEdgesBetweenFloors )
			//		System.out.println( "Knoten auf verschiedenen Etagen." );
			//	else
			// TODO skip first
			GLEdgeControl edgeControl = new GLEdgeControl( nodePositionMapping, edge );
			add( edgeControl );
			
		}

		setView( new GLSimpleNode( this ) );
		for( GLEdgeControl edgeControl : this ) {
			view.addChild( edgeControl.getView() );
		}
	}

	public int getCapacity() {
		return capacity;
	}

	public double getXPosition() {
		return position.x;
	}

	public double getYPosition() {
		return -position.y;
	}

	public double getZPosition() {
		return position.z;
	}

	public Vector3 getPosition() {
		return position;
	}

}
