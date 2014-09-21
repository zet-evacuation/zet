/**
 * GraphAssignmentConverter.java
 * Created: Jul 29, 2010,3:31:36 PM
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.coga.zet.model.ConcreteAssignment;
import de.tu_berlin.coga.zet.model.Person;
import de.tu_berlin.coga.zet.model.PlanPoint;
import de.tu_berlin.coga.zet.model.Room;
import java.util.HashMap;
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

		model.resetAssignment();
		
		// the new converted node assignment
		List<Person> persons = assignment.getPersons();

		// setting the people requirement (negative assignment) to the number of persons in the building
		//Node superSink = model.getSupersink();
		model.setNodeAssignment( model.getSupersink(), -persons.size() );
		//nodeAssignment.set( superSink, -persons.size() );

    HashMap<Room,Double> roomMaxTime = new HashMap<>();
    HashMap<Node,Integer> nodeCount = new HashMap<>();
    HashMap<Node,Room> nodeRoom = new HashMap<>();
    
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
      //System.out.println("Square in Assignment: " + square);
      //ZToGraphRasterSquare square = roomRaster.getSquare((int)Math.floor(XPos/400), (int)Math.floor(YPos/400));
			// get the square's associated node
			Node node = square.getNode();

			// increase the nodes assignment if already defined or set it's assignment to 1
      //normal:
			//model.increaseNodeAssignment( node ); 
      //max-in-room-assignment
      final double maxTimeForRoom = roomMaxTime.getOrDefault( room, 0d );
      roomMaxTime.put( room, Math.max( maxTimeForRoom, persons.get( i ).getReaction() ) );
      final int count = nodeCount.getOrDefault( node, 0 );
      nodeCount.put( node, count+1 );
      nodeRoom.put( node, room );
		}
    
    for( Node n : nodeRoom.keySet() ) {
      int count = nodeCount.get( n );
      double delay = roomMaxTime.get( nodeRoom.get( n ) );
      // Delay in sekunden
      double factor = 1/0.26425707443;
      
      for( int i = 0; i < count; ++i ) {
        model.increaseNodeAssignment( n, delay*factor );
        System.out.println( "Setting delay: " + (delay*factor) + " for delay " + delay + " in room " + nodeRoom.get( n ) );
      }
    }

		// set node assignment to 0 for every node the assignment has not already defined for
//		IdentifiableCollection<Node> nodes = model.getGraph().nodes();
//		for( int i = 0; i < nodes.size(); i++ )
//			if( !nodeAssignment.isDefinedFor( nodes.get( i ) ) )
//				nodeAssignment.set( nodes.get( i ), 0 );

		// set the network flow model's assignment to the calculated node assignment
//		model.setCurrentAssignment( nodeAssignment );

		model.checkSupplies();
		
//		if( progress )
//			System.out.println( ": A concrete assignment has been converted into supplies and demands for the graph." );

	}

}
