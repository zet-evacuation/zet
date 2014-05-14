
package de.tu_berlin.coga.graph.traversal;

import de.tu_berlin.coga.graph.DirectedGraph;
import de.tu_berlin.coga.graph.GraphTest;
import static org.junit.Assert.assertEquals;
import org.junit.Test;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DepthFirstSearchTest {

 	/**
	 * Checks if the classification in graph edges works correctly.
	 */
	@Test
	public void testClassification() {
    DepthFirstSearch dfs = new DepthFirstSearch();
    dfs.setProblem( GraphTest.generateDirected( 8, GraphTest.DFS_GRAPH ) );

    dfs.run();

    System.out.println( "Tree-Edges: " + dfs.treeEdges );
    assertEquals( 7, dfs.treeEdges.size() );
    System.out.println( "Back-Edges: " + dfs.backEdges );
    assertEquals( 2, dfs.backEdges.size() );
    System.out.println( "Forward-Edges: " + dfs.forwardEdges );
    assertEquals( 1, dfs.forwardEdges.size() );
    System.out.println( "Cross-Edges: " + dfs.crossEdges );
    assertEquals( 1, dfs.crossEdges.size() );
  }

  @Test
  public void testLoop() {
    DirectedGraph g = GraphTest.generateDirected( 1, new int[][]{{0,0}} );
    DepthFirstSearch dfs = new DepthFirstSearch();
    dfs.setProblem( g );
    dfs.run();
    assertEquals( 0, dfs.treeEdges.size() );
    assertEquals( 1, dfs.backEdges.size() );
    assertEquals( 0, dfs.forwardEdges.size() );
    assertEquals( 0, dfs.crossEdges.size() );
  }

  @Test
  public void undirectedTest() {
    DepthFirstSearch dfs = new DepthFirstSearch();
    dfs.setProblem( GraphTest.generateUndirected( 8, GraphTest.DFS_GRAPH ) );
    System.out.println( "Starting undirected run" );

    dfs.run();

    System.out.println( "Tree-Edges: " + dfs.treeEdges );
    assertEquals( 7, dfs.treeEdges.size() );
    System.out.println( "Back-Edges: " + dfs.backEdges );
    assertEquals( 4, dfs.backEdges.size() );
    System.out.println( "Forward-Edges: " + dfs.forwardEdges );
    assertEquals( 0, dfs.forwardEdges.size() );
    System.out.println( "Cross-Edges: " + dfs.crossEdges );
    assertEquals( 0, dfs.crossEdges.size() );
  }

  @Test
  public void reversedTest() {
    DepthFirstSearch dfs = new DepthFirstSearch();
    DirectedGraph g = GraphTest.generateDirected( 8, GraphTest.DFS_GRAPH2 );
    dfs.setProblem( g );
    dfs.setReverse( true );
    //dfs.setStart( g.getNode( 2 ) );

    System.out.println( "Starting reversed run" );
    dfs.run();
    System.out.println( "Tree-Edges: " + dfs.treeEdges );
    assertEquals( 5, dfs.treeEdges.size() );
    System.out.println( "Back-Edges: " + dfs.backEdges );
    assertEquals( 6, dfs.backEdges.size() );
    System.out.println( "Forward-Edges: " + dfs.forwardEdges );
    assertEquals( 0, dfs.forwardEdges.size() );
    System.out.println( "Cross-Edges: " + dfs.crossEdges );
    assertEquals( 0, dfs.crossEdges.size() );
  }

}
