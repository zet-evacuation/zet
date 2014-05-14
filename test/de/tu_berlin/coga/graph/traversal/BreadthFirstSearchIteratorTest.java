
package de.tu_berlin.coga.graph.traversal;

import de.tu_berlin.coga.common.util.Helper;
import de.tu_berlin.coga.graph.EdgeNodePair;
import java.util.Arrays;
import org.junit.Test;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BreadthFirstSearchIteratorTest {

  /**
   * Tests bfs iteration on some instances.
  */
  @Test
  public void bfsTest() {
    for( GraphTraversalTestInstance test : GraphTraversalTestInstance.getAllInstances() ) {
      System.out.println( "Testing " + test.getName() );
      // Test for the general iterator
      GeneralBreadthFirstSearchIterator bfs = new GeneralBreadthFirstSearchIterator(
              test.getGraph(), test.startNode(), false );
      int[] resultDist = new int[test.size()];
      Arrays.fill( resultDist, Integer.MAX_VALUE );

      for( EdgeNodePair n : Helper.in( bfs ) ) {
        resultDist[n.getNode().id()] = n.getPred() != null ? resultDist[n.getPred().start().id()] + 1 : 0;
      }

      assert checkResult( resultDist, test );
    }
  }

	public static boolean checkResult( int[] resultDist, GraphTraversalTestInstance instance ) {
	  if( resultDist == null ) {
      throw new IllegalArgumentException( "Result set must not be null!" );
    }
    if( resultDist.length == 0 ) {
      throw new IllegalArgumentException( "Result set must at least contain one node." );
    }
    if( resultDist.length != instance.size() ) {
      System.out.println( "Sizes of given results and expected for instance are not the same! Given size: "
              + resultDist.length + " Expected size: " + instance.size() );
      return false;
    }
		boolean correct = true;
    for( int i = 0; i < resultDist.length - 1; ++i ) {
      if( resultDist[i] != instance.getDistance( i ) ) {
        correct = false;
      }
    }
    return correct;
	}
}
