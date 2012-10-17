/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.reduction.Prim;
import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import ds.graph.MinSpanningTree;
import ds.graph.problem.MinSpanningTreeProblem;
import java.util.logging.Level;

/**
 *
 * @author Marlen Schwengfelder
 */
public class SpanningTreeShrinker extends GraphShrinker {
	
	/**
	 * Initializes the instance for beeing solved.
	 */
	public SpanningTreeShrinker() {
		super( true );
	}

	/**
	 * Shrinks the graph in the {@link NetworkFlowModel} by creating a spanning
	 * tree. The returned edges are exactly the edges in the minimum spanning
	 * tree on the nodes of the graph. The transit times on the arcs are used
	 * as spanning tree weights.
	 * @see GraphShrinker.runEdge()
	 * @return edges in a minimum spanning tree on the problem instance graph
	 */
	@Override
	IdentifiableCollection<Edge> runEdge() {
		MinSpanningTreeProblem minimumSpanningTreeProblem = new MinSpanningTreeProblem( getProblem(), getProblem().transitTimes() );

		//using Prims algorithm
		Prim primalgo = new Prim();
		primalgo.setProblem( minimumSpanningTreeProblem );
		log.info( "Compute minimum spanning tree using Prim... " );
		primalgo.run();
		log.log( Level.INFO, "done in {0}", primalgo.getRuntimeAsString());
		MinSpanningTree minspantree = primalgo.getSolution();
		return minspantree.getEdges();
	}
}
