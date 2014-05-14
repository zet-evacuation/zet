package de.tu_berlin.coga.graph.traversal;

import de.tu_berlin.coga.graph.DirectedGraph;
import ds.graph.network.Network;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Some static test instances for testing breadth first search and depth first search.
 * @author Jan-Philipp Kappmeier
 */
public final class GraphTraversalTestInstance {
  /** A simple graph with 4 nodes. */
	final static GraphTraversalTestInstance SIMPLE_1;
  /** A simple graph with 4 nodes. */
	final static GraphTraversalTestInstance SIMPLE_2;
  /** An instance whose iteration starts at the last node. */
	final static GraphTraversalTestInstance LAST_NODE;
  /** One single node. */
	final static GraphTraversalTestInstance SMALL;
  /** Two triangles building a circle. */
	final static GraphTraversalTestInstance DISJOINT_TRIANGLE;
  /** A star. */
	final static GraphTraversalTestInstance STAR;
  /** A wheel; a star whose outer nodes form a circle. */
	final static GraphTraversalTestInstance WHEEL;
  /** A complete directed graph. */
	final static GraphTraversalTestInstance COMPLETE;
  /** A complete directed graph without cycles. */
	final static GraphTraversalTestInstance COMPLETE_NO_CYCLES;
  /** A line. */
	final static GraphTraversalTestInstance LINE;
  /** A binary tree. */
	final static GraphTraversalTestInstance BINARY_TREE;

	/** The graph for a test instance. */
	private final DirectedGraph g;
	/** The start node for a test instance. */
	private final int startNode;
	/** The expected distances. */
	private final int[] expected;
	/** A name for the instance.*/
	private final String name;

	/**
	 * Initializes a instance.
	 * @param g the graph
	 * @param expected the expected distances
	 * @param name the name
	 * @param startNode the start node
	 */
	private GraphTraversalTestInstance( DirectedGraph g, int[] expected, String name, int startNode ) {
		this.g = g;
		this.startNode = startNode;
		this.expected = expected;
		this.name = name;
	}

	/**
	 * Returns an array containing all supported test instances.
	 * @return an array containing all supported test instances
	 */
	static GraphTraversalTestInstance[] getAllInstances() {
		return new GraphTraversalTestInstance[]{SIMPLE_1, SIMPLE_2, LAST_NODE, SMALL, DISJOINT_TRIANGLE,
      STAR, WHEEL, COMPLETE, LINE, BINARY_TREE};
	}

	/**
	 * Returns the graph.
	 * @return the graph
	 */
	public DirectedGraph getGraph() {
		return g;
	}

	/**
	 * Returns the index of the start node.
	 * @return the index of the start node
	 */
	public int startNode() {
		return startNode;
	}

	/**
	 * Returns the size of the instance, which equals the size of the graph.
	 * @return the size of the instance
	 */
	public int size() {
		return g.nodeCount();
	}

	/**
	 * Returns the distance from the start node to node with given index.
	 * @param i the index of the node
	 * @return the distnace from the start node to {@code i}
	 */
	public int getDistance( int i ) {
		return expected[i];
	}

	/**
	 * Returns the name of the instance.
	 * @return the name of the instance
	 */
	public String getName() {
		return name;
	}

	/**
	 * Initializer for the static instances.
	 */
	static {
		Network g = new Network( 4, 5 );
		g.createAndSetEdge( g.getNode( 0 ), g.getNode( 1 ) );
		g.createAndSetEdge( g.getNode( 0 ), g.getNode( 2 ) );
		g.createAndSetEdge( g.getNode( 1 ), g.getNode( 2 ) );
		g.createAndSetEdge( g.getNode( 1 ), g.getNode( 3 ) );
		g.createAndSetEdge( g.getNode( 2 ), g.getNode( 3 ) );
		SIMPLE_1 = new GraphTraversalTestInstance( g, new int[]{0, 1, 1, 2}, "Simple 1", 0 );
		LAST_NODE = new GraphTraversalTestInstance( g, new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 0}, "Last node", 3 );

		g = new Network( 4, 6 );
		g.createAndSetEdge( g.getNode( 0 ), g.getNode( 1 ) );
		g.createAndSetEdge( g.getNode( 0 ), g.getNode( 3 ) );
		g.createAndSetEdge( g.getNode( 0 ), g.getNode( 2 ) );
		g.createAndSetEdge( g.getNode( 1 ), g.getNode( 2 ) );
		g.createAndSetEdge( g.getNode( 1 ), g.getNode( 3 ) );
		g.createAndSetEdge( g.getNode( 2 ), g.getNode( 3 ) );
		SIMPLE_2 = new GraphTraversalTestInstance( g, new int[]{0, 1, 1, 1}, "Simple 2", 0 );

