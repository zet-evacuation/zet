/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import static org.zetool.common.util.Direction8.*;
import static org.zetool.common.util.Level.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.tu_berlin.math.coga.zet.ZETLocalization2;
import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import de.zet_evakuierung.model.BuildingPlan;
import de.zet_evakuierung.model.ConcreteAssignment;
import de.zet_evakuierung.model.Person;
import de.zet_evakuierung.model.PlanPoint;
import de.zet_evakuierung.model.Room;
import de.zet_evakuierung.network.model.NetworkFlowModel;
import ds.PropertyContainer;
import ds.graph.NodeRectangle;
import org.zetool.common.util.Direction8;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.DynamicNetwork;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GridGraphConverter extends BaseZToGraphConverter {

    /**
     * Finds rectangles in the rastered rooms to define nodes in the graph.
     */
    @Override
    protected void createNodes() {
        //protected static void createNodes( ZToGraphRasterContainer rasterContainer, NetworkFlowModel model, BuildingPlan plan ) {
        System.out.println("create Nodes");
        List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();

        // New graph
        DynamicNetwork graph = new DynamicNetwork();

        // speed mapping
        ZToGraphMapping mappingLocal = modelBuilder.getZToGraphMapping();

        // List of sources according to isSource flag of squares
        //LinkedList<Node> sources = new LinkedList<>();
        // super sink
        //Node supersink = new Node( 0 );
        Node supersink = modelBuilder.getSupersink();
        //graph.setNode( supersink );
        //model.setSupersink( supersink );

        mappingLocal.setNodeSpeedFactor(supersink, 1);
        mappingLocal.setNodeRectangle(supersink, new NodeRectangle(0, 0, 0, 0));
        mappingLocal.setFloorForNode(supersink, -1);

        // get attribute from property container
        PropertyContainer propertyContainer = PropertyContainer.getGlobal();
        boolean accurateDelayAreaCreation = propertyContainer.getAsBoolean("converter.AccurateDelayAreaCreation");
        boolean accurateAssignmentAreaCration = propertyContainer.getAsBoolean("converter.accurateAssignmentAreaCreation");
        if (debug) {
            if (accurateDelayAreaCreation) {
                System.out.println("Delay areas are taken into account.");
            } else {
                System.out.println("Delay areas are not taken into account.");
            }
            System.out.println();
        }

        //int nodeCount = 1;
        // iterate through all rooms and create a graph for each room
        for (ZToGraphRoomRaster room : rasteredRooms) {

            // unmark all squares because they are not yet processed.
            room.unmarkAllSquares();

            int roomOffsetX = room.getXOffset();
            int roomOffsetY = room.getYOffset();

            int numOfColumns = room.getColumnCount();
            int numOfRows = room.getRowCount();

            // DynamicNetwork graph = new DynamicNetwork();
            // iterate through all squares of the (rastered) room
            // and merge some of them to nodes
            for (int y = 0; y < numOfRows; y++) {
                for (int x = 0; x < numOfColumns; x++) {

                    // Current square;
                    // A new node will be created that contains at least the
                    // current square.
                    ZToGraphRasterSquare square = room.getSquare(x, y);

                    int nodeRectangleNW_x = roomOffsetX + x * room.getRaster();
                    int nodeRectangleNW_y = roomOffsetY + y * room.getRaster();
                    // Finding the upper Right square by looking for the maximal x and y.
                    int maxX = x;
                    int maxY = y;

                    if (square.isAccessible() && !square.isMarked()) {

                        //Node node = new Node( nodeCount );
                        Node node = modelBuilder.newNode();
                        modelBuilder.getZToGraphMapping().getNodeFloorMapping().set(node, getProblem().getFloorID(room.getFloor()));
                        //model.getZToGraphMapping().setIsEvacuationNode( node, square.isExit() );
                        //model.getZToGraphMapping().setIsSourceNode( node, square.isSource() );
                        modelBuilder.getZToGraphMapping().setDeletedSourceNode(node, false);
                        if (getProblem().getFloorID(room.getFloor()) == -1) {
                            System.out.println("Fehler: Floor beim Konvertieren nicht gefunden.");
                        }

                        boolean nodeIsSource = false;

                        // Initializing variables for speed factor calculation
                        //boolean test = square.isStair();
                        double sumOfSpeedFactors = square.getSpeedFactor();
                        double downSpeedFactor = square.getDownSpeedFactor();
                        double upSpeedFactor = square.getUpSpeedFactor();
                        int numOfSquares = 1;

                        graph.setNode(node);

                        // set the node of the current square to the new node
                        // and mark it as processed.
                        square.mark();
                        square.setNode(node);
                        // If the isSource flag of the square is set, 
                        // the node becomes a source.
                        if (square.isSource()) {
                            nodeIsSource = true;
                        }

                        mappingLocal.setNodeSpeedFactor(node, sumOfSpeedFactors / numOfSquares);
                        mappingLocal.setNodeUpSpeedFactor(node, upSpeedFactor);
                        mappingLocal.setNodeDownSpeedFactor(node, downSpeedFactor);
                        // calculate the lower Right corner of the node rectangle.
                        int nodeRectangleSE_x = roomOffsetX + room.getRaster() * (maxX + 1);
                        int nodeRectangleSE_y = roomOffsetY + room.getRaster() * (maxY + 1);
                        // save the node rectangle in the mapping
                        mappingLocal.setNodeRectangle(node, new NodeRectangle(nodeRectangleNW_x, -nodeRectangleNW_y, nodeRectangleSE_x, -nodeRectangleSE_y));
                        // save the number of the floor the node belongs to
                        mappingLocal.setFloorForNode(node, raster.getFloors().indexOf(room.getFloor()));
                        if (nodeIsSource) {
                            modelBuilder.addSource(node);
                        }
                    }
                }
            }
            if (progress) {
                System.out.println("Progress: A rastered room was processed and subdivided into nodes.");
            }
            if (debug) {
                System.out.println("A rastered room was processed and got subdevided like this:");
                System.out.print(room);
            }
        }
        // Set graph to model
        //model.setNetwork( graph );
        //model.setSources( sources );
        System.out.println("create Nodes FERTIG");
    }

    @Override
    protected void createEdgesAndCapacities() {
        //protected static void calculateEdgesAndCapacities( ZToGraphRasterContainer raster, NetworkFlowModel model ) {
        System.out.println("calculate Edges & Capacities");
        //ZToGraphMapping mapping = model.getZToGraphMapping();

        List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();
        int nextEdge = 0;

        //DynamicNetwork graph = model.getDynamicNetwork();
        //Two mappings to store capacities
        //IdentifiableIntegerMapping<Node> nodesCap = new IdentifiableIntegerMapping<>( graph.nodeCount() );
        //model.setNodeCapacities( nodesCap );
        //IdentifiableIntegerMapping<Edge> edgesCap = new IdentifiableIntegerMapping<>( graph.edgeCount() * graph.edgeCount() );
        //model.setEdgeCapacities( edgesCap );
        // set node capacity of super sink to max value
        //nodesCap.set( model.getSupersink(), Integer.MAX_VALUE );
        modelBuilder.setNodeCapacity(modelBuilder.getSupersink(), Integer.MAX_VALUE);

        for (ZToGraphRoomRaster room : rasteredRooms) {

            int colCount = room.getColumnCount();
            int rowCount = room.getRowCount();

            Node lastNode = null;

            //iterate over each square VERTICALLY
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < colCount; col++) {
                    ZToGraphRasterSquare square = room.getSquare(col, row);
                    Node node = square.getNode();
                    //increase node capacity
                    if (node != null) {
                        modelBuilder.increaseNodeCapacity(node, 1 * FACTOR);
                    }

                    boolean nodesConnectable = (node != null) && (lastNode != null) && !lastNode.equals(node);
                    boolean connectionPassable = (col != 0) && (!square.isBlocked(Left));

                    if (nodesConnectable && connectionPassable) {
                        Edge edge = modelBuilder.getEdge(lastNode, node);
                        if (edge == null) {
                            edge = modelBuilder.newEdge(lastNode, node);
                            //graph.addEdge( edge );
                            //edgesCap.set( edge, 0 );
                            ZToGraphRasterSquare lastSquare = null;
                            if (col > 0) {
                                lastSquare = room.getSquare(col - 1, row);
                            } else {
                                throw new AssertionError("Col should not be zero at this point.");
                            }
                            mapping.setEdgeLevel(edge, lastSquare.getLevel(Direction8.getDirection(1, 0)));
                        }
                        //edgesCap.increase( edge, 1 * FACTOR );
                        modelBuilder.increaseEdgeCapacity(edge, 1 * FACTOR);
                    }
                    lastNode = node;
                }// end of the outer for each loop
                lastNode = null;
            }//end for each room

            //Iterate now VERTICALLY to add the capacities of the
            lastNode = null;
            for (int col = 0; col < colCount; col++) {
                for (int row = 0; row < rowCount; row++) {
                    ZToGraphRasterSquare square = room.getSquare(col, row);
                    Node node = square.getNode();
                    //No need to increase the capacity since the square has already been taken in consideration

                    boolean nodesConnectable = (node != null) && (lastNode != null) && !lastNode.equals(node);
                    boolean connectionPassable = (row != 0) && (!square.isBlocked(Top));

                    if (nodesConnectable && connectionPassable) {
                        Edge edge = modelBuilder.getEdge(lastNode, node);
                        if (edge == null) {
                            edge = modelBuilder.newEdge(lastNode, node);
                            //graph.addEdge( edge );
                            //edgesCap.set( edge, 0 );
                            ZToGraphRasterSquare lastSquare = null;
                            if (row > 0) {
                                lastSquare = room.getSquare(col, row - 1);
                            } else {
                                throw new AssertionError(ZETLocalization2.loc.getString("converter.RowIsZeroException"));
                            }
                            mapping.setEdgeLevel(edge, lastSquare.getLevel(Direction8.getDirection(0, 1)));
                        }
                        //edgesCap.increase( edge, 1 * FACTOR );
                        modelBuilder.increaseEdgeCapacity(edge, 1 * FACTOR);
                    }
                    lastNode = node;
                }// end of the inner for each loop
                lastNode = null;
            }// end of the outer for each loop
        }//end for each room
        //model.setNodeCapacities( nodesCap );
        //model.setEdgeCapacities( edgesCap );
        System.out.println("calculate Edges & Capacities FERTIG");
    }//end of function

    /**
     * This method calculates the transit times for the converted graphs. The ZToGraphRasterContainer raster supplies
     * one with all necessary rastered rooms, that can be mapped to the proper graphs through the HashMap graphs.
     * Afterwards the calculated transit times are set into the network flow model. The transit times are weighted by
     * the rooms speed factors and rounded to the multiple of the graph precision value.
     */
    @Override
    protected void computeTransitTimes() {
        //protected static void computeTransitTimes( ZToGraphRasterContainer raster, NetworkFlowModel model, Hashtable<Edge, ArrayList<ZToGraphRasterSquare>> doorEdgeToSquare ) {
        HashMap<Edge, ArrayList<ZToGraphRasterSquare>> doorEdgeToSquare = connectRooms(raster, modelBuilder);

        long startTT = System.currentTimeMillis();
        System.out.println("BEGINNE TRANSIT-TIMES");
        //exactTransitTimes = new IdentifiableDoubleMapping<>( 1 );

        List<ZToGraphRoomRaster> roomRasterList = raster.getAllRasteredRooms();

        //Graph graph = model.getGraph();
        //IdentifiableCollection<Node> nodes = graph.nodes();
        // calculate INTRA-Room-Edge-Transit-Times
        long intraStart = System.currentTimeMillis();
        System.out.println("calculate INTRA-Room-Edge-Transit-Times");

        // do for all rooms of the roomRasterList
        for (ZToGraphRoomRaster room : roomRasterList) {

            List<ZToGraphRasterSquare> roomSquareList = room.getAccessibleSquares();
            HashSet<Node> nodeListOfRoom = new HashSet<>();
            for (ZToGraphRasterSquare square : roomSquareList) {
                nodeListOfRoom.add(square.getNode());
            }

            // calculate the Node -> ZToGraphRasterSquare mapping
            HashMap<Node, LinkedList<ZToGraphRasterSquare>> nodeToSquare = new HashMap<>();
            for (Node node : nodeListOfRoom) {//nodes){
                LinkedList<ZToGraphRasterSquare> nodeSquareList = new LinkedList<>();
                for (ZToGraphRasterSquare square : roomSquareList) {
                    if (square.getNode() != null) {
                        if (square.getNode().id() == node.id()) {
                            nodeSquareList.add(square);
                        }
                    }
                }
                nodeToSquare.put(node, nodeSquareList);
            }

            Node supersink = modelBuilder.getSupersink();

            for (Node start : nodeListOfRoom)//nodes){
            {
                for (Node end : nodeListOfRoom) {//nodes){
                    // do only, if there is an edge between start and end & if start does not equal end
                    Edge edge = modelBuilder.getEdge(start, end);
                    if (edge != null && edge.id() == 400) {
                        System.out.println("debug");
                    }
                    if (edge != null && start != end) {
                        if (end.equals(supersink)) {
                            modelBuilder.setExactTransitTime(edge, 0);
                            continue;
                        }
                        // write the new transitTime into the IIMapping
                        modelBuilder.setExactTransitTime(edge, 1);
                    }
                } // END of for(start)
            }
        } // END of for(roomRaster)
        // END calculate INTRA-Room-Edge-Transit-Times
        System.out.println("calculate INTRA-Room-Edge-Transit-Times FERTIG " + (System.currentTimeMillis() - intraStart));

        // calculate INTER-Room-Edge-Transit-Times
        long interStart = System.currentTimeMillis();
        System.out.println("calculate INTER-Room-Edge-Transit-Times");
        for (ZToGraphRoomRaster startRoom : roomRasterList) {
            for (ZToGraphRoomRaster endRoom : roomRasterList) {

                // CALCULATE roomNodeMap : ZToGraphRoomRaster -> LinkedList<Node>
                List<ZToGraphRasterSquare> startRoomSquareList = startRoom.getAccessibleSquares();
                List<ZToGraphRasterSquare> endRoomSquareList = endRoom.getAccessibleSquares();
                HashSet<Node> nodeListOfStartRoom = new HashSet<>();
                HashSet<Node> nodeListOfEndRoom = new HashSet<>();
                for (ZToGraphRasterSquare square : startRoomSquareList) {
                    nodeListOfStartRoom.add(square.getNode());
                }
                for (ZToGraphRasterSquare square : endRoomSquareList) {
                    nodeListOfEndRoom.add(square.getNode());
                }

                for (Node nodeA : nodeListOfStartRoom) {
                    for (Node nodeB : nodeListOfEndRoom) {
                        Edge edge = modelBuilder.getEdge(nodeA, nodeB);
                        if (edge != null && modelBuilder.contains(edge) && doorEdgeToSquare.get(edge) != null && !doorEdgeToSquare.get(edge).isEmpty()) {
                            modelBuilder.setExactTransitTime(edge, 1);
                        }
                    }
                }
            }
        }
        // END calculate INTER-Room-Edge-Transit-Times
        System.out.println("calculate INTER-Room-Edge-Transit-Times FERTIG " + (System.currentTimeMillis() - interStart));

        // set the calculated transitTime-IIMapping as the transitTimes of the NFM
        //model.setTransitTimes( exactTransitTimes );
        System.out.println("TRANSIT-TIMES-FERTIG " + (System.currentTimeMillis() - startTT));

    }

    final static boolean debug = false;
    final static boolean progress = false;
    final static int FACTOR = 1;

    /**
     * Converts a concrete assignment into an assignment for graphs. The concrete assignments provides a list of all
     * persons, their associated rooms and positions on the plan. These coordinates are translated into local
     * coordinates of the room they inhabit. The associated nodes of the individual room raster squares are then
     * provided with the proper number of persons and the node assignment is afterwards set to the network flow model.
     * Additionally the super sink node is given a negative assignment in the amount of the number of people in the
     * building.
     *
     * @param assignment The concrete assignment to be converted
     * @param model The network flow model to which the converted assignment has to be written
     */
    public static NetworkFlowModel convertConcreteAssignment(ConcreteAssignment assignment, NetworkFlowModel model) {
        NetworkFlowModel.AssignmentBuilder modelBuilder = new NetworkFlowModel.AssignmentBuilder(model);
        ZToGraphMapping mapping = model.getZToGraphMapping();
        ZToGraphRasterContainer raster = mapping.getRaster();

        // the new converted node assignment
        IdentifiableIntegerMapping<Node> nodeAssignment = new IdentifiableIntegerMapping<>(1);
        List<Person> persons = assignment.getPersons();

        // setting the people requirement (negative assignment) to the number of persons in the building
        Node superSink = model.getSupersink();
        nodeAssignment.set(superSink, -persons.size());

        // for every person do
        for (int i = 0; i < persons.size(); i++) {
            // get the room that is inhabited by the current person
            Room room = persons.get(i).getRoom();
            ZToGraphRoomRaster roomRaster = raster.getRasteredRoom(room);

            // calculate the coordinates of the person inside of it's room
            PlanPoint pos = persons.get(i).getPosition();
            int XPos = pos.getXInt();
            int YPos = pos.getYInt();
            // get the square the person is located
            ZToGraphRasterSquare square = roomRaster.getSquareWithGlobalCoordinates(XPos, YPos);
//            ZToGraphRasterSquare square = roomRaster.getSquare((int)Math.floor(XPos/400), (int)Math.floor(YPos/400));

            // get the square's associated node
            Node node = square.getNode();

            // increase the nodes assignment if already defined or set it's assignment to 1
            if (nodeAssignment.isDefinedFor(node)) {
                nodeAssignment.increase(node, 1);
            } else {
                nodeAssignment.set(node, 1);
            }
        }

        // set node assignment to 0 for every node the assignment has not already defined for
        //IdentifiableCollection<Node> nodes = model.getGraph().nodes();
        // TODO: node assignment in the model is not set for the other nodes
        for (int i = 0; i < model.numberOfNodes(); i++) // TODO remove this
        {
            if (!nodeAssignment.isDefinedFor(model.getNode(i))) {
                modelBuilder.setNodeAssignment(model.getNode(i), 0);
            }
        }

        // set the network flow model's assignment to the calculated node assignment
        //model.setCurrentAssignment( nodeAssignment );

        if (progress) {
            System.out.println("Progress: A concrete assignment has been converted into supplies and demands for the graph.");
        }
        return modelBuilder.build();
    }

    protected static ZToGraphRasterContainer createRaster(BuildingPlan plan) {
        ZToGraphRasterContainer container = RasterContainerCreator.getInstance().ZToGraphRasterContainer(plan);
        return container;
    }

    protected static HashMap<Edge, ArrayList<ZToGraphRasterSquare>> connectRooms(ZToGraphRasterContainer raster, NetworkFlowModel.BasicBuilder modelBuilder) {
        System.out.println("connect Rooms");
        ZToGraphMapping mapping = modelBuilder.getZToGraphMapping();

        HashMap<Edge, ArrayList<ZToGraphRasterSquare>> table = new HashMap<>();

        //Two mappings to store capacities just temporally.
        Collection<ZToGraphRasteredDoor> doors = raster.getDoors();
        //DynamicNetwork graph = model.getDynamicNetwork();
        //IdentifiableIntegerMapping<Edge> edgesCap = model.getEdgeCapacities();
        //if( edgesCap == null ) {
        //	edgesCap = new IdentifiableIntegerMapping<>( graph.edgeCount() );
        //	model.setEdgeCapacities( edgesCap );
        //}

        //int nextEdge = graph.edgeCount();
        for (ZToGraphRasteredDoor door : doors) {
            Node firstNode = door.getFirstDoorPart().getNode();
            Node secondNode = door.getSecondDoorPart().getNode();

            if (firstNode.id() > secondNode.id()) // only craeate one of the edges as they are doubled afterwards
            {
                continue;
            }

            Edge edge = modelBuilder.getEdge(firstNode, secondNode);
            if (edge == null) {
                edge = modelBuilder.newEdge(firstNode, secondNode);
                //graph.addEdge( edge );
                //edgesCap.setDomainSize( edgesCap.getDomainSize() + 1 );
                //edgesCap.set( edge, 0 );
                mapping.setEdgeLevel(edge, Equal);

            }
            //edgesCap.increase( edge, 1 * FACTOR );
            modelBuilder.increaseEdgeCapacity(edge, 1 * FACTOR);
            //store squares in the squares list of the door-edge
            ArrayList<ZToGraphRasterSquare> list = table.get(edge);
            if (list == null) {
                list = new ArrayList<>();
                table.put(edge, list);
            }

            //retrieve the squares to store them in the list if they are not already in it
            ZToGraphRasterSquare square = door.getFirstDoorPart();
            if (!list.contains(square)) {
                list.add(square);
            }
            square = door.getSecondDoorPart();
            if (!list.contains(square)) {
                list.add(square);
            }

        }//end for each door loop

        //Connect the super source withh all other sources
        Node supersink = modelBuilder.getSupersink();

        if (supersink == null) {
            return table;
        }

        List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();
        for (ZToGraphRoomRaster room : rasteredRooms) {

            int colCount = room.getColumnCount();
            int rowCount = room.getRowCount();

            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < colCount; col++) {
                    ZToGraphRasterSquare square = room.getSquare(col, row);

                    if (square.isSave()) {
                        Node node = square.getNode();
                        Edge edge = modelBuilder.getEdge(node, supersink);
                        if (edge == null) {
                            edge = modelBuilder.newEdge(node, supersink);
                            //graph.addEdge( edge );
                            mapping.setEdgeLevel(edge, Equal);
                        }
                        //edgesCap.set( edge, Integer.MAX_VALUE );
                        modelBuilder.setEdgeCapacity(edge, Integer.MAX_VALUE);
                    }// end if safe
                }//end outer loop
            }
        }
        System.out.println("connect Rooms FERTIG");
        return table;
    }//end of function
}
