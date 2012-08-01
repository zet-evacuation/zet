/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import ds.z.BuildingPlan;
import ds.graph.problem.MinSpanningTreeProblem;
import algo.graph.reduction.PrimsAlgo;
import ds.graph.network.DynamicNetwork;
import ds.graph.MinSpanningTree;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.IdentifiableCollection;
import ds.graph.NodeRectangle;
import algo.graph.reduction.GreedyAlgo;
import de.tu_berlin.math.coga.common.util.Level;
import ds.mapping.IdentifiableIntegerMapping;

/**
 *
 * @author schwengf
 */
//creates a t-spanner for grid graphs using a greedy algorithm 
public class ZToGreedySpannerConverter extends ZToNonGridGraphConverter{
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
                minspanmodel = new NetworkFlowModel();
                
		raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer( problem );
		mapping.setRaster( raster );
		model.setZToGraphMapping( mapping );

                DynamicNetwork newgraph = new DynamicNetwork();
                
		super.createNodes();
		super.createEdgesAndCapacities();
		super.computeTransitTimes();
		super.multiplyWithUpAndDownSpeedFactors();
		model.setTransitTimes( exactTransitTimes.round() );
                model.setExactTransitTimes(exactTransitTimes);
		createReverseEdges( model );
        	zzmodel.setNetwork( model.getGraph().getAsStaticNetwork() );
                /*for (Edge e: model.getGraph().edges())
                {
                    System.out.println("original edge: " + e);
                }*/
                System.out.println("number of edges in original graph:" + model.getGraph().numberOfEdges());
                //Knoten stimmen bei Original und beim MinSpanModel ueberein
                minspanmodel.setNetwork(newgraph);
                newgraph.setNodes(model.getGraph().nodes());
                
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
                        //newmapping.setNodeShape(node, mapping.getNodeShape(node));
                        newmapping.setNodeSpeedFactor(node, mapping.getNodeSpeedFactor(node));
                        newmapping.setNodeUpSpeedFactor(node, mapping.getUpNodeSpeedFactor(node));
                        newmapping.setNodeDownSpeedFactor(node, mapping.getDownNodeSpeedFactor(node));   
                    }
                }

                //creates a minimum spanning tree problem
                minspanprob = new MinSpanningTreeProblem(model,model.getTransitTimes());
              
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
                    if (neu.start() != model.getSupersink() && neu.end()!= model.getSupersink())
                    {
                        newgraph.addEdge(neu);
                        minspanmodel.setEdgeCapacity(neu, cap.get(neu));
                        minspanmodel.setTransitTime(neu, transit.get(neu));
                        newmapping.setEdgeLevel(neu, mapping.getEdgeLevel(neu));             
                        minspanmodel.setExactTransitTime(neu, model.getExactTransitTime(neu));
                    }
                    else
                    {
                        newgraph.addEdge(neu);
                        minspanmodel.setEdgeCapacity(neu, Integer.MAX_VALUE);
                        minspanmodel.setTransitTime(neu, 0);
                        newmapping.setEdgeLevel(neu, Level.Equal);
                        minspanmodel.setExactTransitTime(neu, 0);
                    }
                }
                
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
                createReverseEdges( minspanmodel );
                minspanmodel.setNetwork(newgraph);
                minspanmodel.setNetwork( minspanmodel.getGraph().getAsStaticNetwork());
                /*for (Edge e: minspanmodel.getGraph().edges())
                {
                    System.out.println("Kante im Spanner: " + e);
                }*/
                System.out.println("number of edges in t-spanner: " + minspanmodel.getGraph().numberOfEdges());
		return minspanmodel;
               
	}
    
}
