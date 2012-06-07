/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.common.util.Level;
import de.tu_berlin.math.coga.math.vectormath.Vector2;
import de.tu_berlin.math.coga.math.vectormath.Vector3;
import ds.PropertyContainer;
import ds.graph.network.DynamicNetwork;
import ds.graph.Edge;
import ds.mapping.IdentifiableDoubleMapping;
import ds.mapping.IdentifiableIntegerMapping;
import ds.collection.ListSequence;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import ds.graph.PositionNode;
import ds.mapping.IdentifiableObjectMapping;
import ds.z.*;
import java.awt.Point;
import java.util.*;

/**
 *
 * @author marlenschwengfelder
 */
public class ZToThinNetworkConverter extends BaseZToGraphConverter{
    
    //maps the node representing the door to corresponding room
    public HashMap<Room,Node> DoorNodeForRoom = new HashMap<>();
    //maps the center node for a room to a specific room
    public HashMap<Room,Node> CenterNodeForRoom = new HashMap<>();
    //maps all the other nodes that represent the room to it
    public HashMap<Room,ListSequence<Node>> NodesForRoom = new HashMap<>();
    //maps all the door nodes for a specific room to it
    public HashMap<Room,Collection<PositionNode>> DoorNodesForRoom = new HashMap<>();
    public Collection<PositionNode> doorNodes;
    //...
    public HashMap<Room,Collection<PositionNode>> floorNodesForRoom = new HashMap<>();
    public Collection<PositionNode> floorNodes;
    public IdentifiableObjectMapping<Node, Boolean> isfloorNode = new IdentifiableObjectMapping<>(1,Boolean.class);
    //stores the associated room of a floorNode
    public HashMap<Node,Room> linktarget = new HashMap<>();
    public HashMap<Point,Integer> floordoors = new HashMap<>();
    //stores all nodes for the room (except the door node)
    public ListSequence<Node> nodes;
    //maps the names of the neighoured rooms to each room
    public HashMap<Room,Collection<Room>> NeighbourRooms = new HashMap<>();
    //stores all neighbours for a room
    public Collection<Room> neighbours;
    //stores the number of neighbours for each room
    public HashMap<Room,Integer> numNeighb = new HashMap<>();
    //stores the evacuationNode for a room
    public HashMap<Room,ListSequence<Node>> EvacuationNodes = new HashMap<>();
    public ListSequence<Node> EvacNodes;
    //stores the assignment nodes for a room
    public HashMap<Room,ListSequence<Node>> AssignmentNodes = new HashMap<>();
    public ListSequence<Node> AssignNodes;
    public HashMap<Area,List<PositionNode>> NodesForAssignArea = new HashMap<>();
    public List<PositionNode> AssignAreaNodes;
    public HashMap<Area,List<Integer>> ValuesForAssignArea = new HashMap<>();
    public List<Integer> AssignValues;
    public int[][] used;
    //stores number of evacuation areas
    int numEvac1,numEvac2, num, numAssign1, numAssign2;
    Room MoreDoorRoom, EvacRoom;
    
    //position Nodes to remember which rooms are connected by the given node
    public HashMap<Point, Node> connection = new HashMap<>(); 
    //stores for each edge the corresponding room
    public HashMap<Room,ListSequence<Edge>> EdgesForRoom = new HashMap<>();
    public ListSequence<Edge> RoomEdges;
    //stores all doors that do not get a node (only if one of the corresponding rooms has only one door and is empty)
    public List<Point> needsNoConnection = new LinkedList<>();
    Map<Node,List<PlanPoint>> coveredArea;
    //stores the associated room according to a connecting edge over floors
    public HashMap<Room,Room> FloorConnection = new HashMap<>();
    
    public HashMap<Room,List<Point>> smallRecForRoom = new HashMap<>();
    
    DynamicNetwork graph = new DynamicNetwork();
    IdentifiableIntegerMapping<Node> nodesCap = new IdentifiableIntegerMapping<>(1);
    int nodeCount = 0;
   
    IdentifiableIntegerMapping<Edge> edgesCap = new IdentifiableIntegerMapping<>(1);
    int EdgeCount;
    
    /*defines precision of created nodes for assignment areas, 
     * gives the max. number of persons that can be assigned to one node*/
    int AssignPrecision = 50;
    final static boolean debug = false;
    
