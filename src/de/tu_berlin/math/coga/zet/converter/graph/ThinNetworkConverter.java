package de.tu_berlin.math.coga.zet.converter.graph;

import de.zet_evakuierung.model.FloorInterface;
import org.zetool.common.debug.Debug;
import org.zetool.common.util.Level;
import org.zetool.math.vectormath.Vector2;
import ds.PropertyContainer;
import org.zetool.container.collection.ListSequence;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import ds.graph.NodeRectangle;
import org.zetool.container.mapping.IdentifiableObjectMapping;
import org.zetool.math.geom.ArbitraryRectangle;
import org.zetool.math.geom.Rectangle;
import de.zet_evakuierung.model.Area;
import de.zet_evakuierung.model.AssignmentArea;
import de.zet_evakuierung.model.DelayArea;
import de.zet_evakuierung.model.EvacuationArea;
import de.zet_evakuierung.model.InaccessibleArea;
import de.zet_evakuierung.model.PlanPoint;
import de.zet_evakuierung.model.PlanPolygon;
import de.zet_evakuierung.model.Room;
import de.zet_evakuierung.model.RoomEdge;
import de.zet_evakuierung.model.StairArea;
import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A network converter creating a small graph (compared to
 * {@link RectangleConverter}) that is mostly built on semantics of the building
 * structure.
 *
 * @author Marlen Schwengfelder
 * @author Jan-Philipp Kappmeier
 */
public class ThinNetworkConverter extends BaseZToGraphConverter {

  //maps the node representing the door to corresponding room
  private HashMap<Room, Node> doorNodeForRoom = new HashMap<>();
  //maps the center node for a room to a specific room
  private HashMap<Room, Node> centerNodeForRoom = new HashMap<>();
  //maps all the other nodes that represent the room to it
  private HashMap<Room, ListSequence<Node>> nodesForRoom = new HashMap<>();
  //maps all the door nodes for a specific room to it
  private HashMap<Room, Collection<PositionNode>> doorNodesForRoom = new HashMap<>();
  private Collection<PositionNode> doorNodes;
  //...
  private HashMap<Room, Collection<PositionNode>> floorNodesForRoom = new HashMap<>();
  private Collection<PositionNode> floorNodes;
  // TODO identifiable boolean mapping
  private IdentifiableObjectMapping<Node, Boolean> isfloorNode = new IdentifiableObjectMapping<>( 1 );
  //stores the associated room of a floorNode
  private HashMap<Node, Room> linkTarget = new HashMap<>();
  private HashMap<Point, Integer> floorDoors = new HashMap<>();
  //stores all nodes for the room (except the door node)
  private ListSequence<Node> nodes;
  //maps the names of the neighoured rooms to each room
  private HashMap<Room, Collection<Room>> neighbourRooms = new HashMap<>();
  //stores all neighbours for a room
  private Collection<Room> neighbours;

  /**
   * The number of neighbouring rooms for each room.
   */
  private final HashMap<Room, Integer> numberNeighborRooms = new HashMap<>();
  //stores the evacuationNode for a room
  private HashMap<Room, ListSequence<Node>> EvacuationNodes = new HashMap<>();

  /**
   * Nodes representing evacuation area, that means sources.
   */
  private ListSequence<Node> evacuationNodes;

//stores the assignment nodes for a room
  private HashMap<Room, ListSequence<Node>> AssignmentNodes = new HashMap<>();
  private ListSequence<Node> AssignNodes;
  private HashMap<Area, List<PositionNode>> NodesForAssignArea = new HashMap<>();
  private List<PositionNode> AssignAreaNodes;
  private HashMap<Area, List<Integer>> ValuesForAssignArea = new HashMap<>();
  private List<Integer> AssignValues;
  private int[][] used;
  //stores number of evacuation areas
  private int numEvac2, num;
  private Room MoreDoorRoom, EvacRoom;

  //position Nodes to remember which rooms are connected by the given node
  private HashMap<Point, Node> connection = new HashMap<>();
  //stores for each edge the corresponding room
  private HashMap<Room, ListSequence<Edge>> EdgesForRoom = new HashMap<>();
  private ListSequence<Edge> RoomEdges;
  //stores all doors that do not get a node (only if one of the corresponding rooms has only one door and is empty)
  private HashMap<FloorInterface, List<Point>> needsNoConnection = new HashMap<>();
  private Map<Node, Rectangle> coveredArea;
  //stores the associated room according to a connecting edge over floors
  private HashMap<Room, Room> FloorConnection = new HashMap<>();

  private HashMap<Room, List<Point>> smallRecForRoom = new HashMap<>();

  /**
   * Defines precision of created nodes for assignment areas, gives the max.
   * number of persons that can be assigned to one node.
   */
  private int AssignPrecision = 50;
  private final static boolean debug = false;

