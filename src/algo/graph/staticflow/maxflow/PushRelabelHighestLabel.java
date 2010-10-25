/**
 * PushRelabelHighestLabel.java
 * Created: Oct 21, 2010, 6:03:17 PM
 */
package algo.graph.staticflow.maxflow;

import de.tu_berlin.math.coga.datastructure.BucketSet;
import de.tu_berlin.math.coga.datastructure.Tupel;
import de.tu_berlin.math.coga.datastructure.priorityQueue.BucketPriorityQueue;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Node;
import ds.graph.flow.MaximumFlow;
import ds.graph.problem.MaximumFlowProblem;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PushRelabelHighestLabel extends PushRelabel {
	/**
	 * A class for residual edges that have a residual capacity and a reverse edge.
	 * The edges save the information, if they are a reverse edge with respect
	 * to the original network and which edge of the original network they
	 * correspond with.
	 */
	public class ResidualEdge extends Edge {
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
	/** An array of residual edges for the network. */
	protected ResidualEdge[] residualEdges;
	/** The index of the current edge for a given node in the edges array. */
	protected IdentifiableIntegerMapping<Node> current;
	/** The index of the first outgoing edge of a node in the edges array. */
	protected IdentifiableIntegerMapping<Node> first;
	/** The index of the last outgoing edge of a node in the edges array. */
	protected IdentifiableIntegerMapping<Node> last;
	/** The buckets for active nodes. */
	protected BucketPriorityQueue<Node> activeBuckets;
	/** The buckets for inactive nodes. */
	protected BucketSet<Node> inactiveBuckets;

	/**
	 * Allocates the memory for the data structures. These contain information
	 * for the nodes and edges.
	 */
	private void alloc() {
		distanceLabels = new IdentifiableIntegerMapping<Node>( n );	// distance
		excess = new IdentifiableIntegerMapping<Node>( n );	// excess
		current = new IdentifiableIntegerMapping<Node>( n );	// current edge datastructure (index)
		first = new IdentifiableIntegerMapping<Node>( n ); // first outgoing edge (index)
		last = new IdentifiableIntegerMapping<Node>( n ); // last outgoing edge (index)
		activeBuckets = new BucketPriorityQueue<Node>( n + 1, Node.class );
		activeBuckets.setDistanceLabels( distanceLabels );
		inactiveBuckets = new BucketSet<Node>( n + 1, Node.class );
		residualEdges = new ResidualEdge[2 * m];
	}

	@Override
	protected MaximumFlow runAlgorithm( MaximumFlowProblem problem ) {
		source = getProblem().getSource();
		sink = getProblem().getSink();
		n = getProblem().getNetwork().numberOfNodes();
		m = getProblem().getNetwork().numberOfEdges();
		long start = System.nanoTime();
		alloc();
		init();
		long end = System.nanoTime();
		initTime = end-start;
		start = System.nanoTime();
		computeMaxFlow();
		end = System.nanoTime();
		phase1Time = end-start;
		start = System.nanoTime();
		makeFeasible();
		end = System.nanoTime();
		phase2Time = end-start;

		IdentifiableIntegerMapping<Edge> flow = new IdentifiableIntegerMapping<Edge>( m );
		for( ResidualEdge e : residualEdges ) {
			if( e.original == null )
				continue;
			flow.set( e.original, e.reverse.residualCapacity );
		}
		return new MaximumFlow( getProblem(), flow );
	}

	/**
	 * Sets up the data structures. At first, creates reverse edges and orders the
	 * edges according to their tail nodes into the array. Thus, the incident
	 * edges to a vertex can be searched by a run through the array.
	 */
	protected void init() {
		// set up residual edges
		int edgeCounter = 0;
		int[] temp = new int[2 * m];
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
				temp[e.id() + m] = edgeCounter++;
			}
			last.set( v, edgeCounter );
		}
		for( int i = 0; i < m; ++i ) {
			residualEdges[temp[i]].reverse = residualEdges[temp[i + m]];
			residualEdges[temp[i + m]].reverse = residualEdges[temp[i]];
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

	@Override
	protected void computeMaxFlow() {
		while( activeBuckets.getMaxIndex() >= activeBuckets.getMinIndex() ) {
			final Node v = activeBuckets.max();
			if( v != null ) {
				activeBuckets.removeActive( activeBuckets.getMaxIndex(), v );
				discharge( v );

				if( activeBuckets.getMaxIndex() < activeBuckets.getMinIndex() )
					break;
			}
		}
		flowValue = excess.get( sink ); // we have a max flowValue
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
	 *
	 * @param e
	 * @return the rest excess at the start node of the edge
	 */
	protected int push( ResidualEdge e ) {
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

	@Override
	protected int push( Edge e ) {
		if( e instanceof ResidualEdge )
			return push( (ResidualEdge)e );
		else throw new IllegalArgumentException( "Need to use ResidualEdge class for edges with this algorithm" );
	}

	@Override
protected int relabel( Node v ) {
		assert excess.get( v ) > 0;

		relabels++;

		distanceLabels.set( v, n );

		final Tupel<Integer,ResidualEdge> minEdge = searchForMinDistance( v );
		if( minEdge.getU() < n ) {
			distanceLabels.set( v, minEdge.getU() );
			current.set( v, minEdge.getV().id() );
			if( activeBuckets.getdMax() < minEdge.getU() )
				activeBuckets.setdMax( minEdge.getU() );
		}
		return minEdge.getU();
	}

	protected Tupel<Integer,ResidualEdge> searchForMinDistance( Node v ) {
		int minDistance = n;
		ResidualEdge minEdge = null;
		// search for the minimum distance value
		for( int i = first.get( v ); i < last.get( v ); ++i ) {
			final ResidualEdge e = residualEdges[i];
			if( e.residualCapacity > 0 && distanceLabels.get( e.end() ) < minDistance ) {
				minDistance = distanceLabels.get( e.end() );
				minEdge = e;
			}
		}
		return new Tupel( minDistance+1, minEdge );
	}

	private static enum FeasibleState {
		Unused( 0 ),
		Active( 1 ),
		Finished( 2 );
		int val;
		FeasibleState( int value ) {
			this.val = value;
		}
	}

	@Override
	protected void makeFeasible() {
		Node[] buckets = new Node[n];
		Node[] next = new Node[n];

		Node tos, bos;
		// init
		tos = bos = null;
		for( Node i : getProblem().getNetwork() ) {
			distanceLabels.set( i, FeasibleState.Unused.val );
			activeBuckets.reset();
			current.set( i, first.get( i ) );
		}

		/* eliminate flowValue cycles, topologicaly order vertices */
		for( Node i : getProblem().getNetwork() )
			if( distanceLabels.get( i ) == FeasibleState.Unused.val && excess.get( i ) > 0 && !i.equals( source ) && !i.equals( sink ) ) {

				Node r = i;
				distanceLabels.set( r, FeasibleState.Active.val );
				do {
					for( ; current.get( i ) != last.get( i ); current.increase( i, 1 ) ) {
						ResidualEdge a = residualEdges[current.get( i )];
						if( (a.reverseEdge) && (a.residualCapacity > 0) ) {
							Node j = a.end();
							if( distanceLabels.get( j ) == FeasibleState.Unused.val ) {
								/* start scanning j */
								distanceLabels.set( j, FeasibleState.Active.val );
								buckets[j.id()] = i;
								i = j;
								break;
							} else if( distanceLabels.get( j ) == FeasibleState.Active.val ) {
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
									if( (distanceLabels.get( j ) == FeasibleState.Unused.val) || (a.residualCapacity == 0) ) {
										distanceLabels.set( residualEdges[current.get( j )].end(), FeasibleState.Unused.val );
										if( distanceLabels.get( j ) != FeasibleState.Unused.val )
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
						distanceLabels.set( i, FeasibleState.Finished.val );
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
						final int delta = a.residualCapacity < excess.get( i ) ? a.residualCapacity : excess.get( i );
						a.residualCapacity -= delta;
						a.reverse.residualCapacity += delta;
						excess.decrease( i, delta );
						excess.increase( a.end(), delta );
					}
					a = residualEdges[++pos];
				}
			}
			/* now do the bottom */
			final Node i = bos;
			int pos = first.get( i );
			ResidualEdge a = residualEdges[pos];
			while( excess.get( i ) > 0 ) {
				if( (a.reverseEdge) && (a.residualCapacity > 0) ) {
					final int delta = a.residualCapacity < excess.get( i ) ? a.residualCapacity : excess.get( i );
					a.residualCapacity -= delta;
					a.reverse.residualCapacity += delta;
					excess.decrease( i, delta );
					excess.increase( a.end(), delta );
				}
				a = residualEdges[++pos];
			}
		}

	}

	private boolean isAdmissible( ResidualEdge e ) {
		return e.residualCapacity > 0 && distanceLabels.get( e.start() ) == distanceLabels.get( e.end() ) + 1;
	}
}
