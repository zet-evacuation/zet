/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.reduction.GreedyAlgo;
import algo.graph.reduction.PrimsAlgo;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import ds.graph.MinSpanningTree;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import ds.graph.problem.MinSpanningTreeProblem;
import ds.z.BuildingPlan;
/**
 *
 * @author schwengf
 */
//creates a Minimum Spanning tree for Non Grid Graphs
public class ZToSpanTreeConverter extends ZToNonGridGraphConverter{
    
    public NetworkFlowModel minspanmodel;
    public MinSpanningTreeProblem minspanprob;
    public PrimsAlgo primalgo;
    public GreedyAlgo greedy;
    public MinSpanningTree minspantree;
    
    @Override
    protected NetworkFlowModel runAlgorithm( BuildingPlan problem ) {
		mapping = new ZToGraphMapping();
                ZToGraphMapping newmapping = new ZToGraphMapping();
		model = new NetworkFlowModel();
                
		raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer( problem );
		mapping.setRaster( raster );
		model.setZToGraphMapping( mapping );

                
		super.createNodes();
		super.createEdgesAndCapacities();
		super.computeTransitTimes();
		super.multiplyWithUpAndDownSpeedFactors();
		//model.setTransitTimes( exactTransitTimes.round() );
		model.roundTransitTimes();
		
		createReverseEdges( model );
    //    	model.setNetwork( model.getGraph().getAsStaticNetwork() );
                System.out.println("number of edges of original graph:" + model.numberOfEdges());
                // nodes are nodes of original network
                minspanmodel = new NetworkFlowModel( model );
                //DynamicNetwork newgraph = new DynamicNetwork( model );
                //minspanmodel.setNetwork(newgraph);
                //newgraph.setNodes(model.getGraph().nodes());
       
                //minspanmodel.setSupersink(model.getSupersink());
                Node Super = minspanmodel.getSupersink();
                newmapping.setNodeSpeedFactor( Super, 1 );
		newmapping.setNodeRectangle( Super, new NodeRectangle( 0, 0, 0, 0 ) );
		newmapping.setFloorForNode( Super, -1 );
                
                
                
                for (Node node: model )
                {
                    if (node.id()!= 0)
                    {
                        minspanmodel.setNodeCapacity(node, model.getNodeCapacity(node));
                        //newmapping.setNodeShape(node, mapping.getNodeShape(node));
                        newmapping.setNodeSpeedFactor(node, mapping.getNodeSpeedFactor(node));
                        newmapping.setNodeUpSpeedFactor(node, mapping.getUpNodeSpeedFactor(node));
                        newmapping.setNodeDownSpeedFactor(node, mapping.getDownNodeSpeedFactor(node));   
                    }
                }
              
                //creates a minimum spanning tree problem   
                minspanprob = new MinSpanningTreeProblem(model,model.transitTimes());
              
                //using Prims algorithm:
                primalgo = new PrimsAlgo();
                primalgo.setProblem( minspanprob );
                System.out.print("Compute minimum spanning tree using Prim... " );
		primalgo.run();
                System.out.println("used time: " + primalgo.getRuntimeAsString() );
		minspantree = primalgo.getSolution();
                IdentifiableCollection<Edge> MinEdges = minspantree.getEdges();
              
                
                for (Edge neu: MinEdges)
                {
									minspanmodel.addEdge( neu, model.getEdgeCapacity( neu), model.getTransitTime( neu), model.getExactTransitTime( neu ) );
//                    newgraph.addEdge(neu);
//                    minspanmodel.setEdgeCapacity(neu, model.getEdgeCapacity(neu));
//                    minspanmodel.setTransitTime(neu, model.getTransitTime(neu));
                    newmapping.setEdgeLevel(neu, mapping.getEdgeLevel(neu));                
//                    minspanmodel.setExactTransitTime(neu, model.getExactTransitTime(neu));
                }
                
                 //minspanmodel.setCurrentAssignment(model.getCurrentAssignment());
//                 minspanmodel.setSources(model.getSources());
                 
                 //values from mapping of original network 
                 newmapping.raster = mapping.getRaster();
                 newmapping.nodeRectangles = mapping.getNodeRectangles();
                 newmapping.nodeFloorMapping = mapping.getNodeFloorMapping();
                 newmapping.isEvacuationNode = mapping.isEvacuationNode;
                 newmapping.isSourceNode = mapping.isSourceNode;
                 newmapping.isDeletedSourceNode = mapping.isDeletedSourceNode;
                 newmapping.exitName = mapping.exitName;
                 
                minspanmodel.setZToGraphMapping(newmapping);                
                //minspanmodel.setSupersink(model.getSupersink());
                createReverseEdges( minspanmodel );
                //minspanmodel.setNetwork(newgraph);
                //minspanmodel.setNetwork( minspanmodel.getGraph().getAsStaticNetwork());
                System.out.println("Edges used in Minimum Spanning Tree: " + minspanmodel.numberOfEdges());
                System.out.println("Edge capacities: " + model.edgeCapacities());
								minspanmodel.resetAssignment();
								System.out.println( minspanmodel.graph().toString() );
		return minspanmodel;
               
	}
    
    
}
