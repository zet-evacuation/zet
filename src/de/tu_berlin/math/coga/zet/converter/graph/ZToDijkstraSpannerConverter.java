/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.shortestpath.Dijkstra;
import de.tu_berlin.math.coga.common.util.Level;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import ds.graph.network.DynamicNetwork;
import ds.graph.Edge;
import ds.graph.Forest;
import ds.graph.IdentifiableCollection;
import ds.mapping.IdentifiableIntegerMapping;
import ds.collection.ListSequence;
import ds.graph.MinSpanningTree;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import ds.graph.problem.MinSpanningTreeProblem;
import ds.z.BuildingPlan;

/**
 *
 * @author schwengf
 */
//Creates a Shortest Path Tree for Non Grid graphs
public class ZToDijkstraSpannerConverter extends ZToNonGridGraphConverter{
    
    public NetworkFlowModel minspanmodel;
    public MinSpanningTreeProblem minspanprob;
    public MinSpanningTree spantree;
    public IdentifiableIntegerMapping TransitForEdge;
    public IdentifiableCollection<Edge> ForestEdges;
    public Forest forest;
    public Edge neu;
    public Edge neureverse;
    public Edge neu2;
    public int NumEdges = 0;
    IdentifiableCollection<Edge> solEdges = new ListSequence<Edge>();
    
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
        	model.setNetwork( model.getGraph().getAsStaticNetwork() );
                //nodes are nodes of original graph               
                
                minspanmodel.setSupersink(model.getSupersink());
                Node Super = minspanmodel.getSupersink();
                newmapping.setNodeSpeedFactor( Super, 1 );
		newmapping.setNodeRectangle( Super, new NodeRectangle( 0, 0, 0, 0 ) );
		newmapping.setFloorForNode( Super, -1 );
                
               
                /*Node superSource = new Node(model.getGraph().numberOfNodes());
                model.setNodeCapacity(superSource, Integer.MAX_VALUE);
                mapping.setNodeUpSpeedFactor(superSource, 1);
                mapping.setNodeDownSpeedFactor(superSource, 1);
                model.getDynamicNetwork().addNode(superSource);
                mapping.setNodeSpeedFactor( superSource, 1 );
		mapping.setNodeRectangle( superSource, new NodeRectangle( 0, 0, 0, 0 ) );
		mapping.setFloorForNode( superSource, -1 );*/
                
                /*for (Node s: model.getSources())
                {
                    int k = model.getGraph().numberOfEdges();
                    Edge e = new Edge(k++,s,superSource);  
                    Edge e_rev = new Edge(k++,superSource,s);
                    model.getDynamicNetwork().addEdge(e);
                    model.getDynamicNetwork().addEdge(e_rev);
                    model.setEdgeCapacity(e, Integer.MAX_VALUE);         
                    model.setTransitTime(e, 0);
                    mapping.setEdgeLevel(e,Level.Equal );             
                    model.setExactTransitTime(e, 0);
                    
                    model.setEdgeCapacity(e_rev, Integer.MAX_VALUE);         
                    model.setTransitTime(e_rev, 0);
                    mapping.setEdgeLevel(e_rev,Level.Equal );             
                    model.setExactTransitTime(e_rev, 0);
                }*/
      
                TransitForEdge = model.getTransitTimes();
                /*for (Edge e: model.getGraph().edges())
                {System.out.println("kante: " + e + "transit:" + model.getTransitTime(e));}*/
                minspanmodel.setNetwork(newgraph);
                newgraph.setNodes(model.getGraph().nodes());
                for (Node node: model.getGraph().nodes())
                {
                    if (node.id()!= 0)
                    {
                        minspanmodel.setNodeCapacity(node, model.getNodeCapacity(node));
                        newmapping.setNodeSpeedFactor(node, mapping.getNodeSpeedFactor(node));
                        newmapping.setNodeUpSpeedFactor(node, mapping.getUpNodeSpeedFactor(node));
                        newmapping.setNodeDownSpeedFactor(node, mapping.getDownNodeSpeedFactor(node));   
                    }
                }
                
                
                //System.out.println("Transitzeiten: " + TransitForEdge);
                Dijkstra dijkstra = new Dijkstra(model.getNetwork(), TransitForEdge, model.getSupersink(), true);
                dijkstra.run();
                forest = dijkstra.getShortestPathTree();
                ForestEdges = forest.edges();
                for (Edge edge: ForestEdges)
                {
                    //System.out.println("Forest Edge: " + edge);
                    neu = new Edge(NumEdges++, edge.start(), edge.end());
                    if (neu.start()== Super || neu.end() == Super)
                    {
                        neureverse = new Edge(NumEdges++, neu.end(), neu.start());
                        newgraph.addEdge(neu);
                        newgraph.addEdge(neureverse);
                        minspanmodel.setEdgeCapacity(neureverse, Integer.MAX_VALUE);
                        minspanmodel.setEdgeCapacity(neu, Integer.MAX_VALUE);
                        minspanmodel.setTransitTime(neureverse, 0);
                        minspanmodel.setTransitTime(neu, 0);
                        newmapping.setEdgeLevel(neureverse,mapping.getEdgeLevel(edge) ); 
                        newmapping.setEdgeLevel(neu,mapping.getEdgeLevel(edge) ); 
                        minspanmodel.setExactTransitTime(neureverse, 0);
                         minspanmodel.setExactTransitTime(neu, 0);
                    }
                    else
                    {
                        Edge origedge = model.getGraph().getEdge(edge.start(), edge.end());
                        newgraph.addEdge(neu);
                        minspanmodel.setEdgeCapacity(neu, model.getEdgeCapacity(origedge));
                        minspanmodel.setTransitTime(neu, model.getTransitTime(origedge));
                        newmapping.setEdgeLevel(neu,mapping.getEdgeLevel(origedge) );             
                        minspanmodel.setExactTransitTime(neu, model.getExactTransitTime(origedge));
                    }
                }
                
                 
                 /*for (Edge e: minspanmodel.getGraph().edges())
                 {
                     System.out.println("Edge:" + e + "Transit neu: " + minspanmodel.getTransitTime(e));
                 }*/
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
                createReverseEdges( minspanmodel );
                /*for (Edge e: minspanmodel.getGraph().edges())
                {System.out.println("Edge_last: " + e + "tran" + minspanmodel.getTransitTime(e));}*/
                minspanmodel.setNetwork(newgraph);
                minspanmodel.setNetwork( minspanmodel.getGraph().getAsStaticNetwork());
                System.out.println("Number of Created Shortest Path Tree Edges: " + minspanmodel.getGraph().numberOfEdges());
		return minspanmodel;
                
                
    }
}