  @Override
  protected void createNodes() {
    System.out.println( "Create Nodes for thin Network... " );
    List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();
    coveredArea = new HashMap<>();

    computeNeighbors();

    Node supersink = model.getSupersink();

    model.setNodeCapacity( supersink, Integer.MAX_VALUE );

    mapping.setNodeSpeedFactor( supersink, 1 );
    mapping.setNodeRectangle( supersink, new NodeRectangle( 0, 0, 0, 0 ) );
    mapping.setFloorForNode( supersink, -1 );

    for( ZToGraphRoomRaster room : rasteredRooms ) {
      Logger log = Debug.globalLogger;
      log.log( java.util.logging.Level.FINE, "Currently considered room: {0}on floor: {1}", new Object[]{room.getRoom().getName(), room.getRoom().getAssociatedFloor()} );
      Room ZRoom = room.getRoom();

      doorNodes = new HashSet<>();
      evacuationNodes = new ListSequence<>();
      AssignNodes = new ListSequence<>();
      nodes = new ListSequence<>();
      floorNodes = new HashSet<>();

      ComputeNeighbourRoomValues( ZRoom );

      //create Node for each Assignment Area in these rooms
      for( AssignmentArea area : ZRoom.getAssignmentAreas() ) {
        //if there can be more than (AssignPrecision) persons
        if( area.getHeight() * area.getWidth() > AssignPrecision * room.getRaster() * room.getRaster() ) {
          AssignAreaNodes = new LinkedList<>();
          AssignValues = new LinkedList<>();
          int NodePrecisionWidth = (int) Math.floor( (int) (area.getWidth() / Math.sqrt( AssignPrecision * room.getRaster() * room.getRaster() )) );
          if( NodePrecisionWidth == 0 ) {
            NodePrecisionWidth = 1;
          }
          int NodePrecisionHeight = (int) Math.floor( (int) (area.getHeight() / Math.sqrt( AssignPrecision * room.getRaster() * room.getRaster() )) );
          if( NodePrecisionHeight == 0 ) {
            NodePrecisionHeight = 1;
          }
          AssignValues.add( NodePrecisionWidth );
          AssignValues.add( NodePrecisionHeight );
          int width = area.getWidth() / NodePrecisionWidth;
          int height = area.getHeight() / NodePrecisionHeight;

          int size = room.getRaster();
          int numCol = (int) Math.ceil( ((double) width / size) );
          int numRow = (int) Math.ceil( ((double) height / size) );

          for( int i = 0; i < NodePrecisionWidth; i++ ) {
            int startCol = (area.getxOffset() - room.getXOffset()) / size + i * numCol;

            for( int j = 0; j < NodePrecisionHeight; j++ ) {
              int startRow = (area.getyOffset() - room.getYOffset()) / size + j * numRow;
              Node n = model.newNode();
              if( debug ) {
                System.out.println( "Precise Assignment Node for area: " + n );
              }
              model.setNodeCapacity( n, Integer.MAX_VALUE );
              model.getZToGraphMapping().getNodeFloorMapping().set( n, getProblem().getFloorID( room.getFloor() ) );
              model.getZToGraphMapping().setDeletedSourceNode( n, false );
              model.addSource( n );
              model.addSource( n );
              NodeRectangle r = new NodeRectangle( area.getxOffset() + i * width, -(area.getyOffset() + j * height), area.getxOffset() + ((i + 1) * width), -(area.getyOffset() + ((j + 1) * height)) );
              mapping.setNodeRectangle( n, r );
              Point point = new Point( (int) r.getCenterX(), -((int) r.getCenterY()) );
              PositionNode p = new PositionNode( n, point );
              AssignAreaNodes.add( p );

              for( int k = startCol; k < startCol + numCol; k++ ) {
                for( int l = startRow; l < startRow + numRow; l++ ) {
                  if( k < room.getColumnCount() && l < room.getRowCount() ) {
                    ZToGraphRasterSquare square = room.getSquare( k, l );
                    square.mark();
                    square.setNode( n );
                  }
                }
              }
            }
          }
          NodesForAssignArea.put( area, AssignAreaNodes );
          ValuesForAssignArea.put( area, AssignValues );
        } else {
          //Node node = model.newNode(nodeCount++);
          Node node = model.newNode();
          if( debug ) {
            System.out.println( "One AssignmentNode: " + node + " in Room: " + ZRoom.getName() );
          }
          model.setNodeCapacity( node, Integer.MAX_VALUE );
          NodeRectangle rec = new NodeRectangle( area.getxOffset(), -area.getyOffset(), area.getxOffset() + area.getWidth(), -(area.getyOffset() + area.getHeight()) );
          mapping.setNodeRectangle( node, rec );
          model.getZToGraphMapping().getNodeFloorMapping().set( node, getProblem().getFloorID( room.getFloor() ) );
          model.getZToGraphMapping().setDeletedSourceNode( node, false );
          AssignNodes.add( node );
          //map raster squares to assignment node (for ConvertConcreteAssignment()...)
          int size = room.getRaster();
          int startCol = (area.getxOffset() - room.getXOffset()) / size;
          int startRow = (area.getyOffset() - room.getYOffset()) / size;
          int numCol = (int) Math.ceil( ((double) area.getWidth() / size) );
          int numRow = (int) Math.ceil( ((double) area.getHeight() / size) );

          for( int i = startCol; i < startCol + numCol; i++ ) {
            for( int j = startRow; j < startRow + numRow; j++ ) {
              if( i < room.getColumnCount() && j < room.getRowCount() ) {
                ZToGraphRasterSquare square = room.getSquare( i, j );
                square.mark();
                square.setNode( node );
                //System.out.println("Square in ThinNetwork: " + square.toString());
              }
            }
          }
        }
      }
      floorDoors.putAll( getFloorDoors( ZRoom ) );
      for( EvacuationArea Earea : ZRoom.getEvacuationAreas() ) {
        //if room has only one neighbour, create Evacuation node near to door
        if( numberNeighborRooms.get( ZRoom ) == 1 ) {
          Set<Point> poss = new HashSet<>();
          for( Point p1 : floorDoors.keySet() ) {
            poss.add( p1 );
          }
          for( Point p : ZRoom.getDoors().keySet() ) {
            poss.add( p );
          }
          for( Point p : poss ) {
            double mindist = Double.MAX_VALUE;
            Point min = null;
            for( PlanPoint pp : Earea.getPlanPoints() ) {
              double dist = p.distance( pp );// calculateDistance( p, pp );
              if( dist < mindist ) {
                mindist = dist;
                min = pp;
              }
            }
            Node node = model.newNode();
            if( debug ) {
              System.out.println( "near EvacuationNode: " + node + " in room: " + ZRoom.getName() );
            }
            int Evalue = Earea.getMaxEvacuees();
            model.setNodeCapacity( node, Evalue );
            NodeRectangle rec = new NodeRectangle( min.x - 1, -(min.y - 1), min.x + 1, -(min.y + 1) );
            mapping.setNodeRectangle( node, rec );
            model.getZToGraphMapping().getNodeFloorMapping().set( node, getProblem().getFloorID( room.getFloor() ) );
            model.getZToGraphMapping().setDeletedSourceNode( node, false );
            evacuationNodes.add( node );
          }
        } else {
          Node node = model.newNode();;
          if( debug ) {
            System.out.println( "EvacuationNode: " + node + " in room: " + ZRoom.getName() );
          }
          int Evalue = Earea.getMaxEvacuees();
          model.setNodeCapacity( node, Evalue );
          NodeRectangle rec = new NodeRectangle( Earea.getxOffset(), -Earea.getyOffset(), Earea.getxOffset() + Earea.getWidth(), -(Earea.getyOffset() + Earea.getHeight()) );
          mapping.setNodeRectangle( node, rec );
          model.getZToGraphMapping().getNodeFloorMapping().set( node, getProblem().getFloorID( room.getFloor() ) );
          model.getZToGraphMapping().setDeletedSourceNode( node, false );
          evacuationNodes.add( node );
        }
      }

      //create door nodes for rooms with only one neighbour
      //if there is at least one evacuation or assignment area, create a node representing the doors
      if( (!(ZRoom.getEvacuationAreas().isEmpty() && ZRoom.getAssignmentAreas().isEmpty()) && (numberNeighborRooms.get( ZRoom ) == 1)) || (numberNeighborRooms.get( ZRoom ) == 1 && (floorNodesForRoom.containsKey( ZRoom ))) ) {
        HashMap<Point, Integer> doors = ZRoom.getDoors();
        if( !doors.isEmpty() ) {
          for( Point p : doors.keySet() ) {
            //create node for door
            Node node = model.newNode();
            //Node node = model.newNode(nodeCount);
            if( debug ) {
              System.out.println( "Door Node: " + node + " for room: " + ZRoom.getName() + "with 1 neighbour" );
            }
            PositionNode pos = new PositionNode( node, p, doors.get( p ) );
            doorNodes.add( pos );
            //length of door is returned in mm, Observation: 0.5 meter/person
            int width = (int) Math.floor( ((double) ZRoom.getLengthOfDoor( ZRoom )) / 1000.0 ) * 2;
            //System.out.println("Knotenkap: " + width);
            model.setNodeCapacity( node, width );
            NodeRectangle rec = new NodeRectangle( p.x - 1, -(p.y + 1), p.x + 1, -(p.y - 1) );
            mapping.setNodeRectangle( node, rec );
            model.getZToGraphMapping().getNodeFloorMapping().set( node, getProblem().getFloorID( room.getFloor() ) );
            model.getZToGraphMapping().setDeletedSourceNode( node, false );
          }
        }
      }

      //create nodes for rooms with more than one neighbour
      if( numberNeighborRooms.get( ZRoom ) > 1 ) {
        //creating door nodes
        HashMap<Point, Integer> doors = ZRoom.getDoors();
        for( Point p : doors.keySet() ) {
          if( needsNoConnection.containsKey( ZRoom.getAssociatedFloor() ) ) {
            //only create door node if rooms with one door is not empty or is empty and has a floor connecting node
            if( !(needsNoConnection.get( ZRoom.getAssociatedFloor() ).contains( p )) ) {
              //Node node = model.newNode(nodeCount);
              Node node = model.newNode();
              connection.put( p, node );
              model.setNodeCapacity( node, ZRoom.getPolygon().getMaxEvacuees() );
              if( debug ) {
                System.out.println( "Door Node: " + node + "at pos: " + p + "for room" + ZRoom.getName() );
              }
              PositionNode pos = new PositionNode( node, p, doors.get( p ) );
              doorNodes.add( pos );
              NodeRectangle rec = new NodeRectangle( p.x - 1, -(p.y + 1), p.x + 1, -(p.y - 1) );
              mapping.setNodeRectangle( node, rec );
              model.getZToGraphMapping().getNodeFloorMapping().set( node, getProblem().getFloorID( room.getFloor() ) );
              model.getZToGraphMapping().setDeletedSourceNode( node, false );
            }
          } else {
            Node node = model.newNode();
            connection.put( p, node );
            model.setNodeCapacity( node, ZRoom.getPolygon().getMaxEvacuees() );
            if( debug ) {
              System.out.println( "Door Node: " + node + "at pos: " + p + "for room" + ZRoom.getName() );
            }
            PositionNode pos = new PositionNode( node, p, doors.get( p ) );
            doorNodes.add( pos );
            NodeRectangle rec = new NodeRectangle( p.x - 1, -(p.y + 1), p.x + 1, -(p.y - 1) );
            mapping.setNodeRectangle( node, rec );
            model.getZToGraphMapping().getNodeFloorMapping().set( node, getProblem().getFloorID( room.getFloor() ) );
            model.getZToGraphMapping().setDeletedSourceNode( node, false );
          }
        }

        //only neighbours with one neighbour, Case 2b)
        //one evacuation node inside room and one in neighbourrooms
        if( numEvac2 == 1 && num == 0 && ZRoom.getEvacuationAreas().size() == 1 ) {
          SmallestRectangle rectangle = FindSmallestRectangle( room );
          double width = rectangle.width();
          double length = rectangle.height();
          //room is nearly rectangular
          if( (width > 2 * length) || (length > 2 * width) ) {
            if( debug ) {
              System.out.println( "Case 2b)" );
            }
            FindRectangulationNodes( room, rectangle );
          } else {
            if( debug ) {
              System.out.println( "Create center1 for room: " + ZRoom.getName() );
            }
            //create node between two evacuation areas
            for( Point p : EvacRoom.getDoors().keySet() ) {
              Point q = new Point( p.x, -p.y );
              createCenterNode( ZRoom, q, evacuationNodes.first() );
            }
          }
        } //exactly one room with more than one neighbour, no evacuation areas in rooms with one neighbour
        else if( num == 1 && numEvac2 == 0 ) {
          if( ZRoom.getEvacuationAreas().size() == 1 ) {
            //node between 2 evacuation areas
            if( debug ) {
              System.out.println( "Create node between 2 evacuation areas for room: " + ZRoom.getName() );
            }
            Collection<de.zet_evakuierung.model.PlanEdge> doors1 = MoreDoorRoom.getDoorEdges();
            Collection<de.zet_evakuierung.model.PlanEdge> doors2 = ZRoom.getDoorEdges();
            Point p = new Point();
            for( de.zet_evakuierung.model.PlanEdge edge : doors1 ) {
              for( de.zet_evakuierung.model.PlanEdge edge1 : doors2 ) {
                boolean first = edge.getSource().x == edge1.getSource().x;
                boolean second = edge.getSource().y == edge1.getSource().y;
                boolean third = edge.getTarget().x == edge1.getTarget().x;
                boolean fourth = edge.getTarget().y == edge1.getTarget().y;

                //source in one edge is target for other source
                boolean first1 = edge.getSource().x == edge1.getTarget().x;
                boolean second1 = edge.getSource().y == edge1.getTarget().y;
                boolean third1 = edge.getTarget().x == edge1.getSource().x;
                boolean fourth1 = edge.getTarget().y == edge1.getSource().y;

                if( (first && second && third && fourth) || (first1 && second1 && third1 && fourth1) ) {
                  int x = (edge.getMaxX() + edge.getMinX()) / 2;
                  int y = -((edge.getMaxY() + edge.getMinY()) / 2);
                  Point point = new Point( x, y );
                  p.setLocation( point );
                  createCenterNode( ZRoom, p, evacuationNodes.first() );
                }
              }
            }
          }
        } //only neighbours with one neighbour and one evacuation area inside
        else if( num == 0 && numEvac2 == 1 ) {
          System.out.println( "new case: " );
        } //more than one room with more neighbours or more than one evacuation area in neighbouring rooms
        else {
          if( debug ) {
            System.out.println( "Trivial Node Creation.." + ZRoom.getName() );
          }
          SmallestRectangle rectangle = FindSmallestRectangle( room );
          double width = rectangle.width();
          double length = rectangle.height();
          if( debug ) {
            System.out.println( "width: " + width + " length: " + length );
          }
          //room is nearly rectangular
          if( (width > 2 * length) || (length > 2 * width) ) {
            FindRectangulationNodes( room, rectangle );
          } else {
            Point c = new Point( (int) rectangle.getCenter().getX(), (int) rectangle.getCenter().getY() );
            CreateCenterForRoom( room, rectangle, c );
          }
        }
      }

      //stores all the constructed nodes for one room (except door node, evacuation and assignment nodes)
      nodesForRoom.put( ZRoom, nodes );
      for( Node n : nodes ) {
        isfloorNode.set( n, Boolean.FALSE );
      }
      EvacuationNodes.put( ZRoom, evacuationNodes );
      for( Node n : evacuationNodes ) {
        isfloorNode.set( n, Boolean.FALSE );
      }
      AssignmentNodes.put( ZRoom, AssignNodes );
      for( Node n : AssignNodes ) {
        isfloorNode.set( n, Boolean.FALSE );
      }
      doorNodesForRoom.put( ZRoom, doorNodes );
      for( PositionNode n : doorNodes ) {
        if( !floorNodes.contains( n ) ) {
          isfloorNode.set( n.getNode(), Boolean.FALSE );
        }
      }
      floorNodesForRoom.put( ZRoom, floorNodes );
      for( PositionNode n : floorNodes ) {
        isfloorNode.set( n.getNode(), true );
      }
    } //end for all rastered rooms
  }

