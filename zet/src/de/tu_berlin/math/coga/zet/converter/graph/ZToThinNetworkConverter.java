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
    
    //maps the number of nodes to the corresponding room 
    public HashMap<Room,Integer> NumNodesForRoom = new HashMap<>();
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
    int numEvac1;
    int numEvac2;
    //stores number of doors of neighbourrooms
    int num;
    int numAssign1;
    int numAssign2;
    //position Nodes to remember which rooms are connected by the given node
    public HashMap<Point, Node> connection = new HashMap<>(); 
    //stores for each edge the corresponding room
    public HashMap<Room,ListSequence<Edge>> EdgesForRoom = new HashMap<>();
    public ListSequence<Edge> RoomEdges = new ListSequence<>();
    
    DynamicNetwork graph = new DynamicNetwork();
    IdentifiableIntegerMapping<Node> nodesCap = new IdentifiableIntegerMapping<>(1);
    int nodeCount = 0;
    
    
    @Override
    protected void createNodes()
    {
        //gives the neighbouring rooms for each room 
        FindNeighbourRooms();
        
        System.out.print( "Create Nodes for thin Network... " );
	List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();
        Collection<Point> usedPos = new HashSet<>();

	// List of sources according to isSource flag of squares
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
               nodes = new ListSequence<>();
               doorNodes = new HashSet<>();
               EvacNodes = new ListSequence<>();
               AssignNodes = new ListSequence<>();
               Room ZRoom = room.getRoom();
               int NodeForRoom = 0;
               
               //gives the neighbours of the current room
               Collection<Room> neighbRooms = NeighbourRooms.get(ZRoom);
               Room MoreDoorRoom = new Room(ZRoom.getAssociatedFloor());
               num = 0;
               numEvac1 = 0;
               numEvac2 = 0;
               numAssign1=0;
               numAssign2=0;
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
                        }
                        if (!r.getAssignmentAreas().isEmpty()){
                            numAssign2++;
                        }
                    }
                }
               
               if (ZRoom.getDoorEdges().size() >= 1)
               {
                    //create Node for each Assignment Area in these rooms
                    for (AssignmentArea area: ZRoom.getAssignmentAreas())
                    {
                       System.out.println("Belegungsknoten in Raum: " + ZRoom.getName()); 
                       Node node = new Node(nodeCount);
                       System.out.println("Belegungsknoten: " + node);
                       graph.setNode(node);
                       int value = area.getMaxEvacuees();
                       nodesCap.add(node, value); 
                       //gespiegelte Werte
                       NodeRectangle rec = new NodeRectangle(area.getxOffset(),-area.getyOffset(), area.getxOffset()+ area.getWidth(), -(area.getyOffset() + area.getHeight()));
                       //NodeRectangle rec = new NodeRectangle(area.getxOffset(),area.getyOffset(), area.getxOffset()+ area.getWidth(), area.getyOffset() + area.getHeight());
                       mapping.setNodeRectangle(node, rec );
                       model.getZToGraphMapping().getNodeFloorMapping().set( node,plan.getFloorID(room.getFloor()));
                       model.getZToGraphMapping().setIsEvacuationNode( node, false );
                       model.getZToGraphMapping().setIsSourceNode(node, true);
                       model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                       sources.add(node);
                       AssignNodes.add(node);
                       nodes.add(node);
                       nodeCount++;
                       NodeForRoom++;
                       //TODO: map raster squares to the assignment node (relevant for ConvertConcreteAssignment()...)
                       int size = room.getRaster();
                       int startCol = (area.getxOffset()-room.getXOffset())/size;
                       int startRow = (area.getyOffset()-room.getYOffset())/size;
                       int numCol = area.getWidth()/size;
                       int numRow = area.getHeight()/size;
                       
                       for (int i=startCol; i<startCol+numCol; i++)
                       {
                           for (int j=startRow; j<startRow+numRow;j++)
                           {
                               ZToGraphRasterSquare square = room.getSquare( i, j );
                               square.mark();
                               square.setNode(node);
                           }
                       }
                    }
                    
                    for (EvacuationArea Earea : ZRoom.getEvacuationAreas())
                    {
                        System.out.println("Evakuierungsknoten in Raum: " + ZRoom.getName());
                        Node node = new Node(nodeCount);
                        System.out.println("Evakuierungsknoten: " + node);
                        graph.setNode(node);
                        int Evalue = Earea.getMaxEvacuees();
                        nodesCap.add(node, Evalue); 
                        //NodeRectangle rec = new NodeRectangle(Earea.getxOffset(),Earea.getyOffset(), Earea.getxOffset()+ Earea.getWidth(), Earea.getyOffset() + Earea.getHeight());

                        NodeRectangle rec = new NodeRectangle(Earea.getxOffset(),-Earea.getyOffset(), Earea.getxOffset()+ Earea.getWidth(), -(Earea.getyOffset() + Earea.getHeight()));
                        mapping.setNodeRectangle(node, rec );
                        model.getZToGraphMapping().getNodeFloorMapping().set( node,plan.getFloorID(room.getFloor()));
                        model.getZToGraphMapping().setIsEvacuationNode( node, true );
                        model.getZToGraphMapping().setIsSourceNode(node, false);
                        model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                        nodes.add(node);
                        EvacNodes.add(node);
                        nodeCount++;
                        NodeForRoom++;
                    }
                    
                    //create door nodes for rooms with exactly one door first
                    //if there is at least one evacuation or assignment area in room with only one door, create an node representing the door
                    if (!(ZRoom.getEvacuationAreas().isEmpty() && ZRoom.getAssignmentAreas().isEmpty()) && (ZRoom.getDoorEdges().size()==1))
                    { 
                        //only contains one door
                        HashMap<Point,Integer> doors = ZRoom.getDoors(ZRoom);
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
                                nodesCap.add(node, Integer.MAX_VALUE); 
                                //usedPos.add(p); 
                                //NodeRectangle rec = new NodeRectangle(p.x-1,p.y+1, p.x+1,p.y-1);
                                NodeRectangle rec = new NodeRectangle(p.x-1,-(p.y+1), p.x+1,-(p.y-1));
                                mapping.setNodeRectangle(node, rec );
                                model.getZToGraphMapping().getNodeFloorMapping().set( node,plan.getFloorID(room.getFloor()));
                                model.getZToGraphMapping().setIsEvacuationNode( node, false );
                                model.getZToGraphMapping().setIsSourceNode(node, false);
                                model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                                NodeForRoom++;
                        }
                    }
                    //create door nodes for rooms with more than one door
                    if (ZRoom.getDoorEdges().size()>1)
                    {
                        System.out.println("Türknoten für Raum:  " + ZRoom.getName());
                        //gives location of neighbouring doors
                        //should give only those for rooms with more than one door
                        HashMap<Point,Integer> doors = ZRoom.getDoors(ZRoom);
                        for (Point p: doors.keySet())
                        {   
                             Node node = new Node(nodeCount);
                             connection.put(p,node);
                                //TODO: get exact node capacity
                                nodesCap.add(node, 10);
                                System.out.println("Türknoten: " + node);
                                PositionNode pos = new PositionNode(node,p,doors.get(p)); 
                                doorNodes.add(pos);
                                graph.setNode(node);
                                usedPos.add(p);
                                //NodeRectangle rec = new NodeRectangle(p.x-1,p.y+1, p.x+1,p.y-1);
                                NodeRectangle rec = new NodeRectangle(p.x-1,-(p.y+1), p.x+1,-(p.y-1));
                                mapping.setNodeRectangle(node, rec );
                                model.getZToGraphMapping().getNodeFloorMapping().set( node,plan.getFloorID(room.getFloor()));
                                model.getZToGraphMapping().setIsEvacuationNode( node, false );
                                model.getZToGraphMapping().setIsSourceNode(node, false);
                                model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                                nodeCount++;
                                NodeForRoom++; 
                        }                      
                        
                    //only neighbours with one door
                    //one evacuation node inside room and one in neighbourrooms (room has more than one neighbour)
                    if (numEvac2 == 1 && num==0 && ZRoom.getEvacuationAreas().size() == 1 && ZRoom.getDoorEdges().size()>1 )
                    {
                        if (ZRoom.getHeight() < 2* ZRoom.getWidth() || ZRoom.getWidth() < 2*ZRoom.getHeight())
                        {    
                            System.out.println("Create center1 for room: " + ZRoom.getName());
                            //create node between two evacuation areas
                            Room EvacRoom= new Room(ZRoom.getAssociatedFloor());
                            //need the room with the evacuationNode
                            for (Room r : neighbRooms)
                            {
                                if (!r.getEvacuationAreas().isEmpty())
                                { 
                                   EvacRoom = r;  
                                   System.out.println("EvacRoom: " + EvacRoom.getName());
                                }
                            }
                            //gives door node for evacuation room
                            for (Point p: EvacRoom.getDoors(EvacRoom).keySet())
                            {
                                createCenterNode(ZRoom,p,EvacNodes.first());
                            }
                        }
                        else
                        {
                             int unit;
                             int rest;
                             int xPos;
                             int yPos;
                             if (ZRoom.getHeight() < ZRoom.getWidth())
                             {
                                unit = ZRoom.getHeight();
                                rest = ZRoom.getWidth();
                                xPos = (ZRoom.getxOffset() + unit)/2;
                                yPos = (ZRoom.getyOffset() + unit)/2;
                                Node node = new Node(nodeCount++);
                                graph.setNode(node);
                                nodesCap.set(node, 10);
                                //NodeRectangle rec = new NodeRectangle(ZRoom.getxOffset(), ZRoom.getyOffset(), ZRoom.getxOffset()+unit, ZRoom.getyOffset()+unit);
                                NodeRectangle rec = new NodeRectangle(ZRoom.getxOffset(), -ZRoom.getyOffset(), ZRoom.getxOffset()+unit, -(ZRoom.getyOffset()+unit));
                                mapping.setNodeRectangle(node, rec );
                                model.getZToGraphMapping().getNodeFloorMapping().set( node,plan.getFloorID(room.getFloor()));
                                model.getZToGraphMapping().setIsEvacuationNode( node, false );
                                model.getZToGraphMapping().setIsSourceNode(node, false);
                                model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                                nodes.add(node);
                                nodeCount++;
                                NodeForRoom++; 
                             }
                             else
                             {
                                 unit = ZRoom.getWidth();
                                 rest = ZRoom.getHeight();
                             } 
                             
                             
                        }
                }
                //exactly one room with more than one door, no evacuation areas in rooms with one door    
                else if (num==1 && numEvac2==0 && ZRoom.getEvacuationAreas().size()==1 && (numAssign2!=0))
                {
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
                               System.out.println("Kante aktuell: " + edge);
                               int x = (edge.getMaxX()+ edge.getMinX())/2;
                               int y = (edge.getMaxY()+ edge.getMinY())/2;
                               Point point = new Point(x,y);
                               //System.out.println("Point: " + point);
                               p.setLocation(point);
                               createCenterNode(ZRoom,p,EvacNodes.first());
                           }
                        } 
                    }
                }
                //more than one room with more doors and more than one evacuation area in neighbouring rooms
                else if (num>1 || numEvac2>1)
                {
                    //room is nearly rectangular
                    if ((ZRoom.getHeight() > 2* ZRoom.getWidth() || ZRoom.getWidth() > 2*ZRoom.getHeight()))
                    {
                        int unit,rest,xPos,yPos;
                        int DirectionRight; //0 der 1 je nachdem ob Raum höher oder breiter ist...
                        if (ZRoom.getHeight() < ZRoom.getWidth()){
                            
                                unit = ZRoom.getHeight();
                                rest = ZRoom.getWidth() - (2*unit);
                                DirectionRight = 1; }
                        else{
                                unit = ZRoom.getWidth();
                                rest = ZRoom.getHeight() - (2*unit);
                                DirectionRight = 0; }
                        
                            xPos = (ZRoom.getxOffset() + ZRoom.getWidth());
                            yPos = (ZRoom.getyOffset() + ZRoom.getHeight());
                            Node node = new Node(nodeCount++);
                            System.out.println("rectanglar room node: " + node);
                            Node node2 = new Node(nodeCount++);
                            System.out.println("rectanglar room node: " + node2);
                            graph.setNode(node);
                            graph.setNode(node2);
                            nodesCap.set(node, 10);
                            nodesCap.set(node2, 10);                            
                            //NodeRectangle rec = new NodeRectangle(ZRoom.getxOffset(), ZRoom.getyOffset(), ZRoom.getxOffset()+unit, ZRoom.getyOffset()+unit);
                            //NodeRectangle rec2 = new NodeRectangle(xPos-unit,yPos-unit,xPos,yPos);
                            
                            NodeRectangle rec = new NodeRectangle(ZRoom.getxOffset(), -ZRoom.getyOffset(), ZRoom.getxOffset()+unit, -(ZRoom.getyOffset()+unit));
                            NodeRectangle rec2 = new NodeRectangle(xPos-unit,-(yPos-unit),xPos,-yPos);
                            mapping.setNodeRectangle(node, rec );
                            mapping.setNodeRectangle(node2, rec2);
                            model.getZToGraphMapping().getNodeFloorMapping().set( node,plan.getFloorID(room.getFloor()));
                            model.getZToGraphMapping().setIsEvacuationNode( node, false );
                            model.getZToGraphMapping().setIsSourceNode(node, false);
                            model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                            nodes.add(node);
                            model.getZToGraphMapping().getNodeFloorMapping().set( node2,plan.getFloorID(room.getFloor()));
                            model.getZToGraphMapping().setIsEvacuationNode( node2, false );
                            model.getZToGraphMapping().setIsSourceNode(node2, false);
                            model.getZToGraphMapping().setIsDeletedSourceNode( node2, false );
                            nodes.add(node2);
                            NodeForRoom = NodeForRoom+2; 
                            System.out.println("Rest: " + rest);
                            if (rest < 2*unit)
                            {
                                Node middle = new Node(nodeCount++);
                                System.out.println("Mittelknoten: " + middle);
                                graph.setNode(middle);
                                nodesCap.set(middle, 10);
                                //NodeRectangle recmidd = new NodeRectangle(room.getXOffset()+(DirectionRight*unit), room.getYOffset()+((1-DirectionRight)*unit),xPos-unit,yPos);
                                NodeRectangle recmidd = new NodeRectangle(room.getXOffset()+(DirectionRight*unit), -(room.getYOffset()+((1-DirectionRight)*unit)),xPos-unit,-yPos);
                                mapping.setNodeRectangle(middle, recmidd);
                                model.getZToGraphMapping().getNodeFloorMapping().set( middle,plan.getFloorID(room.getFloor()));
                                model.getZToGraphMapping().setIsEvacuationNode( middle, false );
                                model.getZToGraphMapping().setIsSourceNode(middle, false);
                                model.getZToGraphMapping().setIsDeletedSourceNode( middle, false );
                                nodes.add(middle);
                            }
                                /*rest = xPos - unit-(ZRoom.getxOffset()+unit);
                                System.out.println("Rest: " + rest);
                                if (rest < 2*unit)
                                {
                                    Node node3 = new Node(nodeCount++);
                                    graph.setNode(node3);
                                    nodesCap.set(node3, 10);
                                    NodeRectangle rec3 = new NodeRectangle(ZRoom.getxOffset()+unit,ZRoom.getyOffset()+unit, xPos-unit, yPos-unit);
                                    mapping.setNodeRectangle(node3, rec3);
                                    model.getZToGraphMapping().getNodeFloorMapping().set( node3,plan.getFloorID(room.getFloor()));
                                    model.getZToGraphMapping().setIsEvacuationNode( node3, false );
                                    model.getZToGraphMapping().setIsSourceNode(node3, false);
                                    model.getZToGraphMapping().setIsDeletedSourceNode( node3, false );
                                    nodes.add(node3);
                                    NodeForRoom++; 
                                }*/
                                
                    }
                             
                    
                    else
                    {
                        System.out.println("Create node in the middle fooooor: " + ZRoom.getName());
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
                        NodeForRoom++;
                    }
                }
                    }
                
          }        
                
     
        NumNodesForRoom.put(ZRoom, NodeForRoom);  
        //stores all the constructed nodes for one room (except door node)
        NodesForRoom.put(ZRoom, nodes); 
        EvacuationNodes.put(ZRoom, EvacNodes);
        AssignmentNodes.put(ZRoom, AssignNodes);
        DoorNodesForRoom.put(ZRoom, doorNodes);
        //System.out.println("DoorNodes: " + doorNodes);
        }
        
        model.setNodeCapacities( nodesCap );
        model.setNetwork(graph);
        model.setSources(sources);
        //System.out.println(NodesForRoom.values());
        for (Node node: model.getGraph().nodes())
        {
            System.out.println("erstellte Knoten: " + node);
        }
    }
    

    
    @Override
    protected void createEdgesAndCapacities()
    {
        System.out.print( "Set up edges and compute capacities... " );
        ZToGraphMapping mapping = model.getZToGraphMapping();
        List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();
        
        int EdgeCount = 0;

        //mapping to store edge capacities
	IdentifiableIntegerMapping<Edge> edgesCap = new IdentifiableIntegerMapping<Edge>( graph.numberOfEdges() * graph.numberOfEdges() );
	model.setEdgeCapacities( edgesCap );

	for( ZToGraphRoomRaster room : rasteredRooms ) 
        {
            System.out.println("Aktueller Raum: " + room.getRoom().getName());
            Room ZRoom = room.getRoom();
            //gives the neighbours of the current room
            Collection<Room> neighbRooms = NeighbourRooms.get(ZRoom);
            //door nodes of the current room
            Collection<PositionNode> nodes1 = DoorNodesForRoom.get(ZRoom);
            Room MoreDoorRoom = new Room(ZRoom.getAssociatedFloor());
            num = 0;
            numEvac1 = 0;
            numEvac2 = 0;
            numAssign2 = 0;
            for (Room r : neighbRooms)
            {
                //count rooms with more than one door
                if (r.getDoorEdges().size() > 1)
                {
                    MoreDoorRoom = r;
                    num++;
                    if (!r.getEvacuationAreas().isEmpty()){                        
                        numEvac1++;//count evacuation areas for rooms with more than one 
                    }
                    if (!r.getAssignmentAreas().isEmpty()){
                        numAssign1++;
                    }                        
                }    
                else
                {
                    if (!r.getEvacuationAreas().isEmpty()){ 
                        numEvac2++;//count evacuation areas for rooms with only one door
                    }
                    if (!r.getAssignmentAreas().isEmpty()){
                        numAssign2++;
                    }
                }
                Collection<PositionNode> nodes2 = DoorNodesForRoom.get(r);
                //edge between door of current room and all neighbour room doors
                for (PositionNode node1: nodes1)
                {
                   System.out.println("doornode1: " + node1.toString(node1));
                   for (PositionNode node2: nodes2)
                   {
                       System.out.println("doornode2: " + node2.toString(node2));
                       if (node1.getPosition().equals(node2.getPosition()))
                       {
                           Edge edge = new Edge(EdgeCount++,node1.getNode(),node2.getNode());
                           mapping.setEdgeLevel(edge, Level.Equal);
                           graph.setEdge(edge);
                           System.out.println("door connecting edge: " + edge);
                           
                           edgesCap.set(edge,Integer.MAX_VALUE);
                       }
                   }
                }
                
            }
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
            
            
            /*System.out.println("Num: " + num);
            System.out.println("numEvac1: " + numEvac1);
            System.out.println("numEvac2: " + numEvac2);*/
            
            //room has only one door
            //if (ZRoom.getDoors(ZRoom) == 1)
            if (ZRoom.getDoorEdges().size() == 1)
            {  
                System.out.println("Case 0 for Room: " + ZRoom.getName());
                  
                    for (Node node: NodesForRoom.get(ZRoom))
                    {
                        //create an edge between all nodes inside a room and its doornode
                        Edge edge = new Edge(EdgeCount++,node, DoorNodeForRoom.get(ZRoom));
                        mapping.setEdgeLevel(edge, Level.Equal);
                        graph.addEdge(edge);
                        System.out.println("yeah Edge: " + edge);
                        int numAreas = ZRoom.getAssignmentAreas().size() + ZRoom.getEvacuationAreas().size();
                        //dividing by numAreas lowers the capacity of the edges to the door 
                        //int width = ((int) Math.floor(((double) ZRoom.getLengthOfDoor(ZRoom))/1000.0)*2)/numAreas;
                        edgesCap.set(edge,Integer.MAX_VALUE);
                        RoomEdges.add(edge);
                    }
                    if (!ZRoom.getEvacuationAreas().isEmpty() )
                    {  
                        for (Node node1: EvacuationNodes.get(ZRoom))
                        {
                            if (!ZRoom.getAssignmentAreas().isEmpty())
                            {
                                for (Node node2: AssignmentNodes.get(ZRoom))
                                {
                                    //connect evacutaion area and assignment area in one room directly
                                    Edge edge = new Edge(EdgeCount++,node1,node2);
                                    mapping.setEdgeLevel(edge, Level.Equal);
                                    graph.addEdge(edge);
                                    //TODO: construct edge capacity
                                    edgesCap.set(edge,10);
                                    RoomEdges.add(edge);
                                }
                            }
                        }
                }
                   
                    
                    
            }
            else if (ZRoom.getDoorEdges().size() > 1)
            {
                //all neighbours have only one door and no evacuation areas
                //connect only evacuation node with doors
                //DONE
                if (numEvac2==0 && num==0)
                {
                  System.out.println("Case 1 for Room: " + ZRoom);
                  for (Node node: EvacuationNodes.get(ZRoom))
                  {
                        for (Room room2 : neighbRooms)
                        {    
                            //gives the door node for the neighbour room
                            //maybe NULL if room is empty
                            Node node2 = DoorNodeForRoom.get(room2);
                            //create Edge between the inner nodes of room and all door rooms for neighbours

                            if (node2 != null)
                            {
                                Edge edge = new Edge(EdgeCount++,node, node2);
                                mapping.setEdgeLevel(edge, Level.Equal);
                                graph.addEdge(edge);
                                //TODO: set edge capacity correctly
                                edgesCap.set(edge, 10);
                                RoomEdges.add(edge);
                            }
                        }
                  }
                }
                //there is one evacuationNode in a neighbour room and not only one on room itself
                //connect all doors with the door of evacuation room
                else if (numEvac2 == 1 && num==0 && ZRoom.getEvacuationAreas().size() != 1)
                {
                    System.out.println("Case 2 for Room: " + ZRoom);
                    Room EvacRoom= new Room(ZRoom.getAssociatedFloor());
                    //need the room with the evacuationNode
                    for (Room r : neighbRooms)
                    {
                        if (!r.getEvacuationAreas().isEmpty())
                        {
                           //gives door node for this room  
                           EvacRoom = r;                          
                        }
                    }
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
                    System.out.println("Case 3 for Room: " + ZRoom);
                    //room is nearly quadratic
                    if ((ZRoom.getHeight() < 2*ZRoom.getWidth()) || (ZRoom.getWidth() < 2*ZRoom.getHeight()))
                    {
                        System.out.println("Yes");
                        Node center = CenterNodeForRoom.get(ZRoom);
                        //connect all door nodes of neighbours with center node
                        for (Room r: neighbRooms)
                        {
                            Node door = DoorNodeForRoom.get(r);
                            if (door != null)
                            {
                                Edge edge = new Edge(EdgeCount++,center,door);
                                mapping.setEdgeLevel(edge, Level.Equal);
                                graph.addEdge(edge);
                                //TODO: set edge capacity correctly
                                edgesCap.set(edge, 10);
                                RoomEdges.add(edge);
                            }
                        }
                        //connect the inner evacuation node with center node
                        for (Node node:EvacuationNodes.get(ZRoom))
                        {
                            Edge edge = new Edge(EdgeCount++,center,node);
                            mapping.setEdgeLevel(edge, Level.Equal);
                            graph.addEdge(edge);
                            //TODO: set edge capacity correctly
                            edgesCap.set(edge, 10);
                            RoomEdges.add(edge);
                        }
                    }
                    //room is more rectangular
                    else 
                    {
                        int unit;
                        if (ZRoom.getHeight() < ZRoom.getWidth())
                        {
                            unit = ZRoom.getHeight();
                        }
                        else
                        {
                            unit = ZRoom.getWidth();
                        }
                        
                    }
                }
                    //exactly one room with more than one door and no evacuation areas in rooms with only one door
                    else if (num==1 && numEvac2 == 0 && ZRoom.getEvacuationAreas().isEmpty())
                    {
                        System.out.println("Case 4 for Room: " + ZRoom.getName());
                        Collection<PositionNode> doors = DoorNodesForRoom.get(ZRoom);
                        System.out.println("doors: " + doors);
                        for (PositionNode node1: doors  )
                        {
                            for (Room r: neighbRooms)
                            {
                                if (r!= MoreDoorRoom)
                                {
                                   Node node2 = DoorNodeForRoom.get(r);
                                   if (node2 != null)
                                   {
                                       System.out.println("edge between: " + node1 + " und " + node2);
                                       
                                       Edge edge = new Edge(EdgeCount++,node1.getNode(),node2);
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
                    //exactly one room with more than one door and one evacuation area inside
                    else if (num==1 && numEvac2 == 0 && ZRoom.getEvacuationAreas().size()==1 && numAssign2!=0)
                    {
                        System.out.println("Case 4b1) for Room: " + ZRoom.getName());
                        HashMap<Point,Integer> connect = MoreDoorRoom.getDoors(MoreDoorRoom);
                        System.out.println("MoreDoorRoom: " + MoreDoorRoom.getName());
                        Node center = CenterNodeForRoom.get(ZRoom);
                        for (Point p: connect.keySet())
                        {
                            if (connection.containsKey(p))
                            {
                                Node node1 = connection.get(p);
                                Edge edge = new Edge(EdgeCount++,node1,center);
                                System.out.println("Connection1: " + edge);
                                mapping.setEdgeLevel(edge, Level.Equal);
                                graph.addEdge(edge);
                                //TODO: set edge capacity correctly
                                edgesCap.set(edge, 10);
                                RoomEdges.add(edge);
                            }
                        }
                        
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
                    //no evacuation and assignment areas for rooms with one door, only one room with more than one door
                    else if (numAssign2==0 && num==1 && numEvac2==0)
                    {
                        System.out.println("Case 4b2 for Room: " + ZRoom.getName());
                        HashMap<Point,Integer> connect = MoreDoorRoom.getDoors(MoreDoorRoom);
                        for (Point p: connect.keySet())
                        {
                            if (connection.containsKey(p))
                            {
                                //gives node of the door
                                Node node1 = connection.get(p);
                                if (!ZRoom.getEvacuationAreas().isEmpty())
                                {
                                    for (Node node: EvacuationNodes.get(ZRoom))
                                    {
                                        Edge edge = new Edge(EdgeCount++,node, node1);
                                        System.out.println("Connection3a: " + edge);
                                        mapping.setEdgeLevel(edge, Level.Equal);
                                        graph.addEdge(edge);
                                        //TODO: set edge capacity correctly
                                        edgesCap.set(edge, 10);
                                        RoomEdges.add(edge);
                                    }
                                }
                                if (!ZRoom.getAssignmentAreas().isEmpty())
                                {
                                    for (Node node: AssignmentNodes.get(ZRoom))
                                    {
                                        Edge edge = new Edge(EdgeCount++,node, node1);
                                        System.out.println("Connection3b: " + edge);
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
                else
                {
                 System.out.println("Case 5 for Room: " + ZRoom.getName());
                 //room is rectangular
                 System.out.println("Höhe: " +ZRoom.getHeight());
                 System.out.println("Weite: " +ZRoom.getWidth());
                 if (ZRoom.getHeight() > 2*ZRoom.getWidth() || ZRoom.getWidth() > 2*ZRoom.getHeight())
                 {
                     System.out.println("Yes");
                 }
                 //room rectangular
                 else 
                 {
                     Node center = CenterNodeForRoom.get(ZRoom);
                     System.out.println("Center: " + center);
                     Collection<Node> inner = NodesForRoom.get(ZRoom);
                     for (Node node: inner)
                     {
                         Edge edge = new Edge(EdgeCount++,node,center);
                         System.out.println("Kante 1: " + edge);
                         mapping.setEdgeLevel(edge, Level.Equal);
                         graph.addEdge(edge);
                         //TODO: set edge capacity correctly
                         edgesCap.set(edge, 10);
                         RoomEdges.add(edge);
                     }  
                     HashMap<Point,Integer> doors = ZRoom.getDoors(ZRoom);
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
                     }
                 
                 }
                }
                //create edge between assignment- and evacuation nodes
                for (Node node_a: EvacuationNodes.get(ZRoom))
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
                }
                
                
            }
        //stores all the created edges for the current Room    
        EdgesForRoom.put(ZRoom, RoomEdges);            
        }
    model.setEdgeCapacities(edgesCap);  
    System.out.println(model.getEdgeCapacities());
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
        System.out.println("TransitTimes without delay: " + exactTransitTimes);
        
        for (ZToGraphRoomRaster room: rasteredRooms)
        {
            System.out.println("Transittimes For Room: " + room.getRoom().getName());
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
                        Collection<PlanPoint> EdgePoints = new HashSet<PlanPoint>((int)length);
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
                        System.out.println("punkte: " + EdgePoints.size());
                        System.out.println("count: " + count);
                        double delaysize = ((double)count)/((double)EdgePoints.size());
                        System.out.println("delaysize: " + delaysize); 
                        double speed = delay.getSpeedFactor();
                        double transit = (1.0-delaysize)*exactTransitTimes.get(edge) + delaysize*exactTransitTimes.get(edge)*(1.0/speed);
                        exactTransitTimes.set(edge, transit);
                    }
                }
                            
                        }
        }
        System.out.println("Transitzeiten: " + exactTransitTimes.toString());
    }
    
    protected void FindNeighbourRooms()
    {
        //System.out.println("Finding neighbourrooms started...");
        List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();
        
        for( ZToGraphRoomRaster room : rasteredRooms ) 
        {
            neighbours = new HashSet<Room>();
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
            //System.out.println("Raum: " + room.getRoom().getName());
            //System.out.println("Nachbarn: " + neighbours);
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
              System.out.println("x1: " + x1);
              System.out.println("y1: " + y1);
              System.out.println("x2: " + x2);
              System.out.println("y2: " + y2);
              
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
         
      
}
