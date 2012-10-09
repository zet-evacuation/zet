/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.reduction;

import algo.graph.shortestpath.Dijkstra;
import de.tu_berlin.math.coga.common.algorithm.Algorithm;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import de.tu_berlin.math.coga.zet.converter.graph.ZToGraphMapping;
import ds.collection.ListSequence;
import ds.graph.Edge;
import ds.graph.Forest;
import ds.graph.Graph;
import ds.graph.IdentifiableCollection;
import ds.graph.MinSteinerTree;
import ds.graph.NetworkMST;
import ds.graph.Node;
import ds.graph.Path;
import ds.graph.network.AbstractNetwork;
import ds.graph.network.DynamicNetwork;
import ds.graph.problem.MinSpanningTreeProblem;
import ds.graph.problem.NetworkMSTProblem;
import ds.mapping.IdentifiableIntegerMapping;


/**
 *
 * @author schwengf
 */
public class MSTSteiner extends Algorithm<MinSpanningTreeProblem,MinSteinerTree>{
    
    int overalldist = 0;
    NetworkFlowModel OriginNetwork;
    Graph OriginGraph;
    Node steiner;
    Edge edge;
    Edge supersinkedge;
    int NumNode = 1;
    int Nodenum = 1;
    int NumEdges = 0;
    int Num = 0;
    int count;  
    IdentifiableCollection<Node> solNodes = new ListSequence();
    IdentifiableCollection<Node> solutionNodes = new ListSequence();
    IdentifiableCollection<Node> SteinerNodes = new ListSequence();
    IdentifiableCollection<Node> EvacuationNodes = new ListSequence();
    IdentifiableCollection<Edge> solEdges = new ListSequence();
    IdentifiableCollection<Edge> solutionEdges = new ListSequence();
    
    
    Path[][] ShortestPaths;
    IdentifiableIntegerMapping<Edge> shortestpathdist;
    NetworkMSTProblem networkprob;
    PrimForNetwork prim;
    Dijkstra dijkstra;
    
