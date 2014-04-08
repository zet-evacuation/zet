/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.shortestpath.DijkstraWithRationalDistances;
import de.tu_berlin.coga.container.collection.ListSequence;
import ds.graph.Edge;
import de.tu_berlin.coga.container.collection.IdentifiableCollection;
import ds.graph.network.DynamicNetwork;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Marlen Schwengfelder
 */
public class ShortestPathGraphShrinker extends GraphShrinker {

	public ShortestPathGraphShrinker() {
		super( true );
	}

	@Override
	IdentifiableCollection<Edge> runEdge() {
    int edgeNumber = 0;
    IdentifiableCollection<Edge> solEdges = new ListSequence<>();

		Map<Edge, Double> costs = new HashMap<>( getProblem().numberOfEdges() );
		for( Edge edge : getProblem().graph().edges() )
			costs.put( edge, (double)getProblem().getTransitTime( edge ) );

		DijkstraWithRationalDistances dijkstra = new DijkstraWithRationalDistances( (DynamicNetwork)getProblem().graph(), costs, getProblem().getSupersink() );
		dijkstra.run();
		DynamicNetwork shortestPathNetwork = dijkstra.getShortestPathGraph();

		for( Edge edge : shortestPathNetwork.edges() ) {
			Edge create = new Edge( edgeNumber++, edge.start(), edge.end() );
			//Edge create = getProblem().getEdge( edge.start(), edge.end() ); // create a new edge here (all edges should be enumerated from 1 to m
			solEdges.add( create );
		}
		return solEdges;
	}
}