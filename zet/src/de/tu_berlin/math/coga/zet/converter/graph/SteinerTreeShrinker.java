/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.reduction.MSTSteiner;
import algo.graph.reduction.Prim;
import de.tu_berlin.coga.common.algorithm.Algorithm;
import de.tu_berlin.coga.container.collection.ListSequence;
import ds.graph.Edge;
import de.tu_berlin.coga.container.collection.IdentifiableCollection;
import ds.graph.MinSpanningTree;
import ds.graph.MinSteinerTree;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import ds.graph.problem.MinSpanningTreeProblem;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;

/**
 *
 * @author schwengf
 */
public class SteinerTreeShrinker extends Algorithm<NetworkFlowModel,NetworkFlowModel> {
    
    private NetworkFlowModel minspanmodel;
    public IdentifiableCollection<Node> SteinerNodes = new ListSequence<Node>();
    public MinSpanningTreeProblem minspanprob;
    int NumNode = 0;
    int NumEdges = 0;
    public IdentifiableIntegerMapping<Edge> TransitForEdge;
    public MSTSteiner steineralgo;
    public MinSteinerTree steinertree;
    public Prim prim;
    public MinSpanningTreeProblem minspan;
    public MinSpanningTree tree;
    
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
//                //DynamicNetwork newgraph = new DynamicNetwork();
//                
//		super.createNodes();
//		super.createEdgesAndCapacities();
//		super.computeTransitTimes();
//		super.multiplyWithUpAndDownSpeedFactors();
//		//model.setTransitTimes( exactTransitTimes.round() );
//		model.roundTransitTimes();
//		createReverseEdges( model );
        	//model.setNetwork( model.getGraph().getAsStaticNetwork() );
                System.out.println("number of edges of original graph:" + problem.numberOfEdges());
               
                minspanmodel = new NetworkFlowModel( problem );
                //minspanmodel.setNetwork(newgraph);
                //newgraph.setNodes(model.getGraph().nodes());
       
                //minspanmodel.setSupersink(model.getSupersink());
                Node Super = minspanmodel.getSupersink();
			ZToGraphMapping newMapping = minspanmodel.getZToGraphMapping();

			newMapping.setNodeSpeedFactor( Super, 1 );
		newMapping.setNodeRectangle( Super, new NodeRectangle( 0, 0, 0, 0 ) );
		newMapping.setFloorForNode( Super, -1 );
 
                
                minspanprob = new MinSpanningTreeProblem(problem,problem.transitTimes());
              
                
                //using 
                steineralgo = new MSTSteiner();
                steineralgo.setProblem( minspanprob );
                System.out.print("Compute Steiner... " );
		steineralgo.run();
                System.out.println("used time: " + steineralgo.getRuntimeAsString() );
		steinertree = steineralgo.getSolution();
                IdentifiableCollection<Edge> MinEdges = steinertree.getEdges();
                IdentifiableCollection<Node> MinNodes = steinertree.getNodes();
                IdentifiableIntegerMapping<Edge> MinDist = steinertree.getdist();
               
                
                //Aenderungen...
                //AbstractNetwork netw = minspanmodel.getNetwork();
                //DynamicNetwork dynnet = minspanmodel.getDynamicNetwork();
                //newgraph.setNode(Super);
                
                IdentifiableCollection<Node> NonMinNodes = new ListSequence();
                int numberhidden = 0;
                
                for (Node node: problem )
                {
                    if (node.id()!= 0)
                    {
                        
                        minspanmodel.setNodeCapacity(node, problem.getNodeCapacity(node));
                        //newmapping.setNodeShape(node, mapping.getNodeShape(node));
                        newMapping.setNodeSpeedFactor(node, originalMapping.getNodeSpeedFactor(node));
                        newMapping.setNodeUpSpeedFactor(node, originalMapping.getUpNodeSpeedFactor(node));
                        newMapping.setNodeDownSpeedFactor(node, originalMapping.getDownNodeSpeedFactor(node));  
                        if (MinNodes.contains(node))
                    {
                        //netw.setHiddenOnlyNode(node, false);
                        
                    }
                    else
                    {
                        if (node.id() != 0)
                        {
                            NonMinNodes.add(node);
                            numberhidden++;
                          //  netw.setHiddenOnlyNode(node, true);
                        }
                    }
                    }
                }
                
                /*AbstractNetwork neu1 = minspanmodel.getNetwork();
                int i = neu1.getNodeCapacity();
                System.out.println("Kapazitaet: " + i);
                neu1.setNodeCapacity(i- MinNodes.size());*/
                
               //System.out.print("Number of hidden Nodes: " + numberhidden); 
               //dynnet.removeNodes(NonMinNodes);
         
                //creates edges of new graph
                for (Edge neu: MinEdges)
                {
									minspanmodel.addEdge( neu, problem.getEdgeCapacity( neu ), problem.getTransitTime( neu ), problem.getExactTransitTime( neu ) );
                  newMapping.setEdgeLevel(neu, originalMapping.getEdgeLevel(neu));                 
                }

								 //values from mapping of original network 
                 newMapping.raster = originalMapping.getRaster();
                 newMapping.nodeRectangles = originalMapping.getNodeRectangles();
                 newMapping.nodeFloorMapping = originalMapping.getNodeFloorMapping();
                 newMapping.isDeletedSourceNode = originalMapping.isDeletedSourceNode;
                 newMapping.exitName = originalMapping.exitName;
                 
                BaseZToGraphConverter.createReverseEdges( minspanmodel, new DefaultEdgeFilter( minspanmodel ) );
                
								minspanmodel.resetAssignment();
                return minspanmodel;
                    
                
    }
    
    
}
