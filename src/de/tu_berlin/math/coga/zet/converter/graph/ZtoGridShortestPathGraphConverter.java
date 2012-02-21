/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;


import algo.graph.shortestpath.DijkstraWithRationalDistances;
import de.tu_berlin.math.coga.common.util.Level;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import ds.graph.network.DynamicNetwork;
import ds.graph.Edge;
import ds.graph.Forest;
import ds.graph.IdentifiableCollection;
import ds.collection.ListSequence;
import ds.graph.MinSpanningTree;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import ds.graph.problem.MinSpanningTreeProblem;
import ds.z.BuildingPlan;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author schwengf
 */
public class ZtoGridShortestPathGraphConverter extends ZToGridGraphConverter{
    
    public NetworkFlowModel minspanmodel;
    public MinSpanningTreeProblem minspanprob;
    public MinSpanningTree spantree;
    private Map<Edge,Double> costs;
    public IdentifiableCollection<Edge> ForestEdges;
    public Forest forest;
    public Edge neu;
    public Edge neureverse;
    public Edge neu2;
    public int NumEdges = 0;
    IdentifiableCollection<Edge> solEdges = new ListSequence<Edge>();
    
    @Override
    protected NetworkFlowModel runAlgorithm( BuildingPlan problem ) {
		mapping = new ZToGraphMapping();
                ZToGraphMapping newmapping = new ZToGraphMapping();
		model = new NetworkFlowModel();
                minspanmodel = new NetworkFlowModel();
                
		raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer( problem );
		mapping.setRaster( raster );
		model.setZToGraphMapping( mapping );
                
		super.createNodes();
		super.createEdgesAndCapacities();
		super.computeTransitTimes();
		super.multiplyWithUpAndDownSpeedFactors();
		model.setTransitTimes( exactTransitTimes.round() );
		
		createReverseEdges( model );
        	//model.setNetwork( model.getGraph().getAsStaticNetwork() );
     
                //nodes are nodes of original graph
                DynamicNetwork newgraph = new DynamicNetwork();
                newgraph.setNodes(model.getGraph().nodes());
                minspanmodel.setNetwork(newgraph);
                
                
                minspanmodel.setSupersink(model.getSupersink());
                Node Super = minspanmodel.getSupersink();
                newmapping.setNodeSpeedFactor( Super, 1 );
		newmapping.setNodeRectangle( Super, new NodeRectangle( 0, 0, 0, 0 ) );
		newmapping.setFloorForNode( Super, -1 );
                 
                for (Node node: model.getGraph().nodes())
                {
                    if (node.id()!= 0)
                    {
                        minspanmodel.setNodeCapacity(node, model.getNodeCapacity(node));
                        newmapping.setNodeSpeedFactor(node, mapping.getNodeSpeedFactor(node));
                        newmapping.setNodeUpSpeedFactor(node, mapping.getUpNodeSpeedFactor(node));
                        newmapping.setNodeDownSpeedFactor(node, mapping.getDownNodeSpeedFactor(node));   
                    }
                }
                
                costs = new HashMap<Edge,Double>(model.getGraph().numberOfEdges());
                for (Edge edge: model.getGraph().edges())
                {
                    costs.put(edge, (double)model.getTransitTime(edge));
                }
                DynamicNetwork net =  model.getDynamicNetwork();
                DijkstraWithRationalDistances dijkstra = new DijkstraWithRationalDistances(net, costs, model.getSupersink());
                dijkstra.run();
                DynamicNetwork netw = dijkstra.getShortestPathGraph();
                
                for (Edge edge: netw.edges() )
                {
                    Edge create = new Edge(NumEdges++, edge.start(), edge.end());
                    solEdges.add(create);
                }

                for (Edge sinkedge: model.getGraph().incidentEdges(model.getSupersink()))
                {
                    if (sinkedge.start() == model.getSupersink())
                    {
                        Edge add = new Edge(NumEdges++, Super, sinkedge.end());
                        Edge add2 = new Edge(NumEdges++, sinkedge.end(), Super);
                        solEdges.add(add);
                        solEdges.add(add2);
                    }
                    else
                    {
                        Edge add = new Edge(NumEdges++, Super, sinkedge.start());
                        Edge add2 = new Edge(NumEdges++, sinkedge.start(), Super);                       
                        solEdges.add(add);
                        solEdges.add(add2);
                    }
                }
                
                for (Edge create: solEdges)
                {
                    //System.out.println("Kante: " + create);
                    newgraph.addEdge(create);
                    if (create.start() == Super || create.end() == Super)
                    {
                        minspanmodel.setEdgeCapacity(create, Integer.MAX_VALUE);
                        minspanmodel.setTransitTime(create, 0);
                        minspanmodel.setExactTransitTime(create, 0);
                        newmapping.setEdgeLevel(create,Level.Higher);  
                    }
                    else
                    {    
                        minspanmodel.setEdgeCapacity(create, model.getEdgeCapacity(create));
                        minspanmodel.setTransitTime(create, model.getTransitTime(create));
                        minspanmodel.setExactTransitTime(create, model.getExactTransitTime(create));
                        newmapping.setEdgeLevel(create,mapping.getEdgeLevel(create) );  
                    }
                               
                }
                
                model.setNetwork( model.getGraph().getAsStaticNetwork() );
                minspanmodel.setCurrentAssignment(model.getCurrentAssignment());
                minspanmodel.setSources(model.getSources());
                 
                 //values from mapping of original graph
                 newmapping.raster = mapping.getRaster();
                 newmapping.nodeRectangles = mapping.getNodeRectangles();
                 newmapping.nodeFloorMapping = mapping.getNodeFloorMapping();
                 newmapping.isEvacuationNode = mapping.isEvacuationNode;
                 newmapping.isSourceNode = mapping.isSourceNode;
                 newmapping.isDeletedSourceNode = mapping.isDeletedSourceNode;
                 newmapping.exitName = mapping.exitName;
                 
                 minspanmodel.setZToGraphMapping(newmapping);                
                 minspanmodel.setSupersink(model.getSupersink());
                //createReverseEdges( minspanmodel );
                minspanmodel.setNetwork(newgraph);
                minspanmodel.setNetwork( minspanmodel.getGraph().getAsStaticNetwork());
                System.out.println("Number of Created Shortest Path Tree Edges: " + minspanmodel.getGraph().numberOfEdges());
		return minspanmodel;
                
                
    }
    
}
