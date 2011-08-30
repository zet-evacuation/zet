/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.shortestpath.Dijkstra;
import de.tu_berlin.math.coga.common.util.Level;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import ds.graph.DynamicNetwork;
import ds.graph.Edge;
import ds.graph.Forest;
import ds.graph.IdentifiableCollection;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.ListSequence;
import ds.graph.MinSpanningTree;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import ds.graph.problem.MinSpanningTreeProblem;
import ds.z.BuildingPlan;

/**
 *
 * @author schwengf
 */
public class ZToDijkstraSpannerConverter extends ZToNonGridGraphConverter{
    
    public NetworkFlowModel minspanmodel;
    public MinSpanningTreeProblem minspanprob;
    public MinSpanningTree spantree;
    public IdentifiableIntegerMapping TransitForEdge;
    public IdentifiableCollection<Edge> ForestEdges;
    public Forest forest;
    public Edge neu;
    public Edge neu2;
    public int NumEdges = 0;
    IdentifiableCollection<Edge> solEdges = new ListSequence<Edge>();
    
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
                System.out.println("Grad der Supersenke im Original: " + model.getGraph().degree(model.getSupersink()));
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
                
                TransitForEdge = model.getTransitTimes();
                Dijkstra dijkstra = new Dijkstra(model.getNetwork(), TransitForEdge, model.getSupersink(), true);
                dijkstra.run();
                forest = dijkstra.getShortestPathTree();
                ForestEdges = forest.edges();
                for (Edge edge: ForestEdges)
                {
                    neu = new Edge(NumEdges++, edge.start(), edge.end());
                    solEdges.add(neu);
                }
                
                for (Edge create: solEdges)
                {
                    newgraph.addEdge(create);
                    minspanmodel.setEdgeCapacity(create, model.getEdgeCapacity(create));
                    //minspanmodel.setEdgeCapacity(neu, 1);
                    minspanmodel.setTransitTime(create, 1);
                    newmapping.setEdgeLevel(create, Level.Lower);             
                    minspanmodel.setExactTransitTime(create, model.getExactTransitTime(create));
                }
                
                 minspanmodel.setCurrentAssignment(model.getCurrentAssignment());
                 minspanmodel.setSources(model.getSources());
                 
                 //Werte, die aus altem Mapping uebernommen werden
                 newmapping.raster = mapping.getRaster();
                 newmapping.nodeRectangles = mapping.getNodeRectangles();
                 newmapping.nodeFloorMapping = mapping.getNodeFloorMapping();
                 newmapping.isEvacuationNode = mapping.isEvacuationNode;
                 newmapping.isSourceNode = mapping.isSourceNode;
                 newmapping.isDeletedSourceNode = mapping.isDeletedSourceNode;
                 newmapping.exitName = mapping.exitName;
                 
                 minspanmodel.setZToGraphMapping(newmapping);                
                 minspanmodel.setSupersink(model.getSupersink());
                System.out.println("Anzahl der t-Spanner Kanten vorm Verdoppeln " + minspanmodel.getGraph().numberOfEdges());      
                createReverseEdges( minspanmodel );
                minspanmodel.setNetwork(newgraph);
                minspanmodel.setNetwork( minspanmodel.getGraph().getAsStaticNetwork());
                System.out.println("Grad der Senke " + minspanmodel.getGraph().degree(minspanmodel.getSupersink()));
                System.out.println("Anzahl der t-Spanner Kantennach Verdoppeln: " + minspanmodel.getGraph().numberOfEdges());
		return minspanmodel;
                
                
    }
}
