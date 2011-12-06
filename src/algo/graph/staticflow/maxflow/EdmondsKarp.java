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
	long pushes = 0;
	int flow = 0;
	int augmentations = 0;
	Node source;
	Node sink;
	boolean verbose = true;

	public EdmondsKarp() {
		super();
	}

	@Override
	protected MaximumFlow runAlgorithm( MaximumFlowProblem problem ) {
		initializeDatastructures();

		int maxPossibleFlow = 0;
		for( Edge e : residualNetwork.outgoingEdges( source ) )
			maxPossibleFlow += residualNetwork.residualCapacities().get( e );
		
		int maxPossibleFlow2 = 0;
		for( Edge e : residualNetwork.incomingEdges( sink ) )
			if( residualNetwork.residualCapacities().get( e ) == Integer.MAX_VALUE ) {
				maxPossibleFlow2= Integer.MAX_VALUE;
				break;
			} else
				maxPossibleFlow2 += residualNetwork.residualCapacities().get( e );
		
		if( maxPossibleFlow2 < maxPossibleFlow )
			maxPossibleFlow = maxPossibleFlow2;
		
		while( augmentFlow() != 0 )
				fireProgressEvent( (double)flow/maxPossibleFlow );

		return new MaximumFlow( getProblem(), residualNetwork.flow() );
	}

	private void initializeDatastructures() {
		residualNetwork = new ResidualNetwork( getProblem().getNetwork(), getProblem().getCapacities() );
		source = getProblem().getSource();
		sink = getProblem().getSink();
	}

	public int augmentFlow() {
		BFS bfs = new BFS( residualNetwork );
		bfs.run( source, sink );

		// Compute min
		int min = Integer.MAX_VALUE;
		Node current = sink;
		do {
			final Edge e = bfs.predecedingEdge( current );
			if( e == null )
				return 0;
			min = Math.min( min, residualNetwork.residualCapacities().get( e ) );
			current = e.start();
		} while( !current.equals( source ) );

		// augment
		current = sink;
		do {
			final Edge e = bfs.predecedingEdge( current );
			residualNetwork.augmentFlow( e, min );
			pushes++;
			current = e.start();
		} while( !current.equals( source ) );

		flow += min;
		augmentations++;
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