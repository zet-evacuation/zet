/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.reduction.GreedyAlgo;
import org.zetool.algorithm.spanningtree.UndirectedForest;
import org.zetool.graph.Edge;
import org.zetool.container.collection.IdentifiableCollection;

/**
 * Creates a t-spanner for grid graphs using a greedy algorithm.
 * @author Marlen Schwengfelder
 */
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
		//creates a t-spanner using a greedy algorithm
		GreedyAlgo greedySpannerAlgorithm = new GreedyAlgo();
		greedySpannerAlgorithm.setProblem( getProblem() );
		greedySpannerAlgorithm.run();
		UndirectedForest minspantree = greedySpannerAlgorithm.getSolution();
		System.out.print( "Compute t-Spanner using greedy... " );
		System.out.println( "used time: " + greedySpannerAlgorithm.getRuntimeAsString() );
		IdentifiableCollection<Edge> MinEdges = minspantree.getEdges();
		//IdentifiableIntegerMapping<Edge> transit = minspantree.getTransit();
		//IdentifiableIntegerMapping<Edge> cap = minspantree.getCapac();
		return MinEdges;
	}
}
