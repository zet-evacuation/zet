/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import ds.graph.Edge;
import ds.graph.Graph;
import ds.z.BuildingPlan;
import java.util.LinkedList;
import java.util.List;

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

	@Override
	protected NetworkFlowModel runAlgorithm( BuildingPlan problem ) {
		//mapping = new ZToGraphMapping();
		raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer( problem );
		model = new NetworkFlowModel( raster );
		mapping = model.getZToGraphMapping();
		
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
		createReverseEdgesWithoutStairEdges( model );

		model.resetAssignment();
		
		//model.setNetwork( model.getGraph().getAsStaticNetwork() );
		System.out.println( "NumNodes: " + model.numberOfNodes() );
		System.out.println( "NumEdges: " + model.numberOfEdges() );
		return model;

	}

	protected abstract void createNodes();

	protected abstract void createEdgesAndCapacities();

	protected abstract void computeTransitTimes();

	protected void createReverseEdges() {
		createReverseEdges( model );
	}

	public static void createReverseEdges(NetworkFlowModel model ) {
		//int edgeIndex = numberOfEdges();
		final int oldEdgeIndex = model.numberOfEdges();
		model.setNumberOfEdges( model.numberOfEdges() * 2 - model.numberOfSinks() );

		// don't use an iterator here, as it will result in concurrent modification
		for( int i = 0; i < oldEdgeIndex; ++i ) {
			Edge edge = model.getEdge( i );
			if( !edge.isIncidentTo( model.getSupersink() ) ) {
				Edge newEdge = model.createReverseEdge( edge );
			}
		}
	}

	// TODO
	protected void createReverseEdgesWithoutStairEdges( NetworkFlowModel model ) {
		int edgeIndex = model.numberOfEdges();
		final int oldEdgeIndex = edgeIndex;
		model.setNumberOfEdges( ((edgeIndex - numStairEdges) * 2) - model.numberOfSinks() );

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
}
