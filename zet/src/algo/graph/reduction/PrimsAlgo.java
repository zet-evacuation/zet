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
import ds.graph.IdentifiableObjectMapping;
import ds.graph.ListSequence;
import ds.graph.MinHeap;
import java.util.PriorityQueue;


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
    Edge supersinkedge;
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
    //Zur Implementation der Priority Queue...
    IdentifiableIntegerMapping<Node> distances;
    IdentifiableObjectMapping<Node, Edge> heapedges;
    
    @Override
    public MinSpanningTree runAlgorithm(MinSpanningTreeProblem minspan)
    {
        
        try{
        //Holt das Networkflowmodel, das nach dem Konvertieren erstellt wird
        OriginNetwork = minspan.getGraph(); 
        Node supersink = minspan.getGraph().getSupersink();
        //Holt zugehoerigen Graphen dazu
        OriginGraph = OriginNetwork.getGraph();
        int numNodes = OriginGraph.numberOfNodes();
        //kalkuliert Distanzen der einzelnen Knoten
        IdentifiableIntegerMapping<Edge> TransitForEdge = OriginNetwork.getTransitTimes();
     
        //gibt zufaellig einen Startknoten wider, Zufallszahl beginnend bei 1, sodass nie
        //Supersenke gewaehlt werden kann 
        Random r = new Random();
        r.setSeed(100000);
        int num = 1 + Math.abs(r.nextInt()) % numNodes;
        
        System.out.println("Startknoten: " + num  );
        startNode = OriginGraph.getNode(num);
        solNodes.add(startNode);
         
        distances = new IdentifiableIntegerMapping<Node>(OriginNetwork.getGraph().numberOfNodes());
        heapedges = new IdentifiableObjectMapping<Node, Edge>(OriginNetwork.getGraph().numberOfEdges(), Edge.class);
        MinHeap<Node, Integer> queue = new MinHeap<Node, Integer>(OriginNetwork.getGraph().numberOfNodes());
        IdentifiableCollection<Edge> incidentEdges;
        
        for (Node node: OriginNetwork.getGraph().nodes())
        {
            if (node != supersink)
            {
                distances.add(node, Integer.MAX_VALUE);
                heapedges.set(node, null);
            }
        }
        
        distances.set(startNode, 0);
        System.out.println("done");
        queue.insert(startNode, 0);
        
        
        while (!queue.isEmpty())
        {
            MinHeap<Node, Integer>.Element min = queue.extractMin();
            Node v = min.getObject();
            solNodes.add(v);
            distances.set(v, Integer.MIN_VALUE);
            
            //Supersenke nicht betrachten...
            if (v != startNode)
            {
                Edge edge = new Edge(NumEdges++,heapedges.get(v).start(),heapedges.get(v).end());
                //fuege nur Kanten hinzu, die nicht zur Supersenke adjazent sind
                if (heapedges.get(v).start() != supersink && heapedges.get(v).end() != supersink)
                {
                    solEdges.add(edge);
                }
            }
            incidentEdges = OriginNetwork.getGraph().incidentEdges(v);
            for (Edge edge: incidentEdges)
            {
                Node w = edge.opposite(v);
                if (distances.get(w) == Integer.MAX_VALUE)
                {
                    distances.set(w, TransitForEdge.get(edge));
                    heapedges.set(w, edge);
                    queue.insert(w,distances.get(w));
                }
                else
                {
                    if (TransitForEdge.get(edge) < distances.get(w))
                    {
                        distances.set(w, TransitForEdge.get(edge));
                        queue.decreasePriority(w, TransitForEdge.get(edge));
                        heapedges.set(w, edge);
                    }
                }
                
            }
        }
        
        IdentifiableCollection<Edge> addEdges = OriginNetwork.getGraph().incidentEdges(supersink);
        for (Edge edge: addEdges)
        {
            supersinkedge = new Edge(NumEdges++, edge.start(), edge.end());
            solEdges.add(supersinkedge);
        }
        
               //2. langsamere Implementation
       
        /*currentNode = startNode;
        currentEdges = OriginNetwork.getGraph().incidentEdges(startNode);
        
        while (solNodes.size() < OriginGraph.numberOfNodes()+1)
        {
            for (Edge edge: currentEdges)
            {
                if (solNodes.contains(edge.start()) ^ solNodes.contains(edge.end()))
                {
                    if (TransitForEdge.get(edge) < Min)
                    {
                        MinEdge = edge;
                    }
                }

            }
            Edge edge = new Edge(NumEdges++,MinEdge.start(),MinEdge.end());
            solEdges.add(edge);
            if (solNodes.contains(MinEdge.start()))
            {
               solNodes.add(MinEdge.end()); 
               //currentNode = MinEdge.end();
               for (Edge neu: OriginNetwork.getGraph().incidentEdges(MinEdge.end()))
               {
                    currentEdges.add(neu);
               }
            }
            else
            {
                solNodes.add(MinEdge.start());
                //currentNode = MinEdge.start();
                for (Edge neu: OriginNetwork.getGraph().incidentEdges(MinEdge.start()))
               {
                    currentEdges.add(neu);
               }
            }
           
        }*/
        
        }
        catch(Exception e) {
             System.out.println("Fehler in runMinSpan " + e.toString());
         }
        //System.out.println("Overalldistance " + overalldist);
        return new MinSpanningTree(minspan,solEdges,overalldist);
       
 
        
    }
    
    
}
