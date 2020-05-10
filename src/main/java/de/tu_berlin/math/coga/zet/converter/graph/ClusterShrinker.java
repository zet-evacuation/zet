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

import algo.graph.reduction.ClusterAlgo;
import org.zetool.graph.Edge;
import org.zetool.container.collection.IdentifiableCollection;
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
