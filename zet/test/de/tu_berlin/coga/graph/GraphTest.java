
package de.tu_berlin.coga.graph;


/**
 * Unit test for graphs.
 * @author Jan-Philipp Kappmeier
 */
public class GraphTest {
  /** A sample graph containing all classes of edges and a loop. */
  public final static int[][] DFS_GRAPH = {
    {0,1},
    {0,4},
    {0,7},
    {1,2},
    {2,3},
    {3,1},
    {4,5},
    {4,4},
    {5,2},
    {5,6},
    {5,7}
  };
  public final static int[][] DFS_GRAPH2 = {
    {0,3},
    {1,0},
    {2,1},
    {2,4},
    {2,7},
    {3,1},
    {4,5},
    {4,4},
    {5,0},
    {5,6},
    {5,7}
  };

  /**
   * Generates a directed graph out of edges in an array.
   * @param nodes the number of n odes
   * @param edges the edges
   * @return a directed graph
   */
  public static DirectedGraph generateDirected( int nodes, int[][] edges ) {
    DefaultDirectedGraph g = new DefaultDirectedGraph( nodes, edges.length );

    for( int i = 0; i < edges.length; ++i ) {
      g.createAndSetEdge( g.getNode( edges[i][0] ), g.getNode( edges[i][1] ) );
    }

    return g;
  }

  public static UndirectedGraph generateUndirected( int nodes, int[][] edges ) {
    SimpleUndirectedGraph g = new SimpleUndirectedGraph( nodes );

    for( int i = 0; i < edges.length; ++i ) {
      g.addEdge( edges[i][0], edges[i][1] );
    }

    return g;
  }
}
