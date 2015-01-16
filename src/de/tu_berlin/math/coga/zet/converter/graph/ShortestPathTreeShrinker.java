
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.algorithm.shortestpath.RationalDijkstra;
import org.zetool.container.collection.ListSequence;
import de.tu_berlin.coga.graph.Edge;
import org.zetool.container.collection.IdentifiableCollection;
import de.tu_berlin.coga.graph.DynamicNetwork;
import java.util.HashMap;
import java.util.Map;

/**
 * Shrinks a given network to a shortest path tree rooted at the super source.
 * As edge length for shortest path computation, the transit times are used.
 * @author Marlen Schwengfelder
 */
public class ShortestPathTreeShrinker extends GraphShrinker {
	public ShortestPathTreeShrinker() {
		super( true );
	}

	@Override
	IdentifiableCollection<Edge> runEdge() {
    int edgeNumber = 0;
    IdentifiableCollection<Edge> solEdges = new ListSequence<>();

		Map<Edge, Double> costs = new HashMap<>( getProblem().numberOfEdges() );
		for( Edge edge : getProblem().graph().edges() )
			costs.put( edge, (double)getProblem().getTransitTime( edge ) );

		RationalDijkstra dijkstra = new RationalDijkstra( (DynamicNetwork)getProblem().graph(), costs, getProblem().getSupersink() );
		dijkstra.run();
		DynamicNetwork shortestPathTree = dijkstra.getShortestPathTree();

		for( Edge edge : shortestPathTree.edges() ) {
			Edge create = new Edge( edgeNumber++, edge.start(), edge.end() );
			//Edge create = getProblem().getEdge( edge.start(), edge.end() ); // create a new edge here (all edges should be enumerated from 1 to m
			solEdges.add( create );
		}
		return solEdges;
	}
}
