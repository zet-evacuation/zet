/**
 * GraphView.java
 * Created: 18.3.2010, 12:02:46
 */
package zet.xml;

import de.tu_berlin.math.coga.math.vectormath.Vector3;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Network;
import ds.graph.Node;
import java.util.ArrayList;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GraphView {

	/** The graph. */
	Network network;
	/** The positions for the nodes. */
	IdentifiableObjectMapping<Node, Vector3> nodePositionMapping;
	/** Capacities for all edges. */
	IdentifiableIntegerMapping<Edge> edgeCapacities;
	/** Capacities for all nodes. */
	IdentifiableIntegerMapping<Node> nodeCapacities;
	/** Transit times for all edges. */
	IdentifiableIntegerMapping<Edge> transitTimes;
	/** Supplies (balance values) for all nodes. */
	IdentifiableIntegerMapping<Node> supplies;
	/** A list of the nodes that are sources. */
	ArrayList<Node> sources;
	/** A list of the nodes that are sinks. */
	ArrayList<Node> sinks;
	/** A scale value that should be used for displaying the graph. */
	private double scale = 1;

	public GraphView( Network network, IdentifiableObjectMapping<Node, Vector3> nodePositionMapping, IdentifiableIntegerMapping<Edge> edgeCapacities, IdentifiableIntegerMapping<Node> nodeCapacities, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Node> supplies, ArrayList<Node> sources, ArrayList<Node> sinks ) {
		this.network = network;
		this.nodePositionMapping = nodePositionMapping;
		this.edgeCapacities = edgeCapacities;
		this.nodeCapacities = nodeCapacities;
		this.transitTimes = transitTimes;
		this.supplies = supplies;
		this.sources = sources;
		this.sinks = sinks;
	}


	public Network getNetwork() {
		return network;
	}

	public void setNetwork( Network network ) {
		this.network = network;
	}

	public IdentifiableObjectMapping<Node, Vector3> getNodePositionMapping() {
		return nodePositionMapping;
	}

	public void setNodePositionMapping( IdentifiableObjectMapping<Node, Vector3> nodePositionMapping ) {
		this.nodePositionMapping = nodePositionMapping;
	}

	public IdentifiableIntegerMapping<Edge> getEdgeCapacities() {
		return edgeCapacities;
	}

	public void setEdgeCapacities( IdentifiableIntegerMapping<Edge> edgeCapacities ) {
		this.edgeCapacities = edgeCapacities;
	}

	public IdentifiableIntegerMapping<Node> getNodeCapacities() {
		return nodeCapacities;
	}

	public void setNodeCapacities( IdentifiableIntegerMapping<Node> nodeCapacities ) {
		this.nodeCapacities = nodeCapacities;
	}

	public ArrayList<Node> getSinks() {
		return sinks;
	}

	public void setSinks( ArrayList<Node> sinks ) {
		this.sinks = sinks;
	}

	public ArrayList<Node> getSources() {
		return sources;
	}

	public void setSources( ArrayList<Node> sources ) {
		this.sources = sources;
	}

	public IdentifiableIntegerMapping<Node> getSupplies() {
		return supplies;
	}

	public void setSupplies( IdentifiableIntegerMapping<Node> supplies ) {
		this.supplies = supplies;
	}

	public IdentifiableIntegerMapping<Edge> getTransitTimes() {
		return transitTimes;
	}

	public void setTransitTimes( IdentifiableIntegerMapping<Edge> transitTimes ) {
		this.transitTimes = transitTimes;
	}

	public Node getSupersink() {
		return sinks.get( 0 );
	}

	public boolean isEvacuationNode( Node node ) {
		return network.adjacentNodes( node ).contains( getSupersink() );
	}

	public boolean isSourceNode( Node node ) {
		return sources.contains( node );
	}

	public double getScale() {
		return scale;
	}

	void setScale( double scale ) {
		this.scale = scale;
	}	
}
