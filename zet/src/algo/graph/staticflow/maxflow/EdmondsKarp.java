/**
 * EdmondsKarp.java
 * Created: Oct 8, 2010, 11:39:11 AM
 */
package algo.graph.staticflow.maxflow;

import algo.graph.traverse.BFS;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.ResidualNetwork;
import ds.graph.flow.MaximumFlow;
import ds.graph.problem.MaximumFlowProblem;


/**
 * An implementation of the algorithm of Edmonds and Karp. Successively flow is
 * augmented along shortest s-t-paths.
 *
 * Warning: there is a bug in here, on some instances, the result is not correct.
 * @author Jan-Philipp Kappmeier
 */
public class EdmondsKarp extends Algorithm<MaximumFlowProblem, MaximumFlow> {
		protected ResidualNetwork residualNetwork;

	public EdmondsKarp() {
		super();
	}

	@Override
	protected MaximumFlow runAlgorithm( MaximumFlowProblem problem ) {
		initializeDatastructures();

		while( augmentFlow() != 0 )
			;

		return new MaximumFlow( getProblem(), residualNetwork.flow() );
	}

	private void initializeDatastructures() {
		residualNetwork = new ResidualNetwork( getProblem().getNetwork(), getProblem().getCapacities() );
		source = getProblem().getSource();
		sink = getProblem().getSink();
	}

	long pushes = 0;
	int flow = 0;
	int augmentations = 0;
	Node source;
	Node sink;
	boolean verbose = true;

	public int augmentFlow() {
		int min = Integer.MAX_VALUE;
		//CapacitatedBFS<V,E> bfs = new CapacitatedBFS<V,E>();
		BFS bfs = new BFS( residualNetwork );
		bfs.run( source, sink );
		//if( verbose )
		//	System.out.println( "Start Ford & Fulkerson Algorithm");
		Node current = sink;

		if( augmentations == 471 ) {
			int k = 0;
			k++;
		}

		// Compute min
		while( !current.equals( source ) ) {
			Edge e = bfs.predecedingEdge( current );
			if( e == null ) {
				return 0;
			}
			if( residualNetwork.residualCapacities().get( e ) == 0 ) {
				int k = 0;
				k++;
			} else
				min = Math.min( min, residualNetwork.residualCapacities().get( e ) );

			current = e.start();
		}
		if( min == Integer.MAX_VALUE )
			return 0;
		// augment
		current = sink;
		while( !current.equals( source ) ) {
			Edge e = bfs.predecedingEdge( current );


			residualNetwork.augmentFlow( e, min );
			pushes++;



			current = e.start();
		}
		//System.out.println( "Augment flow by " + min + ". Now is: " + flow );
		flow += min;
		if( flow == 1077 ) {
			int k = 0;
			k++;
		}
		augmentations++;
		//if( augmentations % 100 == 0 )
		//	System.out.println( "Augmentation: " + augmentations + ", Flow: " + flow + ", Pushes: " + pushes );
		
		//while( bfs.findPath( network ) ) {
		//	augmentations++;
		//	if( verbose )
		//		System.out.println( "Path found: " + bfs.getPath().toString() );
		//	augment( bfs.getPath(), bfs.getPath().getMinLeft() );
		//	flow += bfs.getPath().getMinLeft();
		//	bfs.resetNumbers();
		//}
		return min;
	}

	public int getFlow() {
		return flow;
	}

	public int getAugmentations() {
		return augmentations;
	}

	public long getPushes() {
		return pushes;
	}

}
