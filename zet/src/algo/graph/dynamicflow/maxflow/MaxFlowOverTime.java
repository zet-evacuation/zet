/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/*
 * MaxFlowOverTime.java
 * Created on 22. Januar 2008, 03:39
 */
package algo.graph.dynamicflow.maxflow;

import algo.graph.Flags;
import algo.graph.staticflow.mincost.MinimumMeanCycleCancelling;
import algo.graph.util.PathDecomposition;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.graph.IdentifiableCollection;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.graph.DynamicPath;
import ds.graph.flow.FlowOverTimePath;
import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.GraphLocalization;
import ds.mapping.IdentifiableIntegerMapping;
import ds.graph.network.AbstractNetwork;
import ds.graph.Node;
import ds.graph.flow.PathBasedFlow;
import ds.graph.StaticPath;
import ds.graph.flow.StaticPathFlow;
import ds.graph.problem.MinimumCostFlowProblem;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * The class {@code MaxFlowOverTime} solves the max flow over time 
 * problem.
 * 
 * @author Gordon Schlechter
 */
public class MaxFlowOverTime extends Algorithm<MaximumFlowOverTimeProblem, PathBasedFlowOverTime> {
	private AbstractNetwork network;
	private IdentifiableIntegerMapping<Edge> edgeCapacities;
	private List<Node> sinks;
	private List<Node> sources;
	private IdentifiableIntegerMapping<Node> zeroSupplies;
	private Node superNode;
	private Node superSink;
	private Node superSource;
	private int timeHorizon;
	private IdentifiableIntegerMapping<Edge> transitTimes;
	private LinkedList<Node> newNodes;
	private LinkedList<Edge> newEdges;
	protected PathBasedFlowOverTime maxFlowOT;

	/** Creates a new instance of MaxFlowOverTime */
	public MaxFlowOverTime() {
		newNodes = new LinkedList<>();
		newEdges = new LinkedList<>();
	}

	/** Creates the supplies for all nodes in the network. The value of the 
	 * supplies of each node is 0. */
	private void createZeroSupply() {
		int supplies = 0;
		zeroSupplies = new IdentifiableIntegerMapping<>( supplies );

		for( Node n : network.nodes() )
			zeroSupplies.set( n, 0 );
	}

	/** Creates a new super source. A super source is connected with all
	 * sources in the original network. These connecting edges have a transit 
	 * time of 0 and a capacity of MAX_VALUE for an integer. */
	private void createSuperSource() {
		int nodeCount = network.getNodeCapacity();
		superSource = new Node( nodeCount );
		nodeCount++;
		network.setNodeCapacity( nodeCount );
		network.setNode( superSource );
		newNodes.add( superSource );

		int edgeCount = network.getEdgeCapacity();
		network.setEdgeCapacity( edgeCount + sources.size() );

		for( Node source : sources ) {
			Edge newEdge = new Edge( edgeCount, superSource, source );
			edgeCapacities.set( newEdge, Integer.MAX_VALUE );
			transitTimes.set( newEdge, 0 );
			network.setEdge( newEdge );
			newEdges.add( newEdge );
			edgeCount++;
		}
	}

	/** Creates a new super sink. A super sink is connected with all
	 * sinks in the original network. These connecting edges have a transit 
	 * time of 0 and a capacity of MAX_VALUE for an integer. */
	private void createSuperSink() {
		int nodeCount = network.getNodeCapacity();
		superSink = new Node( nodeCount );
		nodeCount++;
		network.setNodeCapacity( nodeCount );
		network.setNode( superSink );
		newNodes.add( superSink );

		int edgeCount = network.getEdgeCapacity();
		network.setEdgeCapacity( edgeCount + sinks.size() );

		for( Node sink : sinks ) {
			Edge newEdge = new Edge( edgeCount, sink, superSink );
			edgeCapacities.set( newEdge, Integer.MAX_VALUE );
			transitTimes.set( newEdge, 0 );
			network.setEdge( newEdge );
			newEdges.add( newEdge );
			edgeCount++;
		}
	}

	/** Creates an edge between the super sink and the super source.
	 * This edge has a capacity of MAX_VALUE for an integer. The transit 
	 * time of this edge is -( the given time horizin + 1). 
	 */
	private void createEdgeBetween() {

		int edgeCount = network.getEdgeCapacity();
		network.setEdgeCapacity( edgeCount + 1 );

		Edge edgeBetween = new Edge( edgeCount, superSink, superSource );

		edgeCapacities.set( edgeBetween, Integer.MAX_VALUE );
		transitTimes.set( edgeBetween, -(timeHorizon + 1) );

		network.setEdge( edgeBetween );

		newEdges.add( edgeBetween );
	}

	/** In the first step, a super source is created, if there is more 
	 * than one source. After that, a super sink is created, if it 
	 * is necessary. At the end the edge between these two nodes is created.
	 */
	private void createSuperNodes() {

		if( sources.size() > 1 )
			createSuperSource();
		else
			superSource = sources.get( 0 );

		if( sinks.size() > 1 )
			createSuperSink();
		else
			superSink = sinks.get( 0 );

		createEdgeBetween();
	}

