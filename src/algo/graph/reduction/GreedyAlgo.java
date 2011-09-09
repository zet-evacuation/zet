/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.reduction;
import algo.graph.shortestpath.Dijkstra;
import java.util.Arrays;
import de.tu_berlin.math.coga.common.algorithm.Algorithm; 
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.DynamicNetwork;
import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.ListSequence;
import ds.graph.MinSpanningTree;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.problem.MinSpanningTreeProblem;

/**
 *
 * @author schwengf
 */
public class GreedyAlgo extends Algorithm<MinSpanningTreeProblem,MinSpanningTree> {
    
    int t = 2;
    int[][] used;
    int Min = 100000;
    int overalldist = 0;
    int NumEdges = 0;
    int count=0;
    Edge MinEdge;
    Edge currentEdge;
    NetworkFlowModel OriginNetwork;
    Network network;
    IdentifiableIntegerMapping<Edge> TransitForEdge;
    //gives distance of nodes in current graph G'
    IdentifiableIntegerMapping<Edge> currentTransitForEdge;
    IdentifiableCollection<Edge> origedges = new ListSequence<Edge>();
    IdentifiableCollection<Edge> sortededges = new ListSequence<Edge>();
    IdentifiableCollection<Edge> solEdges = new ListSequence<Edge>();
    Node supersink;
    Edge supersinkedge;
    @Override
    public MinSpanningTree runAlgorithm(MinSpanningTreeProblem minspan)
    {
        try{
            OriginNetwork = minspan.getGraph();
            supersink = OriginNetwork.getSupersink();
            int numNodes = OriginNetwork.getGraph().numberOfNodes();
            TransitForEdge = OriginNetwork.getTransitTimes();
            currentTransitForEdge = new IdentifiableIntegerMapping<Edge>(OriginNetwork.getGraph().numberOfEdges());
            for (Edge edge: OriginNetwork.getGraph().edges())
            {
                if ((edge.start() != supersink) && (edge.end()!= supersink) )
                {
                    origedges.add(edge);                
                 // no edges existent --> high transit times
                    currentTransitForEdge.add(edge, 100000);
                }
            }   
           
            //sorting the edges by transit times
            while (origedges.size() > 0)
            {
                for (Edge edge: origedges)
                {
                    if (TransitForEdge.get(edge) < Min)
                    {
                        MinEdge = edge;
                    }
                }  
                sortededges.add(MinEdge);
                origedges.remove(MinEdge);
            }
            network = new Network(OriginNetwork.getGraph().numberOfNodes(), OriginNetwork.getGraph().numberOfEdges());
            for (Node node: OriginNetwork.getGraph().nodes())
            {
                network.setNode(node);
            }
            network.setEdges(OriginNetwork.getGraph().edges());
            //Array stores if nodes in a certain combination are already used as an edge
            used = new int[numNodes][numNodes];
            for (int i=0; i< numNodes; i++)
            {
                for (int j=0; j< numNodes; j++)
                {
                    used[i][j] = 0;
                }
            }
            //look at sorted edges
            while (sortededges.size()> 0)
            {
                // Calculates distance from a node of the current considered edge to every other node
                Dijkstra dijkstra = new Dijkstra(network, currentTransitForEdge, sortededges.first().end(), true);
                dijkstra.run();
                int dist = dijkstra.getDistance(sortededges.first().start());
                
                currentEdge = sortededges.first();
                if (dist > t* TransitForEdge.get(currentEdge))
                {
                    currentTransitForEdge.set(currentEdge,TransitForEdge.get(currentEdge));             
                    //verhindere, dass zugehoerige Rueckwaertskanten eingefuegt werden
     
                    if ((used[currentEdge.start().id()][currentEdge.end().id()]) == 0)
                    {
                        Edge edge = new Edge(NumEdges++,currentEdge.start(),currentEdge.end());
                        solEdges.add(edge);
                        used[edge.start().id()][edge.end().id()] = 1;
                        used[edge.end().id()][edge.start().id()] = 1;
                        count++;
                    }
                }
                
                
                sortededges.remove(currentEdge);
                  
            }
            
        IdentifiableCollection<Edge> addEdges = OriginNetwork.getGraph().incidentEdges(supersink);
        for (Edge edge: addEdges)
        {
            supersinkedge = new Edge(NumEdges++, edge.start(), edge.end());
            solEdges.add(supersinkedge);
        }
     
           
        }
        catch(Exception e) {
             System.out.println("Fehler in runGreedyAlgo " + e.toString());
         }
        
         return new MinSpanningTree(minspan,solEdges,overalldist);
            
    }
}

