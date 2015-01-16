/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.spanningtree;

import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.Edge;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import org.zetool.graph.Graph;

/**
 *
 * @author Jan-Philipp Kappmeier
 * @author Marlen Schwengfelder
 */
public class MinSpanningTreeProblem {

  private NetworkFlowModel graph;
  private IdentifiableIntegerMapping<Edge> distances;

  public MinSpanningTreeProblem( NetworkFlowModel graph, IdentifiableIntegerMapping<Edge> distances ) {
    this.graph = graph;
    this.distances = distances;
  }

  public NetworkFlowModel getNetworkFlowModel() {
    return graph;
  }

  public IdentifiableIntegerMapping<Edge> getDistances() {
    return distances;
  }

  public Graph getGraph() {
    return graph.graph();
  }
}
