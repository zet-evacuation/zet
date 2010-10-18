/**
 * HIPR2.java
 * Created: Oct 12, 2010, 11:17:14 AM
 */
package algo.graph.staticflow.maxflow;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.graph.ArraySet;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.flow.MaximumFlow;
import ds.graph.problem.MaximumFlowProblem;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class HIPR2 extends Algorithm<MaximumFlowProblem, MaximumFlow> {

	//protected ResidualNetwork residualNetwork;
//protected transient MaxHeap<Node, Integer> activeNodes;
	protected transient IdentifiableIntegerMapping<Node> distanceLabels;
	protected transient IdentifiableIntegerMapping<Node> excess;

	long flow;

	int pushes;
	int relabels;

	int n;
	int m;

	Node source;
	Node sink;
	//LinkedList<UnorderedEdge>[] incidentEdges;
	IdentifiableObjectMapping<Node, ArrayList> incidentEdges;
	IdentifiableIntegerMapping<Node> current;

	ArraySet<Edge> residualEdges;
	IdentifiableIntegerMapping<Edge> residualCapacities;

	@Override
	protected MaximumFlow runAlgorithm( MaximumFlowProblem problem ) {
		init();
		preflowPush();
		//return new MaximumFlow( getProblem(), residualNetwork.flow() );
		return null;
	}

	private void init() {
		source = getProblem().getSource();
		sink = getProblem().getSink();
		n = getProblem().getNetwork().numberOfNodes();
		m = getProblem().getNetwork().numberOfEdges();
		distanceLabels = new IdentifiableIntegerMapping<Node>( n );
		excess = new IdentifiableIntegerMapping<Node>( m );

		//residualNetwork = new ResidualNetwork( getProblem().getNetwork(), getProblem().getCapacities() );
		residualEdges = new ArraySet<Edge>(Edge.class, 2*m);
		for( Edge e : getProblem().getNetwork().edges() ) {
			residualEdges.add( e );
			residualEdges.add( new Edge(e.id() + m, e.end(), e.start() ) );
		}

		residualCapacities = new IdentifiableIntegerMapping<Edge>(  2*m );
		for( int i = 0; i < m; ++i  ) {
			residualCapacities.add( residualEdges.get( i ), getProblem().getCapacities().get( residualEdges.get( i ) ) );
			residualCapacities.add( residualEdges.get( i+m ), 0 );
		}

		distanceLabels.set( getProblem().getSink(), 0 );
		Queue<Node> q = new LinkedList<Node>();
		q.add( getProblem().getSink() );

		int d = 1;

		while( q.size() != 0 ) {
			Node currentNode = q.poll();
			d = distanceLabels.get( currentNode );
			for( Node node : getProblem().getNetwork().predecessorNodes( currentNode ) ) {
				if( distanceLabels.get( node ) == 0 && !node.equals( sink ) ) {
				//if( !distanceLabels.isDefinedFor( currentNode ) ) {
					distanceLabels.set( node, Math.min( n, d+1 ) );
					q.add( node );
				}
				//}
			}
		}

		// create list of incident edges
		current = new IdentifiableIntegerMapping<Node>( n );
		incidentEdges = new IdentifiableObjectMapping<Node, ArrayList>( n, ArrayList.class );
		// Create all lists
		for( Node node : getProblem().getNetwork().nodes() ) {
			ArrayList<Edge> list = new ArrayList<Edge>( getProblem().getNetwork().outdegree( node )*2 );
			incidentEdges.set( node, list );
			current.set( node, 0 );
		}
		// run over all edges
		for( Edge e : residualEdges ) {
			incidentEdges.get( e.start() ).add( e );
		}

		// init buckets
		buckets = new LinkedList[n];
		for( int i = 0; i < n; ++i ) {
			buckets[i] = new LinkedList<Node>();
		}
		b = 0;
		buckets[0].add( source );

		// scan all edges and put them into the internal list
		//System.out.println( "Scanning " + residualNetwork.edges().size() + " edges." );
//		for( Edge e : residualNetwork.edges() ) {
//			incidentEdges.get( e.start() ).add( e );
//		}

		// compute excess
		int ex = 0;
		int edgeCount = 0;
		for( Edge e : getProblem().getNetwork().outgoingEdges( source ) ) {
			ex += getProblem().getCapacities().get( e );
			edgeCount++;
		}

		excess.set( source, ex+1 );
	}

	LinkedList<Node>[] buckets;
	int b;

	protected void preflowPush() {

		while( true /* active nodes exist */ ) {
			// first, check if in bucket b any element is contained, otherwise,
			// decrese b
			while( b >= 0 && buckets[b].size() == 0 ) {
				--b;
			}
			if( b < 0 ) {
				// no active node
				break;
			}
			// remove a node out of the active list
			Node active = buckets[b].pollFirst();
			discharge( active );


		}
		// we have a max flow
		flow = excess.get( sink );
	}

	protected void discharge( Node v ) {
		// note here v is not in the buckets of active nodes!
		//boolean applicable = isActive( v );
		//if( !applicable )
		//	throw new IllegalArgumentException( "Discharging not applicable" );

		//System.out.println( "Discharge " + v.toString() );

		// Action
		ArrayList<Edge> list = incidentEdges.get( v );
		int cur = current.get( v );
		Edge e = list.get( cur ); // current u,v!

		boolean endOfList = false;
		do {
			if( isAdmissible( e ) )
				push( e );
			else {
				if( cur < list.size()-1 /*&& residualNetwork.residualCapacities().get( e ) != 0*/ /* is last edge in the list of v*/)  {
					//cur++;
					e = list.get( ++cur );
					// next edge = current edge
				} else {
					cur = 0;
					e = list.get( cur );
					endOfList = true;
				}
			}
		} while( excess.get( v ) != 0 && !endOfList );
		current.set( v, cur );
		if( endOfList ) {
			relabel( v );
		}

		if( isActive( v ) ) {
			int bucketIndex = distanceLabels.get( v );
			buckets[bucketIndex].add( v );
			if( bucketIndex > b )
				b = bucketIndex;
		}
	}

	protected void push( Edge e /* * (v,w) */ ) {
		// note, that e.start() is active here but is not in the bucket of active nodes!
		//boolean applicable = pushApplicable( e );
		//if( !applicable )
		//	throw new IllegalStateException( "Push is not applicable!" );

		boolean bActive = isActive( e.end() );
		pushes++;
		// Action
		final int oldCap = residualCapacities.get( e );
		int delta = Math.min( excess.get( e.start() ), oldCap );

		//System.out.println( "Augmenting edge " + e.toString() + " by " + delta + ". Orignal value was: " + residualNetwork.residualCapacities().get( e ) );
		//residualNetwork.augmentFlow( e, delta );

		// augment flow by delta == change residual capacities
		final Edge resEdte = residualEdges.get( e.id() + (e.id() >= m ? -m : m) );//  e.id() >= m ?
		residualCapacities.set( e, oldCap - delta );
		residualCapacities.set( resEdte, residualCapacities.get( resEdte ) + delta );

//		if( e.id() >= m ) {
//			// e is an residual edge
//			residualCapacities.set( e, oldCap - delta );
//			final  Edge resEdge = residualEdges.get( e.id() - m) ;
//			residualCapacities.set( resEdge, residualCapacities.get( resEdge ) + delta );
//		} else {
//			// e is a normal edge
//			residualCapacities.set( e, oldCap - delta );
//			final  Edge resEdge = residualEdges.get( e.id() + m) ;
//			residualCapacities.set( resEdge, residualCapacities.get( resEdge ) + delta );
//		}

		excess.increase( e.end(), delta );
		excess.decrease( e.start(), delta );
//		if( delta == 0 )
//			throw new IllegalArgumentException( "increase by zero!" );
		// now we can add the node to a bucket, if it wasn't already. at least, it has access now!

//		if( e.end().equals( sink ) ) {
			//System.out.println( "Flow: " + excess.get( sink ) + " Pushes: " + pushes + "Relabels: " + relabels );
//		}
		if( !bActive && !e.end().equals( sink ) ) {
			int bucketIndex = distanceLabels.get( e.end() );
			buckets[bucketIndex].add( e.end() );
			if( bucketIndex > b )
				b = bucketIndex;
		}
	}

	protected boolean pushApplicable( Edge e ) {
		return isActive( e.start() ) && isAdmissible( e );
	}

	protected void relabel( Node v ) {
		//boolean applicable = relabelApplicable( v );
		//if( !applicable ) {
		//	throw new IllegalStateException( "Relabel not applicable!" );
			//applicable = relabelApplicable( v );
		//}

		//int oldDistance = distanceLabels.get( v );


		// Action
		int newDistance;
		int k = 0;
		newDistance = Integer.MAX_VALUE;
		ArrayList<Edge> edges = incidentEdges.get( v );
		for( Edge e : edges ) {
			// ignore edges with zero residual capacity
			if( residualCapacities.get( e )  > 0 && distanceLabels.get( e.end() ) < newDistance ) {
				newDistance = distanceLabels.get( e.end() ); //; = Math.min( distanceLabels.get( e.end() ), newDistance );
			}
		}
		if( newDistance == Integer.MAX_VALUE )
			newDistance = n;
		else
			newDistance += 1;

		//if( oldDistance == newDistance ) {
		//	System.out.println( "Old == new!" );
		//}

		relabels++;
		//System.out.println( "Relabeling " + v.toString() + " from " + distanceLabels.get( v ) + " to " + newDistance );
		distanceLabels.set( v, newDistance );
	}

	protected boolean relabelApplicable( Node v ) {
		if( !isActive( v ) )
			return false;

		ArrayList<Edge> el = incidentEdges.get( v );
		for( Edge e : el /*residualNetwork.outgoingEdges( v )*/ ) {
			if( pushApplicable( e ) ) {
				return false;
			}
		}

		return true;
	}

	protected boolean isAdmissible( Edge e ) {
		//return residualNetwork.residualCapacities().get( e ) > 0 && distanceLabels.get( e.start() ) == distanceLabels.get( e.end() ) + 1;
		return residualCapacities.get( e ) > 0 && distanceLabels.get( e.start() ) == distanceLabels.get( e.end() ) + 1;
	}

	protected boolean isActive( Node v ) {
		return /*!v.equals( source ) && */ !v.equals( sink ) && distanceLabels.get( v ) < n && excess.get( v ) > 0;
	}

	public long getFlow() {
		return flow;
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

	public int getPushes() {
		return pushes;
	}

	public int getRelabels() {
		return relabels;
	}
}
