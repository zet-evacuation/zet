/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.reduction.GreedyAlgo;
import org.zetool.graph.Edge;
import org.zetool.container.collection.IdentifiableCollection;
import algo.graph.spanningtree.UndirectedTree;
import algo.graph.spanningtree.MinSpanningTreeProblem;
import org.zetool.container.mapping.IdentifiableIntegerMapping;

/**
 *
 * @author Marlen Schwengfelder
 */
//creates a t-spanner for grid graphs using a greedy algorithm 
public class GreedySpannerShrinker extends GraphShrinker {

	public GreedySpannerShrinker( ) {
		super( true );
	}
    
	
//    @Override
//    protected NetworkFlowModel runAlgorithm( NetworkFlowModel problem ) {
//
//                for (Edge neu: MinEdges) {
//                    if (neu.start() != problem.getSupersink() && neu.end()!= problem.getSupersink()) {
//											minspanmodel.addEdge( neu, cap.get( neu), transit.get( neu), problem.getExactTransitTime( neu) );
////                        newgraph.addEdge(neu);
////                        minspanmodel.setEdgeCapacity(neu, cap.get(neu));
////                        minspanmodel.setTransitTime(neu, transit.get(neu));
//                        newMapping.setEdgeLevel(neu, originalMapping.getEdgeLevel(neu));             
////                        minspanmodel.setExactTransitTime(neu, model.getExactTransitTime(neu));
//                    } else {
//											minspanmodel.addEdge( neu, Integer.MAX_VALUE, 0, 0);
////                        newgraph.addEdge(neu);
////                        minspanmodel.setEdgeCapacity(neu, Integer.MAX_VALUE);
////                        minspanmodel.setTransitTime(neu, 0);
//                        newMapping.setEdgeLevel(neu, Level.Equal);
////                        minspanmodel.setExactTransitTime(neu, 0);
//                    }
//                }
//	}

	@Override
	IdentifiableCollection<Edge> runEdge() {
		//creates a minimum spanning tree problem
		MinSpanningTreeProblem minimumSpanningTreeProblem = new MinSpanningTreeProblem( getProblem(), getProblem().transitTimes() );

		//creates a t-spanner using a greedy algorithm
		GreedyAlgo greedySpannerAlgorithm = new GreedyAlgo();
		greedySpannerAlgorithm.setProblem( minimumSpanningTreeProblem );
		greedySpannerAlgorithm.run();
		UndirectedTree minspantree = greedySpannerAlgorithm.getSolution();
		System.out.print( "Compute t-Spanner using greedy... " );
		System.out.println( "used time: " + greedySpannerAlgorithm.getRuntimeAsString() );
		IdentifiableCollection<Edge> MinEdges = minspantree.getEdges();
		//IdentifiableIntegerMapping<Edge> transit = minspantree.getTransit();
		//IdentifiableIntegerMapping<Edge> cap = minspantree.getCapac();
		return MinEdges;
	}
}
