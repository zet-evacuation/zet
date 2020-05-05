/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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

import org.zetool.algorithm.shortestpath.RationalDijkstra;
import org.zetool.container.collection.ListSequence;
import org.zetool.graph.Edge;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.graph.DynamicNetwork;
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

		RationalDijkstra dijkstra = new RationalDijkstra( (DynamicNetwork)getProblem().graph(), costs, getProblem().getSupersink() );
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