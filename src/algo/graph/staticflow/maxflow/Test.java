/**
 * Test.java
 * input:
 * output:
 *
 * method:
 *
 * Created: Oct 6, 2010,4:41:48 PM
 */
package algo.graph.staticflow.maxflow;

import de.tu_berlin.math.coga.common.util.Formatter;
import ds.graph.Edge;
import ds.graph.Network;
import ds.graph.Node;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Test {

	public static void main( String[] arguments ) {
		final int size = 500000;

		Node a = new Node( 0 );
		Node b = new Node( 1 );
		Network n = new Network(2, size);
		n.setNode( a );
		n.setNode( b );

		long start = System.nanoTime();
		Edge[] eA = new Edge[size];
		for( int i = 0; i < size; ++i ) {
			eA[i] = new Edge( i, a, b );
			n.setEdge( eA[i] );
			//eA[i].setP( i, a, b );
		}
		long end = System.nanoTime();
		System.out.println( Formatter.formatTimeNanoseconds( end-start ) );


		start = System.nanoTime();

		for( int i = 0; i < size; ++i ) {
		}

		end = System.nanoTime();
		System.out.println( Formatter.formatTimeNanoseconds( end-start ) );


	}
}
