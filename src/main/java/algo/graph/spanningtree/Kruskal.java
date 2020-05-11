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
package algo.graph.spanningtree;

import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.container.collection.DisjointSet;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.container.collection.ListSequence;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.Edge;
import org.zetool.graph.Graph;
import org.zetool.graph.Node;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Kruskal extends AbstractAlgorithm<MinSpanningTreeProblem,UndirectedTree> {
  UndirectedTree tree;
  
  @Override
  protected UndirectedTree runAlgorithm( MinSpanningTreeProblem problem ) {
    Graph graph = problem.getNetworkFlowModel().graph();
    
    IdentifiableIntegerMapping<Edge> weights = problem.getDistances();
            
    IdentifiableCollection<Edge> treeEdges = new ListSequence<>();
    
    if( graph.edgeCount() == 0 ) {
      return tree = new UndirectedTree( treeEdges );
    }

    final Comparator<Edge> comp = (Edge o1, Edge o2) -> weights.get( o1 ) - weights.get( o2 );

    long time = System.nanoTime();
    PriorityQueue<Edge> heap = new PriorityQueue<>( graph.edgeCount(), comp );

    // iterate over all edges and add into list
    for( Edge e : graph.edges() ) {
      heap.offer( e );
    }

    long end = System.nanoTime();

    System.out.println( "Sorting: " + (end-time)/1_000_000_000d );
    DisjointSet<Node> unionFind = new DisjointSet<>( graph.nodes() );
    
    time = System.nanoTime();
    while( !heap.isEmpty() && treeEdges.size() != graph.nodeCount()-1 ) {
      Edge e = heap.poll();
      if( unionFind.find( e.start() ).id() != unionFind.find( e.end() ).id() ) {
        treeEdges.add( e );
        unionFind.union( unionFind.find( e.start() ), unionFind.find( e.end() ) );
      }
    }
    end = System.nanoTime();
    System.out.println( "Kruskal: " + (end-time)/1_000_000_000d );
  
    tree = new UndirectedTree( treeEdges );
    return tree;
  }
  
  public UndirectedTree getTree() {
    return tree;
  }

}
