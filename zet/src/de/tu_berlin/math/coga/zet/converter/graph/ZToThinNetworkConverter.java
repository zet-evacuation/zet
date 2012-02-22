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
    public ListSequence<Edge> RoomEdges;
    //stores all doors that do not get a node (only if one of the corresponding rooms has only one door and is empty)
    public List<Point> needsNoConnection = new LinkedList<>();
    Map<Node,List<PlanPoint>> coveredArea;
    
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
               System.out.println("Currently considered room: " + room.getRoom().getName());
               Room ZRoom = room.getRoom();
               doorNodes = new HashSet<>();
               EvacNodes = new ListSequence<>();
               AssignNodes = new ListSequence<>();
               nodes = new ListSequence<>();
               ComputeNeighbourRoomValues(ZRoom);


               //create Node for each Assignment Area in these rooms
               for (AssignmentArea area: ZRoom.getAssignmentAreas())
               {
                       Node node = new Node(nodeCount++);
                       System.out.println("AssignmentNode: " + node + " in Room: " + ZRoom.getName());
                       graph.setNode(node);
                       int value = area.getMaxEvacuees();
                       nodesCap.add(node, value); 
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
                       //System.out.println("rastered values: " + startCol + " " + startRow + " "+ numCol + " " + numRow);
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
                    //create node for each evacuation area of building
                    for (EvacuationArea Earea : ZRoom.getEvacuationAreas())
                    {                        
                        Node node = new Node(nodeCount);
                        System.out.println("EvacuationNode: " + node + " in room: " + ZRoom.getName());
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
                    
                    /*if (ZRoom.getDoorEdges().isEmpty())
                    {   
                        if (ZRoom.getEvacuationAreas().size() > 1)
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
                            model.getZToGraphMapping().getNodeFloorMapping().set( node,getProblem().getFloorID(room.getFloor()));
                            model.getZToGraphMapping().setIsEvacuationNode( node, false );
                            model.getZToGraphMapping().setIsSourceNode(node, false);
                            model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                            CenterNodeForRoom.put(ZRoom, node);
                        }
                    }*/
                    
                    //create door nodes for rooms with exactly one door first
                    //if there is at least one evacuation or assignment area in room with only one door, create a node representing the door
                    if (!(ZRoom.getEvacuationAreas().isEmpty() && ZRoom.getAssignmentAreas().isEmpty()) && (ZRoom.getDoorEdges().size()==1))
                    { 
                        //only contains one door
                        HashMap<Point,Integer> doors = ZRoom.getDoors();
                        for (Point p: doors.keySet())
                        {                                     
                                //create node for door                        
                                Node node = new Node(nodeCount);
                                System.out.println("Door Node: " + node + " for room: " + ZRoom.getName());
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
                                model.getZToGraphMapping().getNodeFloorMapping().set( node,getProblem().getFloorID(room.getFloor()));
                                model.getZToGraphMapping().setIsEvacuationNode( node, false );
                                model.getZToGraphMapping().setIsSourceNode(node, false);
                                model.getZToGraphMapping().setIsDeletedSourceNode( node, false );
                        }
                    }
                    //create nodes for rooms with more than one door
                    if (ZRoom.getDoorEdges().size()>1)
                    {
                        //creating door nodes                        
                        HashMap<Point,Integer> doors = ZRoom.getDoors();
                        for (Point p: doors.keySet())
                        {
                            //only create door node if rooms with one door is not empty
                            if (!needsNoConnection.contains(p))
                            {
                                Node node = new Node(nodeCount);
                                connection.put(p,node);
                                //TODO: get exact node capacity
                                nodesCap.add(node, ZRoom.getMaxEvacuees());
                                System.out.println("Door Node: " + node + "for room"  + ZRoom.getName());
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
                //exactly one room with more than one door, no evacuation areas in rooms with one door    
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
                        Node node = new Node(nodeCount++);
                        System.out.println("Create node in the middle: " + node + "for: " + ZRoom.getName());
                        graph.setNode(node);
                        CenterNodeForRoom.put(ZRoom, node);
                        //TODO: define exact value of node capacity
                        nodesCap.set(node, Integer.MAX_VALUE);
                        NodeRectangle rec = new NodeRectangle(ZRoom.getxOffset(),-ZRoom.getyOffset(),ZRoom.getxOffset()+ZRoom.getWidth(),-(ZRoom.getyOffset()+ZRoom.getHeight()));
                        if (ZRoom.getInaccessibleAreas().size() > 0)
                        {
                            PlanPoint p = new PlanPoint(); 
                            p.setLocation(ZRoom.getxOffset() + (ZRoom.getWidth()/2), ZRoom.getyOffset() + (ZRoom.getHeight()/2));
                            for (InaccessibleArea area: ZRoom.getInaccessibleAreas())
                            {
                                if (area.contains(p))
                                {
                                    System.out.println("center lies on inaccessible area... ");
                                    int areaCenterX = area.getxOffset() + (area.getWidth()/2);
                                    rec = new NodeRectangle(areaCenterX-1,-(area.getyOffset()-1),areaCenterX+1,-(area.getyOffset()-1));
                                }                                 
                            }
                        }                        
                        mapping.setNodeRectangle(node, rec );
                        model.getZToGraphMapping().getNodeFloorMapping().set( node,getProblem().getFloorID(room.getFloor()));
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
        } //end of: for all rastered rooms...
        
        model.setNodeCapacities( nodesCap );
        model.setNetwork(graph);
        model.setSources(sources);
        System.out.print("Created nodes: ");
        for (Node node: model.getGraph().nodes())
        {
            System.out.print(node + "; ");
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
            System.out.println("Currently considered room: " + room.getRoom().getName());
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
                System.out.println("Edge to supersink: " + edge);                   
                edgesCap.set(edge,Integer.MAX_VALUE);
            }
            Collection<Room> neighbRooms = NeighbourRooms.get(ZRoom);
            //connect the room with all of its neighbours
            ConnectRooms(ZRoom);
            
            //room has no doors 
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
                if (ZRoom.getEvacuationAreas().size()>1)
                {
                    for (Node n1: AssignmentNodes.get(ZRoom))
                    {
                        for (Node n2: EvacuationNodes.get(ZRoom))
                        {
                            Edge edge = new Edge(EdgeCount++,n1,n2);
                            mapping.setEdgeLevel(edge, Level.Equal);
                            graph.addEdge(edge);
                            System.out.println("Edge between assignment area and evacuation area: " + edge);
                            //set the capacity of an edge to the max. number of persons(in this room) divided by number of assignment areas
                            int cap = ZRoom.getMaxEvacuees()/ZRoom.getAssignmentAreas().size();
                            edgesCap.set(edge,cap);
                            RoomEdges.add(edge); 
                        }
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
                                    //connect evacuation area and assignment area in one room directly
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
                  System.out.println("Case 1 for Room: " + ZRoom.getName());  
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
                    System.out.println("Case 2 for Room: " + ZRoom);
                    if (ZRoom.getHeight() >= 2* ZRoom.getWidth() || ZRoom.getWidth() >= ZRoom.getHeight())
                    {
                        int size = NodesForRoom.get(ZRoom).size();
                     for (int i=0;i< size-2;i++)
                     {
                         Node node1 = NodesForRoom.get(ZRoom).get(i);
                         Node node2 = NodesForRoom.get(ZRoom).get(i+2);
                         Edge edge = new Edge(EdgeCount++,node1,node2);
                         System.out.println("Dividing Edge: " + edge);
                         mapping.setEdgeLevel(edge, Level.Equal);
                         graph.addEdge(edge);
                         //TODO: set edge capacity correctly
                         edgesCap.set(edge, Integer.MAX_VALUE);
                         RoomEdges.add(edge);
                     }
                     
                     Node node_a = NodesForRoom.get(ZRoom).get(size-1);
                     Node node_b = NodesForRoom.get(ZRoom).get(size-2);
                     Edge last = new Edge(EdgeCount++,node_a,node_b);
                     System.out.println("Last Edge: " + last);
                     mapping.setEdgeLevel(last, Level.Equal);
                     graph.addEdge(last);
                     //TODO: set edge capacity correctly
                     edgesCap.set(last, Integer.MAX_VALUE);
                     RoomEdges.add(last);
                     //connect the inner nodes with the neighbouring doors
                     Collection<PositionNode> doors = DoorNodesForRoom.get(ZRoom);
                     Collection<Node> innernodes = NodesForRoom.get(ZRoom);
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
                               System.out.println("Edge to door: " + edge);
                               mapping.setEdgeLevel(edge, Level.Equal);
                               graph.addEdge(edge);
                               //TODO: set edge capacity correctly
                               edgesCap.set(edge, Integer.MAX_VALUE);
                               RoomEdges.add(edge);  
                           }
                       }
                       if (ZRoom.getAssignmentAreas().size() > 0)
                       {
                           for (Node node: AssignmentNodes.get(ZRoom))
                           { 
                              int x = (int)mapping.getNodeRectangles().get(node).getCenterX();
                              int y = -(int)mapping.getNodeRectangles().get(node).getCenterY();
                              PlanPoint point = new PlanPoint(x,y);
                              if (poly.contains(point))
                              {
                                   Edge edge = new Edge(EdgeCount++,node,node2);
                                   System.out.println("Edge between center and assignment area: " + edge);
                                   mapping.setEdgeLevel(edge, Level.Equal);
                                   graph.addEdge(edge);
                                   //TODO: set edge capacity correctly
                                   edgesCap.set(edge, Integer.MAX_VALUE);
                                   RoomEdges.add(edge); 
                              }
                           }
                       }
                       if (ZRoom.getEvacuationAreas().size() > 0)
                       {
                           for (Node node: EvacuationNodes.get(ZRoom))
                           {
                              int x = (int)mapping.getNodeRectangles().get(node).getCenterX(); 
                              int y = -(int)mapping.getNodeRectangles().get(node).getCenterY();
                              PlanPoint point = new PlanPoint(x,y);
                              if (poly.contains(point))
                              {
                                   Edge edge = new Edge(EdgeCount++,node,node2);
                                   System.out.println("Edge between center and evacuation area: " + edge);
                                   mapping.setEdgeLevel(edge, Level.Equal);
                                   graph.addEdge(edge);
                                   //TODO: set edge capacity correctly (smaller than infinity, so that waiting is considered)
                                   edgesCap.set(edge, 10);
                                   RoomEdges.add(edge); 
                              }
                           }
                       }
                     }
                    }
                    else
                    {
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
                    System.out.println("Case 3 for Room: " + ZRoom.getName());
                    //room is more quadratic
                    if ((ZRoom.getHeight() < 2*ZRoom.getWidth()) && (ZRoom.getWidth() < 2*ZRoom.getHeight()))
                    {
                        System.out.println("quadratic room");
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
                     int size = NodesForRoom.get(ZRoom).size();
                     for (int i=0;i< size-2;i++)
                     {
                         Node node1 = NodesForRoom.get(ZRoom).get(i);
                         Node node2 = NodesForRoom.get(ZRoom).get(i+2);
                         Edge edge = new Edge(EdgeCount++,node1,node2);
                         System.out.println("Dividing Edge: " + edge);
                         mapping.setEdgeLevel(edge, Level.Equal);
                         graph.addEdge(edge);
                         //TODO: set edge capacity correctly
                         edgesCap.set(edge, Integer.MAX_VALUE);
                         RoomEdges.add(edge);
                     }
                     
                     Node node_a = NodesForRoom.get(ZRoom).get(size-1);
                     Node node_b = NodesForRoom.get(ZRoom).get(size-2);
                     Edge last = new Edge(EdgeCount++,node_a,node_b);
                     System.out.println("Last Edge: " + last);
                     mapping.setEdgeLevel(last, Level.Equal);
                     graph.addEdge(last);
                     //TODO: set edge capacity correctly
                     edgesCap.set(last, Integer.MAX_VALUE);
                     RoomEdges.add(last);
                     //connect the inner nodes with the neighbouring doors
                     Collection<PositionNode> doors = DoorNodesForRoom.get(ZRoom);
                     Collection<Node> innernodes = NodesForRoom.get(ZRoom);
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
                               System.out.println("Edge to door: " + edge);
                               mapping.setEdgeLevel(edge, Level.Equal);
                               graph.addEdge(edge);
                               //TODO: set edge capacity correctly
                               edgesCap.set(edge, Integer.MAX_VALUE);
                               RoomEdges.add(edge);  
                           }
                       }
                       if (ZRoom.getAssignmentAreas().size() > 0)
                       {
                           for (Node node: AssignmentNodes.get(ZRoom))
                           { 
                              int x = (int)mapping.getNodeRectangles().get(node).getCenterX();
                              int y = -(int)mapping.getNodeRectangles().get(node).getCenterY();
                              PlanPoint point = new PlanPoint(x,y);
                              if (poly.contains(point))
                              {
                                   Edge edge = new Edge(EdgeCount++,node,node2);
                                   System.out.println("Edge between center and assignment area: " + edge);
                                   mapping.setEdgeLevel(edge, Level.Equal);
                                   graph.addEdge(edge);
                                   //TODO: set edge capacity correctly
                                   edgesCap.set(edge, Integer.MAX_VALUE);
                                   RoomEdges.add(edge); 
                              }
                           }
                       }
                       if (ZRoom.getEvacuationAreas().size() > 0)
                       {
                           for (Node node: EvacuationNodes.get(ZRoom))
                           {
                              int x = (int)mapping.getNodeRectangles().get(node).getCenterX(); 
                              int y = -(int)mapping.getNodeRectangles().get(node).getCenterY();
                              PlanPoint point = new PlanPoint(x,y);
                              if (poly.contains(point))
                              {
                                   Edge edge = new Edge(EdgeCount++,node,node2);
                                   System.out.println("Edge between center and evacuation area: " + edge);
                                   mapping.setEdgeLevel(edge, Level.Equal);
                                   graph.addEdge(edge);
                                   //TODO: set edge capacity correctly (smaller than infinity, so that waiting is considered)
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
                        coll.remove(want);
                        //System.out.println("Coll: " + coll.toString());
                        ConnectWithCertainNode(ZRoom,coll,want); 
                        if (!ZRoom.getAssignmentAreas().isEmpty())
                        {
                            for (Node n: AssignmentNodes.get(ZRoom))
                            {
                                Edge edge = new Edge(EdgeCount++,n,want);
                                mapping.setEdgeLevel(edge, Level.Equal);
                                graph.addEdge(edge);
                                System.out.println("Assign Edge " + edge + "in Room: " + ZRoom.getName());        
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
                                    System.out.println("Connection2: " + edge);
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
                            System.out.println("Connection3: " + edge);
                            mapping.setEdgeLevel(edge, Level.Equal);
                            graph.addEdge(edge);
                            //TODO: set edge capacity correctly (smaller than infinity..)
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
                         edgesCap.set(edge, Integer.MAX_VALUE);
                         RoomEdges.add(edge);
                     }
                     //connect the inner nodes with the neighbouring doors
                     Collection<PositionNode> doors = DoorNodesForRoom.get(ZRoom);
                     Collection<Node> innernodes = NodesForRoom.get(ZRoom);
                 
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
                               System.out.println("Edge to door: " + edge);
                               mapping.setEdgeLevel(edge, Level.Equal);
                               graph.addEdge(edge);
                               //TODO: set edge capacity correctly
                               edgesCap.set(edge, Integer.MAX_VALUE);
                               RoomEdges.add(edge);  
                           }
                       }
                       if (ZRoom.getAssignmentAreas().size() > 0)
                       {
                           for (Node node: AssignmentNodes.get(ZRoom))
                           { 
                              int x = (int)mapping.getNodeRectangles().get(node).getCenterX();
                              int y = -(int)mapping.getNodeRectangles().get(node).getCenterY();
                              PlanPoint point = new PlanPoint(x,y);
                              if (poly.contains(point))
                              {
                                   Edge edge = new Edge(EdgeCount++,node,node2);
                                   System.out.println("Edge between center and assignment area: " + edge);
                                   mapping.setEdgeLevel(edge, Level.Equal);
                                   graph.addEdge(edge);
                                   //TODO: set edge capacity correctly
                                   edgesCap.set(edge, Integer.MAX_VALUE);
                                   RoomEdges.add(edge); 
                              }
                           }
                       }
                       if (ZRoom.getEvacuationAreas().size() > 0)
                       {
                           for (Node node: EvacuationNodes.get(ZRoom))
                           {
                              int x = (int)mapping.getNodeRectangles().get(node).getCenterX(); 
                              int y = -(int)mapping.getNodeRectangles().get(node).getCenterY();
                              PlanPoint point = new PlanPoint(x,y);
                              if (poly.contains(point))
                              {
                                   Edge edge = new Edge(EdgeCount++,node,node2);
                                   System.out.println("Edge between center and evacuation area: " + edge);
                                   mapping.setEdgeLevel(edge, Level.Equal);
                                   graph.addEdge(edge);
                                   //TODO: set edge capacity correctly
                                   edgesCap.set(edge, Integer.MAX_VALUE);
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
                             System.out.println("Edge between center and evacuation node: " + edge);
                             mapping.setEdgeLevel(edge, Level.Equal);
                             graph.addEdge(edge);
                             //TODO: set edge capacity correctly
                             edgesCap.set(edge, Integer.MAX_VALUE);
                             RoomEdges.add(edge);
                         }  
                     }
                     if (!AssignmentNodes.get(ZRoom).isEmpty())
                     {
                         for (Node node: AssignmentNodes.get(ZRoom))
                         {
                             Edge edge = new Edge(EdgeCount++,node,center);
                             System.out.println("edge between center and assignment node: " + edge);
                             mapping.setEdgeLevel(edge, Level.Equal);
                             graph.addEdge(edge);
                             //TODO: set edge capacity correctly
                             edgesCap.set(edge, Integer.MAX_VALUE);
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
                         edgesCap.set(edge, Integer.MAX_VALUE);
                         RoomEdges.add(edge);
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
        System.out.println("TransitTimes without delay or inaccessible areas: " + exactTransitTimes);
        
        for (ZToGraphRoomRaster room: rasteredRooms)
        {
            Room ZRoom = room.getRoom();
            //room contains inaccessible areas or delay areas --> recompute transittimes
            if ((!ZRoom.getDelayAreas().isEmpty()) || (!ZRoom.getInaccessibleAreas().isEmpty())  )
            {
                System.out.println("Room with delay/inacc: " + ZRoom.getName() );
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
                        if (!ZRoom.getInaccessibleAreas().isEmpty())
                        {
                            for (InaccessibleArea barrier: ZRoom.getInaccessibleAreas())
                            {                       
                               for (PlanPoint p: points)
                                {
                                    if (barrier.contains(p))
                                    {
                                        System.out.println("Inaccessible Area contains edge: " + edge);
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
                                    if(barrier.getxOffset() >= startX && barrEndX <= endX && barrier.getyOffset() <= UpperY && barrEndY >= LowerY)
                                    { 
                                            System.out.println("case 0...");                                         
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
                                            System.out.println("Case 1a....");
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
                                            System.out.println("Case 1b...");
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
                                            System.out.println("Case 1c...");
                                            Path1 = Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrier.getyOffset(),2)) + Math.sqrt(Math.pow(barrier.getWidth(),2)) + Math.sqrt(Math.pow(barrEndX-endX,2)+Math.pow(barrier.getyOffset()-endY,2)); 
                                            Path2 = Math.sqrt(Math.pow(startX-barrier.getxOffset(),2)+Math.pow(startY-barrEndY,2)) + Math.sqrt(Math.pow(barrier.getWidth(), 2)) + Math.sqrt(Math.pow(barrEndX-endX,2)+Math.pow(barrEndY-endY,2));
                                        }
                                    }
                                    else if (barrier.getyOffset() >= UpperY && barrEndY <= LowerY )
                                    {
                                        //does not fit on left side
                                        if (barrier.getxOffset() < startX && barrEndX <= endX)
                                        {
                                            System.out.println("Case 2a)... ");
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
                                            System.out.println("case 2b)...");
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
                                            System.out.println("Case 2c...");
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
                                        System.out.println("case 3");
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
                                        System.out.println("case 4");
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
                                        System.out.println("Currently not considered case");
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
        System.out.println("TransitTimes nach delay: " + exactTransitTimes);
        System.out.println("Capacities: " + model.getEdgeCapacities().toString());
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
              //System.out.println("x1: " + x1 + "y1: " + y1 + "x2: " + x2 + "y2: " + y2);              
              Point nodepos = new Point((int)(x1 + x2)/2,-((int)(y1 + y2)/2));
              Node node = new Node(nodeCount);
              //System.out.println("Centerknoten: " + node);
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
                                    System.out.println("center lies on inaccessible area... ");
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
         
    public void FindRectangulationNodes(ZToGraphRoomRaster Zroom)
    {
         Room room = Zroom.getRoom();
         //stores the covered area for a certain node, represented as a list of planpoints 
         List<PlanPoint> covered; 
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
                 int UpperRight1_x = DirectionDown*xPos + DirectionRight*((numIteration+1)*unit+room.getxOffset());
                 int UpperRight1_y = room.getyOffset() + (numIteration*DirectionDown*unit);
                 int LowerRight1_x = DirectionDown*xPos + DirectionRight*((numIteration+1)*unit+room.getxOffset());
                 int LowerRight1_y = DirectionRight*yPos + DirectionDown*((numIteration+1)*unit + room.getyOffset());
                 int LowerLeft1_x = room.getxOffset() + (numIteration*DirectionRight*unit);
                 int LowerLeft1_y = DirectionRight*yPos + DirectionDown*((numIteration+1)*unit + room.getyOffset());
                 
                 PlanPoint upperLeft = new PlanPoint(upperLeft1_x,upperLeft1_y);
                 PlanPoint lowerRight = new PlanPoint(LowerRight1_x,LowerRight1_y);
                 PlanPoint upperRight = new PlanPoint(UpperRight1_x,UpperRight1_y);
                 PlanPoint LowerLeft = new PlanPoint(LowerLeft1_x,LowerLeft1_y);
                 covered = new LinkedList<>();
                 covered.add(upperLeft); covered.add(upperRight); covered.add(lowerRight); covered.add(LowerLeft);
                 //System.out.println("covered: " + covered.toString());
                 //node at the left/upper end of rectangle
                 Node node1 = new Node(nodeCount++);
                 coveredArea.put(node1, covered);
                 System.out.println("rectanglar room node: " + node1);
                 graph.setNode(node1);
                 nodesCap.set(node1, 10);                            
                 NodeRectangle rec1 = new NodeRectangle(upperLeft1_x,-(upperLeft1_y),LowerRight1_x, -(LowerRight1_y));
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
                 mapping.setNodeRectangle(node1, rec1);                            
                 model.getZToGraphMapping().getNodeFloorMapping().set( node1,getProblem().getFloorID(Zroom.getFloor()));
                 model.getZToGraphMapping().setIsEvacuationNode(node1, false );
                 model.getZToGraphMapping().setIsSourceNode(node1, false);
                 model.getZToGraphMapping().setIsDeletedSourceNode(node1, false );
                 nodes.add(node1);
                                
                 int UpperLeft2_x = DirectionDown*room.getxOffset() + DirectionRight*(xPos-((numIteration+1)*unit));
                 int UpperLeft2_y = DirectionRight*room.getyOffset() + DirectionDown*(yPos-((numIteration+1)*unit));
                 int UpperRight2_x = DirectionRight*(xPos-numIteration*unit) + DirectionDown*xPos;
                 int UpperRight2_y = DirectionRight*room.getyOffset() + DirectionDown*(yPos-((numIteration+1)*unit));
                 int LowerRight2_x = DirectionRight*(xPos-numIteration*unit) + DirectionDown*xPos;
                 int LowerRight2_y = DirectionRight*yPos + DirectionDown*(yPos-numIteration*unit);
                 int LowerLeft2_x = DirectionDown*room.getxOffset() + DirectionRight*(xPos-((numIteration+1)*unit));
                 int LowerLeft2_y = DirectionRight*yPos + DirectionDown*(yPos-numIteration*unit);
                 
                 PlanPoint upperLeft2 = new PlanPoint(UpperLeft2_x,UpperLeft2_y);
                 PlanPoint lowerRight2 = new PlanPoint(LowerRight2_x,LowerRight2_y);
                 PlanPoint upperRight2 = new PlanPoint(UpperRight2_x,UpperRight2_y);
                 PlanPoint LowerLeft2 = new PlanPoint(LowerLeft2_x,LowerLeft2_y);
                 covered = new LinkedList<>();
                 covered.add(upperLeft2); covered.add(upperRight2); covered.add(lowerRight2); covered.add(LowerLeft2);
                 Node node2 = new Node(nodeCount++);
                 System.out.println("rectanglar room node: " + node2);
                 coveredArea.put(node2, covered);
                 graph.setNode(node2);
                 nodesCap.set(node2,Integer.MAX_VALUE );                            
                 NodeRectangle rec2 = new NodeRectangle(UpperLeft2_x,-(UpperLeft2_y),LowerRight2_x,-(LowerRight2_y));
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
            System.out.println("node in the middle: " + middle);
            graph.setNode(middle);
            nodesCap.set(middle, Integer.MAX_VALUE);
            int midUpperLeft_x = DirectionDown*room.getxOffset() + DirectionRight*(room.getxOffset()+(numIteration*unit));
            int midUpperLeft_y = DirectionRight*room.getyOffset() + DirectionDown*(room.getyOffset()+(numIteration*unit));
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
            coveredArea.put(middle, covered);
            NodeRectangle recmidd = new NodeRectangle(midUpperLeft_x, -(midUpperLeft_y),midLowerRight_x,-midLowerRight_y);
            mapping.setNodeRectangle(middle, recmidd);
            model.getZToGraphMapping().getNodeFloorMapping().set( middle,getProblem().getFloorID(Zroom.getFloor()));
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
                               int width = (int) Math.floor(room.getDoors().get(node1.getPosition())/1000*2);
                               edgesCap.set(edge,width);
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