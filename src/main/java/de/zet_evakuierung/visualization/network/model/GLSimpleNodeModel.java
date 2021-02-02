/**
 * GLSimpleNodeControl.java
 * Created: Aug 18, 2010,3:32:10 PM
 */
package de.zet_evakuierung.visualization.network.model;

import java.util.ArrayList;

import de.zet_evakuierung.visualization.AbstractVisualizationModel;
import de.zet_evakuierung.visualization.VisualizationNodeModel;
import de.zet_evakuierung.visualization.network.FlowHistroryTriple;
import de.zet_evakuierung.visualization.network.draw.GLSimpleNode;
import de.zet_evakuierung.visualization.network.util.FlowCalculator;
import org.zetool.graph.DirectedGraph;
import org.zetool.graph.Node;
import org.zetool.graph.visualization.NodePositionMapping;
import org.zetool.math.vectormath.Vector3;

/**
 * A class that controls a node of a graph, used by its visualization glass
 * {@link GLSimpleNode}.
 * @author Jan-Philipp Kappmeier
 */
public class GLSimpleNodeModel extends AbstractVisualizationModel<NetworkVisualizationModel> implements VisualizationNodeModel {

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

	public GLSimpleNodeModel( DirectedGraph graph, Node node, NodePositionMapping<Vector3> nodePositionMapping ) {
        super(new NetworkVisualizationModel());
		this.position = nodePositionMapping.get( node );
		this.graph = graph;
		this.nodePositionMapping = nodePositionMapping;
		this.node = node;
	}

	public int getCapacity() {
		return capacity;
	}

    @Override
	public double getXPosition() {
		return position.x;
	}

    @Override
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
