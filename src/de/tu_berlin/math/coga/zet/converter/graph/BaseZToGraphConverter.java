/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.Edge;
import ds.graph.IdentifiableDoubleMapping;
import ds.z.BuildingPlan;

/**
 *
 * @author Martin Groß, Jan-Philipp Kappmeier
 */
public abstract class BaseZToGraphConverter extends Algorithm<BuildingPlan, NetworkFlowModel> {

	protected IdentifiableDoubleMapping<Edge> exactTransitTimes;
	protected ZToGraphMapping mapping;
	protected NetworkFlowModel model;
	protected BuildingPlan plan;
	protected ZToGraphRasterContainer raster;

	@Override
	protected NetworkFlowModel runAlgorithm( BuildingPlan problem ) {
		mapping = new ZToGraphMapping();
		plan = problem;
		model = new NetworkFlowModel();
		raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer( plan );
		mapping.setRaster( raster );
		model.setZToGraphMapping( mapping );

		createNodes();
		// create edges, their capacities and the capacities of the nodes
		createEdgesAndCapacities();


		// connect the nodes of different rooms with edges
		//Hashtable<Edge, ArrayList<ZToGraphRasterSquare>> doorEdgeToSquare = connectRooms(raster, model);
		// calculate the transit times for all edges
		computeTransitTimes();
		// adjust transit times according to stair speed factors
		multiplyWithUpAndDownSpeedFactors();

		// set this before reverse edges are computed as they modify the model.
		model.setTransitTimes( exactTransitTimes.round() );

		// duplicate the edges and their transit times (except those concerning the super sink)
		createReverseEdges( model );

		model.setNetwork( model.getGraph().getAsStaticNetwork() );

		return model;

	}

	protected abstract void createNodes();

	protected abstract void createEdgesAndCapacities();

	protected abstract void computeTransitTimes();

	protected void createReverseEdges( NetworkFlowModel model ) {
		int edgeIndex = model.getGraph().numberOfEdges();
		final int oldEdgeIndex = edgeIndex;
		model.setNumberOfEdges( edgeIndex * 2 - model.getGraph().degree( model.getSupersink() ) );

		// don't use an iterator here, as it will result in concurrent modification
		for( int i = 0; i < oldEdgeIndex; ++i ) {
			Edge edge = model.getGraph().getEdge( i );
			if( !edge.isIncidentTo( model.getSupersink() ) ) {
				Edge newEdge = new Edge( edgeIndex++, edge.end(), edge.start() );
				mapping.setEdgeLevel( newEdge, mapping.getEdgeLevel( edge ).getInverse() );
				model.setEdgeCapacity( newEdge, model.getEdgeCapacity( edge ) );
				model.setTransitTime( newEdge, model.getTransitTime( edge ) );
				model.getGraph().setEdge( newEdge );
			}
		}
	}

	protected void multiplyWithUpAndDownSpeedFactors() {
		for( Edge edge : model.getGraph().edges() )
			if( !edge.isIncidentTo( model.getSupersink() ) )
				switch( mapping.getEdgeLevel( edge ) ) {
					case Higher:
						exactTransitTimes.divide( edge, mapping.getUpNodeSpeedFactor( edge.start() ) );
						break;
					case Lower:
						exactTransitTimes.divide( edge, mapping.getDownNodeSpeedFactor( edge.start() ) );
						break;
				}
	}
}
