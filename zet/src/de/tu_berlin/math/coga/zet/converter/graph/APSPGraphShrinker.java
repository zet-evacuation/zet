package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.algorithm.shortestpath.APSPAlgo;
import org.zetool.container.collection.ListSequence;
import org.zetool.graph.Edge;
import org.zetool.container.collection.IdentifiableCollection;
/**
 *
 * @author Marlen Schwengfelder
 */
public class APSPGraphShrinker extends GraphShrinker {

	/**
	 * Initializes the instance for beeing solved.
	 */
	public APSPGraphShrinker() {
		super( true );
	}

	/**
	 * Shrinks the graph in the {@link NetworkFlowModel} by using shortest paths
	 * between all sources and sinks.
	 * @see GraphShrinker.runEdge()
	 * @return edges on a shortest path graph between sources and sinks
	 */
	@Override
	IdentifiableCollection<Edge> runEdge() {
		//using APSP algorithm:
    int numEdges=0;
    APSPAlgo apspalgo = new APSPAlgo( getProblem() );
		int[][] succ = apspalgo.run();
		int numNodes = getProblem().numberOfNodes() - 1;

		IdentifiableCollection<Edge> solEdges = new ListSequence<>();
		int[][] used = new int[numNodes][numNodes];
		for( int i = 0; i < numNodes; i++ )
			for( int j = 0; j < numNodes; j++ )
				used[i][j] = 0;
		for( int i = 0; i < numNodes; i++ )
			for( int j = 0; j < numNodes; j++ )
				if( i != j && (used[i][succ[i][j]] != 1) && (used[succ[i][j]][i] != 1) ) {
					Edge edge = new Edge( numEdges++, getProblem().getNode( i + 1 ), getProblem().getNode( succ[i][j] + 1 ) );
					//System.out.println("i:" + i + " j:" + j + "Edge: " + edge) ;
					used[i][succ[i][j]] = 1;
					used[succ[i][j]][i] = 1;
					solEdges.add( edge );
				}
		for( Edge edge : getProblem().graph().incidentEdges( getProblem().getSupersink() ) )
			solEdges.add( edge );
		return solEdges;
	}
}