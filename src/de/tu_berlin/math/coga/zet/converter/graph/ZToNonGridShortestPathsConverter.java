/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tu_berlin.math.coga.zet.converter.graph;

import algo.graph.shortestpath.Dijkstra;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.RasterContainerCreator;
import ds.graph.network.DynamicNetwork;
import ds.graph.Edge;
import ds.graph.Forest;
import ds.graph.IdentifiableCollection;
import ds.mapping.IdentifiableIntegerMapping;
import ds.collection.ListSequence;
import ds.graph.MinSpanningTree;
import ds.graph.network.AbstractNetwork;
import ds.graph.Node;
import ds.graph.NodeRectangle;
import ds.graph.problem.MinSpanningTreeProblem;
import ds.z.BuildingPlan;

/**
 *
 * @author schwengf
 */
public class ZToNonGridShortestPathsConverter  extends ZToNonGridGraphConverter{
    
    public NetworkFlowModel minspanmodel;
    public MinSpanningTreeProblem minspanprob;
    public MinSpanningTree spantree;
    public IdentifiableIntegerMapping TransitForEdge;
    public IdentifiableIntegerMapping currentTransitForEdge;
    IdentifiableIntegerMapping<Edge> currentTransitForEdge2;
    public IdentifiableCollection<Edge> ForestEdges;
    public Forest forest;
    public Edge neu;
    public Edge neureverse;
    public Edge neu2;
    public int NumEdges = 0;
    public int NumCurrentEdges = 0;
    public int NumShortestPaths = 5;
    IdentifiableCollection<Edge> solEdges = new ListSequence<Edge>();
    private ListSequence<Edge> currentEdges = new ListSequence<Edge>();
    private ListSequence<Edge> res; 
    
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
		/*for (Edge edge: model.getGraph().edges())
                {
                    System.out.println("Edge before doubling:" + edge);
                }*/
		createReverseEdges( model );
                /*for (Edge edge: model.getGraph().edges())
                {
                    System.out.println("Edge after doubling:" + edge);
                }*/
        	model.setNetwork( model.getGraph().getAsStaticNetwork() );
                //nodes are nodes of original graph
                minspanmodel.setNetwork(newgraph);
                newgraph.setNodes(model.getGraph().nodes());
       
                //set up dynamic network for problem to remove edges used in repeated Dijkstra
                DynamicNetwork net = new DynamicNetwork();
                net.setNodes(model.getNetwork().nodes());
          
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
                        newmapping.setNodeSpeedFactor(node, mapping.getNodeSpeedFactor(node));
                        newmapping.setNodeUpSpeedFactor(node, mapping.getUpNodeSpeedFactor(node));
                        newmapping.setNodeDownSpeedFactor(node, mapping.getDownNodeSpeedFactor(node));   
                    }
                }
                
                TransitForEdge = model.getTransitTimes();
                for (Edge edge: model.getGraph().edges())
                {
                    //System.out.println("Original Edges: " + edge);
                    currentEdges.add(edge);
                }
                
                Dijkstra dijkstra = new Dijkstra(model.getNetwork(), TransitForEdge, model.getSupersink(), true);
                dijkstra.run();
                //System.out.println(dijkstra.getLastEdges());
                for (Node sink: model.getSinks())
                {
                    for (Node source: model.getSources())
                    {
                        Node currentNode = source;
                        //System.out.println(currentNode);
                        while (currentNode != sink)
                        {
                            Edge create = dijkstra.getLastEdge(currentNode);
                            if (!create.isIncidentTo(model.getSupersink()))
                            {
                                currentEdges.remove(create);
                            }
                            System.out.println("Edges in 1. run: " + create);
                            neu = new Edge(NumEdges++, create.start(),create.end());
                            solEdges.add(neu);
                            currentNode = create.opposite(currentNode);
                        }
                    }
                }
                
                currentTransitForEdge = new IdentifiableIntegerMapping(currentEdges.size());
                for (Edge edge: model.getGraph().edges())
                    {
                        if (currentEdges.contains(edge))
                        {
                            Edge current = new Edge(NumCurrentEdges++,edge.start(),edge.end());
                            net.setEdge(current);
                            currentTransitForEdge.add(current, TransitForEdge.get(edge));
                        }
                    }
                
                for (int k=0; k<NumShortestPaths - 1 ; k++)
                { 
                    ListSequence<Edge> currentEdges2 = new ListSequence<Edge>();
                    for (Edge edge: net.edges())
                    {
                        currentEdges2.add(edge);
                    }
                    res = new ListSequence<Edge>();
                    System.out.println("Looking for " + (k+2) + " th shortest path" );
                    NumCurrentEdges = 0;
                    
                    AbstractNetwork repeat = net.getAsStaticNetwork();
                    /*for (Edge edge: repeat.edges())
                    {
                        System.out.println("neue Kanten: " + edge);
                    }*/
                    if (k>0)
                    {
                        currentTransitForEdge = currentTransitForEdge2;
                    }
            
                    Dijkstra dijkstra2 = new Dijkstra(repeat,currentTransitForEdge, model.getSupersink(), true);
                    dijkstra2.run();
                    //System.out.println(dijkstra2.getLastEdges());
                    int countwrong = 0;
                    for (Node sink: model.getSinks())
                    {
                           for (Node source: model.getSources())
                        {
                            Node currentNode = source;
                            //System.out.println(currentNode);
                            while (currentNode != sink)
                            {
                                Edge create = dijkstra2.getLastEdge(currentNode);
                                if (create == null)
                                {
                                    countwrong++;
                                    System.out.println("No " + (k+2) + " th shortest Path for node" + currentNode);
                                    break;
                                }
                                if (!create.isIncidentTo(model.getSupersink()))
                                {
                                    res.add(create);
                                }
                                System.out.println("Shortest Path Edges: " + create);
                                neu2 = new Edge(NumEdges++, create.start(),create.end());
                                solEdges.add(neu2);
                                currentNode = create.opposite(currentNode);
                            }
                        }
                         if (countwrong == model.getSources().size())
                         {
                            k = NumShortestPaths;
                            System.out.println("No more shortest paths available for given sources!");
                         }  
                    }
                    net.removeAllEdges();
                    NumCurrentEdges = 0;
                    currentTransitForEdge2 = new IdentifiableIntegerMapping<Edge>(currentEdges2.size());
                    for (Edge edge: currentEdges2)
                    {
                        if (!res.contains(edge))
                        {
                            //System.out.println("Yes");
                            Edge next = new Edge(NumCurrentEdges++, edge.start(), edge.end());
                            net.addEdge(next);
                            currentTransitForEdge2.add(next, currentTransitForEdge.get(edge) );
                            
                        } 
                    }
                
                }
                for (Edge edge: solEdges)
                {
                    newgraph.addEdge(edge);
                    minspanmodel.setEdgeCapacity(edge, model.getEdgeCapacity(edge));
                    minspanmodel.setTransitTime(edge, model.getTransitTime(edge));
                    newmapping.setEdgeLevel(edge,mapping.getEdgeLevel(edge) );             
                    minspanmodel.setExactTransitTime(edge, model.getExactTransitTime(edge));
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
                System.out.println("Number of Created Repeated Shortest Paths Edges: " + minspanmodel.getGraph().numberOfEdges());
		return minspanmodel;
                
                
    }
    
    
    
    
    
}
