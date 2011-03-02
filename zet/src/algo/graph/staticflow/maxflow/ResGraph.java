/**
 * ResGraph.java
 * Created: 25.02.2011, 18:33:02
 */
package algo.graph.staticflow.maxflow;

import algo.graph.staticflow.maxflow.PushRelabelHighestLabel.ResidualEdge;
import ds.graph.ArraySet;
import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.IdentifiableBooleanMapping;
import ds.graph.IdentifiableCollection;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Network;
import ds.graph.Node;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class ResGraph implements Graph {

	protected ArraySet<Node> nodes;
	protected ArraySet<Edge> edges;

	// new mapping replacing the old stuff
	protected IdentifiableBooleanMapping<Edge> isReverseEdge;	// indicates if a given edge is residual or was contained in the original graph
	protected IdentifiableIntegerMapping<Edge> residualCapacity; // gives the residual capacity of a given edge
	protected IdentifiableObjectMapping<Edge,Edge> reverseEdge; // gives the residual edge for a given edge

	/** An array of residual edges for the network. */
	protected ResidualEdge[] residualEdges;
	/** The index of the first outgoing edge of a node in the edges array. */
	protected IdentifiableIntegerMapping<Node> first;
	/** The index of the last outgoing edge of a node in the edges array. */
	protected IdentifiableIntegerMapping<Node> last;

	/**
	 * Initializes the graph with a given size
	 * @param n
	 * @param m
	 */
	ResGraph( int n, int m ) {
		edges = new ArraySet<Edge>( Edge.class, 2*m );
		
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
	public int indegree( Node node ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public int outdegree( Node node ) {
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
	public void setNode( Node node ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public void setEdge( Edge edge ) {
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

	@Override
	public String deepToString() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	@Override
	public Network getAsStaticNetwork() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	int getResidualCapacity( int i ) {
		return residualCapacity.get( edges.get( i ) );
	}

	int getResidualCapacity( Edge e ) {
		return residualCapacity.get( e );
	}

	boolean isReverseEdge( Edge a ) {
		 return isReverseEdge( a );
	}

	int getReverseResidualCapacity( Edge e ) {
		return residualCapacity.get( reverseEdge.get( e ) );
	}

	int getReverseResidualCapacity( int i ) {
		return residualCapacity.get( reverseEdge.get( edges.get( i ) ) );
	}

	void augment( Edge a, int delta ) {
		residualCapacity.decrease( a, delta );
		residualCapacity.increase( reverseEdge.get( a ), delta );
	}

	Edge getReverseEdge( Edge e ) {
		return reverseEdge.get( e );
	}

}
