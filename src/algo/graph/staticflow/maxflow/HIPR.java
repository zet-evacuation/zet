/**
 * HIPR.java
 * Created: Oct 8, 2010, 4:01:10 PM
 */
package algo.graph.staticflow.maxflow;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
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
public class HIPR extends Algorithm<MaximumFlowProblem, MaximumFlow> {

	boolean useBugHeuristic = true;

	private class ResidualEdge extends Edge {
		ResidualEdge reverse;
		int residualCapacity;
		boolean reverseEdge;
		Edge original;
		ResidualEdge( int id, Node start, Node end, int cap, boolean reverse ) {
			super( id, start, end );
			this.residualCapacity = cap;
			reverseEdge = reverse;
		}

	}

	protected IdentifiableIntegerMapping excess;
	protected IdentifiableIntegerMapping distanceLabels;

	boolean performGlobalRelabel = true;

	long flowValue;

	int pushes;
	int relabels;
	int globalRelabels;
	int gaps;
	int gapNodes;

	int n;
	int m;
	int nm;

	int globalRelabelThreshold;
	int relabelsSinceLastGlobalRelabel;

	Node source;
	Node sink;
	ResidualEdge[] residualEdges;
	IdentifiableIntegerMapping<Node> current;
	IdentifiableIntegerMapping<Node> first;
	IdentifiableIntegerMapping<Node> last;

	BucketPriorityQueue<Node> activeBuckets;
	BucketSet<Node> inactiveBuckets;


	@Override
	protected MaximumFlow runAlgorithm( MaximumFlowProblem problem ) {
		source = getProblem().getSource();
		sink = getProblem().getSink();
		n = getProblem().getNetwork().numberOfNodes();
		m = getProblem().getNetwork().numberOfEdges();

		alloc();
		init();
		stageOne();
		stageTwo();

		IdentifiableIntegerMapping<Edge> flow = new IdentifiableIntegerMapping<Edge>( m );
		for( ResidualEdge e : residualEdges ) {
			if( e.original == null )
				continue;
			flow.set( e.original, e.reverse.residualCapacity );
		}
		return new MaximumFlow( getProblem(), flow );
	}

	/**
	 * Allocates the memory for the data structures. These contain information
	 * for the nodes and edges
	 */
	private void alloc() {
		distanceLabels = new IdentifiableIntegerMapping<Node>( n );	// distance
		excess = new IdentifiableIntegerMapping<Node>( n );	// excess
		current = new IdentifiableIntegerMapping<Node>( n );	// current edge datastructure (index)
		first = new IdentifiableIntegerMapping<Node>( n ); // first outgoing edge (index)
		last = new IdentifiableIntegerMapping<Node>( n ); // last outgoing edge (index)
		activeBuckets = new BucketPriorityQueue<Node>( n+1, Node.class );
		activeBuckets.setDistanceLabels( distanceLabels );
		inactiveBuckets = new BucketSet<Node>( n+1, Node.class );
		residualEdges = new ResidualEdge[2*m];
	}

