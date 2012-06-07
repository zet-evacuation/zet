/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.reduction.*;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import ds.graph.network.DynamicNetwork;
import ds.graph.Edge;
import ds.graph.Forest;
import ds.graph.IdentifiableCollection;
import ds.mapping.IdentifiableIntegerMapping;
import ds.collection.ListSequence;
import ds.graph.MinSpanningTree;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import ds.graph.problem.MinSpanningTreeProblem;
import ds.z.BuildingPlan;
import java.util.*;

/**
 *
 * @author schwengf
 */
public class ZToNonGridShortestPathsConverter  extends ZToNonGridGraphConverter{
    
    public NetworkFlowModel minspanmodel;
    public MinSpanningTreeProblem minspanprob;
    public MinSpanningTree spantree;
    public IdentifiableIntegerMapping TransitForEdge;
    public IdentifiableIntegerMapping currentTransitForEdge;
    IdentifiableIntegerMapping<Edge> currentTransitForEdge2;
    public IdentifiableCollection<Edge> ForestEdges;
    public Forest forest;
    public Edge neu;
    public Edge neureverse;
    public Edge neu2;
    public int NumEdges = 0;
    public int NumNodes = 0;
    public int NumCurrentEdges = 0;
    public int NumShortestPaths = 5;
    IdentifiableCollection<Edge> solEdges = new ListSequence<>();
    IdentifiableCollection<Node> solNodes = new ListSequence<>();
    private ListSequence<Edge> currentEdges = new ListSequence<>();
    private ListSequence<Edge> res; 
    
    public Map<Node,Node> newNodeMap= new HashMap<>();
    