  private Edge createEdge( Node from, Node to, int capacity ) {
    Edge edge = model.newEdge( from, to );
    mapping.setEdgeLevel( edge, Level.Equal );
    model.setEdgeCapacity( edge, capacity );
    return edge;
  }

  
  @Override
  protected void createEdgesAndCapacities() {
    System.out.println( "Set up edges and compute capacities... " );
    int numNodes = model.numberOfNodes();

    used = new int[numNodes][numNodes];
    for( int i = 0; i < numNodes; i++ ) {
      for( int j = 0; j < numNodes; j++ ) {
        used[i][j] = 0;
        used[j][i] = 0;
      }
    }
    ZToGraphMapping mapping = model.getZToGraphMapping();
    List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();

    //mapping to store edge capacities
    for( ZToGraphRoomRaster room : rasteredRooms ) {
      if( debug ) {
        System.out.println( "Currently considered room: " + room.getRoom().getName() + "on floor: " + room.getRoom().getAssociatedFloor() );
      }
      Room ZRoom = room.getRoom();
      ComputeNeighbourRoomValues( ZRoom );
      RoomEdges = new ListSequence<>();
      //create Edge between evacuation nodes and super sink
      Node supersink = model.getSupersink();

      for( Node evacNode : EvacuationNodes.get( room.getRoom() ) ) {
        Node node1 = evacNode;
        createEdge( node1, supersink, Integer.MAX_VALUE );
      }
      Collection<Room> neighbRooms = neighbourRooms.get( ZRoom );
      //connect the room with all of its neighbours
      ConnectRooms( ZRoom );
      //connects the different floors
      ConnectFloors( ZRoom );

      //connect nodes of assignment areas
      for( AssignmentArea a : ZRoom.getAssignmentAreas() ) {
        if( NodesForAssignArea.containsKey( a ) ) {
          int numWidth = ValuesForAssignArea.get( a ).get( 0 );
          int numHeight = ValuesForAssignArea.get( a ).get( 1 );
          for( int i = 0; i < (numWidth * numHeight) - 1; i++ ) {
            Node first = NodesForAssignArea.get( a ).get( i ).getNode();
            Node second = NodesForAssignArea.get( a ).get( i + 1 ).getNode();
            if( ((i + 1) % numHeight) != 0 ) {
              createEdge( first, second, Integer.MAX_VALUE );
            }
            if( i < (numWidth * numHeight) - numHeight ) {
              Node hor = NodesForAssignArea.get( a ).get( i + numHeight ).getNode();
              createEdge( first, hor, Integer.MAX_VALUE );
            }
          }
        }
      }

      //room has no neighbours
      if( ZRoom.getDoorEdges().isEmpty() ) {
        //connect evacuation and assignment nodes directly
        if( ZRoom.getEvacuationAreas().size() > 0 ) {
          for( Node n1 : AssignmentNodes.get( ZRoom ) ) {
            for( Node n2 : EvacuationNodes.get( ZRoom ) ) {
              // TODO
              //set the capacity of an edge to the max. number of persons(in this room) divided by number of assignment areas
              int cap = 2;
              Edge e = createEdge( n1, n2, cap );
              RoomEdges.add( e );
            }
          }

          for( AssignmentArea area : ZRoom.getAssignmentAreas() ) {
            if( NodesForAssignArea.containsKey( area ) ) {
              for( Node n2 : EvacuationNodes.get( ZRoom ) ) {
                Point p = new Point( (int) mapping.getNodeRectangles().get( n2 ).getCenterX(), -(int) mapping.getNodeRectangles().get( n2 ).getCenterY() );
                PositionNode evac = new PositionNode( n2, p );
                List<PositionNode> nearest = FindNearestAssignNodes( NodesForAssignArea.get( area ), evac );
                for( PositionNode near : nearest ) {
                  Edge e = createEdge( n2, near.getNode(), Integer.MAX_VALUE );
                  RoomEdges.add( e );
                }
              }
            }
          }
        }
      }
      //room has only one neighbour
      if( numberNeighborRooms.get( ZRoom ) == 1 ) {
        if( debug ) {
          System.out.println( "Case 0 for Room: " + ZRoom.getName() );
        }
        Collection<Node> nodes = new HashSet<>();
        if( !AssignmentNodes.get( ZRoom ).isEmpty() ) {
          for( Node node : AssignmentNodes.get( ZRoom ) ) {
            nodes.add( node );
          }
        }
        if( !EvacuationNodes.get( ZRoom ).isEmpty() ) {
          for( Node node : EvacuationNodes.get( ZRoom ) ) {
            nodes.add( node );
          }
        }

        for( AssignmentArea area : ZRoom.getAssignmentAreas() ) {
          if( NodesForAssignArea.containsKey( area ) ) {
            for( PositionNode n2 : doorNodesForRoom.get( ZRoom ) ) {
              List<PositionNode> nearest = FindNearestAssignNodes( NodesForAssignArea.get( area ), n2 );
              for( PositionNode near : nearest ) {
                Edge edge = createEdge( n2.getNode(), near.getNode(), Integer.MAX_VALUE );
                RoomEdges.add( edge );
              }
            }
          }
        }

        //ConnectWithCertainNode(ZRoom, nodes, DoorNodeForRoom.get(ZRoom) );
        for( PositionNode n : doorNodesForRoom.get( ZRoom ) ) {
          ConnectWithCertainNode( ZRoom, nodes, n.getNode() );
        }

        if( (!ZRoom.getEvacuationAreas().isEmpty()) && (!ZRoom.getAssignmentAreas().isEmpty()) ) {
          for( Node node1 : EvacuationNodes.get( ZRoom ) ) {
            for( Node node2 : AssignmentNodes.get( ZRoom ) ) {
              //connect evacuation area and assignment area in one room directly
              Edge edge = createEdge( node1, node2, Integer.MAX_VALUE );
              RoomEdges.add( edge );
            }
          }
        }
        if( ZRoom.getAssignmentAreas().isEmpty() && ZRoom.getEvacuationAreas().isEmpty() && (!floorNodesForRoom.get( ZRoom ).isEmpty()) ) {
          Collection<PositionNode> flnodes = floorNodesForRoom.get( ZRoom );
          Collection<PositionNode> doornodes = doorNodesForRoom.get( ZRoom );
          for( PositionNode n1 : flnodes ) {
            for( PositionNode n2 : doornodes ) {
              if( !(flnodes.contains( n2 )) ) {
                Edge edge = createEdge( n1.getNode(), n2.getNode(), Integer.MAX_VALUE );
                RoomEdges.add( edge );
              }
            }
          }
        }
      } else if( numberNeighborRooms.get( ZRoom ) > 1 ) {
        //all neighbours have only one door and no evacuation areas
        if( numEvac2 == 0 && num == 0 ) {
          if( debug ) {
            System.out.println( "Case 1 for Room: " + ZRoom.getName() );
          }
          if( ZRoom.getEvacuationAreas().isEmpty() ) {
            System.out.println( "there is no reachable evacuation area for room: " + ZRoom.getName() );
          } //connect only evacuation node with doors
          else if( ZRoom.getEvacuationAreas().size() == 1 ) {
            Collection<Node> DoorNodes = new HashSet<>();
            Node evac = EvacuationNodes.get( ZRoom ).first();

            for( PositionNode n : doorNodesForRoom.get( ZRoom ) ) {
              DoorNodes.add( n.getNode() );
            }
            ConnectWithCertainNode( ZRoom, DoorNodes, evac );
          }

        } //there is one evacuationNode in a neighbour room and no one in room itself
        //connect all doors with the door of evacuation room
        else if( numEvac2 == 1 && num == 0 && ZRoom.getEvacuationAreas().isEmpty() ) {
          System.out.println( "EvacRoom" + EvacRoom );
          System.out.println( "Yes" );
          //connect all door Nodes with door Node of evacuation room
          for( PositionNode n : doorNodesForRoom.get( EvacRoom ) ) {
            for( Room r : neighbRooms ) {
              if( r != EvacRoom ) {
                for( PositionNode door2 : doorNodesForRoom.get( r ) ) {
                  if( door2 != null ) {
                    Edge edge = createEdge( n.getNode(), door2.getNode(), Integer.MAX_VALUE );
                    RoomEdges.add( edge );
                  }
                }
              }
            }
            //connect all assignmentnodes with door node of evacuation room
            for( AssignmentArea area : ZRoom.getAssignmentAreas() ) {
              if( NodesForAssignArea.containsKey( area ) ) {
                List<PositionNode> nearest = FindNearestAssignNodes( NodesForAssignArea.get( area ), n );
                for( PositionNode near : nearest ) {
                  Edge edge = createEdge( n.getNode(), near.getNode(), Integer.MAX_VALUE );
                  RoomEdges.add( edge );
                }
              }
            }
            if( !AssignmentNodes.get( ZRoom ).isEmpty() ) {
              for( Node n2 : AssignmentNodes.get( ZRoom ) ) {
                Edge edge = createEdge( n.getNode(), n2, Integer.MAX_VALUE );
                RoomEdges.add( edge );
              }
            }
          }

        } //}
        //one neighbouring evacuation area and one evacuation area inside (Case 2b)
        else if( numEvac2 == 1 && num == 0 && ZRoom.getEvacuationAreas().size() == 1 ) {
          if( debug ) {
            System.out.println( "Case 3 for Room: " + ZRoom.getName() );
          }
          SmallestRectangle rectangle = FindSmallestRectangle( room );
          double width = rectangle.width();
          double length = rectangle.height();
          if( debug ) {
            System.out.println( "width: " + width + "length: " + length );
          }
          //room is rectangular
          if( (width > 2 * length) || (length > 2 * width) ) {
            System.out.println( "Call weird method for room " + ZRoom );
            FindRectangulationEdges( ZRoom );
          } //room is more quadratic
          else {
            if( debug ) {
              System.out.println( "quadratic room" );
            }
            Collection<Node> doors = new HashSet<>();
            for( PositionNode n : doorNodesForRoom.get( ZRoom ) ) {
              doors.add( n.getNode() );
            }
            doors.add( EvacuationNodes.get( ZRoom ).first() );
            Node center = centerNodeForRoom.get( ZRoom );
            //connect the center with evacuation area and all neighbour rooms...
            ConnectWithCertainNode( ZRoom, doors, center );
          }

        } //exactly one room with more than one door and no evacuation areas in rooms with only one door
        // connect door nodes directly, Example2.zet
        else if( num == 1 && numEvac2 == 0 && ZRoom.getEvacuationAreas().isEmpty() ) {
          if( debug ) {
            System.out.println( "Case 4a) for room: " + ZRoom.getName() );
          }
          Collection<Node> coll = new HashSet<>();
          Node want = null;
          for( PositionNode n1 : doorNodesForRoom.get( MoreDoorRoom ) ) {
            if( debug ) {
              System.out.println( "Door Node: " + n1.getNode() + "at pos: " + n1.getPosition() );
            }
            for( PositionNode n2 : doorNodesForRoom.get( ZRoom ) ) {
              if( (n2.getPosition().x == n1.getPosition().x) && (n2.getPosition().y == n1.getPosition().y) ) {
                want = n2.getNode();
              } else {
                coll.add( n2.getNode() );
              }
            }
          }
          coll.remove( want );
          //System.out.println("Coll: " + coll.toString());
          //System.out.println("want: " + want);
          ConnectWithCertainNode( ZRoom, coll, want );
          if( !ZRoom.getAssignmentAreas().isEmpty() ) {
            for( Node n : AssignmentNodes.get( ZRoom ) ) {
              createEdge( n, want, Integer.MAX_VALUE );
            }
          }
        } //exactly one room with more than one door and one evacuation area inside
        else if( num == 1 && numEvac2 == 0 && ZRoom.getEvacuationAreas().size() == 1 ) {
          if( debug ) {
            System.out.println( "Case 4b1) for Room: " + ZRoom.getName() );
          }
          HashMap<Point, Integer> doors1 = MoreDoorRoom.getDoors();
          if( debug ) {
            System.out.println( "MoreDoorRoom: " + MoreDoorRoom.getName() );
          }
          HashMap<Point, Integer> doors2 = ZRoom.getDoors();
          Node center = centerNodeForRoom.get( ZRoom );
          //connect more door room with center
          for( Point p1 : doors1.keySet() ) {
            for( Point p2 : doors2.keySet() ) {
              if( p1.equals( p2 ) ) {
                Node node1 = connection.get( p1 );
                Edge edge = createEdge( node1, center, Integer.MAX_VALUE );
                RoomEdges.add( edge );
              }
            }
          }
          //connect all the other rooms with center
          for( Room r : neighbRooms ) {
            if( r != MoreDoorRoom ) {
              Node door2 = doorNodeForRoom.get( r );
              if( door2 != null ) {
                Edge edge = createEdge( center, door2, Integer.MAX_VALUE );
                RoomEdges.add( edge );
              }
            }
          }
          //connect the evacuation node with center
          for( Node node : EvacuationNodes.get( ZRoom ) ) {
            //TODO: set edge capacity correctly (smaller than infinity..)
            Edge edge = createEdge( center, node, 10 );
            RoomEdges.add( edge );
          }

        } else {
          if( debug ) {
            System.out.println( "Case 5 for Room: " + ZRoom.getName() );
          }
          SmallestRectangle rectangle = FindSmallestRectangle( room );
          double width = rectangle.width();
          double length = rectangle.height();
          if( debug ) {
            System.out.println( "width: " + width + "length: " + length );
          }
          //room is rectangular
          if( (width > 2 * length) || (length > 2 * width) ) {
            FindRectangulationEdges( ZRoom );
          } //room nearly quadratic
          else {
            Node center = centerNodeForRoom.get( ZRoom );
            int center_x = (int) mapping.nodeRectangles.get( center ).getCenterX();
            int center_y = (int) mapping.nodeRectangles.get( center ).getCenterY();
            Point centerPoint = new Point( center_x, center_y );
            Collection<PositionNode> doors = doorNodesForRoom.get( ZRoom );

            if( !EvacuationNodes.get( ZRoom ).isEmpty() ) {
              for( Node node : EvacuationNodes.get( ZRoom ) ) {
                int evac_x = (int) mapping.nodeRectangles.get( node ).getCenterX();
                int evac_y = (int) mapping.nodeRectangles.get( node ).getCenterY();
                Point evacPoint = new Point( evac_x, evac_y );
                //TODO: set edge capacity correctly
                Edge edge = createEdge( node, center, Integer.MAX_VALUE );
                RoomEdges.add( edge );
                for( PositionNode door : doors ) {
                  Point doorPoint = new Point( door.getPosition().x, -door.getPosition().y );
                  //System.out.println("assignPoint " + assignPoint + " door " + doorPoint + " centerPoint " + centerPoint);
                  double dirPath = evacPoint.distance( doorPoint );
                  double nondirPath = evacPoint.distance( centerPoint ) + centerPoint.distance( doorPoint );
                  //System.out.println("short " + dirPath + " long " + nondirPath);
                  if( nondirPath > 1.5 * dirPath ) {
                    Edge shortedge = model.newEdge( node, door.getNode() );
                    if( debug ) {
                      System.out.println( "direct edge between assignment node and door: " + shortedge );
                    }
                    mapping.setEdgeLevel( shortedge, Level.Equal );
                    //TODO: set edge capacity correctly
                    model.setEdgeCapacity( shortedge, Integer.MAX_VALUE );
                    RoomEdges.add( shortedge );
                    System.out.println( "Create Edge " + shortedge.id() );
                    System.out.println( "Setting edge capacity to " + Integer.MAX_VALUE );
                  }
                }
              }
            }
            if( !AssignmentNodes.get( ZRoom ).isEmpty() ) {
              for( Node node : AssignmentNodes.get( ZRoom ) ) {
                int assign_x = (int) mapping.nodeRectangles.get( node ).getCenterX();
                int assign_y = (int) mapping.nodeRectangles.get( node ).getCenterY();
                Point assignPoint = new Point( assign_x, assign_y );
                Edge edge = createEdge( node, center, Integer.MAX_VALUE );
                RoomEdges.add( edge );
                for( PositionNode door : doors ) {
                  Point doorPoint = new Point( door.getPosition().x, -door.getPosition().y );
                  //System.out.println("assignPoint " + assignPoint + " door " + doorPoint + " centerPoint " + centerPoint);
                  double dirPath = assignPoint.distance( doorPoint );
                  double nondirPath = assignPoint.distance( centerPoint ) + centerPoint.distance( doorPoint );
                  //System.out.println("short " + dirPath + " long " + nondirPath);
                  if( nondirPath > 1.5 * dirPath ) {
                    Edge shortedge = model.newEdge( node, door.getNode() );
                    if( debug ) {
                      System.out.println( "direct edge between assignment node and door: " + shortedge );
                    }
                    mapping.setEdgeLevel( shortedge, Level.Equal );
                    model.setEdgeCapacity( shortedge, Integer.MAX_VALUE );
                    RoomEdges.add( shortedge );
                  }
                }
              }
            }
            for( AssignmentArea area : ZRoom.getAssignmentAreas() ) {
              if( NodesForAssignArea.containsKey( area ) ) {
                //System.out.println("Center Point: " + centerPoint);
                for( PositionNode door : doors ) {
                  List<PositionNode> nearest = FindNearestAssignNodes( NodesForAssignArea.get( area ), door );
                  for( PositionNode near : nearest ) {
                    Edge edge = createEdge( near.getNode(), door.getNode(), Integer.MAX_VALUE );
                    RoomEdges.add( edge );
                  }
                }
                Point cen = new Point( center_x, -center_y );
                PositionNode centerPos = new PositionNode( center, cen );
                List<PositionNode> nearestToCenter = FindNearestAssignNodes( NodesForAssignArea.get( area ), centerPos );
                for( PositionNode near : nearestToCenter ) {
                  Edge shortedge = model.newEdge( near.getNode(), center );
                  if( debug ) {
                    System.out.println( "edge between center and nearest assignment node...: " + shortedge );
                  }
                  mapping.setEdgeLevel( shortedge, Level.Equal );
                  model.setEdgeCapacity( shortedge, Integer.MAX_VALUE );
                  RoomEdges.add( shortedge );
                }
              }
            }

            for( PositionNode node : doors ) {
              Edge edge = createEdge( node.getNode(), center, Integer.MAX_VALUE );
              RoomEdges.add( edge );
            }

          }

        }
      }
      //stores all the created edges for the current Room
      EdgesForRoom.put( ZRoom, RoomEdges );

    }
    //model.setEdgeCapacities(edgesCap);
  }

