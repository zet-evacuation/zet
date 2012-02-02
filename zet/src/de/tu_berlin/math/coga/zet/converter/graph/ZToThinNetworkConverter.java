/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.common.util.Level;
import ds.PropertyContainer;
import ds.graph.network.DynamicNetwork;
import ds.graph.Edge;
import ds.mapping.IdentifiableDoubleMapping;
import ds.mapping.IdentifiableIntegerMapping;
import ds.collection.ListSequence;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import ds.graph.PositionNode;
import ds.z.AssignmentArea;
import ds.z.DelayArea;
import ds.z.EvacuationArea;
import ds.z.PlanPoint;
import ds.z.PlanPolygon;
import ds.z.Room;
import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author marlenschwengfelder
 */
public class ZToThinNetworkConverter extends BaseZToGraphConverter{
    
    //maps the node representing the door to corresponding room
    public HashMap<Room,Node> DoorNodeForRoom = new HashMap<>();
    //maps the center node for a room to a specific room
    public HashMap<Room,Node> CenterNodeForRoom = new HashMap<>();
    //maps all the other nodes that represent the room to that
    public HashMap<Room,ListSequence<Node>> NodesForRoom = new HashMap<>();
    //maps all the door nodes for a specific room to it
    public HashMap<Room,Collection<PositionNode>> DoorNodesForRoom = new HashMap<>();
    public Collection<PositionNode> doorNodes;
    //stores all nodes for the room (except the door node)
    public ListSequence<Node> nodes;
    //maps the names of the neighoured rooms to each room
    public HashMap<Room,Collection<Room>> NeighbourRooms = new HashMap<>();
    //stores all neighbours for a room
    public Collection<Room> neighbours;
    //stores the evacuationNode for a room
    public HashMap<Room,ListSequence<Node>> EvacuationNodes = new HashMap<>();
    public ListSequence<Node> EvacNodes;
    //stores the assignment nodes for a room
    public HashMap<Room,ListSequence<Node>> AssignmentNodes = new HashMap<>();
    public ListSequence<Node> AssignNodes;
    public int[][] used;
    //stores number of evacuation areas
    int numEvac1,numEvac2, num, numAssign1, numAssign2;
    Room MoreDoorRoom, EvacRoom;
    
    //position Nodes to remember which rooms are connected by the given node
    public HashMap<Point, Node> connection = new HashMap<>(); 
    //stores for each edge the corresponding room
    public HashMap<Room,ListSequence<Edge>> EdgesForRoom = new HashMap<>();
    public ListSequence<Edge> RoomEdges = new ListSequence<>();
    //stores all doors that do not get a node (only if one of the corresponding rooms has only one door and is empty)
    public List<Point> needsNoConnection = new LinkedList<>();
    
    DynamicNetwork graph = new DynamicNetwork();
    IdentifiableIntegerMapping<Node> nodesCap = new IdentifiableIntegerMapping<>(1);
    int nodeCount = 0;
   
    IdentifiableIntegerMapping<Edge> edgesCap = new IdentifiableIntegerMapping<>(1);
    int EdgeCount;
    
