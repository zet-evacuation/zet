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
    int[] Sort;
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
    //speichert den Abstand zweier Knoten im aktuell betrachteten Graphen G'
    IdentifiableIntegerMapping<Edge> currentTransitForEdge;
    IdentifiableCollection<Edge> origedges = new ListSequence<Edge>();
    IdentifiableCollection<Edge> sortededges = new ListSequence<Edge>();
    IdentifiableCollection<Edge> solEdges = new ListSequence<Edge>();
    IdentifiableCollection<Edge> solEdges2 = new ListSequence<Edge>();
    @Override
    public MinSpanningTree runAlgorithm(MinSpanningTreeProblem minspan)
    {
        try{
            OriginNetwork = minspan.getGraph();
            int numNodes = OriginNetwork.getGraph().numberOfNodes();
            TransitForEdge = OriginNetwork.getTransitTimes();
            currentTransitForEdge = new IdentifiableIntegerMapping<Edge>(OriginNetwork.getGraph().numberOfEdges());
            for (Edge edge: OriginNetwork.getGraph().edges())
            {
                origedges.add(edge);                
                //Transitzeit soll am Anfang in G' sehr hoch gesetzt sein
                currentTransitForEdge.add(edge, 100000);
            }   
            System.out.println("Anzahl der OriginalKanten " + origedges.size());
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
            network = new Network(OriginNetwork.getGraph().numberOfNodes(), OriginNetwork.getGraph().numberOfEdges());
            for (Node node: OriginNetwork.getGraph().nodes())
            {
                network.setNode(node);
            }
            network.setEdges(OriginNetwork.getGraph().edges());
            //Array zum Speichern ob Knoten in der Kombination schon verwendet wurden
            used = new int[numNodes][numNodes];
            for (int i=0; i< numNodes; i++)
            {
                for (int j=0; j< numNodes; j++)
                {
                    used[i][j] = 0;
                }
            }
            //gehe ueber alle geordneten Kanten...
            while (sortededges.size()> 0)
            {
                
                //Berechnet kuerzesten Weg von einem Knoten der aktuell betrachteten Kante zu allen anderen
                Dijkstra dijkstra = new Dijkstra(network, currentTransitForEdge, sortededges.first().end(), true);
                dijkstra.run();
                int dist = dijkstra.getDistance(sortededges.first().start());
                //System.out.println("Distanz der 1. Kante im Dijkstra: " + dist);
                //System.out.println("Distanz der 1. Kante im Originalgraphen: " + TransitForEdge.get(sortededges.first()));
                currentEdge = sortededges.first();
                if (dist > t* TransitForEdge.get(currentEdge))
                {
                    currentTransitForEdge.set(currentEdge,TransitForEdge.get(currentEdge));             
                    //verhindere, dass zugehoerige Rueckwaertskanten eingefuegt werden
                    //Problem: Kante hat andere ID...bloss dieselben Endknoten
                    Edge edge = new Edge(NumEdges++,currentEdge.start(),currentEdge.end());
                    if ((used[edge.start().id()][edge.end().id()]) == 0)
                    {
                        solEdges.add(edge);
                        used[edge.start().id()][edge.end().id()] = 1;
                        used[edge.end().id()][edge.start().id()] = 1;
                        System.out.println("neue Kante: " + edge);
                        count++;
                    }
                }
                //System.out.println("neue Transitzeit fuer Dijkstra " + currentTransitForEdge.get(sortededges.first()));
                
                sortededges.remove(currentEdge);
                    
            //}
            }
      
            System.out.println("Anzahl neuer Kanten:" + count);
           
        }
        catch(Exception e) {
             System.out.println("Fehler in runGreedyAlgo " + e.toString());
         }
        
         return new MinSpanningTree(minspan,solEdges,overalldist);
            
    }
}