    @Override
    protected void createNodes()
    {   
        System.out.println( "Create Nodes for thin Network... " );
        List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();
        coveredArea = new HashMap<>();
        
        FindNeighbourRooms();
        
	LinkedList<Node> sources = new LinkedList<>();
        
        Node supersink = new Node(0);
	graph.setNode( supersink );
	model.setSupersink( supersink );
        nodesCap.set(supersink, Integer.MAX_VALUE);
        
        mapping.setNodeSpeedFactor( supersink, 1 );
	mapping.setNodeRectangle( supersink, new NodeRectangle( 0, 0, 0, 0 ) );
	mapping.setFloorForNode( supersink, -1 );
        nodeCount = 1;
        
        for( ZToGraphRoomRaster room : rasteredRooms ) 
        {
               if( debug ) {System.out.println("Currently considered room: " + room.getRoom().getName() + "on floor: " + room.getRoom().getAssociatedFloor());}
               Room ZRoom = room.getRoom();
              
               doorNodes = new HashSet<>();
               EvacNodes = new ListSequence<>();
               AssignNodes = new ListSequence<>();
               nodes = new ListSequence<>();
               floorNodes = new HashSet<>();
               
               ComputeNeighbourRoomValues(ZRoom); 
               
               //create Node for each Assignment Area in these rooms
               for (AssignmentArea area: ZRoom.getAssignmentAreas())
               { 
                       //if there can be more than (AssignPrecision) persons 
                       if (area.getHeight()*area.getWidth() > AssignPrecision*room.getRaster()*room.getRaster())
                       {
                           AssignAreaNodes = new LinkedList<>();
                           AssignValues = new LinkedList<>();
                           //System.out.println("Height: " + area.getHeight() + "Width: " + area.getWidth());
                           int NodePrecisionWidth = (int) Math.floor((int) (area.getWidth()/Math.sqrt(AssignPrecision*room.getRaster()*room.getRaster())));
                           if (NodePrecisionWidth==0)
                           {NodePrecisionWidth=1;}
                           int NodePrecisionHeight = (int) Math.floor((int) (area.getHeight()/Math.sqrt(AssignPrecision*room.getRaster()*room.getRaster())));
                           if (NodePrecisionHeight==0)
                           {NodePrecisionHeight=1;}
                           //System.out.println("NodePrecisionWidth: " + NodePrecisionWidth + "NodePrecisionHeight " + NodePrecisionHeight );
                           AssignValues.add(NodePrecisionWidth); AssignValues.add(NodePrecisionHeight);
                           int width = area.getWidth()/NodePrecisionWidth;
                           int height = area.getHeight()/NodePrecisionHeight;
                           
                           int size = room.getRaster();                                                                            
                           int numCol = (int) Math.ceil(((double)width/size));
                           int numRow = (int) Math.ceil(((double)height/size)); 
                           
                           for (int i=0; i<NodePrecisionWidth ; i++)
                           {
                               int startCol = (area.getxOffset()-room.getXOffset())/size + i*numCol;
                               
                               for (int j=0; j<NodePrecisionHeight ; j++)
                               {
                                   int startRow = (area.getyOffset()-room.getYOffset())/size + j*numRow;
                                   Node n = new Node(nodeCount++);
                                   if( debug ) {System.out.println("Precise Assignment Node for area: " + n);}
                                   graph.setNode(n);
                                   nodesCap.add(n, Integer.MAX_VALUE); 
                                   model.getZToGraphMapping().getNodeFloorMapping().set( n,getProblem().getFloorID(room.getFloor()));
                                   model.getZToGraphMapping().setIsEvacuationNode( n, false );
                                   model.getZToGraphMapping().setIsSourceNode(n, true);
                                   model.getZToGraphMapping().setIsDeletedSourceNode( n, false );
                                   sources.add(n);
                                   NodeRectangle r = new NodeRectangle(area.getxOffset()+i*width,-(area.getyOffset()+j*height),area.getxOffset()+((i+1)*width),-(area.getyOffset()+((j+1)*height)));
                                   mapping.setNodeRectangle(n, r );
                                   Point point = new Point((int)r.getCenterX(),-((int)r.getCenterY()));
                                   PositionNode p = new PositionNode(n,point);
                                   AssignAreaNodes.add(p);

                                   for (int k=startCol; k<startCol+numCol; k++)
                                   {
                                       for (int l=startRow; l<startRow+numRow; l++)
                                       {
                                           if (k < room.getColumnCount() && l < room.getRowCount())
                                           {
                                                ZToGraphRasterSquare square = room.getSquare( k, l );
                                                square.mark();
                                                square.setNode(n);
                                           }
                                       }
                                    }  
                               }
                           }
                           NodesForAssignArea.put(area, AssignAreaNodes);
                           ValuesForAssignArea.put(area, AssignValues);
                       }
                       else
                       {
                           Node node = new Node(nodeCount++);
                           if( debug ) {System.out.println("One AssignmentNode: " + node + " in Room: " + ZRoom.getName());}
                           graph.setNode(node);
                           int value = area.getMaxEvacuees();
                           nodesCap.add(node, Integer.MAX_VALUE); 
                           NodeRectangle rec = new NodeRectangle(area.getxOffset(),-area.getyOffset(), area.getxOffset()+ area.getWidth(), -(area.getyOffset() + area.getHeight()));
                           mapping.setNodeRectangle(node, rec );
                           model.getZToGraphMapping().getNodeFloorMapping().set( node,getProblem().getFloorID(room.getFloor()));
                           model.getZToGraphMapping().setIsEvacuationNode( node, false );
                           model.getZToGraphMapping().setIsSourceNode(node, true);
                           model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                           sources.add(node);
                           AssignNodes.add(node);
                           //map raster squares to assignment node (for ConvertConcreteAssignment()...)
                           int size = room.getRaster();      
                           int startCol = (area.getxOffset()-room.getXOffset())/size;                      
                           int startRow = (area.getyOffset()-room.getYOffset())/size;
                           int numCol = (int) Math.ceil(((double)area.getWidth()/size));
                           int numRow = (int) Math.ceil(((double)area.getHeight()/size));                       

                           for (int i=startCol; i<startCol+numCol; i++)
                           {
                               for (int j=startRow; j<startRow+numRow;j++)
                               {
                                   if (i < room.getColumnCount() && j < room.getRowCount())
                                   {
                                        ZToGraphRasterSquare square = room.getSquare( i, j );
                                        square.mark();
                                        square.setNode(node);
                                        //System.out.println("Square in ThinNetwork: " + square.toString());
                                   }
                               }
                           }
                       }
              }
                    //create node for each evacuation area of building
                    for (EvacuationArea Earea : ZRoom.getEvacuationAreas())
                    {
                        //if room has only one neighbour, create Evacuation node near to door
                        if (numNeighb.get(ZRoom)==1)
                        {                            
                            for (Point p: ZRoom.getDoors().keySet())
                            {
                                 double mindist = Double.MAX_VALUE;
                                 Point min = null;
                                     for (PlanPoint pp :Earea.getPlanPoints())
                                     {
                                         double dist = calculateDistance(p,pp);
                                         if (dist < mindist)
                                         {
                                             mindist = dist;
                                             min = pp;                                             
                                         }
                                     } 
                            Node node = new Node(nodeCount);
                            if( debug ) {System.out.println("near EvacuationNode: " + node + " in room: " + ZRoom.getName());}
                            graph.setNode(node);
                            int Evalue = Earea.getMaxEvacuees();
                            nodesCap.add(node, Evalue); 
                            NodeRectangle rec = new NodeRectangle(min.x-1,-(min.y-1),min.x+1, -(min.y+1));
                            mapping.setNodeRectangle(node, rec );
                            model.getZToGraphMapping().getNodeFloorMapping().set( node,getProblem().getFloorID(room.getFloor()));
                            model.getZToGraphMapping().setIsEvacuationNode( node, true );
                            model.getZToGraphMapping().setIsSourceNode(node, false);
                            model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                            EvacNodes.add(node);
                            nodeCount++;
                            }
                        }
                        else
                        {
                            Node node = new Node(nodeCount);
                            if( debug ) {System.out.println("EvacuationNode: " + node + " in room: " + ZRoom.getName());}
                            graph.setNode(node);
                            int Evalue = Earea.getMaxEvacuees();
                            nodesCap.add(node, Evalue); 
                            NodeRectangle rec = new NodeRectangle(Earea.getxOffset(),-Earea.getyOffset(), Earea.getxOffset()+ Earea.getWidth(), -(Earea.getyOffset() + Earea.getHeight()));
                            mapping.setNodeRectangle(node, rec );
                            model.getZToGraphMapping().getNodeFloorMapping().set( node,getProblem().getFloorID(room.getFloor()));
                            model.getZToGraphMapping().setIsEvacuationNode( node, true );
                            model.getZToGraphMapping().setIsSourceNode(node, false);
                            model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                            EvacNodes.add(node);
                            nodeCount++;
                        }
                    }
                    
                    floordoors = getFloorDoors(ZRoom);                   
                    
                    //create door nodes for rooms with only one neighbour
                    //if there is at least one evacuation or assignment area, create a node representing the doors

                    if ((!(ZRoom.getEvacuationAreas().isEmpty() && ZRoom.getAssignmentAreas().isEmpty()) && (numNeighb.get(ZRoom) ==1)) ||(numNeighb.get(ZRoom)==1 && (floorNodesForRoom.containsKey(ZRoom))) )
                    { 
                        HashMap<Point,Integer> doors = ZRoom.getDoors();
                        for (Point p: doors.keySet())
                        {                                     
                                //create node for door                        
                                Node node = new Node(nodeCount);
                                if( debug ) {System.out.println("Door Node: " + node + " for room: " + ZRoom.getName() + "with 1 neighbour");}
                                PositionNode pos = new PositionNode(node,p,doors.get(p));
                                doorNodes.add(pos);
                                nodeCount++;
                                graph.setNode(node);
                                //DoorNodeForRoom.put(ZRoom, node);
                                //length of door is returned in mm, Observation: 0.5 meter/person
                                int width = (int) Math.floor(((double) ZRoom.getLengthOfDoor(ZRoom))/1000.0)*2;
                                //System.out.println("Knotenkap: " + width);
                                nodesCap.add(node, width);
                                NodeRectangle rec = new NodeRectangle(p.x-1,-(p.y+1), p.x+1,-(p.y-1));
                                mapping.setNodeRectangle(node, rec );
                                model.getZToGraphMapping().getNodeFloorMapping().set( node,getProblem().getFloorID(room.getFloor()));
                                model.getZToGraphMapping().setIsEvacuationNode( node, false );
                                model.getZToGraphMapping().setIsSourceNode(node, false);
                                model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                        }
                    }
                    
                    //create nodes for rooms with more than one neighbour
                    if (numNeighb.get(ZRoom) > 1)
                    {
                        //creating door nodes                        
                        HashMap<Point,Integer> doors = ZRoom.getDoors();
                        for (Point p: doors.keySet())
                        {
                            //only create door node if rooms with one door is not empty or is empty and has a floor connecting node
                            if (!needsNoConnection.contains(p))
                            {
                                Node node = new Node(nodeCount);
                                connection.put(p,node);
                                nodesCap.add(node, ZRoom.getMaxEvacuees());
                                if( debug ) {System.out.println("Door Node: " + node + "at pos: " + p + "for room"  + ZRoom.getName());}
                                PositionNode pos = new PositionNode(node,p,doors.get(p)); 
                                doorNodes.add(pos);
                                graph.setNode(node);
                                NodeRectangle rec = new NodeRectangle(p.x-1,-(p.y+1), p.x+1,-(p.y-1));
                                mapping.setNodeRectangle(node, rec );
                                model.getZToGraphMapping().getNodeFloorMapping().set( node,getProblem().getFloorID(room.getFloor()));
                                model.getZToGraphMapping().setIsEvacuationNode( node, false );
                                model.getZToGraphMapping().setIsSourceNode(node, false);
                                model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                                nodeCount++;
                            }
                        }                      
                        
                    //only neighbours with one neighbour, Case 2b)
                    //one evacuation node inside room and one in neighbourrooms 
                    if (numEvac2 == 1 && num==0 && ZRoom.getEvacuationAreas().size() == 1)
                    {
                        SmallestRectangle rectangle = FindSmallestRectangle(room);            
                        List<PlanPoint> rec = rectangle.getPoints();
                    
                        Point nw = rec.get(0); Point ne = rec.get(1); Point sw = rec.get(2); Point se = rec.get(3);
                        double width = Math.sqrt(Math.pow(nw.getX()-ne.getX(),2) + Math.pow(nw.getY()-ne.getY(),2));
                        double length = Math.sqrt(Math.pow(nw.getX()-sw.getX(),2) + Math.pow(nw.getY()-sw.getY(),2));
                        //room is nearly rectangular
                        if ((width > 2* length) || (length > 2*width))
                        { 
                            if( debug ) {System.out.println("Case 2b)"); }
                            FindRectangulationNodes(room,rec);
                        }         
                        else
                        {
                            if( debug ) {System.out.println("Create center1 for room: " + ZRoom.getName());}
                            //create node between two evacuation areas
                            for (Point p: EvacRoom.getDoors().keySet())
                            {
                                Point q = new Point(p.x,-p.y);
                                createCenterNode(ZRoom,q,EvacNodes.first());
                            }
                        }
                    }
                //exactly one room with more than one neighbour, no evacuation areas in rooms with one neighbour   
                else if (num==1 && numEvac2==0 )
                {                    
                    if (ZRoom.getEvacuationAreas().size()==1)
                    {
                        //node between 2 evacuation areas
                        if( debug ) {System.out.println("Create node between 2 evacuation areas for room: " + ZRoom.getName());}
                        Collection<ds.z.Edge> doors1 = MoreDoorRoom.getDoorEdges();
                        Collection<ds.z.Edge> doors2 = ZRoom.getDoorEdges();
                        Point p = new Point();
                        for (ds.z.Edge edge: doors1)
                        {
                            for (ds.z.Edge edge1: doors2)
                            {
                            boolean first = edge.getSource().x == edge1.getSource().x;
                            boolean second = edge.getSource().y == edge1.getSource().y;
                            boolean third = edge.getTarget().x == edge1.getTarget().x;
                            boolean fourth = edge.getTarget().y == edge1.getTarget().y;

                            //source in one edge is target for other source
                            boolean first1 = edge.getSource().x == edge1.getTarget().x;
                            boolean second1 = edge.getSource().y == edge1.getTarget().y;
                            boolean third1 = edge.getTarget().x == edge1.getSource().x;
                            boolean fourth1 = edge.getTarget().y == edge1.getSource().y;

                            if ((first && second && third && fourth) || (first1 && second1 && third1 && fourth1) )
                            {
                                int x = (edge.getMaxX()+ edge.getMinX())/2;
                                int y = -((edge.getMaxY()+ edge.getMinY())/2);
                                Point point = new Point(x,y);
                                p.setLocation(point);
                                createCenterNode(ZRoom,p,EvacNodes.first());
                            }
                            } 
                        }
                    }
                }
                //only neighbours with one neighbour and one evacuation area inside
                else if (num==0 && numEvac2 == 1)
                {
                    
                }
                //more than one room with more neighbours or more than one evacuation area in neighbouring rooms
                else 
                {
                    if( debug ) {System.out.println("Trivial Node Creation.." + ZRoom.getName());}
                    SmallestRectangle rectangle = FindSmallestRectangle(room);            
                    List<PlanPoint> rec = rectangle.getPoints();
                    
                    Point nw = rec.get(0); Point ne = rec.get(1); Point sw = rec.get(2); Point se = rec.get(3);
                    double width = Math.sqrt(Math.pow(nw.getX()-ne.getX(),2) + Math.pow(nw.getY()-ne.getY(),2));
                    double length = Math.sqrt(Math.pow(nw.getX()-sw.getX(),2) + Math.pow(nw.getY()-sw.getY(),2));
                    if( debug ) {System.out.println("width: " + width + " length: " + length);}
                    //room is nearly rectangular
                    if ((width > 2* length) || (length > 2*width))
                    {                 
                        FindRectangulationNodes(room,rec);
                    }         
                    else
                    {
                        Point c = new Point((int)rectangle.getCenter().getX(),(int)rectangle.getCenter().getY());
                        CreateCenterForRoom(room,rec,c);
                    }
                 }
        }
             
                  
        //stores all the constructed nodes for one room (except door node, evacuation and assignment nodes)
        NodesForRoom.put(ZRoom, nodes); 
        for (Node n: nodes)
        {
            isfloorNode.set(n, Boolean.FALSE);
        }
        EvacuationNodes.put(ZRoom, EvacNodes);
        for (Node n: EvacNodes)
        {
            isfloorNode.set(n, Boolean.FALSE);
        }
        AssignmentNodes.put(ZRoom, AssignNodes);
        for (Node n: AssignNodes)
        {
            isfloorNode.set(n, Boolean.FALSE);
        }
        DoorNodesForRoom.put(ZRoom, doorNodes);
        for (PositionNode n: doorNodes)
        {
            if (!floorNodes.contains(n))
            {
                isfloorNode.set(n.getNode(), Boolean.FALSE);
            }
        }
        floorNodesForRoom.put(ZRoom, floorNodes);
        
        } //end for all rastered rooms        
        model.setNodeCapacities( nodesCap );
        model.setNetwork(graph);
        model.setSources(sources);
        if( debug ) {System.out.print("Created nodes: ");
        for (Node node: model.getGraph().nodes())
        {
            System.out.print(node + "; ");
        } }
        System.out.println("NumNodes: " + model.getGraph().nodes().size());
    }
    
    
    @Override
    protected void createEdgesAndCapacities()
    { 
        System.out.println( "Set up edges and compute capacities... " );
        int numNodes = model.getGraph().numberOfNodes();
        
        used = new int[numNodes][numNodes];
        for (int i=0;i<numNodes;i++){
            for (int j=0;j<numNodes;j++){
                used[i][j] = 0;
                used[j][i] = 0;
            }
        }
        ZToGraphMapping mapping = model.getZToGraphMapping();
        List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();

        //mapping to store edge capacities
	//IdentifiableIntegerMapping<Edge> edgesCap = new IdentifiableIntegerMapping<>( graph.numberOfEdges() * graph.numberOfEdges() );
	model.setEdgeCapacities( edgesCap );

	for( ZToGraphRoomRaster room : rasteredRooms ) 
        {
            if( debug ) {System.out.println("Currently considered room: " + room.getRoom().getName()+ "on floor: " + room.getRoom().getAssociatedFloor());}
            Room ZRoom = room.getRoom();
            ComputeNeighbourRoomValues(ZRoom);
            RoomEdges = new ListSequence<>();
            //create Edge between evacuation nodes and super sink
            Node supersink = model.getSupersink();
            
            for (Node evacNode: EvacuationNodes.get(room.getRoom()))
            {
                Node node1 = evacNode;
                Edge edge = new Edge(EdgeCount++,node1,supersink);
                mapping.setEdgeLevel(edge, Level.Equal);
                graph.setEdge(edge);
                if( debug ) {System.out.println("Edge to supersink: " + edge);}                   
                edgesCap.set(edge,Integer.MAX_VALUE);
            }
            Collection<Room> neighbRooms = NeighbourRooms.get(ZRoom);
            //connect the room with all of its neighbours
            ConnectRooms(ZRoom);
            //connects the different floors
            ConnectFloors(ZRoom);
            
            //connect nodes of assignment areas
            for (AssignmentArea a: ZRoom.getAssignmentAreas() )
            {
                if (NodesForAssignArea.containsKey(a))
                {
                    int numWidth = ValuesForAssignArea.get(a).get(0);
                    int numHeight = ValuesForAssignArea.get(a).get(1);
                    for (int i=0; i<(numWidth*numHeight)-1;i++)
                    {
                        Node first = NodesForAssignArea.get(a).get(i).getNode();
                        Node second = NodesForAssignArea.get(a).get(i+1).getNode();
                        if (((i+1) % numHeight) != 0)
                        {
                            Edge edge = new Edge(EdgeCount++,first,second);
                            mapping.setEdgeLevel(edge, Level.Equal);
                            graph.setEdge(edge);
                            if( debug ) {System.out.println("Edge between assignNodes: " + edge);}                   
                            edgesCap.set(edge,Integer.MAX_VALUE);
                        }
                        if (i<(numWidth*numHeight) - numHeight)
                        {
                           Node hor = NodesForAssignArea.get(a).get(i+numHeight).getNode(); 
                           Edge edge = new Edge(EdgeCount++,first,hor);
                           mapping.setEdgeLevel(edge, Level.Equal);
                           graph.setEdge(edge);
                           if( debug ) {System.out.println("Edge between assignNodes: " + edge);}                   
                           edgesCap.set(edge,Integer.MAX_VALUE);
                        }
                    }                    
                }
            }
            
            //room has no neighbours
            if (ZRoom.getDoorEdges().isEmpty())
            {
                /*if (ZRoom.getEvacuationAreas().size() > 1)
                {
                    Node center = CenterNodeForRoom.get(ZRoom);
                    if (!EvacuationNodes.get(ZRoom).isEmpty())
                    {
                        for (Node node: EvacuationNodes.get(ZRoom))
                        {
                            Edge edge = new Edge(EdgeCount++,center,node);
                            mapping.setEdgeLevel(edge, Level.Equal);
                            graph.addEdge(edge);
                            System.out.println("Edge between assignment area and center: " + edge);
                            int cap = ZRoom.getMaxEvacuees()/ZRoom.getAssignmentAreas().size();
                            edgesCap.set(edge,cap);
                            RoomEdges.add(edge);
                        }
                    }
                    if (!AssignmentNodes.get(ZRoom).isEmpty())
                    {
                        for (Node node: AssignmentNodes.get(ZRoom))
                        {
                            Edge edge = new Edge(EdgeCount++,center,node);
                            mapping.setEdgeLevel(edge, Level.Equal);
                            graph.addEdge(edge);
                            System.out.println("Edge between assignment area and center: " + edge);
                            int cap = ZRoom.getMaxEvacuees()/ZRoom.getAssignmentAreas().size();
                            edgesCap.set(edge,cap);
                            RoomEdges.add(edge);
                        }
                    }
                }*/
                //connect evacuation and assignment nodes directly
                if (ZRoom.getEvacuationAreas().size()>0)
                {
                    for (Node n1: AssignmentNodes.get(ZRoom))
                    {
                        for (Node n2: EvacuationNodes.get(ZRoom))
                        {
                            Edge edge = new Edge(EdgeCount++,n1,n2);
                            mapping.setEdgeLevel(edge, Level.Equal);
                            graph.addEdge(edge);
                            if( debug ) {System.out.println("Edge between assignment area and evacuation area: " + edge);}
                            //set the capacity of an edge to the max. number of persons(in this room) divided by number of assignment areas
                            int cap = 2;
                            edgesCap.set(edge,cap);
                            RoomEdges.add(edge);
                            
                        }
                    }
                    
                    for (AssignmentArea area: ZRoom.getAssignmentAreas())
                    {
                       if (NodesForAssignArea.containsKey(area))
                       {
                           for (Node n2: EvacuationNodes.get(ZRoom))
                           {
                                Point p = new Point((int)mapping.getNodeRectangles().get(n2).getCenterX(),-(int)mapping.getNodeRectangles().get(n2).getCenterY());
                                PositionNode evac = new PositionNode(n2,p);
                                List<PositionNode> nearest = FindNearestAssignNodes(NodesForAssignArea.get(area),evac);
                                for (PositionNode near: nearest)
                                {
                                    Edge edge = new Edge(EdgeCount++,n2, near.getNode());
                                    if( debug ) {System.out.println("Edge between nearest assignment area and evac node: " + edge);}
                                    mapping.setEdgeLevel(edge, Level.Equal);
                                    graph.addEdge(edge);
                                    edgesCap.set(edge, Integer.MAX_VALUE);
                                    RoomEdges.add(edge);
                                }
                            }                      
                        }
                    }
            }
            }
            //room has only one neighbour
            if (numNeighb.get(ZRoom) == 1)
            {  
                if( debug ) {System.out.println("Case 0 for Room: " + ZRoom.getName());}
                Collection<Node> nodes = new HashSet<>();  
                    if (!AssignmentNodes.get(ZRoom).isEmpty()){
                        for (Node node: AssignmentNodes.get(ZRoom)){
                            nodes.add(node);
                        }
                    }
                    if (!EvacuationNodes.get(ZRoom).isEmpty()){
                        for (Node node: EvacuationNodes.get(ZRoom) ){
                            nodes.add(node);
                        }
                    }
                    
                    for (AssignmentArea area: ZRoom.getAssignmentAreas())
                    {
                       if (NodesForAssignArea.containsKey(area))
                       {
                           for (PositionNode n2: DoorNodesForRoom.get(ZRoom))
                           {
                                List<PositionNode> nearest = FindNearestAssignNodes(NodesForAssignArea.get(area),n2);
                                for (PositionNode near: nearest)
                                {
                                    Edge edge = new Edge(EdgeCount++,n2.getNode(), near.getNode());
                                    if( debug ) {System.out.println("Edge between nearest assignment area and door node: " + edge);}
                                    mapping.setEdgeLevel(edge, Level.Equal);
                                    graph.addEdge(edge);
                                    edgesCap.set(edge, Integer.MAX_VALUE);
                                    RoomEdges.add(edge);
                                }
                            }                      
                        }
                    }
                    
                    //ConnectWithCertainNode(ZRoom, nodes, DoorNodeForRoom.get(ZRoom) );
                    for (PositionNode n: DoorNodesForRoom.get(ZRoom))
                    {
                        ConnectWithCertainNode(ZRoom, nodes, n.getNode());
                    }
                    
                    if ((!ZRoom.getEvacuationAreas().isEmpty()) && (!ZRoom.getAssignmentAreas().isEmpty()) )
                    {  
                        for (Node node1: EvacuationNodes.get(ZRoom))
                        {
                                for (Node node2: AssignmentNodes.get(ZRoom))
                                {
                                    //connect evacuation area and assignment area in one room directly
                                    Edge edge = new Edge(EdgeCount++,node1,node2);
                                    if( debug ) {System.out.println("Edge between assign node and evacuation node: " + edge);}
                                    mapping.setEdgeLevel(edge, Level.Equal);
                                    graph.addEdge(edge);
                                    edgesCap.set(edge,Integer.MAX_VALUE);
                                    RoomEdges.add(edge);
                                }
                        }
                    } 
                    if (ZRoom.getAssignmentAreas().isEmpty() && ZRoom.getEvacuationAreas().isEmpty() && (!floorNodesForRoom.get(ZRoom).isEmpty()))
                    {
                        Collection<PositionNode> flnodes = floorNodesForRoom.get(ZRoom);
                        Collection<PositionNode> doornodes = DoorNodesForRoom.get(ZRoom);
                        for (PositionNode n1: flnodes)
                        {
                            for (PositionNode n2: doornodes)
                            {
                                if (!(flnodes.contains(n2)))
                                {
                                    Edge edge = new Edge(EdgeCount++,n1.getNode(),n2.getNode());
                                    if( debug ) {System.out.println("Edge between door and floor connecting door: " + edge);}
                                    mapping.setEdgeLevel(edge, Level.Equal);
                                    graph.addEdge(edge);
                                    edgesCap.set(edge,Integer.MAX_VALUE);
                                    RoomEdges.add(edge);
                                }
                            }
                        }
                    }
            }
            else if (numNeighb.get(ZRoom) > 1)
            {
                //all neighbours have only one door and no evacuation areas
                if (numEvac2==0 && num==0)
                {
                  if( debug ) {System.out.println("Case 1 for Room: " + ZRoom.getName());}  
                  if (ZRoom.getEvacuationAreas().isEmpty())
                  {
                      System.out.println("there is no reachable evacuation area for room: " + ZRoom.getName());
                  }
                  //connect only evacuation node with doors
                  else if (ZRoom.getEvacuationAreas().size() ==1)
                  { 
                        Collection<Node> DoorNodes = new HashSet<>();
                        Node evac= EvacuationNodes.get(ZRoom).first();
                        
                        for (PositionNode n : DoorNodesForRoom.get(ZRoom))
                        {                
                                DoorNodes.add(n.getNode());
                        }  
                        ConnectWithCertainNode(ZRoom,DoorNodes,evac);
                  }
                  
                }
                //there is one evacuationNode in a neighbour room and no one in room itself
                //connect all doors with the door of evacuation room
                else if (numEvac2 == 1 && num==0 && ZRoom.getEvacuationAreas().isEmpty())
                {
                    if( debug ) {System.out.println("Case 2 for Room: " + ZRoom.getName());}
                    if (ZRoom.getHeight() >= 2* ZRoom.getWidth() || ZRoom.getWidth() >= 2*ZRoom.getHeight())
                    {
                        FindRectangulationEdges(ZRoom);
                    }
                    else
                    {
                        //connect all door Nodes with door Node of evacuation room
                        for (PositionNode n: DoorNodesForRoom.get(EvacRoom))
                        {
                            for (Room r: neighbRooms)
                            {
                                if (r != EvacRoom)
                                { 
                                    for (PositionNode door2 : DoorNodesForRoom.get(r))
                                    {
                                        if (door2 != null)
                                        {    
                                            Edge edge = new Edge(EdgeCount++,n.getNode(), door2.getNode());
                                            mapping.setEdgeLevel(edge, Level.Equal);
                                            graph.addEdge(edge);
                                            edgesCap.set(edge, Integer.MAX_VALUE);
                                            RoomEdges.add(edge);
                                        }
                                    }
                                }
                            }
                             //connect all assignmentnodes with door node of evacuation room
                            for (AssignmentArea area: ZRoom.getAssignmentAreas())
                            {
                                if (NodesForAssignArea.containsKey(area))
                                {
                                    List<PositionNode> nearest = FindNearestAssignNodes(NodesForAssignArea.get(area),n);
                                    for (PositionNode near : nearest)
                                    {
                                        Edge edge = new Edge(EdgeCount++,n.getNode(), near.getNode());
                                        if( debug ) {System.out.println("Edge between nearest assignment area and door node: " + edge);}
                                        mapping.setEdgeLevel(edge, Level.Equal);
                                        graph.addEdge(edge);
                                        edgesCap.set(edge, Integer.MAX_VALUE);
                                        RoomEdges.add(edge);
                                    }
                                }                      
                            }
                            if (!AssignmentNodes.get(ZRoom).isEmpty())
                            {
                                for (Node n2: AssignmentNodes.get(ZRoom))
                                {
                                    Edge edge = new Edge(EdgeCount++,n.getNode(), n2);
                                    if( debug ) {System.out.println("Edge between assignment area and door node: " + edge);}
                                    mapping.setEdgeLevel(edge, Level.Equal);
                                    graph.addEdge(edge);
                                    edgesCap.set(edge, Integer.MAX_VALUE);
                                    RoomEdges.add(edge);
                                }
                            }
                        }
                       
                    }
                }
                //one neighbouring evacuation area and one evacuation area inside (Case 2b)
                else if (numEvac2 == 1 && num==0 && ZRoom.getEvacuationAreas().size() == 1)
                {
                    if( debug ) {System.out.println("Case 3 for Room: " + ZRoom.getName());}
                    //room is more quadratic
                    if ((ZRoom.getHeight() < 2*ZRoom.getWidth()) && (ZRoom.getWidth() < 2*ZRoom.getHeight()))
                    {
                        if( debug ) {System.out.println("quadratic room");}
                        Collection<Node> doors = new HashSet<>();
                        for (PositionNode n: DoorNodesForRoom.get(ZRoom))
                        {
                            doors.add(n.getNode());
                        }
                        doors.add(EvacuationNodes.get(ZRoom).first());
                        Node center = CenterNodeForRoom.get(ZRoom);
                        //connect the center with evacuation area and all neighbour rooms...
                        ConnectWithCertainNode(ZRoom,doors,center);
                    }
                    
                    else 
                    {
                        FindRectangulationEdges(ZRoom);
                    }
                    
                }
                    //exactly one room with more than one door and no evacuation areas in rooms with only one door
                    // connect door nodes directly, Example2.zet
                    else if (num==1 && numEvac2 == 0 && ZRoom.getEvacuationAreas().isEmpty())
                    {
                        if( debug ) {System.out.println("Case 4a) for room: " + ZRoom.getName());}
                        Collection<Node> coll= new HashSet<>(); 
                        Node want = null;        
                        for (PositionNode n1: DoorNodesForRoom.get(MoreDoorRoom))
                        {
                            if( debug ) {System.out.println("Door Node: " + n1.getNode() + "at pos: " + n1.getPosition());}
                            for (PositionNode n2: DoorNodesForRoom.get(ZRoom))
                            {
                                if ((n2.getPosition().x==n1.getPosition().x) && (n2.getPosition().y==n1.getPosition().y))
                                {
                                    want = n2.getNode();
                                }
                                else
                                {
                                    coll.add(n2.getNode());
                                }
                            }
                        }                    
                        coll.remove(want);
                        //System.out.println("Coll: " + coll.toString());
                        //System.out.println("want: " + want);
                        ConnectWithCertainNode(ZRoom,coll,want); 
                        if (!ZRoom.getAssignmentAreas().isEmpty())
                        {
                            for (Node n: AssignmentNodes.get(ZRoom))
                            {
                                Edge edge = new Edge(EdgeCount++,n,want);
                                mapping.setEdgeLevel(edge, Level.Equal);
                                graph.addEdge(edge);
                                if( debug ) {System.out.println("Assign Edge " + edge + "in Room: " + ZRoom.getName());}        
                                edgesCap.set(edge,Integer.MAX_VALUE);
                                
                            }
                        }
                    }
                    //exactly one room with more than one door and one evacuation area inside
                    else if (num==1 && numEvac2==0 && ZRoom.getEvacuationAreas().size()==1)
                    {
                        if( debug ) {System.out.println("Case 4b1) for Room: " + ZRoom.getName());}
                        HashMap<Point,Integer> doors1 = MoreDoorRoom.getDoors();
                        if( debug ) {System.out.println("MoreDoorRoom: " + MoreDoorRoom.getName());}
                        HashMap<Point,Integer> doors2 = ZRoom.getDoors();
                        Node center = CenterNodeForRoom.get(ZRoom);
                        //connect more door room with center
                        for (Point p1: doors1.keySet())
                        {
                            for(Point p2: doors2.keySet() )
                            {
                                if (p1.equals(p2))
                                {
                                    Node node1 = connection.get(p1);
                                    Edge edge = new Edge(EdgeCount++,node1,center);
                                    if( debug ) {System.out.println("Connection1: " + edge);}
                                    mapping.setEdgeLevel(edge, Level.Equal);
                                    graph.addEdge(edge);
                                    //TODO: set edge capacity correctly
                                    edgesCap.set(edge, Integer.MAX_VALUE);
                                    RoomEdges.add(edge);
                                }
                            }
                        }
                        //connect all the other rooms with center
                        for (Room r: neighbRooms)
                        {
                            if (r != MoreDoorRoom)
                            { 
                                Node door2 = DoorNodeForRoom.get(r);
                                if (door2 != null)
                                {    
                                    Edge edge = new Edge(EdgeCount++,center, door2);
                                    if( debug ) {System.out.println("Connection2: " + edge);}
                                    mapping.setEdgeLevel(edge, Level.Equal);
                                    graph.addEdge(edge);
                                    //TODO: set edge capacity correctly
                                    edgesCap.set(edge, Integer.MAX_VALUE);
                                    RoomEdges.add(edge);
                                }
                            }
                        }
                        //connect the evacuation node with center
                        for (Node node:EvacuationNodes.get(ZRoom))
                        {
                            Edge edge = new Edge(EdgeCount++,center,node);
                            if( debug ) {System.out.println("Connection3: " + edge);}
                            mapping.setEdgeLevel(edge, Level.Equal);
                            graph.addEdge(edge);
                            //TODO: set edge capacity correctly (smaller than infinity..)
                            edgesCap.set(edge, 10);
                            RoomEdges.add(edge);
                        }
                        
                    }               
                else
                {
                     if( debug ) {System.out.println("Case 5 for Room: " + ZRoom.getName());}
                     SmallestRectangle rectangle = FindSmallestRectangle(room);
                     Point center1 = new Point((int)rectangle.getCenter().getX(),(int)rectangle.getCenter().getY());
                     List<PlanPoint> rec = rectangle.getPoints();   
                     Point nw = rec.get(0); Point ne = rec.get(1); Point sw = rec.get(2); Point se = rec.get(3);
                     double width = Math.sqrt(Math.pow(nw.getX()-ne.getX(),2) + Math.pow(nw.getY()-ne.getY(),2));
                     double length = Math.sqrt(Math.pow(nw.getX()-sw.getX(),2) + Math.pow(nw.getY()-sw.getY(),2));
                     if( debug ) {System.out.println("width: " + width + "length: " + length);}
                     //room is rectangular        
                     if ((width > 2*length) || (length > 2*width))
                     {    
                        FindRectangulationEdges(ZRoom);
                     }
                 //room nearly quadratic
                 else 
                 {
                     Node center = CenterNodeForRoom.get(ZRoom);
                     int center_x = (int)mapping.nodeRectangles.get(center).getCenterX(); 
                     int center_y = (int)mapping.nodeRectangles.get(center).getCenterY();
                     Point centerPoint = new Point(center_x,center_y);
                     Collection<PositionNode> doors = DoorNodesForRoom.get(ZRoom);
                     
                     if (!EvacuationNodes.get(ZRoom).isEmpty())
                     {
                         for (Node node: EvacuationNodes.get(ZRoom))
                         {
                             int evac_x = (int)mapping.nodeRectangles.get(node).getCenterX();
                             int evac_y = (int)mapping.nodeRectangles.get(node).getCenterY();
                             Point evacPoint = new Point(evac_x,evac_y);
                             Edge edge = new Edge(EdgeCount++,node,center);
                             if( debug ) {System.out.println("Edge between center and evacuation node: " + edge);}
                             mapping.setEdgeLevel(edge, Level.Equal);
                             graph.addEdge(edge);
                             //TODO: set edge capacity correctly
                             edgesCap.set(edge, Integer.MAX_VALUE);
                             RoomEdges.add(edge);
                             for (PositionNode door: doors)
                             {
                                 Point doorPoint = new Point(door.getPosition().x,-door.getPosition().y);
                                 //System.out.println("assignPoint " + assignPoint + " door " + doorPoint + " centerPoint " + centerPoint);
                                 double dirPath = calculateDistance(evacPoint, doorPoint);
                                 double nondirPath = calculateDistance(evacPoint, centerPoint) + calculateDistance(centerPoint,doorPoint);
                                 //System.out.println("short " + dirPath + " long " + nondirPath);
                                 if (nondirPath > 1.5*dirPath)
                                 {
                                     Edge shortedge = new Edge(EdgeCount++,node,door.getNode());
                                     if( debug ) {System.out.println("direct edge between assignment node and door: " + shortedge);}
                                     mapping.setEdgeLevel(shortedge, Level.Equal);
                                     graph.addEdge(shortedge);
                                     //TODO: set edge capacity correctly
                                     edgesCap.set(shortedge, Integer.MAX_VALUE);
                                     RoomEdges.add(shortedge);
                                 }
                             }
                         }  
                     }
                     if (!AssignmentNodes.get(ZRoom).isEmpty())
                     {
                         for (Node node: AssignmentNodes.get(ZRoom))
                         {
                             int assign_x = (int)mapping.nodeRectangles.get(node).getCenterX();
                             int assign_y = (int)mapping.nodeRectangles.get(node).getCenterY();
                             Point assignPoint = new Point(assign_x,assign_y);
                             Edge edge = new Edge(EdgeCount++,node,center);
                             if( debug ) {System.out.println("edge between center and assignment node: " + edge);}
                             mapping.setEdgeLevel(edge, Level.Equal);
                             graph.addEdge(edge);
                             edgesCap.set(edge, Integer.MAX_VALUE);
                             RoomEdges.add(edge);
                             for (PositionNode door: doors)
                             {
                                 Point doorPoint = new Point(door.getPosition().x,-door.getPosition().y);
                                 //System.out.println("assignPoint " + assignPoint + " door " + doorPoint + " centerPoint " + centerPoint);
                                 double dirPath = calculateDistance(assignPoint, doorPoint);
                                 double nondirPath = calculateDistance(assignPoint, centerPoint) + calculateDistance(centerPoint,doorPoint);
                                 //System.out.println("short " + dirPath + " long " + nondirPath);
                                 if (nondirPath > 1.5*dirPath)
                                 {
                                     Edge shortedge = new Edge(EdgeCount++,node,door.getNode());
                                     if( debug ) {System.out.println("direct edge between assignment node and door: " + shortedge);}
                                     mapping.setEdgeLevel(shortedge, Level.Equal);
                                     graph.addEdge(shortedge);
                                     edgesCap.set(shortedge, Integer.MAX_VALUE);
                                     RoomEdges.add(shortedge);
                                 }
                             }
                         }  
                     }   
                     for (AssignmentArea area: ZRoom.getAssignmentAreas())
                     {
                         if (NodesForAssignArea.containsKey(area))
                         {
                             //System.out.println("Center Point: " + centerPoint);
                             for (PositionNode door: doors)
                             {
                                List<PositionNode> nearest = FindNearestAssignNodes(NodesForAssignArea.get(area),door);
                                for (PositionNode near: nearest )
                                {
                                    Edge edge = new Edge(EdgeCount++,near.getNode(),door.getNode());
                                    if( debug ) {System.out.println("edge between door and nearest assignment node... " + edge);}
                                    mapping.setEdgeLevel(edge, Level.Equal);
                                    graph.addEdge(edge);
                                    edgesCap.set(edge, Integer.MAX_VALUE);
                                    RoomEdges.add(edge); 
                                }
                             }
                             Point cen = new Point(center_x,-center_y);
                             PositionNode centerPos = new PositionNode(center,cen);
                             List<PositionNode> nearestToCenter = FindNearestAssignNodes(NodesForAssignArea.get(area),centerPos);
                             for (PositionNode near: nearestToCenter)
                             {
                                Edge shortedge = new Edge(EdgeCount++,near.getNode(),center);
                                if( debug ) {System.out.println("edge between center and nearest assignment node...: " + shortedge);}
                                mapping.setEdgeLevel(shortedge, Level.Equal);
                                graph.addEdge(shortedge);
                                edgesCap.set(shortedge, Integer.MAX_VALUE);
                                RoomEdges.add(shortedge);
                             }
                         }
                     }
                     
                     for (PositionNode node: doors)
                     {
                         Edge edge = new Edge(EdgeCount++,node.getNode(),center);
                         if( debug ) {System.out.println("edge between center and door: " + edge);}
                         mapping.setEdgeLevel(edge, Level.Equal);
                         graph.addEdge(edge);
                         //TODO: set edge capacity correctly
                         edgesCap.set(edge, Integer.MAX_VALUE);
                         RoomEdges.add(edge);
                     }
                
                 }
          
                }
                //create edge between assignment- and evacuation nodes
                /*for (Node node_a: EvacuationNodes.get(ZRoom))
                {
                    for (Node node_b: AssignmentNodes.get(ZRoom))
                    {
                        Edge edge = new Edge(EdgeCount++, node_a, node_b);
                        mapping.setEdgeLevel(edge, Level.Equal);
                        graph.addEdge(edge);
                        //TODO: set edge capacity correctly
                        edgesCap.set(edge, 10);
                        RoomEdges.add(edge);
                    }      
                }*/
                
                
            }
        //stores all the created edges for the current Room    
        EdgesForRoom.put(ZRoom, RoomEdges);            

        }
    model.setEdgeCapacities(edgesCap); 
    }
    
