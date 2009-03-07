/*
 * ZToGraphConverter.java
 *
 */
package converter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.lang.Math;
import java.util.Collection;
import java.util.Hashtable;
import java.util.ArrayList;

import localization.Localization;

import util.Direction;
import util.GraphInstanceChecker;
import util.Level;
import util.DebugFlags;
import ds.PropertyContainer;
import ds.graph.DynamicNetwork;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.NetworkFlowModel;
import ds.graph.Node;
import ds.graph.Edge;
import ds.graph.Graph;
import ds.graph.IdentifiableCollection;
import ds.graph.NodeRectangle;
import ds.z.BuildingPlan;
import ds.z.ConcreteAssignment;
import ds.z.Person;
import ds.z.PlanPoint;
import ds.z.Room;
import static util.Direction.*;
import static util.Level.*;
import util.ProgressBooleanFlags;

/**
 *
 */
public class ZToGridGraphConverterAlt2 {
    
    final static int FACTOR = 1;
    public static void convertBuildingPlan(BuildingPlan plan, NetworkFlowModel model) {
        
    	if (ProgressBooleanFlags.GRAPH_CREATION_PROGRESS){
    		System.out.println("Progress: The Z->graph converter starts to convert a building plan.");
    	}
    	
    	// create new mapping
    	ZToGraphMapping mapping = new ZToGraphMapping();
        
    	// Convert the building plan into a rastered version (ZToGraphRaster)
    	ZToGraphRasterContainer raster = createRaster(plan);
        mapping.setRaster(raster);
        
        model.setZToGraphMapping(mapping);
        // calculate and save dynamic graph
        convertRaster(raster, model, plan);
        
        // transform dynamic graph into static network
        model.setNetwork(model.getGraph().getAsStaticNetwork());
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
    public static void convertConcreteAssignment(ConcreteAssignment assignment, NetworkFlowModel model) {
    	ZToGraphMapping mapping = model.getZToGraphMapping();
        ZToGraphRasterContainer raster = mapping.getRaster();        
        
        // the new converted node assignment
        IdentifiableIntegerMapping<Node> nodeAssignment = new IdentifiableIntegerMapping<Node>(1);
        List<Person> persons = assignment.getPersons();                
        
        // setting the people requirement (negative assignment) to the number of persons in the building
        Node superSink = model.getSupersink();
        nodeAssignment.set(superSink,-persons.size());
        
        // for every person do
        for(int i=0; i<persons.size(); i++){
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
            if (nodeAssignment.isDefinedFor(node)){
                nodeAssignment.increase(node,1);
            } else {
                nodeAssignment.set(node,1);
            }
        }
        
        // set node assignment to 0 for every node the assignment has not already defined for
        IdentifiableCollection<Node> nodes = model.getGraph().nodes();        
        for (int i=0; i<nodes.size(); i++){
            if (!nodeAssignment.isDefinedFor(nodes.get(i))){                    
                    nodeAssignment.set(nodes.get(i), 0);
            }
        }
        
        // set the network flow model's assignment to the calculated node assignment
        model.setCurrentAssignment(nodeAssignment); 
        
        checkSupplies(model);
        
        if (ProgressBooleanFlags.GRAPH_CREATION_PROGRESS){
        	System.out.println("Progress: A concrete assignment has been converted into supplies and demands for the graph.");
        }
        
    }

    /**
     * Deletes sources that cannot reach a sink. These nodes are marked as deleted sources.
     * @param model the <code>NetworkFlowModel</code> object.
     */
    private static void checkSupplies(NetworkFlowModel model){
    	Network network = model.getNetwork();
    	IdentifiableIntegerMapping<Node> supplies = model.getCurrentAssignment();
    	
    	GraphInstanceChecker checker = new GraphInstanceChecker(network, supplies);
    	checker.supplyChecker();
    	
    	if (checker.hasRun()){
    		model.setCurrentAssignment(checker.getNewSupplies());
    		model.setSources(checker.getNewSources());
    		ZToGraphMapping mapping = model.getZToGraphMapping();
    		for (Node oldSource : checker.getDeletedSources()){
    			mapping.setIsSourceNode(oldSource, false);
    			mapping.setIsDeletedSourceNode(oldSource, true);
    		}
    	} else
    		throw new AssertionError(Localization.getInstance (
			).getString ("converter.NoCheckException"));    	
    }
    
    protected static ZToGraphRasterContainer createRaster(BuildingPlan plan) {
    	ZToGraphRasterContainer container =  RasterContainerCreator.getInstance().ZToGraphRasterContainer(plan);    	
    	return container;
    }
    
    protected static void convertRaster(ZToGraphRasterContainer raster, NetworkFlowModel model, BuildingPlan plan) {
    	// create nodes of the dynamic graph
        createNodes(raster, model, plan);
        // create edges, their capacities and the capacities of the nodes
        calculateEdgesAndCapacities(raster, model);
        // connect the nodes of different rooms with edges 
        Hashtable<Edge,ArrayList<ZToGraphRasterSquare>> doorEdgeToSquare = connectRooms(raster, model);
        // calculate the transit times for all edges 
        calculateTransitTimes(raster, model, doorEdgeToSquare);      
        // dublicate the edges and their transit times (except those concerning the super sink)
        dublicateEdges(model);
        // adjust transit times according to stair speed factors
        multiplyWithUpAndDownSpeedFactors(model);
        if (ProgressBooleanFlags.GRAPH_CREATION_PROGRESS){
        	System.out.println("Progress: The network, capacities and transit times were created.");
        }
        if (DebugFlags.CONVERTING){
        	System.out.println("Network:");
        	System.out.println(model.getNetwork());
        	System.out.println("transit times:");
        	System.out.println(model.getTransitTimes());
        	System.out.println("edge capacities:");
        	System.out.println(model.getEdgeCapacities());
        }
    }

    // i,j stehen fuer das eigentliche square, nicht fuer den nachbarn!
    private static boolean isRightSquareBlocked(ZToGraphRoomRaster room, int i, int j, boolean careForDelayAreas, boolean careForAssignmentAreas) {
		int numOfColumns = room.getColumnCount();
		int numOfRows = room.getRowCount();
		
		if (i >= numOfColumns-1) return true;
		if (j >= numOfRows) return true;
		
		ZToGraphRasterSquare square = room.getSquare(i,j);
		if (square.isBlocked(RIGHT)) return true;
		
		ZToGraphRasterSquare right = room.getSquare(i+1,j);
		
		if (right.inaccessible()) return true;
		if (right.isMarked()) return true;
		
		if (careForDelayAreas){
			if (square.getSpeedFactor()!=right.getSpeedFactor())
				return true;
		}
		
		if (careForAssignmentAreas){
			if (square.isSource()!=right.isSource())
				return  true;
		}
		
		// a node is save or not but not both
		if (square.getSave() != right.getSave()) return true;
		
		// Only squares from stairs with the same up and down speedfactor may be in the same node
		// (or squares that are not in a stair with stairs that are also not in a stair)
		if (square.getUpSpeedFactor() != right.getUpSpeedFactor()){
			return true;
		}
		if (square.getDownSpeedFactor() != right.getDownSpeedFactor()){
			return true;
		}
		
		return false;
	}
    
    private static boolean isDownSquareBlocked(ZToGraphRoomRaster room, int i, int j, boolean careForDelayAreas,boolean careForAssignmentAreas) {
		int numOfColumns = room.getColumnCount();
		int numOfRows = room.getRowCount();
		
		if (i >= numOfColumns) return true;
		if (j >= numOfRows -1) return true;
		
		ZToGraphRasterSquare square = room.getSquare(i,j);
		if (square.isBlocked(DOWN)) return true;
		
		ZToGraphRasterSquare down = room.getSquare(i,j+1);
		
		if (down.inaccessible()) return true;
		if (down.isMarked()) return true;
		
		if (careForDelayAreas){
			if (square.getSpeedFactor()!=down.getSpeedFactor())
				return true;
		}
		
		if (careForAssignmentAreas)
			if (square.isSource()!=down.isSource())
				return true;
		
		// a node is save or not but not both
		if (square.getSave() != down.getSave()) return true;
		
		// Only squares from stairs with the same up and down speedfactor may be in the same node
		// (or squares that are not in a stair with stairs that are also not in a stair)
		if (square.getUpSpeedFactor() != down.getUpSpeedFactor()){
			return true;
		}
		if (square.getDownSpeedFactor() != down.getDownSpeedFactor()){
			return true;
		}
		
		return false;
	}
    
    /**
	 * Finds rectangles in the rastered rooms to define nodes in the graph.
	 * @param rasterContainer an object containing the rasterized version of a BuildingPlan
	 * @param model the model that will get the graph containing the made nodes.
     * @param plan 
	 */
	protected static void createNodes(ZToGraphRasterContainer rasterContainer,
			NetworkFlowModel model, BuildingPlan plan) {
		System.out.println("create Nodes");
                List<ZToGraphRoomRaster> rasteredRooms = rasterContainer
				.getAllRasteredRooms();

		// New graph
		DynamicNetwork graph = new DynamicNetwork();

		// speed mapping
		ZToGraphMapping mapping = model.getZToGraphMapping();
				
		// List of sources according to isSource flag of squares
		LinkedList<Node> sources = new LinkedList<Node>();

		// super sink
		Node supersink = new Node(0);
		graph.setNode(supersink);
		model.setSupersink(supersink);
		
		mapping.setNodeSpeedFactor(supersink, 1);
		mapping.setNodeRectangle(supersink, new NodeRectangle(0,0,0,0));
		mapping.setFloorForNode(supersink, -1);

		// get attribute from property container
		PropertyContainer propertyContainer = PropertyContainer.getInstance();
		boolean accurateDelayAreaCreation = propertyContainer.getAsBoolean("converter.AccurateDelayAreaCreation");
		boolean accurateAssignmentAreaCration = propertyContainer.getAsBoolean("converter.accurateAssignmentAreaCreation");
		if (DebugFlags.NODECREATION) {
			if (accurateDelayAreaCreation)
				System.out.println("Delay areas are taken into account.");
			else
				System.out.println("Delay areas are not taken into account.");
			System.out.println();
		}
		
		int nodeCount = 1;

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
					
					int nodeRectangleNW_x = roomOffsetX + x*room.getRaster();
					int nodeRectangleNW_y = roomOffsetY + y*room.getRaster();
					// Finding the upper right square by looking for the maximal x and y.
					int maxX = x;
					int maxY = y;

					if (square.accessible() && !square.isMarked()) {

						Node node = new Node(nodeCount);
						model.getZToGraphMapping().getNodeFloorMapping().set(node, plan.getFloorID(room.getFloor()));
						model.getZToGraphMapping().setIsEvacuationNode(node, square.isExit());
						model.getZToGraphMapping().setIsSourceNode(node, square.isSource());
						model.getZToGraphMapping().setIsDeletedSourceNode(node, false);
						if(plan.getFloorID(room.getFloor()) == -1 ){
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
						nodeCount++;

						// set the node of the current square to the new node
						// and mark it as processed.
						square.mark();						
						square.setNode(node);
						// If the isSource flag of the square is set, 
						// the node becomes a source.
						if (square.isSource())
							nodeIsSource = true;

						mapping.setNodeSpeedFactor(node, sumOfSpeedFactors / numOfSquares);
						mapping.setNodeUpSpeedFactor(node, upSpeedFactor);
						mapping.setNodeDownSpeedFactor(node, downSpeedFactor);
						// calculate the lower right corner of the node rectangle.
						int nodeRectangleSE_x = roomOffsetX + room.getRaster()*(maxX+1);
						int nodeRectangleSE_y = roomOffsetY + room.getRaster()*(maxY+1);
						// save the node rectangle in the mapping
						mapping.setNodeRectangle(node, new NodeRectangle(nodeRectangleNW_x,nodeRectangleNW_y, nodeRectangleSE_x, nodeRectangleSE_y));
						// save the number of the floor the node belongs to
						mapping.setFloorForNode(node,rasterContainer.getFloors().indexOf(room.getFloor()));										
						if (nodeIsSource)
							sources.add(node);
					}
				}
			}
			if (ProgressBooleanFlags.GRAPH_CREATION_PROGRESS){
				System.out.println("Progress: A rastered room was processed and subdivided into nodes.");
			}
			if (DebugFlags.NODECREATION){
				System.out.println("A rastered room was processed and got subdevided like this:");
				System.out.print(room);
			}
		}
		// Set graph to model
		model.setNetwork(graph);
		model.setSources(sources);
                System.out.println("create Nodes FERTIG");
	}

    protected static void calculateEdgesAndCapacities(ZToGraphRasterContainer raster, 
            NetworkFlowModel model)  
    {
        System.out.println("calculate Edges & Capacities");
    	ZToGraphMapping mapping = model.getZToGraphMapping();
    	
        List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();
        int nextEdge = 0;
        
        DynamicNetwork graph = model.getDynamicNetwork();

        //Two mappings to store capacities
        IdentifiableIntegerMapping<Node> nodesCap = new
               IdentifiableIntegerMapping<Node>(graph.numberOfNodes());
        model.setNodeCapacities(nodesCap);
        
        IdentifiableIntegerMapping<Edge> edgesCap = new 
               IdentifiableIntegerMapping<Edge>(graph.numberOfEdges()*graph.numberOfEdges());
        model.setEdgeCapacities(edgesCap);
        
        // set node capacity of super sink to max value
        nodesCap.set(model.getSupersink(), Integer.MAX_VALUE);
                
        for (ZToGraphRoomRaster room : rasteredRooms)
        {            

            int colCount = room.getColumnCount();
            int rowCount = room.getRowCount();
            
            Node lastNode = null;
            
            //iterate over each square VERTICALLY
            for(int row = 0 ; row < rowCount; row++)
            {
                for(int col = 0 ; col < colCount; col++)
                {
                    ZToGraphRasterSquare square = room.getSquare(col,row);
                    Node node = square.getNode();
                    //increase node capacity
                    if(node != null)
                        nodesCap.increase(node, 1*FACTOR);
                    
                    boolean nodesConnectable = (node != null) && (lastNode != null) && !lastNode.equals(node);
                    boolean connectionPassable = (col != 0) && (!square.isBlocked(LEFT));
                    
                    if(nodesConnectable && connectionPassable)
                    {
                        Edge edge = graph.getEdge(lastNode, node);
                        if(edge == null )
                        {
                            edge = new Edge(nextEdge++, lastNode, node);
                            graph.addEdge(edge);
                            edgesCap.set(edge, 0);
                            ZToGraphRasterSquare lastSquare=null;
                            if (col > 0){
                            	lastSquare = room.getSquare(col-1,row);                            	
                            } else throw new AssertionError("Col should not be zero at this point.");
                            mapping.setEdgeLevel(edge, lastSquare.getLevel(Direction.getDirection(1, 0)));
                        }
                        edgesCap.increase(edge, 1*FACTOR);
                    }                      
                    lastNode = node;
              }// end of the outer for each loop
                lastNode=null;
            }//end for each room
           
           
            //Iterate now VERTICALLY to add the capacities of the
            lastNode = null;
            for(int col = 0 ; col < colCount; col++)
            {
                for(int row = 0 ; row < rowCount; row++)
                {
                    ZToGraphRasterSquare square = room.getSquare(col, row);
                    Node node = square.getNode();
                    //No need to increase the capacity since the square has already been taken in consideration
 
                    boolean nodesConnectable = (node != null) && (lastNode != null) && !lastNode.equals(node);
                    boolean connectionPassable = (row != 0) && (!square.isBlocked(UP));
                    
                    if(nodesConnectable && connectionPassable)
                    {
                        Edge edge = graph.getEdge(lastNode, node);
                        if(edge == null )
                        {
                            edge = new Edge(nextEdge++, lastNode, node);
                            graph.addEdge(edge);
                            edgesCap.set(edge, 0);
                            ZToGraphRasterSquare lastSquare=null;
                            if (row > 0){
                            	lastSquare = room.getSquare(col,row-1);                            	
                            } else throw new AssertionError(Localization.getInstance (
                			).getString ("converter.RowIsZeroException"));
                            mapping.setEdgeLevel(edge, lastSquare.getLevel(Direction.getDirection(0, 1)));
                        }
                        edgesCap.increase(edge, 1*FACTOR);
                    }
                    lastNode = node;
                }// end of the inner for each loop    
                lastNode=null;
            }// end of the outer for each loop    
        }//end for each room
        model.setNodeCapacities(nodesCap);
        model.setEdgeCapacities(edgesCap);
        System.out.println("calculate Edges & Capacities FERTIG");
    }//end of function
            
    private static PlanPoint calculateCentre(Node node, List<ZToGraphRasterSquare> squareList){
        int nodeBreadth;
        int nodeHeight;        
                        
        int nodeCentreX;
        int nodeCentreY;        
                        
        // coordinates of the upper left corner of the upper left square of the start node
        int nodeUpperLeftX = Integer.MAX_VALUE;
        int nodeUpperLeftY = Integer.MAX_VALUE;
        // coordinates of the lower right corner of the lower right square of the start node
        int nodeLowerRightX = 0;
        int nodeLowerRightY = 0;
                                                            
        // find the coordinates of the upper left and the lower right square of the current start-node
        for(ZToGraphRasterSquare square : squareList){                            
            if (node.id() == square.getNode().id()){
                if (square.getXOffset() <= nodeUpperLeftX) nodeUpperLeftX = square.getXOffset();
                if (square.getYOffset() <= nodeUpperLeftY) nodeUpperLeftY = square.getYOffset();
                if (square.getXOffset() >= nodeLowerRightX) nodeLowerRightX = square.getXOffset();
                if (square.getYOffset() >= nodeLowerRightY) nodeLowerRightY = square.getYOffset();                            
            }
        }
        // adapt to rectangle corner coordinates
        nodeLowerRightX += 400;
        nodeLowerRightY += 400;
        // calculate the centre-coordinates of the start-node-rectangle
        nodeBreadth = Math.abs(nodeLowerRightX - nodeUpperLeftX);
        nodeHeight = Math.abs(nodeLowerRightY - nodeUpperLeftY);
        nodeCentreX = (int)Math.round(0.5 * nodeBreadth) + nodeUpperLeftX;
        nodeCentreY = (int)Math.round(0.5 * nodeHeight) + nodeUpperLeftY;
        
        PlanPoint point = new PlanPoint(nodeCentreX,nodeCentreY);
        return point;        
    }
    
    private static PlanPoint calculateCentre(ZToGraphRasterSquare square){        
        int squareCentreX, squareCentreY;
        
        squareCentreX = square.getXOffset() + 200;
        squareCentreY = square.getYOffset() + 200;
        
        PlanPoint point = new PlanPoint(squareCentreX,squareCentreY);
        return point;
    }    
    
    private static int calculateDistance(PlanPoint start, PlanPoint end){
        int distance;
        int distanceX, distanceY;
        int startX = start.getXInt();
        int startY = start.getYInt();
        int endX = end.getXInt();
        int endY = end.getYInt();
        
        distanceX = Math.abs(startX - endX);
        distanceY = Math.abs(startY - endY);
        
        distance = (int)Math.round(Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2)));
        return distance;
    }
    
    /**
     * Private method to duplicate all edges in the graph contained in model
     * except edges that concern the super sink.
     * @param model The <code>NetworkFlowModel</code> containing the graph which edges shall be dublicated.
     */
    private static void dublicateEdges(NetworkFlowModel model){
    	Graph graph = model.getGraph();
    	
        Node supersink = model.getSupersink();
        LinkedList<Edge> newEdges = new LinkedList<Edge>();
        IdentifiableIntegerMapping<Edge> transitTimes = model.getTransitTimes();
        IdentifiableIntegerMapping<Edge> edgeCapacities = model.getEdgeCapacities();
        ZToGraphMapping mapping = model.getZToGraphMapping();
        
        int nextEdge = graph.numberOfEdges();
        
    	for (Edge edge : graph.edges()){
    		if ( !(edge.start().equals(supersink)) && !(edge.end().equals(supersink)) ){
    			if (graph.getEdge(edge.end(), edge.start())==null){
    				Edge newEdge = new Edge(nextEdge,edge.end(),edge.start());
    				newEdges.add(newEdge);
    				nextEdge++;
    				mapping.setEdgeLevel(newEdge, mapping.getEdgeLevel(edge).getInverse()); 
    				transitTimes.set(newEdge, transitTimes.get(edge));
    				edgeCapacities.set(newEdge, edgeCapacities.get(edge));
    			}
    		}
    	}
    	
    	for (Edge edge : newEdges){
    		graph.setEdge(edge);
    	}
    }
    
    /**
     * Private method to multiply transit times with up and down speed factors. Necessary for stars.
     * @param model the network flow model containing the data to be adjusted
     */
    private static void multiplyWithUpAndDownSpeedFactors(NetworkFlowModel model){    	
    	Graph graph = model.getGraph();
    	
		ZToGraphMapping mapping = model.getZToGraphMapping();
        IdentifiableIntegerMapping<Edge> transitTimes = model.getTransitTimes();
        Node supersink = model.getSupersink();
    	
    	for (Edge edge : graph.edges()) {
			if (edge.start() != supersink && edge.end() != supersink) {
				double upSpeedFactor = mapping.getUpNodeSpeedFactor(edge
						.start());
				double downSpeedFactor = mapping.getDownNodeSpeedFactor(edge
						.start());
				double oldTransitTime = transitTimes.get(edge);
				Level edgeLevel = mapping.getEdgeLevel(edge);
				if (edgeLevel == HIGHER)
					transitTimes.set(edge, (int) Math.round(oldTransitTime
							/ upSpeedFactor));
				if (edgeLevel == LOWER)
					transitTimes.set(edge, (int) Math.round(oldTransitTime
							/ downSpeedFactor));
			}
		}   	
    }
    
    /**
     * This method calculates the transit times for the converted graphs.
     * The ZToGraphRasterContainer raster supplies one with all necessary rastered rooms, that can be mapped to the proper graphs through
     * the HashMap graphs. Afterwards the calculated transit times are set into the network flow model.
     * The transit times are weighted by the rooms speed factors and rounded to the multiple of the graph precision value.
     * @param raster Supplies a list of all rastered rooms
     * @param graphs Is a HashMap, that maps the separate room raster to the corresponding dynamic networks.
     * @param model A reference to the network flow model to set it's transit times.
     */
    protected static void calculateTransitTimes(ZToGraphRasterContainer raster, NetworkFlowModel model, Hashtable<Edge,ArrayList<ZToGraphRasterSquare>> doorEdgeToSquare) {
        long startTT = System.currentTimeMillis();
        System.out.println("BEGINNE TRANSIT-TIMES");
        IdentifiableIntegerMapping<Edge> transitTimes = new IdentifiableIntegerMapping<Edge>(1);
        
        List<ZToGraphRoomRaster> roomRasterList = raster.getAllRasteredRooms();

        Graph graph = model.getGraph();
        
        IdentifiableCollection<Node> nodes = graph.nodes();                    
        
        // calculate INTRA-Room-Edge-Transit-Times
        long intraStart = System.currentTimeMillis();
        System.out.println("calculate INTRA-Room-Edge-Transit-Times");
        
        // do for all rooms of the roomRasterList
        for (ZToGraphRoomRaster room : roomRasterList){                                                                
            
            List<ZToGraphRasterSquare> roomSquareList = room.getAccessibleSquares();                            
            HashSet<Node> nodeListOfRoom = new HashSet<Node>();                
            for(ZToGraphRasterSquare square : roomSquareList){
                nodeListOfRoom.add(square.getNode());
            }
            
            // calculate the Node -> ZToGraphRasterSquare mapping
            HashMap<Node,LinkedList<ZToGraphRasterSquare>> nodeToSquare = new HashMap<Node,LinkedList<ZToGraphRasterSquare>>();
            for(Node node : nodeListOfRoom){//nodes){
                LinkedList<ZToGraphRasterSquare> nodeSquareList = new LinkedList<ZToGraphRasterSquare>();
                for(ZToGraphRasterSquare square : roomSquareList){
                	if (square.getNode()!=null)
                		if(square.getNode().id() == node.id()) nodeSquareList.add(square);
                }
                nodeToSquare.put(node,nodeSquareList);                
            }
            
            Node supersink = model.getSupersink();            
            
            for(Node start : nodeListOfRoom){//nodes){
                for(Node end : nodeListOfRoom){//nodes){                        
                  // do only, if there is an edge between start and end & if start does not equal end
                  Edge edge = graph.getEdge(start,end);   
                  if (edge != null && edge.id()==400){
                	  System.out.println("debug");
                  }
                  if (edge != null && start != end){
                         if (end.equals(supersink)){
                            transitTimes.set(edge,0);                        
                            continue;
                        }    
                        // add a transitTime-0-entry to the IIMapping for the current edge if there is not yet such an entry
                        if (!transitTimes.isDefinedFor(edge) || transitTimes.get(edge) <= 0) transitTimes.set(edge,0);                                           
                        // write the new transitTime into the IIMapping
                        transitTimes.set(edge,1);                        
                    }                
                } // END of for(end)                        
            } // END of for(start)
        } // END of for(roomRaster)
        // END calculate INTRA-Room-Edge-Transit-Times        
        System.out.println("calculate INTRA-Room-Edge-Transit-Times FERTIG " + (System.currentTimeMillis() - intraStart));
        
        // calculate INTER-Room-Edge-Transit-Times
        long interStart = System.currentTimeMillis();
        System.out.println("calculate INTER-Room-Edge-Transit-Times");
        for (ZToGraphRoomRaster startRoom : roomRasterList){
            for(ZToGraphRoomRaster endRoom : roomRasterList){            	
                                
                // CALCULATE roomNodeMap : ZToGraphRoomRaster -> LinkedList<Node>
                List<ZToGraphRasterSquare> startRoomSquareList = startRoom.getAccessibleSquares();
                List<ZToGraphRasterSquare> endRoomSquareList = endRoom.getAccessibleSquares();                
                HashSet<Node> nodeListOfStartRoom = new HashSet<Node>();
                HashSet<Node> nodeListOfEndRoom = new HashSet<Node>();
                for(ZToGraphRasterSquare square : startRoomSquareList){
                    nodeListOfStartRoom.add(square.getNode());
                }
                for(ZToGraphRasterSquare square : endRoomSquareList){
                    nodeListOfEndRoom.add(square.getNode());
                }                
                
                for(Node nodeA : nodeListOfStartRoom){
                    for(Node nodeB : nodeListOfEndRoom){
                        Edge edge = graph.getEdge(nodeA,nodeB);                                                
                        if (edge != null && graph.contains(edge) && doorEdgeToSquare.get(edge) != null && !doorEdgeToSquare.get(edge).isEmpty()){
                            transitTimes.set(edge, 1);
                        }
                    }
                }                
            }
        }
        // END calculate INTER-Room-Edge-Transit-Times
        System.out.println("calculate INTER-Room-Edge-Transit-Times FERTIG " + (System.currentTimeMillis() - interStart));
        
        // set the calculated transitTime-IIMapping as the transitTimes of the NFM
        model.setTransitTimes(transitTimes);        
        System.out.println("TRANSIT-TIMES-FERTIG " + (System.currentTimeMillis() - startTT));
        
    }

    protected static Hashtable<Edge,ArrayList<ZToGraphRasterSquare>>  connectRooms(ZToGraphRasterContainer raster, NetworkFlowModel model)
    {
        System.out.println("connect Rooms");
    	ZToGraphMapping mapping = model.getZToGraphMapping();

        Hashtable<Edge,ArrayList<ZToGraphRasterSquare>>  table =
                new Hashtable<Edge,ArrayList<ZToGraphRasterSquare>>();
        
        
        //Two mappings to store capacities
        //just temporally. 
        Collection<ZToGraphRasteredDoor> doors =  raster.getDoors();
        DynamicNetwork graph = model.getDynamicNetwork();
        IdentifiableIntegerMapping<Edge> edgesCap = model.getEdgeCapacities();// new IdentifiableIntegerMapping<Edge>(graph.numberOfEdges());
        if(edgesCap == null)
        {
            edgesCap = new IdentifiableIntegerMapping<Edge>(graph.numberOfEdges());
            model.setEdgeCapacities(edgesCap);
        }             

        int nextEdge = graph.numberOfEdges();
        for(ZToGraphRasteredDoor door : doors)
        {
            Node firstNode = door.getFirstDoorPart().getNode(); 
            Node secondNode = door.getSecondDoorPart().getNode(); 
            
            Edge edge = graph.getEdge(firstNode, secondNode);
            if(edge == null)
            {
                edge = new Edge(nextEdge++,firstNode,secondNode);
                graph.addEdge(edge);
                edgesCap.setDomainSize(edgesCap.getDomainSize()+1);
                edgesCap.set(edge,0);
                mapping.setEdgeLevel(edge,EQUAL);
                
            }
            edgesCap.increase(edge, 1 * FACTOR);
            //store squares in the squares list of the door-edge
            ArrayList<ZToGraphRasterSquare> list = table.get(edge);
            if (list == null)
            {
                list = new ArrayList<ZToGraphRasterSquare>();
                table.put(edge, list);
            }
            
            
            //retrieve the squares to store them in the list if they are not already in it
            ZToGraphRasterSquare square = door.getFirstDoorPart();
            if(!list.contains(square))
                list.add(square);
             square = door.getSecondDoorPart();
            if(!list.contains(square))
                list.add(square);
            
        }//end for each door loop
        
        //Connect the super source withh all other sources
        Node supersink = model.getSupersink();
        
        
        if(supersink == null)
            return table;
        
        List<ZToGraphRoomRaster> rasteredRooms = raster
				.getAllRasteredRooms();
        for (ZToGraphRoomRaster room : rasteredRooms)
        {            

            int colCount = room.getColumnCount();
            int rowCount = room.getRowCount();

            for(int row = 0 ; row < rowCount; row++)
            {
                for(int col = 0 ; col < colCount; col++)
                {
                    ZToGraphRasterSquare square = room.getSquare(col,row);
                    
                    if(square.getSave())
                    {
                        Node node = square.getNode();
                        Edge edge = graph.getEdge( node, supersink);
                        if(edge == null)
                        {
                            edge = new Edge(nextEdge++, node, supersink);
                            graph.addEdge(edge);
                            mapping.setEdgeLevel(edge,EQUAL);
                        }
                        edgesCap.set(edge, Integer.MAX_VALUE);
                    }// end if safe 
                }//end inner loop
            }//end outer loop
        }
        System.out.println("connect Rooms FERTIG");
        return table;
    }//end of function 
    
    
    
}
