
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.coga.common.util.Filter;
import ds.graph.Edge;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
class DefaultEdgeFilter implements Filter<Edge> {
  private final NetworkFlowModel model;

  public DefaultEdgeFilter( NetworkFlowModel model ) {
    this.model = model;
  }

  @Override
  public boolean accept( Edge edge ) {
    return !edge.isIncidentTo( model.getSupersink() );
  }
  
  
}
