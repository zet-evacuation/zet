/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.EATAPPROX;

import de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.SimpleResidualGraph;
import de.tu_berlin.math.coga.datastructure.Tuple;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.network.NetworkInterface;
import ds.mapping.IdentifiableIntegerMapping;
import ds.mapping.IdentifiableObjectMapping;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class HidingResidualGraph extends SimpleResidualGraph implements NetworkInterface {
	IdentifiableIntegerMapping<Node> current;
	NetworkInterface network;
	IdentifiableIntegerMapping<Edge> capacities;
	IdentifiableIntegerMapping<Edge> transitTimes;

	// Stores a list of reverse edges that go from node to another node and an edge object which already has been created.
	IdentifiableObjectMapping<Node, KnownEdgesList> knownEdges;

	int timeHorizon;
	List<Node> sources;
	List<Node> sinks;
	int M = 100000;

	final int SUPER_SOURCE = 0;
	final int SUPER_SINK;
	final int BASE_SOURCE = 1;
	final int BASE_SINK;
	final int NODES;

	final int nodeCount;
	private IdentifiableIntegerMapping<Node> supplies;

	public HidingResidualGraph( NetworkInterface network, IdentifiableIntegerMapping<Edge> capacities, IdentifiableIntegerMapping<Edge> transitTimes, int timeHorizon,  List<Node> sources, List<Node> sinks, IdentifiableIntegerMapping<Node> supplies ) {
		super( network.numberOfNodes()* (timeHorizon+1) + 1 + sources.size() + 1 + sinks.size(), (timeHorizon+1)*(network.numberOfEdges() + sources.size() + sinks.size() ) + sources.size() + sinks.size() );
		// Formel zur Bestimmung der tatsächlichen Knotenzahl basierend auf n und m:
		// Knoten: n*(t+1) + 1 + #sources + 1 + #sinks
		// Kanten: m + (t+1) + #sources + #sinks + (t+1)*#sources + (t+1)*#sinks

		this.network = network;
		this.capacities = capacities;
		this.transitTimes = transitTimes;
		this.timeHorizon = timeHorizon;
		this.sources = sources;
		this.sinks = sinks;
		this.supplies = supplies;

		knownEdges = new IdentifiableObjectMapping<>( nodes, KnownEdgesList.class );

		current = new IdentifiableIntegerMapping<>( network.numberOfEdges()* (timeHorizon+1) + 1 + sources.size() + 1 + sinks.size() );

		NODES = BASE_SOURCE + sources.size();
		BASE_SINK = NODES + (timeHorizon+1)*network.numberOfNodes();
		SUPER_SINK = BASE_SINK + sinks.size();
		nodeCount = SUPER_SINK + 1;
	}

	void build() {
		System.out.println( "Create nodes" );
		createNodes();

		for( int i = 0; i < nodeCount; ++i ) {
			knownEdges.set( nodes.get( i ), new KnownEdgesList() );
		}

		System.out.println( "Create edges" );
		createEdges();

		visibleNodeCount = 2 + sources.size() + sinks.size() + network.numberOfNodes();
		System.out.println( "Done" );
	}

	void createNodes() {
		int id = 0;
		// Super-Source
		assert id == SUPER_SOURCE;
		nodes.add( new Node( id++ ) );
		// sources
		assert id == BASE_SOURCE;
		for( int i = 0; i < sources.size(); ++i )
			nodes.add( new Node( id++ ) );
		// nodes
		assert id == NODES;
		for( int t = 0; t <= timeHorizon; ++t )
			for( int i = 0; i < network.numberOfNodes(); ++i )
				nodes.add( new Node( id++ ) );
		// sinks
		assert id == BASE_SINK;
		for( int i = 0; i < sinks.size(); ++i )
			nodes.add( new Node( id++ ) );
		// Super-Sink
		assert id == SUPER_SINK;
		nodes.add( new Node( id++ ) );

	}



		int edgeCounter = 0;
	private void createEdges() {

		// Outgoing for super source. Without incoming edges!
		Node v = nodes.get( SUPER_SOURCE );
		first.set( v, edgeCounter );
		current.set( v, edgeCounter );
		// Generate outgoing arcs from supersource to sources
		for( int i = 0; i < sources.size(); ++i ) {
			Node baseSource = nodes.get( BASE_SOURCE + i ); // todo better!
			createEdge( v, baseSource, supplies.get( sources.get( i ) ) );
		}
		last.set( v, edgeCounter );

		// Outgoing for base sources
		int counter = 0;
		for( Node source : sources ) {
			v = nodes.get( BASE_SOURCE + counter++ ); // The base source belonging to the counterth source
			//if( verbose )
			//	System.out.println( "Base source " + v + " belongs to original node " + source );
			first.set( v, edgeCounter );
			current.set( v, edgeCounter );

			// handle known reverse edges to lower levels
			assert knownEdges.get( v ).size() == 1;
			createReverseEdges( v );

			// Create the holdover arcs
			for( int t = 0; t <= timeHorizon; ++t ) {
				Edge e = createEdge( v, getCopy( source, t ), M );
				//* The edges are automatically ordered in correct order. */
				if( t == 0 )
					last.set( v, edgeCounter );
			}

		}

		// outgoing for all the nodes (without holdover)
		for( int t = 0; t <= timeHorizon; ++t ) {

			// Stores the gaps for backward arcs that are created later on
			HashMap<Node,Integer> gaps = new HashMap<>();

			for( Node node : network ) {
				v = getCopy( node, t );
				//System.out.println( "Entering Node o" + node.id() + " at time " + t + " with id=" + v.id() );

				first.set( v, edgeCounter );
				current.set( v, edgeCounter );

				// If the node is a sink, we have to create the outgoing arc to base sink first
				// the super sink has negative layer, so it will come before all reverse arcs!
				if( sinks.contains( node ) ) {
					int sinkIndex = 0;
					for( Node sink : sinks ) {
						if( sink.equals( node ) )
							break;
						sinkIndex++;
					}
					//System.out.println( "Creating an edge to base sink starting from time " + t );
					createEdge( v, nodes.get( BASE_SINK + sinkIndex ), M );
				}

				createReverseEdges( v );

				// leave space for reverse edges coming later on.
				int gap = 0;
				for( Edge e : network.incomingEdges( node ) ) {
					if( transitTimes.get( e ) == 0 && e.start().id() > node.id() ) {
						gap++;
					}
				}
				gaps.put( node, edgeCounter );
				edgeCounter += gap;

				PriorityQueue<PriorityEdge> queue = new PriorityQueue<>();

				for( Edge e : network.outgoingEdges( node ) ) {
//					if( transitTimes.get( e ) == 0 ) {
//						System.out.println( "Arc from " + e.start() + " to " + e.end() + " with transit time 0." );
//					}

					if( t + transitTimes.get( e ) > timeHorizon )
						continue;
					Node target = getCopy( e.end(), t + transitTimes.get( e ) );
					//System.out.println( "Creating an edge from o" + node + " to o" + e.end() + " starting at time layer " + t );
					//PriorityEdge pe = new PriorityEdge( createEdge( v, target, 1 ), t + transitTimes.get( e ) );
					PriorityEdge pe = new PriorityEdge( v, target, capacities.get( e ), t + transitTimes.get( e ), e.start(), e.end(), transitTimes.get( e ) );
					queue.add( pe );
				}
				last.set( v, edgeCounter );
				while( !queue.isEmpty() ) {
					PriorityEdge pe = queue.poll();
					Edge e = createEdge( pe.from, pe.to, pe.capacity );
					if( pe.transitTime == 0 ) { // we have found an edge that goes to the same time horizon.
						last.set( v, edgeCounter );

						// we have to create the backward edge later on, if the edge goes to
						// a node with lower id.
						if( pe.originalTarget.id() < pe.originalFrom.id() ) {
							int id = gaps.get( pe.originalTarget );
							createLateReverseEdge( e, id++ );
							gaps.put( pe.originalTarget, id );
						}
					}
				}
			}
		}


		// Outgoing for base sinks
		counter = 0;
		for( Node sink : sinks ) {
			v = nodes.get( BASE_SINK + counter++ ); // The base source belonging to the counterth source

			first.set( v, edgeCounter );
			last.set( v, edgeCounter );

			// handle known reverse edges to lower levels
			// These are the arcs that are to be deleted!

			createReverseEdges( v );

			createEdge( v, nodes.get( SUPER_SINK ), -supplies.get( sink ) ); // TODO: sink capacity
			last.set( v, edgeCounter );
		}

		// Incoming to super sink


		first.set( nodes.get( SUPER_SINK ), edgeCounter );
		current.set( nodes.get( SUPER_SINK ), edgeCounter );
		createReverseEdges( nodes.get( SUPER_SINK ) );
		last.set( nodes.get( SUPER_SINK ), edgeCounter );

	}

	private Edge createEdge( Node from, Node to, int capacity ) {
		Edge newEdge = new Edge( edgeCounter++, from, to );
		//System.out.println( "Edge from " + from.id() + " to " + to.id() );
		edges.add( newEdge );
		residualCapacity.add( newEdge, capacity );
		isReverseEdge.add( newEdge, false );

		// store the information of the reverse edge in some kind of map
		Tuple<Node,Edge> tup = new Tuple<>( from, newEdge );
		knownEdges.get( to ).add( tup );
		return newEdge;
	}


	private void createReverseEdges( Node v ) {
		KnownEdgesList list = knownEdges.get( v );

		for( Tuple<Node,Edge> tup : list ) {
			Edge original = tup.getV();
			Edge newReverseEdge = new Edge( edgeCounter++, v, tup.getU() );
			//System.out.println( "Reverse Edge from " + v.id() + " to " + tup.getU().id() );
			edges.add( newReverseEdge );
			residualCapacity.add( newReverseEdge, 0 );
			isReverseEdge.add( newReverseEdge, true );

			reverseEdge.set( newReverseEdge, original );
			reverseEdge.set( original, newReverseEdge );
		}
	}

	private void createLateReverseEdge( Edge original, int id ) {
		Edge newReverseEdge = new Edge( id, original.end(), original.start() );
		//System.out.println( "----------------------- LATE REVERSE EDGE " + newReverseEdge );

		edges.add( newReverseEdge );
		residualCapacity.add( newReverseEdge, 0 );
		isReverseEdge.add( newReverseEdge, true );

		reverseEdge.set( newReverseEdge, original );
		reverseEdge.set( original, newReverseEdge );
	}


	public final Node getCopy( Node node, int t ) {
		return nodes.get( NODES + network.numberOfNodes()* t + node.id() );
	}

	public final int getLayer( int nodeId ) {
		if( nodeId < 0 )
			throw new AssertionError( "Node IDs start with 0." );
		else if( nodeId == SUPER_SOURCE || nodeId == SUPER_SINK )
			return -2;
		else if( nodeId > SUPER_SOURCE && nodeId < NODES || nodeId >= BASE_SINK )
			return -1;
		else {
			int tl = (nodeId - NODES)/network.numberOfNodes();
			return tl;
		}
	}

	public final int getOriginalNode( int nodeId ) {
		assert getLayer( nodeId ) >= 0;

		int tl = getLayer( nodeId );
		return nodeId - NODES - tl * network.numberOfNodes();
	}

	private int lastLayer = 0;

	/**
	 *
	 * @param time
	 * @return a list of affected nodes.
	 */
	public Set<Edge> activateTimeLayer( int time ) {
		if( time != lastLayer + 1 )
			throw new IllegalArgumentException( "More than one timestep!" );

		Set<Edge> edgesSet = new HashSet<>();

		// Iterate through all nodes and check, if last can be set forther forward!
		for( int t = 0; t <= timeHorizon; ++t ) {
			for( Node node : network ) {
				Node v = getCopy( node, t );
				edgesSet.addAll( activeTimeLayerForNode( time, v ) );
			}
		}

		for( int i = BASE_SOURCE; i < NODES; ++i ) {
			Node v = nodes.get( i );
			edgesSet.addAll( activeTimeLayerForNode( time, v ) );
		}

		visibleNodeCount += network.numberOfNodes();
		//System.out.println( "Now: number of nodes set to " + visibleNodeCount );


		lastLayer = time;
		return edgesSet;
	}

	private Set<Edge> activeTimeLayerForNode( int time, Node v ) {
		int nodeStop = first.get( nodes.get( v.id()+1 ) );

		int newLastIndex = last.get( v );

		HashSet<Edge> set = new HashSet<>();

		do {
			// zur zeit zeigt lastIndex auf einen gültigen wert.
			// überprüfe die nächste kante.

			Edge e = edges.get( newLastIndex );
			if( getLayer( e.end().id() ) == time ) {
				// we have found a new edge!
				set.add( e );
				newLastIndex++;
			} else
				break;
		} while( newLastIndex < nodeStop );
		last.set( v, newLastIndex );
		return set;
	}

	@Override
	public int numberOfEdges() {
		return super.numberOfEdges()/2;
	}

	int visibleNodeCount;
	int getCurrentVisibleNodeCount() {
		return visibleNodeCount;
	}


	private final static class KnownEdgesList extends LinkedList<Tuple<Node, Edge>> {}

	private final static class PriorityEdge implements Comparable<PriorityEdge> {
		private Node from;
		private Node to;
		private int capacity;
		private int t;
		private Node originalTarget;
		private Node originalFrom;
		private int transitTime;

		private PriorityEdge( Node from, Node to, int capacity, int t, Node originalFrom, Node originalTarget, int transitTime ) throws NullPointerException {
			this.from = from;
			this.to = to;
			this.capacity = capacity;
			this.t = t;
			this.originalTarget = originalTarget;
			this.originalFrom = originalFrom;
			this.transitTime = transitTime;
		}


		@Override
		public int compareTo( PriorityEdge o ) {
			return t-o.t;
		}

	}
}
