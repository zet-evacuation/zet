/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.reduction;
import algo.graph.shortestpath.Dijkstra;
import java.util.Arrays;
import de.tu_berlin.math.coga.common.algorithm.Algorithm; 
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.Edge;
import ds.graph.IdentifiableCollection;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.ListSequence;
import ds.graph.MinSpanningTree;
import ds.graph.Network;
import ds.graph.problem.MinSpanningTreeProblem;

/**
 *
 * @author schwengf
 */
public class GreedyAlgo extends Algorithm<MinSpanningTreeProblem,MinSpanningTree> {
    
    int t;
    int[] Sort;
    int Min = 100000;
    int overalldist = 0;
    int NumEdges = 0;
    Edge MinEdge;
    Edge currentEdge;
    NetworkFlowModel OriginNetwork;
    Network network;
    IdentifiableIntegerMapping<Edge> TransitForEdge;
    //speichert den Abstand zweier Knoten im aktuell betrachteten Graphen G'
    IdentifiableIntegerMapping<Edge> currentTransitForEdge;
    IdentifiableCollection<Edge> origedges = new ListSequence<Edge>();
    IdentifiableCollection<Edge> sortededges = new ListSequence<Edge>();
    IdentifiableCollection<Edge> solEdges = new ListSequence<Edge>();
    
    @Override
    public MinSpanningTree runAlgorithm(MinSpanningTreeProblem minspan)
    {
        try{
            OriginNetwork = minspan.getGraph();
            TransitForEdge = OriginNetwork.getTransitTimes();
            
            for (Edge edge: OriginNetwork.getGraph().edges())
            {
                origedges.add(edge);
                //Transitzeit soll am Anfang in G' sehr hoch gesetzt sein
                currentTransitForEdge.add(edge, 100000);
            }   
            //Sortiere die Kanten nach aufsteigenden Transitzeiten...
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
            Edge edge = new Edge(NumEdges++,sortededges.first().start(),sortededges.first().end());
            solEdges.add(edge);
            network.setNodes(OriginNetwork.getGraph().nodes());
            network.setEdges(OriginNetwork.getGraph().edges());
            Dijkstra dijkstra = new Dijkstra(network,currentTransitForEdge ,null, true);
            
                
        }
           
        
        catch(Exception e) {
             System.out.println("Fehler in runGreedyAlgo " + e.toString());
         }
        
         return new MinSpanningTree(minspan,solEdges,overalldist);
            
    }
}