    @Override
    protected void computeTransitTimes()
    {  
        //first set it to the euclidean distance between two points
        exactTransitTimes = new IdentifiableDoubleMapping<>( 1 );
        List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();

        for (Edge edge: model.getGraph().edges())
        {
            if (edge.end()!= model.getSupersink() && edge.start()!= model.getSupersink())
            {    
                double startx = mapping.getNodeRectangles().get(edge.start()).getCenterX();
                double starty = mapping.getNodeRectangles().get(edge.start()).getCenterY();

                double endx = mapping.getNodeRectangles().get(edge.end()).getCenterX();
                double endy = mapping.getNodeRectangles().get(edge.end()).getCenterY();

                double time = Math.sqrt(Math.pow((startx-endx),2) + Math.pow((starty-endy),2));
                PropertyContainer propertyContainer = PropertyContainer.getInstance();
                int precision = propertyContainer.getAs( "converter.GraphPrecision", Integer.class );
                time = time * precision / 400.0d;
                exactTransitTimes.set(edge, time);
            }
            else
            {
                exactTransitTimes.set(edge, 0.0);
            }
        }
        if( debug ) {System.out.println("TransitTimes without delay or inaccessible areas: " + exactTransitTimes);}
        
        for (ZToGraphRoomRaster room: rasteredRooms)
        {
            Room ZRoom = room.getRoom();
                      
            //room contains inaccessible areas or delay areas --> recompute transittimes
            if ((!ZRoom.getDelayAreas().isEmpty()) || (!ZRoom.getInaccessibleAreas().isEmpty()) || (!ZRoom.getStairAreas().isEmpty()) )
            {
                //System.out.println("Room with delay/inacc/stairs: " + ZRoom.getName() );
                double startX,startY,endX,endY;
                ListSequence<Edge> roomEdges = EdgesForRoom.get(room.getRoom());
                //System.out.println("Room Edges: " + roomEdges.toString());
                for (Edge edge: roomEdges)
                    {
                        boolean containsEdge = false;
                        Node node1 = edge.start();
                        Node node2 = edge.end();
                        boolean directionDown = false;
                        if (mapping.getNodeRectangles().get(node1).getCenterX() < mapping.getNodeRectangles().get(node2).getCenterX())
                        {
                            startX = mapping.getNodeRectangles().get(node1).getCenterX();
                            startY = -(mapping.getNodeRectangles().get(node1).getCenterY());
                            endX = mapping.getNodeRectangles().get(node2).getCenterX();
                            endY = -(mapping.getNodeRectangles().get(node2).getCenterY());
                        }
                        else
                        {
                            endX = mapping.getNodeRectangles().get(node1).getCenterX();
                            endY = -(mapping.getNodeRectangles().get(node1).getCenterY());
                            startX = mapping.getNodeRectangles().get(node2).getCenterX();
                            startY = -(mapping.getNodeRectangles().get(node2).getCenterY());   
                        }
                        double UpperY, LowerY;
                        if (startY < endY)
                        {
                            UpperY = startY;
                            LowerY = endY;
                            directionDown = true; 
                        }
                        else
                        {
                            UpperY = endY;
                            LowerY = startY;
                        }
                        //System.out.println("Start: " + startX + " " + startY + "End: " + endX + " " + endY);
                        List<PlanPoint> points = new LinkedList<>();
                        //saves all points of the edge
                        if (endX-startX != 0)
                        {
                            double a = (endY-startY)/(endX-startX);
                            double b = endY - (a*endX);
                            for (int i=(int)startX; i<(int)endX+1; i++)
                            {
                                PlanPoint p = new PlanPoint();
                                p.x = i;
                                p.y = (int)(a*i+b);
                                points.add(p);
                                //System.out.println("p: " + p);
                            }
                        }
                        else
                        {
                            for (int i = (int)UpperY; i<(int) LowerY+1;i++)
                            {
                                PlanPoint p = new PlanPoint();
                                p.x = (int)endX;
                                p.y = i;
                                points.add(p);
                                //System.out.println("p: " + p);
                            }
                        }
                        
                        //recompute transittimes for stair areas
                        if (!ZRoom.getStairAreas().isEmpty())
                        {
                            for (StairArea stair: ZRoom.getStairAreas())
                            {                 
                                int count = 0;
                                for (PlanPoint p: points){
                                    if (stair.contains(p)){                                       
                                        count++;
                                    }
                                }
                                if (count > 0){
                                     //System.out.println("Stair area contains edge: " + edge );
                                     Edge back = new Edge(EdgeCount++,edge.end(),edge.start());
                                     mapping.setEdgeLevel(back, Level.Equal);
                                     graph.setEdge(back);
                                     if( debug ) {System.out.println("backward stair edge: " + back);}                   
                                     edgesCap.set(back,edgesCap.get(edge));
                                     stairEdges.add(edge); stairEdges.add(back);
                                     numStairEdges = numStairEdges + 2;
                                     
                                     boolean directionUp;
                                     int start_x = (int)mapping.getNodeRectangles().get(edge.start()).getCenterX();
                                     int start_y = -(int)mapping.getNodeRectangles().get(edge.start()).getCenterY();
                                     int end_x = (int)mapping.getNodeRectangles().get(edge.end()).getCenterX();
                                     int end_y = -(int)mapping.getNodeRectangles().get(edge.end()).getCenterY();
                                     Point edgeStart = new Point(start_x,start_y);
                                     Point LowerStair = new Point((stair.getLowerLevelEnd().getXInt() + stair.getLowerLevelStart().getXInt())/2, (stair.getLowerLevelEnd().getYInt() + stair.getLowerLevelStart().getYInt())/2);
                                     Point UpperStair = new Point((stair.getUpperLevelEnd().getXInt() + stair.getUpperLevelStart().getXInt())/2, (stair.getUpperLevelEnd().getYInt() + stair.getUpperLevelStart().getYInt())/2);
                                     double startToLower = calculateDistance(edgeStart,LowerStair);
                                     double startToUpper = calculateDistance(edgeStart,UpperStair);
                                     if (startToLower < startToUpper){
                                        directionUp = true; 
                                     }
                                     else
                                     {
                                         directionUp = false;
                                     }
                                     //System.out.println("Direction Up: " + directionUp);
                                     double stairsize = ((double)count)/((double)points.size());
                                     //System.out.println("stairsize: " + stairsize); 
                                     double UpSpeed = stair.getSpeedFactorUp();
                                     double DownSpeed = stair.getSpeedFactorDown();
                                     
                                     Vector2 first = new Vector2();
                                     first.setX((LowerStair.getX()-UpperStair.getX())/2);
                                     first.setY((LowerStair.getY()-UpperStair.getY())/2);
                                     if( debug ) {System.out.println("first Vector: " + first.getX() + " " + first.getY());}
                                     Vector2 second = new Vector2();
                                     second.setX((start_x-end_x)/2);
                                     second.setY((start_y-end_y)/2);
                                     if( debug ) {System.out.println("second Vector: " + second.getX() + " " + second.getY());}
                                     double angle = first.getAngleBetween(first,second);
                                     //System.out.println("get Angle between: " + angle);
                                     double factor = -1.0/90.0*angle + 1;
                                     //System.out.println("factor: " + factor);
                                     double transitUp,transitDown;
                                     
                                     double transitOrig = exactTransitTimes.get(edge);
                                     //System.out.println("Transit Orig: " + transitOrig);
                                     double transitUpDelay = (1.0-stairsize)*exactTransitTimes.get(edge) + stairsize*exactTransitTimes.get(edge)*(1.0/UpSpeed);
                                     //System.out.println("Transit Up Delay: " + transitUpDelay);
                                     double transitDownDelay = (1.0-stairsize)*exactTransitTimes.get(edge) + stairsize*exactTransitTimes.get(edge)*(1.0/DownSpeed);
                                     //System.out.println("Transit Down Delay: " + transitDownDelay);
                                     transitUp = (transitUpDelay - transitOrig)*factor + transitOrig;
                                     //System.out.println("Transit Up: " + transitUp);
                                     transitDown = (transitDownDelay - transitOrig)*factor + transitOrig;
                                     //System.out.println("Transit Down: " + transitDown);
                                     
                                     if (directionUp){
                                        exactTransitTimes.set(edge, transitUp);
                                        exactTransitTimes.set(back, transitDown);
                                     }
                                     else{
                                        exactTransitTimes.set(edge, transitDown);
                                        exactTransitTimes.set(back, transitUp);
                                     }
                                 }
                             }
                    
                        }
                        
                        if (!ZRoom.getDelayAreas().isEmpty())
                        {
                            for (DelayArea delay: ZRoom.getDelayAreas())
                            { 
                                int count = 0;
                                //take into account all points of an edge and check if they are inside the area polygon
                                for (PlanPoint p: points){
                                    if (delay.contains(p)){
                                        count++;
                                    }
                                }
                                //System.out.println("punkte: " + points.size());
                                //System.out.println("count: " + count);
                                double delaysize = ((double)count)/((double)points.size());
                                //System.out.println("delaysize: " + delaysize); 
                                double speed = delay.getSpeedFactor();
                                double transit = (1.0-delaysize)*exactTransitTimes.get(edge) + delaysize*exactTransitTimes.get(edge)*(1.0/speed);
                                exactTransitTimes.set(edge, transit);
                            }
                        }
                        if ((!ZRoom.getInaccessibleAreas().isEmpty()) || (!ZRoom.getBarriers().isEmpty()))
                        {
                            List<InaccessibleArea> barr = new LinkedList<>();
                            for (InaccessibleArea barrier: ZRoom.getInaccessibleAreas())
                            {
                                barr.add(barrier);
                            }
                            for (InaccessibleArea barrier: ZRoom.getBarriers())
                            {
                                barr.add(barrier);
                            }
                            
                            for (InaccessibleArea barrier: barr)
                            { 
                               //System.out.println("Barrieren: " + barr.toString());
                               for (PlanPoint p: points)
                                {
                                    if (barrier.contains(p))
                                    {
                                        //System.out.println("Inaccessible Area contains edge: " + edge);
                                        containsEdge = true;
                                        break;
                                    }
                                } 
                                if (containsEdge) 
                                {
                                int barrEndX = barrier.getxOffset() + barrier.getWidth(); 
                                        int barrEndY = barrier.getyOffset() + barrier.getHeight();
                                //System.out.println("Barrier x Offset: " + barrier.getxOffset());
                                //System.out.println("Barrier y Offset: " + barrier.getyOffset());
                                //System.out.println("Barrier Endx: " + barrEndX);
                                //System.out.println("Barrier Endy: " + barrEndY);
                                //System.out.println("Upper Y: " + UpperY);
              
                                if (ZRoom.getDoorEdges().size()>=0)
                                {
                                    double Path1,Path2;
                                    if(barrier.getxOffset() >= startX && barrEndX <= endX && barrier.getyOffset() >= UpperY && barrEndY <= LowerY)
                                    { 
                                            //System.out.println("case 0...");                                         
                                            if (directionDown){
                                                Path1 = Math.sqrt(Math.pow(startX-barrier.getxOffset(), 2)+Math.pow(startY-barrEndY,2)) + Math.sqrt(Math.pow(barrier.getxOffset()-endX, 2)+Math.pow(barrEndY-endY,2));
                                                Path2 = Math.sqrt(Math.pow(startX-barrEndX,2)+Math.pow(startY-barrier.getyOffset(),2)) + Math.sqrt(Math.pow(barrEndX-endX,2)+Math.pow(barrier.getyOffset()-endY,2));
                                            }
                                            else{
                                                Path1 = Math.sqrt(Math.pow(startX-barrEndX, 2)+Math.pow(startY-barrEndY,2)) + Math.sqrt(Math.pow(barrEndX-endX, 2)+Math.pow(barrEndY-endY,2));
                                                Path2 = Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrier.getyOffset(),2)) + Math.sqrt(Math.pow(barrier.getxOffset()-endX,2)+Math.pow(barrier.getyOffset()-endY,2));
                                            }         
                                    }
                                    //x values of barrier are between two x-values of edge endpoints
                                    else if (barrier.getxOffset() >= startX && barrEndX <= endX )
                                    {
                                        //does not fit on upper side
                                        if (barrier.getyOffset() <= UpperY && barrEndY <= LowerY){
                                            //System.out.println("Case 1a....");
                                            Path1 = Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrier.getyOffset(),2))+Math.sqrt(Math.pow(barrier.getxOffset()-barrEndX,2)) + Math.sqrt(Math.pow(barrEndX-endX, 2)+Math.pow(barrEndY-endY,2));
                                            if (directionDown){
                                                Path2 = Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrEndY,2)) + Math.sqrt(Math.pow(barrier.getxOffset()-endX, 2)+Math.pow(barrEndY-endY,2)) ;
                                            }
                                            else{
                                                Path2 = Math.sqrt(Math.pow(startX-barrEndX, 2)+Math.pow(startY-barrEndY,2));
                                            }
                                        }
                                        //does not fit on lower side
                                        else if (barrier.getyOffset() >= UpperY && barrEndY >= LowerY)
                                        {
                                            //System.out.println("Case 1b...");
                                            Path1 = Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrEndY,2)) + Math.sqrt(Math.pow(barrier.getWidth(),2)) + Math.sqrt(Math.pow(barrEndX-endX,2)+Math.pow(barrEndY-endY,2));
                                            if (directionDown){
                                                Path2 = Math.sqrt(Math.pow(startX-barrEndX,2) + Math.pow(startY-barrier.getyOffset(),2))+ Math.sqrt(Math.pow(barrier.getxOffset()-endX,2)+Math.pow(barrier.getyOffset()-endY,2));
                                            }
                                            else{
                                                Path2 = Math.sqrt(Math.pow(startX-barrier.getxOffset(), 2)+Math.pow(startY-barrier.getyOffset(),2)) + Math.sqrt(Math.pow(barrier.getxOffset()-endX,2)+Math.pow(barrier.getyOffset()-endY,2));
                                            }
                                        }  
                                        //does not fit on both sides
                                        else{
                                            //System.out.println("Case 1c...");
                                            Path1 = Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrier.getyOffset(),2)) + Math.sqrt(Math.pow(barrier.getWidth(),2)) + Math.sqrt(Math.pow(barrEndX-endX,2)+Math.pow(barrier.getyOffset()-endY,2)); 
                                            Path2 = Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrEndY,2)) + Math.sqrt(Math.pow(barrier.getWidth(), 2)) + Math.sqrt(Math.pow(barrEndX-endX,2)+Math.pow(barrEndY-endY,2));
                                        }
                                    }
                                    else if (barrier.getyOffset() >= UpperY && barrEndY <= LowerY )
                                    {
                                        //does not fit on left side
                                        if (barrier.getxOffset() < startX && barrEndX <= endX)
                                        {
                                            //System.out.println("Case 2a)... ");
                                            if (directionDown){
                                                Path1 = Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrier.getyOffset(),2)) + Math.sqrt(Math.pow(barrier.getHeight(),2)) + Math.sqrt(Math.pow(barrier.getxOffset()-endX,2)+Math.pow(barrEndY-endY,2));                                  
                                                Path2 = Math.sqrt(Math.pow(startX-barrEndX,2)+Math.pow(startY-barrier.getyOffset(),2)) + Math.sqrt(Math.pow(barrier.getxOffset()-endX,2)+Math.pow(barrier.getyOffset()-endY,2));
                                            }
                                            else{
                                                Path1 = Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrEndY,2)) + Math.sqrt(Math.pow(barrier.getHeight(),2)) + Math.sqrt(Math.pow(barrier.getxOffset()-endX,2)+Math.pow(barrier.getyOffset()-endY,2));
                                                Path2 = Math.sqrt(Math.pow(startX-barrEndX,2)+Math.pow(startY-barrEndY,2)) + Math.sqrt(Math.pow(barrEndX-endX,2)+Math.pow(barrEndY-endY,2));
                                            }
                                        }
                                        //does not fit on right side
                                        else if (barrier.getxOffset() >= startX && barrEndX > endX)
                                        {
                                            //System.out.println("case 2b)...");
                                            if (directionDown){
                                                Path1 = Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrEndY,2)) + Math.sqrt(Math.pow(barrier.getxOffset()-endX,2)+Math.pow(barrEndY-endY,2));
                                                Path2 = Math.sqrt(Math.pow(startX-barrEndX,2)+Math.pow(startY-barrier.getyOffset(),2)) + Math.sqrt(Math.pow(barrier.getHeight(),2)) + Math.sqrt(Math.pow(barrEndX-endX,2)+Math.pow(barrEndY-endY,2));
                                            }
                                            else{
                                                Path1 = Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrier.getyOffset(),2)) + Math.sqrt(Math.pow(barrier.getxOffset()-endX,2)+Math.pow(barrier.getyOffset()-endY,2));                                                
                                                Path2 = Math.sqrt(Math.pow(startX-barrEndX,2)+Math.pow(startY-barrEndY,2)) + Math.sqrt(Math.pow(barrier.getHeight(),2)) + Math.sqrt(Math.pow(barrEndX-endX,2)+Math.pow(barrier.getyOffset()-endY,2));
                                            }
                                        } 
                                        //does not fit on both sides
                                        else 
                                        {
                                            //System.out.println("Case 2c...");
                                            if (directionDown){
                                                Path1 = Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrier.getyOffset(),2)) + Math.sqrt(Math.pow(barrier.getHeight(),2)) + Math.sqrt(Math.pow(barrier.getxOffset()-endX,2)+Math.pow(barrEndY-endY,2));
                                                Path2 = Math.sqrt(Math.pow(startX-barrEndX,2)+Math.pow(startY-barrier.getyOffset(),2)) + Math.sqrt(Math.pow(barrier.getHeight(),2)) + Math.sqrt(Math.pow(barrEndX-endX,2)+Math.pow(barrEndY-endY,2));
                                            }
                                            else{
                                                Path1 = Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrEndY,2)) + Math.sqrt(Math.pow(barrier.getHeight(),2)) + Math.sqrt(Math.pow(barrier.getxOffset()-endX,2)+Math.pow(barrier.getyOffset()-endY,2));
                                                Path2 = Math.sqrt(Math.pow(startX-barrEndX,2)+Math.pow(startY-barrEndY,2)) + Math.sqrt(Math.pow(barrier.getHeight(),2)) + Math.sqrt(Math.pow(barrEndX-endX,2)+Math.pow(barrier.getyOffset()-endY,2));
                                            }
                                        }
                                    }
                                    else if (barrier.getxOffset() > startX)
                                    {
                                        //System.out.println("case 3");
                                        if (directionDown){
                                            Path1 = 2*(Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrEndY,2)) + Math.sqrt(Math.pow(barrier.getxOffset()-endX,2)+Math.pow(barrEndY-endY,2)));
                                            Path2 = 0;
                                        }
                                        else{
                                            Path1 = 2*(Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrier.getyOffset(),2))+Math.sqrt(Math.pow(barrier.getxOffset()-endX,2)+Math.pow(barrier.getyOffset()-endY,2)));
                                            Path2 = 0;
                                        }
                                    }
                                    else if (barrier.getxOffset() < startX)
                                    {
                                        //System.out.println("case 4");
                                        if (directionDown){
                                            Path1 = 2*(Math.sqrt(Math.pow(startX-barrEndX,2)+Math.pow(startY-barrier.getyOffset(),2)) + Math.sqrt(Math.pow(barrEndX-endX,2)+Math.pow(barrier.getyOffset()-endY,2)));
                                            Path2 = 0;
                                        }
                                        else{
                                            Path1 = 2*(Math.sqrt(Math.pow(startX-barrEndX,2)+Math.pow(startY-barrEndY,2)) + Math.sqrt(Math.pow(barrEndX-endX,2)+Math.pow(barrEndY-endY,2)));
                                            Path2 = 0;
                                        }
                                    }
                                    else 
                                    {
                                        if( debug ) {System.out.println("Currently not considered case");}
                                        Path1=1;Path2=1;
                                    }
                                    PropertyContainer propertyContainer = PropertyContainer.getInstance();
                                    int precision = propertyContainer.getAs( "converter.GraphPrecision", Integer.class );
                                    //System.out.println("Ergebnis: " + Path1*precision/400.0d + " " + Path2*precision/400.0d);
                                    double PathLength = (0.5*Path1+0.5*Path2) * precision / 400.0d;
                                    exactTransitTimes.set(edge, PathLength);  
                                    
                                }
                            }
                            }
                        }
                    }
            }            
    }  
        if( debug ) {System.out.println("TransitTimes: " + exactTransitTimes);
        System.out.println("Capacities: " + model.getEdgeCapacities().toString());}
        System.out.println("NumEdges: " + model.getGraph().edges().size());
    }
    
    public HashMap<Point,Integer> getFloorDoors(Room room)
        {
            HashMap<Point,Integer> doors = new HashMap<>();
            LinkedList<RoomEdge> edges = new LinkedList<>();

            for (RoomEdge edge: room.getEdges() ){
                //System.out.println("link target: " + edge.getLinkTarget());
                if (!(edge.getLinkTarget()==null)){
                    if (!(edge.getLinkTarget().getRoom().getAssociatedFloor().equals(room.getAssociatedFloor())))
                    {
                        //System.out.println("Diese Kante: " + edge);
                        edges.add(edge);
                        FloorConnection.put(room, edge.getLinkTarget().getRoom());
                    }
                }
            }
            //create one door for neighbouring edges
            if (edges.size() >1)
            {
                RoomEdge start = edges.peekFirst();
                int longwidth = start.length();
                RoomEdge end = start;
                RoomEdge next;
                
                while (edges.size() > 1)
                {
                    RoomEdge first = edges.poll();
                    next = edges.getFirst();
                    
                    if (next.isNeighbour(first))
                    {
                        longwidth = longwidth + next.length();

                        if (edges.size() == 1)
                        {
                            end=next;
                            int xpos = (start.getSource().getXInt() + end.getTarget().getXInt())/2;
                            int ypos = (start.getSource().getYInt() + end.getTarget().getYInt())/2;
                            Point p = new Point(xpos,ypos);
                            doors.put(p,longwidth); 
                            Node node = new Node(nodeCount++);
                            isfloorNode.set(node, Boolean.TRUE);
                            linktarget.put(node, next.getLinkTarget().getRoom());
                            if( debug ) {System.out.println("Floor Connecting Node_1: " + node + "at position: " + p + " for room: " + room.getName());}  
                            graph.setNode(node);
                            PositionNode pos = new PositionNode(node,p,longwidth);
                            floorNodes.add(pos);
                            doorNodes.add(pos);
                            int width1 = longwidth/1000*2;
                            nodesCap.add(node, width1);
                            NodeRectangle rec = new NodeRectangle(p.x-1,-(p.y+1), p.x+1,-(p.y-1));
                            mapping.setNodeRectangle(node, rec );
                            model.getZToGraphMapping().getNodeFloorMapping().set( node,getProblem().getFloorID(room.getAssociatedFloor()));
                            model.getZToGraphMapping().setIsEvacuationNode( node, false );
                            model.getZToGraphMapping().setIsSourceNode(node, false);
                            model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                        }
                    }
                    else
                    {
                        end = first;
                        int xpos = (start.getSource().getXInt() + end.getTarget().getXInt())/2;
                        int ypos = (start.getSource().getYInt() + end.getTarget().getYInt())/2;
                        Point p = new Point(xpos,ypos);
                        doors.put(p,longwidth);  
                        Node node1 = new Node(nodeCount++);
                        isfloorNode.set(node1, Boolean.TRUE);
                        linktarget.put(node1, first.getLinkTarget().getRoom());
                        if( debug ) {System.out.println("Floor Connecting Node_2: " + node1 + "at position: " + p + " for room: " + room.getName());}  
                        graph.setNode(node1);
                        PositionNode pos1 = new PositionNode(node1,p,longwidth);
                        floorNodes.add(pos1);
                        doorNodes.add(pos1);
                        int width1_1 = longwidth/1000*2;
                        nodesCap.add(node1, width1_1);
                        NodeRectangle rec1 = new NodeRectangle(p.x-1,-(p.y+1), p.x+1,-(p.y-1));
                        mapping.setNodeRectangle(node1, rec1 );
                        model.getZToGraphMapping().getNodeFloorMapping().set( node1,getProblem().getFloorID(room.getAssociatedFloor()));
                        model.getZToGraphMapping().setIsEvacuationNode( node1, false );
                        model.getZToGraphMapping().setIsSourceNode(node1, false);
                        model.getZToGraphMapping().setIsDeletedSourceNode( node1, false );
                        if (edges.size()==1)
                        {
                            int w = next.length();
                            int x = (next.getSource().getXInt() + next.getTarget().getXInt())/2;
                            int y = (next.getSource().getYInt() + next.getTarget().getYInt())/2;
                            Point po = new Point(x,y);
                            doors.put(po,w);
                            Node node = new Node(nodeCount++);
                            isfloorNode.set(node, Boolean.TRUE);
                            linktarget.put(node, next.getLinkTarget().getRoom());
                            if( debug ) {System.out.println("Floor Connecting Node_2: " + node + "at position: " + po + " for room: " + room.getName()); } 
                            graph.setNode(node);
                            PositionNode pos = new PositionNode(node,p,longwidth);
                            floorNodes.add(pos);
                            doorNodes.add(pos);
                            int width1 = longwidth/1000*2;
                            nodesCap.add(node, width1);
                            NodeRectangle rec = new NodeRectangle(p.x-1,-(p.y+1), p.x+1,-(p.y-1));
                            mapping.setNodeRectangle(node, rec );
                            model.getZToGraphMapping().getNodeFloorMapping().set( node,getProblem().getFloorID(room.getAssociatedFloor()));
                            model.getZToGraphMapping().setIsEvacuationNode( node, false );
                            model.getZToGraphMapping().setIsSourceNode(node, false);
                            model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                        }
                        start = next;
                        longwidth = start.length();
                    }
                }
    
            }
            else if (edges.size()==1)
            {
                RoomEdge x = edges.element();
                int width = x.length();
                int xpos = (x.getSource().getXInt() + x.getTarget().getXInt())/2;
                int ypos = (x.getSource().getYInt() + x.getTarget().getYInt())/2;
                Point p = new Point(xpos,ypos);
                doors.put(p,width);
                Node node = new Node(nodeCount++);
                isfloorNode.set(node, Boolean.TRUE);
                linktarget.put(node, x.getLinkTarget().getRoom());
                if( debug ) {System.out.println("Floor Connecting Node_3: " + node + " at position: " + p +" for room: " + room.getName()); } 
                graph.setNode(node);
                PositionNode pos = new PositionNode(node,p,width);
                floorNodes.add(pos);
                doorNodes.add(pos);
                int width1 = width/1000*2;
                //System.out.println("Knotenkap: " + width);
                nodesCap.add(node, width1);
                NodeRectangle rec = new NodeRectangle(p.x-1,-(p.y+1), p.x+1,-(p.y-1));
                mapping.setNodeRectangle(node, rec );
                model.getZToGraphMapping().getNodeFloorMapping().set( node,getProblem().getFloorID(room.getAssociatedFloor()));
                model.getZToGraphMapping().setIsEvacuationNode( node, false );
                model.getZToGraphMapping().setIsSourceNode(node, false);
                model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
            }
            return doors;
        }
        
    
    protected void FindNeighbourRooms()
    {
        //System.out.println("Finding neighbourrooms started...");
        List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();
        
        for( ZToGraphRoomRaster room : rasteredRooms ) 
        {
            neighbours = new HashSet<>();
            List<RoomEdge> rEdges = room.getRoom().getEdges();
            for (RoomEdge e: rEdges)
            {
                if (e.getLinkTarget() != null)
                {
                    if (!(e.getLinkTarget().getRoom().getAssociatedFloor().equals(room.getRoom().getAssociatedFloor())))
                    {
                        neighbours.add(e.getLinkTarget().getRoom());
                    }
                }
            }
            //gives all dooredges for the first considered room  
            Collection<ds.z.Edge> dooredgesroom1 = room.getRoom().getDoorEdges();
            //look for all other rooms on the same floor
            for (ZToGraphRoomRaster room2:rasteredRooms )
            {
                //gives all dooredges for the other considered room
                Collection<ds.z.Edge> dooredgesroom2 = room2.getRoom().getDoorEdges();
                
                if (room2 != room && (room2.getRoom().getAssociatedFloor().equals(room.getRoom().getAssociatedFloor())))
                {                             
                    for (ds.z.Edge edge: dooredgesroom1)
                    {
                        for (ds.z.Edge edge2: dooredgesroom2)
                        {
     
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
                           
                           if (((first && second && third && fourth) || (first1 && second1 && third1 && fourth1)) )
                           {
                               if (!neighbours.contains(room2.getRoom()))
                               {
                                    neighbours.add(room2.getRoom());
                               }
                           }
                        }
                    }
                }
                
            } 
            NeighbourRooms.put(room.getRoom(), neighbours);
            numNeighb.put(room.getRoom(), neighbours.size());
        }
    }
    /*
     * Computes some values of neighbourrooms needed to create nodes and edges 
     * (the number of neighbourrooms with only one neighbour, the number of neighbouring evacuation areas)
     * @param room the room for that neighbourrooms are scanned
     */
    public void ComputeNeighbourRoomValues(Room room)
    {
               //gives the neighbours of the current room
               Collection<Room> neighbRooms = NeighbourRooms.get(room);
               num = 0; numEvac1 = 0; numEvac2 = 0; numAssign1=0; numAssign2=0;
               
               for (Room r : neighbRooms)
               {
                    //count rooms with more than one neighbour
                    if (numNeighb.get(r) > 1)
                    {
                        MoreDoorRoom = r;
                        num++;
                        if (!r.getEvacuationAreas().isEmpty()){                          
                            numEvac1++; //count evacuation areas for rooms with more than one door
                        }
                        if (!r.getAssignmentAreas().isEmpty()){
                            numAssign1++;
                        }
                    }
                    else
                    {
                        if (!r.getEvacuationAreas().isEmpty()){
                            numEvac2 = numEvac2 + r.getEvacuationAreas().size(); //count evacuation areas for rooms with only one door
                            EvacRoom = r;
                        }
                        if (!r.getAssignmentAreas().isEmpty()){
                            numAssign2++;
                        }
                        if (r.getEvacuationAreas().isEmpty() && r.getAssignmentAreas().isEmpty())
                        {                             
                                HashMap<Point,Integer> doors = r.getDoors();
                                for (Point p: doors.keySet()){
                                needsNoConnection.add(p);}   

                        }   
                    }
                }  
    }
    
    
    public HashMap<Room,Collection<Room>> getNeighbourRooms()
    {
        return NeighbourRooms;
    }
    
    public void createCenterNode(Room r, Point doorNode, Node connectNode)
    {   
              if( debug ) {System.out.println("Create Center...");}
              double x1 = doorNode.x;
              double y1 = doorNode.y;
              double x2 = mapping.nodeRectangles.get(connectNode).getCenterX();
              double y2 = mapping.nodeRectangles.get(connectNode).getCenterY();
              //System.out.println("x1: " + x1 + "y1: " + y1 + "x2: " + x2 + "y2: " + y2);              
              Point nodepos = new Point((int)(x1 + x2)/2,-((int)(y1 + y2)/2));
              Node node = new Node(nodeCount);
              if( debug ) {System.out.println("Centerknoten: " + node);}
              graph.setNode(node);
              CenterNodeForRoom.put(r, node);
              //TODO: define exact value of node capacity
              nodesCap.set(node, Integer.MAX_VALUE);
              NodeRectangle rec = new NodeRectangle(nodepos.x-1,nodepos.y+1, nodepos.x+1,nodepos.y-1);
              if (r.getInaccessibleAreas().size() > 0)
              {
                   PlanPoint p = new PlanPoint(); 
                   p.setLocation(nodepos.x, nodepos.y);
                   for (InaccessibleArea area: r.getInaccessibleAreas())
                   {
                       if (area.contains(p))
                       {
                                    if( debug ) {System.out.println("center lies on inaccessible area... ");}
                                    int areaCenterX = area.getxOffset() + (area.getWidth()/2);
                                    rec = new NodeRectangle(areaCenterX-1,-(area.getyOffset()-1),areaCenterX+1,-(area.getyOffset()-1));
                       }                                 
                    }
              }         
              mapping.setNodeRectangle(node, rec );
              model.getZToGraphMapping().getNodeFloorMapping().set( node,getProblem().getFloorID(r.getAssociatedFloor()));
              model.getZToGraphMapping().setIsEvacuationNode( node, false );
              model.getZToGraphMapping().setIsSourceNode(node, false);
              model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
              nodes.add(node);
              nodeCount++;                      
    }
    
    /*
     * creates a central node for a room corresponding to a smallest rectangle with 
     * arbitrary direction that contains all points of that room  
     */
    public void CreateCenterForRoom(ZToGraphRoomRaster room, List<PlanPoint> recPoints, Point center)
    {
        Room ZRoom = room.getRoom();
        Node node = new Node(nodeCount++);
        if( debug ) {System.out.println("Create node in the middle: " + node + "for: " + ZRoom.getName());}
        graph.setNode(node);
        CenterNodeForRoom.put(ZRoom, node);
        //TODO: define exact value of node capacity
        nodesCap.set(node, Integer.MAX_VALUE);        
        NodeRectangle n = new NodeRectangle((int)recPoints.get(0).getX(),-(int)recPoints.get(0).getY(),(int)recPoints.get(1).getX(),-(int)recPoints.get(1).getY(),(int)recPoints.get(2).getX(),-(int)recPoints.get(2).getY(),(int)recPoints.get(3).getX(),-(int)recPoints.get(3).getY());
         if (ZRoom.getInaccessibleAreas().size() > 0)
         {
               PlanPoint p = new PlanPoint(center.getX(),center.getY()); 
               for (InaccessibleArea area: ZRoom.getInaccessibleAreas())
               {
                    if (area.contains(p))
                    {
                         if( debug ) {System.out.println("center lies on inaccessible area... ");}
                         int areaCenterX = area.getxOffset() + (area.getWidth()/2);
                         n = new NodeRectangle(areaCenterX-1,-(area.getyOffset()-1),areaCenterX+1,-(area.getyOffset()-1));
                    }                                 
                }
          }                        
          mapping.setNodeRectangle(node, n );
          if( debug ) {System.out.println("Node Rectangle for center node: " + recPoints.get(0) + " " + recPoints.get(1) + " " + recPoints.get(2) + " " +  recPoints.get(3)) ;}
          //System.out.println("Center des Node Rectangles: " + n.getCenterX() + " " + n.getCenterY());
          model.getZToGraphMapping().getNodeFloorMapping().set( node,getProblem().getFloorID(room.getFloor()));
          model.getZToGraphMapping().setIsEvacuationNode( node, false );
          model.getZToGraphMapping().setIsSourceNode(node, false);
          model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                    
    }
    
    public SmallestRectangle FindSmallestRectangle(ZToGraphRoomRaster room)
    {
        SmallestRectangle rec = new SmallestRectangle();
        List<Vector2> givenPoints = new LinkedList<>();

        int size = room.getRaster()/2;
        int numCol = room.getColumnCount()*2;
        int numRow = room.getRowCount()*2; 

        for (int i=0; i<numCol+2;i++)
        {
            for (int j=0;j<numRow+2;j++)
            {
                 PlanPoint p = new PlanPoint(room.getXOffset()+(i*size),room.getYOffset()+(j*size));
                 if (room.getRoom().contains(p))
                 {
                      Vector2 v = new Vector2((double)p.x,(double)p.y);
                      givenPoints.add(v);
                 }
             }
         }
         rec.computeSmallestRectangle(givenPoints.size(), givenPoints);
         return rec;
    }
    
    public List<PlanPoint> rotateRectangle(List<PlanPoint> recPoints, double angle, Point rotation)
    {
        List<PlanPoint> rotated = new LinkedList<>();
        final double cosTheta = Math.cos(angle * Math.PI/180.0);
	final double sinTheta = Math.sin(angle * Math.PI/180.0);

        for (Point po: recPoints)
        {
            PlanPoint rotate = new PlanPoint();            
            double rotate_x = (po.getX()-rotation.getX())*cosTheta + ((po.getY()-rotation.getY())*(-sinTheta)) + rotation.getX();
            double rotate_y = (po.getX()-rotation.getX())*sinTheta + ((po.getY()-rotation.getY())*cosTheta) + rotation.getY() ;
            rotate.setLocation(rotate_x, rotate_y);
            rotated.add(rotate); 
        }
        
        return rotated;
    }
    
    public void FindRectangulationNodes(ZToGraphRoomRaster Zroom, List<PlanPoint> recPoints)
    {   
         if( debug ) {System.out.println("Find Rectangulation Nodes started: ");}
         //stores the covered area for a certain node, represented as a list of planpoints 
         List<PlanPoint> covered; 
         int unit,rest,xPos,yPos;
         int DirectionRight, DirectionDown; //0 der 1 je nachdem ob Raum höher oder breiter ist...
         
            //new version of creating rectangulation Nodes for room with arbitrary direction
            Point nw = recPoints.get(0); Point ne = recPoints.get(1) ; Point sw = recPoints.get(2); Point se = recPoints.get(3); 
            //compute rotation angle
            Point origin, lower;
            if (nw.getX() < sw.getX()){
                origin = nw;
                lower = sw;
            }
            else{
                origin = sw;
                lower = nw;
            }
            Vector2 first = new Vector2(0.0,1.0);
            Vector2 second = new Vector2();
            second.setX(origin.getX() - lower.getX());
            second.setY(origin.getY() - lower.getY());
            double angle = first.getAngleBetween(first,second);
            //System.out.println("computed angle: " + angle);
            
            List<PlanPoint> rotated = rotateRectangle(recPoints,angle,nw);
            /*for (Point po: rotated){
                System.out.println("Rotierter Punkt: " + po);
            }*/
            
            nw = rotated.get(0); ne = rotated.get(1); sw = rotated.get(2); se = rotated.get(3); 
            int width = (int)Math.sqrt(Math.pow(nw.getX()-ne.getX(),2) + Math.pow(nw.getY()-ne.getY(),2));
            int length = (int)Math.sqrt(Math.pow(nw.getX()-sw.getX(),2) + Math.pow(nw.getY()-sw.getY(),2));
            
            if (width < length){
             unit = width;
             rest = length;
             DirectionRight = 0;
             DirectionDown = 1; }
            
            else{
             unit = length;
             rest = width;
             DirectionRight = 1; 
             DirectionDown = 0;  }
             
            int numIteration=0;
            xPos = (int) se.getX();
            yPos = (int) se.getY();
            
            while (rest > 2*unit)
            {
                int UpperLeft1_x = (int)(nw.getX() + (numIteration*DirectionRight*unit));
                int UpperLeft1_y = (int)(nw.getY() + (numIteration*DirectionDown*unit));
                int UpperRight1_x = (int)(DirectionDown*xPos + DirectionRight*((numIteration+1)*unit+nw.getX()));
                int UpperRight1_y = (int)(nw.getY() + (numIteration*DirectionDown*unit));
                int LowerRight1_x = (int)(DirectionDown*xPos + DirectionRight*((numIteration+1)*unit+nw.getX()));
                int LowerRight1_y = (int)(DirectionRight*yPos + DirectionDown*((numIteration+1)*unit + nw.getY()));
                int LowerLeft1_x = (int)(nw.getX() + (numIteration*DirectionRight*unit));
                int LowerLeft1_y = (int)(DirectionRight*yPos + DirectionDown*((numIteration+1)*unit + nw.getY()));
                 
                 PlanPoint UpperLeft = new PlanPoint(UpperLeft1_x,UpperLeft1_y);
                 PlanPoint LowerRight = new PlanPoint(LowerRight1_x,LowerRight1_y);
                 PlanPoint UpperRight = new PlanPoint(UpperRight1_x,UpperRight1_y);
                 PlanPoint LowerLeft = new PlanPoint(LowerLeft1_x,LowerLeft1_y);
                 covered = new LinkedList<>();
                 //TODO: transformiere Punkte zurueck in urspruengliche Lage                 
                 covered.add(UpperLeft); covered.add(UpperRight); covered.add(LowerRight); covered.add(LowerLeft);
                 List<PlanPoint> backrotate = rotateRectangle(covered,-angle,nw);
                 covered = new LinkedList<>();
                 for (PlanPoint p : backrotate){
                     covered.add(p);
                 }
                 //System.out.println("covered: " + covered.toString());
                 //node at the left/upper end of rectangle
                 Node node1 = new Node(nodeCount++);
                 coveredArea.put(node1, covered);
                 if( debug ) {System.out.println("rectanglar room node: " + node1);}
                 graph.setNode(node1);
                 nodesCap.set(node1, Integer.MAX_VALUE);                            
                 //NodeRectangle rec1 = new NodeRectangle(UpperLeft1_x,-(UpperLeft1_y),LowerRight1_x, -(LowerRight1_y));
                 NodeRectangle rec1 = new NodeRectangle(backrotate.get(0).getXInt(),-backrotate.get(0).getYInt(),backrotate.get(1).getXInt(),-backrotate.get(1).getYInt(),backrotate.get(2).getXInt(),-backrotate.get(2).getYInt(),backrotate.get(3).getXInt(),-backrotate.get(3).getYInt());
                 //System.out. println("NodeRectangle1: " + UpperLeft + " " + UpperRight + " " + LowerRight + " " + LowerLeft);
                 mapping.setNodeRectangle(node1, rec1);                            
                 model.getZToGraphMapping().getNodeFloorMapping().set( node1,getProblem().getFloorID(Zroom.getFloor()));
                 model.getZToGraphMapping().setIsEvacuationNode(node1, false );
                 model.getZToGraphMapping().setIsSourceNode(node1, false);
                 model.getZToGraphMapping().setIsDeletedSourceNode(node1, false );
                 nodes.add(node1);
                                
                 int UpperLeft2_x = (int) (DirectionDown*nw.getX() + DirectionRight*(xPos-((numIteration+1)*unit)));
                 int UpperLeft2_y = (int) (DirectionRight*nw.getY() + DirectionDown*(yPos-((numIteration+1)*unit)));
                 int UpperRight2_x = (int) (DirectionRight*(xPos-numIteration*unit) + DirectionDown*xPos);
                 int UpperRight2_y = (int) (DirectionRight*nw.getY() + DirectionDown*(yPos-((numIteration+1)*unit)));
                 int LowerRight2_x = DirectionRight*(xPos-numIteration*unit) + DirectionDown*xPos;
                 int LowerRight2_y = DirectionRight*yPos + DirectionDown*(yPos-numIteration*unit);
                 int LowerLeft2_x = (int) (DirectionDown*nw.getX() + DirectionRight*(xPos-((numIteration+1)*unit)));
                 int LowerLeft2_y = (int) (DirectionRight*yPos + DirectionDown*(yPos-numIteration*unit));
                 
                 PlanPoint upperLeft2 = new PlanPoint(UpperLeft2_x,UpperLeft2_y);
                 PlanPoint lowerRight2 = new PlanPoint(LowerRight2_x,LowerRight2_y);
                 PlanPoint upperRight2 = new PlanPoint(UpperRight2_x,UpperRight2_y);
                 PlanPoint LowerLeft2 = new PlanPoint(LowerLeft2_x,LowerLeft2_y);
                 covered = new LinkedList<>();
                 covered.add(upperLeft2); covered.add(upperRight2); covered.add(lowerRight2); covered.add(LowerLeft2);
                 List<PlanPoint> backrotate2 = rotateRectangle(covered,-angle,nw);
                 covered = new LinkedList<>();
                 for (PlanPoint p : backrotate2){
                     covered.add(p);
                 }
                 //System.out.println("covered 2: " + covered.toString());
                 //System.out. println("NodeRectangle2: " + upperLeft2 + " " + upperRight2 + " " + lowerRight2 + " " + LowerLeft2);
                 Node node2 = new Node(nodeCount++);
                 if( debug ) {System.out.println("rectanglar room node: " + node2);}
                 coveredArea.put(node2, covered);
                 graph.setNode(node2);
                 nodesCap.set(node2,Integer.MAX_VALUE );
                 NodeRectangle rec2 = new NodeRectangle(backrotate2.get(0).getXInt(),-backrotate2.get(0).getYInt(),backrotate2.get(1).getXInt(),-backrotate2.get(1).getYInt(),backrotate2.get(2).getXInt(),-backrotate2.get(2).getYInt(),backrotate2.get(3).getXInt(),-backrotate2.get(3).getYInt());
                 //NodeRectangle rec2 = new NodeRectangle(UpperLeft2_x,-(UpperLeft2_y),LowerRight2_x,-(LowerRight2_y));
                 mapping.setNodeRectangle(node2, rec2);                            
                 model.getZToGraphMapping().getNodeFloorMapping().set( node2,getProblem().getFloorID(Zroom.getFloor()));
                 model.getZToGraphMapping().setIsEvacuationNode(node2, false );
                 model.getZToGraphMapping().setIsSourceNode(node2, false);
                 model.getZToGraphMapping().setIsDeletedSourceNode(node2, false );
                 nodes.add(node2);
                                
                 rest = rest-2*unit;
                 numIteration++;
            }
            
            Node middle = new Node(nodeCount++);
            nodes.add(middle);
            if( debug ) {System.out.println("node in the middle: " + middle);}
            graph.setNode(middle);
            nodesCap.set(middle, Integer.MAX_VALUE);
            int midUpperLeft_x = (int) ((DirectionDown*nw.getX()) + DirectionRight*(nw.getX()+(numIteration*unit)));
            int midUpperLeft_y = (int) (DirectionRight*nw.getY() + DirectionDown*(nw.getY()+(numIteration*unit)));
            int midUpperRight_x = DirectionRight*(xPos-(numIteration*unit)) + DirectionDown*xPos;
            int midUpperRight_y = midUpperLeft_y;
            int midLowerRight_x = DirectionRight*(xPos-(numIteration*unit)) + DirectionDown*xPos;
            int midLowerRight_y = DirectionDown*(yPos-numIteration*unit) + DirectionRight*yPos; 
            int midLowerLeft_x = midUpperLeft_x;
            int midLowerLeft_y = midLowerRight_y;
            
            PlanPoint upperLeft3 = new PlanPoint(midUpperLeft_x,midUpperLeft_y);
            PlanPoint lowerRight3 = new PlanPoint(midLowerRight_x,midLowerRight_y);
            PlanPoint upperRight3 = new PlanPoint(midUpperRight_x,midUpperRight_y);
            PlanPoint LowerLeft3 = new PlanPoint(midLowerLeft_x,midLowerLeft_y);
            covered = new LinkedList<>();
            covered.add(upperLeft3); covered.add(upperRight3); covered.add(lowerRight3); covered.add(LowerLeft3);
            List<PlanPoint> backrotate3 = rotateRectangle(covered,-angle,nw);
            covered = new LinkedList<>();
            for (PlanPoint p : backrotate3){
                 covered.add(p);
            }
            //System.out.println("covered 3: " + covered.toString());
            coveredArea.put(middle, covered);
            //NodeRectangle recmidd = new NodeRectangle(midUpperLeft_x, -(midUpperLeft_y),midLowerRight_x,-midLowerRight_y);
            NodeRectangle recmidd = new NodeRectangle(backrotate3.get(0).getXInt(),-backrotate3.get(0).getYInt(),backrotate3.get(1).getXInt(),-backrotate3.get(1).getYInt(),backrotate3.get(2).getXInt(),-backrotate3.get(2).getYInt(),backrotate3.get(3).getXInt(),-backrotate3.get(3).getYInt());
            mapping.setNodeRectangle(middle, recmidd);
            model.getZToGraphMapping().getNodeFloorMapping().set( middle,getProblem().getFloorID(Zroom.getFloor()));
            model.getZToGraphMapping().setIsEvacuationNode( middle, false );
            model.getZToGraphMapping().setIsSourceNode(middle, false);
            model.getZToGraphMapping().setIsDeletedSourceNode( middle, false );
            
             /*
                 if (room.getInaccessibleAreas().size() > 0)
                 {
                    PlanPoint p = new PlanPoint(); 
                    p.setLocation(rec1.getCenterX(), -rec1.getCenterY());
                    for (InaccessibleArea area: room.getInaccessibleAreas())
                    {
                        if (area.contains(p))
                        {
                                        System.out.println("center lies on inaccessible area... ");
                                        int areaCenterX = area.getxOffset() + (area.getWidth()/2);
                                        rec1 = new NodeRectangle(areaCenterX-1,-(area.getyOffset()-1),areaCenterX+1,-(area.getyOffset()-1));
                                        
                        }                                 
                    }
                 }         
                
            */
            
            
    }
    
    public void FindRectangulationEdges(Room room)
    {
            int size = NodesForRoom.get(room).size();
            for (int i=0;i< size-2;i++)
            {
                Node node1 = NodesForRoom.get(room).get(i);
                Node node2 = NodesForRoom.get(room).get(i+2);
                Edge edge = new Edge(EdgeCount++,node1,node2);
                if( debug ) {System.out.println("Dividing Edge: " + edge);}
                mapping.setEdgeLevel(edge, Level.Equal);
                graph.addEdge(edge);
                //TODO: set edge capacity correctly
                edgesCap.set(edge, Integer.MAX_VALUE);
                RoomEdges.add(edge);
            }

            Node node_a = NodesForRoom.get(room).get(size-1);
            Node node_b = NodesForRoom.get(room).get(size-2);
            Edge last = new Edge(EdgeCount++,node_a,node_b);
            if( debug ) {System.out.println("Last Edge: " + last);}
            mapping.setEdgeLevel(last, Level.Equal);
            graph.addEdge(last);
            //TODO: set edge capacity correctly
            edgesCap.set(last, Integer.MAX_VALUE);
            RoomEdges.add(last);
            //connect the inner nodes with the neighbouring doors
            Collection<PositionNode> doors = DoorNodesForRoom.get(room);
            Collection<Node> innernodes = NodesForRoom.get(room);
            //System.out.println("Covered Areas: " + coveredArea.toString());
            for (Node node2: innernodes)
            { 
                 List<PlanPoint> getPoints = coveredArea.get(node2);                   
                 PlanPolygon poly = new PlanPolygon(ds.z.Edge.class);
                 poly.defineByPoints(getPoints);
                 //System.out.println("PlanPolygon: " + poly.toString());
                 for (PositionNode node1: doors)
                 {
                      PlanPoint point = new PlanPoint(node1.getPosition().x,node1.getPosition().y);
                      if (poly.contains(point))
                      {
                            Edge edge = new Edge(EdgeCount++,node1.getNode(),node2);
                            if( debug ) {System.out.println("Edge to door: " + edge);}
                            mapping.setEdgeLevel(edge, Level.Equal);
                            graph.addEdge(edge);
                            //TODO: set edge capacity correctly
                            edgesCap.set(edge, Integer.MAX_VALUE);
                            RoomEdges.add(edge);  
                      }
                 }
                 if (room.getAssignmentAreas().size() > 0)
                 {
                      for (Node node: AssignmentNodes.get(room))
                      { 
                           int x = (int)mapping.getNodeRectangles().get(node).getCenterX();
                           int y = -(int)mapping.getNodeRectangles().get(node).getCenterY();
                           PlanPoint point = new PlanPoint(x,y);
                           if (poly.contains(point))
                           {
                                 Edge edge = new Edge(EdgeCount++,node,node2);
                                 if( debug ) {System.out.println("Edge between center and assignment area: " + edge);}
                                 mapping.setEdgeLevel(edge, Level.Equal);
                                 graph.addEdge(edge);
                                 //TODO: set edge capacity correctly
                                 edgesCap.set(edge, Integer.MAX_VALUE);
                                 RoomEdges.add(edge); 
                            }
                      }
                  }
                  if (room.getEvacuationAreas().size() > 0)
                  {
                       for (Node node: EvacuationNodes.get(room))
                       {
                            int x = (int)mapping.getNodeRectangles().get(node).getCenterX(); 
                            int y = -(int)mapping.getNodeRectangles().get(node).getCenterY();
                            PlanPoint point = new PlanPoint(x,y);
                            if (poly.contains(point))
                            {
                                Edge edge = new Edge(EdgeCount++,node,node2);
                                if( debug ) {System.out.println("Edge between center and evacuation area: " + edge);}
                                mapping.setEdgeLevel(edge, Level.Equal);
                                graph.addEdge(edge);
                                //TODO: set edge capacity correctly (smaller than infinity, so that waiting is considered)
                                edgesCap.set(edge, Integer.MAX_VALUE);
                                RoomEdges.add(edge); 
                           }
                        }
                   }
            }
    }
    
    public void ConnectRooms(Room room)
    {
            Collection<Room> neighbRooms = NeighbourRooms.get(room);
            //door nodes of the current room
            Collection<PositionNode> nodes1 = DoorNodesForRoom.get(room);
            
            for (Room r : neighbRooms)
            {
                Collection<PositionNode> nodes2 = DoorNodesForRoom.get(r);                
                //edge between door of current room and all neighbour room doors
                for (PositionNode node1: nodes1)
                {
                   //System.out.println("Is floor node1: " + isfloorNode.get(node1.getNode()));
                   if(!isfloorNode.get(node1.getNode()))            
                   {
                   for (PositionNode node2: nodes2)
                   {
                       //System.out.println("Is floor node2: " + isfloorNode.get(node1.getNode()));
                       if (!isfloorNode.get(node2.getNode()))         
                       {
                       if (node1.getPosition().equals(node2.getPosition()) && used[node1.getNode().id()][node2.getNode().id()]==0)
                       {
                               Edge edge = new Edge(EdgeCount++,node1.getNode(),node2.getNode());
                               mapping.setEdgeLevel(edge, Level.Equal);
                               graph.setEdge(edge);
                               if( debug ) {System.out.println("door connecting edge: " + edge);}
                               int width = (int) Math.floor(room.getDoors().get(node1.getPosition())/1000*2);
                               if (width ==0)
                               {
                                   width =1;
                               }
                               //System.out.println("Weite: " + Math.floor(room.getDoors().get(node1.getPosition())));
                               //edgesCap.set(edge,width);
                               edgesCap.set(edge,10);
                               used[node1.getNode().id()][node2.getNode().id()] = 1;
                               used[node2.getNode().id()][node1.getNode().id()] = 1;
                       }
                       
                       }
                    }  
            
                   }
                }           
            } 
    }
    
    
    public void ConnectFloors(Room room)
    {
        Collection<PositionNode> nodes1 = floorNodesForRoom.get(room);
        
        for (PositionNode n1: nodes1)
        {
            Room connect = linktarget.get(n1.getNode());
            Collection<PositionNode> nodes2 = floorNodesForRoom.get(connect);
            for (PositionNode n2: nodes2)
            {
                if (linktarget.get(n2.getNode()).equals(room))
                {
                    Edge edge = new Edge(EdgeCount++,n1.getNode(),n2.getNode());
                    if( debug ) {System.out.println("floor connecting edge: " + edge);}
                    mapping.setEdgeLevel(edge, Level.Equal);
                    graph.setEdge(edge);  
                    if( debug ) {System.out.println("n1 position: " + n1.getPosition());}
                    if( debug ) {System.out.println("floor doors: " + floordoors.toString());}
                    int width = getFloorDoors(room).get(n2.getPosition());                 
                    edgesCap.set(edge,width);                    
                    if( debug ) {System.out.println("for room: " + room.getName() + "on floor: " + room.getAssociatedFloor().getName() + " and: " + connect.getName() + " on floor: " + connect.getAssociatedFloor().getName());}
                }
            }
        }
    }
    
    public void ConnectWithCertainNode(Room room, Collection<Node> nodes, Node DoorNode )
    {
        for (Node node: nodes)
        {
            Edge edge = new Edge(EdgeCount++,node, DoorNode);
            mapping.setEdgeLevel(edge, Level.Equal);
            graph.addEdge(edge);
            if( debug ) {System.out.println("Edge " + edge + "in Room: " + room.getName()); }       
            edgesCap.set(edge,Integer.MAX_VALUE);
            RoomEdges.add(edge);
        }
    }
    
   
    
    private static double calculateDistance( Point start, Point end ) {
		double distance;
		double distanceX, distanceY;
		double startX = start.getX();
		double startY = start.getY();
		double endX = end.getX();
		double endY = end.getY();

		distanceX = Math.abs( startX - endX );
		distanceY = Math.abs( startY - endY );

		distance =  Math.sqrt( Math.pow( distanceX, 2 ) + Math.pow( distanceY, 2 ) ) ;
		return distance;
	}
    
    public List<PositionNode> FindNearestAssignNodes(List<PositionNode> nodes ,PositionNode connect)
    {
        //System.out.println("connect: " + connect.getPosition());
        List<PositionNode> nearest = new LinkedList<>();
        PositionNode near= null;
        double dist = Double.MAX_VALUE;
        
        for (PositionNode n: nodes )
        {
            //System.out.println("Node Position: " + n.getPosition());
            double distance = calculateDistance(n.getPosition(),connect.getPosition());
            if (distance < dist)
            {
                dist = distance;
                near = n;
            }
        }
        nearest.add(near);
        //System.out.println("dist: " + dist + "for node: " + near.getNode() );
        for (PositionNode n: nodes )
        {
            if (!n.getNode().equals(near.getNode()))
            {
                double distance = calculateDistance(n.getPosition(),connect.getPosition());
                //System.out.println("distance: " + distance + "for node: " + n.getNode());
                if (distance == dist)
                {
                    nearest.add(n);
                }
            }
        }
        return nearest;
    }
}