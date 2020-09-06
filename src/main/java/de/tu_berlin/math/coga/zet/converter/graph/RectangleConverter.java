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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import de.zet_evakuierung.model.PlanPoint;
import de.zet_evakuierung.model.Room;
import de.zet_evakuierung.model.StairArea;
import ds.PropertyContainer;
import ds.graph.NodeRectangle;
import org.zetool.common.util.Direction8;
import org.zetool.common.util.Formatter;
import org.zetool.common.util.Level;
import org.zetool.common.util.units.TimeUnits;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;

/**
 *
 */
public class RectangleConverter extends BaseZToGraphConverter {

    final static int FACTOR = 1;

    static {
        if (!PropertyContainer.getGlobal().isDefined("converter.AccurateDelayAreaCreation")) {
            PropertyContainer.getGlobal().define("converter.AccurateDelayAreaCreation", Boolean.class, true);
        }
        if (!PropertyContainer.getGlobal().isDefined("converter.accurateAssignmentAreaCreation")) {
            PropertyContainer.getGlobal().define("converter.accurateAssignmentAreaCreation", Boolean.class, true);
        }
        if (!PropertyContainer.getGlobal().isDefined("converter.Imbalance")) {
            PropertyContainer.getGlobal().define("converter.Imbalance", Integer.class, 1);
        }
        if (!PropertyContainer.getGlobal().isDefined("converter.GraphPrecision")) {
            PropertyContainer.getGlobal().define("converter.GraphPrecision", Integer.class, 1);
        }
    }

