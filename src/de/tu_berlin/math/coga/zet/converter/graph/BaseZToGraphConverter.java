/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.coga.common.util.Filter;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.datastructure.Tuple;
import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.Node;
import de.tu_berlin.coga.zet.model.BuildingPlan;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Martin Groß, Jan-Philipp Kappmeier
 */
public abstract class BaseZToGraphConverter extends Algorithm<BuildingPlan, NetworkFlowModel> {
	//protected IdentifiableDoubleMapping<Edge> exactTransitTimes;
	protected ZToGraphMapping mapping;
	protected NetworkFlowModel model;
	protected ZToGraphRasterContainer raster;
	protected Graph roomGraph;
	public int numStairEdges = 0;
	public List<Edge> stairEdges = new LinkedList<>();
  protected Filter<Edge> checker;

  
  protected BaseZToGraphConverter() {
    this.checker = (Edge e) -> true;
  }
  
	protected BaseZToGraphConverter( Filter<Edge> checker ) {
		this.checker = checker;
	}
  
  protected NetworkFlowModel getModel() {
    return model;
  }
	
	

	@Override
	protected NetworkFlowModel runAlgorithm( BuildingPlan problem ) {
		//mapping = new ZToGraphMapping();
		raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer( problem );
		model = new NetworkFlowModel( raster );
		mapping = model.getZToGraphMapping();
    checker = new DefaultEdgeFilter( model );
		
		createNodes();
		// create edges, their capacities and the capacities of the nodes
		createEdgesAndCapacities();
		// connect the nodes of different rooms with edges
		//HashMap<Edge, ArrayList<ZToGraphRasterSquare>> doorEdgeToSquare = connectRooms(raster, model); // done in compute transit times now!
		// calculate the transit times for all edges

		computeTransitTimes();
		// adjust transit times according to stair speed factors
		multiplyWithUpAndDownSpeedFactors();

		// set this before reverse edges are computed as they modify the model.
		//model.setTransitTimes( exactTransitTimes.round() );
		model.roundTransitTimes();

		// duplicate the edges and their transit times (except those concerning the super sink)
    System.out.println( "Edgecaps: " + model.edgeCapacities.getDomainSize() );
    //if( manualStairs ) {
    //  createReverseEdgesWithoutStairEdges( model );
    //} else {
    createReverseEdges();
    //}
    System.out.println( "Edgecaps: " + model.edgeCapacities.getDomainSize() );

		model.resetAssignment();

		assert checkParallelEdges();
		
		//model.setNetwork( model.getGraph().getAsStaticNetwork() );
		log.log( Level.INFO, "Number of nodes: {0}", model.numberOfNodes() );
		log.log( Level.INFO, "Number of edges: {0}", model.numberOfEdges() );
		return model;

	}

	protected abstract void createNodes();

	protected abstract void createEdgesAndCapacities();

	protected abstract void computeTransitTimes();

	protected void createReverseEdges() {
		createReverseEdges( model, checker );
	}

	public static void createReverseEdges(NetworkFlowModel model, Filter<Edge> checker ) {
		//int edgeIndex = numberOfEdges();
		final int oldEdgeIndex = model.numberOfEdges();
		model.setNumberOfEdges( model.numberOfEdges() * 2 - model.numberOfSinks() );

		// don't use an iterator here, as it will result in concurrent modification
		for( int i = 0; i < oldEdgeIndex; ++i ) {
			Edge edge = model.getEdge( i );
      if( checker.accept( edge ) ) {
//if( !edge.isIncidentTo( model.getSupersink() ) ) {
				Edge newEdge = model.createReverseEdge( edge );
			}
		}
	}

	// TODO
	protected void createReverseEdgesWithoutStairEdges( NetworkFlowModel model ) {
		int edgeIndex = model.numberOfEdges();
		final int oldEdgeIndex = edgeIndex;
    
    final int normalEdges = edgeIndex - numStairEdges;
    // edges = 2*normal + existing stair edges. reduce by model.numberOfSinks because for sinks we do not create backward edges
    
    model.setNumberOfEdges( normalEdges * 2 + numStairEdges - model.numberOfSinks() );
		//model.setNumberOfEdges( ((edgeIndex - numStairEdges) * 2) - model.numberOfSinks() );

		// don't use an iterator here, as it will result in concurrent modification
		for( int i = 0; i < oldEdgeIndex; ++i ) {
			Edge edge = model.getEdge( i );
			if( !stairEdges.contains( edge ) && !edge.isIncidentTo( model.getSupersink() ) ) {
				Edge newEdge = model.createReverseEdge( edge );
			}
		}
	}

	protected void multiplyWithUpAndDownSpeedFactors() {
		for( Edge edge : model.edges() )
			if( !edge.isIncidentTo( model.getSupersink() ) )
				switch( mapping.getEdgeLevel( edge ) ) {
					case Higher:
						model.divide( edge, mapping.getUpNodeSpeedFactor( edge.start() ) );
						//exactTransitTimes.divide( edge, mapping.getUpNodeSpeedFactor( edge.start() ) );
						break;
					case Lower:
						model.divide( edge, mapping.getDownNodeSpeedFactor( edge.start() ) );
						//exactTransitTimes.divide( edge, mapping.getDownNodeSpeedFactor( edge.start() ) );
						break;
				}
	}

	/**
	 * Checks if the generated network flow model contains at most one edge between
	 * any pair of nodes.
	 * @return {@code true} if the network does not contain parallel arcs. Otherwise, an {@link AssertionError} is thrown
	 */
	boolean checkParallelEdges() {
		log.info( "Check for parallel edges..." );
		
		HashMap<Tuple<Node,Node>,Edge> usedEdges = new HashMap<>( (int)(model.numberOfEdges()/0.75)+1, 0.75f );
		
		for( Edge edge :  model.edges() ) {
			final Tuple<Node,Node> nodePair = new Tuple<>( edge.start(), edge.end() );
			if( usedEdges.containsKey( nodePair ) ) {
				log.log( Level.WARNING, "Two edges between nodes {0}: {1} and {2}", new Object[]{nodePair, usedEdges.get( nodePair ), edge});
				return false;
			}
			usedEdges.put( nodePair, edge );
		}
		log.log( Level.INFO, "No parallel edges found." );
		return true;
	}
}
