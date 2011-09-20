/* zet evacuation tool copyright (c) 2007-10 zet evacuation team
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/**
 * ZToNonGridGraphConverter.java
 *
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.common.localization.DefaultLoc;
import ds.PropertyContainer;
import ds.graph.DynamicNetwork;
import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import ds.z.PlanPoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import de.tu_berlin.math.coga.common.util.Direction;
import de.tu_berlin.math.coga.common.util.Level;
import de.tu_berlin.math.coga.common.util.Formatter;
import de.tu_berlin.math.coga.common.util.Formatter.TimeUnits;
import ds.graph.IdentifiableDoubleMapping;

/**
 *
 */
public class ZToNonGridGraphConverter extends BaseZToGraphConverter {

	final static boolean progress = false;
	final static boolean debug = false;
	final static int FACTOR = 1;

	@Override
	protected void createNodes() {
		System.out.print( "Create Nodes... " );
		List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();

		// New graph
		DynamicNetwork graph = new DynamicNetwork();

		// List of sources according to isSource flag of squares
		LinkedList<Node> sources = new LinkedList<Node>();

		// super sink
		Node supersink = new Node( 0 );
		graph.setNode( supersink );
		model.setSupersink( supersink );

		mapping.setNodeSpeedFactor( supersink, 1 );
		mapping.setNodeRectangle( supersink, new NodeRectangle( 0, 0, 0, 0 ) );
		mapping.setFloorForNode( supersink, -1 );

		// get attribute from property container
		PropertyContainer propertyContainer = PropertyContainer.getInstance();
		boolean accurateDelayAreaCreation = propertyContainer.getAsBoolean( "converter.AccurateDelayAreaCreation" );
		boolean accurateAssignmentAreaCration = propertyContainer.getAsBoolean( "converter.accurateAssignmentAreaCreation" );
		if( debug ) {
			if( accurateDelayAreaCreation )
				System.out.println( "\nDelay areas are taken into account." );
			else
				System.out.println( "\nDelay areas are not taken into account." );
			System.out.println();
		}

		int nodeCount = 1;

		// iterate through all rooms and create a graph for each room
		for( ZToGraphRoomRaster room : rasteredRooms ) {

			// unmark all squares because they are not yet processed.
			room.unmarkAllSquares();

			int roomOffsetX = room.getXOffset();
			int roomOffsetY = room.getYOffset();

			int numOfColumns = room.getColumnCount();
			int numOfRows = room.getRowCount();

			// iterate through all squares of the (rastered) room
			// and merge some of them to nodes
			for( int y = 0; y < numOfRows; y++ )
				for( int x = 0; x < numOfColumns; x++ ) {

					// Current square;
					// A new node will be created that contains at least the
					// current square.
					ZToGraphRasterSquare square = room.getSquare( x, y );

					int nodeRectangleNW_x = roomOffsetX + x * room.getRaster();
					int nodeRectangleNW_y = roomOffsetY + y * room.getRaster();
					// Finding the upper Right square by looking for the maximal x and Years.
					int maxX = x;
					int maxY = y;

					if( square.isAccessible() && !square.isMarked() ) {

						Node node = new Node( nodeCount );
						model.getZToGraphMapping().getNodeFloorMapping().set( node, plan.getFloorID( room.getFloor() ) );
						model.getZToGraphMapping().setIsEvacuationNode( node, square.isExit() );
						if( square.isExit() )
							model.getZToGraphMapping().setNameOfExit( node, square.getName() );
						model.getZToGraphMapping().setIsSourceNode( node, square.isSource() );
						model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
						if( plan.getFloorID( room.getFloor() ) == -1 )
							System.out.println( "\nFehler: Floor beim Konvertieren nicht gefunden." );

						boolean nodeIsSource = false;

						// Initializing variables for speed factor calculation

						//boolean test = square.isStair();
						double sumOfSpeedFactors = square.getSpeedFactor();
						double downSpeedFactor = square.getDownSpeedFactor();
						double upSpeedFactor = square.getUpSpeedFactor();
						int numOfSquares = 1;

						graph.setNode( node );
						nodeCount++;

						// set the node of the current square to the new node
						// and mark it as processed.
						square.mark();
						square.setNode( node );
						// If the isSource flag of the square is set,
						// the node becomes a source.
						if( square.isSource() )
							nodeIsSource = true;

						// First part: Find the highest number n
						// such that a the n x n-square having the current
						// raster square as upper Left corner
						// fits into the building plan without colliding
						// with isInaccessible areas or similar.
						// (n is stored in the variable extent)

						int extent = 0;
						boolean downblocked = false, rightblocked = false, blocked = false;

						while( extent < numOfColumns && extent < numOfRows && !downblocked && !rightblocked && !blocked ) {

							// check whether a new line can be added at the
							// Right of the square of raster squares
							for( int offset = 0; offset <= extent; offset++ )
								rightblocked |= isRightSquareBlocked( room, x + extent,
												y + offset, accurateDelayAreaCreation, accurateAssignmentAreaCration );

							// check whether a new line can be added under
							// the square of raster squares
							for( int offset = 0; offset <= extent; offset++ )
								downblocked |= isDownSquareBlocked( room, x + offset,
												y + extent, accurateDelayAreaCreation, accurateAssignmentAreaCration );

							blocked = isDownSquareBlocked( room, x + extent + 1,
											y + extent, accurateDelayAreaCreation, accurateAssignmentAreaCration );

							// extent the square of raster squares by one line
							// to the Right and under the square,
							// if both directions are not blocked
							if( !downblocked && !rightblocked && !blocked ) {
								for( int offset = 0; offset <= extent + 1; offset++ ) {
									ZToGraphRasterSquare rsquare = room.getSquare( x + extent + 1, y + offset );
									maxX = Math.max( maxX, x + extent + 1 );
									maxY = Math.max( maxY, y + offset );
									rsquare.setNode( node );
									// If the isSource flag of the square is set,
									// the node becomes a source.
									if( rsquare.isSource() )
										nodeIsSource = true;
									rsquare.mark();
									sumOfSpeedFactors += rsquare.getSpeedFactor();
									numOfSquares++;
								}
								// hier muss man nur eins weniger setzen,
								// da das gemeinsame schon in der 1. schleife gesetzt wurde
								for( int offset = 0; offset <= extent; offset++ ) {
									ZToGraphRasterSquare dsquare = room.getSquare( x + offset, y + extent + 1 );
									maxX = Math.max( maxX, x + offset );
									maxY = Math.max( maxY, y + extent + 1 );
									dsquare.setNode( node );
									if( dsquare.isSource() )
										nodeIsSource = true;
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

						int imbalance = propertyContainer.getAsInt( "converter.Imbalance" );
						int added = 0;

						// extent down
						while( !downblocked && added < imbalance ) {
							// Check the line under the rectangle of raster
							// squares
							for( int offset = 0; offset <= extent; offset++ )
								downblocked |= isDownSquareBlocked( room, x + offset,
												y + extent + added, accurateDelayAreaCreation, accurateAssignmentAreaCration );    // sondern der obere �bergeben werden muss

							// set the line under the rectangle if is free
							if( !downblocked ) {
								for( int offset = 0; offset <= extent; offset++ ) {
									ZToGraphRasterSquare dsquare = room.getSquare( x + offset, y + extent + added + 1 );
									maxX = Math.max( maxX, x + offset );
									maxY = Math.max( maxY, y + extent + added + 1 );
									dsquare.mark();
									dsquare.setNode( node );
									if( dsquare.isSource() )
										nodeIsSource = true;
									sumOfSpeedFactors += dsquare.getSpeedFactor();
									numOfSquares++;
									rightblocked = true;
									// damit das n�chste while nicht auch
									// noch aufgerufen wird
								}
								added++;
							}
						}

						// extent to the Right
						while( !rightblocked && added < imbalance ) {
							// Check the line Right of the rectangle of raster squares
							for( int offset = 0; offset <= extent; offset++ )
								rightblocked |= isRightSquareBlocked( room, x + extent + added,
												y + offset, accurateDelayAreaCreation, accurateAssignmentAreaCration );

							// set the line Right of the rectangle if it is free
							if( !rightblocked ) {
								for( int offset = 0; offset <= extent; offset++ ) {
									ZToGraphRasterSquare rsquare = room.getSquare( x + extent + added + 1,
													y + offset );
									maxX = Math.max( maxX, x + extent + added + 1 );
									maxY = Math.max( maxY, y + offset );
									rsquare.mark();
									rsquare.setNode( node );
									if( rsquare.isSource() )
										nodeIsSource = true;
									sumOfSpeedFactors += rsquare.getSpeedFactor();
									numOfSquares++;
									downblocked = true; // nur aus Prinzip
								}
								added++;
							}
						}
						mapping.setNodeSpeedFactor( node, sumOfSpeedFactors / numOfSquares );
						mapping.setNodeUpSpeedFactor( node, upSpeedFactor );
						mapping.setNodeDownSpeedFactor( node, downSpeedFactor );
						// calculate the lower Right corner of the node rectangle.
						int nodeRectangleSE_x = roomOffsetX + room.getRaster() * (maxX + 1);
						int nodeRectangleSE_y = roomOffsetY + room.getRaster() * (maxY + 1);
						// save the node rectangle in the mapping
						mapping.setNodeRectangle( node, new NodeRectangle( nodeRectangleNW_x, -nodeRectangleNW_y, nodeRectangleSE_x, -nodeRectangleSE_y ) );
						// save the number of the floor the node belongs to
						mapping.setFloorForNode( node, raster.getFloors().indexOf( room.getFloor() ) );
						if( nodeIsSource )
							sources.add( node );
					}
				}
			if( progress )
				System.out.println( ": A rastered room was processed and subdivided into nodes." );
			if( debug ) {
				System.out.println( "A rastered room was processed and got subdevided like this:" );
				System.out.print( room );
			}
			if( debug )
				System.out.println( "A rastered room was processed and got subdevided, nodecount is now " + nodeCount + "." );
		}
		// Set graph to model
		model.setNetwork( graph );
		model.setSources( sources );
		System.out.println( " fertig" );
	}

	@Override
	protected void createEdgesAndCapacities() {
		System.out.print( "Set up edges and compute capacities... " );
		ZToGraphMapping mapping = model.getZToGraphMapping();

		List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();
		int nextEdge = 0;

		DynamicNetwork graph = model.getDynamicNetwork();

		//Two mappings to store capacities
		IdentifiableIntegerMapping<Node> nodesCap = new IdentifiableIntegerMapping<Node>( graph.numberOfNodes() );
		model.setNodeCapacities( nodesCap );

		IdentifiableIntegerMapping<Edge> edgesCap = new IdentifiableIntegerMapping<Edge>( graph.numberOfEdges() * graph.numberOfEdges() );
		model.setEdgeCapacities( edgesCap );

		// set node capacity of super sink to max value
		nodesCap.set( model.getSupersink(), Integer.MAX_VALUE );

		for( ZToGraphRoomRaster room : rasteredRooms ) {

			int colCount = room.getColumnCount();
			int rowCount = room.getRowCount();

			Node lastNode = null;

			//iterate over each square VERTICALLY
			for( int row = 0; row < rowCount; row++ ) {
				for( int col = 0; col < colCount; col++ ) {
					ZToGraphRasterSquare square = room.getSquare( col, row );
					Node node = square.getNode();
					//increase node capacity
					if( node != null )
						nodesCap.increase( node, 1 * FACTOR );

					boolean nodesConnectable = (node != null) && (lastNode != null) && !lastNode.equals( node );
					boolean connectionPassable = (col != 0) && (!square.isBlocked( Direction.Left ));

					if( nodesConnectable && connectionPassable ) {
						Edge edge = graph.getEdge( lastNode, node );
						if( edge == null ) {
							edge = new Edge( nextEdge++, lastNode, node );
							graph.addEdge( edge );
							edgesCap.set( edge, 0 );
							ZToGraphRasterSquare lastSquare = null;
							if( col > 0 )
								lastSquare = room.getSquare( col - 1, row );
							else
								throw new AssertionError( "Col should not be zero at this point." );
							mapping.setEdgeLevel( edge, lastSquare.getLevel( Direction.getDirection( 1, 0 ) ) );
						}
						edgesCap.increase( edge, 1 * FACTOR );
					}
					lastNode = node;
				}// end of the outer for each loop
				lastNode = null;
			}//end for each room


			//Iterate now VERTICALLY to add the capacities of the
			lastNode = null;
			for( int col = 0; col < colCount; col++ ) {
				for( int row = 0; row < rowCount; row++ ) {
					ZToGraphRasterSquare square = room.getSquare( col, row );
					Node node = square.getNode();
					//No need to increase the capacity since the square has already been taken in consideration

					boolean nodesConnectable = (node != null) && (lastNode != null) && !lastNode.equals( node );
					boolean connectionPassable = (row != 0) && (!square.isBlocked( Direction.Top ));

					if( nodesConnectable && connectionPassable ) {
						Edge edge = graph.getEdge( lastNode, node );
						if( edge == null ) {
							edge = new Edge( nextEdge++, lastNode, node );
							graph.addEdge( edge );
							edgesCap.set( edge, 0 );
							ZToGraphRasterSquare lastSquare = null;
							if( row > 0 )
								lastSquare = room.getSquare( col, row - 1 );
							else
								throw new AssertionError( DefaultLoc.getSingleton().getString( "converter.RowIsZeroException" ) );
							mapping.setEdgeLevel( edge, lastSquare.getLevel( Direction.getDirection( 0, 1 ) ) );
						}
						edgesCap.increase( edge, 1 * FACTOR );
					}
					lastNode = node;
				}// end of the inner for each loop
				lastNode = null;
			}// end of the outer for each loop
		}//end for each room
		model.setNodeCapacities( nodesCap );
		model.setEdgeCapacities( edgesCap );
		System.out.println( "fertig" );
	}

	@Override
	protected void computeTransitTimes() {
		Hashtable<Edge, ArrayList<ZToGraphRasterSquare>> doorEdgeToSquare = connectRooms();
		long startTT = System.currentTimeMillis();
		//System.out.print( "Compute transit times... " );

		//protected IdentifiableDoubleMapping<Edge> exactTransitTimes;
		exactTransitTimes = new IdentifiableDoubleMapping<Edge>( 1 );
		List<ZToGraphRoomRaster> roomRasterList = raster.getAllRasteredRooms();
		Graph graph = model.getGraph();

		// calculate INTRA-Room-Edge-Transit-Times
		long intraStart = System.currentTimeMillis();
		System.out.print( "Compute intra room edge transit times... " );

		// do for all rooms of the roomRasterList
		for( ZToGraphRoomRaster room : roomRasterList ) {

			List<ZToGraphRasterSquare> roomSquareList = room.getAccessibleSquares();
			HashSet<Node> nodeListOfRoom = new HashSet<Node>();
			for( ZToGraphRasterSquare square : roomSquareList )
				nodeListOfRoom.add( square.getNode() );

			// calculate the Node -> ZToGraphRasterSquare mapping
			HashMap<Node, LinkedList<ZToGraphRasterSquare>> nodeToSquare = new HashMap<Node, LinkedList<ZToGraphRasterSquare>>();
			for( Node node : nodeListOfRoom ) {//nodes){
				LinkedList<ZToGraphRasterSquare> nodeSquareList = new LinkedList<ZToGraphRasterSquare>();
				for( ZToGraphRasterSquare square : roomSquareList )
					if( square.getNode() != null )
						if( square.getNode().id() == node.id() )
							nodeSquareList.add( square );
				nodeToSquare.put( node, nodeSquareList );
			}

			Node supersink = model.getSupersink();

			for( Node start : nodeListOfRoom )//nodes){
				for( Node end : nodeListOfRoom ) {//nodes){
					// do only, if there is an edge between start and end & if start does not equal end
					Edge edge = graph.getEdge( start, end );
					if( edge != null && start != end ) {
						if( end.equals( supersink ) ) {
							exactTransitTimes.set( edge, 0 );
							continue;
						}
						// add a transitTime-0-entry to the IIMapping for the current edge if there is not yet such an entry
						if( !exactTransitTimes.isDefinedFor( edge ) || exactTransitTimes.get( edge ) <= 0 )
							exactTransitTimes.set( edge, 0 );
						// if the transitTime for the current edge is not already modified
						if( exactTransitTimes.get( edge ) <= 0 ) {
							NodeRectangle.NodeRectanglePoint startUpperLeft = mapping.getNodeRectangles().get( start ).get_nw_point();
							NodeRectangle.NodeRectanglePoint startLowerRight = mapping.getNodeRectangles().get( start ).get_se_point();
							int startCentreX = (int) Math.round( mapping.getNodeRectangles().get( start ).getCenterX() );
							int startCentreY = (int) Math.round( mapping.getNodeRectangles().get( start ).getCenterY() );

							NodeRectangle.NodeRectanglePoint endUpperLeft = mapping.getNodeRectangles().get( end ).get_nw_point();
							NodeRectangle.NodeRectanglePoint endLowerRight = mapping.getNodeRectangles().get( end ).get_se_point();
							int endCentreX = (int) Math.round( mapping.getNodeRectangles().get( end ).getCenterX() );
							int endCentreY = (int) Math.round( mapping.getNodeRectangles().get( end ).getCenterY() );
							
							// describes the relative orientation of the start- and end-node rectangles
							boolean startAboveEnd = false;
							boolean startBeneathEnd = false;
							boolean startLeftOfEnd = false;
							boolean startRightOfEnd = false;

							if( startLowerRight.getY() == endUpperLeft.getY() )
								startAboveEnd = true;
							if( startUpperLeft.getY() == endLowerRight.getY() )
								startBeneathEnd = true;
							if( startLowerRight.getX() == endUpperLeft.getX() )
								startLeftOfEnd = true;
							if( startUpperLeft.getX() == endLowerRight.getX() )
								startRightOfEnd = true;

							// coordinates of the point on the centre of the intersecting edge part
							int intersectionPointX = 0;
							int intersectionPointY = 0;

							// calculate the coordinates of the intersection point depending on the relative orientation of the two rectangles
							if( startAboveEnd ) {
								intersectionPointX = (int) Math.round( 0.5 * Math.abs( Math.min( startLowerRight.getX(), endLowerRight.getX() ) - Math.max( endUpperLeft.getX(), startUpperLeft.getX() ) ) ) + Math.max( endUpperLeft.getX(), startUpperLeft.getX() );
								intersectionPointY = startLowerRight.getY();
							}
							if( startBeneathEnd ) {
								intersectionPointX = (int) Math.round( 0.5 * Math.abs( Math.min( startLowerRight.getX(), endLowerRight.getX() ) - Math.max( endUpperLeft.getX(), startUpperLeft.getX() ) ) ) + Math.max( endUpperLeft.getX(), startUpperLeft.getX() );
								intersectionPointY = startUpperLeft.getY();
							}
							if( startLeftOfEnd ) {
								intersectionPointX = startLowerRight.getX();
								intersectionPointY = (int) Math.round( 0.5 * Math.abs( Math.min( endLowerRight.getY(), startLowerRight.getY() ) - Math.max( endUpperLeft.getY(), startUpperLeft.getY() ) ) ) + Math.max( endUpperLeft.getY(), startUpperLeft.getY() );
							}
							if( startRightOfEnd ) {
								intersectionPointX = startUpperLeft.getY();
								intersectionPointY = (int) Math.round( 0.5 * Math.abs( Math.min( endLowerRight.getY(), startLowerRight.getY() ) - Math.max( endUpperLeft.getY(), startUpperLeft.getY() ) ) ) + Math.max( endUpperLeft.getY(), startUpperLeft.getY() );
							}

							// speed factor within the node-squares
							double startSpeedFactor = model.getZToGraphMapping().getNodeSpeedFactor( start );
							double endSpeedFactor = model.getZToGraphMapping().getNodeSpeedFactor( end );

							// path from the start centre point to the intersection point
							double startPath;
							// path from the intersection point to the end centre point
							double endPath;

							// calculate the path length weighted with the appropriate node'Seconds speed factor
							startPath = Math.sqrt( Math.pow( Math.abs( intersectionPointY - startCentreY ), 2 ) + Math.pow( Math.abs( intersectionPointX - startCentreX ), 2 ) ) / startSpeedFactor;
							endPath = Math.sqrt( Math.pow( Math.abs( intersectionPointY - endCentreY ), 2 ) + Math.pow( Math.abs( intersectionPointX - endCentreX ), 2 ) ) / endSpeedFactor;
							double transitTimeStartEnd = startPath + endPath;

							// getting the graph precision factor, defining the exactness of the distances
							PropertyContainer propertyContainer = PropertyContainer.getInstance();
							int precision = propertyContainer.getAs( "converter.GraphPrecision", Integer.class );

							// adjusting the transit time according to the graph precision value
							transitTimeStartEnd = transitTimeStartEnd * precision / 400.0d;

							// write the new transitTime into the IIMapping
							exactTransitTimes.set( edge, transitTimeStartEnd );
						}
					}
				} // END of for(start)
		} // END of for(roomRaster)
		// END calculate INTRA-Room-Edge-Transit-Times
		System.out.println( "fertig in " + Formatter.formatTimeUnit( System.currentTimeMillis() - intraStart, TimeUnits.MilliSeconds ) );

		// calculate INTER-Room-Edge-Transit-Times
		long interStart = System.currentTimeMillis();
		System.out.print( "Compute inter room transit times... " );
		for( ZToGraphRoomRaster startRoom : roomRasterList )
			for( ZToGraphRoomRaster endRoom : roomRasterList ) {

				// CALCULATE roomNodeMap : ZToGraphRoomRaster -> LinkedList<Node>
				List<ZToGraphRasterSquare> startRoomSquareList = startRoom.getAccessibleSquares();
				List<ZToGraphRasterSquare> endRoomSquareList = endRoom.getAccessibleSquares();
				HashSet<Node> nodeListOfStartRoom = new HashSet<Node>();
				HashSet<Node> nodeListOfEndRoom = new HashSet<Node>();
				for( ZToGraphRasterSquare square : startRoomSquareList )
					nodeListOfStartRoom.add( square.getNode() );
				for( ZToGraphRasterSquare square : endRoomSquareList )
					nodeListOfEndRoom.add( square.getNode() );

				for( Node nodeA : nodeListOfStartRoom )
					for( Node nodeB : nodeListOfEndRoom ) {
						Edge edge = graph.getEdge( nodeA, nodeB );
						if( edge != null && graph.contains( edge ) && doorEdgeToSquare.get( edge ) != null && !doorEdgeToSquare.get( edge ).isEmpty() ) {
							ArrayList<ZToGraphRasterSquare> doorSquareListAB = doorEdgeToSquare.get( edge );
							ArrayList<ZToGraphRasterSquare> doorSquareListA = new ArrayList<ZToGraphRasterSquare>();
							ArrayList<ZToGraphRasterSquare> doorSquareListB = new ArrayList<ZToGraphRasterSquare>();
							for( ZToGraphRasterSquare square : doorSquareListAB ) {
								if( square.getNode() == nodeA )
									doorSquareListA.add( square );
								if( square.getNode() == nodeB )
									doorSquareListB.add( square );
							}
							double transitTimeA = 0;
							double transitTimeB = 0;
							double transitTimeAB;

							// CALCULATE centre of nodeA
							PlanPoint mitteA = calculateCentre( nodeA, startRoomSquareList );
							// END CALCULATE centre of nodeA

							// CALCULATE centre of nodeB
							PlanPoint mitteB = calculateCentre( nodeB, endRoomSquareList );
							// END CALCULATE centre of nodeB

							PlanPoint mitteSquare;

							double nodeASpeedFactor = model.getZToGraphMapping().getNodeSpeedFactor( nodeA );
							for( ZToGraphRasterSquare square : doorSquareListA ) {
								mitteSquare = calculateCentre( square );
								transitTimeA += calculateDistance( mitteA, mitteSquare );
							}
							transitTimeA = (1. / nodeASpeedFactor) * transitTimeA / doorSquareListA.size();

							double nodeBSpeedFactor = model.getZToGraphMapping().getNodeSpeedFactor( nodeB );
							for( ZToGraphRasterSquare square : doorSquareListB ) {
								mitteSquare = calculateCentre( square );
								transitTimeB += calculateDistance( mitteB, mitteSquare );
							}
							transitTimeB = (1. / nodeBSpeedFactor) * transitTimeB / doorSquareListB.size();

							transitTimeAB = transitTimeA + transitTimeB;

							// getting the graph precision factor, defining the exactness of the distances
							PropertyContainer propertyContainer = PropertyContainer.getInstance();
							int precision = propertyContainer.getAs( "converter.GraphPrecision", Integer.class );

							// adjusting the transit time according to the graph precision value
							transitTimeAB = transitTimeAB * precision / 400.0d;

							exactTransitTimes.set( edge, transitTimeAB );
						}
					}
			}
		// END calculate INTER-Room-Edge-Transit-Times
		System.out.println( "fertig in " + Formatter.formatTimeUnit( System.currentTimeMillis() - interStart, TimeUnits.MilliSeconds ) );

		//System.out.println( "TRANSIT-TIMES-FERTIG " + (System.currentTimeMillis() - startTT) );
	}

	// i,j stehen fuer das eigentliche square, nicht fuer den nachbarn!
	private static boolean isRightSquareBlocked( ZToGraphRoomRaster room, int i, int j, boolean careForDelayAreas, boolean careForAssignmentAreas ) {
		int numOfColumns = room.getColumnCount();
		int numOfRows = room.getRowCount();

		if( i >= numOfColumns - 1 )
			return true;
		if( j >= numOfRows )
			return true;

		ZToGraphRasterSquare square = room.getSquare( i, j );
		if( square.isBlocked( Direction.Right ) )
			return true;

		ZToGraphRasterSquare right = room.getSquare( i + 1, j );

		if( right.isInaccessible() )
			return true;
		if( right.isMarked() )
			return true;

		if( careForDelayAreas )
			if( square.getSpeedFactor() != right.getSpeedFactor() )
				return true;

		if( careForAssignmentAreas )
			if( square.isSource() != right.isSource() )
				return true;

		// a node is save or not but not both
		if( square.isSave() != right.isSave() )
			return true;

		// Only squares from stairs with the same up and down speedfactor may be in the same node
		// (or squares that are not in a stair with stairs that are also not in a stair)
		if( square.getUpSpeedFactor() != right.getUpSpeedFactor() )
			return true;
		if( square.getDownSpeedFactor() != right.getDownSpeedFactor() )
			return true;

		return false;
	}

	private static boolean isDownSquareBlocked( ZToGraphRoomRaster room, int i, int j, boolean careForDelayAreas, boolean careForAssignmentAreas ) {
		int numOfColumns = room.getColumnCount();
		int numOfRows = room.getRowCount();

		if( i >= numOfColumns )
			return true;
		if( j >= numOfRows - 1 )
			return true;

		ZToGraphRasterSquare square = room.getSquare( i, j );
		if( square.isBlocked( Direction.Down ) )
			return true;

		ZToGraphRasterSquare down = room.getSquare( i, j + 1 );

		if( down.isInaccessible() )
			return true;
		if( down.isMarked() )
			return true;

		if( careForDelayAreas )
			if( square.getSpeedFactor() != down.getSpeedFactor() )
				return true;

		if( careForAssignmentAreas )
			if( square.isSource() != down.isSource() )
				return true;

		// a node is save or not but not both
		if( square.isSave() != down.isSave() )
			return true;

		// Only squares from stairs with the same up and down speedfactor may be in the same node
		// (or squares that are not in a stair with stairs that are also not in a stair)
		if( square.getUpSpeedFactor() != down.getUpSpeedFactor() )
			return true;
		if( square.getDownSpeedFactor() != down.getDownSpeedFactor() )
			return true;

		return false;
	}

	/**
	 * 
	 * @param node
	 * @param squareList
	 * @return 
	 */
	private static PlanPoint calculateCentre( Node node, List<ZToGraphRasterSquare> squareList ) {
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
		for( ZToGraphRasterSquare square : squareList )
			if( node.id() == square.getNode().id() ) {
				if( square.getXOffset() <= nodeUpperLeftX )
					nodeUpperLeftX = square.getXOffset();
				if( square.getYOffset() <= nodeUpperLeftY )
					nodeUpperLeftY = square.getYOffset();
				if( square.getXOffset() >= nodeLowerRightX )
					nodeLowerRightX = square.getXOffset();
				if( square.getYOffset() >= nodeLowerRightY )
					nodeLowerRightY = square.getYOffset();
			}
		// adapt to rectangle corner coordinates
		nodeLowerRightX += 400;
		nodeLowerRightY += 400;
		// calculate the centre-coordinates of the start-node-rectangle
		nodeBreadth = Math.abs( nodeLowerRightX - nodeUpperLeftX );
		nodeHeight = Math.abs( nodeLowerRightY - nodeUpperLeftY );
		nodeCentreX = (int) Math.round( 0.5 * nodeBreadth ) + nodeUpperLeftX;
		nodeCentreY = (int) Math.round( 0.5 * nodeHeight ) + nodeUpperLeftY;

		PlanPoint point = new PlanPoint( nodeCentreX, nodeCentreY );
		return point;
	}

	private static PlanPoint calculateCentre( ZToGraphRasterSquare square ) {
		int squareCentreX, squareCentreY;

		squareCentreX = square.getXOffset() + 200;
		squareCentreY = square.getYOffset() + 200;

		PlanPoint point = new PlanPoint( squareCentreX, squareCentreY );
		return point;
	}

	private static int calculateDistance( PlanPoint start, PlanPoint end ) {
		int distance;
		int distanceX, distanceY;
		int startX = start.getXInt();
		int startY = start.getYInt();
		int endX = end.getXInt();
		int endY = end.getYInt();

		distanceX = Math.abs( startX - endX );
		distanceY = Math.abs( startY - endY );

		distance = (int) Math.round( Math.sqrt( Math.pow( distanceX, 2 ) + Math.pow( distanceY, 2 ) ) );
		return distance;
	}

	private Hashtable<Edge, ArrayList<ZToGraphRasterSquare>> connectRooms() {
		System.out.print( "Connect rooms... " );
		ZToGraphMapping mapping = model.getZToGraphMapping();

		Hashtable<Edge, ArrayList<ZToGraphRasterSquare>> table =
						new Hashtable<Edge, ArrayList<ZToGraphRasterSquare>>();


		//Two mappings to store capacities
		//just temporally.
		Collection<ZToGraphRasteredDoor> doors = raster.getDoors();
		DynamicNetwork graph = model.getDynamicNetwork();
		IdentifiableIntegerMapping<Edge> edgesCap = model.getEdgeCapacities();// new IdentifiableIntegerMapping<Edge>(graph.numberOfEdges());
		if( edgesCap == null ) {
			edgesCap = new IdentifiableIntegerMapping<Edge>( graph.numberOfEdges() );
			model.setEdgeCapacities( edgesCap );
		}

		int nextEdge = graph.numberOfEdges();
		for( ZToGraphRasteredDoor door : doors ) {
			Node firstNode = door.getFirstDoorPart().getNode();
			Node secondNode = door.getSecondDoorPart().getNode();

			Edge edge = graph.getEdge( firstNode, secondNode );
			if( edge == null ) {
				edge = new Edge( nextEdge++, firstNode, secondNode );
				graph.addEdge( edge );
				edgesCap.setDomainSize( edgesCap.getDomainSize() + 1 );
				edgesCap.set( edge, 0 );
				mapping.setEdgeLevel( edge, Level.Equal );

			}
			edgesCap.increase( edge, 1 * FACTOR );
			//store squares in the squares list of the door-edge
			ArrayList<ZToGraphRasterSquare> list = table.get( edge );
			if( list == null ) {
				list = new ArrayList<ZToGraphRasterSquare>();
				table.put( edge, list );
			}


			//retrieve the squares to store them in the list if they are not already in it
			ZToGraphRasterSquare square = door.getFirstDoorPart();
			if( !list.contains( square ) )
				list.add( square );
			square = door.getSecondDoorPart();
			if( !list.contains( square ) )
				list.add( square );

		}//end for each door loop

		//Connect the super source withh all other sources
		Node supersink = model.getSupersink();


		if( supersink == null )
			return table;

		List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();
		for( ZToGraphRoomRaster room : rasteredRooms ) {

			int colCount = room.getColumnCount();
			int rowCount = room.getRowCount();

			for( int row = 0; row < rowCount; row++ )
				for( int col = 0; col < colCount; col++ ) {
					ZToGraphRasterSquare square = room.getSquare( col, row );

					if( square.isSave() ) {
						Node node = square.getNode();
						Edge edge = graph.getEdge( node, supersink );
						if( edge == null ) {
							edge = new Edge( nextEdge++, node, supersink );
							graph.addEdge( edge );
							mapping.setEdgeLevel( edge, Level.Equal );
						}
						edgesCap.set( edge, Integer.MAX_VALUE );
					}// end if safe
				}//end outer loop
		}
		System.out.println( "fertig" );
		return table;
	}
}
