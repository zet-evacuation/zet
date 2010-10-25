/**
 * PushRelabelHighestLabelGlobalGapRelabelling.java
 * Created: Oct 21, 2010, 6:03:42 PM
 */
package algo.graph.staticflow.maxflow;

import de.tu_berlin.math.coga.datastructure.BucketSet;
import de.tu_berlin.math.coga.datastructure.priorityQueue.BucketPriorityQueue;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.flow.MaximumFlow;
import ds.graph.problem.MaximumFlowProblem;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PushRelabelHighestLabelGlobalGapRelabelling extends PushRelabelHighestLabelGlobalRelabelling {
	int gaps;
	int gapNodes;

	@Override
	protected void discharge( Node v ) {
		assert excess.get( v ) > 0;
		assert v.id() != sink.id();
		do {
			final int nodeDistance = distanceLabels.get( v );	// current node distance. -1 is applicable distance

			int i;	// for all outarcs
			for( i = current.get( v ); i < last.get( v ); ++i ) {
				final ResidualEdge e = residualEdges[i];
				// if is applicable, push. break if no excess is leftover
				if( e.residualCapacity > 0 && distanceLabels.get( e.end() ) == nodeDistance - 1 && push( e ) == 0 )
					break;
			}

			// all outgoing arcs are scanned now.
			if( i == last.get( v ) ) {
				// relabel, ended due to pointer at the last arc
				relabel( v );

				if( distanceLabels.get( v ) == n )
					break;

				if( activeBuckets.get( nodeDistance ) == null && inactiveBuckets.get( nodeDistance ) == null )
					gap( nodeDistance );

				if( distanceLabels.get( v ) == n )
					throw new IllegalStateException( "here a break should be somehow" );
			} else {
				// node is no longer active
				current.set( v, i );
				// put the vertex on the inactive list
				inactiveBuckets.addInactive( nodeDistance, v );
				break;
			}
		} while( true );
	}

	/**
	 * Gap relabeling (maybe move to bucket?)
	 * @param l
	 */
	protected int gap( int emptyB ) {
		gaps++;
		int r = emptyB - 1;
		int cc;

		/* set labels of nodes beyond the gap to "infinity" */
		for( int l = emptyB + 1; l <= activeBuckets.getdMax(); l++ ) {
			// TODO iterator
			for( Node node = inactiveBuckets.get( l ); node != null; node = inactiveBuckets.next( node ) ) {
				distanceLabels.set( node, n );
				gapNodes++;
			}
			// TODO change somehow...
			inactiveBuckets.set( l, null );
		}
		cc = (activeBuckets.getMinIndex() > r) ? 1 : 0;
		activeBuckets.setMaxIndex( r );
		return cc;
	}

	public int getGapNodes() {
		return gapNodes;
	}

	public int getGaps() {
		return gaps;
	}

	public static void main( String[] arguments ) {
		Network network = new Network( 4, 5 );
		network.createAndSetEdge( network.getNode( 0 ), network.getNode( 1 ) );
		network.createAndSetEdge( network.getNode( 0 ), network.getNode( 2 ) );
		network.createAndSetEdge( network.getNode( 1 ), network.getNode( 2 ) );
		network.createAndSetEdge( network.getNode( 1 ), network.getNode( 3 ) );
		network.createAndSetEdge( network.getNode( 2 ), network.getNode( 3 ) );

		IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<Edge>( 5 );
		capacities.add( network.getEdge( 0 ), 2 );
		capacities.add( network.getEdge( 1 ), 1 );
		capacities.add( network.getEdge( 2 ), 1 );
		capacities.add( network.getEdge( 3 ), 1 );
		capacities.add( network.getEdge( 4 ), 2 );

		MaximumFlowProblem mfp = new MaximumFlowProblem( network, capacities, network.getNode( 0 ), network.getNode( 3 ) );

		HIPR hipr = new HIPR();
		hipr.setProblem( mfp );
		hipr.run();

		System.out.println( "Flow: " + hipr.getFlow() );
		//System.out.println( "Flow: " + hipr.getSolution().getFlowValue() );
	}
}
