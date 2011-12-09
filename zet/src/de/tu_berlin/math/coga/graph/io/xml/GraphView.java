/**
 * GraphView.java
 * Created: 18.3.2010, 12:02:46
 */
package de.tu_berlin.math.coga.graph.io.xml;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.graph.Edge;
import ds.mapping.IdentifiableIntegerMapping;
import ds.graph.network.AbstractNetwork;
import ds.graph.Node;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GraphView {

	/** The graph. */
	AbstractNetwork network;
	/** The positions for the nodes. */
	NodePositionMapping nodePositionMapping;
	/** Capacities for all edges. */
	IdentifiableIntegerMapping<Edge> edgeCapacities;
	/** Capacities for all nodes. */
	IdentifiableIntegerMapping<Node> nodeCapacities;
	/** Transit times for all edges. */
	IdentifiableIntegerMapping<Edge> transitTimes;
	/** Supplies (balance values) for all nodes. */
	IdentifiableIntegerMapping<Node> supplies;
	/** A list of the nodes that are sources. */
	List<Node> sources;
	/** A list of the nodes that are sinks. */
	ArrayList<Node> sinks;
	/** A scale value that should be used for displaying the graph. */
	private double scale = 1;
	/** The offset that has to be added to a point to center the graph.  */
	private Vector3 effectiveOffset = new Vector3();

	boolean containsSuperSink = false;

	int xOffset;
	int yOffset;
	int zOffset;

	double minX = Double.MAX_VALUE;
	double maxX = Double.MIN_VALUE;
	double minY = Double.MAX_VALUE;
	double maxY = Double.MIN_VALUE;
	double minZ = Double.MAX_VALUE;
	double maxZ = Double.MIN_VALUE;


	public GraphView( AbstractNetwork network, NodePositionMapping nodePositionMapping, IdentifiableIntegerMapping<Edge> edgeCapacities, IdentifiableIntegerMapping<Node> nodeCapacities, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Node> supplies, List<Node> sources, ArrayList<Node> sinks ) {
		this.network = network;
		setNodePositionMapping( nodePositionMapping );
		this.edgeCapacities = edgeCapacities;
		this.nodeCapacities = nodeCapacities;
		this.transitTimes = transitTimes;
		this.supplies = supplies;
		this.sources = sources;
		this.sinks = sinks;
	}

	public GraphView( EarliestArrivalFlowProblem eafp, NodePositionMapping nodePositionMapping ) {
		this.network = eafp.getNetwork();
		this.edgeCapacities = eafp.getEdgeCapacities();
		this.nodeCapacities = eafp.getNodeCapacities();
		this.transitTimes = eafp.getTransitTimes();
		this.supplies = eafp.getSupplies();
		this.sources = eafp.getSources();
		final ArrayList<Node> sink = new ArrayList<Node>( 1 );
		sink.add( eafp.getSink() );
		this.sinks = sink;
		setNodePositionMapping( nodePositionMapping );
	}


	public AbstractNetwork getNetwork() {
		return network;
	}

	public void setNetwork( AbstractNetwork network ) {
		this.network = network;
	}

	public NodePositionMapping getNodePositionMapping() {
		return nodePositionMapping;
	}

	public final void setNodePositionMapping( NodePositionMapping nodePositionMapping ) {
		this.nodePositionMapping = nodePositionMapping;

		// compute min and max values
		for( Node node : network.nodes() ) {
			minX = Math.min( nodePositionMapping.get( node ).x, minX );
			maxX = Math.max( nodePositionMapping.get( node ).x, maxX );

			minY = Math.min( nodePositionMapping.get( node ).y, minY );
			maxY = Math.max( nodePositionMapping.get( node ).y, maxY );

			minZ = Math.min( nodePositionMapping.get( node ).z, minZ );
			maxZ = Math.max( nodePositionMapping.get( node ).z, maxZ );
		}

		// compute effective offset for centration


		final double xadd = (maxX - minX)/2;
		final double yadd = (maxY - minY)/2;
		final double zadd = (maxZ - minZ)/2;

		if( maxX < 0 )
			effectiveOffset.x = maxX + xadd;
		else if( minX > 0 )
			effectiveOffset.x = -minX - xadd;
		else if( minX < 0 )
			effectiveOffset.x = -minX - xadd;
		else
			effectiveOffset.x = -maxX + xadd;

		if( maxY < 0 )
			effectiveOffset.y = maxY + yadd;
		else if( minY > 0 )
			effectiveOffset.y = -minY - yadd;
		else if( minY < 0 )
			effectiveOffset.y = -minY - yadd;
		else
			effectiveOffset.y = -maxY + yadd;

		if( maxZ < 0 )
			effectiveOffset.z = maxZ + zadd;
		else if( minZ > 0 )
			effectiveOffset.z = -minZ - zadd;
		else if( minZ < 0 )
			effectiveOffset.z = -minZ - zadd;
		else
			effectiveOffset.z = -maxZ + zadd;
		
		System.out.println( "Offset: " + effectiveOffset.toString() );
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

	public List<Node> getSources() {
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
		if( containsSuperSink )
			return network.adjacentNodes( node ).contains( getSupersink() );
		else
			return sinks.contains( node );
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

	/**
	 * Returns the largest {@code x}-position of a node.
	 * @return the largest {@code x}-position of a node
	 */
	public double getMaxX() {
		return maxX;
	}

	/**
	 * Returns the largest {@code y}-position of a node.
	 * @return the largest {@code y}-position of a node
	 */
	public double getMaxY() {
		return maxY;
	}

	/**
	 * Returns the largest {@code z}-position of a node.
	 * @return the largest {@code z}-position of a node
	 */
	public double getMaxZ() {
		return maxZ;
	}

	/**
	 * Returns the smallest {@code x}-position of a node.
	 * @return the smallest {@code x}-position of a node
	 */
	public double getMinX() {
		return minX;
	}

	/**
	 * Returns the smallest {@code y}-position of a node.
	 * @return the smallest {@code y}-position of a node
	 */
	public double getMinY() {
		return minY;
	}

	/**
	 * Returns the smallest {@code z}-position of a node.
	 * @return the smallest {@code z}-position of a node
	 */
	public double getMinZ() {
		return minZ;
	}

	/**
	 * Returns the offset that has to be added to all coordinates to center the
	 * graph around the origin.
	 * @return a vector containing the offsets for all three coordinate directions
	 */
	public Vector3 getEffectiveOffset() {
		return effectiveOffset;
	}

	public boolean isContainsSuperSink() {
		return containsSuperSink;
	}

	public void setContainsSuperSink( boolean containsSuperSink ) {
		this.containsSuperSink = containsSuperSink;
	}
}
