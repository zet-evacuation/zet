
package algo.graph.spanningtree;

import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.container.collection.DisjointSet;
import de.tu_berlin.coga.container.collection.IdentifiableCollection;
import de.tu_berlin.coga.container.collection.ListSequence;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import de.tu_berlin.coga.graph.Edge;
import de.tu_berlin.coga.graph.Graph;
import de.tu_berlin.coga.graph.Node;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class Kruskal extends Algorithm<MinSpanningTreeProblem,UndirectedTree> {
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
