
package algo.graph.spanningtree;

import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.graph.Edge;

/**
 *
 * @author Marlen Schwengfelder
 * @author Jan-Philipp Kappmeier
 */
public class UndirectedTree {

  private IdentifiableCollection<Edge> edges;

  public UndirectedTree( IdentifiableCollection<Edge> edges ) {
    this.edges = edges;
  }

  public IdentifiableCollection<Edge> getEdges() {
    return edges;
  }

}
