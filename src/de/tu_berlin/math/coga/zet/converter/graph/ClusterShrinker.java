/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.reduction.ClusterAlgo;
import de.tu_berlin.coga.graph.Edge;
import de.tu_berlin.coga.container.collection.IdentifiableCollection;
import algo.graph.spanningtree.UndirectedTree;
import algo.graph.spanningtree.MinSpanningTreeProblem;

/**
 *
 * @author Marlen Schwengfelder
 */
public class ClusterShrinker extends GraphShrinker {
	/**
	 * Initializes the instance for beeing solved.
	 */
	public ClusterShrinker() {
		super( true );
	}

	/**
	 * Returns a shrinked graph base
	 * @return 
	 */
	@Override
	IdentifiableCollection<Edge> runEdge() {
		MinSpanningTreeProblem minspanprob = new MinSpanningTreeProblem( getProblem(), getProblem().transitTimes() );
		ClusterAlgo clusteralgo = new ClusterAlgo();
		clusteralgo.setProblem( minspanprob );
		System.out.print( "Compute Cluster... " );
		clusteralgo.run();
		System.out.println( "used time: " + clusteralgo.getRuntimeAsString() );
		UndirectedTree tree = clusteralgo.getSolution();
		IdentifiableCollection<Edge> MinEdges = tree.getEdges();
		return MinEdges;
	}
}
