/**
 * HIPR.java
 * Created: Oct 8, 2010, 4:01:10 PM
 */
package algo.graph.staticflow.maxflow;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.flow.MaximumFlow;
import ds.graph.problem.MaximumFlowProblem;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class HIPR extends Algorithm<MaximumFlowProblem, MaximumFlow> {

	boolean useBugHeuristic = false;

	private class myEdge {
		int id;
		Node start;
		Node end;
		myEdge reverse;
		int residualCapacity;
		myEdge( int id, Node start, Node end, int cap ) {
			this.id = id;
			this.start = start;
			this.end = end;
			this.residualCapacity = cap;
		}

		@Override
		public String toString() {
			return( "(" + start.id() + "," + end.id() + ")" );
		}

	}

	protected int[] excess;
	protected int[] distanceLabels;

	boolean performGlobalRelabel = true;

	long flow;

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
	//LinkedList<UnorderedEdge>[] incidentEdges;
	//IdentifiableObjectMapping<Node, ArrayList> incidentEdges;
	myEdge[] residualEdges;
	IdentifiableIntegerMapping<Node> current;
	IdentifiableIntegerMapping<Node> first;
	IdentifiableIntegerMapping<Node> last;

	IdentifiableObjectMapping<Node, Node> next;
	IdentifiableObjectMapping<Node, Node> prev;
	boolean[] active;
	boolean[] inactive;

	//int[] reverseEdges;

	Node[] activeBuckets;
	Node[] inactiveBuckets;
	int dMax;	// maximum distance label
	int aMax;	// maximum active label
	int aMin; // maximum inactive label

	//IdentifiableIntegerMapping<Edge> residualCapacities;
	//int[] residualCapacities;

	@Override
	protected MaximumFlow runAlgorithm( MaximumFlowProblem problem ) {
		//return new MaximumFlow( getProblem(), residualNetwork.flow() );

		source = getProblem().getSource();
		sink = getProblem().getSink();
		n = getProblem().getNetwork().numberOfNodes();
		m = getProblem().getNetwork().numberOfEdges();

		alloc();
		init();
		stageOne();

		return null;
	}

	/**
	 * Allocates the memory for the data structures.
	 */
	private void alloc() {
		// Node information
		//distanceLabels = new IdentifiableIntegerMapping<Node>( n );	// distance
		distanceLabels = new int[n];
		//excess = new IdentifiableIntegerMapping<Node>( n );	// excess
		excess = new int[n];
		current = new IdentifiableIntegerMapping<Node>( n );	// current edge datastructure (index)
		first = new IdentifiableIntegerMapping<Node>( n ); // first outgoing arc (index)
		last = new IdentifiableIntegerMapping<Node>( n ); // last outgoing arc (index)
		next = new IdentifiableObjectMapping<Node, Node> ( n, Node.class );	// next node in the bucket list
		prev = new IdentifiableObjectMapping<Node, Node> ( n, Node.class ); // previous node in the bucket list
		
		// edge information
		//residualEdges = new ArraySet<Edge>(Edge.class, 2*m);	// array of all edges (including reverse edges)
		residualEdges = new myEdge[2*m];

		activeBuckets = new Node[n+1];
		inactiveBuckets = new Node[n+1];
		active = new boolean[n+1];
		inactive = new boolean[n+1];
	}

	/**
	 * Sets up the data structures. At first, creates reverse edges and orders the
	 * edges according to their tail nodes into the array. Thus, the incident
	 * edges to a vertex can be searched by a run through the array.
	 */
	private void init() {
		nm = 6*n+m;
		globalRelabelThreshold = (int)0.5*nm;
		globalRelabels = 0;	// set to -1 here, as it will be set to 0 during initialization

		int[] temp = new int[2*m];

		int edgeCounter = 0;

		for( Node v : getProblem().getNetwork() ) {
			first.set( v, edgeCounter );
			current.set( v, edgeCounter );
			// add the outgoing edges to the arc list
			for( Edge e : getProblem().getNetwork().outgoingEdges( v ) ) {
				residualEdges[edgeCounter] = new myEdge( edgeCounter, e.start(), e.end(), getProblem().getCapacities().get( e ) );
				temp[e.id()] = edgeCounter++;
			}
			// add the reverse edge for incoming edges to the arc list (they are also outgoing!
			for( Edge e : getProblem().getNetwork().incomingEdges( v ) ) {
				residualEdges[edgeCounter] = new myEdge( edgeCounter, e.end(), e.start(), 0 );
				temp[e.id()+m] = edgeCounter++;
			}
			last.set( v, edgeCounter );
		}
		for( int i = 0; i < m; ++i ) {
			final myEdge newEdge = residualEdges[temp[i]];
			final myEdge newRevEdge = residualEdges[temp[i+m]];
			newEdge.reverse = newRevEdge;
			newRevEdge.reverse = newEdge;
		}
		// set up residual edges
	  // initialize excesses
		excess[source.id()] = 0;
		for( int i = first.get( source ); i < last.get( source ); ++i ) {
			myEdge e = residualEdges[i];
			if( e.end.id() != source.id() ) {
				pushes++;
				final int delta = e.residualCapacity;
				e.residualCapacity -= delta;
				e.reverse.residualCapacity += delta;
				excess[e.end.id()] += delta;
			}
		}

		// bucket indizes
		aMax = 0;
		aMin = n;

		for( Node v : getProblem().getNetwork() ) {
			final int id = v.id();
			if( id == sink.id() ) {
				distanceLabels[id] = 0;
				addInactive( 0, v );
				continue;
			}
			distanceLabels[id] = id == source.id() ? n : 1;
			if( excess[id] > 0 )
				addActive( 1, v );
			else if( distanceLabels[id] < n )
				addInactive( 1, v );
		}
		dMax = 1;
	}


	protected void stageOne() {
		while( aMax >= aMin ) {
			final Node v = activeBuckets[aMax];
			if( v == null ) {
				aMax--;
				if( aMax < aMin && useBugHeuristic )
					globalUpdate();
			} else {
				removeActive( aMax, v );

				assert excess[v.id()] > 0;
				discharge( v );

				if( aMax < aMin )
					break;

				/* is it time for global update? */
				if( relabelsSinceLastGlobalRelabel > nm )
					globalUpdate();
			}
		}
		// we have a max flow
		flow = excess[sink.id()];
	}

	protected void discharge( Node v ) {
		// note here v is not in the activeBuckets of active nodes!
		//boolean applicable = isActive( v );
		//if( !applicable )
		//	throw new IllegalArgumentException( "Discharging not applicable" );

		//System.out.println( "Discharge " + v.toString() );

  assert excess[v.id()] > 0;
  assert v.id() != sink.id();
	do {
		int l = distanceLabels[v.id()];	// current node distance
		int jD = l-1;	// the distance of applicable nodes

		/* outarcs of v */
		int i;
		for( i = current.get( v ); i < last.get( v ); ++i ) {
			myEdge e = residualEdges[i];
			if( e.residualCapacity > 0 ) {
				Node j = e.end;

				if( distanceLabels[j.id()] == jD ) {	// if is applicable
					// push
					if ( push( e ) == 0 )
						break;
				}
			}
		}

		// all outgoing arcs are scanned now.
		if( i == last.get( v ) ) {
			// relabel, ended due to pointer at the last arc
			relabel( v );

			if( distanceLabels[v.id()] == n )
				break;

			// todo: gap
			if( activeBuckets[l] == null && inactiveBuckets[l] == null )
				gap( l );

			// todo gap-break

			if( distanceLabels[v.id()] == n )
				break;

//			if ((l -> firstActive == sentinelNode) &&
//         (l -> firstInactive == sentinelNode)
//          )
//        gap (l);
		} else {
			// node is no longer active
			current.set( v, i );
			// put the vertex on the inactive list
			addInactive( l, v );
			break;
		}
	} while( true );

}

	/**
	 * Gap relabeling
	 * @param l
	 */
	protected int gap( int emptyB ) {
		gaps++;
		int r = emptyB-1;
		int cc;

  /* set labels of nodes beyond the gap to "infinity" */
  for ( int l = emptyB + 1; l <= dMax; l++ ) {
    /* this does nothing for high level selection
    for (i = l -> firstActive; i != sentinelNode; i = i -> bNext) {
      i -> d = n;
      gNodeCnt++;
    }
    l -> firstActive = sentinelNode;
    */

		for( Node node = inactiveBuckets[l]; node != null; node = next.get( node ) ) {
			distanceLabels[node.id()] = n;
			gapNodes++;
		}
		inactiveBuckets[l] = null;

//    for ( int i =  l -> firstInactive; i != sentinelNode; i = i -> bNext ) {
//      i -> d = n;
//      gNodeCnt ++;
//    }
//
//    l -> firstInactive = sentinelNode;
  }

  cc = ( aMin > r ) ? 1 : 0;

  dMax = r;
  aMax = r;

		return cc;
	}

	/**
	 *
	 * @param a
	 * @return the rest excess at the start node of the edge
	 */
	private int push( myEdge a /* * (v,w) */ ) {
		pushes++;
		int delta;
		if( a.residualCapacity < excess[a.start.id()] ) {
			delta = a.residualCapacity;
		} else {
			delta = excess[a.start.id()];
		}
		a.residualCapacity -= delta;
		a.reverse.residualCapacity += delta;

		if( a.end.id() != sink.id() ) {
			int dist = distanceLabels[a.start.id()]-1;
			if( excess[a.end.id()] == 0 ) {	// excess of a.end will be positive after the push!
				// remove from j from inactive list
				deleteInactive( dist, a.end );
				// put j to active list
				addActive( dist, a.end );
			}
		}

		excess[a.start.id()] -= delta;
		excess[a.end.id()] += delta;

//		if( a.end.equals( sink ) ) {
//			System.out.println( "Flow: " + excess[sink.id()] + " Pushes: " + pushes + "Relabels: " + relabels );
//		}



		return excess[a.start.id()];
	}

	private boolean pushApplicable( myEdge e ) {
		return isActive( e.start ) && isAdmissible( e );
	}

	protected int relabel( Node v ) {
		//boolean applicable = relabelApplicable( v );
		//if( !applicable ) {
		//	throw new IllegalStateException( "Relabel not applicable!" );
			//applicable = relabelApplicable( v );
		//}

		//int oldDistance = distanceLabels.get( v );

		assert excess[v.id()] > 0;

		relabels++;
		relabelsSinceLastGlobalRelabel += 12;

		distanceLabels[v.id()] = n;
		int minD = n;
		myEdge minEdge = null;

		// search for the minimum distance value
		for( int i = useBugHeuristic ? current.get( v ) : first.get( v ); i < last.get( v ); ++i ) {
			relabelsSinceLastGlobalRelabel++;
			final myEdge e = residualEdges[i];

			if( e.residualCapacity > 0 ) {
				final int id = distanceLabels[e.end.id()];
				if( id < minD ) {
					minD = id;
					minEdge = e;
				}
			}
		}

		minD++;

		if( minD < n ) {
			distanceLabels[v.id()] = minD;
			current.set( v, minEdge.id );
			if( dMax < minD )
				dMax = minD;
		}

		return minD;
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

	private  final void addInactive( int distance, Node node ) {
		if( inactive[node.id()] )
			return;
		inactive[node.id()] = true;
		
		if( inactiveBuckets[distance] != null ) {
			final Node next_t = inactiveBuckets[distance];
			next.set( node, next_t );
			prev.set( node, null );
			prev.set( next_t, node );
		} else {
			next.set( node, null );
		}
		inactiveBuckets[distance] = node;
	}

	private  final void deleteInactive( int distance, Node node ) {
		assert inactive[node.id()];
		inactive[node.id()] = false;

		final Node next_t = next.get( node );
		if( inactiveBuckets[distance].id() == node.id() ) {
			inactiveBuckets[distance] = next_t;
			if( next_t != null )
				prev.set( next_t, null );
		} else {
			final Node prev_t = prev.get( node );
			next.set( prev_t, next.get( node ) );
			if( next_t != null)
				prev.set( next_t, prev_t );

		}
	}

	private  final void addActive( int distance, Node node ) {
		if( active[node.id()] == true )
			return;	// was already active

		active[node.id()] = true;

		next.set( node, activeBuckets[distance] );
		activeBuckets[distance] = node;
		final int dist = distanceLabels[node.id()];
		if( dist < aMin )
			aMin = dist;
		if( dist > aMax )
			aMax = dist;
		if( dMax < aMax )
			dMax = aMax;
	}

	/**
	 * Removes the first element in the bucket list of the given distance. Only
	 * works if {@code node} is the first element.
	 * @param distance
	 * @param node
	 */
	private final void removeActive( int distance, Node node ) {
		assert active[node.id()];
		active[node.id()] = false;
		activeBuckets[distance] = next.get( node );
	}

	public void printActiveBucket( int distance ) {
		Node node = activeBuckets[distance];
		System.out.print( "ABucket " + distance + ": " );
		while( node != null ) {
			System.out.print( node.id() );
			if( prev.get( node ) != null ) {
				System.out.print( "<");
			}
			if( next.get( node ) != null ) {
				System.out.print( "->" );
			}
			node = next.get( node );
		}
		System.out.println();
	}

	public void printInactiveBucket( int distance ) {
		Node node = inactiveBuckets[distance];
		System.out.print( "IBucket " + distance + ": " );
		while( node != null ) {
			System.out.print( node.id() );
			if( next.get( node ) != null && prev.get( next.get( node ) ) != null ) {
				System.out.print( "<");
			}
			if( next.get( node ) != null ) {
				System.out.print( "->" );
			}
			node = next.get( node );
		}
		System.out.println();
	}

	private static enum States {
		inactiveFirst,
		inactiveNext,
		activeFirst,
		activeNext;
	}

	protected void globalUpdate() {
		relabelsSinceLastGlobalRelabel = 0;
		globalRelabels++;
		
		for( int i = 0; i <= dMax; ++i ) {
			activeBuckets[i] = null;
			inactiveBuckets[i] = null;
		}
		dMax = aMax = 0;
		aMin = n;

		// all node distances to n
		for( Node node : getProblem().getNetwork().nodes() ) {
			distanceLabels[node.id()] = n;
			active[node.id()] = false;
			inactive[node.id()] = false;
		}
		distanceLabels[sink.id()] = 0;

		Node node = null;

		addInactive( 0, sink );
		for( int curDist = 0; true; curDist++ ) {
			final int cdpo = curDist+1;
			//int nextNodeState = INACTIVE_FIRST; // which type is the next node.
			States nextNodeState = States.inactiveFirst; // which type is the next node.
			if( activeBuckets[curDist] == null && inactiveBuckets[curDist] == null )
				break;

			while( true ) {
				switch( nextNodeState ) {
					case inactiveFirst:
						node = inactiveBuckets[curDist];
						nextNodeState = States.inactiveNext;
						break;
					case inactiveNext:
						node = next.get( node );
						break;
					case activeFirst:
						node = activeBuckets[curDist];
						nextNodeState = States.activeNext;
						break;
					case activeNext:
						node = next.get( node );
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
				for( int i = first.get( node ); i < last.get( node ); ++i ) {
					final myEdge a = residualEdges[i];
					if( a.reverse.residualCapacity > 0 ) {
						final Node j = a.end;
						if( distanceLabels[j.id()] == n ) {
							distanceLabels[j.id()] = cdpo;
							current.set( j, first.get( j ) );
							if( cdpo > dMax)
								dMax = cdpo;

							if( excess[j.id()] > 0 )
								// put into active list
								addActive( cdpo, j );
							else
							// put into inactive list
								addInactive( cdpo, j);
						}
					}
				}
			}
		}
	}

	private boolean isAdmissible( myEdge e ) {
		return e.residualCapacity > 0 && distanceLabels[e.start.id()] == distanceLabels[e.end.id()] + 1;
	}

	protected boolean isActive( Node v ) {
		return /*!v.equals( source ) && */ !v.equals( sink ) && distanceLabels[v.id()] < n && excess[v.id()] > 0;
	}

	public long getFlow() {
		return flow;
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
