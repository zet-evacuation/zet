/**
 * GLSimpleNodeControl.java
 * Created: Aug 18, 2010,3:32:10 PM
 */
package gui.visualization.control.graph;

import de.tu_berlin.coga.graph.DirectedGraph;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.graph.Edge;
import de.tu_berlin.coga.graph.Graph;
import ds.graph.Node;
import gui.visualization.control.FlowHistroryTriple;
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
	private double time;
	private int index;
	private ArrayList<FlowHistroryTriple> graphHistory;
	private boolean isEvacuationNode, isSourceNode, isDeletedSourceNode = false;
	private int duration;
	private int startTime;
	private boolean gridVisible = true;
	protected final DirectedGraph graph;
	protected final NodePositionMapping nodePositionMapping;
	protected final Node node;

	public GLSimpleNodeControl( DirectedGraph graph, Node node, NodePositionMapping nodePositionMapping ) {
		this.position = nodePositionMapping.get( node );
		this.graph = graph;
		this.nodePositionMapping = nodePositionMapping;
		this.node = node;
		setUpEdges();
	}

	public GLSimpleNodeControl( DirectedGraph graph, Node node, NodePositionMapping nodePositionMapping, boolean setUpEdges ) {
		this.position = nodePositionMapping.get( node );
		this.graph = graph;
		this.nodePositionMapping = nodePositionMapping;
		this.node = node;
		if( setUpEdges )
			setUpEdges();
	}

	protected void setUpEdges() {
		// add outgoing edges as children
		for( Edge edge : graph.outgoingEdges( node ) ) {
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
