/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.spanningtree.Prim;
import org.zetool.graph.Edge;
import org.zetool.container.collection.IdentifiableCollection;
import algo.graph.spanningtree.UndirectedTree;
import algo.graph.spanningtree.MinSpanningTreeProblem;
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
		LOG.info( "Compute minimum spanning tree using Prim... " );
		primalgo.run();
		LOG.log( Level.INFO, "done in {0}", primalgo.getRuntimeAsString());
		UndirectedTree minspantree = primalgo.getSolution();
		return minspantree.getEdges();
	}
}
