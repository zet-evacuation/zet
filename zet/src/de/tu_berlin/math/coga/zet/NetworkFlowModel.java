/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
/*
 * NetworkFlowModel.java
 *
 */
package de.tu_berlin.math.coga.zet;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.util.GraphInstanceChecker;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphMapping;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import ds.graph.network.AbstractNetwork;
import ds.graph.network.DynamicNetwork;
import ds.graph.network.Network;
import ds.mapping.IdentifiableDoubleMapping;
import ds.mapping.IdentifiableIntegerMapping;
import gui.visualization.VisualizationOptionManager;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
@XStreamAlias( "networkFlowModel" )
public class NetworkFlowModel implements Iterable<Node> {
	protected Network netw;
	protected DynamicNetwork network;
	protected IdentifiableIntegerMapping<Edge> edgeCapacities;
	protected IdentifiableIntegerMapping<Node> nodeCapacities;
	protected IdentifiableIntegerMapping<Edge> transitTimes;
	protected IdentifiableDoubleMapping<Edge> exactTransitTimes;
	protected IdentifiableIntegerMapping<Node> currentAssignment;
	protected LinkedList<Node> sources;
	protected ZToGraphMapping mapping;
	protected Node supersink;
	int edgeIndex = 0;
	int nodeCount = 1;

	public NetworkFlowModel() {
		this.network = new DynamicNetwork();
		this.edgeCapacities = new IdentifiableIntegerMapping<>( 0 );
		this.nodeCapacities = new IdentifiableIntegerMapping<>( 0 );
		this.transitTimes = new IdentifiableIntegerMapping<>( 0 );
		this.exactTransitTimes = new IdentifiableDoubleMapping<>( 0 );
		this.currentAssignment = new IdentifiableIntegerMapping<>( 0 );
		this.sources = new LinkedList<>();
		supersink = new Node( 0 );
		network.setNode( supersink );
	}

	public NetworkFlowModel( NetworkFlowModel model ) {
		// a constructor that copies the nodes
		this();
		network.setNodes( model.network.nodes() );
		supersink = model.supersink;
		sources = model.sources;
		nodeCapacities = model.nodeCapacities;
		currentAssignment = model.currentAssignment;
	}

	public void setNumberOfEdges( int numberOfEdges ) {
		edgeCapacities.setDomainSize( numberOfEdges );
		transitTimes.setDomainSize( numberOfEdges );
		exactTransitTimes.setDomainSize( numberOfEdges );
	}

	public int getEdgeCapacity( Edge edge ) {
		if( edgeCapacities.isDefinedFor( edge ) )
			return edgeCapacities.get( edge );
		else
			throw new IllegalArgumentException( DefaultLoc.getSingleton().getString( "ds.Graph.NoEdgeCapacityException" + edge + "." ) );
	}

	public void setEdgeCapacity( Edge edge, int value ) {
		edgeCapacities.set( edge, value );
	}

	/**
	 * Returns a linked list containing the sources. May be consistent with the
	 * current assignment or not.
	 * @return a linked list containing the sources.
	 */
	public List<Node> getSources() {
		return Collections.unmodifiableList( sources );
	}

	/**
	 * Returns a linked list containing the super sink.
	 * @return a linked list containing the super sink
	 */
	public LinkedList<Node> getSinks() {
		LinkedList<Node> sinks = new LinkedList<>();
		sinks.add( supersink );
		return sinks;
	}

	public ZToGraphMapping getZToGraphMapping() {
		return mapping;
	}

	public void setZToGraphMapping( ZToGraphMapping mapping ) {
		this.mapping = mapping;
	}

	public Graph graph() {
		return network;
	}

//    public AbstractNetwork getNetwork(){
//    	return network.getAsStaticNetwork();
//    }
//    public DynamicNetwork getDynamicNetwork(){
//    	if (network instanceof DynamicNetwork)
//    		return (DynamicNetwork)network;
//    	else throw new RuntimeException(DefaultLoc.getSingleton (
//		).getString ("ds.Graph.NoDynamicGraphException"));
//
//    }
//    public void setNetwork(Graph network) {
//        this.network = network;
//    }
	public int getNodeCapacity( Node node ) {
		if( nodeCapacities.isDefinedFor( node ) )
			return nodeCapacities.get( node );
		else
			throw new IllegalArgumentException( DefaultLoc.getSingleton().getString( "ds.Graph.NoNodeCapacityException" + node + "." ) );
	}

	public void setNodeCapacity( Node node, int value ) {
		nodeCapacities.set( node, value );
	}

	public IdentifiableIntegerMapping<Node> nodeCapacities() {
		return nodeCapacities;
	}

	public int getTransitTime( Edge edge ) {
		if( transitTimes.isDefinedFor( edge ) )
			return transitTimes.get( edge );
		else
			throw new IllegalArgumentException( DefaultLoc.getSingleton().getString( "ds.Graph.NoTransitTimeException" + edge + "." ) );
	}

	public void setTransitTime( Edge edge, int value ) {
		transitTimes.set( edge, value );
	}

	public void setExactTransitTime( Edge edge, double value ) {
		exactTransitTimes.set( edge, value );
	}

	public double getExactTransitTime( Edge edge ) {
		if( exactTransitTimes.isDefinedFor( edge ) )
			return exactTransitTimes.get( edge );
		else
			return -1;
	}

	public Node getSupersink() {
		return supersink;
	}

