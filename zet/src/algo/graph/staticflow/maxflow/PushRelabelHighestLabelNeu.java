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
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.flow.MaximumFlow;
import ds.graph.problem.MaximumFlowProblem;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class PushRelabelHighestLabelNeu extends PushRelabel {
	ResidualGraph residualGraph;
	/** The index of the current edge for a given node in the edges array. */
	protected IdentifiableIntegerMapping<Node> current;
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
		activeBuckets = new BucketPriorityQueue<Node>( n + 1, Node.class );
		activeBuckets.setDistanceLabels( distanceLabels );
		inactiveBuckets = new BucketSet<Node>( n + 1, Node.class );
		residualGraph = new ResidualGraph( n, m );
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

		// compute outgoing flow
		int s = 0;
		for( int i = residualGraph.getFirst( source ); i < residualGraph.getLast( source ); ++i ) {// TODO edge iterator
			Edge e = residualGraph.getReverseEdge( residualGraph.getEdge( i ) );
			s += residualGraph.getResidualCapacity( e );
		}
		System.out.println( "Fluss: " + s );

		IdentifiableIntegerMapping<Edge> flow = new IdentifiableIntegerMapping<Edge>( m );
// TODO copy results from residual network to maximum flow
//		for( ResidualEdge e : residualEdges ) {
//			if( e.original == null )
//				continue;
//			flow.set( e.original, e.reverse.residualCapacity );
//		}
		return new MaximumFlow( getProblem(), flow );
	}

	/**
	 * Sets up the data structures. At first, creates reverse edges and orders the
	 * edges according to their tail nodes into the array. Thus, the incident
	 * edges to a vertex can be searched by a run through the array.
	 */
	protected void init() {
		residualGraph.init( getProblem().getNetwork(), getProblem().getCapacities(), current );
		// initialize excesses
		excess.set( source, 0 );

		for( int i = residualGraph.getFirst( source ); i < residualGraph.getLast( source ) ; ++i ) {
			Edge e = residualGraph.getEdge( i );
			if( e.end().id() != source.id() ) { // loops?
				final int delta = residualGraph.getResidualCapacity( e );
				residualGraph.augment( e, delta );
				excess.increase( e.end(), delta );
			}
		}
		pushes += residualGraph.getLast( source );

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
			int i; // for all outarcs
			for( i = current.get( v ); i < residualGraph.getLast( v ); ++i ) {
				final Edge e = residualGraph.getEdge( i );
				if( residualGraph.getResidualCapacity( e ) > 0 && distanceLabels.get( e.end() ) == nodeDistance - 1 && push( e ) == 0 )
					break;
			}

			// all outgoing arcs are scanned now.
			if( i == residualGraph.getLast( v ) ) {
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
	protected int push( Edge e ) {
		pushes++;
		final int delta = residualGraph.getResidualCapacity( e ) < excess.get( e.start() ) ? residualGraph.getResidualCapacity( e ) : excess.get( e.start() );
		residualGraph.augment( e, delta );

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
	protected int relabel( Node v ) {
		assert excess.get( v ) > 0;

		relabels++;

		distanceLabels.set( v, n );

		final Tupel<Integer,Edge> minEdge = searchForMinDistance( v );
		if( minEdge.getU() < n ) {
			distanceLabels.set( v, minEdge.getU() );
			current.set( v, minEdge.getV().id() );
			if( activeBuckets.getdMax() < minEdge.getU() )
				activeBuckets.setdMax( minEdge.getU() );
		}
		return minEdge.getU();
	}

	protected Tupel<Integer,Edge> searchForMinDistance( Node v ) {
		int minDistance = n;
		Edge minEdge = null;
		// search for the minimum distance value
		for( int i = residualGraph.getFirst( v ); i < residualGraph.getLast( v ); ++i ) {
			final Edge e = residualGraph.getEdge( i );
			if( residualGraph.getResidualCapacity( e ) > 0 && distanceLabels.get( e.end() ) < minDistance ) {
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
			current.set( i, residualGraph.getFirst( i ) );
		}

		/* eliminate flowValue cycles, topologicaly order vertices */
		for( Node i : getProblem().getNetwork() )
			if( distanceLabels.get( i ) == FeasibleState.Unused.val && excess.get( i ) > 0 && !i.equals( source ) && !i.equals( sink ) ) {

				Node r = i;
				distanceLabels.set( r, FeasibleState.Active.val );
				do {
					for( ; current.get( i ) != residualGraph.getLast( i ); current.increase( i, 1 ) ) {
						Edge a = residualGraph.getEdge( current.get( i ) );
						if( residualGraph.isReverseEdge( a ) && residualGraph.getResidualCapacity( a )> 0 ) {
							Node j = a.end();
							if( distanceLabels.get( j ) == FeasibleState.Unused.val ) {
								/* start scanning j */
								distanceLabels.set( j, FeasibleState.Active.val );
								buckets[j.id()] = i;
								i = j;
								break;
							} else if( distanceLabels.get( j ) == FeasibleState.Active.val ) {
								/* find minimum flowValue on the cycle */
								int delta = residualGraph.getResidualCapacity( a );
								while( !j.equals( i ) )
									j = residualGraph.getEdge( current.get( j ) ).end();
								delta = Math.min( delta, residualGraph.getReverseResidualCapacity( current.get( j ) ) );

								/* remove delta flowValue units */
								do {
									a = residualGraph.getEdge( current.get( j ) );
									residualGraph.augment( a, delta );
									j = a.end();
								} while( !j.equals( i ) );

								/* backup DFS to the first saturated arc */
								Node restart = i;
								for( j = residualGraph.getEdge( current.get( i ) ).end(); !j.equals( i ); j = a.end() ) {
									a = residualGraph.getEdge( current.get( j ) );
									if( (distanceLabels.get( j ) == FeasibleState.Unused.val) || (residualGraph.getResidualCapacity( a ) == 0) ) {
										distanceLabels.set( residualGraph.getEdge( current.get( j ) ).end(), FeasibleState.Unused.val );
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

					if( current.get( i ) == residualGraph.getLast( i ) ) {
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
				int pos = residualGraph.getFirst( i );
				Edge a = residualGraph.getEdge( pos );
				while( excess.get( i ) > 0 ) {
					if( (residualGraph.isReverseEdge( a ) ) && (residualGraph.getResidualCapacity( a ) > 0) ) {
						final int delta = residualGraph.augmentMax( a, excess.get( i ) );
						excess.decrease( i, delta );
						excess.increase( a.end(), delta );
					}
					a = residualGraph.getEdge( ++pos );
				}
			}
			/* now do the bottom */
			final Node i = bos;
			int pos = residualGraph.getFirst( i );
			Edge a = residualGraph.getEdge( pos );
			while( excess.get( i ) > 0 ) {
				if( (residualGraph.isReverseEdge( a )) && (residualGraph.getResidualCapacity( a ) > 0) ) {
					final int delta = residualGraph.augmentMax( a, excess.get( i ) );
					excess.decrease( i, delta );
					excess.increase( a.end(), delta );
				}
				a = residualGraph.getEdge( ++pos );
			}
		}
	}

	private boolean isAdmissible( Edge e ) {
		return residualGraph.getResidualCapacity( e ) > 0 && distanceLabels.get( e.start() ) == distanceLabels.get( e.end() ) + 1;
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

		PushRelabelHighestLabelNeu hipr = new PushRelabelHighestLabelNeu();
		hipr.setProblem( mfp );
		hipr.run();

		//System.out.println( "Flow: " + hipr.getFlow() );
		System.out.println( "Flow: " + hipr.getSolution().getFlowValue() );
	}
}
