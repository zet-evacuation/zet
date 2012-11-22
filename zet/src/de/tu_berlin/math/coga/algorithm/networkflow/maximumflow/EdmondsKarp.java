/**
 * EdmondsKarp.java
 * Created: Oct 8, 2010, 11:39:11 AM
 */
package de.tu_berlin.math.coga.algorithm.networkflow.maximumflow;

import algo.graph.traverse.BFS;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.StaticPath;


/**
 * An implementation of the algorithm of Edmonds and Karp. Successively flow is
 * augmented along shortest s-t-paths.
 *
 * Warning: there is a bug in here, on some instances, the result is not correct.
 * @author Jan-Philipp Kappmeier
 */
public class EdmondsKarp extends FordFulkerson {

	public EdmondsKarp() {
		super();
	}

	/**
	 * Finds a shortest path such that the Ford and Fulkerson algorithm runs in
	 * polynomial time.
	 * @return a shortest path in the residual network
	 */
	@Override
	protected StaticPath findPath() {
		BFS bfs = new BFS( residualNetwork );
		bfs.run( source, sink );

		StaticPath path = new StaticPath();

		Node current = sink;
		do {
			final Edge e = bfs.predecedingEdge( current );
			if( e == null )
				return path;
			path.addFirstEdge( e );
			current = e.start();
		} while( !current.equals( source ) );
		return path;
	}
}