  @Override
  protected void computeTransitTimes() {
    //first set it to the euclidean distance between two points
    List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();

    for( Edge edge : model.graph().edges() ) {
      if( edge.end() != model.getSupersink() && edge.start() != model.getSupersink() ) {
        double startx = mapping.getNodeRectangles().get( edge.start() ).getCenterX();
        double starty = mapping.getNodeRectangles().get( edge.start() ).getCenterY();

        double endx = mapping.getNodeRectangles().get( edge.end() ).getCenterX();
        double endy = mapping.getNodeRectangles().get( edge.end() ).getCenterY();

        double time = Math.sqrt( Math.pow( (startx - endx), 2 ) + Math.pow( (starty - endy), 2 ) );
        PropertyContainer propertyContainer = PropertyContainer.getInstance();
        int precision = propertyContainer.getAs( "converter.GraphPrecision", Integer.class );
        time = time * precision / 400.0d;
        model.setExactTransitTime( edge, time );
      } else {
        model.setExactTransitTime( edge, 0.0 );
      }
    }
    if( debug ) {
      System.out.println( "TransitTimes without delay or inaccessible areas: " );
    }

    for( ZToGraphRoomRaster room : rasteredRooms ) {
      Room ZRoom = room.getRoom();

      //room contains inaccessible areas or delay areas --> recompute transittimes
      if( (!ZRoom.getDelayAreas().isEmpty()) || (!ZRoom.getInaccessibleAreas().isEmpty()) || (!ZRoom.getStairAreas().isEmpty()) ) {
        //System.out.println("Room with delay/inacc/stairs: " + ZRoom.getName() );
        double startX, startY, endX, endY;
        ListSequence<Edge> roomEdges = EdgesForRoom.get( room.getRoom() );
        //System.out.println("Room Edges: " + roomEdges.toString());
        for( Edge edge : roomEdges ) {
          boolean containsEdge = false;
          Node node1 = edge.start();
          Node node2 = edge.end();
          boolean directionDown = false;
          if( mapping.getNodeRectangles().get( node1 ).getCenterX() < mapping.getNodeRectangles().get( node2 ).getCenterX() ) {
            startX = mapping.getNodeRectangles().get( node1 ).getCenterX();
            startY = -(mapping.getNodeRectangles().get( node1 ).getCenterY());
            endX = mapping.getNodeRectangles().get( node2 ).getCenterX();
            endY = -(mapping.getNodeRectangles().get( node2 ).getCenterY());
          } else {
            endX = mapping.getNodeRectangles().get( node1 ).getCenterX();
            endY = -(mapping.getNodeRectangles().get( node1 ).getCenterY());
            startX = mapping.getNodeRectangles().get( node2 ).getCenterX();
            startY = -(mapping.getNodeRectangles().get( node2 ).getCenterY());
          }
          double UpperY, LowerY;
          if( startY < endY ) {
            UpperY = startY;
            LowerY = endY;
            directionDown = true;
          } else {
            UpperY = endY;
            LowerY = startY;
          }
          //System.out.println("Start: " + startX + " " + startY + "End: " + endX + " " + endY);
          List<PlanPoint> points = new LinkedList<>();
          //saves all points of the edge
          if( endX - startX != 0 ) {
            double a = (endY - startY) / (endX - startX);
            double b = endY - (a * endX);
            for( int i = (int) startX; i < (int) endX + 1; i++ ) {
              PlanPoint p = new PlanPoint();
              p.x = i;
              p.y = (int) (a * i + b);
              points.add( p );
              //System.out.println("p: " + p);
            }
          } else {
            for( int i = (int) UpperY; i < (int) LowerY + 1; i++ ) {
              PlanPoint p = new PlanPoint();
              p.x = (int) endX;
              p.y = i;
              points.add( p );
              //System.out.println("p: " + p);
            }
          }

          //recompute transittimes for stair areas
          if( !ZRoom.getStairAreas().isEmpty() ) {
            for( StairArea stair : ZRoom.getStairAreas() ) {
              int count = 0;
              for( PlanPoint p : points ) {
                if( stair.contains( p ) ) {
                  count++;
                }
              }
              if( count > 0 ) {
                //System.out.println("Stair area contains edge: " + edge );
                Edge back = model.newEdge( edge.end(), edge.start() );
                mapping.setEdgeLevel( back, Level.Equal );
                //graph.setEdge(back);
                if( debug ) {
                  System.out.println( "backward stair edge: " + back );
                }
                model.setEdgeCapacity( back, model.getEdgeCapacity( edge ) );
                stairEdges.add( edge );
                stairEdges.add( back );
                System.out.println( "Created a stair edge (Or 2)!" );
                numStairEdges = numStairEdges + 2;

                boolean directionUp;
                int start_x = (int) mapping.getNodeRectangles().get( edge.start() ).getCenterX();
                int start_y = -(int) mapping.getNodeRectangles().get( edge.start() ).getCenterY();
                int end_x = (int) mapping.getNodeRectangles().get( edge.end() ).getCenterX();
                int end_y = -(int) mapping.getNodeRectangles().get( edge.end() ).getCenterY();
                Point edgeStart = new Point( start_x, start_y );
                Point LowerStair = new Point( (stair.getLowerLevelEnd().getXInt() + stair.getLowerLevelStart().getXInt()) / 2, (stair.getLowerLevelEnd().getYInt() + stair.getLowerLevelStart().getYInt()) / 2 );
                Point UpperStair = new Point( (stair.getUpperLevelEnd().getXInt() + stair.getUpperLevelStart().getXInt()) / 2, (stair.getUpperLevelEnd().getYInt() + stair.getUpperLevelStart().getYInt()) / 2 );
                double startToLower = edgeStart.distance( LowerStair );
                double startToUpper = edgeStart.distance( UpperStair );
                directionUp = startToLower < startToUpper;
                //System.out.println("Direction Up: " + directionUp);
                double stairsize = ((double) count) / ((double) points.size());
                //System.out.println("stairsize: " + stairsize);
                double UpSpeed = stair.getSpeedFactorUp();
                double DownSpeed = stair.getSpeedFactorDown();

                Vector2 first = new Vector2();
                first.setX( (LowerStair.getX() - UpperStair.getX()) / 2 );
                first.setY( (LowerStair.getY() - UpperStair.getY()) / 2 );
                if( debug ) {
                  System.out.println( "first Vector: " + first.getX() + " " + first.getY() );
                }
                Vector2 second = new Vector2();
                second.setX( (start_x - end_x) / 2 );
                second.setY( (start_y - end_y) / 2 );
                if( debug ) {
                  System.out.println( "second Vector: " + second.getX() + " " + second.getY() );
                }
                double angle = first.getAngleBetween( first, second );
                //System.out.println("get Angle between: " + angle);
                double factor = -1.0 / 90.0 * angle + 1;
                //System.out.println("factor: " + factor);
                double transitUp, transitDown;

                double transitOrig = model.getExactTransitTime( edge );
                //System.out.println("Transit Orig: " + transitOrig);
                double transitUpDelay = (1.0 - stairsize) * model.getExactTransitTime( edge ) + stairsize * model.getExactTransitTime( edge ) * (1.0 / UpSpeed);
                //System.out.println("Transit Up Delay: " + transitUpDelay);
                double transitDownDelay = (1.0 - stairsize) * model.getExactTransitTime( edge ) + stairsize * model.getExactTransitTime( edge ) * (1.0 / DownSpeed);
                //System.out.println("Transit Down Delay: " + transitDownDelay);
                transitUp = (transitUpDelay - transitOrig) * factor + transitOrig;
                //System.out.println("Transit Up: " + transitUp);
                transitDown = (transitDownDelay - transitOrig) * factor + transitOrig;
                //System.out.println("Transit Down: " + transitDown);

                if( directionUp ) {
                  model.setExactTransitTime( edge, transitUp );
                  model.setExactTransitTime( back, transitDown );
                } else {
                  model.setExactTransitTime( edge, transitDown );
                  model.setExactTransitTime( back, transitUp );
                }
              }
            }

          }

          if( !ZRoom.getDelayAreas().isEmpty() ) {
            for( DelayArea delay : ZRoom.getDelayAreas() ) {
              int count = 0;
              //take into account all points of an edge and check if they are inside the area polygon
              for( PlanPoint p : points ) {
                if( delay.contains( p ) ) {
                  count++;
                }
              }
              //System.out.println("punkte: " + points.size());
              //System.out.println("count: " + count);
              double delaysize = ((double) count) / ((double) points.size());
              //System.out.println("delaysize: " + delaysize);
              double speed = delay.getSpeedFactor();
              double transit = (1.0 - delaysize) * model.getExactTransitTime( edge ) + delaysize * model.getExactTransitTime( edge ) * (1.0 / speed);
              model.setExactTransitTime( edge, transit );
            }
          }
          if( (!ZRoom.getInaccessibleAreas().isEmpty()) || (!ZRoom.getBarriers().isEmpty()) ) {
            List<InaccessibleArea> barr = new LinkedList<>();
            for( InaccessibleArea barrier : ZRoom.getInaccessibleAreas() ) {
              barr.add( barrier );
            }
            for( InaccessibleArea barrier : ZRoom.getBarriers() ) {
              barr.add( barrier );
            }

            for( InaccessibleArea barrier : barr ) {
              //System.out.println("Barrieren: " + barr.toString());
              for( PlanPoint p : points ) {
                if( barrier.contains( p ) ) {
                  //System.out.println("Inaccessible Area contains edge: " + edge);
                  containsEdge = true;
                  break;
                }
              }
              if( containsEdge ) {
                int barrEndX = barrier.getxOffset() + barrier.getWidth();
                int barrEndY = barrier.getyOffset() + barrier.getHeight();
                //System.out.println("Barrier x Offset: " + barrier.getxOffset());
                //System.out.println("Barrier y Offset: " + barrier.getyOffset());
                //System.out.println("Barrier Endx: " + barrEndX);
                //System.out.println("Barrier Endy: " + barrEndY);
                //System.out.println("Upper Y: " + UpperY);

                if( ZRoom.getDoorEdges().size() >= 0 ) {
                  double Path1, Path2;
                  if( barrier.getxOffset() >= startX && barrEndX <= endX && barrier.getyOffset() >= UpperY && barrEndY <= LowerY ) {
                    //System.out.println("case 0...");
                    if( directionDown ) {
                      Path1 = Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrEndY, 2 ) ) + Math.sqrt( Math.pow( barrier.getxOffset() - endX, 2 ) + Math.pow( barrEndY - endY, 2 ) );
                      Path2 = Math.sqrt( Math.pow( startX - barrEndX, 2 ) + Math.pow( startY - barrier.getyOffset(), 2 ) ) + Math.sqrt( Math.pow( barrEndX - endX, 2 ) + Math.pow( barrier.getyOffset() - endY, 2 ) );
                    } else {
                      Path1 = Math.sqrt( Math.pow( startX - barrEndX, 2 ) + Math.pow( startY - barrEndY, 2 ) ) + Math.sqrt( Math.pow( barrEndX - endX, 2 ) + Math.pow( barrEndY - endY, 2 ) );
                      Path2 = Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrier.getyOffset(), 2 ) ) + Math.sqrt( Math.pow( barrier.getxOffset() - endX, 2 ) + Math.pow( barrier.getyOffset() - endY, 2 ) );
                    }
                  } //x values of barrier are between two x-values of edge endpoints
                  else if( barrier.getxOffset() >= startX && barrEndX <= endX ) {
                    //does not fit on upper side
                    if( barrier.getyOffset() <= UpperY && barrEndY <= LowerY ) {
                      //System.out.println("Case 1a....");
                      Path1 = Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrier.getyOffset(), 2 ) ) + Math.sqrt( Math.pow( barrier.getxOffset() - barrEndX, 2 ) ) + Math.sqrt( Math.pow( barrEndX - endX, 2 ) + Math.pow( barrEndY - endY, 2 ) );
                      if( directionDown ) {
                        Path2 = Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrEndY, 2 ) ) + Math.sqrt( Math.pow( barrier.getxOffset() - endX, 2 ) + Math.pow( barrEndY - endY, 2 ) );
                      } else {
                        Path2 = Math.sqrt( Math.pow( startX - barrEndX, 2 ) + Math.pow( startY - barrEndY, 2 ) );
                      }
                    } //does not fit on lower side
                    else if( barrier.getyOffset() >= UpperY && barrEndY >= LowerY ) {
                      //System.out.println("Case 1b...");
                      Path1 = Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrEndY, 2 ) ) + Math.sqrt( Math.pow( barrier.getWidth(), 2 ) ) + Math.sqrt( Math.pow( barrEndX - endX, 2 ) + Math.pow( barrEndY - endY, 2 ) );
                      if( directionDown ) {
                        Path2 = Math.sqrt( Math.pow( startX - barrEndX, 2 ) + Math.pow( startY - barrier.getyOffset(), 2 ) ) + Math.sqrt( Math.pow( barrier.getxOffset() - endX, 2 ) + Math.pow( barrier.getyOffset() - endY, 2 ) );
                      } else {
                        Path2 = Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrier.getyOffset(), 2 ) ) + Math.sqrt( Math.pow( barrier.getxOffset() - endX, 2 ) + Math.pow( barrier.getyOffset() - endY, 2 ) );
                      }
                    } //does not fit on both sides
                    else {
                      //System.out.println("Case 1c...");
                      Path1 = Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrier.getyOffset(), 2 ) ) + Math.sqrt( Math.pow( barrier.getWidth(), 2 ) ) + Math.sqrt( Math.pow( barrEndX - endX, 2 ) + Math.pow( barrier.getyOffset() - endY, 2 ) );
                      Path2 = Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrEndY, 2 ) ) + Math.sqrt( Math.pow( barrier.getWidth(), 2 ) ) + Math.sqrt( Math.pow( barrEndX - endX, 2 ) + Math.pow( barrEndY - endY, 2 ) );
                    }
                  } else if( barrier.getyOffset() >= UpperY && barrEndY <= LowerY ) {
                    //does not fit on left side
                    if( barrier.getxOffset() < startX && barrEndX <= endX ) {
                      //System.out.println("Case 2a)... ");
                      if( directionDown ) {
                        Path1 = Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrier.getyOffset(), 2 ) ) + Math.sqrt( Math.pow( barrier.getHeight(), 2 ) ) + Math.sqrt( Math.pow( barrier.getxOffset() - endX, 2 ) + Math.pow( barrEndY - endY, 2 ) );
                        Path2 = Math.sqrt( Math.pow( startX - barrEndX, 2 ) + Math.pow( startY - barrier.getyOffset(), 2 ) ) + Math.sqrt( Math.pow( barrier.getxOffset() - endX, 2 ) + Math.pow( barrier.getyOffset() - endY, 2 ) );
                      } else {
                        Path1 = Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrEndY, 2 ) ) + Math.sqrt( Math.pow( barrier.getHeight(), 2 ) ) + Math.sqrt( Math.pow( barrier.getxOffset() - endX, 2 ) + Math.pow( barrier.getyOffset() - endY, 2 ) );
                        Path2 = Math.sqrt( Math.pow( startX - barrEndX, 2 ) + Math.pow( startY - barrEndY, 2 ) ) + Math.sqrt( Math.pow( barrEndX - endX, 2 ) + Math.pow( barrEndY - endY, 2 ) );
                      }
                    } //does not fit on right side
                    else if( barrier.getxOffset() >= startX && barrEndX > endX ) {
                      //System.out.println("case 2b)...");
                      if( directionDown ) {
                        Path1 = Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrEndY, 2 ) ) + Math.sqrt( Math.pow( barrier.getxOffset() - endX, 2 ) + Math.pow( barrEndY - endY, 2 ) );
                        Path2 = Math.sqrt( Math.pow( startX - barrEndX, 2 ) + Math.pow( startY - barrier.getyOffset(), 2 ) ) + Math.sqrt( Math.pow( barrier.getHeight(), 2 ) ) + Math.sqrt( Math.pow( barrEndX - endX, 2 ) + Math.pow( barrEndY - endY, 2 ) );
                      } else {
                        Path1 = Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrier.getyOffset(), 2 ) ) + Math.sqrt( Math.pow( barrier.getxOffset() - endX, 2 ) + Math.pow( barrier.getyOffset() - endY, 2 ) );
                        Path2 = Math.sqrt( Math.pow( startX - barrEndX, 2 ) + Math.pow( startY - barrEndY, 2 ) ) + Math.sqrt( Math.pow( barrier.getHeight(), 2 ) ) + Math.sqrt( Math.pow( barrEndX - endX, 2 ) + Math.pow( barrier.getyOffset() - endY, 2 ) );
                      }
                    } //does not fit on both sides
                    else {
                      //System.out.println("Case 2c...");
                      if( directionDown ) {
                        Path1 = Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrier.getyOffset(), 2 ) ) + Math.sqrt( Math.pow( barrier.getHeight(), 2 ) ) + Math.sqrt( Math.pow( barrier.getxOffset() - endX, 2 ) + Math.pow( barrEndY - endY, 2 ) );
                        Path2 = Math.sqrt( Math.pow( startX - barrEndX, 2 ) + Math.pow( startY - barrier.getyOffset(), 2 ) ) + Math.sqrt( Math.pow( barrier.getHeight(), 2 ) ) + Math.sqrt( Math.pow( barrEndX - endX, 2 ) + Math.pow( barrEndY - endY, 2 ) );
                      } else {
                        Path1 = Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrEndY, 2 ) ) + Math.sqrt( Math.pow( barrier.getHeight(), 2 ) ) + Math.sqrt( Math.pow( barrier.getxOffset() - endX, 2 ) + Math.pow( barrier.getyOffset() - endY, 2 ) );
                        Path2 = Math.sqrt( Math.pow( startX - barrEndX, 2 ) + Math.pow( startY - barrEndY, 2 ) ) + Math.sqrt( Math.pow( barrier.getHeight(), 2 ) ) + Math.sqrt( Math.pow( barrEndX - endX, 2 ) + Math.pow( barrier.getyOffset() - endY, 2 ) );
                      }
                    }
                  } else if( barrier.getxOffset() > startX ) {
                    //System.out.println("case 3");
                    if( directionDown ) {
                      Path1 = 2 * (Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrEndY, 2 ) ) + Math.sqrt( Math.pow( barrier.getxOffset() - endX, 2 ) + Math.pow( barrEndY - endY, 2 ) ));
                      Path2 = 0;
                    } else {
                      Path1 = 2 * (Math.sqrt( Math.pow( startX - barrier.getxOffset(), 2 ) + Math.pow( startY - barrier.getyOffset(), 2 ) ) + Math.sqrt( Math.pow( barrier.getxOffset() - endX, 2 ) + Math.pow( barrier.getyOffset() - endY, 2 ) ));
                      Path2 = 0;
                    }
                  } else if( barrier.getxOffset() < startX ) {
                    //System.out.println("case 4");
                    if( directionDown ) {
                      Path1 = 2 * (Math.sqrt( Math.pow( startX - barrEndX, 2 ) + Math.pow( startY - barrier.getyOffset(), 2 ) ) + Math.sqrt( Math.pow( barrEndX - endX, 2 ) + Math.pow( barrier.getyOffset() - endY, 2 ) ));
                      Path2 = 0;
                    } else {
                      Path1 = 2 * (Math.sqrt( Math.pow( startX - barrEndX, 2 ) + Math.pow( startY - barrEndY, 2 ) ) + Math.sqrt( Math.pow( barrEndX - endX, 2 ) + Math.pow( barrEndY - endY, 2 ) ));
                      Path2 = 0;
                    }
                  } else {
                    if( debug ) {
                      System.out.println( "Currently not considered case" );
                    }
                    Path1 = 1;
                    Path2 = 1;
                  }
                  PropertyContainer propertyContainer = PropertyContainer.getInstance();
                  int precision = propertyContainer.getAs( "converter.GraphPrecision", Integer.class );
                  //System.out.println("Ergebnis: " + Path1*precision/400.0d + " " + Path2*precision/400.0d);
                  double PathLength = (0.5 * Path1 + 0.5 * Path2) * precision / 400.0d;
                  model.setExactTransitTime( edge, PathLength );
                }
              }
            }
          }
        }
      }
    }
    if( debug ) {
      System.out.println( "TransitTimes: " );
      System.out.println( "Capacities: " + model.edgeCapacities().toString() );
    }
    //System.out.println("NumEdges: " + model.getGraph().edges().size());
  }

  @Override
  protected void createReverseEdges() {
    int edgeIndex = model.numberOfEdges();
    final int oldEdgeIndex = edgeIndex;

    final int normalEdges = edgeIndex - numStairEdges;

    model.setNumberOfEdges( normalEdges * 2 + numStairEdges - model.numberOfSinks() );

    // don't use an iterator here, as it will result in concurrent modification
    for( int i = 0; i < oldEdgeIndex; ++i ) {
      Edge edge = model.getEdge( i );
      if( !stairEdges.contains( edge ) && !edge.isIncidentTo( model.getSupersink() ) ) {
        model.createReverseEdge( edge );
      }
    }

  }

  public HashMap<Point, Integer> getFloorDoors( Room room ) {
    HashMap<Point, Integer> doors = new HashMap<>();
    LinkedList<RoomEdge> edges = new LinkedList<>();

    for( RoomEdge edge : room.getPolygon().getEdges() ) {
      //System.out.println("link target: " + edge.getLinkTarget());
      if( !(edge.getLinkTarget() == null) ) {
        if( !(edge.getLinkTarget().getRoom().getAssociatedFloor().equals( room.getAssociatedFloor() )) ) {
          //System.out.println("Diese Kante: " + edge);
          edges.add( edge );
          FloorConnection.put( room, edge.getLinkTarget().getRoom() );
        }
      }
    }
    //create one door for neighbouring edges
    if( edges.size() > 1 ) {
      RoomEdge start = edges.peekFirst();
      int longwidth = start.length();
      RoomEdge end = start;
      RoomEdge next;

      while( edges.size() > 1 ) {
        RoomEdge first = edges.poll();
        next = edges.getFirst();

        if( next.isNeighbour( first ) ) {
          longwidth = longwidth + next.length();

          if( edges.size() == 1 ) {
            end = next;
            int xpos = (start.getSource().getXInt() + end.getTarget().getXInt()) / 2;
            int ypos = (start.getSource().getYInt() + end.getTarget().getYInt()) / 2;
            Point p = new Point( xpos, ypos );
            doors.put( p, longwidth );
            Node node = model.newNode();
            isfloorNode.set( node, Boolean.TRUE );
            linkTarget.put( node, next.getLinkTarget().getRoom() );
            if( debug ) {
              System.out.println( "Floor Connecting Node_1: " + node + "at position: " + p + " for room: " + room.getName() );
            }
            //graph.setNode(node);
            PositionNode pos = new PositionNode( node, p, longwidth );
            floorNodes.add( pos );
            doorNodes.add( pos );
            int width1 = longwidth / 1000 * 2;
            model.setNodeCapacity( node, width1 );
            NodeRectangle rec = new NodeRectangle( p.x - 1, -(p.y + 1), p.x + 1, -(p.y - 1) );
            mapping.setNodeRectangle( node, rec );
            model.getZToGraphMapping().getNodeFloorMapping().set( node, getProblem().getFloorID( room.getAssociatedFloor() ) );
            model.getZToGraphMapping().setDeletedSourceNode( node, false );
          }
        } else {
          end = first;
          int xpos = (start.getSource().getXInt() + end.getTarget().getXInt()) / 2;
          int ypos = (start.getSource().getYInt() + end.getTarget().getYInt()) / 2;
          Point p = new Point( xpos, ypos );
          doors.put( p, longwidth );
          Node node1 = model.newNode();
          isfloorNode.set( node1, Boolean.TRUE );
          linkTarget.put( node1, first.getLinkTarget().getRoom() );
          if( debug ) {
            System.out.println( "Floor Connecting Node_2: " + node1 + "at position: " + p + " for room: " + room.getName() );
          }
          //graph.setNode(node1);
          PositionNode pos1 = new PositionNode( node1, p, longwidth );
          floorNodes.add( pos1 );
          doorNodes.add( pos1 );
          int width1_1 = longwidth / 1000 * 2;
          model.setNodeCapacity( node1, width1_1 );
          NodeRectangle rec1 = new NodeRectangle( p.x - 1, -(p.y + 1), p.x + 1, -(p.y - 1) );
          mapping.setNodeRectangle( node1, rec1 );
          model.getZToGraphMapping().getNodeFloorMapping().set( node1, getProblem().getFloorID( room.getAssociatedFloor() ) );
          model.getZToGraphMapping().setDeletedSourceNode( node1, false );
          if( edges.size() == 1 ) {
            int w = next.length();
            int x = (next.getSource().getXInt() + next.getTarget().getXInt()) / 2;
            int y = (next.getSource().getYInt() + next.getTarget().getYInt()) / 2;
            Point po = new Point( x, y );
            doors.put( po, w );
            Node node = model.newNode();
            isfloorNode.set( node, Boolean.TRUE );
            linkTarget.put( node, next.getLinkTarget().getRoom() );
            if( debug ) {
              System.out.println( "Floor Connecting Node_2: " + node + "at position: " + po + " for room: " + room.getName() );
            }
            PositionNode pos = new PositionNode( node, p, longwidth );
            floorNodes.add( pos );
            doorNodes.add( pos );
            int width1 = longwidth / 1000 * 2;
            model.setNodeCapacity( node, width1 );
            NodeRectangle rec = new NodeRectangle( p.x - 1, -(p.y + 1), p.x + 1, -(p.y - 1) );
            mapping.setNodeRectangle( node, rec );
            model.getZToGraphMapping().getNodeFloorMapping().set( node, getProblem().getFloorID( room.getAssociatedFloor() ) );
            model.getZToGraphMapping().setDeletedSourceNode( node, false );
          }
          start = next;
          longwidth = start.length();
        }
      }

    } else if( edges.size() == 1 ) {
      RoomEdge x = edges.element();
      int width = x.length();
      int xpos = (x.getSource().getXInt() + x.getTarget().getXInt()) / 2;
      int ypos = (x.getSource().getYInt() + x.getTarget().getYInt()) / 2;
      Point p = new Point( xpos, ypos );
      doors.put( p, width );
      Node node = model.newNode();
      isfloorNode.set( node, Boolean.TRUE );
      linkTarget.put( node, x.getLinkTarget().getRoom() );
      if( debug ) {
        System.out.println( "Floor Connecting Node_3: " + node + " at position: " + p + " for room: " + room.getName() );
      }
      //graph.setNode(node);
      PositionNode pos = new PositionNode( node, p, width );
      floorNodes.add( pos );
      doorNodes.add( pos );
      int width1 = width / 1000 * 2;
      //System.out.println("Knotenkap: " + width);
      model.setNodeCapacity( node, width1 );
      NodeRectangle rec = new NodeRectangle( p.x - 1, -(p.y + 1), p.x + 1, -(p.y - 1) );
      mapping.setNodeRectangle( node, rec );
      model.getZToGraphMapping().getNodeFloorMapping().set( node, getProblem().getFloorID( room.getAssociatedFloor() ) );
      model.getZToGraphMapping().setDeletedSourceNode( node, false );
    }
    return doors;
  }

  /**
   * Iterates over the building datastructure and counts the number of neighbor
   * rooms for each room.
   */
  private void computeNeighbors() {
    LOG.finest( "Finding neighbourrooms started..." );
    final List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();

    for( ZToGraphRoomRaster room : rasteredRooms ) {
      neighbours = new HashSet<>();
      PlanPolygon<? extends RoomEdge> p = room.getRoom().getPolygon();
      List<? extends RoomEdge> rEdges = p.getEdges();

      // Count neighbors to different room
      for( RoomEdge e : rEdges ) {
        if( e.getLinkTarget() != null ) { // we have found a room that has a connection
          if( !(e.getLinkTarget().getRoom().getAssociatedFloor().equals( room.getRoom().getAssociatedFloor() )) ) {
            neighbours.add( e.getLinkTarget().getRoom() );
          }
        }
      }

      //gives all dooredges for the first considered room
      Collection<de.zet_evakuierung.model.PlanEdge> dooredgesroom1 = room.getRoom().getDoorEdges();
      //look for all other rooms on the same floor
      for( ZToGraphRoomRaster room2 : rasteredRooms ) {
        if( room == room2 ) {
          continue;
        }

        //gives all dooredges for the other considered room
        Collection<de.zet_evakuierung.model.PlanEdge> dooredgesroom2 = room2.getRoom().getDoorEdges();

        if( (room2.getRoom().getAssociatedFloor().equals( room.getRoom().getAssociatedFloor() )) ) {
          for( de.zet_evakuierung.model.PlanEdge edge : dooredgesroom1 ) {
            for( de.zet_evakuierung.model.PlanEdge edge2 : dooredgesroom2 ) {
              //edges do not have the same associated polygon, so only look for same start/end
              //sources and targets are equal in both edges
              boolean first = edge.getSource().x == edge2.getSource().x;
              boolean second = edge.getSource().y == edge2.getSource().y;
              boolean third = edge.getTarget().x == edge2.getTarget().x;
              boolean fourth = edge.getTarget().y == edge2.getTarget().y;

              //source in one edge is target for other source
              boolean first1 = edge.getSource().x == edge2.getTarget().x;
              boolean second1 = edge.getSource().y == edge2.getTarget().y;
              boolean third1 = edge.getTarget().x == edge2.getSource().x;
              boolean fourth1 = edge.getTarget().y == edge2.getSource().y;

              if( ((first && second && third && fourth) || (first1 && second1 && third1 && fourth1)) ) {
                neighbours.add( room2.getRoom() );
              }
            }
          }
        }

      }
      neighbourRooms.put( room.getRoom(), neighbours );
      numberNeighborRooms.put( room.getRoom(), neighbours.size() );
    }
  }

  /*
   * Computes some values of neighbourrooms needed to create nodes and edges
   * (the number of neighbourrooms with only one neighbour, the number of neighbouring evacuation areas)
   * @param room the room for that neighbourrooms are scanned
   */
  public void ComputeNeighbourRoomValues( Room room ) {
    //gives the neighbours of the current room
    Collection<Room> neighbRooms = neighbourRooms.get( room );
    num = 0;
    numEvac2 = 0;

    for( Room r : neighbRooms ) {
      //count rooms with more than one neighbour
      if( numberNeighborRooms.get( r ) > 1 ) {
        MoreDoorRoom = r;
        num++;
      } else {
        if( !r.getEvacuationAreas().isEmpty() ) {
          numEvac2 = numEvac2 + r.getEvacuationAreas().size(); //count evacuation areas for rooms with only one door
          EvacRoom = r;
        }
        if( r.getEvacuationAreas().isEmpty() && r.getAssignmentAreas().isEmpty() ) {
          HashMap<Point, Integer> doors = r.getDoors();
          List<Point> pointsnotneeded = new LinkedList<>();
          for( Point p : doors.keySet() ) {
            pointsnotneeded.add( p );
          }
          needsNoConnection.put( room.getAssociatedFloor(), pointsnotneeded );
        }
      }
    }
  }

  public HashMap<Room, Collection<Room>> getNeighbourRooms() {
    return neighbourRooms;
  }

  public void createCenterNode( Room r, Point doorNode, Node connectNode ) {
    if( debug ) {
      System.out.println( "Create Center..." );
    }
    double x1 = doorNode.x;
    double y1 = doorNode.y;
    double x2 = mapping.nodeRectangles.get( connectNode ).getCenterX();
    double y2 = mapping.nodeRectangles.get( connectNode ).getCenterY();
    //System.out.println("x1: " + x1 + "y1: " + y1 + "x2: " + x2 + "y2: " + y2);
    Point nodepos = new Point( (int) (x1 + x2) / 2, -((int) (y1 + y2) / 2) );
    Node node = model.newNode();
    if( debug ) {
      System.out.println( "Centerknoten: " + node );
    }
    //graph.setNode(node);
    centerNodeForRoom.put( r, node );
    //TODO: define exact value of node capacity
    model.setNodeCapacity( node, Integer.MAX_VALUE );
    NodeRectangle rec = new NodeRectangle( nodepos.x - 1, nodepos.y + 1, nodepos.x + 1, nodepos.y - 1 );
    if( r.getInaccessibleAreas().size() > 0 ) {
      PlanPoint p = new PlanPoint();
      p.setLocation( nodepos.x, nodepos.y );
      for( InaccessibleArea area : r.getInaccessibleAreas() ) {
        if( area.contains( p ) ) {
          if( debug ) {
            System.out.println( "center lies on inaccessible area... " );
          }
          int areaCenterX = area.getxOffset() + (area.getWidth() / 2);
          rec = new NodeRectangle( areaCenterX - 1, -(area.getyOffset() - 1), areaCenterX + 1, -(area.getyOffset() - 1) );
        }
      }
    }
    mapping.setNodeRectangle( node, rec );
    model.getZToGraphMapping().getNodeFloorMapping().set( node, getProblem().getFloorID( r.getAssociatedFloor() ) );
    //model.getZToGraphMapping().setIsEvacuationNode( node, false );
    //model.getZToGraphMapping().setIsSourceNode(node, false);
    model.getZToGraphMapping().setDeletedSourceNode( node, false );
    System.out.println( "Node created (center)" );
    nodes.add( node );
    //nodeCount++;
  }

  /*
   * creates a central node for a room corresponding to a smallest rectangle with
   * arbitrary direction that contains all points of that room
   */
  //public void CreateCenterForRoom( ZToGraphRoomRaster room, List<PlanPoint> recPoints, Point center ) {
  public void CreateCenterForRoom( ZToGraphRoomRaster room, SmallestRectangle rectangle, Point center ) {
    Room ZRoom = room.getRoom();
    Node node = model.newNode();
    if( debug ) {
      System.out.println( "Create node in the middle: " + node + "for: " + ZRoom.getName() );
    }
    //graph.setNode(node);
    centerNodeForRoom.put( ZRoom, node );
    //TODO: define exact value of node capacity
    //nodesCap.set(node, Integer.MAX_VALUE);
    model.setNodeCapacity( node, Integer.MAX_VALUE );
    NodeRectangle n = new NodeRectangle( (int) rectangle.getPoint( 0 ).getX(),
            -(int) rectangle.getPoint( 0 ).getY(),
            (int) rectangle.getPoint( 1 ).getX(),
            -(int) rectangle.getPoint( 1 ).getY(),
            (int) rectangle.getPoint( 2 ).getX(),
            -(int) rectangle.getPoint( 2 ).getY(),
            (int) rectangle.getPoint( 3 ).getX(),
            -(int) rectangle.getPoint( 3 ).getY() );
    if( ZRoom.getInaccessibleAreas().size() > 0 ) {
      PlanPoint p = new PlanPoint( center.getX(), center.getY() );
      for( InaccessibleArea area : ZRoom.getInaccessibleAreas() ) {
        if( area.contains( p ) ) {
          if( debug ) {
            System.out.println( "center lies on inaccessible area... " );
          }
          int areaCenterX = area.getxOffset() + (area.getWidth() / 2);
          n = new NodeRectangle( areaCenterX - 1, -(area.getyOffset() - 1), areaCenterX + 1, -(area.getyOffset() - 1) );
        }
      }
    }
    mapping.setNodeRectangle( node, n );
    if( debug ) {
      System.out.println( "Node Rectangle for center node: " + rectangle.getPoint( 0 ) + " " + rectangle.getPoint( 1 ) + " " + rectangle.getPoint( 2 ) + " " + rectangle.getPoint( 3 ) );
    }
    //System.out.println("Center des Node Rectangles: " + n.getCenterX() + " " + n.getCenterY());
    model.getZToGraphMapping().getNodeFloorMapping().set( node, getProblem().getFloorID( room.getFloor() ) );
    model.getZToGraphMapping().setDeletedSourceNode( node, false );

  }

  public SmallestRectangle FindSmallestRectangle( ZToGraphRoomRaster room ) {
    SmallestRectangle rec = new SmallestRectangle();
    List<Vector2> givenPoints = new LinkedList<>();

    int size = room.getRaster() / 2;
    int numCol = room.getColumnCount() * 2;
    int numRow = room.getRowCount() * 2;

    for( int i = 0; i < numCol + 2; i++ ) {
      for( int j = 0; j < numRow + 2; j++ ) {
        PlanPoint p = new PlanPoint( room.getXOffset() + (i * size), room.getYOffset() + (j * size) );
        if( room.getRoom().getPolygon().contains( p ) ) {
          Vector2 v = new Vector2( (double) p.x, (double) p.y );
          givenPoints.add( v );
        }
      }
    }
    rec.computeSmallestRectangle( givenPoints.size(), givenPoints );
    return rec;
  }

  public void FindRectangulationNodes( ZToGraphRoomRaster Zroom, SmallestRectangle rectangle ) {
    if( debug ) {
      System.out.println( "Find Rectangulation Nodes started: " );
    }
    //stores the covered area for a certain node, represented as a list of planpoints
    //List<PlanPoint> covered;
    int unit, rest, xPos, yPos;
    int DirectionRight, DirectionDown; //0 der 1 je nachdem ob Raum höher oder breiter ist...

    //new version of creating rectangulation Nodes for room with arbitrary direction
    org.zetool.math.geom.Point nw = rectangle.getPoint( 0 );
    org.zetool.math.geom.Point ne = rectangle.getPoint( 1 );
    org.zetool.math.geom.Point sw = rectangle.getPoint( 2 );
    org.zetool.math.geom.Point se = rectangle.getPoint( 3 );
    //compute rotation angle
    org.zetool.math.geom.Point origin;
    //compute rotation angle
    org.zetool.math.geom.Point lower;
    if( nw.getX() < sw.getX() ) {
      origin = nw;
      lower = sw;
    } else {
      origin = sw;
      lower = nw;
    }
    Vector2 first = new Vector2( 0.0, 1.0 );
    Vector2 second = new Vector2();
    second.setX( origin.getX() - lower.getX() );
    second.setY( origin.getY() - lower.getY() );
    double angle = first.getAngleBetween( first, second );

    Rectangle rotated = rectangle.rotate( angle, nw );

    nw = rotated.getCoordinate( Rectangle.CornerCoordinates.NW );
    ne = rotated.getCoordinate( Rectangle.CornerCoordinates.NE );
    sw = rotated.getCoordinate( Rectangle.CornerCoordinates.SW );
    se = rotated.getCoordinate( Rectangle.CornerCoordinates.SE );
    int width = (int) Math.sqrt( Math.pow( nw.getX() - ne.getX(), 2 ) + Math.pow( nw.getY() - ne.getY(), 2 ) );
    int length = (int) Math.sqrt( Math.pow( nw.getX() - sw.getX(), 2 ) + Math.pow( nw.getY() - sw.getY(), 2 ) );

    if( width < length ) {
      unit = width;
      rest = length;
      DirectionRight = 0;
      DirectionDown = 1;
    } else {
      unit = length;
      rest = width;
      DirectionRight = 1;
      DirectionDown = 0;
    }

    int numIteration = 0;
    xPos = (int) se.getX();
    yPos = (int) se.getY();

    while( rest > 2 * unit ) {
      int UpperLeft1_x = (int) (nw.getX() + (numIteration * DirectionRight * unit));
      int UpperLeft1_y = (int) (nw.getY() + (numIteration * DirectionDown * unit));
      int UpperRight1_x = (int) (DirectionDown * xPos + DirectionRight * ((numIteration + 1) * unit + nw.getX()));
      int UpperRight1_y = (int) (nw.getY() + (numIteration * DirectionDown * unit));
      int LowerRight1_x = (int) (DirectionDown * xPos + DirectionRight * ((numIteration + 1) * unit + nw.getX()));
      int LowerRight1_y = (int) (DirectionRight * yPos + DirectionDown * ((numIteration + 1) * unit + nw.getY()));
      int LowerLeft1_x = (int) (nw.getX() + (numIteration * DirectionRight * unit));
      int LowerLeft1_y = (int) (DirectionRight * yPos + DirectionDown * ((numIteration + 1) * unit + nw.getY()));

      PlanPoint UpperLeft = new PlanPoint( UpperLeft1_x, UpperLeft1_y );
      PlanPoint LowerRight = new PlanPoint( LowerRight1_x, LowerRight1_y );
      PlanPoint UpperRight = new PlanPoint( UpperRight1_x, UpperRight1_y );
      PlanPoint LowerLeft = new PlanPoint( LowerLeft1_x, LowerLeft1_y );

      Rectangle coveredRectangle = new ArbitraryRectangle( UpperLeft, UpperRight, LowerRight, LowerLeft );

      Rectangle covered;
      //TODO: transformiere Punkte zurueck in urspruengliche Lage
      covered = new ArbitraryRectangle( UpperLeft, UpperRight, LowerRight, LowerLeft );

      Rectangle backrotate = coveredRectangle.rotate( -angle, nw );

      //System.out.println("covered: " + covered.toString());
      //node at the left/upper end of rectangle
      Node node1 = model.newNode();
      //coveredArea.put( node1, covered );
      coveredArea.put( node1, backrotate );
      if( debug ) {
        System.out.println( "rectanglar room node: " + node1 );
      }
      //graph.setNode(node1);
      model.setNodeCapacity( node1, Integer.MAX_VALUE );
      NodeRectangle rec1 = new NodeRectangle( (int) backrotate.getCoordinate( Rectangle.CornerCoordinates.NW ).getX(), (int) -backrotate.getCoordinate( Rectangle.CornerCoordinates.NW ).getY(), (int) backrotate.getCoordinate( Rectangle.CornerCoordinates.NE ).getX(), (int) -backrotate.getCoordinate( Rectangle.CornerCoordinates.NW ).getY(), (int) backrotate.getCoordinate( Rectangle.CornerCoordinates.SE ).getX(), (int) -backrotate.getCoordinate( Rectangle.CornerCoordinates.SE ).getY(), (int) backrotate.getCoordinate( Rectangle.CornerCoordinates.SW ).getX(), (int) -backrotate.getCoordinate( Rectangle.CornerCoordinates.SW ).getY() );
      //System.out. println("NodeRectangle1: " + UpperLeft + " " + UpperRight + " " + LowerRight + " " + LowerLeft);
      mapping.setNodeRectangle( node1, rec1 );
      model.getZToGraphMapping().getNodeFloorMapping().set( node1, getProblem().getFloorID( Zroom.getFloor() ) );
      model.getZToGraphMapping().setDeletedSourceNode( node1, false );
      System.out.println( "Node created (node1)" );
      nodes.add( node1 );

      int UpperLeft2_x = (int) (DirectionDown * nw.getX() + DirectionRight * (xPos - ((numIteration + 1) * unit)));
      int UpperLeft2_y = (int) (DirectionRight * nw.getY() + DirectionDown * (yPos - ((numIteration + 1) * unit)));
      int UpperRight2_x = (int) (DirectionRight * (xPos - numIteration * unit) + DirectionDown * xPos);
      int UpperRight2_y = (int) (DirectionRight * nw.getY() + DirectionDown * (yPos - ((numIteration + 1) * unit)));
      int LowerRight2_x = DirectionRight * (xPos - numIteration * unit) + DirectionDown * xPos;
      int LowerRight2_y = DirectionRight * yPos + DirectionDown * (yPos - numIteration * unit);
      int LowerLeft2_x = (int) (DirectionDown * nw.getX() + DirectionRight * (xPos - ((numIteration + 1) * unit)));
      int LowerLeft2_y = (int) (DirectionRight * yPos + DirectionDown * (yPos - numIteration * unit));

      PlanPoint upperLeft2 = new PlanPoint( UpperLeft2_x, UpperLeft2_y );
      PlanPoint lowerRight2 = new PlanPoint( LowerRight2_x, LowerRight2_y );
      PlanPoint upperRight2 = new PlanPoint( UpperRight2_x, UpperRight2_y );
      PlanPoint LowerLeft2 = new PlanPoint( LowerLeft2_x, LowerLeft2_y );
      covered = new ArbitraryRectangle( upperLeft2, upperRight2, lowerRight2, LowerLeft2 );
      Rectangle backrotate2 = covered.rotate( -angle, nw );

      //System.out.println("covered 2: " + covered.toString());
      //System.out. println("NodeRectangle2: " + upperLeft2 + " " + upperRight2 + " " + lowerRight2 + " " + LowerLeft2);
      Node node2 = model.newNode();
      if( debug ) {
        System.out.println( "rectanglar room node: " + node2 );
      }
      coveredArea.put( node2, backrotate2 );
      //graph.setNode(node2);
      model.setNodeCapacity( node2, Integer.MAX_VALUE );
      backrotate = backrotate2;
      NodeRectangle rec2 = new NodeRectangle( (int) backrotate.getCoordinate( Rectangle.CornerCoordinates.NW ).getX(), (int) -backrotate.getCoordinate( Rectangle.CornerCoordinates.NW ).getY(), (int) backrotate.getCoordinate( Rectangle.CornerCoordinates.NE ).getX(), (int) -backrotate.getCoordinate( Rectangle.CornerCoordinates.NW ).getY(), (int) backrotate.getCoordinate( Rectangle.CornerCoordinates.SE ).getX(), (int) -backrotate.getCoordinate( Rectangle.CornerCoordinates.SE ).getY(), (int) backrotate.getCoordinate( Rectangle.CornerCoordinates.SW ).getX(), (int) -backrotate.getCoordinate( Rectangle.CornerCoordinates.SW ).getY() );

      mapping.setNodeRectangle( node2, rec2 );
      model.getZToGraphMapping().getNodeFloorMapping().set( node2, getProblem().getFloorID( Zroom.getFloor() ) );
      model.getZToGraphMapping().setDeletedSourceNode( node2, false );
      System.out.println( "Node created (node2)" );
      nodes.add( node2 );

      rest = rest - 2 * unit;
      numIteration++;
    }

    Node middle = model.newNode();
    System.out.println( "Node created (middle)" );
    nodes.add( middle );
    if( debug ) {
      System.out.println( "node in the middle: " + middle );
    }
    //graph.setNode(middle);
    model.setNodeCapacity( middle, Integer.MAX_VALUE );
    int midUpperLeft_x = (int) ((DirectionDown * nw.getX()) + DirectionRight * (nw.getX() + (numIteration * unit)));
    int midUpperLeft_y = (int) (DirectionRight * nw.getY() + DirectionDown * (nw.getY() + (numIteration * unit)));
    int midUpperRight_x = DirectionRight * (xPos - (numIteration * unit)) + DirectionDown * xPos;
    int midUpperRight_y = midUpperLeft_y;
    int midLowerRight_x = DirectionRight * (xPos - (numIteration * unit)) + DirectionDown * xPos;
    int midLowerRight_y = DirectionDown * (yPos - numIteration * unit) + DirectionRight * yPos;
    int midLowerLeft_x = midUpperLeft_x;
    int midLowerLeft_y = midLowerRight_y;

    PlanPoint upperLeft3 = new PlanPoint( midUpperLeft_x, midUpperLeft_y );
    PlanPoint lowerRight3 = new PlanPoint( midLowerRight_x, midLowerRight_y );
    PlanPoint upperRight3 = new PlanPoint( midUpperRight_x, midUpperRight_y );
    PlanPoint LowerLeft3 = new PlanPoint( midLowerLeft_x, midLowerLeft_y );

    Rectangle covered = new ArbitraryRectangle( upperLeft3, upperRight3, lowerRight3, LowerLeft3 );
    Rectangle backrotate3 = covered.rotate( -angle, nw );

    //System.out.println("covered 3: " + covered.toString());
    coveredArea.put( middle, backrotate3 );

    Rectangle backrotate = backrotate3;
    NodeRectangle recmidd = new NodeRectangle( (int) backrotate.getCoordinate( Rectangle.CornerCoordinates.NW ).getX(), (int) -backrotate.getCoordinate( Rectangle.CornerCoordinates.NW ).getY(), (int) backrotate.getCoordinate( Rectangle.CornerCoordinates.NE ).getX(), (int) -backrotate.getCoordinate( Rectangle.CornerCoordinates.NW ).getY(), (int) backrotate.getCoordinate( Rectangle.CornerCoordinates.SE ).getX(), (int) -backrotate.getCoordinate( Rectangle.CornerCoordinates.SE ).getY(), (int) backrotate.getCoordinate( Rectangle.CornerCoordinates.SW ).getX(), (int) -backrotate.getCoordinate( Rectangle.CornerCoordinates.SW ).getY() );

    mapping.setNodeRectangle( middle, recmidd );
    model.getZToGraphMapping().getNodeFloorMapping().set( middle, getProblem().getFloorID( Zroom.getFloor() ) );
    model.getZToGraphMapping().setDeletedSourceNode( middle, false );

  }

  public void FindRectangulationEdges( Room room ) {
    int size = nodesForRoom.get( room ).size();
    for( int i = 0; i < size - 2; i++ ) {
      Node node1 = nodesForRoom.get( room ).get( i );
      Node node2 = nodesForRoom.get( room ).get( i + 2 );
      Edge edge = createEdge( node1, node2, Integer.MAX_VALUE );
      RoomEdges.add( edge );
    }

    Node node_a = nodesForRoom.get( room ).get( size - 1 );
    Node node_b = nodesForRoom.get( room ).get( size - 2 );
    Edge last = model.newEdge( node_a, node_b );
    if( debug ) {
      System.out.println( "Last Edge: " + last );
    }
    mapping.setEdgeLevel( last, Level.Equal );
    //graph.addEdge(last);
    //TODO: set edge capacity correctly
    model.setEdgeCapacity( last, Integer.MAX_VALUE );
    RoomEdges.add( last );
    //connect the inner nodes with the neighbouring doors
    Collection<PositionNode> doors = doorNodesForRoom.get( room );
    Collection<Node> innernodes = nodesForRoom.get( room );
    //System.out.println("Covered Areas: " + coveredArea.toString());
    for( PositionNode node1 : doors ) {
      double dist = Double.MAX_VALUE;
      Node nextto = null;

      for( Node n : innernodes ) {
        Point center = new Point( (int) mapping.getNodeRectangles().get( n ).getCenterX(), -(int) mapping.getNodeRectangles().get( n ).getCenterY() );
        Point point = new Point( node1.getPosition().x, node1.getPosition().y );
        double distance = point.distance( center );
        if( distance < dist ) {
          dist = distance;
          nextto = n;
        }
      }
      //TODO: set edge capacity correctly
      Edge edge = createEdge( nextto, node1.getNode(), Integer.MAX_VALUE );
      RoomEdges.add( edge );
    }

    for( Node node2 : innernodes ) {
      Rectangle getPoints = coveredArea.get( node2 );
      List<PlanPoint> p = new LinkedList<>();
      for( org.zetool.math.geom.Point po : getPoints ) {
        p.add( new PlanPoint( po.getX(), po.getY() ) );
      }
      PlanPolygon poly = new PlanPolygon( de.zet_evakuierung.model.PlanEdge.class );
      poly.defineByPoints( p );
      //System.out.println("center: " + center);

      if( room.getAssignmentAreas().size() > 0 ) {
        for( Node node : AssignmentNodes.get( room ) ) {
          int x = (int) mapping.getNodeRectangles().get( node ).getCenterX();
          int y = -(int) mapping.getNodeRectangles().get( node ).getCenterY();
          PlanPoint point = new PlanPoint( x, y );
          if( poly.contains( point ) ) {
            Edge edge1 = model.newEdge( node, node2 );
            if( debug ) {
              System.out.println( "Edge between center and assignment area: " + edge1 );
            }
            mapping.setEdgeLevel( edge1, Level.Equal );
            //graph.addEdge(edge1);
            //TODO: set edge capacity correctly
            model.setEdgeCapacity( edge1, Integer.MAX_VALUE );
            RoomEdges.add( edge1 );
          }
        }
      }
      if( room.getEvacuationAreas().size() > 0 ) {
        for( Node node : EvacuationNodes.get( room ) ) {
          int x = (int) mapping.getNodeRectangles().get( node ).getCenterX();
          int y = -(int) mapping.getNodeRectangles().get( node ).getCenterY();
          PlanPoint point = new PlanPoint( x, y );
          if( poly.contains( point ) ) {
            Edge edge2 = model.newEdge( node, node2 );
            if( debug ) {
              System.out.println( "Edge between center and evacuation area: " + edge2 );
            }
            mapping.setEdgeLevel( edge2, Level.Equal );
            //graph.addEdge(edge2);
            //TODO: set edge capacity correctly (smaller than infinity, so that waiting is considered)
            model.setEdgeCapacity( edge2, Integer.MAX_VALUE );
            RoomEdges.add( edge2 );
          }
        }
      }
    }
  }

  public void ConnectRooms( Room room ) {
    Collection<Room> neighbRooms = neighbourRooms.get( room );
    //door nodes of the current room
    Collection<PositionNode> nodes1 = doorNodesForRoom.get( room );

    for( Room r : neighbRooms ) {
      if( r.getAssociatedFloor().equals( room.getAssociatedFloor() ) ) {
        Collection<PositionNode> nodes2 = doorNodesForRoom.get( r );
        //edge between door of current room and all neighbour room doors
        for( PositionNode node1 : nodes1 ) {
          //System.out.println("Is floor node1: " + isfloorNode.get(node1.getNode()));
          if( !isfloorNode.get( node1.getNode() ) ) {
            for( PositionNode node2 : nodes2 ) {
              //System.out.println("Is floor node2: " + isfloorNode.get(node1.getNode()));
              if( !isfloorNode.get( node2.getNode() ) ) {
                if( node1.getPosition().equals( node2.getPosition() ) && used[node1.getNode().id()][node2.getNode().id()] == 0 ) {
                  int width = (int) Math.floor( room.getDoors().get( node1.getPosition() ) / 1000 * 2 );
                  if( width == 0 ) {
                    width = 1;
                  }
                  createEdge( node1.getNode(), node2.getNode(), width );
                  //System.out.println("Weite: " + Math.floor(room.getDoors().get(node1.getPosition())));
                  used[node1.getNode().id()][node2.getNode().id()] = 1;
                  used[node2.getNode().id()][node1.getNode().id()] = 1;
                }
              }
            }
          }
        }
      }
    }
  }

  public void ConnectFloors( Room room ) {
    Collection<PositionNode> nodes1 = floorNodesForRoom.get( room );
    HashMap<Point, Integer> width = floorDoors;

    for( PositionNode n1 : nodes1 ) {
      Room connect = linkTarget.get( n1.getNode() );
      Collection<PositionNode> nodes2 = floorNodesForRoom.get( connect );
      for( PositionNode n2 : nodes2 ) {
        if( linkTarget.get( n2.getNode() ).equals( room ) ) {
          int width1 = width.get( n1.getPosition() );
          createEdge( n1.getNode(), n2.getNode(), width1 );
        }
      }
    }
  }

  public void ConnectWithCertainNode( Room room, Collection<Node> nodes, Node DoorNode ) {
    for( Node node : nodes ) {
      Edge edge = createEdge( node, DoorNode, Integer.MAX_VALUE );
      RoomEdges.add( edge );
    }
  }

  public List<PositionNode> FindNearestAssignNodes( List<PositionNode> nodes, PositionNode connect ) {
    //System.out.println("connect: " + connect.getPosition());
    if( nodes.isEmpty() ) {
      throw new AssertionError( "Nodes cannot be empty!" );
    }
    List<PositionNode> nearest = new LinkedList<>();
    PositionNode near = nodes.get( 0 );
    double dist = Double.MAX_VALUE;

    for( PositionNode n : nodes ) {
      //System.out.println("Node Position: " + n.getPosition());
      double distance = n.getPosition().distance( connect.getPosition() );
      if( distance < dist ) {
        dist = distance;
        near = n;
      }
    }
    nearest.add( near );
    //System.out.println("dist: " + dist + "for node: " + near.getNode() );
    for( PositionNode n : nodes ) {
      if( !n.getNode().equals( near.getNode() ) ) {
        double distance = n.getPosition().distance( connect.getPosition() );
        //System.out.println("distance: " + distance + "for node: " + n.getNode());
        if( distance == dist ) {
          nearest.add( n );
        }
      }
    }
    return nearest;
  }
}