    @Override
    protected void createNodes()
    {   
        System.out.println( "Create Nodes for thin Network... " );
        List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();
        
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
               System.out.println("Akt. Raum: " + room.getRoom().getName());
               Room ZRoom = room.getRoom();
               doorNodes = new HashSet<>();
               EvacNodes = new ListSequence<>();
               AssignNodes = new ListSequence<>();
               nodes = new ListSequence<>();
               //Collection<Room> neighbRooms = NeighbourRooms.get(ZRoom);
               ComputeNeighbourRoomValues(ZRoom);


               //create Node for each Assignment Area in these rooms
               for (AssignmentArea area: ZRoom.getAssignmentAreas())
               {
                       Node node = new Node(nodeCount++);
                       System.out.println("Belegungsknoten: " + node + "in Raum: " + ZRoom.getName());
                       graph.setNode(node);
                       int value = area.getMaxEvacuees();
                       nodesCap.add(node, value); 
                       NodeRectangle rec = new NodeRectangle(area.getxOffset(),-area.getyOffset(), area.getxOffset()+ area.getWidth(), -(area.getyOffset() + area.getHeight()));
                       mapping.setNodeRectangle(node, rec );
                       model.getZToGraphMapping().getNodeFloorMapping().set( node,plan.getFloorID(room.getFloor()));
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
                       //System.out.println("Werte: " + startCol + " " + startRow + " "+ numCol + " " + numRow);
                       for (int i=startCol; i<startCol+numCol+1; i++)
                       {
                           for (int j=startRow; j<startRow+numRow+1;j++)
                           {
                               ZToGraphRasterSquare square = room.getSquare( i, j );
                               //System.out.println("Square: " + square.toString());
                               square.mark();
                               square.setNode(node);
                           }
                       }
              }
                    //create node for each evacuation area of building
                    for (EvacuationArea Earea : ZRoom.getEvacuationAreas())
                    {
                        System.out.println("Evakuierungsknoten in Raum: " + ZRoom.getName());
                        Node node = new Node(nodeCount);
                        System.out.println("Evakuierungsknoten: " + node);
                        graph.setNode(node);
                        int Evalue = Earea.getMaxEvacuees();
                        nodesCap.add(node, Evalue); 
                        NodeRectangle rec = new NodeRectangle(Earea.getxOffset(),-Earea.getyOffset(), Earea.getxOffset()+ Earea.getWidth(), -(Earea.getyOffset() + Earea.getHeight()));
                        mapping.setNodeRectangle(node, rec );
                        model.getZToGraphMapping().getNodeFloorMapping().set( node,plan.getFloorID(room.getFloor()));
                        model.getZToGraphMapping().setIsEvacuationNode( node, true );
                        model.getZToGraphMapping().setIsSourceNode(node, false);
                        model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                        EvacNodes.add(node);
                        nodeCount++;
                    }
                    
                    if (ZRoom.getDoorEdges().isEmpty())
                    {
                        int mid_x = ZRoom.getxOffset()+(ZRoom.getWidth()/2);
                        int mid_y = ZRoom.getyOffset()+(ZRoom.getHeight()/2);
                        Node node = new Node(nodeCount++);
                        graph.setNode(node);
                        System.out.println("Center Node: " + node + "for room: " + ZRoom.getName());
                        NodeRectangle rec = new NodeRectangle(mid_x-1,-(mid_y-1), mid_x+1, -(mid_y+1));
                        mapping.setNodeRectangle(node, rec );
                        int numPersons = room.getRoom().getMaxEvacuees();
                        nodesCap.add(node, numPersons);
                        model.getZToGraphMapping().getNodeFloorMapping().set( node,plan.getFloorID(room.getFloor()));
                        model.getZToGraphMapping().setIsEvacuationNode( node, false );
                        model.getZToGraphMapping().setIsSourceNode(node, false);
                        model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                        CenterNodeForRoom.put(ZRoom, node);
                    }
                    
                    //create door nodes for rooms with exactly one door first
                    //if there is at least one evacuation or assignment area in room with only one door, create an node representing the door
                    if (!(ZRoom.getEvacuationAreas().isEmpty() && ZRoom.getAssignmentAreas().isEmpty()) && (ZRoom.getDoorEdges().size()==1))
                    { 
                        //only contains one door
                        HashMap<Point,Integer> doors = ZRoom.getDoors();
                        for (Point p: doors.keySet())
                        {      
                                System.out.println("Türknoten im Raum: " + ZRoom.getName());
                                //create node for door                        
                                Node node = new Node(nodeCount);
                                System.out.println("Türknoten: " + node);
                                PositionNode pos = new PositionNode(node,p,doors.get(p));
                                doorNodes.add(pos);
                                nodeCount++;
                                graph.setNode(node);
                                DoorNodeForRoom.put(ZRoom, node);
                                //length of door is returned in mm
                                //Observation: 0.5 meter/person
                                int width = (int) Math.floor(((double) ZRoom.getLengthOfDoor(ZRoom))/1000.0)*2;
                                //System.out.println("Knotenkap: " + width);
                                nodesCap.add(node, width);
                                NodeRectangle rec = new NodeRectangle(p.x-1,-(p.y+1), p.x+1,-(p.y-1));
                                mapping.setNodeRectangle(node, rec );
                                model.getZToGraphMapping().getNodeFloorMapping().set( node,plan.getFloorID(room.getFloor()));
                                model.getZToGraphMapping().setIsEvacuationNode( node, false );
                                model.getZToGraphMapping().setIsSourceNode(node, false);
                                model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                        }
                    }
                    //create nodes for rooms with more than one door
                    if (ZRoom.getDoorEdges().size()>1)
                    {
                        //creating door nodes
                        System.out.println("Türknoten für Raum:  " + ZRoom.getName());
                        HashMap<Point,Integer> doors = ZRoom.getDoors();
                        for (Point p: doors.keySet())
                        {
                            //only create door node if rooms with one door is not empty
                            if (!needsNoConnection.contains(p))
                            {
                                Node node = new Node(nodeCount);
                                connection.put(p,node);
                                //TODO: get exact node capacity
                                nodesCap.add(node, 10);
                                System.out.println("Türknoten: " + node);
                                PositionNode pos = new PositionNode(node,p,doors.get(p)); 
                                doorNodes.add(pos);
                                graph.setNode(node);
                                //NodeRectangle rec = new NodeRectangle(p.x-1,p.y+1, p.x+1,p.y-1);
                                NodeRectangle rec = new NodeRectangle(p.x-1,-(p.y+1), p.x+1,-(p.y-1));
                                mapping.setNodeRectangle(node, rec );
                                model.getZToGraphMapping().getNodeFloorMapping().set( node,plan.getFloorID(room.getFloor()));
                                model.getZToGraphMapping().setIsEvacuationNode( node, false );
                                model.getZToGraphMapping().setIsSourceNode(node, false);
                                model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                                nodeCount++;
                            }
                        }                      
                        
                    //only neighbours with one door, Case 2b)
                    //one evacuation node inside room and one in neighbourrooms 
                    if (numEvac2 == 1 && num==0 && ZRoom.getEvacuationAreas().size() == 1)
                    {   
                        //room nearly quadratic
                        if (ZRoom.getHeight() < 2*ZRoom.getWidth() && ZRoom.getWidth() < 2*ZRoom.getHeight())
                        {    
                            System.out.println("Create center1 for room: " + ZRoom.getName());
                            //create node between two evacuation areas
                            for (Point p: EvacRoom.getDoors().keySet())
                            {
                                Point q = new Point(p.x,-p.y);
                                createCenterNode(ZRoom,q,EvacNodes.first());
                            }
                        }
                        else
                        {
                            System.out.println("Case 2b)");
                            FindRectangulationNodes(room);                           
                        }
                    }
                //exactly one room with more than one door (x), no evacuation areas in rooms with one door    
                else if (num==1 && numEvac2==0 )
                {                    
                    if (ZRoom.getEvacuationAreas().size()==1)
                    {
                        //node between 2 evacuation areas
                        System.out.println("Create Center for room: " + ZRoom.getName());
                        Collection<ds.z.Edge> doors1 = MoreDoorRoom.getDoorEdges();
                        //System.out.println("doors: " + doors);
                        Collection<ds.z.Edge> doors2 = ZRoom.getDoorEdges();
                        //System.out.println("doors1: " + doors1);
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
                                //System.out.println("Point: " + point);
                                p.setLocation(point);
                                createCenterNode(ZRoom,p,EvacNodes.first());
                            }
                            } 
                        }
                    }
                }
                //more than one room with more doors and more than one evacuation area in neighbouring rooms
                else 
                {
                    System.out.println("Trivial Node Creation.." + ZRoom.getName());
                    //room is nearly rectangular
                    if ((ZRoom.getHeight() > 2* ZRoom.getWidth() || ZRoom.getWidth() > 2*ZRoom.getHeight()))
                    {
                        FindRectangulationNodes(room);
                    }         
                    else
                    {
                        System.out.println("Create node in the middle for: " + ZRoom.getName());
                        Node node = new Node(nodeCount++);
                        System.out.println("node: " + node);
                        graph.setNode(node);
                        CenterNodeForRoom.put(ZRoom, node);
                        //TODO: define exact value of node capacity
                        nodesCap.set(node, 10);
                        //NodeRectangle rec = new NodeRectangle(ZRoom.getxOffset(),ZRoom.getyOffset(),ZRoom.getxOffset()+ZRoom.getWidth(),ZRoom.getyOffset()+ZRoom.getHeight());
                        NodeRectangle rec = new NodeRectangle(ZRoom.getxOffset(),-ZRoom.getyOffset(),ZRoom.getxOffset()+ZRoom.getWidth(),-(ZRoom.getyOffset()+ZRoom.getHeight()));
                        mapping.setNodeRectangle(node, rec );
                        model.getZToGraphMapping().getNodeFloorMapping().set( node,plan.getFloorID(room.getFloor()));
                        model.getZToGraphMapping().setIsEvacuationNode( node, false );
                        model.getZToGraphMapping().setIsSourceNode(node, false);
                        model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                    }
                }
                    }
             
                  
        //stores all the constructed nodes for one room (except door node, evacuation and assignment nodes)
        NodesForRoom.put(ZRoom, nodes); 
        EvacuationNodes.put(ZRoom, EvacNodes);
        AssignmentNodes.put(ZRoom, AssignNodes);
        DoorNodesForRoom.put(ZRoom, doorNodes);
        //System.out.println("DoorNodes: " + doorNodes);
        } //end of: for all rastered rooms...
        
        model.setNodeCapacities( nodesCap );
        model.setNetwork(graph);
        model.setSources(sources);
        for (Node node: model.getGraph().nodes())
        {
            System.out.print("erstellte Knoten: " + node + " ; ");
        } 
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
            System.out.println("Aktueller Raum: " + room.getRoom().getName());
            Room ZRoom = room.getRoom();
            ComputeNeighbourRoomValues(ZRoom);
            
            //create Edge between evacuation nodes and super sink
            Node supersink = model.getSupersink();
            for (Node evacNode: EvacuationNodes.get(room.getRoom()))
            {
                Node node1 = evacNode;
                Edge edge = new Edge(EdgeCount++,node1,supersink);
                mapping.setEdgeLevel(edge, Level.Equal);
                graph.setEdge(edge);
                System.out.println("Edge to supersink: " + edge);                   
                edgesCap.set(edge,Integer.MAX_VALUE);
            }
            Collection<Room> neighbRooms = NeighbourRooms.get(ZRoom);
            //connect the room with all of its neighbours
            ConnectRooms(ZRoom);
            
            //room has no doors 
            if (ZRoom.getDoorEdges().isEmpty())
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
                        edgesCap.set(edge,10);
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
                        edgesCap.set(edge,10);
                    }
                }
            }
            //room has only one door
            if (ZRoom.getDoorEdges().size() == 1)
            {  
                System.out.println("Case 0 for Room: " + ZRoom.getName());
                Collection<Node> nodes = new HashSet<>();  
                    if (!AssignmentNodes.get(ZRoom).isEmpty())
                    {
                        for (Node node: AssignmentNodes.get(ZRoom))
                        {
                            nodes.add(node);
                        }
                    }
                    if (!EvacuationNodes.get(ZRoom).isEmpty())
                    {
                        for (Node node: EvacuationNodes.get(ZRoom) )
                        {
                            nodes.add(node);
                        }
                    }
                    
                    ConnectWithCertainNode(ZRoom, nodes, DoorNodeForRoom.get(ZRoom) );
                    
                    if ((!ZRoom.getEvacuationAreas().isEmpty()) && (!ZRoom.getAssignmentAreas().isEmpty()) )
                    {  
                        for (Node node1: EvacuationNodes.get(ZRoom))
                        {
                                for (Node node2: AssignmentNodes.get(ZRoom))
                                {
                                    //connect evacutaion area and assignment area in one room directly
                                    Edge edge = new Edge(EdgeCount++,node1,node2);
                                    mapping.setEdgeLevel(edge, Level.Equal);
                                    graph.addEdge(edge);
                                    edgesCap.set(edge,Integer.MAX_VALUE);
                                    RoomEdges.add(edge);
                                }
                        }
                    }              
            }
            else if (ZRoom.getDoorEdges().size() > 1)
            {
                //all neighbours have only one door and no evacuation areas
                if (numEvac2==0 && num==0)
                {
                  System.out.println("Case 1 for Room: " + ZRoom);  
                  if (ZRoom.getEvacuationAreas().isEmpty())
                  {
                      System.out.println("there is no reachable evacuation area for this room");
                  }
                  //connect only evacuation node with doors
                  else if (ZRoom.getEvacuationAreas().size() ==1)
                  { 
                        Collection<Node> DoorNodes = new HashSet<>();
                        Node evac= EvacuationNodes.get(ZRoom).first();
                        
                        for (Room room2 : neighbRooms)
                        { 
                            if (DoorNodeForRoom.get(room2)!=null)
                            {
                                DoorNodes.add(DoorNodeForRoom.get(room2));
                            }
                        }  
                        ConnectWithCertainNode(ZRoom,DoorNodes,evac);
                  }
                  
                }
                //there is one evacuationNode in a neighbour room and not only one on room itself
                //connect all doors with the door of evacuation room
                else if (numEvac2 == 1 && num==0 && ZRoom.getEvacuationAreas().isEmpty())
                {
                    System.out.println("Case 2 for Room: " + ZRoom);
              
                    Node door = DoorNodeForRoom.get(EvacRoom);
                    for (Room r: neighbRooms)
                    {
                        if (r != EvacRoom)
                        { 
                            Node door2 = DoorNodeForRoom.get(r);
                            if (door2 != null)
                            {    
                                Edge edge = new Edge(EdgeCount++,door, door2);
                                mapping.setEdgeLevel(edge, Level.Equal);
                                graph.addEdge(edge);
                                //TODO: set edge capacity correctly
                                edgesCap.set(edge, 10);
                                RoomEdges.add(edge);
                            }
                        }
                    }
                }
                //one neighbouring evacuation area and one evacuation area inside (Case 2b)
                else if (numEvac2 == 1 && num==0 && ZRoom.getEvacuationAreas().size() == 1)
                {
                    System.out.println("Case 3 for Room: " + ZRoom.getName());
                    //room is more quadratic
                    if ((ZRoom.getHeight() < 2*ZRoom.getWidth()) && (ZRoom.getWidth() < 2*ZRoom.getHeight()))
                    {
                        System.out.println("Yes");
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

                     for (int i=0;i< NodesForRoom.get(ZRoom).size()-1;i++)
                     {
                         Node node1 = NodesForRoom.get(ZRoom).get(i);
                         Node node2 = NodesForRoom.get(ZRoom).get(i+1);
                         Edge edge = new Edge(EdgeCount++,node1,node2);
                         System.out.println("Dividing Edge: " + edge);
                         mapping.setEdgeLevel(edge, Level.Equal);
                         graph.addEdge(edge);
                         //TODO: set edge capacity correctly
                         edgesCap.set(edge, 10);
                         RoomEdges.add(edge);
                     }
                     //connect the inner nodes with the neighbouring doors
                     Collection<PositionNode> doors = DoorNodesForRoom.get(ZRoom);
                     Collection<Node> innernodes = NodesForRoom.get(ZRoom);
                 
                     for (Node node2: innernodes)
                     { 
                       NodeRectangle rec = mapping.getNodeRectangles().get(node2);
                       List<PlanPoint> points = new LinkedList<>();
                       PlanPoint p = new PlanPoint(rec.get_nw_point().getX(),rec.get_nw_point().getY());
                       PlanPoint q = new PlanPoint(rec.get_ne_point().getX(),rec.get_ne_point().getY());
                       PlanPoint r = new PlanPoint(rec.get_se_point().getX(),rec.get_se_point().getY());
                       PlanPoint s = new PlanPoint(rec.get_sw_point().getX(),rec.get_sw_point().getY());
                       points.add(p);points.add(q);points.add(r);points.add(s);
                       PlanPolygon poly = new PlanPolygon(ds.z.Edge.class);
                       poly.defineByPoints(points);
                       //System.out.println("PlanPolygon: " + poly.toString());
                       
                       for (PositionNode node1: doors)
                       {
                           PlanPoint point = new PlanPoint(node1.getPosition().x,-node1.getPosition().y);
                           if (poly.contains(point))
                           {
                               Edge edge = new Edge(EdgeCount++,node1.getNode(),node2);
                               System.out.println("Edge to door: " + edge);
                               mapping.setEdgeLevel(edge, Level.Equal);
                               graph.addEdge(edge);
                               //TODO: set edge capacity correctly
                               edgesCap.set(edge, 10);
                               RoomEdges.add(edge);  
                           }
                       }
                       if (ZRoom.getAssignmentAreas().size() > 0)
                       {
                           for (Node node: AssignmentNodes.get(ZRoom))
                           { 
                              int x = (int)mapping.getNodeRectangles().get(node).getCenterX();
                              int y = (int)mapping.getNodeRectangles().get(node).getCenterY();
                              PlanPoint point = new PlanPoint(x,y);
                              if (poly.contains(point))
                              {
                                   Edge edge = new Edge(EdgeCount++,node,node2);
                                   System.out.println("Edge between center and assignment area: " + edge);
                                   mapping.setEdgeLevel(edge, Level.Equal);
                                   graph.addEdge(edge);
                                   //TODO: set edge capacity correctly
                                   edgesCap.set(edge, 10);
                                   RoomEdges.add(edge); 
                              }
                           }
                       }
                       if (ZRoom.getEvacuationAreas().size() > 0)
                       {
                           for (Node node: EvacuationNodes.get(ZRoom))
                           {
                              int x = (int)mapping.getNodeRectangles().get(node).getCenterX(); 
                              int y = (int)mapping.getNodeRectangles().get(node).getCenterY();
                              PlanPoint point = new PlanPoint(x,y);
                              if (poly.contains(point))
                              {
                                   Edge edge = new Edge(EdgeCount++,node,node2);
                                   System.out.println("Edge between center and evacuation area: " + edge);
                                   mapping.setEdgeLevel(edge, Level.Equal);
                                   graph.addEdge(edge);
                                   //TODO: set edge capacity correctly
                                   edgesCap.set(edge, 10);
                                   RoomEdges.add(edge); 
                              }
                           }
                       }
                     }
                    }
                    
                }
                    //exactly one room with more than one door and no evacuation areas in rooms with only one door
                    // connect door nodes directly, Example2.zet
                    else if (num==1 && numEvac2 == 0 && ZRoom.getEvacuationAreas().isEmpty())
                    {
                        System.out.println("Case 4a) for room: " + ZRoom.getName());
                        Collection<Node> coll= new HashSet<>(); 
                        Node want = null;        
                        for (PositionNode n1: DoorNodesForRoom.get(MoreDoorRoom))
                        {
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
                        ConnectWithCertainNode(ZRoom,coll,want); 
                        if (!ZRoom.getAssignmentAreas().isEmpty())
                        {
                            for (Node n: AssignNodes)
                            {
                                Edge edge = new Edge(EdgeCount++,n,want);
                                mapping.setEdgeLevel(edge, Level.Equal);
                                graph.addEdge(edge);
                                System.out.println("Edge " + edge + "in Room: " + ZRoom.getName());        
                                edgesCap.set(edge,Integer.MAX_VALUE);
                                
                            }
                        }
                    }
                    //exactly one room with more than one door and one evacuation area inside
                    else if (num==1 && numEvac2==0 && ZRoom.getEvacuationAreas().size()==1)
                    {
                        System.out.println("Case 4b1) for Room: " + ZRoom.getName());
                        HashMap<Point,Integer> doors1 = MoreDoorRoom.getDoors();
                        System.out.println("MoreDoorRoom: " + MoreDoorRoom.getName());
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
                                    System.out.println("Connection1: " + edge);
                                    mapping.setEdgeLevel(edge, Level.Equal);
                                    graph.addEdge(edge);
                                    //TODO: set edge capacity correctly
                                    edgesCap.set(edge, 10);
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
                                    System.out.println("Connection2: " + edge);
                                    mapping.setEdgeLevel(edge, Level.Equal);
                                    graph.addEdge(edge);
                                    //TODO: set edge capacity correctly
                                    edgesCap.set(edge, 10);
                                    RoomEdges.add(edge);
                                }
                            }
                        }
                        //connect the evacuation node with center
                        for (Node node:EvacuationNodes.get(ZRoom))
                        {
                            Edge edge = new Edge(EdgeCount++,center,node);
                            System.out.println("Connection3: " + edge);
                            mapping.setEdgeLevel(edge, Level.Equal);
                            graph.addEdge(edge);
                            //TODO: set edge capacity correctly
                            edgesCap.set(edge, 10);
                            RoomEdges.add(edge);
                        }
                        
                    }               
                else
                {
                 System.out.println("Case 5 for Room: " + ZRoom.getName());
                 //room is rectangular        
                 if (ZRoom.getHeight() > 2*ZRoom.getWidth() || ZRoom.getWidth() > 2*ZRoom.getHeight())
                 {    
                     //connect neighbouring nodes
                     for (int i=0;i< NodesForRoom.get(ZRoom).size()-1;i++)
                     {
                         Node node1 = NodesForRoom.get(ZRoom).get(i);
                         Node node2 = NodesForRoom.get(ZRoom).get(i+1);
                         Edge edge = new Edge(EdgeCount++,node1,node2);
                         System.out.println("Dividing Edge: " + edge);
                         mapping.setEdgeLevel(edge, Level.Equal);
                         graph.addEdge(edge);
                         //TODO: set edge capacity correctly
                         edgesCap.set(edge, 10);
                         RoomEdges.add(edge);
                     }
                     //connect the inner nodes with the neighbouring doors
                     Collection<PositionNode> doors = DoorNodesForRoom.get(ZRoom);
                     Collection<Node> innernodes = NodesForRoom.get(ZRoom);
                 
                     for (Node node2: innernodes)
                     {
                       NodeRectangle rec = mapping.getNodeRectangles().get(node2);
                       List<PlanPoint> points = new LinkedList<>();
                       PlanPoint p = new PlanPoint(rec.get_nw_point().getX(),rec.get_nw_point().getY());
                       PlanPoint q = new PlanPoint(rec.get_ne_point().getX(),rec.get_ne_point().getY());
                       PlanPoint r = new PlanPoint(rec.get_se_point().getX(),rec.get_se_point().getY());
                       PlanPoint s = new PlanPoint(rec.get_sw_point().getX(),rec.get_sw_point().getY());
                       points.add(p);points.add(q);points.add(r);points.add(s);
                       PlanPolygon poly = new PlanPolygon(ds.z.Edge.class);
                       poly.defineByPoints(points);
                       //System.out.println("PlanPolygon: " + poly.toString());
                       
                       for (PositionNode node1: doors)
                       {
                           PlanPoint point = new PlanPoint(node1.getPosition().x,-node1.getPosition().y);
                           if (poly.contains(point))
                           {
                               Edge edge = new Edge(EdgeCount++,node1.getNode(),node2);
                               System.out.println("Edge to door: " + edge);
                               mapping.setEdgeLevel(edge, Level.Equal);
                               graph.addEdge(edge);
                               //TODO: set edge capacity correctly
                               edgesCap.set(edge, 10);
                               RoomEdges.add(edge);  
                           }
                       }
                       if (ZRoom.getAssignmentAreas().size() > 0)
                       {
                           for (Node node: AssignmentNodes.get(ZRoom))
                           { 
                              int x = (int)mapping.getNodeRectangles().get(node).getCenterX();
                              int y = (int)mapping.getNodeRectangles().get(node).getCenterY();
                              PlanPoint point = new PlanPoint(x,y);
                              if (poly.contains(point))
                              {
                                   Edge edge = new Edge(EdgeCount++,node,node2);
                                   System.out.println("Edge between center and assignment area: " + edge);
                                   mapping.setEdgeLevel(edge, Level.Equal);
                                   graph.addEdge(edge);
                                   //TODO: set edge capacity correctly
                                   edgesCap.set(edge, 10);
                                   RoomEdges.add(edge); 
                              }
                           }
                       }
                       if (ZRoom.getEvacuationAreas().size() > 0)
                       {
                           for (Node node: EvacuationNodes.get(ZRoom))
                           {
                              int x = (int)mapping.getNodeRectangles().get(node).getCenterX(); 
                              int y = (int)mapping.getNodeRectangles().get(node).getCenterY();
                              PlanPoint point = new PlanPoint(x,y);
                              if (poly.contains(point))
                              {
                                   Edge edge = new Edge(EdgeCount++,node,node2);
                                   System.out.println("Edge between center and evacuation area: " + edge);
                                   mapping.setEdgeLevel(edge, Level.Equal);
                                   graph.addEdge(edge);
                                   //TODO: set edge capacity correctly
                                   edgesCap.set(edge, 10);
                                   RoomEdges.add(edge); 
                              }
                           }
                       }
                     }
           
                 }
                 //room nearly quadratic
                 else 
                 {
                     Node center = CenterNodeForRoom.get(ZRoom);
                     if (!EvacuationNodes.get(ZRoom).isEmpty())
                     {
                         for (Node node: EvacuationNodes.get(ZRoom))
                         {
                             Edge edge = new Edge(EdgeCount++,node,center);
                             System.out.println("Kante 1: " + edge);
                             mapping.setEdgeLevel(edge, Level.Equal);
                             graph.addEdge(edge);
                             //TODO: set edge capacity correctly
                             edgesCap.set(edge, 10);
                             RoomEdges.add(edge);
                         }  
                     }
                     if (!AssignmentNodes.get(ZRoom).isEmpty())
                     {
                         for (Node node: AssignmentNodes.get(ZRoom))
                         {
                             Edge edge = new Edge(EdgeCount++,node,center);
                             System.out.println("Kante 1: " + edge);
                             mapping.setEdgeLevel(edge, Level.Equal);
                             graph.addEdge(edge);
                             //TODO: set edge capacity correctly
                             edgesCap.set(edge, 10);
                             RoomEdges.add(edge);
                         }  
                     }
                     Collection<PositionNode> doors = DoorNodesForRoom.get(ZRoom);
                     for (PositionNode node: doors)
                     {
                         Edge edge = new Edge(EdgeCount++,node.getNode(),center);
                         System.out.println("edge between center and door: " + edge);
                         mapping.setEdgeLevel(edge, Level.Equal);
                         graph.addEdge(edge);
                         //TODO: set edge capacity correctly
                         edgesCap.set(edge, 10);
                     }
                     /*HashMap<Point,Integer> doors = ZRoom.getDoors(ZRoom);
                     Node LastNode = null;
                     for (Point point: doors.keySet())
                     {
                        if (connection.containsKey(point))
                        {
                            Node node = connection.get(point);                            
                            Edge edge = new Edge(EdgeCount++,node,center);
                            System.out.println("Kante 2: " + edge);
                            mapping.setEdgeLevel(edge, Level.Equal);
                            graph.addEdge(edge);
                            //TODO: set edge capacity correctly
                            edgesCap.set(edge, 10); 
                            //connects the neighbouring doors with an edge
                            if (LastNode != null)
                            {
                                Edge edge2 = new Edge(EdgeCount++,node,LastNode);
                                System.out.println("Kante 3: " + edge2);
                                mapping.setEdgeLevel(edge2, Level.Equal);
                                graph.addEdge(edge2);
                                //TODO: set edge capacity correctly
                                edgesCap.set(edge2, 10);
                                RoomEdges.add(edge2);
                            }
                            LastNode = node;
                        }
                     }*/
                 
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
        //vorerst einfacher euklidischer Abstand der beiden Knotenenden...
        exactTransitTimes = new IdentifiableDoubleMapping<>( 1 );
        List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();

        for (Edge edge: model.getGraph().edges())
        {
            if (edge.end()!= model.getSupersink() && edge.start()!= model.getSupersink())
            {    
                //coordinates of start node
                double startx = mapping.getNodeRectangles().get(edge.start()).getCenterX();
                double starty = mapping.getNodeRectangles().get(edge.start()).getCenterY();

                //coordinates of end node
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
        //ds.z.Edge edge2 = (ds.z.Edge) edge;
        }
        //System.out.println("TransitTimes without delay: " + exactTransitTimes);
        
        for (ZToGraphRoomRaster room: rasteredRooms)
        {
            Room ZRoom = room.getRoom();
            if (ZRoom.getDelayAreas().size() > 0)
            {
                double startX,startY,endX,endY;
                ListSequence<Edge> roomEdges = EdgesForRoom.get(room.getRoom());
                for (Edge edge: roomEdges)
                {
                    System.out.println("Considered Edge: " + edge);
                    Node node1 = edge.start();
                    Node node2 = edge.end();
                    if (mapping.getNodeRectangles().get(node1).getCenterX() < mapping.getNodeRectangles().get(node2).getCenterX())
                    {
                        startX = mapping.getNodeRectangles().get(node1).getCenterX();
                        startY = mapping.getNodeRectangles().get(node1).getCenterY();
                        endX = mapping.getNodeRectangles().get(node2).getCenterX();
                        endY = mapping.getNodeRectangles().get(node2).getCenterY();
                    }
                    else
                    {
                        endX = mapping.getNodeRectangles().get(node1).getCenterX();
                        endY = mapping.getNodeRectangles().get(node1).getCenterY();
                        startX = mapping.getNodeRectangles().get(node2).getCenterX();
                        startY = mapping.getNodeRectangles().get(node2).getCenterY();   
                    }
                            
                    for (DelayArea delay: ZRoom.getDelayAreas())
                    {
                        //TODO: take into account all points on the edge and look if they are inside the area polygon!!!
                        double length = Math.sqrt(Math.pow((startX-endX),2) + Math.pow((startY-endY),2));
                        //saves all points of the edge
                        Collection<PlanPoint> EdgePoints = new HashSet<>((int)length);
                        double a = Math.abs(endY-startY)/Math.abs(endX-startX);
                        double b = endY - (a*endX);
                        for (int i=(int)startX; i<(int)endX+1; i++)
                        {
                            PlanPoint point = new PlanPoint();
                            point.x = i;
                            point.y = (int)(a*i+b);
                            EdgePoints.add(point);
                        }
                        int count = 0;
                        for (PlanPoint p: EdgePoints)
                        {
                            if (delay.contains(p))
                            {
                                count++;
                            }
                        }
                        //System.out.println("punkte: " + EdgePoints.size());
                        //System.out.println("count: " + count);
                        double delaysize = ((double)count)/((double)EdgePoints.size());
                        //System.out.println("delaysize: " + delaysize); 
                        double speed = delay.getSpeedFactor();
                        double transit = (1.0-delaysize)*exactTransitTimes.get(edge) + delaysize*exactTransitTimes.get(edge)*(1.0/speed);
                        exactTransitTimes.set(edge, transit);
                    }
                }
                            
                        }    
    }  
        //System.out.println("TransitTimes: " + exactTransitTimes);
    }
    protected void FindNeighbourRooms()
    {
        //System.out.println("Finding neighbourrooms started...");
        List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();
        
        for( ZToGraphRoomRaster room : rasteredRooms ) 
        {
            neighbours = new HashSet<>();
            //gives all dooredges for the first considered room  
            Collection<ds.z.Edge> dooredgesroom1 = room.getRoom().getDoorEdges();
            //look for all other rooms
            for (ZToGraphRoomRaster room2:rasteredRooms )
            {
                if (room2 != room)
                {
                    //gives all dooredges for the other considered room
                    Collection<ds.z.Edge> dooredgesroom2 = room2.getRoom().getDoorEdges();
                    
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
                           
                           if ((first && second && third && fourth) || (first1 && second1 && third1 && fourth1) )
                           {
                               neighbours.add(room2.getRoom());
                           }
                        }
                    }
                }
            } 
            NeighbourRooms.put(room.getRoom(), neighbours);
        }
    }
    /*
     * Computes some values of neighbourrooms needed to create nodes and edges 
     * (the number of neighbourrooms with only one door, the number of neighbouring evacuation areas)
     * @param room the room for that neighbourrooms are scanned
     */
    public void ComputeNeighbourRoomValues(Room room)
    {
               //gives the neighbours of the current room
               Collection<Room> neighbRooms = NeighbourRooms.get(room);
               num = 0; numEvac1 = 0; numEvac2 = 0; numAssign1=0; numAssign2=0;
               
               for (Room r : neighbRooms)
               {
                    //count rooms with more than one door
                    if (r.getDoorEdges().size() > 1)
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
              double x1 = doorNode.x;
              double y1 = doorNode.y;
              double x2 = mapping.nodeRectangles.get(connectNode).getCenterX();
              double y2 = mapping.nodeRectangles.get(connectNode).getCenterY();
              //System.out.println("x1: " + x1);
              //System.out.println("y1: " + y1);
              //System.out.println("x2: " + x2);
              //System.out.println("y2: " + y2);
              
              Point nodepos = new Point((int)(x1 + x2)/2,(int)(y1 + y2)/2);
              Node node = new Node(nodeCount);
              System.out.println("Centerknoten: " + node);
              graph.setNode(node);
              CenterNodeForRoom.put(r, node);
              //TODO: define exact value of node capacity
              nodesCap.set(node, 10);
              NodeRectangle rec = new NodeRectangle(nodepos.x-1,nodepos.y+1, nodepos.x+1,nodepos.y-1);
              mapping.setNodeRectangle(node, rec );
              model.getZToGraphMapping().getNodeFloorMapping().set( node,plan.getFloorID(r.getAssociatedFloor()));
              model.getZToGraphMapping().setIsEvacuationNode( node, false );
              model.getZToGraphMapping().setIsSourceNode(node, false);
              model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
              nodes.add(node);
              nodeCount++;                      
    }
         
    public void FindRectangulationNodes(ZToGraphRoomRaster Zroom)
    {
         Room room = Zroom.getRoom();
         int unit,rest,xPos,yPos;
         int DirectionRight, DirectionDown; //0 der 1 je nachdem ob Raum höher oder breiter ist...
         if (room.getHeight() < room.getWidth()){
             unit = room.getHeight();
             rest = room.getWidth();
             DirectionRight = 1;
             DirectionDown = 0; }
         else{
             unit = room.getWidth();
             rest = room.getHeight();
             DirectionRight = 0; 
             DirectionDown = 1;  }
                            
             int numIteration=0;
            xPos = (room.getxOffset() + room.getWidth());
            yPos = (room.getyOffset() + room.getHeight());                          
                          
            while (rest > 2*unit) 
            {
                                int upperLeft1_x = room.getxOffset() + (numIteration*DirectionRight*unit); 
                                int upperLeft1_y = room.getyOffset() + (numIteration*DirectionDown*unit);
                                int LowerRight1_x = DirectionDown*xPos + DirectionRight*((numIteration+1)*unit+room.getxOffset());
                                int LowerRight1_y = DirectionRight*yPos + DirectionDown*((numIteration+1)*unit + room.getyOffset());
                                //node at the left/upper end of rectangle
                                Node node1 = new Node(nodeCount++);
                                System.out.println("rectanglar room node: " + node1);
                                graph.setNode(node1);
                                nodesCap.set(node1, 10);                            
                                NodeRectangle rec1 = new NodeRectangle(upperLeft1_x,-(upperLeft1_y),LowerRight1_x, -(LowerRight1_y));
                                mapping.setNodeRectangle(node1, rec1);                            
                                model.getZToGraphMapping().getNodeFloorMapping().set( node1,plan.getFloorID(Zroom.getFloor()));
                                model.getZToGraphMapping().setIsEvacuationNode(node1, false );
                                model.getZToGraphMapping().setIsSourceNode(node1, false);
                                model.getZToGraphMapping().setIsDeletedSourceNode(node1, false );
                                nodes.add(node1);
                                
                                int UpperLeft2_x = DirectionDown*room.getxOffset() + DirectionRight*(xPos-((numIteration+1)*unit));
                                int UpperLeft2_y = DirectionRight*room.getyOffset() + DirectionDown*(yPos-((numIteration+1)*unit));
                                int LowerRight2_x = DirectionRight*(xPos-numIteration*unit) + DirectionDown*xPos;
                                int LowerRight2_y = DirectionRight*yPos + DirectionDown*(yPos-numIteration*unit);
                                Node node2 = new Node(nodeCount++);
                                System.out.println("rectanglar room node: " + node2);
                                graph.setNode(node2);
                                nodesCap.set(node2, 10);                            
                                NodeRectangle rec2 = new NodeRectangle(UpperLeft2_x,-(UpperLeft2_y),LowerRight2_x,-(LowerRight2_y));
                                mapping.setNodeRectangle(node2, rec2);                            
                                model.getZToGraphMapping().getNodeFloorMapping().set( node2,plan.getFloorID(Zroom.getFloor()));
                                model.getZToGraphMapping().setIsEvacuationNode(node2, false );
                                model.getZToGraphMapping().setIsSourceNode(node2, false);
                                model.getZToGraphMapping().setIsDeletedSourceNode(node2, false );
                                nodes.add(node2);
                                
                                rest = rest-2*unit;
                                numIteration++;
                            }
                                Node middle = new Node(nodeCount++);
                                nodes.add(middle);
                                System.out.println("Mittelknoten: " + middle);
                                graph.setNode(middle);
                                nodesCap.set(middle, 10);
                                int midUpperLeft_x = DirectionDown*room.getxOffset() + DirectionRight*(room.getxOffset()+(numIteration*unit));
                                int midUpperLeft_y = DirectionRight*room.getyOffset() + DirectionDown*(room.getyOffset()+(numIteration*unit));
                                int midLowerRight_x = DirectionRight*(xPos-(numIteration*unit)) + DirectionDown*xPos;
                                int midLowerRight_y = DirectionDown*(yPos-numIteration*unit) + DirectionRight*yPos; 
                                NodeRectangle recmidd = new NodeRectangle(midUpperLeft_x, -(midUpperLeft_y),midLowerRight_x,-midLowerRight_y);
                                mapping.setNodeRectangle(middle, recmidd);
                                model.getZToGraphMapping().getNodeFloorMapping().set( middle,plan.getFloorID(Zroom.getFloor()));
                                model.getZToGraphMapping().setIsEvacuationNode( middle, false );
                                model.getZToGraphMapping().setIsSourceNode(middle, false);
                                model.getZToGraphMapping().setIsDeletedSourceNode( middle, false );
                               
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
                   for (PositionNode node2: nodes2)
                   {
                       if (node1.getPosition().equals(node2.getPosition()) && used[node1.getNode().id()][node2.getNode().id()]==0)
                       {
                               Edge edge = new Edge(EdgeCount++,node1.getNode(),node2.getNode());
                               mapping.setEdgeLevel(edge, Level.Equal);
                               graph.setEdge(edge);
                               System.out.println("door connecting edge: " + edge);
                               edgesCap.set(edge,Integer.MAX_VALUE);
                               used[node1.getNode().id()][node2.getNode().id()] = 1;
                               used[node2.getNode().id()][node1.getNode().id()] = 1;
                       }
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
            System.out.println("Edge " + edge + "in Room: " + room.getName());        
            edgesCap.set(edge,Integer.MAX_VALUE);
            RoomEdges.add(edge);
        }
    }
    
    public void connectNodesWithCentralNode(Node center, Collection<Node> nodes)
    {
        
    }
}