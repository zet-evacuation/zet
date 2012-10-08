/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.reduction.GreedyAlgo;
import algo.graph.reduction.PrimsAlgo;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.*;
import ds.graph.problem.MinSpanningTreeProblem;

/**
 *
 * @author schwengf
 */
//creates a Minimum Spanning tree for Non Grid Graphs
public class SpanningTreeShrinker extends Algorithm<NetworkFlowModel,NetworkFlowModel> {
    
    public NetworkFlowModel minspanmodel;
    public MinSpanningTreeProblem minspanprob;
    public PrimsAlgo primalgo;
    public GreedyAlgo greedy;
    public MinSpanningTree minspantree;
    
    @Override
    protected NetworkFlowModel runAlgorithm( NetworkFlowModel problem ) {
			ZToGraphMapping originalMapping = problem.getZToGraphMapping();
			ZToGraphMapping newMapping = new ZToGraphMapping();
                
		newMapping.setRaster( problem.getZToGraphMapping().getRaster() );
		//model.setZToGraphMapping( originalMapping );

                
		//super.createNodes();
		//super.createEdgesAndCapacities();
		//super.computeTransitTimes();
		//super.multiplyWithUpAndDownSpeedFactors();
		//model.setTransitTimes( exactTransitTimes.round() );
		//model.roundTransitTimes();
		
		//createReverseEdges( model );
		
		
    //    	model.setNetwork( model.getGraph().getAsStaticNetwork() );
                System.out.println("number of edges of original graph:" + problem.numberOfEdges());
                // nodes are nodes of original network
                minspanmodel = new NetworkFlowModel( problem );
                //DynamicNetwork newgraph = new DynamicNetwork( model );
                //minspanmodel.setNetwork(newgraph);
                //newgraph.setNodes(model.getGraph().nodes());
       
                //minspanmodel.setSupersink(model.getSupersink());
                Node Super = minspanmodel.getSupersink();
                newMapping.setNodeSpeedFactor( Super, 1 );
		newMapping.setNodeRectangle( Super, new NodeRectangle( 0, 0, 0, 0 ) );
		newMapping.setFloorForNode( Super, -1 );
                
                
                
                for (Node node: problem )
                {
                    if (node.id()!= 0)
                    {
                        minspanmodel.setNodeCapacity(node, problem.getNodeCapacity(node));
                        //newmapping.setNodeShape(node, originalMapping.getNodeShape(node));
                        newMapping.setNodeSpeedFactor(node, originalMapping.getNodeSpeedFactor(node));
                        newMapping.setNodeUpSpeedFactor(node, originalMapping.getUpNodeSpeedFactor(node));
                        newMapping.setNodeDownSpeedFactor(node, originalMapping.getDownNodeSpeedFactor(node));   
                    }
                }
              
                //creates a minimum spanning tree problem   
                minspanprob = new MinSpanningTreeProblem(problem,problem.transitTimes());
              
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
									minspanmodel.addEdge( neu, problem.getEdgeCapacity( neu), problem.getTransitTime( neu), problem.getExactTransitTime( neu ) );
//                    newgraph.addEdge(neu);
//                    minspanmodel.setEdgeCapacity(neu, model.getEdgeCapacity(neu));
//                    minspanmodel.setTransitTime(neu, model.getTransitTime(neu));
                    newMapping.setEdgeLevel(neu, originalMapping.getEdgeLevel(neu));                
//                    minspanmodel.setExactTransitTime(neu, model.getExactTransitTime(neu));
                }
                
                 //minspanmodel.setCurrentAssignment(model.getCurrentAssignment());
//                 minspanmodel.setSources(model.getSources());
                 
                 //values from originalMapping of original network 
                 newMapping.raster = originalMapping.getRaster();
                 newMapping.nodeRectangles = originalMapping.getNodeRectangles();
                 newMapping.nodeFloorMapping = originalMapping.getNodeFloorMapping();
                 newMapping.isEvacuationNode = originalMapping.isEvacuationNode;
                 //newMapping.isSourceNode = originalMapping.isSourceNode;
                 newMapping.isDeletedSourceNode = originalMapping.isDeletedSourceNode;
                 newMapping.exitName = originalMapping.exitName;
                 
                minspanmodel.setZToGraphMapping(newMapping);                
                //minspanmodel.setSupersink(model.getSupersink());
                BaseZToGraphConverter.createReverseEdges( minspanmodel );
                //minspanmodel.setNetwork(newgraph);
                //minspanmodel.setNetwork( minspanmodel.getGraph().getAsStaticNetwork());
                System.out.println("Edges used in Minimum Spanning Tree: " + minspanmodel.numberOfEdges());
                System.out.println("Edge capacities: " + problem.edgeCapacities());
								minspanmodel.resetAssignment();
								System.out.println( minspanmodel.graph().toString() );
		return minspanmodel;
               
	}
    
    
}