    @Override
    protected void createNodes() {
        Logger.getGlobal().fine("Create Nodes... ");
        List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();

        // New graph
        //DynamicNetwork graph = new DynamicNetwork();
        // List of sources according to isSource flag of squares
        //LinkedList<Node> sources = new LinkedList<>();
        // super sink
        //Node supersink = new Node( 0 );
        //graph.setNode( supersink );
        //model.setSupersink( supersink );
        Node supersink = modelBuilder.getSupersink();

        mapping.setNodeSpeedFactor(supersink, 1);
        mapping.setNodeRectangle(supersink, new NodeRectangle(0, 0, 0, 0));
        mapping.setFloorForNode(supersink, -1);
        mapping.setRoomForNode(supersink, null);

        // get attribute from property container
        PropertyContainer propertyContainer = PropertyContainer.getGlobal();
        boolean accurateDelayAreaCreation = propertyContainer.getAsBoolean("converter.AccurateDelayAreaCreation");
        boolean accurateAssignmentAreaCration = propertyContainer.getAsBoolean("converter.accurateAssignmentAreaCreation");
        if (accurateDelayAreaCreation) {
            Logger.getGlobal().finest("\nDelay areas are taken into account.");
        } else {
            Logger.getGlobal().finest("\nDelay areas are not taken into account.");
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
                    // Finding the upper Right square by looking for the maximal x and y ears.
                    int maxX = x;
                    int maxY = y;

                    if (square.isAccessible() && !square.isMarked()) {

                        //Node node = new Node( nodeCount );
                        Node node = modelBuilder.newNode();
                        modelBuilder.getZToGraphMapping().getNodeFloorMapping().set(node, getProblem().getFloorID(room.getFloor()));
                        //model.getZToGraphMapping().setIsEvacuationNode( node, square.isExit() );
                        if (square.isExit()) {
                            modelBuilder.addSink(node);
                            modelBuilder.getZToGraphMapping().setNameOfExit(node, square.getName());
                        }
                        //model.getZToGraphMapping().setIsSourceNode( node, square.isSource() );
                        modelBuilder.getZToGraphMapping().setDeletedSourceNode(node, false);
                        if (getProblem().getFloorID(room.getFloor()) == -1) {
                            Logger.getGlobal().warning("\nFehler: Floor beim Konvertieren nicht gefunden.");
                        }

                        boolean nodeIsSource = false;

                        // Initializing variables for speed factor calculation
                        //boolean test = square.isStair();
                        double sumOfSpeedFactors = square.getSpeedFactor();
                        double downSpeedFactor = square.getDownSpeedFactor();
                        double upSpeedFactor = square.getUpSpeedFactor();
                        int numOfSquares = 1;

                        //graph.setNode( node );
                        //nodeCount++;
                        // set the node of the current square to the new node
                        // and mark it as processed.
                        square.mark();
                        square.setNode(node);
                        // If the isSource flag of the square is set,
                        // the node becomes a source.
                        if (square.isSource()) {
                            nodeIsSource = true;
                        }

                        // First part: Find the highest number n
                        // such that a the n x n-square having the current
                        // raster square as upper Left corner
                        // fits into the building plan without colliding
                        // with isInaccessible areas or similar.
                        // (n is stored in the variable extent)
                        int extent = 0;
                        boolean downblocked = false, rightblocked = false, blocked = false;

                        while (extent < numOfColumns && extent < numOfRows && !downblocked && !rightblocked && !blocked) {

                            // check whether a new line can be added at the
                            // Right of the square of raster squares
                            for (int offset = 0; offset <= extent; offset++) {
                                rightblocked |= isRightSquareBlocked(room, x + extent,
                                        y + offset, accurateDelayAreaCreation, accurateAssignmentAreaCration);
                            }

                            // check whether a new line can be added under
                            // the square of raster squares
                            for (int offset = 0; offset <= extent; offset++) {
                                downblocked |= isDownSquareBlocked(room, x + offset,
                                        y + extent, accurateDelayAreaCreation, accurateAssignmentAreaCration);
                            }

                            blocked = isDownSquareBlocked(room, x + extent + 1,
                                    y + extent, accurateDelayAreaCreation, accurateAssignmentAreaCration);

                            // extent the square of raster squares by one line
                            // to the Right and under the square,
                            // if both directions are not blocked
                            if (!downblocked && !rightblocked && !blocked) {
                                for (int offset = 0; offset <= extent + 1; offset++) {
                                    ZToGraphRasterSquare rsquare = room.getSquare(x + extent + 1, y + offset);
                                    maxX = Math.max(maxX, x + extent + 1);
                                    maxY = Math.max(maxY, y + offset);
                                    rsquare.setNode(node);
                                    // If the isSource flag of the square is set,
                                    // the node becomes a source.
                                    if (rsquare.isSource()) {
                                        nodeIsSource = true;
                                    }
                                    rsquare.mark();
                                    sumOfSpeedFactors += rsquare.getSpeedFactor();
                                    numOfSquares++;
                                }
                                // hier muss man nur eins weniger setzen,
                                // da das gemeinsame schon in der 1. schleife gesetzt wurde
                                for (int offset = 0; offset <= extent; offset++) {
                                    ZToGraphRasterSquare dsquare = room.getSquare(x + offset, y + extent + 1);
                                    maxX = Math.max(maxX, x + offset);
                                    maxY = Math.max(maxY, y + extent + 1);
                                    dsquare.setNode(node);
                                    if (dsquare.isSource()) {
                                        nodeIsSource = true;
                                    }
                                    dsquare.mark();
                                    sumOfSpeedFactors += dsquare.getSpeedFactor();
                                    numOfSquares++;
                                }
                                extent++;
                            }
                        }

                        // Second part: Extent the square to a rectangle of
                        // raster squares, such that it has at most
                        // imbalance many lines more in one direction
                        int imbalance = propertyContainer.getAsInt("converter.Imbalance");
                        int added = 0;

                        // extent down
                        while (!downblocked && added < imbalance) {
                            // Check the line under the rectangle of raster
                            // squares
                            for (int offset = 0; offset <= extent; offset++) {
                                downblocked |= isDownSquareBlocked(room, x + offset,
                                        y + extent + added, accurateDelayAreaCreation, accurateAssignmentAreaCration);    // sondern der obere übergeben werden muss
                            }
                            // set the line under the rectangle if is free
                            if (!downblocked) {
                                for (int offset = 0; offset <= extent; offset++) {
                                    ZToGraphRasterSquare dsquare = room.getSquare(x + offset, y + extent + added + 1);
                                    maxX = Math.max(maxX, x + offset);
                                    maxY = Math.max(maxY, y + extent + added + 1);
                                    dsquare.mark();
                                    dsquare.setNode(node);
                                    if (dsquare.isSource()) {
                                        nodeIsSource = true;
                                    }
                                    sumOfSpeedFactors += dsquare.getSpeedFactor();
                                    numOfSquares++;
                                    rightblocked = true;
                                    // damit das nächste while nicht auch
                                    // noch aufgerufen wird
                                }
                                added++;
                            }
                        }

                        // extent to the Right
                        while (!rightblocked && added < imbalance) {
                            // Check the line Right of the rectangle of raster squares
                            for (int offset = 0; offset <= extent; offset++) {
                                rightblocked |= isRightSquareBlocked(room, x + extent + added,
                                        y + offset, accurateDelayAreaCreation, accurateAssignmentAreaCration);
                            }

                            // set the line Right of the rectangle if it is free
                            if (!rightblocked) {
                                for (int offset = 0; offset <= extent; offset++) {
                                    ZToGraphRasterSquare rsquare = room.getSquare(x + extent + added + 1, y + offset);
                                    maxX = Math.max(maxX, x + extent + added + 1);
                                    maxY = Math.max(maxY, y + offset);
                                    rsquare.mark();
                                    rsquare.setNode(node);
                                    if (rsquare.isSource()) {
                                        nodeIsSource = true;
                                    }
                                    sumOfSpeedFactors += rsquare.getSpeedFactor();
                                    numOfSquares++;
                                    downblocked = true; // nur aus Prinzip
                                }
                                added++;
                            }
                        }
                        mapping.setNodeSpeedFactor(node, sumOfSpeedFactors / numOfSquares);
                        mapping.setNodeUpSpeedFactor(node, upSpeedFactor);
                        mapping.setNodeDownSpeedFactor(node, downSpeedFactor);
                        // calculate the lower Right corner of the node rectangle.
                        int nodeRectangleSE_x = roomOffsetX + room.getRaster() * (maxX + 1);
                        int nodeRectangleSE_y = roomOffsetY + room.getRaster() * (maxY + 1);
                        // save the node rectangle in the mapping
                        mapping.setNodeRectangle(node, new NodeRectangle(nodeRectangleNW_x, -nodeRectangleNW_y, nodeRectangleSE_x, -nodeRectangleSE_y));
                        // save the number of the floor the node belongs to
                        mapping.setFloorForNode(node, raster.getFloors().indexOf(room.getFloor()));
                        mapping.setRoomForNode(node, room.getRoom());

                        //System.out.print( "Node "+ node.id() );
                        int nodex = (nodeRectangleNW_x + nodeRectangleSE_x) / 2;
                        int nodey = (nodeRectangleNW_y + nodeRectangleSE_y) / 2;
                        //System.out.println( " at (" + nodex + "," + nodey + ") on floor " + raster.getFloors().indexOf( room.getFloor() ) );
                        if (nodeIsSource) //sources.add( node );
                        {
                            modelBuilder.addSource(node);
                        }
                    }
                }
            }
            Logger.getGlobal().fine(": A rastered room was processed and subdivided into nodes.");
            Logger.getGlobal().finest("A rastered room was processed and got subdevided like this:");
            Logger.getGlobal().finest(room.toString());
            //Logger.getGlobal().finest( "A rastered room was processed and got subdevided, nodecount is now " + nodeCount + "." );
        }
        // Set graph to model
        //model.setNetwork( graph );
        //model.setSources( sources );
        Logger.getGlobal().fine(" fertig");
    }

    @Override
    protected void createEdgesAndCapacities() {
        Logger.getGlobal().fine("Set up edges and compute capacities... ");
        ZToGraphMapping mappingLocal = modelBuilder.getZToGraphMapping();

        List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();

        // set node capacity of super sink to max value
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
                    if (node != null) //nodesCap.increase( node, 1 * FACTOR );
                    {
                        modelBuilder.increaseNodeCapacity(node, 1 * FACTOR);
                    }

                    boolean nodesConnectable = (node != null) && (lastNode != null) && !lastNode.equals(node);
                    boolean connectionPassable = (col != 0) && (!square.isBlocked(Direction8.Left));

                    if (nodesConnectable && connectionPassable) {
                        Edge edge = modelBuilder.getEdge(lastNode, node);

                        if (edge == null) {
                            edge = modelBuilder.newEdge(lastNode, node);
                            defineEdgeLevel(node, edge, room, mappingLocal, row, col);
//              
//                            ZToGraphRasterSquare lastSquare = null;
//              if( col > 0 ) {
//                lastSquare = room.getSquare( col - 1, row );
//              } else {
//                throw new AssertionError( "Col should not be zero at this point." );
//              }
//              mappingLocal.setEdgeLevel( edge, lastSquare.getLevel( Direction8.getDirection( 1, 0 ) ) );
                        }
                        modelBuilder.increaseEdgeCapacity(edge, 1 * FACTOR);
                        //edgesCap.increase( edge, 1 * FACTOR );
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
                    boolean connectionPassable = (row != 0) && (!square.isBlocked(Direction8.Top));

                    if (nodesConnectable && connectionPassable) {
                        Edge edge = modelBuilder.getEdge(lastNode, node);
                        //Edge edge = graph.getEdge( lastNode, node );
                        if (edge == null) {
                            //edge = new Edge( nextEdge++, lastNode, node );
                            edge = modelBuilder.newEdge(lastNode, node);
                            //graph.addEdge( edge );
                            //sedgesCap.set( edge, 0 );

                            defineEdgeLevel(node, edge, room, mappingLocal, row, col);

                        }
                        //edgesCap.increase( edge, 1 * FACTOR );
                        modelBuilder.increaseEdgeCapacity(edge, 1 * FACTOR);
                    }
                    lastNode = node;
                }// end of the inner for each loop
                lastNode = null;
            }// end of the outer for each loop
        }//end for each room
        Logger.getGlobal().fine("done.");
    }

    @Override
    protected void computeTransitTimes() {
        HashMap<Edge, ArrayList<ZToGraphRasterSquare>> doorEdgeToSquare = connectRooms();
        final long startTT = System.currentTimeMillis();
        log.log(java.util.logging.Level.FINE, "Computing transit times...");

        List<ZToGraphRoomRaster> roomRasterList = raster.getAllRasteredRooms();

        // calculate INTRA-Room-Edge-Transit-Times
        final long intraStart = System.currentTimeMillis();
        Logger.getGlobal().finest(" -Compute intra room edge transit times... ");

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
                    //Edge edge = graph.getEdge( start, end );
                    Edge edge = modelBuilder.getEdge(start, end);
                    if (edge != null && start != end) {
                        if (end.equals(supersink)) {
                            continue;
                        }
                        // if the transitTime for the current edge is not already modified
                        if (modelBuilder.getExactTransitTime(edge) <= 0) {
                            NodeRectangle.NodeRectanglePoint startUpperLeft = mapping.getNodeRectangles().get(start).get_nw_point();
                            NodeRectangle.NodeRectanglePoint startLowerRight = mapping.getNodeRectangles().get(start).get_se_point();
                            int startCentreX = (int) Math.round(mapping.getNodeRectangles().get(start).getCenterX());
                            int startCentreY = (int) Math.round(mapping.getNodeRectangles().get(start).getCenterY());

                            NodeRectangle.NodeRectanglePoint endUpperLeft = mapping.getNodeRectangles().get(end).get_nw_point();
                            NodeRectangle.NodeRectanglePoint endLowerRight = mapping.getNodeRectangles().get(end).get_se_point();
                            int endCentreX = (int) Math.round(mapping.getNodeRectangles().get(end).getCenterX());
                            int endCentreY = (int) Math.round(mapping.getNodeRectangles().get(end).getCenterY());

                            // describes the relative orientation of the start- and end-node rectangles
                            boolean startAboveEnd = false;
                            boolean startBeneathEnd = false;
                            boolean startLeftOfEnd = false;
                            boolean startRightOfEnd = false;

                            if (startLowerRight.getY() == endUpperLeft.getY()) {
                                startAboveEnd = true;
                            }
                            if (startUpperLeft.getY() == endLowerRight.getY()) {
                                startBeneathEnd = true;
                            }
                            if (startLowerRight.getX() == endUpperLeft.getX()) {
                                startLeftOfEnd = true;
                            }
                            if (startUpperLeft.getX() == endLowerRight.getX()) {
                                startRightOfEnd = true;
                            }

                            // coordinates of the point on the centre of the intersecting edge part
                            int intersectionPointX = 0;
                            int intersectionPointY = 0;

                            // calculate the coordinates of the intersection point depending on the relative orientation of the two rectangles
                            if (startAboveEnd) {
                                intersectionPointX = (int) Math.round(0.5 * (Math.min(startLowerRight.getX(), endLowerRight.getX())
                                        + Math.max(endUpperLeft.getX(), startUpperLeft.getX())));
                                intersectionPointY = startLowerRight.getY();
                            }
                            if (startBeneathEnd) {
                                intersectionPointX = (int) Math.round(0.5 * (Math.min(startLowerRight.getX(), endLowerRight.getX())
                                        + Math.max(endUpperLeft.getX(), startUpperLeft.getX())));
                                intersectionPointY = startUpperLeft.getY();
                            }
                            if (startLeftOfEnd) {
                                intersectionPointX = startLowerRight.getX();
                                intersectionPointY = (int) Math.round(0.5 * (Math.min(endLowerRight.getY(), startLowerRight.getY())
                                        + Math.max(endUpperLeft.getY(), startUpperLeft.getY())));
                            }
                            if (startRightOfEnd) {
                                intersectionPointX = startUpperLeft.getY();
                                intersectionPointY = (int) Math.round(0.5 * (Math.min(endLowerRight.getY(), startLowerRight.getY())
                                        + Math.max(endUpperLeft.getY(), startUpperLeft.getY())));
                            }

                            // speed factor within the node-squares
                            double startSpeedFactor = modelBuilder.getZToGraphMapping().getNodeSpeedFactor(start);
                            double endSpeedFactor = modelBuilder.getZToGraphMapping().getNodeSpeedFactor(end);

                            // path from the start centre point to the intersection point
                            double startPath;
                            // path from the intersection point to the end centre point
                            double endPath;

                            // calculate the path length weighted with the appropriate node'Seconds speed factor
                            startPath = Math.sqrt(Math.pow(Math.abs(intersectionPointY - startCentreY), 2)
                                    + Math.pow(Math.abs(intersectionPointX - startCentreX), 2)) / startSpeedFactor;
                            endPath = Math.sqrt(Math.pow(Math.abs(intersectionPointY - endCentreY), 2)
                                    + Math.pow(Math.abs(intersectionPointX - endCentreX), 2)) / endSpeedFactor;
                            double transitTimeStartEnd = startPath + endPath;

                            // getting the graph precision factor, defining the exactness of the distances
                            PropertyContainer propertyContainer = PropertyContainer.getGlobal();
                            int precision = propertyContainer.getAs("converter.GraphPrecision", Integer.class);

                            // adjusting the transit time according to the graph precision value
                            transitTimeStartEnd = transitTimeStartEnd * precision / 400.0d;

                            // write the new transitTime into the IIMapping
                            System.out.println("Set transit time for " + edge + " to " + transitTimeStartEnd);
                            modelBuilder.setExactTransitTime(edge, transitTimeStartEnd);
                        }
                    }
                } // END of for(start)
            }
        } // END of for(roomRaster)
        // END calculate INTRA-Room-Edge-Transit-Times
        Logger.getGlobal().log(java.util.logging.Level.FINEST, "  done in {0}.", Formatter.formatUnit(System.currentTimeMillis() - intraStart, TimeUnits.MILLI_SECOND));

        // calculate INTER-Room-Edge-Transit-Times
        long interStart = System.currentTimeMillis();
        Logger.getGlobal().finest(" -Compute inter room transit times... ");
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
                        //Edge edge = graph.getEdge( nodeA, nodeB );
                        Edge edge = modelBuilder.getEdge(nodeA, nodeB); // TODO: kann contains false sein obwohl edge != null ist?
                        if (edge != null && modelBuilder.contains(edge) && doorEdgeToSquare.get(edge) != null && !doorEdgeToSquare.get(edge).isEmpty()) {
                            ArrayList<ZToGraphRasterSquare> doorSquareListAB = doorEdgeToSquare.get(edge);
                            ArrayList<ZToGraphRasterSquare> doorSquareListA = new ArrayList<>();
                            ArrayList<ZToGraphRasterSquare> doorSquareListB = new ArrayList<>();
                            for (ZToGraphRasterSquare square : doorSquareListAB) {
                                if (square.getNode() == nodeA) {
                                    doorSquareListA.add(square);
                                }
                                if (square.getNode() == nodeB) {
                                    doorSquareListB.add(square);
                                }
                            }
                            double transitTimeA = 0;
                            double transitTimeB = 0;
                            double transitTimeAB;

                            // CALCULATE centre of nodeA
                            PlanPoint mitteA = calculateCentre(nodeA, startRoomSquareList);
                            // END CALCULATE centre of nodeA

                            // CALCULATE centre of nodeB
                            PlanPoint mitteB = calculateCentre(nodeB, endRoomSquareList);
                            // END CALCULATE centre of nodeB

                            PlanPoint mitteSquare;

                            double nodeASpeedFactor = modelBuilder.getZToGraphMapping().getNodeSpeedFactor(nodeA);
                            for (ZToGraphRasterSquare square : doorSquareListA) {
                                mitteSquare = calculateCentre(square);
                                transitTimeA += calculateDistance(mitteA, mitteSquare);
                            }
                            transitTimeA = (1. / nodeASpeedFactor) * transitTimeA / doorSquareListA.size();

                            double nodeBSpeedFactor = modelBuilder.getZToGraphMapping().getNodeSpeedFactor(nodeB);
                            for (ZToGraphRasterSquare square : doorSquareListB) {
                                mitteSquare = calculateCentre(square);
                                transitTimeB += calculateDistance(mitteB, mitteSquare);
                            }
                            transitTimeB = (1. / nodeBSpeedFactor) * transitTimeB / doorSquareListB.size();

                            transitTimeAB = transitTimeA + transitTimeB;

                            // getting the graph precision factor, defining the exactness of the distances
                            PropertyContainer propertyContainer = PropertyContainer.getGlobal();
                            int precision = propertyContainer.getAs("converter.GraphPrecision", Integer.class);

                            // adjusting the transit time according to the graph precision value
                            transitTimeAB = transitTimeAB * precision / 400.0d;

                            System.out.println("Set transit time for " + edge + " to " + transitTimeAB);
                            modelBuilder.setExactTransitTime(edge, transitTimeAB);
                        }
                    }
                }
            }
        }
        // END calculate INTER-Room-Edge-Transit-Times
        Logger.getGlobal().log(java.util.logging.Level.FINEST, "  done in {0}.", Formatter.formatUnit(System.currentTimeMillis() - interStart, TimeUnits.MILLI_SECOND));

        Logger.getGlobal().log(java.util.logging.Level.FINE, "done in {0}.", Formatter.formatUnit((System.currentTimeMillis() - startTT), TimeUnits.MILLI_SECOND));
    }

    // i,j stehen fuer das eigentliche square, nicht fuer den nachbarn!
    private static boolean isRightSquareBlocked(ZToGraphRoomRaster room, int i, int j, boolean careForDelayAreas, boolean careForAssignmentAreas) {
        int numOfColumns = room.getColumnCount();
        int numOfRows = room.getRowCount();

        if (i >= numOfColumns - 1) {
            return true;
        }
        if (j >= numOfRows) {
            return true;
        }

        ZToGraphRasterSquare square = room.getSquare(i, j);
        if (square.isBlocked(Direction8.Right)) {
            return true;
        }

        ZToGraphRasterSquare right = room.getSquare(i + 1, j);

        if (right.isInaccessible()) {
            return true;
        }
        if (right.isMarked()) {
            return true;
        }

        if (careForDelayAreas) {
            if (square.getSpeedFactor() != right.getSpeedFactor()) {
                return true;
            }
        }

        if (careForAssignmentAreas) {
            if (square.isSource() != right.isSource()) {
                return true;
            }
        }

        // a node is save or not but not both
        if (square.isSave() != right.isSave()) {
            return true;
        }

        if(square.isExit() != right.isExit()) {
            return true;
        }

        // Only squares from stairs with the same up and down speedfactor may be in the same node
        // (or squares that are not in a stair with stairs that are also not in a stair)
        if (square.getUpSpeedFactor() != right.getUpSpeedFactor()) {
            return true;
        }
        if (square.getDownSpeedFactor() != right.getDownSpeedFactor()) {
            return true;
        }

        return false;
    }

    private static boolean isDownSquareBlocked(ZToGraphRoomRaster room, int i, int j, boolean careForDelayAreas, boolean careForAssignmentAreas) {
        int numOfColumns = room.getColumnCount();
        int numOfRows = room.getRowCount();

        if (i >= numOfColumns) {
            return true;
        }
        if (j >= numOfRows - 1) {
            return true;
        }

        ZToGraphRasterSquare square = room.getSquare(i, j);
        if (square.isBlocked(Direction8.Down)) {
            return true;
        }

        ZToGraphRasterSquare down = room.getSquare(i, j + 1);

        if (down.isInaccessible()) {
            return true;
        }
        if (down.isMarked()) {
            return true;
        }

        if (careForDelayAreas) {
            if (square.getSpeedFactor() != down.getSpeedFactor()) {
                return true;
            }
        }

        if (careForAssignmentAreas) {
            if (square.isSource() != down.isSource()) {
                return true;
            }
        }

        // a node is save or not but not both
        if (square.isSave() != down.isSave()) {
            return true;
        }

        if(square.isExit() != down.isExit()) {
            return true;
        }

        // Only squares from stairs with the same up and down speedfactor may be in the same node
        // (or squares that are not in a stair with stairs that are also not in a stair)
        if (square.getUpSpeedFactor() != down.getUpSpeedFactor()) {
            return true;
        }
        if (square.getDownSpeedFactor() != down.getDownSpeedFactor()) {
            return true;
        }

        return false;
    }

    /**
     *
     * @param node
     * @param squareList
     * @return
     */
    private static PlanPoint calculateCentre(Node node, List<ZToGraphRasterSquare> squareList) {
        int nodeBreadth;
        int nodeHeight;

        int nodeCentreX;
        int nodeCentreY;

        // coordinates of the upper Left corner of the upper Left square of the start node
        int nodeUpperLeftX = Integer.MAX_VALUE;
        int nodeUpperLeftY = Integer.MAX_VALUE;
        // coordinates of the lower Right corner of the lower Right square of the start node
        int nodeLowerRightX = 0;
        int nodeLowerRightY = 0;

        // find the coordinates of the upper Left and the lower Right square of the current start-node
        for (ZToGraphRasterSquare square : squareList) {
            if (node.id() == square.getNode().id()) {
                if (square.getXOffset() <= nodeUpperLeftX) {
                    nodeUpperLeftX = square.getXOffset();
                }
                if (square.getYOffset() <= nodeUpperLeftY) {
                    nodeUpperLeftY = square.getYOffset();
                }
                if (square.getXOffset() >= nodeLowerRightX) {
                    nodeLowerRightX = square.getXOffset();
                }
                if (square.getYOffset() >= nodeLowerRightY) {
                    nodeLowerRightY = square.getYOffset();
                }
            }
        }
        // adapt to rectangle corner coordinates
        nodeLowerRightX += 400;
        nodeLowerRightY += 400;
        // calculate the centre-coordinates of the start-node-rectangle
        nodeBreadth = Math.abs(nodeLowerRightX - nodeUpperLeftX);
        nodeHeight = Math.abs(nodeLowerRightY - nodeUpperLeftY);
        nodeCentreX = (int) Math.round(0.5 * nodeBreadth) + nodeUpperLeftX;
        nodeCentreY = (int) Math.round(0.5 * nodeHeight) + nodeUpperLeftY;

        PlanPoint point = new PlanPoint(nodeCentreX, nodeCentreY);
        return point;
    }

    private static PlanPoint calculateCentre(ZToGraphRasterSquare square) {
        int squareCentreX, squareCentreY;

        squareCentreX = square.getXOffset() + 200;
        squareCentreY = square.getYOffset() + 200;

        PlanPoint point = new PlanPoint(squareCentreX, squareCentreY);
        return point;
    }

    private static int calculateDistance(PlanPoint start, PlanPoint end) {
        int distance;
        int distanceX, distanceY;
        int startX = start.getXInt();
        int startY = start.getYInt();
        int endX = end.getXInt();
        int endY = end.getYInt();

        distanceX = Math.abs(startX - endX);
        distanceY = Math.abs(startY - endY);

        distance = (int) Math.round(Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2)));
        return distance;
    }

    private HashMap<Edge, ArrayList<ZToGraphRasterSquare>> connectRooms() {
        Logger.getGlobal().fine("Connect rooms... ");
        ZToGraphMapping mappingLocal = modelBuilder.getZToGraphMapping();

        HashMap<Edge, ArrayList<ZToGraphRasterSquare>> table = new HashMap<>();

        //Two mappings to store capacities
        //just temporally.
        Collection<ZToGraphRasteredDoor> doors = raster.getDoors();

        for (ZToGraphRasteredDoor door : doors) {
            Node firstNode = door.getFirstDoorPart().getNode();
            Node secondNode = door.getSecondDoorPart().getNode();

            if (firstNode.id() < secondNode.id()) {
                continue;
            }

            //Edge edge = graph.getEdge( firstNode, secondNode );
            Edge edge = modelBuilder.getEdge(firstNode, secondNode);
            if (edge == null) {
                edge = modelBuilder.newEdge(firstNode, secondNode);
                mappingLocal.setEdgeLevel(edge, Level.Equal);

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

        //Connect the super source with all other sources
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

                    // todo: parameter
                    if ( square.isExit()) {
                        Node node = square.getNode();
                        Edge edge = modelBuilder.getEdge(node, supersink);
                        modelBuilder.setEdgeCapacity(edge, Integer.MAX_VALUE);
                    }// end if safe
                }//end outer loop
            }
        }
        Logger.getGlobal().fine("done.");
        return table;
    }

    private double getLowerDistance(StairArea stair, PlanPoint center) {
        PlanPoint A = stair.getLowerLevelStart();
        PlanPoint Ap = stair.getLowerLevelStart();
        PlanPoint B = center;
        return dist(A, Ap, B);
    }

    private double getHighDistance(StairArea stair, PlanPoint center) {
        PlanPoint A = stair.getUpperLevelStart();
        PlanPoint Ap = stair.getUpperLevelEnd();
        PlanPoint B = center;
        return dist(A, Ap, B);
    }

    private static double dist(PlanPoint A, PlanPoint Ap, PlanPoint B) {
        if (A.x == Ap.x) {
            return Math.abs(A.x - B.x);
        }

        //System.out.println( "A: " + A );
        //System.out.println( "Ap: " + Ap );
        PlanPoint u = new PlanPoint(Ap.x - A.x, Ap.y - A.y);
        //System.out.println( "u: " + u );

        PlanPoint BmA = new PlanPoint(B.x - A.x, B.y - A.y);
        //System.out.println( "B-A: " + BmA );
        double uu = u.x * u.x + u.y * u.y;
        //System.out.println( "uu: " + uu );
        double bmau = BmA.x * u.x + BmA.y * u.y;
        //System.out.println( "bmau: " + bmau );

        double f = bmau / uu;
        //System.out.println( "factor: " + f );

        PlanPoint P = new PlanPoint(A.x / 1000 + u.x / 1000 * f, A.y / 1000 + u.y / 1000 * f);
        System.out.println(P);
        System.out.println(B);
        return P.distance(B);
    }

    public static void main(String args[]) {
        PlanPoint A = new PlanPoint(4, 1, true);
        PlanPoint Ap = new PlanPoint(6, 1, true);
        PlanPoint B = new PlanPoint(1, 4, true);
        System.out.println(dist(A, Ap, B));
    }

    // TODO: nicer, use vector math methods
    private void defineEdgeLevel(Node node, Edge edge, ZToGraphRoomRaster room, ZToGraphMapping mappingLocal, int row, int col) {
        Room modelRoom = mapping.getRoom(node);

        ZToGraphRasterSquare sqStart = null;
        ZToGraphRasterSquare sqEnd = null;

        for (ZToGraphRasterSquare sq : room.getAccessibleSquares()) {
            if (sq.getNode() == edge.start()) {
                sqStart = sq;
            }
            if (sq.getNode() == edge.end()) {
                sqEnd = sq;
            }
        }

        boolean startStair = sqStart.isStair();
        boolean endStair = sqEnd.isStair();

        StairArea startArea = null;
        StairArea endArea = null;

        for (StairArea stair : modelRoom.getStairAreas()) {
            if (stair.contains(sqStart.getCenter())) {
                startArea = stair;
            }
            if (stair.contains(sqEnd.getCenter())) {
                endArea = stair;
            }
        }

        if (startStair && endStair) {
            // beide innerhalb
            double startDistanceLower = getLowerDistance(startArea, sqStart.getCenter());
            double startDistanceHigh = getHighDistance(startArea, sqStart.getCenter());
            double endDistanceLower = getLowerDistance(endArea, sqEnd.getCenter());
            double endDistanceHigh = getHighDistance(endArea, sqEnd.getCenter());
            if (startDistanceLower < endDistanceLower && startDistanceHigh > endDistanceHigh) {
                // goes up
                mappingLocal.setEdgeLevel(edge, Level.Higher);
            } else if (startDistanceLower > endDistanceLower && startDistanceHigh < endDistanceHigh) {
                // goes down
                mappingLocal.setEdgeLevel(edge, Level.Lower);
            } else {
                // wtf?
                mappingLocal.setEdgeLevel(edge, Level.Equal);
            }
        } else if (startStair && !endStair) {
            // start stair innerhalb
            double endDistanceLower = getLowerDistance(startArea, sqEnd.getCenter());
            double endDistanceHigh = getHighDistance(startArea, sqEnd.getCenter());
            if (endDistanceLower < endDistanceHigh) {
                // ende ist näher an lower
                mappingLocal.setEdgeLevel(edge, Level.Lower);
            } else if (endDistanceLower > endDistanceHigh) {
                mappingLocal.setEdgeLevel(edge, Level.Higher);
            } else {
                mappingLocal.setEdgeLevel(edge, Level.Equal);
            }
        } else if (!startStair && endStair) {
            double startDistanceLower = getLowerDistance(endArea, sqStart.getCenter());
            double startDistanceHigh = getHighDistance(endArea, sqStart.getCenter());
            if (startDistanceLower < startDistanceHigh) {
                // ende ist näher an lower
                mappingLocal.setEdgeLevel(edge, Level.Higher);
            } else if (startDistanceLower > startDistanceHigh) {
                mappingLocal.setEdgeLevel(edge, Level.Lower);
            } else {
                mappingLocal.setEdgeLevel(edge, Level.Equal);
            }
        } else {
            // beide außerhalb. ignoriere die möglichkeit, dass fast komplett dazwischen eine treppe liegt!
            mappingLocal.setEdgeLevel(edge, Level.Equal);
        }

    }
}
