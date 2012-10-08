/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.shortestpath.Dijkstra;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import ds.collection.ListSequence;
import ds.graph.Edge;
import ds.graph.Forest;
import ds.graph.IdentifiableCollection;
import ds.graph.MinSpanningTree;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import ds.graph.problem.MinSpanningTreeProblem;
import ds.mapping.IdentifiableIntegerMapping;

/**
 *
 * @author schwengf
 */
//Creates a Shortest Path Tree for Non Grid graphs
public class DijkstraSpannerShrinker extends Algorithm<NetworkFlowModel,NetworkFlowModel>{
    
    public NetworkFlowModel minspanmodel;
    public MinSpanningTreeProblem minspanprob;
    public MinSpanningTree spantree;
    public IdentifiableIntegerMapping TransitForEdge;
    public IdentifiableCollection<Edge> ForestEdges;
    public Forest forest;
    public Edge neu;
    public Edge neureverse;
    public Edge neu2;
    //public int NumEdges = 0;
    IdentifiableCollection<Edge> solEdges = new ListSequence<>();
    
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
//		//model.TransitTimes( exactTransitTimes.round() );
//		model.roundTransitTimes();
//		
//    //            model.setExactTransitTimes(exactTransitTimes);
//		createReverseEdges( model );
    //    	model.setNetwork( model.getGraph().getAsStaticNetwork() );
                //nodes are nodes of original graph               
                
           //     minspanmodel.setSupersink(model.getSupersink());
                minspanmodel = new NetworkFlowModel( problem );
                Node Super = minspanmodel.getSupersink();
			ZToGraphMapping newMapping = minspanmodel.getZToGraphMapping();

			newMapping.setNodeSpeedFactor( Super, 1 );
		newMapping.setNodeRectangle( Super, new NodeRectangle( 0, 0, 0, 0 ) );
		newMapping.setFloorForNode( Super, -1 );
                
                   
                TransitForEdge = problem.transitTimes();
                /*for (Edge e: model.getGraph().edges())
                {System.out.println("kante: " + e + "transit:" + model.getTransitTime(e));}*/
                //minspanmodel.setNetwork(newgraph);
                //newgraph.setNodes(model.getGraph().nodes());
                for (Node node: problem )
                {
                    if (node.id()!= 0)
                    {
                        minspanmodel.setNodeCapacity(node, problem.getNodeCapacity(node));
                        newMapping.setNodeSpeedFactor(node, originalMapping.getNodeSpeedFactor(node));
                        newMapping.setNodeUpSpeedFactor(node, originalMapping.getUpNodeSpeedFactor(node));
                        newMapping.setNodeDownSpeedFactor(node, originalMapping.getDownNodeSpeedFactor(node));   
                    }
                }
                
                
                //System.out.println("Transitzeiten: " + TransitForEdge);
                Dijkstra dijkstra = new Dijkstra(problem.graph().getAsStaticNetwork(), TransitForEdge, problem.getSupersink(), true);
                dijkstra.run();
                forest = dijkstra.getShortestPathTree();
                ForestEdges = forest.edges();
                for (Edge edge: ForestEdges)
                {
                    //System.out.println("Forest Edge: " + edge);
                    neu = minspanmodel.newEdge(edge.start(), edge.end());
                    if (neu.start()== Super || neu.end() == Super)
                    {
                        neureverse = minspanmodel.newEdge(neu.end(), neu.start());
                        //newgraph.addEdge(neu);
                        //newgraph.addEdge(neureverse);
                        minspanmodel.setEdgeCapacity(neureverse, Integer.MAX_VALUE);
                        minspanmodel.setEdgeCapacity(neu, Integer.MAX_VALUE);
                        minspanmodel.setTransitTime(neureverse, 0);
                        minspanmodel.setTransitTime(neu, 0);
                        newMapping.setEdgeLevel(neureverse,originalMapping.getEdgeLevel(edge) ); 
                        newMapping.setEdgeLevel(neu,originalMapping.getEdgeLevel(edge) ); 
                        minspanmodel.setExactTransitTime(neureverse, 0);
                        minspanmodel.setExactTransitTime(neu, 0);
                    }
                    else
                    {
                        Edge origedge = problem.getEdge(edge.start(), edge.end());
                        //newgraph.addEdge(neu);
												minspanmodel.addEdge(neu, problem.getEdgeCapacity(origedge), problem.getTransitTime(origedge), problem.getExactTransitTime(origedge));
//                        minspanmodel.setEdgeCapacity(neu, model.getEdgeCapacity(origedge));
//                        minspanmodel.setTransitTime(neu, );
                        newMapping.setEdgeLevel(neu,originalMapping.getEdgeLevel(origedge) );             
//                        minspanmodel.setExactTransitTime(neu, );
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
                 
                 //minspanmodel.setZToGraphMapping(newMapping);
                BaseZToGraphConverter.createReverseEdges( minspanmodel );
                //minspanmodel.setNetwork(newgraph);
                //minspanmodel.setNetwork( minspanmodel.getGraph().getAsStaticNetwork());
                System.out.println("NumNodes: " + minspanmodel.numberOfNodes() + " Num SPT Edges: " + (minspanmodel.numberOfEdges() - minspanmodel.graph().degree(minspanmodel.getSupersink())));
								minspanmodel.resetAssignment();
		return minspanmodel;
                
                
    }
}