    @Override
    public MinSteinerTree runAlgorithm(MinSpanningTreeProblem minspan){
    
    try{    
        OriginNetwork = minspan.getNetworkFlowModel(); 
        Node supersink = minspan.getNetworkFlowModel().getSupersink();
        OriginGraph = OriginNetwork.graph();
        int numNodes = OriginGraph.numberOfNodes();
        ZToGraphMapping mapping = OriginNetwork.getZToGraphMapping();
        IdentifiableIntegerMapping<Edge> TransitForEdge = OriginNetwork.transitTimes();
        //saves the current considered network for different iterations
        DynamicNetwork firstnet = new DynamicNetwork();
        
        for (Node node: OriginGraph.nodes())
        {
            if (node.id() != 0)
            {
                //if (mapping.getIsEvacuationNode(node)== true /*|| mapping.getIsSourceNode(node) == true */ )
							boolean isSource = OriginNetwork.getSources().contains( node );
							boolean isSink = OriginNetwork.getSinks().contains( node );
              if( isSource || isSink ) // TODO: chick if works  
							{
									
                    firstnet.addNode(node);
                    SteinerNodes.add(node);
                   
                }
            }
        }
        System.out.println("Number of steinernodes: " + SteinerNodes.size());    
        TransitForEdge = OriginNetwork.transitTimes();
        ShortestPaths = new Path[numNodes][numNodes];
        shortestpathdist = new IdentifiableIntegerMapping<Edge>(OriginNetwork.numberOfEdges());
        

        //gives a network connecting the source and evacutaion nodes with shortest path edges...
        while (!SteinerNodes.empty())
        {
            Node node = SteinerNodes.first();
            dijkstra = new Dijkstra(((AbstractNetwork)OriginNetwork.graph()).getAsStaticNetwork(), TransitForEdge, node, true);
            dijkstra.run();
           
            SteinerNodes.remove(SteinerNodes.first());
            for (Node restnode: SteinerNodes)
            {    
                int dist = dijkstra.getDistance(restnode);
                
								edge = new Edge(NumEdges++, node, restnode); 
                firstnet.addEdge(edge); 
                //weight of edge is shortest distance (using Dijkstra)
                shortestpathdist.set(edge, dist);
                solEdges.add(edge);
                Forest spt = dijkstra.getShortestPathTree();
                //stores the shortest path from root to certain vertex
                ShortestPaths[node.id()][restnode.id()] = spt.getPathToRoot(restnode); 
            }
            
         }
        System.out.println("Dijkstra done");
        
        /*for (Node node: firstnet.nodes())
        {
            System.out.println("Evakuierungsknoten: " + node);
        }
        for (Edge edge: firstnet.edges())
        {
            System.out.println("Kante zwischen Evakuierungsknoten:" + edge);
            System.out.println("Distance: " + shortestpathdist.get(edge));
        }*/
        
        //creates problem to find a min spanning tree for given network in 1. iteration
        networkprob = new NetworkMSTProblem(firstnet, shortestpathdist);
        prim = new PrimForNetwork();
        prim.setProblem(networkprob);
        prim.run();
        NetworkMST solv = prim.getSolution();
        IdentifiableCollection<Edge> MSTEdges = solv.getEdges();
				
        for (Edge mst: MSTEdges)
        {
            count = 0;
            //get shortest Path in Original Network
            Path path = ShortestPaths[mst.start().id()][mst.end().id()];
            //gets edges of shortest path
            IdentifiableCollection<Edge> PathEdges = path.getEdges();
            IdentifiableCollection<Node> PathNodes = new ListSequence();
            //gets nodes of shortest path
            for (Edge sptedge : PathEdges)
            {
                if (!PathNodes.contains(sptedge.end()))
                {    
                    PathNodes.add(sptedge.end());
                }
                if (!PathNodes.contains(sptedge.start()))
                {    
                    PathNodes.add(sptedge.start());
                }
            }
             
            for (Node currNode: solNodes)
            {
                if (PathNodes.contains(currNode))
                {
                    count++;
                }
            }
          
            if (count < 2)
            {
                for (Edge sptedge : PathEdges)
                {
                    Edge insert = new Edge(Num++, sptedge.start(), sptedge.end());
										
                    solutionEdges.add(insert);
                    //solutionEdges.add(sptedge);
                    if (!solNodes.contains(sptedge.start()))
                    {
                        solNodes.add(sptedge.start());
                    }
                    if (!solNodes.contains(sptedge.end()))
                    {
                        solNodes.add(sptedge.end());
                    }
                }
            }
            else 
            {
                for (Edge sptedge : PathEdges)
                {
                    if (!solutionEdges.contains(edge))
										{
									for( Edge e : solutionEdges ) {
										if( e.start().equals( sptedge.start() ) && e.end().equals( sptedge.end() ) ) {
											//System.out.println( "We are to insert an edge twice (in the second loop)" );
										} else {
											Edge insert = new Edge(Num++, sptedge.start(), sptedge.end());
														solutionEdges.add(insert);
														if (!solNodes.contains(sptedge.start()))
														{
																solNodes.add(sptedge.start());                                                    
														}
														if (!solNodes.contains(sptedge.end()))
														{
																solNodes.add(sptedge.start());
														}  
											
										}
									}

                          
                    }
                }
            }
            
            
        }
        
        IdentifiableCollection<Edge> addEdges = OriginNetwork.graph().incidentEdges(supersink);
        for (Edge sinkedge: addEdges)
        {
            supersinkedge = new Edge(Num++, sinkedge.start(), sinkedge.end());
            solutionEdges.add(supersinkedge);
        }
        
        
    }
        
        
    catch(Exception e) {
             System.out.println("Fehler in Steiner-MST " + e.toString());
         }
     return new MinSteinerTree(minspan, shortestpathdist, solutionEdges, solNodes, overalldist);
    
    }
}
