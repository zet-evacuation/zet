/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.EATAPPROX;

import de.tu_berlin.math.coga.algorithm.networkflow.maximumflow.PushRelabel;
import de.tu_berlin.math.coga.datastructure.BucketSet;
import de.tu_berlin.math.coga.datastructure.Tuple;
import de.tu_berlin.math.coga.datastructure.priorityQueue.BucketPriorityQueue;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.flow.MaximumFlow;
import ds.graph.problem.MaximumFlowProblem;
import ds.mapping.IdentifiableIntegerMapping;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class NetworkFlowAlgorithm extends PushRelabel {
	HidingResidualGraph residualGraph;
	/** The index of the current edge for a given node in the edges array. */
	protected IdentifiableIntegerMapping<Node> current;
	/** The buckets for active nodes. */
	protected BucketPriorityQueue<Node> activeBuckets;
	/** The buckets for inactive nodes. */
	protected BucketSet<Node> inactiveBuckets;
	protected HashSet<Node> nonActiveExcessNodes = new HashSet<>();

	private final boolean verbose = false;

	/**
	 * Allocates the memory for the data structures. These contain information
	 * for the nodes and edges.
	 */
	private void alloc() {
		// nodecount hier muss der tatsächliche nodecount hin!
		distanceLabels = new IdentifiableIntegerMapping<>( n );	// distance
		excess = new IdentifiableIntegerMapping<>( n );	// excess
		current = new IdentifiableIntegerMapping<>( n );	// current edge datastructure (index)
		activeBuckets = new BucketPriorityQueue<>( n + 1, Node.class );
		activeBuckets.setDistanceLabels( distanceLabels );
		inactiveBuckets = new BucketSet<>( n + 1, Node.class );
		residualGraph = null;//new HidingResidualGraph( n, m, 0 );
	}

	@Override
	protected MaximumFlow runAlgorithm( MaximumFlowProblem problem ) {
		source = problem.getSource();
		sink = problem.getSink();

		FakeMaximumFlowProblem myProblem = (FakeMaximumFlowProblem)problem;
		n = myProblem.getNetwork().numberOfNodes();
		m = myProblem.getNetwork().numberOfEdges();

		long start = System.nanoTime();
		alloc();
		// reset node count!
		init();
		long end = System.nanoTime();
		initTime = end-start;
		start = System.nanoTime();
		computeMaxFlow();
		end = System.nanoTime();
		phase1Time = end-start;

		if( true )
			return null;

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
		//System.out.println( "Fluss: " + s );

		IdentifiableIntegerMapping<Edge> flow = new IdentifiableIntegerMapping<>( m );
		for( Edge e : residualGraph.edges ) {
			if( residualGraph.isReverseEdge( e ) )
				continue;
			flow.set( residualGraph.originalResidualEdgeMapping.get( e ), residualGraph.getReverseResidualCapacity( e ) );
		}
		MaximumFlow f = new MaximumFlow( problem, flow );

		f.check();

		return f;
	}

	/**
	 * Run the algorithm again, but now with the new edges available.
	 */
	void run2() {
		current.get( residualGraph.nodes.get( 10 ) );
		computeMaxFlow();
	}



	/**
	 * Sets up the data structures. At first, creates reverse edges and orders the
	 * edges according to their tail nodes into the array. Thus, the incident
	 * edges to a vertex can be searched by a run through the array.
	 */
	protected void init() {
		//residualGraph.init( getProblem().getNetwork(), getProblem().getCapacities(), current );
		FakeMaximumFlowProblem p = (FakeMaximumFlowProblem)getProblem();
		residualGraph = p.getResidualGraph();
		n = residualGraph.getCurrentVisibleNodeCount(); // update node count to the actual number of visible nodes!

		current = residualGraph.current;


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
			distanceLabels.set( v, id == source.id() ? n : 1 ); // nodecount hier muss der aktuelle node-count hin.
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
				int pushesBefore = pushes;
				discharge( v );
				int pushesAfter = pushes;
				//if( pushesBefore == pushesAfter )
					//System.out.println( "NO PUSHES HAVE BEEN PERFORMED" );

				if( activeBuckets.getMaxIndex() < activeBuckets.getMinIndex() )
					break;
			}
		}
		flowValue = excess.get( sink ); // we have a max flowValue
	}

	protected void discharge( Node v ) {
		if( verbose )
			System.out.println( "Discharge for node " + v + " with excess " + excess.get( v ) );

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

				if( distanceLabels.get( v ) == n ) { // nodecount hier soll der aktuelle-node-count hin
					assert excess.get( v ) > 0;
					nonActiveExcessNodes.add( v );
					break;
				}
			} else {
				// node is no longer active
				//System.out.println( "Set current for node " + v + " to " + i + " THE ONE LINE ");
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
	@Override
	protected int push( Edge e ) {
		pushes++;
		final int delta = residualGraph.getResidualCapacity( e ) < excess.get( e.start() ) ? residualGraph.getResidualCapacity( e ) : excess.get( e.start() );
		if( verbose )
			System.out.println( "Push " + delta + " from " + e.start() + " to " + e.end() );
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
		if( verbose )
			System.out.println( "Relabel. Old distance for " + v + " was " + distanceLabels.get( v ) );

		distanceLabels.set( v, n ); // nodecount hier soll der aktuelle nodecount hin

		final Tuple<Integer,Edge> minEdge = searchForMinDistance( v );
		if( minEdge.getU() < n ) {
			distanceLabels.set( v, minEdge.getU() );

			//System.out.println( "Set current for node " + v + " to " + minEdge.getV().id() );

			current.set( v, minEdge.getV().id() );
			if( activeBuckets.getdMax() < minEdge.getU() )
				activeBuckets.setdMax( minEdge.getU() );
		}
		return minEdge.getU();
	}

	protected Tuple<Integer,Edge> searchForMinDistance( Node v ) {
		int minDistance = n; // nodecount hier soll der aktuelle nodecount hin
		Edge minEdge = null;
		// search for the minimum distance value
		for( int i = residualGraph.getFirst( v ); i < residualGraph.getLast( v ); ++i ) {
			final Edge e = residualGraph.getEdge( i );
			if( residualGraph.getResidualCapacity( e ) > 0 ) {
				if( minEdge == null && distanceLabels.get( e.end() ) <= minDistance ) {
					minEdge = e;
					minDistance = distanceLabels.get( e.end() );
				} else if ( distanceLabels.get( e.end() ) < minDistance ) {
					minDistance = distanceLabels.get( e.end() );
					minEdge = e;
				}
			}
//			if( residualGraph.getResidualCapacity( e ) > 0 && distanceLabels.get( e.end() ) < minDistance ) {
//				minDistance = distanceLabels.get( e.end() );
//				minEdge = e;
//			}
		}
		//System.out.println( "searchForMinDistance für " + v );

		return new Tuple<>( minDistance+1, minEdge );
	}

	/**
	 * Set new (correct) distance label for the newly reachable nodes via new
	 * edges.
	 * @param newEdges
	 */
	void updateDistances( Set<Edge> newEdges ) {
		n = residualGraph.getCurrentVisibleNodeCount();

		for( Edge e: newEdges ) {
			Node u = e.start();
			Node v = e.end(); // a new node
			int oldDist = distanceLabels.get( v ); // we set to max d(u) - 1
			int uDist = distanceLabels.get( u ) - 1;
			if( uDist > oldDist ) {
				//if( verbose )
					 System.out.println( "Update distance label for node " + v + " from " + oldDist + " to " + uDist );
				// TODO: evtl. mit breitensuche nur die knoten die erreichbar sind auf inactive setzen!
				distanceLabels.set( v, uDist );
				try {
					inactiveBuckets.deleteInactive( oldDist, v );

				} catch( Exception ex ) {
					System.out.println( "EXCEPTION" );
					throw ex;
				}
				inactiveBuckets.addInactive( uDist, v );

			}
			if( excess.get( u ) > 0 ) {
				if( !nonActiveExcessNodes.contains( u ) ) {
						System.out.println( "We found a node with positive excess that is not active!" );
			//		throw new IllegalStateException( " a node with positive excess was found that is not in the set!" );
				}
				activeBuckets.addActive( uDist, u );
				System.out.println( "Adding " + u + " with distance " + uDist + " to active." );
			}
		}

		for( Node v : nonActiveExcessNodes ) {
			activeBuckets.addActive( distanceLabels.get( v ), v );
			if( verbose )
				System.out.println( "Adding excess node " + v + " with distance " + distanceLabels.get( v ) + " to active." );
		}
		nonActiveExcessNodes.clear();

		Node v = source;
		distanceLabels.set( v, n );
		//inactiveBuckets.addInactive( n, v ); // the source is neither active nor inactive
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
/*
   do dsf in the reverse flow graph from nodes with excess
   cancel cycles if found
   return excess flow in topological order
*/

/*
   i->d is used for dfs labels
   i->bNext is used for topological order list
   buckets[i-nodes]->firstActive is used for DSF tree
*/

{
	Node j,tos,bos,restart,r;
	Edge a;
  double delta;

		Node[] buckets = new Node[n];
		Node[] next = new Node[n]; // nodecount wird nur am ende aufgerufen, hier kann der tatsächliche nodecount hin?

	/* deal with self-loops */
//  forAllNodes(i) {
//    forAllArcs(i,a)
//      if ( a -> head == i ) {
//	a -> resCap = cap[a - arcs];
//      }
//  }

  /* initialize */
  tos = null;
	bos = null;
	for( Node i : getProblem().getNetwork() ) {
		distanceLabels.set( i, FeasibleState.Unused.val);
		buckets[i.id()] = null;
		if( true )
			throw new IllegalStateException( "This should not be executed!" );
		current.set( i, residualGraph.getFirst( i ) );
  }

  /* eliminate flow cycles, topologicaly order vertices */
	for( Node i : getProblem().getNetwork() ) {
    if( ( distanceLabels.get( i ) == FeasibleState.Unused.val ) && ( excess.get( i ) > 0 ) && ( i.id() != source.id() ) && ( i.id() != sink.id() ) ) {
      r = i;
			distanceLabels.set( r, FeasibleState.Active.val );
			do {
				for ( ; current.get( i ) != residualGraph.getLast( i ); current.increase( i, 1 ) ) {
					a = residualGraph.getEdge( current.get( i ) );
					if( residualGraph.isReverseEdge( a ) && residualGraph.getResidualCapacity( a ) > 0 ) {
						j = a.end();
						if( distanceLabels.get( j ) == FeasibleState.Unused.val ) {
							/* start scanning j */
							distanceLabels.set( j, FeasibleState.Active.val );
							buckets[j.id()] = i;
				      i = j;
				      break;
						}
						else
							if( distanceLabels.get( j ) == FeasibleState.Active.val ) {
								/* find minimum flow on the cycle */
								delta = residualGraph.getResidualCapacity( a );
								while ( true ) {
									delta = Math.min( delta, residualGraph.getResidualCapacity( current.get( j ) ) );
									if ( j.equals( i ) )
								    break;
									else
										j = residualGraph.getEdge( current.get( j ) ).end();
								}

								/* remove delta flow units */
								j = i;
								while ( true ) {
									a = residualGraph.getEdge( current.get( j ) );
									residualGraph.augment( a, (int)delta );
									j = a.end();
									if ( j.equals(i) )
										break;
								}

								/* backup DFS to the first saturated arc */
								restart = i;
								for( j = residualGraph.getEdge( current.get( i ) ).end(); !j.equals( i ); j = a.end() ) {
									a = residualGraph.getEdge( current.get( j ) );
									if( distanceLabels.get( j ) == FeasibleState.Unused.val || residualGraph.getResidualCapacity( a ) == 0 ) {
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

					if( !i.equals( r ) ) {
						i = buckets[i.id()];
						current.increase( i, 1 );
					} else
						break;
				}
			} while ( true );
    }
	}


  /* return excesses */
  /* note that sink is not on the stack */
	if ( bos != null ) {
    for ( Node i = tos; !i.equals( bos ) ; i = next[i.id()] ) {
			a = residualGraph.getEdge( residualGraph.getFirst( i ) );
      while ( excess.get( i ) > 0 ) {
				if( residualGraph.isReverseEdge( a ) && residualGraph.getResidualCapacity( a ) > 0 ) {
					if( residualGraph.getResidualCapacity( a ) < excess.get( i ) )
						delta = residualGraph.getResidualCapacity( a );
					else
						delta = excess.get( i );
					residualGraph.augment( a, (int)delta );
					excess.decrease( i, (int)delta );
					excess.increase( a.end(), (int)delta );
				}
				a = residualGraph.getEdge( a.id()+1 );
      }
    }
    /* now do the bottom */
    Node i = bos;
		a = residualGraph.getEdge( residualGraph.getFirst( i ) );
		while( excess.get( i ) > 0 ) {
			if( residualGraph.isReverseEdge( a ) && residualGraph.getResidualCapacity( a ) > 0 ) {
					if( residualGraph.getResidualCapacity( a ) < excess.get( i ) )
						delta = residualGraph.getResidualCapacity( a );
					else
						delta = excess.get( i );
					residualGraph.augment( a, (int)delta );
					excess.decrease( i, (int)delta );
					excess.increase( a.end(), (int) delta );
				}
				a = residualGraph.getEdge( a.id()+1 );
    }
  }
}

	}

	private boolean isAdmissible( Edge e ) {
		return residualGraph.getResidualCapacity( e ) > 0 && distanceLabels.get( e.start() ) == distanceLabels.get( e.end() ) + 1;
	}
}