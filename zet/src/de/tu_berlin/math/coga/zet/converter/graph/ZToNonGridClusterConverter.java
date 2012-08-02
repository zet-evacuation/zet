/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.reduction.ClusterAlgo;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import ds.collection.ListSequence;
import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import ds.graph.MinSpanningTree;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import ds.graph.problem.MinSpanningTreeProblem;
import ds.mapping.IdentifiableIntegerMapping;
import ds.z.BuildingPlan;
/**
 *
 * @author schwengf
 */
public class ZToNonGridClusterConverter extends ZToNonGridGraphConverter{
    
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
    protected NetworkFlowModel runAlgorithm( BuildingPlan problem ) {
		mapping = new ZToGraphMapping();
                ZToGraphMapping newmapping = new ZToGraphMapping();
		model = new NetworkFlowModel();
                
		raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer( problem );
		mapping.setRaster( raster );
		model.setZToGraphMapping( mapping );

//                DynamicNetwork newgraph = new DynamicNetwork();
                
		super.createNodes();
		super.createEdgesAndCapacities();
		super.computeTransitTimes();
		super.multiplyWithUpAndDownSpeedFactors();
		//model.setTransitTimes( exactTransitTimes.round() );
		model.roundTransitTimes();
		createReverseEdges( model );
        	//model.setNetwork( model.getNetworkFlowModel().getAsStaticNetwork() );
                System.out.println("number of edges of original graph:" + model.numberOfEdges());
               
                minspanmodel = new NetworkFlowModel( model );
                //minspanmodel.setNetwork(newgraph);
                //newgraph.setNodes(model.getNetworkFlowModel().nodes());
                //minspanmodel.setSupersink(model.getSupersink());
       
                Node Super = minspanmodel.getSupersink();
                newmapping.setNodeSpeedFactor( Super, 1 );
		newmapping.setNodeRectangle( Super, new NodeRectangle( 0, 0, 0, 0 ) );
		newmapping.setFloorForNode( Super, -1 );
 
                
                minspanprob = new MinSpanningTreeProblem(model,model.transitTimes());
              
                
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
         
                //creates edges of new graph
                 for (Edge neu: MinEdges)
                {
										minspanmodel.addEdge( neu, model.getEdgeCapacity( neu),model.getTransitTime( neu ),model.getExactTransitTime( neu ) );
                    //newgraph.addEdge(neu);
                    //minspanmodel.setEdgeCapacity(neu, model.getEdgeCapacity(neu));                   
                    //minspanmodel.setTransitTime(neu, model.getTransitTime(neu));
                    newmapping.setEdgeLevel(neu, mapping.getEdgeLevel(neu));               
                    //minspanmodel.setExactTransitTime(neu, model.getExactTransitTime(neu));
                }
                   
                    
                
                //minspanmodel.setCurrentAssignment(model.getCurrentAssignment());
                //minspanmodel.setSources(model.getSources());
                 
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
                //minspanmodel.setNetwork( minspanmodel.getNetworkFlowModel().getAsStaticNetwork());
                System.out.println("Number of new Edges: " + minspanmodel.numberOfEdges());
                
								minspanmodel.resetAssignment();
                return minspanmodel;
                    
                
    }
    
    
    
}
