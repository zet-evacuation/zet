/**
 * PushRelabelHighestLabelGlobalRelabelling.java
 * Created: Oct 21, 2010, 6:03:29 PM
 */
package algo.graph.staticflow.maxflow;

import de.tu_berlin.math.coga.datastructure.Tuple;
import ds.graph.Edge;
import ds.graph.Node;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PushRelabelHighestLabelGlobalRelabelling extends PushRelabelHighestLabelNeu {

	protected boolean useBugHeuristic = true;
	protected int globalRelabels;
	protected int nm;
	protected int globalRelabelThreshold;
	protected int relabelsSinceLastGlobalRelabel;
	boolean performGlobalRelabel = true;

	private static enum States {
		inactiveFirst,
		inactiveNext,
		activeFirst,
		activeNext;
	}

	@Override
	protected void init() {
		super.init();
		nm = 6 * n + m;
		globalRelabelThreshold = (int) 0.5 * nm;
	}

	@Override
	protected void computeMaxFlow() {
		while( activeBuckets.getMaxIndex() >= activeBuckets.getMinIndex() ) {
			final Node v = activeBuckets.max();
			if( v == null ) {
				if( activeBuckets.getMaxIndex() < activeBuckets.getMinIndex() && useBugHeuristic )
					globalUpdate();
			} else {
				activeBuckets.removeActive( activeBuckets.getMaxIndex(), v );
				discharge( v );

				if( activeBuckets.getMaxIndex() < activeBuckets.getMinIndex() )
					break;

				/* is it time for global update? */
				if( relabelsSinceLastGlobalRelabel > nm )
					globalUpdate();
			}
		}
		// we have a max flowValue
		flowValue = excess.get( sink );
	}

	@Override
	protected int relabel( Node v ) {
		relabelsSinceLastGlobalRelabel += 12;
		return super.relabel( v );
	}

	@Override
	protected Tuple<Integer,Edge> searchForMinDistance( Node v ) {
		int minDistance = n;
		Edge minEdge = null;
		// search for the minimum distance value
		for( int i = useBugHeuristic ? current.get( v ) :  residualGraph.getFirst( v ); i < residualGraph.getLast( v ); ++i ) {
			relabelsSinceLastGlobalRelabel++;
			final Edge e = residualGraph.getEdge( i );
			if( residualGraph.getResidualCapacity( e ) > 0 && distanceLabels.get( e.end() ) < minDistance ) {
				minDistance = distanceLabels.get( e.end() );
				minEdge = e;
			}
		}
		return new Tuple( minDistance+1, minEdge );
	}

	protected void globalUpdate() {
		relabelsSinceLastGlobalRelabel = 0;
		globalRelabels++;

		inactiveBuckets.reset( activeBuckets.getdMax() );
		activeBuckets.reset();

		// all node distances to n
		for( Node node : getProblem().getNetwork().nodes() )
			distanceLabels.set( node, n );
		distanceLabels.set( sink, 0 );

		inactiveBuckets.addInactive( 0, sink );
		for( int curDist = 0; true; curDist++ ) {
			final int curDistPlusOne = curDist+1;
			if( activeBuckets.get( curDist ) == null && inactiveBuckets.get( curDist ) == null )
				break;

			Node node = null;
			States nextNodeState = States.inactiveFirst; // which type is the next node.
			while( true ) {
				switch( nextNodeState ) {
					case inactiveFirst:
						node = inactiveBuckets.get( curDist );
						nextNodeState = States.inactiveNext;
						break;
					case inactiveNext:
						node = inactiveBuckets.next( node );
						break;
					case activeFirst:
						node = activeBuckets.get( curDist );
						nextNodeState = States.activeNext;
						break;
					case activeNext:
						node = activeBuckets.next( node );
						break;
					default:
						throw new AssertionError( nextNodeState );
				}

				if( node == null )
					if( nextNodeState == States.inactiveNext ) {
						nextNodeState = States.activeFirst.activeFirst;
						continue;
					} else {
						assert nextNodeState == States.activeNext : nextNodeState;
						break;
					}

				// scanning arcs incoming to a node (these are reverse arcs from outgoing arcs)
				for( int i = residualGraph.getFirst( node ); i < residualGraph.getLast( node ); ++i ) {
					final Edge a = residualGraph.getEdge( i );
					if( residualGraph.getResidualCapacity( residualGraph.getReverseEdge( a ) ) > 0 ) {
						final Node j = a.end();
						if( distanceLabels.get( j ) == n ) {
							distanceLabels.set( j, curDistPlusOne );
							current.set( j, residualGraph.getFirst( j ) );
							if( curDistPlusOne > activeBuckets.getdMax() )
								activeBuckets.setdMax( curDistPlusOne );
							if( excess.get( j ) > 0 ) // put into active list
								activeBuckets.addActive( curDistPlusOne, j );
							else // put into inactive list
								inactiveBuckets.addInactive( curDistPlusOne, j);
						}
					}
				}
			}
		}
	}

	public int getGlobalRelabels() {
		return globalRelabels;
	}
}
