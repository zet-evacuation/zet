/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.shortestpath.APSPAlgo;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.collection.ListSequence;
import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import ds.graph.Node;
import ds.graph.NodeRectangle;
/**
 *
 * @author schwengf
 */
public class APSPGraphShrinker extends Algorithm<NetworkFlowModel,NetworkFlowModel> {
 
    
    public NetworkFlowModel minspanmodel;
    public APSPAlgo apspalgo;
    int numEdges=0;


    @Override
    protected NetworkFlowModel runAlgorithm( NetworkFlowModel problem ) {
			ZToGraphMapping originalMapping = problem.getZToGraphMapping();
			ZToGraphMapping newMapping = new ZToGraphMapping();
                
		newMapping.setRaster( problem.getZToGraphMapping().getRaster() );
				
//		mapping = new ZToGraphMapping();
//                ZToGraphMapping newmapping = new ZToGraphMapping();
//		model = new NetworkFlowModel();
//                
//		raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer( problem );
//		mapping.setRaster( raster );
//		model.setZToGraphMapping( mapping );
//
//                //DynamicNetwork newgraph = new DynamicNetwork();
//                
//		super.createNodes();
//		super.createEdgesAndCapacities();
//		super.computeTransitTimes();
//		super.multiplyWithUpAndDownSpeedFactors();
//		//model.setTransitTimes( exactTransitTimes.round() );
//		model.roundTransitTimes();
//		
//		createReverseEdges( model );
        	//model.setNetwork( model.getGraph().getAsStaticNetwork() );
                System.out.println("number of edges of original graph:" + problem .numberOfEdges());
                System.out.println("Number of Nodes: " + problem .numberOfNodes());
                // nodes are nodes of original network
                //minspanmodel.setNetwork(newgraph);
                //newgraph.setNodes(model.getGraph().nodes());
                minspanmodel = new NetworkFlowModel( problem  );

       
                //minspanmodel.setSupersink(model.getSupersink());
                Node Super = minspanmodel.getSupersink();
                newMapping.setNodeSpeedFactor( Super, 1 );
		newMapping.setNodeRectangle( Super, new NodeRectangle( 0, 0, 0, 0 ) );
		newMapping.setFloorForNode( Super, -1 );
                
                
                
                for (Node node: problem  )
                {
                    if (node.id()!= 0)
                    {
                        minspanmodel.setNodeCapacity(node, problem .getNodeCapacity(node));
                        //newmapping.setNodeShape(node, mapping.getNodeShape(node));
                        newMapping.setNodeSpeedFactor(node, originalMapping.getNodeSpeedFactor(node));
                        newMapping.setNodeUpSpeedFactor(node, originalMapping.getUpNodeSpeedFactor(node));
                        newMapping.setNodeDownSpeedFactor(node, originalMapping.getDownNodeSpeedFactor(node));   
                    }
                }
                
                //using APSP algorithm:
                apspalgo = new APSPAlgo(problem );
                int[][] succ = apspalgo.run();
                int numNodes = problem .numberOfNodes() -1;
                
                IdentifiableCollection<Edge> solEdges = new ListSequence();
                int[][] used = new int[numNodes][numNodes];
                for (int i=0; i<numNodes ; i++)
                {
                    for (int j=0; j<numNodes ; j++)
                    {
                        used[i][j] = 0;
                    }
                }
                for (int i=0; i<numNodes; i++)
                {
                    for (int j=0; j<numNodes; j++)
                    {
                        if (i!= j && (used[i][succ[i][j]] != 1) && (used[succ[i][j]][i] !=1))
                        {
                            Edge edge = new Edge(numEdges++, problem .getNode(i+1), problem .getNode(succ[i][j]+1));
                            //System.out.println("i:" + i + " j:" + j + "Edge: " + edge) ;
                            used[i][succ[i][j]] = 1;
                            used[succ[i][j]][i] = 1;
                            solEdges.add(edge);
                        }
                    }
                }
                for (Edge edge : problem .graph().incidentEdges(problem .getSupersink()))
                {
                    solEdges.add(edge);
                }
                
                for (Edge neu: solEdges)
                {
									minspanmodel.addEdge( neu, problem .getEdgeCapacity(neu), problem .getTransitTime(neu), problem.getExactTransitTime(neu));
                    //newgraph.addEdge(neu);
                    //minspanmodel.setEdgeCapacity(neu, model.getEdgeCapacity(neu));
                    //minspanmodel.setTransitTime(neu, model.getTransitTime(neu));
                    newMapping.setEdgeLevel(neu, originalMapping.getEdgeLevel(neu));                
                    //minspanmodel.setExactTransitTime(neu, model.getExactTransitTime(neu));
                }
                
                 //minspanmodel.setCurrentAssignment(model.getCurrentAssignment());
                 //minspanmodel.setSources(model.getSources());
                 
                 //values from mapping of original network 
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
                System.out.println("Edges used in APSPGraph: " + minspanmodel.numberOfEdges());
                
								minspanmodel.resetAssignment();
		return minspanmodel;
               
	}
    
    
}

