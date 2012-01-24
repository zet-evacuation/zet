/**
 * GraphAssignmentConverter.java
 * Created: Jul 29, 2010,3:31:36 PM
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.util.GraphInstanceChecker;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.IdentifiableCollection;
import ds.mapping.IdentifiableIntegerMapping;
import ds.graph.network.AbstractNetwork;
import ds.graph.Node;
import ds.z.ConcreteAssignment;
import ds.z.Person;
import ds.z.PlanPoint;
import ds.z.Room;
import java.util.List;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GraphAssignmentConverter extends Algorithm<ConcreteAssignment, NetworkFlowModel> {

	private NetworkFlowModel model;

	public GraphAssignmentConverter( NetworkFlowModel model ) {
		this.model = model;
	}

	@Override
	protected NetworkFlowModel runAlgorithm( ConcreteAssignment problem ) {
		convertConcreteAssignment( problem );
		return model;
	}

	/**
	 * Converts a concrete assignment into an assignment for graphs.
	 * The concrete assignments provides a list of all persons, their associated rooms and positions on the plan.
	 * These coordinates are translated into local coordinates of the room they inhabit.
	 * The associated nodes of the individual room raster squares are then provided with the proper number of persons
	 * and the node assignment is afterwards set to the network flow model.
	 * Additionally the super sink node is given a negative assignment in the amount of the number of people in the building.
	 * @param assignment The concrete assignment to be converted
	 * @param model The network flow model to which the converted assignment has to be written
	 */
	private void convertConcreteAssignment( ConcreteAssignment assignment ) {
		ZToGraphMapping mapping = model.getZToGraphMapping();
		ZToGraphRasterContainer raster = mapping.getRaster();

		// the new converted node assignment
		IdentifiableIntegerMapping<Node> nodeAssignment = new IdentifiableIntegerMapping<>( 1 );
		List<Person> persons = assignment.getPersons();

		// setting the people requirement (negative assignment) to the number of persons in the building
		Node superSink = model.getSupersink();
		nodeAssignment.set( superSink, -persons.size() );

		// for every person do
		for( int i = 0; i < persons.size(); i++ ) {
			// get the room that is inhabited by the current person
			Room room = persons.get( i ).getRoom();
			ZToGraphRoomRaster roomRaster = raster.getRasteredRoom( room );

			// calculate the coordinates of the person inside of it's room
			PlanPoint pos = persons.get( i ).getPosition();
			int XPos = pos.getXInt();
			int YPos = pos.getYInt();
			// get the square the person is located
			ZToGraphRasterSquare square = roomRaster.getSquareWithGlobalCoordinates( XPos, YPos );
//            ZToGraphRasterSquare square = roomRaster.getSquare((int)Math.floor(XPos/400), (int)Math.floor(YPos/400));

			// get the square's associated node
			Node node = square.getNode();

			// increase the nodes assignment if already defined or set it's assignment to 1
			if( nodeAssignment.isDefinedFor( node ) )
				nodeAssignment.increase( node, 1 );
			else
				nodeAssignment.set( node, 1 );
		}

		// set node assignment to 0 for every node the assignment has not already defined for
		IdentifiableCollection<Node> nodes = model.getGraph().nodes();
		for( int i = 0; i < nodes.size(); i++ )
			if( !nodeAssignment.isDefinedFor( nodes.get( i ) ) )
				nodeAssignment.set( nodes.get( i ), 0 );

		// set the network flow model's assignment to the calculated node assignment
		model.setCurrentAssignment( nodeAssignment );

		checkSupplies( model );

//		if( progress )
//			System.out.println( ": A concrete assignment has been converted into supplies and demands for the graph." );

	}

	/**
	 * Deletes sources that cannot reach a sink. These nodes are marked as deleted sources.
	 * @param model the {@code NetworkFlowModel} object.
	 */
	private void checkSupplies( NetworkFlowModel model ) {
		AbstractNetwork network = model.getNetwork();
		IdentifiableIntegerMapping<Node> supplies = model.getCurrentAssignment();

		GraphInstanceChecker checker = new GraphInstanceChecker( network, supplies );
		checker.supplyChecker();

		if( checker.hasRun() ) {
			model.setCurrentAssignment( checker.getNewSupplies() );
			model.setSources( checker.getNewSources() );
			ZToGraphMapping mapping = model.getZToGraphMapping();
			for( Node oldSource : checker.getDeletedSources() ) {
				mapping.setIsSourceNode( oldSource, false );
				mapping.setIsDeletedSourceNode( oldSource, true );
			}
		} else
			throw new AssertionError( DefaultLoc.getSingleton().getString( "converter.NoCheckException" ) );
	}
}
