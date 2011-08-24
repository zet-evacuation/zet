/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.reduction;

import ds.graph.MinSpanningTree;
import ds.graph.problem.MinSpanningTreeProblem;
import de.tu_berlin.math.coga.common.algorithm.Algorithm; 
import ds.graph.Node;
import java.util.Random;
import ds.graph.Edge;
import de.tu_berlin.math.coga.zet.NetworkFlowModel;
import ds.graph.DynamicNetwork;
import ds.graph.Graph;
import ds.graph.IdentifiableCollection;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.ListSequence;

/**
 *
 * @author schwengf
 */
public class PrimsAlgo extends Algorithm<MinSpanningTreeProblem,MinSpanningTree> {
    
    
    IdentifiableIntegerMapping<Edge> currentEdgesTransit;
    IdentifiableCollection<Edge> solEdges = new ListSequence<Edge>();    
    IdentifiableCollection<Node> solNodes = new ListSequence<Node>();
    IdentifiableCollection<Edge> currentEdges = new ListSequence<Edge>();
    IdentifiableCollection<Edge> remaincurrentEdges = new ListSequence<Edge>();
    IdentifiableCollection<Edge> edges = new ListSequence<Edge>();
    Edge MinEdge;
    Node startNode;
    Node endNode;
    Node currentNode;
    int Transit;
    int i;
    int overalldist=0;
    int Min = 100000;
    NetworkFlowModel OriginNetwork;
    Graph OriginGraph;
    DynamicNetwork neu;
    int NumEdges = 0;
    
    @Override
    public MinSpanningTree runAlgorithm(MinSpanningTreeProblem minspan)
    {
        
        try{
        //Holt das Networkflowmodel, das nach dem Konvertieren erstellt wird
        OriginNetwork = minspan.getGraph(); 
        //Holt zugehoerigen Graphen dazu
        OriginGraph = OriginNetwork.getGraph();
        int numNodes = OriginGraph.numberOfNodes();
        //kalkuliert Distanzen der einzelnen Knoten
        IdentifiableIntegerMapping<Edge> TransitForEdge = OriginNetwork.getTransitTimes();
     
        //gibt zufaellig einen Startknoten wider
        Random r = new Random();
        int num = 1 + Math.abs(r.nextInt()) % numNodes;
  
        //Zufallszahl beginnt erst bei 1, sodass nicht die Supersenke gefunden werden kann
        startNode = OriginGraph.getNode(num);
        
        /*if (startNode != OriginNetwork.getSupersink())
        {
            currentNode = startNode;
        }
        else
        {
            startNode = OriginGraph.getNode(num+1);
            currentNode = startNode;
        }*/
        
        solNodes.add(startNode);
        edges = OriginGraph.edges();
        
        while (solNodes.size() < OriginGraph.numberOfNodes()+1)
        {
            for (Edge test: edges)
            {
                if (solNodes.contains(test.end()) ^ solNodes.contains(test.start()))
                {
                    //currentEdges.add(test);
                    if (TransitForEdge.get(test) < Min)
                    {
                        MinEdge = test;
                    }
                    /*if (TransitForEdge.get(test) == TransitForEdge.minimum(edges))
                    {
                        MinEdge = test;
                    }*/
                }
            }
            
            Edge edge = new Edge(NumEdges++,MinEdge.start(),MinEdge.end());
            //overalldist = overalldist + TransitForEdge.get(MinEdge);
            solEdges.add(edge);
            if (solNodes.contains(MinEdge.start()))
            {
               solNodes.add(MinEdge.end()); 
            }
            else
            {
                solNodes.add(MinEdge.start());
            }
            edges.remove(edge);
            
        }
       
        }
        catch(Exception e) {
             System.out.println("Fehler in runMinSpan " + e.toString());
         }
        //System.out.println("Overalldistance " + overalldist);
        return new MinSpanningTree(minspan,solEdges,overalldist);
       
 
        
    }
    
    
}