	/** andere Reduktion, noch nicht fertig implementiert und getestet.
	 * Methode zum Entfernen fehlt noch...
	 */
	public void reduction() {
		int nodeCount = network.getNodeCapacity();
		superNode = new Node( nodeCount );
		nodeCount++;
		network.setNodeCapacity( nodeCount );
		network.setNode( superNode );

		int edgeCount1 = network.getEdgeCapacity();
		network.setEdgeCapacity( edgeCount1 + sources.size() );

		for( Node source : sources ) {
			Edge newEdge = new Edge( edgeCount1, superNode, source );
			edgeCapacities.set( newEdge, Integer.MAX_VALUE );
			transitTimes.set( newEdge, 0 );
			network.setEdge( newEdge );
			newEdges.add( newEdge );
			edgeCount1++;
		}

		int edgeCount2 = network.getEdgeCapacity();
		network.setEdgeCapacity( edgeCount2 + sinks.size() );

		for( Node sink : sinks ) {
			Edge newEdge = new Edge( edgeCount2, sink, superNode );
			edgeCapacities.set( newEdge, Integer.MAX_VALUE );
			transitTimes.set( newEdge, -(timeHorizon + 1) );
			network.setEdge( newEdge );
			newEdges.add( newEdge );
			edgeCount2++;
		}
	}
	


	/** Hides the added super node and edges in the network. After this you got 
	 * back the original network.
	 */
	private void reconstruction() {
		int i = network.getNodeCapacity();
		network.setNodeCapacity( i - 1 );

		int g = network.getEdgeCapacity();
		network.setEdgeCapacity( g - newEdges.size() );
	}

	/** Hides the added nodes and edges in the network. After this you got 
	 * back the original network.
	 */
	private void hideAddedInNetwork() {
		/**for (Edge e : newEdges){
		network.setHidden(e, true);
		}
		for (Node n : newNodes) {
		network.setHidden(n, true);
		}*/
		int i = network.getNodeCapacity();
		network.setNodeCapacity( i - newNodes.size() );

		int g = network.getEdgeCapacity();
		network.setEdgeCapacity( g - newEdges.size() );
	}

	/** Hides the added egdes in the flow. After this the flow contains
	 * only edges from the original network.
	 */
	private void hideAddedInFlow( IdentifiableIntegerMapping<Edge> flow ) {

		int i = flow.getDomainSize();
		flow.setDomainSize( i - newEdges.size() );

	}

	/** Hides the added nodes und edges in the network and also hides
	 * the added edges in the flow.
	 */
	private void hideAdded( IdentifiableIntegerMapping<Edge> flow ) {
		hideAddedInNetwork();
		hideAddedInFlow( flow );
	}

	/** Creates dynamic Flow out of the given static flow. At first static 
	 * flow is divided in the different pathes and then it is added to the 
	 * dynamic flow,  if the conditions are met. */
	private PathBasedFlowOverTime translateIntoMaxFlow( PathBasedFlow minCostFlow ) {
		PathBasedFlowOverTime mFlow = new PathBasedFlowOverTime();

		for( StaticPathFlow staticPathFlow : minCostFlow ) {
			if( staticPathFlow.getAmount() == 0 )
				continue;
			StaticPath staticPath = staticPathFlow.getPath();
			int path_transit_time = 0;
			for( Edge e : staticPath )
				path_transit_time += transitTimes.get( e );

			// Add this path only in case that our given time is long 
			// enough to send anything at all over this path
			if( timeHorizon > path_transit_time ) {
				DynamicPath dynamicPath = new DynamicPath( staticPath );
				FlowOverTimePath dynamicPathFlow = new FlowOverTimePath( dynamicPath, staticPathFlow.getAmount(), (timeHorizon - path_transit_time) * staticPathFlow.getAmount() );
				mFlow.addPathFlow( dynamicPathFlow );
			}
		}

		return mFlow;
	}

	public PathBasedFlowOverTime getDynamicFlow() {
		return maxFlowOT;
	}

	@Override
	protected PathBasedFlowOverTime runAlgorithm( MaximumFlowOverTimeProblem problem ) {
		sinks = problem.getSinks();
		sources = problem.getSources();
		this.network = problem.getNetwork();
		edgeCapacities = problem.getCapacities();
		transitTimes = problem.getTransitTimes();
		timeHorizon = problem.getTimeHorizon();

		if( (sources == null) || (sinks == null) )
			throw new IllegalArgumentException( GraphLocalization.getSingleton().getString( "algo.graph.MaxFlowOverTime.SpecifySourceSinkFirst" ) );

		if( (sources.isEmpty()) || (sinks.isEmpty()) ) {
			maxFlowOT = new PathBasedFlowOverTime();
			return maxFlowOT;
		}

		reduction();
		createZeroSupply();

		IdentifiableIntegerMapping<Edge> flow = null;

		MinimumCostFlowProblem p = new MinimumCostFlowProblem( network, edgeCapacities, transitTimes, zeroSupplies );
		Algorithm<MinimumCostFlowProblem, IdentifiableIntegerMapping<Edge>> algorithm = new MinimumMeanCycleCancelling();
		algorithm.setProblem( p );
		algorithm.run();
		flow = algorithm.getSolution();

		//SuccessiveShortestPath algo = new SuccessiveShortestPath(network, zeroSupplies, edgeCapacities, transitTimes);
		//algo.run();
		//flow = algo.getFlow();

		reconstruction();
		hideAddedInFlow( flow );

		PathBasedFlow minCostFlow = PathDecomposition.calculatePathDecomposition( network, sources, sinks, flow );

		maxFlowOT = translateIntoMaxFlow( minCostFlow );

		return maxFlowOT;
	}
}