	/**
	 * Sets up the data structures. At first, creates reverse edges and orders the
	 * edges according to their tail nodes into the array. Thus, the incident
	 * edges to a vertex can be searched by a run through the array.
	 */
	private void init() {
		nm = 6*n+m;
		globalRelabelThreshold = (int)0.5*nm;

		// set up residual edges
		int edgeCounter = 0;
		int[] temp = new int[2*m];
		for( Node v : getProblem().getNetwork() ) {
			first.set( v, edgeCounter );
			current.set( v, edgeCounter );
			// add the outgoing edges to the arc list
			for( Edge e : getProblem().getNetwork().outgoingEdges( v ) ) {
				residualEdges[edgeCounter] = new ResidualEdge( edgeCounter, e.start(), e.end(), getProblem().getCapacities().get( e ), false );
				residualEdges[edgeCounter].original = e;
				temp[e.id()] = edgeCounter++;
			}
			// add the reverse edge for incoming edges to the arc list (they are also outgoing!
			for( Edge e : getProblem().getNetwork().incomingEdges( v ) ) {
				residualEdges[edgeCounter] = new ResidualEdge( edgeCounter, e.end(), e.start(), 0, true );
				temp[e.id()+m] = edgeCounter++;
			}
			last.set( v, edgeCounter );
		}
		for( int i = 0; i < m; ++i ) {
			residualEdges[temp[i]].reverse = residualEdges[temp[i+m]];
			residualEdges[temp[i+m]].reverse = residualEdges[temp[i]];
		}
	  // initialize excesses
		excess.set( source, 0 );
		for( int i = first.get( source ); i < last.get( source ); ++i ) {
			ResidualEdge e = residualEdges[i];
			if( e.end().id() != source.id() ) {
				pushes++;
				final int delta = e.residualCapacity;
				e.residualCapacity -= delta;
				e.reverse.residualCapacity += delta;
				excess.increase( e.end(), delta );
			}
		}

		for( Node v : getProblem().getNetwork() ) {
			final int id = v.id();
			if( id == sink.id() ) {
				distanceLabels.set( v, 0 );
				inactiveBuckets.addInactive( 0, v );
				continue;
			}
			distanceLabels.set( v, id == source.id() ? n : 1 );
			if( excess.get( v ) > 0 )
				activeBuckets.addActive( 1, v );
			else if( distanceLabels.get( v ) < n )
				inactiveBuckets.addInactive( 1, v );
		}
		activeBuckets.setdMax( 1 );
	}

