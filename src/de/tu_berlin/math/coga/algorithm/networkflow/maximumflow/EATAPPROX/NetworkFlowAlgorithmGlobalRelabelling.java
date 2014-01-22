/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.EATAPPROX;

import de.tu_berlin.math.coga.datastructure.Tuple;
import ds.graph.Edge;
import ds.graph.Node;
import java.util.Set;

/**
 *
 * @author kapman
 */
public class NetworkFlowAlgorithmGlobalRelabelling extends NetworkFlowAlgorithm {


	protected boolean useBugHeuristic = false;
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

	// nm muss neu gesetzt werden!

	@Override
	protected void init() {
		super.init();
		m = residualGraph.getCurrentVisibleEdgeCount();
		System.out.println( "Visible Edges: " + m );
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
		for( int i = useBugHeuristic ? current.get( v ) : residualGraph.getFirst( v ); i < residualGraph.getLast( v ); ++i ) {
			relabelsSinceLastGlobalRelabel++;
			final Edge e = residualGraph.getEdge( i );

//			if( residualGraph.getResidualCapacity( e ) > 0 ) {
//				if( minEdge == null && distanceLabels.get( e.end() ) <= minDistance ) {
//					minEdge = e;
//					minDistance = distanceLabels.get( e.end() );
//				} else if ( distanceLabels.get( e.end() ) < minDistance ) {
//					minDistance = distanceLabels.get( e.end() );
//					minEdge = e;
//				}
//			}

			if( residualGraph.getResidualCapacity( e ) > 0 && distanceLabels.get( e.end() ) < minDistance ) {
				minDistance = distanceLabels.get( e.end() );
				minEdge = e;
			}

		}
		return new Tuple<>( minDistance+1, minEdge );
	}

	protected void globalUpdate() {
		System.out.println( "\n\nGlobal Update" );
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
						nextNodeState = States.activeFirst;
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
						System.out.println( "Node update for " + j );
						if( distanceLabels.get( j ) == n ) {
							if( !canReachSink.get( j ) ) {
								// let this node to be at max level because it is useless
								continue;
							}

							distanceLabels.set( j, curDistPlusOne );
							System.out.println( "Update Distance for " + j + " to " + curDistPlusOne );
							current.set( j, residualGraph.getFirst( j ) );
							if( curDistPlusOne > activeBuckets.getdMax() )
								activeBuckets.setdMax( curDistPlusOne );
							if( excess.get( j ) > 0 ) { // put into active list {
								System.out.println( "Adding " + j + " to active" );
								activeBuckets.addActive( curDistPlusOne, j );
							} else { // put into inactive list
								System.out.println( "Adding " + j + " to in_active" );
								inactiveBuckets.addInactive( curDistPlusOne, j);
							}
						}
					}
				}
			}
		}
		int i = 3;
		i++;
	}

	public int getGlobalRelabels() {
		return globalRelabels;
	}

	@Override
	void updateDistances( Set<Edge> newEdges ) {
		super.updateDistances( newEdges ); //To change body of generated methods, choose Tools | Templates.
		nm = 6 * n + m;
		globalRelabelThreshold = (int) 0.5 * nm;
		// set distances correct!
		globalUpdate();
	}


}
