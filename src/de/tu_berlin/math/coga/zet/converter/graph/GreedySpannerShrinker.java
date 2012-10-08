/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import ds.z.BuildingPlan;
import ds.graph.problem.MinSpanningTreeProblem;
import algo.graph.reduction.PrimsAlgo;
import ds.graph.MinSpanningTree;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.IdentifiableCollection;
import ds.graph.NodeRectangle;
import algo.graph.reduction.GreedyAlgo;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.common.util.Level;
import ds.mapping.IdentifiableIntegerMapping;

/**
 *
 * @author schwengf
 */
//creates a t-spanner for grid graphs using a greedy algorithm 
public class GreedySpannerShrinker extends Algorithm<NetworkFlowModel,NetworkFlowModel> {
    public NetworkFlowModel minspanmodel;
    public MinSpanningTreeProblem minspanprob;
    public PrimsAlgo primalgo;
    public GreedyAlgo greedy;
    public MinSpanningTree minspantree;
    
    @Override
    protected NetworkFlowModel runAlgorithm( NetworkFlowModel problem ) {
			ZToGraphMapping originalMapping = problem.getZToGraphMapping();
//		mapping = new ZToGraphMapping();
//                ZToGraphMapping newmapping = new ZToGraphMapping();
//		model = new NetworkFlowModel();
//                
//		raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer( problem );
//		mapping.setRaster( raster );
//		model.setZToGraphMapping( mapping );
//
//                
//		super.createNodes();
//		super.createEdgesAndCapacities();
//		super.computeTransitTimes();
//		super.multiplyWithUpAndDownSpeedFactors();
//		//model.setTransitTimes( exactTransitTimes.round() );
//		model.roundTransitTimes();
//                //model.setExactTransitTimes(exactTransitTimes);
//		createReverseEdges( model );
        	//model.setNetwork( model.getGraph().getAsStaticNetwork() );
                /*for (Edge e: model.getNetworkFlowModel().edges())
                {
                    System.out.println("original edge: " + e);
                }*/
                System.out.println("number of edges in original graph:" + problem.numberOfEdges());
                //Knoten stimmen bei Original und beim MinSpanModel ueberein
                //minspanmodel.setNetwork(newgraph);
                //newgraph.setNodes(model.getGraph().nodes());
                //DynamicNetwork newgraph = new DynamicNetwork( model );
                minspanmodel = new NetworkFlowModel( problem );
                
                //minspanmodel.setSupersink(model.getSupersink());
                Node Super = minspanmodel.getSupersink();
			ZToGraphMapping newMapping = minspanmodel.getZToGraphMapping();
                
                newMapping.setNodeSpeedFactor( Super, 1 );
		newMapping.setNodeRectangle( Super, new NodeRectangle( 0, 0, 0, 0 ) );
		newMapping.setFloorForNode( Super, -1 );
                
                
                for (Node node: problem )
                {
                    if (node.id()!= 0)
                    {
                        minspanmodel.setNodeCapacity(node, problem.getNodeCapacity(node));
                        //newmapping.setNodeShape(node, mapping.getNodeShape(node));
                        newMapping.setNodeSpeedFactor(node, originalMapping.getNodeSpeedFactor(node));
                        newMapping.setNodeUpSpeedFactor(node, originalMapping.getUpNodeSpeedFactor(node));
                        newMapping.setNodeDownSpeedFactor(node, originalMapping.getDownNodeSpeedFactor(node));   
                    }
                }

                //creates a minimum spanning tree problem
                minspanprob = new MinSpanningTreeProblem(problem,problem.transitTimes());
              
                //creates a t-spanner using a greedy algorithm
                greedy = new GreedyAlgo();
                greedy.setProblem(minspanprob);
                greedy.run();
                minspantree = greedy.getSolution();
                System.out.print("Compute t-Spanner using greedy... " );
                System.out.println("used time: " + greedy.getRuntimeAsString() );
                IdentifiableCollection<Edge> MinEdges = minspantree.getEdges();
                IdentifiableIntegerMapping<Edge> transit = minspantree.getTransit();
                IdentifiableIntegerMapping<Edge> cap = minspantree.getCapac();
                
                for (Edge neu: MinEdges)
                {
                    if (neu.start() != problem.getSupersink() && neu.end()!= problem.getSupersink())
                    {
											minspanmodel.addEdge( neu, cap.get( neu), transit.get( neu), problem.getExactTransitTime( neu) );
//                        newgraph.addEdge(neu);
//                        minspanmodel.setEdgeCapacity(neu, cap.get(neu));
//                        minspanmodel.setTransitTime(neu, transit.get(neu));
                        newMapping.setEdgeLevel(neu, originalMapping.getEdgeLevel(neu));             
//                        minspanmodel.setExactTransitTime(neu, model.getExactTransitTime(neu));
                    }
                    else
                    {
											minspanmodel.addEdge( neu, Integer.MAX_VALUE, 0, 0);
//                        newgraph.addEdge(neu);
//                        minspanmodel.setEdgeCapacity(neu, Integer.MAX_VALUE);
//                        minspanmodel.setTransitTime(neu, 0);
                        newMapping.setEdgeLevel(neu, Level.Equal);
//                        minspanmodel.setExactTransitTime(neu, 0);
                    }
                }
                
                 //minspanmodel.setCurrentAssignment(model.getCurrentAssignment());
                 //minspanmodel.setSources(model.getSources());
                 
                 //values from mapping of original graph
                 newMapping.raster = originalMapping.getRaster();
                 newMapping.nodeRectangles = originalMapping.getNodeRectangles();
                 newMapping.nodeFloorMapping = originalMapping.getNodeFloorMapping();
                 //newMapping.isEvacuationNode = originalMapping.isEvacuationNode;
                 //newMapping.isSourceNode = originalMapping.isSourceNode;
                 newMapping.isDeletedSourceNode = originalMapping.isDeletedSourceNode;
                 newMapping.exitName = originalMapping.exitName;
                 

                 //minspanmodel.setSupersink(model.getSupersink());
                BaseZToGraphConverter.createReverseEdges( minspanmodel );
                //minspanmodel.setNetwork(newgraph);
                //minspanmodel.setNetwork( minspanmodel.getGraph().getAsStaticNetwork());
                /*for (Edge e: minspanmodel.getNetworkFlowModel().edges())
                {
                    System.out.println("Kante im Spanner: " + e);
                }*/
                System.out.println("number of edges in t-spanner: " + minspanmodel.numberOfEdges());
								minspanmodel.resetAssignment();
		return minspanmodel;
               
	}
    
}