	@Override
	public String toString() {
		return "NetworkFlowModel{" + "network=" + network + ", edgeCapacities=" + edgeCapacities + ", nodeCapacities=" + nodeCapacities + ", transitTimes=" + transitTimes + ", sources=" + sources + ", supersink=" + supersink + '}';
	}

	public EarliestArrivalFlowProblem getEAFP() {
		return getEAFP( 0 );
	}

	public void roundTransitTimes() {
		transitTimes = exactTransitTimes.round();
	}

	public int numberOfNodes() {
		return network.numberOfNodes();
	}

	public int numberOfEdges() {
		return network.numberOfEdges();
	}

	public Iterable<Edge> edges() {
		return network.edges();
	}

	public int numberOfSinks() {
		return network.degree( getSupersink() );
	}

	public Edge getEdge( int i ) {
		return network.getEdge( i );
	}

	public Edge createReverseEdge( Edge edge ) {
		Edge newEdge = new Edge( edgeIndex++, edge.end(), edge.start() );

		while( network.edges().contains( newEdge ) )
			newEdge = new Edge( edgeIndex++, edge.end(), edge.start() );

		mapping.setEdgeLevel( newEdge, mapping.getEdgeLevel( edge ).getInverse() );
		setEdgeCapacity( newEdge, getEdgeCapacity( edge ) );
		setTransitTime( newEdge, getTransitTime( edge ) );
		network.setEdge( newEdge );
		return newEdge;
	}

	public void resetAssignment() {
		currentAssignment = new IdentifiableIntegerMapping<>( network.numberOfNodes() );
		//IdentifiableCollection<Node> nodes = model.getGraph().nodes();
		for( int i = 0; i < network.nodes().size(); i++ )
			//if( !nodeAssignment.isDefinedFor( nodes.get( i ) ) )
			currentAssignment.set( network.nodes().get( i ), 0 );

	}

	public void setNodeAssignment( Node node, int i ) {
		currentAssignment.set( node, i );
	}

	public void increaseNodeAssignment( Node node ) {
		currentAssignment.increase( node, 1 );
	}

	public void checkSupplies() {
		AbstractNetwork network = this.network.getAsStaticNetwork();
		IdentifiableIntegerMapping<Node> supplies = currentAssignment;

		GraphInstanceChecker checker = new GraphInstanceChecker( network, supplies );
		checker.supplyChecker();

		if( checker.hasRun() ) {
			currentAssignment = checker.getNewSupplies();
			sources = checker.getNewSources();
			for( Node oldSource : checker.getDeletedSources() ) {
				//mapping.setIsSourceNode( oldSource, false );
				mapping.setIsDeletedSourceNode( oldSource, true );
			}
		} else
			throw new AssertionError( DefaultLoc.getSingleton().getString( "converter.NoCheckException" ) );
	}

	public Node newNode() {
		Node node = new Node( nodeCount );
		nodeCount++;
		network.setNode( node );
		return node;
	}

	public void addSource( Node node ) {
		sources.add( node );
	}

	public void ensureCapacities() {
		nodeCapacities.setDomainSize( network.numberOfNodes() );

		edgeCapacities.setDomainSize( network.numberOfEdges() * network.numberOfEdges() ); // TODO weird???
	}

	public void increaseNodeCapacity( Node node, int i ) {
		nodeCapacities.increase( node, i );
	}

	public Edge getEdge( Node lastNode, Node node ) {
		return network.getEdge( lastNode, node );
	}

	public Edge newEdge( Node lastNode, Node node ) {
		Edge edge = new Edge( edgeIndex++, lastNode, node );
		network.setEdge( edge );
		edgeCapacities.set( edge, 0 );
		return edge;
	}

	public void increaseEdgeCapacity( Edge edge, int i ) {
		edgeCapacities.increase( edge, i );
	}

	public boolean contains( Edge edge ) {
		return network.contains( edge );
	}

	public Node getNode( int i ) {
		return network.getNode( i );
	}

	public IdentifiableIntegerMapping<Edge> transitTimes() {
		return transitTimes;
	}

	public IdentifiableIntegerMapping<Node> currentAssignment() {
		return currentAssignment;
	}

	public void addEdge( Edge neu, int edgeCapacity, int transitTime, double exactTransitTime ) {
		network.addEdge( neu );
		edgeCapacities.set( neu, edgeCapacity );
		transitTimes.set( neu, transitTime );
		exactTransitTimes.set( neu, exactTransitTime );
	}

	public NodePositionMapping getNodeCoordinates() {
		NodePositionMapping nodePositionMapping = new NodePositionMapping( network.numberOfNodes() );
		for( Node n : network.nodes() ) {
			NodeRectangle rect = mapping.getNodeRectangles().get( n );
			final double zs = mapping.getNodeFloorMapping().get( n ) * VisualizationOptionManager.getFloorDistance();
			final Vector3 v = new Vector3( rect.getCenterX(), rect.getCenterY(), zs );

			nodePositionMapping.set( n, v );
		}
		return nodePositionMapping;
	}

	public EarliestArrivalFlowProblem getEAFP( int upperBound ) {
		return new EarliestArrivalFlowProblem( edgeCapacities, network.getAsStaticNetwork(), nodeCapacities, supersink, sources, upperBound, transitTimes, currentAssignment );
	}

	@Override
	public Iterator<Node> iterator() {
		return network.nodes().iterator();
	}

	public IdentifiableIntegerMapping<Edge> edgeCapacities() {
		return edgeCapacities;
	}

	public void divide( Edge edge, double factor ) {
		exactTransitTimes.divide( edge, factor );
	}
}
