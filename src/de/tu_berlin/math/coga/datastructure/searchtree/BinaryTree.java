/**
 * BinaryTree.java
 * Created: 23.07.2012, 10:50:17
 */
package de.tu_berlin.math.coga.datastructure.searchtree;

import ds.collection.ListSequence;
import ds.graph.ArraySet;
import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.GraphLocalization;
import ds.graph.IdentifiableCollection;
import ds.graph.Node;
import ds.graph.network.AbstractNetwork;
import ds.mapping.IdentifiableIntegerMapping;
import ds.mapping.IdentifiableObjectMapping;
import java.util.Iterator;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BinaryTree implements Graph, Iterable<Node> {
	/** The nodes of the network. Must not be null. */
	protected ArraySet<Node> nodes;
	/** The edges of the network. Must not be null. */
	protected ArraySet<Edge> edges;
	int size = -1;
	/**
	 * Caches the edges incident to a node for all nodes in the graph.
	 * Must not be null.
	 */
//	protected IdentifiableObjectMapping<Node,Edge> incidentEdges;
	/**
	 * Caches the edges ending at a node for all nodes in the graph.
	 * Must not be null.
	 */
	protected IdentifiableObjectMapping<Node, Edge> incoming;
	/**
	 * Caches the edges starting at a node for all nodes in the graph.
	 * Must not be null.
	 */
	protected IdentifiableObjectMapping<Node, Edge> right;
	protected IdentifiableObjectMapping<Node, Edge> left;
	/**
	 * Caches the number of edges incident to a node for all nodes in the graph.
	 * Must not be null.
	 */
//	protected IdentifiableIntegerMapping<Node> degree;
	/**
	 * Caches the number of edges ending at a node for all nodes in the graph.
	 * Must not be null.
	 */
//	protected IdentifiableIntegerMapping<Node> indegree;
	/**
	 * Caches the number of edges starting at a node for all nodes in the graph.
	 * Must not be null.
	 */
	protected IdentifiableIntegerMapping<Node> outdegree;
	
	protected Node root;
	
	/**
	 * Creates a new AbstractNetwork with the specified capacities for edges and nodes.
	 * Runtime O(max(initialNodeCapacity, initialEdgeCapacity)). Assumes that 0 is
	 * to be the root.
	 * @param initialNodeCapacity the number of nodes that can belong to the graph
	 */
	public BinaryTree( int initialNodeCapacity ) {
		this( initialNodeCapacity, 0 );
	}
	
	public BinaryTree( int initialNodeCapacity, int root ) {
		if( root < 0 || root >= initialNodeCapacity )
			throw new IllegalArgumentException( "Root index must be between 0 and " + initialNodeCapacity + ". Was: " + root );
		size = initialNodeCapacity;
		edges = new ArraySet<>( Edge.class, initialNodeCapacity-1 );
		nodes = new ArraySet<>( Node.class, initialNodeCapacity );
		for( int i = 0; i < initialNodeCapacity; i++ )
			nodes.add( new Node( i ) );
		//incidentEdges = new IdentifiableObjectMapping<>( initialNodeCapacity, DependingListSequence.class );

		incoming = new IdentifiableObjectMapping<>( initialNodeCapacity, Edge.class );

		left = new IdentifiableObjectMapping<>( initialNodeCapacity, Edge.class );
		right = new IdentifiableObjectMapping<>( initialNodeCapacity, Edge.class );
		//for( Node node : nodes ) {
			//incidentEdges.set( node, new DependingListSequence<>( edges ) );
			//incomingEdges.set( node, new DependingListSequence<>( edges ) );
			//outgoingEdges.set( node, new DependingListSequence<>( edges ) );
		//}
		//degree = new IdentifiableIntegerMapping<>( initialNodeCapacity );
		//indegree = new IdentifiableIntegerMapping<>( initialNodeCapacity );
		outdegree = new IdentifiableIntegerMapping<>( initialNodeCapacity );
		this.root = nodes.get( root );
	}

	@Override
	public boolean isDirected() {
		return true;
	}

	@Override
	public IdentifiableCollection<Edge> edges() {
		return edges;
	}

	@Override
	public IdentifiableCollection<Node> nodes() {
		return nodes;
	}

	/**
	 * Returns the number of edges in this graph. Runtime O(1).
	 * @return the number of edges in this graph.
	 */
	@Override
	public int numberOfEdges() {
		return edges().size();
	}

	/**
	 * Returns the number of nodes in this graph. Runtime O(1).
	 * @return the number of nodes in this graph.
	 */
	@Override
	public int numberOfNodes() {
		return nodes().size();
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
		return indegree( node ) + outdegree( node );
	}

	@Override
	public int indegree( Node node ) {
		if( node.equals( root ) )
			return 0;
		else
			return incoming.get( node ) == null ? 0 : 1;
	}

	@Override
	public int outdegree( Node node ) {
		return left.get( node ) != null ? 1 + (right.get( node ) == null ? 0 : 1) : right.get( node ) == null ? 0 : 1;
	}

	/**
	 * Checks whether the specified edge is contained in this graph. Runtime
	 * O(1).
	 * @param edge the edge to be checked.
	 * @return {@code true} if the edge is contained in this graph,
	 * {@code false} otherwise.
	 */
	@Override
	public boolean contains( Edge edge ) {
		return edges.contains( edge );
	}

	/**
	 * Checks whether the specified node is contained in this graph. Runtime
	 * O(1).
	 * @param node the node to be checked.
	 * @return {@code true} if the node is contained in this graph,
	 * {@code false} otherwise.
	 */
	@Override
	public boolean contains( Node node ) {
		return nodes.contains( node );
	}

	/**
	 * Returns the edge with the specified id or {@code null} if the graph
	 * does not contain an edge with the specified id. Runtime O(1).
	 * @param id the id of the edge to be returned.
	 * @return the edge with the specified id or {@code null} if the graph
	 * does not contain an edge with the specified id.
	 */
	@Override
	public Edge getEdge( int id ) {
		return edges.get( id );
	}

	/**
	 * Returns an edge starting at {@code start} and ending at
	 * {@code end}. If no such edge exists, {@code null} is returned.
	 * Runtime O(outdegree(start)).
	 * @param start the start node of the edge to be returned.
	 * @param end the end node of the edge to be returned.
	 * @return an edge starting at {@code start} and ending at
	 * {@code end}.
	 */
	@Override
	public Edge getEdge( Node start, Node end ) {
		for( Edge edge : outgoingEdges( start ) )
			if( edge.end().equals( end ) )
				return edge;
		return null;
	}

	/**
	 * Adds the specified node to the graph by setting it to it ID's correct
	 * position in the internal data structures. If this position was occupied
	 * before it will be overwritten. Runtime O(1). Note, that the incoming and
	 * outgoing edges of any replaced node will be valid for the new one.
	 * @param node the node to be added to the graph.
	 */
	@Override
	public void setNode( Node node ) {
		if( nodes.get( node.id() ) == null ) {
			left.set( node, null ); // not necessary
			right.set( node, null );
			incoming.set( node, null );
			outdegree.set( node, 0 );
		}
		nodes.add( node );
	}
	private int idOfLastCreatedEdge = -1;

	public Edge createAndSetEdge( Node start, Node end ) {
		int id = idOfLastCreatedEdge + 1;
		while( id < size-1 && edges.get( id % (size-1) ) != null )
			id++;
		if( edges.get( id % (size-1) ) == null ) {
			Edge edge = new Edge( id % (size-1), start, end );
			setEdge( edge );
			idOfLastCreatedEdge = id % (size-1);
			return edge;
		} else
			throw new IllegalStateException( GraphLocalization.getSingleton().getString( "ds.Graph.NoCapacityException" ) );
	}
	
	public Edge setLeft( Node start, Node end ) {
		int id = idOfLastCreatedEdge + 1;
		while( id < size-1 && edges.get( id % (size-1) ) != null )
			id++;
		if( edges.get( id % (size-1) ) == null ) {
			Edge edge = new Edge( id % (size-1), start, end );
			setLeft( edge );
			idOfLastCreatedEdge = id % (size-1);
			return edge;
		} else
			throw new IllegalStateException( GraphLocalization.getSingleton().getString( "ds.Graph.NoCapacityException" ) );
	}
	
	public Edge setRight( Node start, Node end ) {
		int id = idOfLastCreatedEdge + 1;
		while( id < size-1 && edges.get( id % (size-1) ) != null )
			id++;
		if( edges.get( id % (size-1) ) == null ) {
			Edge edge = new Edge( id % (size-1), start, end );
			setRight( edge );
			idOfLastCreatedEdge = id % (size-1);
			return edge;
		} else
			throw new IllegalStateException( GraphLocalization.getSingleton().getString( "ds.Graph.NoCapacityException" ) );
	}

	
	/**
	 * Adds the specified edge to the graph by setting it to it ID's correct
	 * position in the internal data structures. The correct position must be
	 * empty. Runtime O(1).
	 * @param edge the edge to be added to the graph.
	 * @exception IllegalArgumentException if the specified position is not empty.
	 */
	@Override
	public void setEdge( Edge edge ) {
		if( edges.get( edge.id() ) == null ) {
			
			// check out, if left or right edge
			Edge leftEdge = left.get( edge.start() );
			Edge rightEdge = right.get( edge.start() );
			
			if( leftEdge != null && leftEdge.end().equals( edge.end() ) )
				throw new IllegalArgumentException( "Edge between the nodes exists already as left edge!" );
			if( rightEdge != null && rightEdge.end().equals( edge.end() ) )
				throw new IllegalArgumentException( "Edge between the nodes exists already as right edge!" );
			if( incoming.get( edge.end() ) != null &&  incoming.get( edge.end() ) != null )
				throw new IllegalArgumentException( "End node " + edge.end() + " has already incoming edge!" );
			
			if( leftEdge != null && rightEdge != null )
				throw new IllegalArgumentException( "Left and right edge are already occupied." );

			edges.add( edge );
		
			incoming.set( edge.end(), edge );
			
			if( leftEdge == null ) {
				left.set( edge.start(), edge );
			} else
				right.set( edge.start(), edge );

			outdegree.increase( edge.start(), 1 );
		} else if( edges.get( edge.id() ).equals( edge ) ) {
			
		} else
			throw new IllegalArgumentException( "Edge position is already occupied" );
	}


		private void setLeft( Edge edge ) {
		if( edges.get( edge.id() ) == null ) {
			// check out, if left or right edge
			Edge leftEdge = left.get( edge.start() );
			
			if( leftEdge != null && leftEdge.end().equals( edge.end() ) )
				throw new IllegalArgumentException( "Edge between the nodes exists already as left edge!" );
			if( incoming.get( edge.end() ) != null &&  incoming.get( edge.end() ) != null )
				throw new IllegalArgumentException( "End node " + edge.end() + " has already incoming edge!" );
			
			if( leftEdge != null )
				throw new IllegalArgumentException( "Left edge is already occupied." );

			edges.add( edge );
		
			incoming.set( edge.end(), edge );
			
			left.set( edge.start(), edge );

			outdegree.increase( edge.start(), 1 );
		} else if( edges.get( edge.id() ).equals( edge ) ) {
			
		} else
			throw new IllegalArgumentException( "Edge position is already occupied" );
	}
	private void setRight( Edge edge ) {
		if( edges.get( edge.id() ) == null ) {
			Edge rightEdge = right.get( edge.start() );
			
			if( rightEdge != null && rightEdge.end().equals( edge.end() ) )
				throw new IllegalArgumentException( "Edge between the nodes exists already as right edge!" );
			if( incoming.get( edge.end() ) != null &&  incoming.get( edge.end() ) != null )
				throw new IllegalArgumentException( "End node " + edge.end() + " has already incoming edge!" );
			
			if( rightEdge != null )
				throw new IllegalArgumentException( "Right edge is already occupied." );

			edges.add( edge );
		
			incoming.set( edge.end(), edge );
			
			right.set( edge.start(), edge );

			outdegree.increase( edge.start(), 1 );
		} else if( edges.get( edge.id() ).equals( edge ) ) {
			
		} else
			throw new IllegalArgumentException( "Edge position is already occupied" );
	}
	
	/**
	 * Returns an {@link ListSequence} containing all edges starting at
	 * {@code start} and ending at
	 * {@code end}. If no such edge exists, an empty list is returned.
	 * Runtime O(outdegree(start)).
	 * @param start the start node of the edges to be returned.
	 * @param end the end node of the edges to be returned.
	 * @return an {@link ListSequence} containing all edges starting at
	 * {@code start} and ending at {@code end}.
	 */
	@Override
	public IdentifiableCollection<Edge> getEdges( Node start, Node end ) {
		ListSequence<Edge> result = new ListSequence<>();
		for( Edge edge : outgoingEdges( start ) )
			if( edge.end().equals( end ) )
				result.add( edge );
		return result;
	}
	
	/**
	 * Returns the node with the specified id or {@code null} if the graph
	 * does not contain a node with the specified id. Runtime O(1).
	 * @param id the id of the node to be returned.
	 * @return the node with the specified id or {@code null} if the graph
	 * does not contain a node with the specified id.
	 */
	@Override
	public Node getNode( int id ) {
		return nodes.get( id );
	}

	/**
	 * Returns a string representation of this network. The representation is
	 * a list of all nodes and edges contained in this graph. The conversion of
	 * nodes and edges to strings is done by the {@code toString} methods
	 * of their classes. Runtime O(n + m).
	 * @return a string representation of this network
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append( "({" );
		for( Node node : nodes() ) {
			buffer.append( node.toString() );
			buffer.append( ", " );
		}
		if( numberOfNodes() > 0 )
			buffer.delete( buffer.length() - 2, buffer.length() );
		buffer.append( "}, {" );
		int counter = 0;
		for( Edge edge : edges() ) {
			if( counter == 10 ) {
				counter = 0;
				buffer.append( "\n" );
			}
			buffer.append( edge.toString() );
			buffer.append( ", " );
			counter++;
		}
		if( numberOfEdges() > 0 )
			buffer.delete( buffer.length() - 2, buffer.length() );
		buffer.append( "})" );
		return buffer.toString();
	}
	
	
	@Override
	public String deepToString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append( "V = {" );
		for( Node node : nodes() ) {
			buffer.append( node.toString() );
			buffer.append( ", " );
		}
		if( numberOfNodes() > 0 )
			buffer.delete( buffer.length() - 2, buffer.length() );
		buffer.append( "\nE= {" );
		int counter = 0;
		for( Edge edge : edges() ) {
			if( counter == 10 ) {
				counter = 0;
				buffer.append( "\n" );
			}
			buffer.append( edge.nodesToString() );
			buffer.append( ", " );
			counter++;
		}
		if( numberOfEdges() > 0 )
			buffer.delete( buffer.length() - 2, buffer.length() );
		buffer.append( "}\n" );
		return buffer.toString();
	}
	
	@Override
	public AbstractNetwork getAsStaticNetwork() {
		throw new UnsupportedOperationException( "Not supported yet." );
	}
	@Override
	public boolean existsPath( Node start, Node end ) {
		throw new UnsupportedOperationException( "Not supported yet." );
	}

	public Node getParent( Node node ) {
		return incoming.get( node ) == null ? null : incoming.get( node ).start();
	}

	public Node getLeft( Node node ) {
		return left.get( node ) == null ? null : left.get( node ).end();
	}

	public Node getRight( Node node ) {
		return right.get( node ) == null ? null : right.get( node ).end();
	}

	public Node getRoot() {
		return root;
	}

	@Override
	public Iterator<Node> iterator() {
		return nodes.iterator();
	}
	
	public int numOfChildren( Node node ) {
		return outdegree( node );
	}
	
	/**
	 * Recursively traverses the tree, appends output of level
	 * to given StringBuffer
	 * @param strbuf StringBuffer to append to
	 * @param level  current level in tree (for indentation)
	 * @param node   current tree node
	 */
	private void _tree2string( StringBuffer strbuf, int level, Node node ) {
		if( getRight( node ) != null )
			_tree2string( strbuf, level + 1, getRight( node ) );

		for( int i = 0; i < level; ++i )
			strbuf.append( "    " );
		if( !getRoot().equals( node ) )
			strbuf.append( isLeftChild( node ) ? "\\" : "/" );
		strbuf.append( node + "\n" );

		if( getLeft( node ) != null )
			_tree2string( strbuf, level + 1, getLeft( node ) );
	}
	
	public String toString2() {
		StringBuffer strbuf = new StringBuffer( this.getClass() + ": " );

		if( root == null )
			strbuf.append( "EMPTY\n" );
		else {
			strbuf.append( "\n" );
			_tree2string( strbuf, 0, getRoot() );
		}

		return strbuf.toString();
	}
	
	boolean isLeftChild( Node node ) {
		if( node.equals( root ) )
			return false;
		return getLeft( getParent( node ) ).equals( node );
	}

	public boolean isEmpty() {
		return root == null;
	}

	public int getHeight() {
		if( isEmpty() )
			return -1;
		else
			return getHeight( root );
	}
	
	private int getHeight( Node node ) {
		if( node == null )
			return -1;
		return 1 + Math.max( getHeight( getLeft( node ) ), getHeight( getRight( node ) ) );		
	}

	void setRoot( int i ) {
		root = getNode( i );
	}

}
