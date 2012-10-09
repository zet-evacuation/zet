/**
 *
 * ResGraph.java
 * Created: 25.02.2011, 18:33:02
 */
package de.tu_berlin.math.coga.algorithm.networkflow.maximumflow;

import ds.graph.ArraySet;
import ds.graph.DirectedGraph;
import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import ds.graph.Node;
import ds.graph.network.AbstractNetwork;
import ds.mapping.IdentifiableBooleanMapping;
import ds.mapping.IdentifiableIntegerMapping;
import ds.mapping.IdentifiableObjectMapping;
import java.util.Iterator;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ResidualGraph implements DirectedGraph {

	protected ArraySet<Node> nodes;
	protected ArraySet<Edge> edges;

	// new mapping replacing the old stuff
	protected IdentifiableBooleanMapping<Edge> isReverseEdge;	// indicates if a given edge is residual or was contained in the original graph
	protected IdentifiableIntegerMapping<Edge> residualCapacity; // gives the residual capacity of a given edge
	protected IdentifiableObjectMapping<Edge,Edge> reverseEdge; // gives the residual edge for a given edge

	public IdentifiableObjectMapping<Edge,Edge> originalResidualEdgeMapping;
	
	/** The index of the first outgoing edge of a node in the edges array. */
	protected IdentifiableIntegerMapping<Node> first;
	/** The index of the last outgoing edge of a node in the edges array. */
	protected IdentifiableIntegerMapping<Node> last;

	/**
	 * Initializes the graph with a given size
	 * @param n
	 * @param m
	 */
	private int m;

	ResidualGraph( int n, int m ) {
		edges = new ArraySet<>( Edge.class, 2*m );
		nodes = new ArraySet<>( Node.class, n );
		first = new IdentifiableIntegerMapping<>( n ); // first outgoing edge (index)
		last = new IdentifiableIntegerMapping<>( n ); // last outgoing edge (index)
		isReverseEdge = new IdentifiableBooleanMapping<>( 2*m );
		residualCapacity = new IdentifiableIntegerMapping<>( 2*m );
		reverseEdge = new IdentifiableObjectMapping<>( 2*m, Edge.class );
		originalResidualEdgeMapping = new IdentifiableObjectMapping<>( 2*m, Edge.class );
		this.m = m;
	}

	/**
	 * Sets up the data structures. At first, creates reverse edges and orders the
	 * edges according to their tail nodes into the array. Thus, the incident
	 * edges to a vertex can be searched by a run through the array.
	 * 
	 * @param network
	 * @param capacities
	 * @param current  
	 */
	protected void init( AbstractNetwork network, IdentifiableIntegerMapping<Edge> capacities, IdentifiableIntegerMapping<Node> current ) {
		// set up residual edges
		int edgeCounter = 0;
		int[] temp = new int[edges.getCapacity()];
		for( Node v : network ) {
			first.set( v, edgeCounter );
			current.set( v, edgeCounter );
			// add the outgoing edges to the arc list
			for( Edge e : network.outgoingEdges( v ) ) {
				//residualEdges[edgeCounter] = new ResidualEdge( edgeCounter, e.start(), e.end(), getProblem().getCapacities().get( e ), false );
				Edge ne = new Edge(edgeCounter, e.start(), e.end() );
				originalResidualEdgeMapping.set( ne, e );
				edges.add( ne );
				residualCapacity.add( ne, capacities.get( e ) );
				isReverseEdge.add( ne, false );
				//reverseEdge.set( e, e )
				//residualEdges[edgeCounter].original = e;
				temp[e.id()] = edgeCounter++;
			}
			// add the reverse edge for incoming edges to the arc list (they are also outgoing!
			for( Edge e : network.incomingEdges( v ) ) {
				//residualEdges[edgeCounter] = new ResidualEdge( edgeCounter, e.end(), e.start(), 0, true );
				Edge ne = new Edge( edgeCounter, e.end(), e.start() );
								
				edges.add( ne );
				residualCapacity.add( ne, 0 );
				isReverseEdge.add( ne, true );

				temp[e.id() + m] = edgeCounter++;
			}
			last.set( v, edgeCounter );
		}
		for( int i = 0; i < m; ++i ) {
			//residualEdges[temp[i]].reverse = residualEdges[temp[i + m]];
			reverseEdge.set( edges.get( temp[i] ), edges.get( temp[i+m]));
			//residualEdges[temp[i + m]].reverse = residualEdges[temp[i]];
			reverseEdge.set( edges.get( temp[i+m]), edges.get(temp[i]));
		}

//		// initialize excesses
//		excess.set( source, 0 );
//		for( int i = first.get( source ); i < last.get( source ); ++i ) {
//			ResidualEdge e = residualEdges[i];
//			if( e.end().id() != source.id() ) {
//				pushes++;
//				final int delta = e.residualCapacity;
//				e.residualCapacity -= delta;
//				e.reverse.residualCapacity += delta;
//				excess.increase( e.end(), delta );
//			}
//		}

//		for( Node v : getProblem().getNetwork() ) {
//			final int id = v.id();
//			if( id == sink.id() ) {
//				distanceLabels.set( v, 0 );
//				inactiveBuckets.addInactive( 0, v );
//				continue;
//			}
//			distanceLabels.set( v, id == source.id() ? n : 1 );
//			if( excess.get( v ) > 0 )
//				activeBuckets.addActive( 1, v );
//			else if( distanceLabels.get( v ) < n )
//				inactiveBuckets.addInactive( 1, v );
//		}
//		activeBuckets.setdMax( 1 );
	}

	@Override
	public boolean isDirected() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public IdentifiableCollection<Edge> edges() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public IdentifiableCollection<Node> nodes() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public int numberOfEdges() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public int numberOfNodes() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public IdentifiableCollection<Edge> incidentEdges( Node node ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public IdentifiableCollection<Edge> incomingEdges( Node node ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public IdentifiableCollection<Edge> outgoingEdges( Node node ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public IdentifiableCollection<Node> adjacentNodes( Node node ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public IdentifiableCollection<Node> predecessorNodes( Node node ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public IdentifiableCollection<Node> successorNodes( Node node ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public int degree( Node node ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public int inDegree( Node node ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public int outDegree( Node node ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public boolean contains( Edge edge ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public boolean contains( Node node ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public Edge getEdge( int id ) {
		return edges.get( id );
	}

	@Override
	public Edge getEdge( Node start, Node end ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public IdentifiableCollection<Edge> getEdges( Node start, Node end ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public Node getNode( int id ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public boolean existsPath( Node start, Node end ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	int getResidualCapacity( int i ) {
		return residualCapacity.get( edges.get( i ) );
	}

	int getResidualCapacity( Edge e ) {
		return residualCapacity.get( e );
	}

	boolean isReverseEdge( Edge a ) {
		 return isReverseEdge.get( a );
	}

	int getReverseResidualCapacity( Edge e ) {
		return residualCapacity.get( reverseEdge.get( e ) );
	}

	int getReverseResidualCapacity( int i ) {
		return residualCapacity.get( reverseEdge.get( edges.get( i ) ) );
	}

	void augment( Edge a, int delta ) {
//		System.out.println( "Augmenting " + a + " by " + delta );
		residualCapacity.decrease( a, delta );
		residualCapacity.increase( reverseEdge.get( a ), delta );
	}

	Edge getReverseEdge( Edge e ) {
		return reverseEdge.get( e );
	}

	int getFirst( Node node ) {
		return first.get( node );
	}

	int getLast( Node node ) {
		return last.get( node );
	}

	int augmentMax( Edge e, int get ) {
		final int delta = residualCapacity.get( e ) < get ? residualCapacity.get( e ) : get;
		residualCapacity.decrease( e, delta );
		residualCapacity.increase( reverseEdge.get( e ), delta );
		return delta;
	}

	@Override
	public Iterator<Node> iterator() {
		return nodes.iterator();
	}
}
