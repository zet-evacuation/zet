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
import ds.graph.DynamicNetwork;
import ds.graph.MinSpanningTree;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.IdentifiableCollection;
import ds.graph.NodeRectangle;
import algo.graph.reduction.GreedyAlgo;
import algo.graph.shortestpath.APSPAlgo;
/**
 *
 * @author schwengf
 */
public class ZToNonGridAPSPGraphConverter extends ZToNonGridGraphConverter{
 
    
    public NetworkFlowModel minspanmodel;

    public APSPAlgo apspalgo;


    @Override
    protected NetworkFlowModel runAlgorithm( BuildingPlan problem ) {
		mapping = new ZToGraphMapping();
                ZToGraphMapping newmapping = new ZToGraphMapping();
		plan = problem;
		model = new NetworkFlowModel();
                minspanmodel = new NetworkFlowModel();
                
		raster = RasterContainerCreator.getInstance().ZToGraphRasterContainer( plan );
		mapping.setRaster( raster );
		model.setZToGraphMapping( mapping );

                DynamicNetwork newgraph = new DynamicNetwork();
                
		super.createNodes();
		super.createEdgesAndCapacities();
		super.computeTransitTimes();
		super.multiplyWithUpAndDownSpeedFactors();
		model.setTransitTimes( exactTransitTimes.round() );
		createReverseEdges( model );
        	model.setNetwork( model.getGraph().getAsStaticNetwork() );
                System.out.println("number of edges of original graph:" + model.getGraph().numberOfEdges());
                // nodes are nodes of original network
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
              
                
                //using APSP algorithm:
                apspalgo = new APSPAlgo(model);
                apspalgo.run();
              
                
                for (Edge neu: model.getGraph().edges())
                {
                    newgraph.addEdge(neu);
                    minspanmodel.setEdgeCapacity(neu, model.getEdgeCapacity(neu));
                    minspanmodel.setTransitTime(neu, model.getTransitTime(neu));
                    newmapping.setEdgeLevel(neu, mapping.getEdgeLevel(neu));                
                    minspanmodel.setExactTransitTime(neu, model.getExactTransitTime(neu));
                }
                
                 minspanmodel.setCurrentAssignment(model.getCurrentAssignment());
                 minspanmodel.setSources(model.getSources());
                 
                 //values from mapping of original network 
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
                System.out.println("Edges used in APSPGraph: " + minspanmodel.getGraph().numberOfEdges());
                
		return minspanmodel;
               
	}
    
    
}