    @Override
    protected NetworkFlowModel runAlgorithm( BuildingPlan problem ) {
		mapping = new ZToGraphMapping();
                ZToGraphMapping newmapping = new ZToGraphMapping();
		model = new NetworkFlowModel();
                minspanmodel = new NetworkFlowModel();
                List<Edge> super_edges = new ListSequence<>();
                
		raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer( problem );
		mapping.setRaster( raster );
		model.setZToGraphMapping( mapping );

                DynamicNetwork newgraph = new DynamicNetwork();
                
		super.createNodes();
		super.createEdgesAndCapacities();
		super.computeTransitTimes();
		super.multiplyWithUpAndDownSpeedFactors();
		model.setTransitTimes( exactTransitTimes.round() );
		createReverseEdges( model );
                
                for (Edge e: model.getGraph().edges()){
                    if (e.isIncidentTo(model.getSupersink())){
                        super_edges.add(e); 
                    }
                }
        	                
                Node Super = model.getSupersink();
                newgraph.addNode(Super);
                NumNodes++;
                minspanmodel.setSupersink(Super);
                newmapping.setNodeSpeedFactor( Super, 1 );
		newmapping.setNodeRectangle( Super, new NodeRectangle( 0, 0, 0, 0 ) );
		newmapping.setFloorForNode( Super, -1 );
                
                model.setNetwork( model.getGraph().getAsStaticNetwork() );
                
                YenKShortestPaths yen = new YenKShortestPaths(model);
                Node exit=null;
                List<ZToGraphRoomRaster> rasteredRooms = raster.getAllRasteredRooms();
		for( ZToGraphRoomRaster room : rasteredRooms ) {

			int colCount = room.getColumnCount();
			int rowCount = room.getRowCount();

			for( int row = 0; row < rowCount; row++ )
				for( int col = 0; col < colCount; col++ ) {
					ZToGraphRasterSquare square = room.getSquare( col, row );

					// todo: parameter
					if( square.isSave() && square.isExit()) {
						exit = square.getNode();       
					}// end if safe
				}
                }
                
                int[][] used = new int[model.getGraph().numberOfNodes()][model.getGraph().numberOfNodes()]; 
                for (int i=0; i< model.getGraph().numberOfNodes();i++){
                    for (int j=0; j<model.getGraph().numberOfNodes();j++){
                        used[i][j] = 0;
                    }
                } 
                
                Collection <YenPath> found = new LinkedList<>();
                for (Node source: model.getSources())
                {
                    found.addAll(yen.get_shortest_paths(source, model.getSupersink(),8));
                }
                for (YenPath y: found)
                {
                    //System.out.println("Pfad: " + y.toString());
                    for (int i=0; i<y.get_vertices().size()-1 ; i++)
                    { 
                        if (used[y.get_vertices().get(i).id()][y.get_vertices().get(i+1).id()] ==0 && y.get_vertices().get(i).id()!=0 && y.get_vertices().get(i+1).id()!=0)
                        {
                            Edge n = model.getGraph().getEdge(y.get_vertices().get(i),y.get_vertices().get(i+1));
                            //Edge e = new Edge(NumEdges++,y.get_vertices().get(i),y.get_vertices().get(i+1));
                            solEdges.add(n);
                            used[y.get_vertices().get(i).id()][y.get_vertices().get(i+1).id()] =1;
                            used[y.get_vertices().get(i+1).id()][y.get_vertices().get(i).id()] =1;
                            if (!solNodes.contains(y.get_vertices().get(i))){                                
                            solNodes.add(y.get_vertices().get(i));
                            }
                            if (!solNodes.contains(y.get_vertices().get(i+1))){  
                            solNodes.add(y.get_vertices().get(i+1));
                            }
                        }    
                    }
                }

                for (Node node: solNodes)
                { 
                    Node new_node = new Node(NumNodes++);                   
                    newgraph.addNode(new_node); 
                    newNodeMap.put(node,new_node);
                    //System.out.println("new Node: " + new_node + "for old: " + node);
                    if (node.id()!= 0)
                    {
                        newmapping.setNodeRectangle(new_node, mapping.getNodeRectangles().get(node));
                        newmapping.setFloorForNode(new_node, model.getZToGraphMapping().getNodeFloorMapping().get(node));
                        newmapping.setIsEvacuationNode( new_node,model.getZToGraphMapping().getIsEvacuationNode(node));
                        newmapping.setIsSourceNode(new_node, model.getZToGraphMapping().getIsSourceNode(node));
                        newmapping.setIsDeletedSourceNode( new_node, model.getZToGraphMapping().getIsDeletedSourceNode(node) );
                        minspanmodel.setNodeCapacity(new_node, model.getNodeCapacity(node));
                        newmapping.setNodeSpeedFactor(new_node, mapping.getNodeSpeedFactor(node));
                        newmapping.setNodeUpSpeedFactor(new_node, mapping.getUpNodeSpeedFactor(node));
                        newmapping.setNodeDownSpeedFactor(new_node, mapping.getDownNodeSpeedFactor(node));   
                    }
                }
                
                for (Edge edge: solEdges)
                {                
                        Edge orig = model.getGraph().getEdge(edge.start(), edge.end());
                        Edge new_edge = new Edge(NumEdges++,newNodeMap.get(edge.start()),newNodeMap.get(edge.end()));
                        //System.out.println("neue Kante: " + new_edge + "for: " + orig);                 
                        newgraph.addEdge(new_edge);
                        minspanmodel.setEdgeCapacity(new_edge, model.getEdgeCapacity(orig));
                        minspanmodel.setTransitTime(new_edge, model.getTransitTime(orig));
                        newmapping.setEdgeLevel(new_edge,mapping.getEdgeLevel(orig) );             
                        minspanmodel.setExactTransitTime(new_edge, model.getExactTransitTime(orig));
                }
                
                for (Edge e: super_edges){
                    if (newNodeMap.containsKey(e.start()))
                    {
                        Edge new_edge = new Edge(NumEdges++,newNodeMap.get(e.start()),minspanmodel.getSupersink());
                        //System.out.println("superEdge: " + new_edge + "for: " + e);
                        newgraph.addEdge(new_edge);                   
                        Edge orig = model.getGraph().getEdge(e.start(), e.end());
                        minspanmodel.setTransitTime(new_edge, model.getTransitTime(orig));
                        minspanmodel.setEdgeCapacity(new_edge, Integer.MAX_VALUE);
                        minspanmodel.setExactTransitTime(new_edge, model.getExactTransitTime(orig));
                        newmapping.setEdgeLevel(new_edge, mapping.getEdgeLevel(orig));
                    }
                }
                
                newmapping.raster = mapping.getRaster();
                for( ZToGraphRoomRaster room : rasteredRooms ) 
                {
                    int numCol = room.getColumnCount();
                    int numRow = room.getRowCount();
                    for (int i=0; i<numCol; i++)
                    {
                        for (int j=0; j<numRow; j++)
                        {
                            ZToGraphRasterSquare square = room.getSquare( i, j );
                            square.mark();
                            Node old = square.getNode();
                            square.setNode(newNodeMap.get(old));
                        }
                    }
                }
                
                
                
                 //minspanmodel.setCurrentAssignment(model.getCurrentAssignment());
                 //minspanmodel.setSources(model.getSources());
                 minspanmodel.setNetwork(newgraph);
                 //values from mapping of original graph                
                 newmapping.exitName = mapping.exitName;
                 
                minspanmodel.setZToGraphMapping(newmapping); 
                
                /*for (Node n: minspanmodel.getGraph().nodes())
                {
                    System.out.println("Nodes: " + n);
                }
                for (Edge e: minspanmodel.getGraph().edges())
                {
                    System.out.println("Kante: " + e + "Cap: " + minspanmodel.getEdgeCapacity(e) + "Tran: " + minspanmodel.getTransitTime(e));
                }*/
                createReverseEdges( minspanmodel );
                minspanmodel.setNetwork(newgraph);               
                minspanmodel.setNetwork( minspanmodel.getGraph().getAsStaticNetwork());
                //System.out.println("Number of Created Repeated Shortest Paths Edges: " + minspanmodel.getGraph().numberOfEdges());
		return minspanmodel;
                
                
    }
    
    
    
    
    
}