		g = new Network( 1, 0 );
		SMALL = new GraphTraversalTestInstance( g, new int[]{0}, "Small", 0 );

		g = new Network( 6, 6 );
		g.createAndSetEdge( g.getNode( 0 ), g.getNode( 1 ) );
		g.createAndSetEdge( g.getNode( 1 ), g.getNode( 2 ) );
		g.createAndSetEdge( g.getNode( 2 ), g.getNode( 0 ) );
		g.createAndSetEdge( g.getNode( 3 ), g.getNode( 4 ) );
		g.createAndSetEdge( g.getNode( 4 ), g.getNode( 5 ) );
		g.createAndSetEdge( g.getNode( 5 ), g.getNode( 3 ) );
		DISJOINT_TRIANGLE = new GraphTraversalTestInstance( g, new int[]{2, 0, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE}, "Disjoint triangles", 1 );

		g = new Network( 101, 100 );
		for( int i = 1; i <= 100; ++i )
			g.createAndSetEdge( g.getNode( 0 ), g.getNode( i ) );
		int[] expected = new int[101];
		for( int i = 1; i <= 100; ++i )
			expected[i] = 1;
		expected[0] = 0;
		STAR = new GraphTraversalTestInstance( g, expected, "Star", 0 );

		g = new Network( 101, 200 );
		for( int i = 1; i <= 100; ++i ) {
			g.createAndSetEdge( g.getNode( 0 ), g.getNode( i ) );
			g.createAndSetEdge( g.getNode( i ), g.getNode( ((i + 1) % 100) + 1 ) );
		}
		expected = new int[101];
		for( int i = 1; i <= 100; ++i )
			expected[i] = 1;
		expected[0] = 0;
		WHEEL = new GraphTraversalTestInstance( g, expected, "Star", 0 );

		expected = new int[32];
		Random r = new Random();
		int rnd = r.nextInt( expected.length );
		g = new Network( expected.length, (expected.length*(expected.length-1)) );
		for( int i = 0; i < expected.length - 1; ++i )
			for( int j = i + 1; j < expected.length; ++j ) {
				g.createAndSetEdge( g.getNode( i ), g.getNode( j ) );
				g.createAndSetEdge( g.getNode( j ), g.getNode( i ) );
			}
		for( int i = 0; i < expected.length; ++i )
			expected[i] = 1;
		expected[rnd] = 0;
		COMPLETE = new GraphTraversalTestInstance( g, expected, "Complete", rnd );

		expected = new int[16];
		rnd = r.nextInt( expected.length );
		g = new Network( expected.length, (expected.length*(expected.length-1))/2 );
		for( int i = 0; i < expected.length - 1; ++i )
			for( int j = i + 1; j < expected.length; ++j )
				g.createAndSetEdge( g.getNode( i ), g.getNode( j ) );
		for( int i = 0; i < rnd; ++i )
			expected[i] = Integer.MAX_VALUE;
		expected[rnd] = 0;
		for( int i = rnd + 1; i < expected.length; ++i )
			expected[i] = 1;
		COMPLETE_NO_CYCLES = new GraphTraversalTestInstance( g, expected, "Complete, no cycles", rnd );

		expected = new int[42];
		g = new Network( expected.length, expected.length-1 );
		for( int i = 0; i < expected.length - 1; )
			g.createAndSetEdge( g.getNode( i ), g.getNode( ++i ) );
		for( int i = 0; i < expected.length; ++i )
			expected[i] = i;
		LINE = new GraphTraversalTestInstance( g, expected, "Line", 0 );

		expected = new int[127];
		int i = 1;
		Queue<Tuple> q = new LinkedList<>();
		q.offer( new Tuple( 0, 0 ) );
		g = new Network( expected.length, expected.length*2 );
		while( i < expected.length ) {
			Tuple current = q.poll();
			g.createAndSetEdge( g.getNode( current.x ), g.getNode( current.x * 2 + 1 ) );
			expected[i++] = current.y + 1;
			q.offer( new Tuple( current.x * 2 + 1, current.y + 1 ) );
			g.createAndSetEdge( g.getNode( current.x ), g.getNode( current.x * 2 + 2 ) );
			expected[i++] = current.y + 1;
			q.offer( new Tuple( current.x * 2 + 2, current.y + 1 ) );
		}
		BINARY_TREE = new GraphTraversalTestInstance( g, expected, "Binary tree", 0 );
	}

	/**
	 * Simple inner class storing two values.
	 */
	private static class Tuple {
		private int x;
		private int y;

		public Tuple( int x, int y ) {
			this.x = x;
			this.y = y;
		}
	}
}
