/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ds.graph;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public interface DirectedGraph extends Graph {
	/**
	 * Returns the indegree of the specified node, i.e. the number of edges
	 * ending at it. The indegree is not defined for undirected graphs.
	 * @param node the node for which the indegree is to be returned.
	 * @return the indegree of the specified node.
	 * @throws UnsupportedOperationException if the graph is not directed.
	 */
	int inDegree( Node node );

	/**
	 * Returns the outdegree of the specified node, i.e. the number of edges
	 * starting at it. The outdegree is not defined for undirected graphs.
	 * @param node the node for which the outdegree is to be returned.
	 * @return the outdegree of the specified node.
	 * @throws UnsupportedOperationException if the graph is not directed.
	 */
	int outDegree( Node node );
}
