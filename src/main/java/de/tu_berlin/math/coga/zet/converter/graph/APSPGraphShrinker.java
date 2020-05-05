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

//import de.tu_berlin.math.coga.algorithm.shortestpath.APSPAlgo;
import org.zetool.algorithm.shortestpath.APSPAlgo;
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
    
    //APSPAlgo apspalgo = new APSPAlgo( getProblem() );
    APSPAlgo apspalgo = new APSPAlgo();
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