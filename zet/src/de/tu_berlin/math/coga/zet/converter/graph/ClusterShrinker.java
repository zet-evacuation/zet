/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.reduction.ClusterAlgo;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.collection.ListSequence;
import ds.graph.*;
import ds.graph.problem.MinSpanningTreeProblem;
import ds.mapping.IdentifiableIntegerMapping;

/**
 *
 * @author schwengf
 */
public class ClusterShrinker extends Algorithm<NetworkFlowModel,NetworkFlowModel> {
    
    private NetworkFlowModel minspanmodel;
    public IdentifiableCollection<Node> SteinerNodes = new ListSequence<Node>();
    public MinSpanningTreeProblem minspanprob;
    int NumNode = 0;
    int NumEdges = 0;
    public IdentifiableIntegerMapping<Edge> TransitForEdge;
    public ClusterAlgo clusteralgo;
    public MinSpanningTreeProblem minspan;
    public MinSpanningTree tree;
   
    
    @Override
    protected NetworkFlowModel runAlgorithm( NetworkFlowModel problem ) {
			ZToGraphMapping originalMapping = problem.getZToGraphMapping();
			ZToGraphMapping newMapping = new ZToGraphMapping();
                
		newMapping.setRaster( problem.getZToGraphMapping().getRaster() );

//                mapping = new ZToGraphMapping();
//                ZToGraphMapping newmapping = new ZToGraphMapping();
//		model = new NetworkFlowModel();
//                
//		raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer( problem );
//		mapping.setRaster( raster );
//		model.setZToGraphMapping( mapping );
//
////                DynamicNetwork newgraph = new DynamicNetwork();
//                
//		super.createNodes();
//		super.createEdgesAndCapacities();
//		super.computeTransitTimes();
//		super.multiplyWithUpAndDownSpeedFactors();
//		//model.setTransitTimes( exactTransitTimes.round() );
//		model.roundTransitTimes();
//		createReverseEdges( model );
        	//model.setNetwork( model.getNetworkFlowModel().getAsStaticNetwork() );
                System.out.println("number of edges of original graph:" + problem.numberOfEdges());
               
                minspanmodel = new NetworkFlowModel( problem );
                //minspanmodel.setNetwork(newgraph);
                //newgraph.setNodes(model.getNetworkFlowModel().nodes());
                //minspanmodel.setSupersink(model.getSupersink());
       
                Node Super = minspanmodel.getSupersink();
                newMapping.setNodeSpeedFactor( Super, 1 );
		newMapping.setNodeRectangle( Super, new NodeRectangle( 0, 0, 0, 0 ) );
		newMapping.setFloorForNode( Super, -1 );
 
                
                minspanprob = new MinSpanningTreeProblem(problem,problem.transitTimes());
              
                
                //using 
                clusteralgo = new ClusterAlgo();
                clusteralgo.setProblem( minspanprob );
                System.out.print("Compute Cluster... " );
		clusteralgo.run();
                System.out.println("used time: " + clusteralgo.getRuntimeAsString() );
		tree = clusteralgo.getSolution();
                IdentifiableCollection<Edge> MinEdges = tree.getEdges();
  
               
                //AbstractNetwork netw = minspanmodel.getNetwork();
                //IdentifiableCollection<Node> NonMinNodes = new ListSequence();
                //int numberhidden = 0;
                
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
         
                //creates edges of new graph
                 for (Edge neu: MinEdges)
                {
			minspanmodel.addEdge( neu, problem.getEdgeCapacity( neu),problem.getTransitTime( neu ),problem.getExactTransitTime( neu ) );
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
                 //newMapping.isEvacuationNode = originalMapping.isEvacuationNode;
                 //newMapping.isSourceNode = originalMapping.isSourceNode;
                 newMapping.isDeletedSourceNode = originalMapping.isDeletedSourceNode;
                 newMapping.exitName = originalMapping.exitName;
                 
                minspanmodel.setZToGraphMapping(newMapping);   
                //minspanmodel.setSupersink(model.getSupersink());
                BaseZToGraphConverter.createReverseEdges( minspanmodel );
                
                //minspanmodel.setNetwork(newgraph);
                //minspanmodel.setNetwork( minspanmodel.getNetworkFlowModel().getAsStaticNetwork());
                System.out.println("Number of new Edges: " + minspanmodel.numberOfEdges());
                
								minspanmodel.resetAssignment();
                return minspanmodel;
                    
                
    }
    
    
    
}