	protected void stageOne() {
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

	protected void stageTwo() {
		Node[] buckets = new Node[n];
		Node[] next = new Node[n];

		/* deal with self-loops */
//		forAllNodes( i ) {
//			forAllArcs( i, a )
//      if( a ->  head == i )
//				a ->  resCap = cap[a - arcs];
//		}
	//TODO enum, inactive, active, finish
		final int WHITE = 0;
		final int GREY = 1;
		final int BLACK = 2;

		Node tos, bos;
		// init
		tos = bos = null;
		for( Node i : getProblem().getNetwork() ) {
			distanceLabels.set( i, WHITE );
			//    buckets[i-nodes].firstActive = NULL;
			activeBuckets.reset();
			//buckets[i-nodes].firstActive = sentinelNode;
			current.set( i, first.get( i ) );
		}

		/* eliminate flowValue cycles, topologicaly order vertices */

		for( Node i : getProblem().getNetwork() )
			if( distanceLabels.get( i ) == WHITE && excess.get( i ) > 0 && !i.equals( source ) && !i.equals( sink ) ) {

				Node r = i;
				distanceLabels.set( r, GREY );
				do {
					for( ; current.get( i ) != last.get( i ); current.increase( i, 1 ) ) {
						ResidualEdge a = residualEdges[current.get( i )];
						if( (a.reverseEdge) && (a.residualCapacity > 0) ) {
							Node j = a.end();
							if( distanceLabels.get( j ) == WHITE ) {
								/* start scanning j */
								distanceLabels.set( j, GREY );
								buckets[j.id()] = i;
								i = j;
								break;
							} else if( distanceLabels.get( j ) == GREY ) {
								/* find minimum flowValue on the cycle */
								int delta = a.residualCapacity;
								while( true ) {
									delta = Math.min( delta, residualEdges[current.get( j )].residualCapacity );
									if( j.equals( i ) )
										break;
									else
										j = residualEdges[current.get( j )].end();
								}

								/* remove delta flowValue units */
								j = i;
								while( true ) {
									a = residualEdges[current.get( j )];
									a.residualCapacity -= delta;
									a.reverse.residualCapacity += delta;
									j = a.end();
									
									if( j.equals( i ) )
										break;
								}

								/* backup DFS to the first saturated arc */
								Node restart = i;
								for( j = residualEdges[current.get( i )].end(); !j.equals( i ); j = a.end() ) {
									a = residualEdges[current.get( j )];
									if( (distanceLabels.get( j ) == WHITE) || (a.residualCapacity == 0) ) {
										distanceLabels.set( residualEdges[current.get( j )].end(), WHITE );
										if( distanceLabels.get( j ) != WHITE )
											restart = j;
									}
								}

								if( !restart.equals( i ) ) {
									i = restart;
									current.increase( i, 1 );
									break;
								}
							}
						}
					}

					if( current.get( i ) == last.get( i ) ) {
						/* scan of i complete */
						distanceLabels.set( i, BLACK );
						if( !i.equals( source ) )
							if( bos == null ) {
								bos = i;
								tos = i;
							} else {
								next[i.id()] = tos;
								tos = i;
							}

						if( i != r ) {
							i = buckets[i.id()];
							current.increase( i, 1 );
						} else
							break;
					}
				} while( true );
			}

		/* return excesses */
		/* note that sink is not on the stack */
		if( bos != null ) {
			for( Node i = tos; !i.equals( bos ) ; i = next[i.id()] ) {
				int pos = first.get( i );
				ResidualEdge a = residualEdges[pos];
				while( excess.get( i ) > 0 ) {
					if( (a.reverseEdge) && (a.residualCapacity > 0) ) {
						int delta;
						if( a.residualCapacity < excess.get( i ) )
							delta = a.residualCapacity;
						else
							delta = excess.get( i );
						a.residualCapacity -= delta;
						a.reverse.residualCapacity += delta;
						excess.decrease( i, delta );
						excess.increase( a.end(), delta );
					}
					a = residualEdges[++pos];
				}
			}
			/* now do the bottom */
			Node i = bos;
			int pos = first.get( i );
			ResidualEdge a = residualEdges[pos];
			while( excess.get( i ) > 0 ) {
				if( (a.reverseEdge) && (a.residualCapacity > 0) ) {
					int delta;
					if( a.residualCapacity < excess.get( i ) )
						delta = a.residualCapacity;
					else
						delta = excess.get( i );
					a.residualCapacity -= delta;
					a.reverse.residualCapacity += delta;
					excess.decrease( i, delta );
					excess.increase( a.end(), delta );
				}
				a = residualEdges[++pos];
			}
		}

	}

	protected void discharge( Node v ) {
		assert excess.get( v ) > 0;
		assert v.id() != sink.id();
		do {
			int nodeDistance = distanceLabels.get( v );	// current node distance. -1 is applicable distance

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
					break;
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
	 * @param emptyBucket the index of an empty bucket, where a gap possibly starts
	 * @return {@code true} if a gap was found, {@code false} otherwise
	 */
	protected boolean gap( int emptyBucket ) {
		gaps++;

		/* set labels of nodes beyond the gap to "infinity" */
		for( int l = emptyBucket + 1; l <= activeBuckets.getdMax(); l++ ) {
			/* this does nothing for high level selection
			for (i = l -> firstActive; i != sentinelNode; i = i -> bNext) {
			i -> d = n;
			gNodeCnt++;
			}
			l -> firstActive = sentinelNode;
			 */

			// TODO iterator
			for( Node node = inactiveBuckets.get( l ); node != null; node = inactiveBuckets.next( node ) ) {
				distanceLabels.set( node, n );
				gapNodes++;
			}
			// TODO change somehow...
			inactiveBuckets.set( l, null );

		}

		final boolean cc = (activeBuckets.getMinIndex() > emptyBucket - 1) ? true : false;

		activeBuckets.setMaxIndex( emptyBucket - 1 );
		return cc;
	}

	/**
	 *
	 * @param e
	 * @return the rest excess at the start node of the edge
	 */
	private int push( ResidualEdge e ) {
		pushes++;
		final int delta = e.residualCapacity < excess.get( e.start() ) ? e.residualCapacity : excess.get( e.start() );
		e.residualCapacity -= delta;
		e.reverse.residualCapacity += delta;

		if( !e.end().equals( sink ) && excess.get( e.end() ) == 0 ) {
			// excess of a.end will be positive after the push!
			// remove j from the inactive list and put to the active list
			final int dist = distanceLabels.get( e.start() )-1;
			inactiveBuckets.deleteInactive( dist, e.end() );
			activeBuckets.addActive( dist, e.end() );
		}

		excess.decrease( e.start(), delta );
		excess.increase( e.end(), delta );

		return excess.get( e.start() );
	}

	private boolean pushApplicable( ResidualEdge e ) {
		return isActive( e.start() ) && isAdmissible( e );
	}

	protected int relabel( Node v ) {
		assert excess.get( v ) > 0;

		relabels++;
		relabelsSinceLastGlobalRelabel += 12;

		distanceLabels.set( v, n );
		int minDistance = n;
		ResidualEdge minEdge = null;

		// search for the minimum distance value
		for( int i = useBugHeuristic ? current.get( v ) : first.get( v ); i < last.get( v ); ++i ) {
			relabelsSinceLastGlobalRelabel++;
			final ResidualEdge e = residualEdges[i];
			if( e.residualCapacity > 0 && distanceLabels.get( e.end() ) < minDistance ) {
				minDistance = distanceLabels.get( e.end() );
				minEdge = e;
			}
		}

		minDistance++;

		if( minDistance < n ) {
			distanceLabels.set( v, minDistance );
			current.set( v, minEdge.id() );
			if( activeBuckets.getdMax() < minDistance )
				activeBuckets.setdMax( minDistance );
		}

		return minDistance;
	}

	protected boolean relabelApplicable( Node v ) {
//		if( !isActive( v ) )
//			return false;
//
//		//ArrayList<Edge> el = incidentEdges.get( v );
//		for( Edge e : el /*residualNetwork.outgoingEdges( v )*/ ) {
//			if( pushApplicable( e ) ) {
//				return false;
//			}
//		}
//
		return true;
	}

	private static enum GlobalUpdateState {
		inactiveFirst,
		inactiveNext,
		activeFirst,
		activeNext;
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

		//addInactive( 0, sink );
		inactiveBuckets.addInactive( 0, sink );
		for( int curDist = 0; true; curDist++ ) {
			final int curDistPlusOne = curDist+1;
			if( activeBuckets.get( curDist ) == null && inactiveBuckets.get( curDist ) == null )
				break;

			Node node = null;
			GlobalUpdateState nextNodeState = GlobalUpdateState.inactiveFirst; // which type is the next node.
			while( true ) {
				switch( nextNodeState ) {
					case inactiveFirst:
						node = inactiveBuckets.get( curDist );
						nextNodeState = GlobalUpdateState.inactiveNext;
						break;
					case inactiveNext:
						node = inactiveBuckets.next( node );
						break;
					case activeFirst:
						node = activeBuckets.get( curDist );
						nextNodeState = GlobalUpdateState.activeNext;
						break;
					case activeNext:
						node = activeBuckets.next( node );
						break;
					default:
						throw new AssertionError( nextNodeState );
				}

				if( node == null )
					if( nextNodeState == GlobalUpdateState.inactiveNext ) {
						nextNodeState = GlobalUpdateState.activeFirst.activeFirst;
						continue;
					} else {
						assert nextNodeState == GlobalUpdateState.activeNext : nextNodeState;
						break;
					}

				// scanning arcs incoming to a node (these are reverse arcs from outgoing arcs)
				for( int i = first.get( node ); i < last.get( node ); ++i ) {
					final ResidualEdge a = residualEdges[i];
					if( a.reverse.residualCapacity > 0 ) {
						final Node j = a.end();
						if( distanceLabels.get( j ) == n ) {
							distanceLabels.set( j, curDistPlusOne );
							current.set( j, first.get( j ) );
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

	public void checkFlow() {
		IdentifiableIntegerMapping<Edge> flow = new IdentifiableIntegerMapping<Edge>( m );
		for( ResidualEdge e : residualEdges ) {
			if( e.original == null )
				continue;
			flow.set( e.original, e.reverse.residualCapacity );
		}

		MaximumFlow mf = new MaximumFlow( getProblem(), flow );
		System.out.println( "Flow value: " + mf.getFlowValue() );
	}

	private boolean isAdmissible( ResidualEdge e ) {
		return e.residualCapacity > 0 && distanceLabels.get( e.start() ) == distanceLabels.get( e.end() ) + 1;
	}

	protected boolean isActive( Node v ) {
		return /*!v.equals( source ) && */ !v.equals( sink ) && distanceLabels.get( v ) < n && excess.get( v ) > 0;
	}

	public long getFlow() {
		return flowValue;
	}

	public int getPushes() {
		return pushes;
	}

	public int getRelabels() {
		return relabels;
	}

	public int getGlobalRelabels() {
		return globalRelabels;
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

	  IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<Edge>(  5 );
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
