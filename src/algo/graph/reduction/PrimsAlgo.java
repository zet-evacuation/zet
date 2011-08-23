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
import ds.graph.Network;

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
        System.out.println("Anzahl an Knoten " + numNodes);
        //kalkuliert Distanzen der einzelnen Knoten
        IdentifiableIntegerMapping<Edge> TransitForEdge = OriginNetwork.getTransitTimes();
        
        Node[] NumNodes = new Node[OriginGraph.numberOfNodes()];
        for (Node node : OriginGraph.nodes())
        {
            NumNodes[i] = node; 
            i++;
        }
        
        //gibt zufaellig einen Startknoten wider
        Random r = new Random();
        int num = 0 + Math.abs(r.nextInt()) % numNodes;
        System.out.println("Knotennummer " + num);
  
        startNode = NumNodes[num];
        
        if (startNode != OriginNetwork.getSupersink())
        {
            currentNode = startNode;
        }
        else
        {
            startNode = NumNodes[num+1];
            currentNode = startNode;
        }
    
        //boolean insert = solNodes.add(startNode);
        solNodes.add(startNode);
        //System.out.println("Startknoten gefunden " + insert);
 
        while (solNodes.size() < OriginGraph.numberOfNodes()+1)
        {
            edges = OriginGraph.edges();
            for (Edge test: edges)
            {
                if (solNodes.contains(test.end()) ^ solNodes.contains(test.start()))
                {
                    currentEdges.add(test);
                }
            }
            for (Edge test2 : currentEdges)
            {
                if (TransitForEdge.get(test2) < Min)
                {
                    MinEdge = test2;
                }
            }
            Edge edge = new Edge(NumEdges++,MinEdge.start(),MinEdge.end());
            System.out.println("Minimale Kante gefunden ");
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
        
     
        /*while (solNodes != OriginGraph.nodes())
        {
            currentEdges = OriginGraph.incidentEdges( currentNode );
            System.out.println("adjazente Kanten " + currentEdges.size());
            //anderer Endknoten der Kante darf nicht schon in SolNodes enthalten sein
            for (Edge error: currentEdges)
            {
                if (!solNodes.contains(error.opposite(currentNode)))
                {
                    remaincurrentEdges = new ListSequence<Edge>();
                    remaincurrentEdges.add(error);
                }
            }
            System.out.println("verbleibende adjazente Kanten " + currentEdges.size());
            
            for (Edge neu : remaincurrentEdges)
            {
                //holt aktuelle Transitzeit fuer Kante
                Transit = TransitForEdge.get(neu);
                //gibt die Kante mit minimalem Wert zurueck
                if (Transit < Min )
                {
                    MinEdge = neu; 
                }
                
            }
            
            Node anode = currentNode;
            Node bnode = MinEdge.opposite(anode);        
            Edge edge = new Edge(NumEdges++, anode, bnode);
            solEdges.add(edge);
            solNodes.add(bnode);
            overalldist = overalldist + Transit;
            currentNode = bnode;
            //alle Kanten der wieder geloescht
            /*for (Edge edgeN : remaincurrentEdges)
            {
                remaincurrentEdges.remove(edgeN);
            }*/
            System.out.println("1. Runde done ");
           
        
        }
        catch(Exception e) {
             System.out.println("Fehler in runMinSpan " + e.toString());
         }
        System.out.println("Overalldistance " + overalldist);
        System.out.println("Number of Solnodes " + solNodes.size());
        return new MinSpanningTree(minspan,solEdges,overalldist);
       
 
        
    }
    
    
}
