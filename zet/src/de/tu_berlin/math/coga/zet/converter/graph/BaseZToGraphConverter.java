
package de.tu_berlin.math.coga.zet.converter.graph;

import org.zetool.common.algorithm.Algorithm;
import org.zetool.common.datastructure.Tuple;
import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import org.zetool.graph.Edge;
import org.zetool.graph.Graph;
import org.zetool.graph.Node;
import de.tu_berlin.coga.zet.model.BuildingPlan;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Jan-Philipp Kappmeier
 * @author Martin Groß
 */
public abstract class BaseZToGraphConverter extends Algorithm<BuildingPlan, NetworkFlowModel> {
	protected ZToGraphMapping mapping;
	protected NetworkFlowModel model;
	protected ZToGraphRasterContainer raster;
	protected Graph roomGraph;
	public int numStairEdges = 0;
	public List<Edge> stairEdges = new LinkedList<>();

  protected BaseZToGraphConverter() {
  }

  protected NetworkFlowModel getModel() {
    return model;
  }



	@Override
	protected NetworkFlowModel runAlgorithm( BuildingPlan problem ) {
		raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer( problem );
		model = new NetworkFlowModel( raster );
		mapping = model.getZToGraphMapping();

		createNodes();

    // create edges, their capacities and the capacities of the nodes
		createEdgesAndCapacities();
    log.info( "Alle Kanten erzeugt." );

    // calculate the transit times for all edges
		computeTransitTimes();

		// duplicate the edges and their transit times (except those concerning the super sink)
    createReverseEdges();

    // adjust transit times according to stair speed factors
    log.setLevel( Level.ALL );
		multiplyWithUpAndDownSpeedFactors();
    log.setLevel( Level.FINE );

		// set this before reverse edges are computed as they modify the model.
		model.roundTransitTimes();

		model.resetAssignment();

		assert checkParallelEdges();

		log.log( Level.INFO, "Number of nodes: {0}", model.numberOfNodes() );
		log.log( Level.INFO, "Number of edges: {0}", model.numberOfEdges() );
		return model;
	}

	protected abstract void createNodes();

	protected abstract void createEdgesAndCapacities();

	protected abstract void computeTransitTimes();

  int originalEdges;
	protected void createReverseEdges() {
    originalEdges = model.numberOfEdges();
		createReverseEdges( model );
	}

	public static void createReverseEdges( NetworkFlowModel model ) {
		//int edgeIndex = numberOfEdges();
		final int oldEdgeIndex = model.numberOfEdges();
             
		model.setNumberOfEdges( model.numberOfEdges() * 2 - model.numberOfSinks() );

		// don't use an iterator here, as it will result in concurrent modification
		for( int i = 0; i < oldEdgeIndex; ++i ) {
			Edge edge = model.getEdge( i );
      if( !edge.isIncidentTo( model.getSupersink() ) ) {
				model.createReverseEdge( edge );
			}
		}
	}

  protected void multiplyWithUpAndDownSpeedFactors() {
    for( Edge edge : model.edges() ) {
      if( !edge.isIncidentTo( model.getSupersink() ) ) {
        switch( mapping.getEdgeLevel( edge ) ) {
          case Higher:
            double factor = 1.0;
            if( mapping.getUpNodeSpeedFactor( edge.start() ) != 1.0 ) {
              factor = mapping.getUpNodeSpeedFactor( edge.start() );
            } else if( mapping.getUpNodeSpeedFactor( edge.end() ) != 1.0 ) {
              factor = mapping.getUpNodeSpeedFactor( edge.end() );
            }
            model.divide( edge, factor );
            log.log( Level.FINEST, "Multiplying edge {0} with up speed factor {1}", new Object[]{edge, factor});
            break;
          case Lower:
            factor = 1.0;
            if( mapping.getDownNodeSpeedFactor( edge.start() ) != 1.0 ) {
              factor = mapping.getDownNodeSpeedFactor( edge.start() );
            } else if( mapping.getDownNodeSpeedFactor( edge.end() ) != 1.0 ) {
              factor = mapping.getDownNodeSpeedFactor( edge.end() );
            }
            model.divide( edge, factor );
            log.log( Level.FINEST, "Multiplying edge {0} with down speed factor {1}", new Object[]{edge, factor});
            break;
        }
      }
    }
  }

	/**
	 * Checks if the generated network flow model contains at most one edge between
	 * any pair of nodes.
	 * @return {@code true} if the network does not contain parallel arcs. Otherwise, an {@link AssertionError} is thrown
	 */
	boolean checkParallelEdges() {
		int count = 0;
    log.info( "Check for parallel edges..." );

		HashMap<Tuple<Node,Node>,Edge> usedEdges = new HashMap<>( (int)(model.numberOfEdges()/0.75)+1, 0.75f );

		for( Edge edge :  model.edges() ) {
			final Tuple<Node,Node> nodePair = new Tuple<>( edge.start(), edge.end() );
			if( usedEdges.containsKey( nodePair ) ) {
				log.log( Level.WARNING, "Two edges between nodes {0}: {1} and {2}", new Object[]{nodePair, usedEdges.get( nodePair ), edge});
				//return false;
        count++;
			}
			usedEdges.put( nodePair, edge );
		}
		log.log( Level.INFO, "No parallel edges found." );
    System.err.println( "Parallel edges: " + count );
		return true;
	}
}
