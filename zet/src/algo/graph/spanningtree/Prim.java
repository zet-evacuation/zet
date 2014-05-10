/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.spanningtree;

import de.tu_berlin.coga.common.algorithm.Algorithm; 
import ds.graph.Node;
import java.util.Random;
import ds.graph.Edge;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import ds.graph.network.DynamicNetwork;
import ds.graph.Graph;
import de.tu_berlin.coga.container.collection.IdentifiableCollection;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import de.tu_berlin.coga.container.mapping.IdentifiableObjectMapping;
import de.tu_berlin.coga.container.collection.ListSequence;
import de.tu_berlin.coga.container.priority.MinHeap;


/**
 *
 * @author schwengf
 */
public class Prim extends Algorithm<MinSpanningTreeProblem,UndirectedTree> {
    
    
    IdentifiableIntegerMapping<Edge> currentEdgesTransit;
    IdentifiableCollection<Edge> solEdges = new ListSequence<>();    
    IdentifiableCollection<Node> solNodes = new ListSequence<>();
    IdentifiableCollection<Edge> currentEdges = new ListSequence<>();
    IdentifiableCollection<Edge> remaincurrentEdges = new ListSequence<>();
    IdentifiableCollection<Edge> edges = new ListSequence<>();
    Edge MinEdge;
    Edge supersinkedge;
    Node startNode;
    Node endNode;
    Node currentNode;
    int Transit;
    int i;
    //int overalldist=0;
    int Min = 100000;
    NetworkFlowModel OriginNetwork;
    Graph OriginGraph;
    DynamicNetwork neu;
    int NumEdges = 0;
    IdentifiableIntegerMapping<Node> distances;
    IdentifiableObjectMapping<Node, Edge> heapedges;
    
    @Override
    public UndirectedTree runAlgorithm(MinSpanningTreeProblem minspan)
    {
        
        try{
        OriginNetwork = minspan.getNetworkFlowModel(); 
        Node supersink = minspan.getNetworkFlowModel().getSupersink();
        OriginGraph = OriginNetwork.graph();
        int numNodes = OriginGraph.numberOfNodes();
        IdentifiableIntegerMapping<Edge> TransitForEdge = OriginNetwork.transitTimes();
     
        //gives a random start node
        Random r = new Random();
				long seed = r.nextLong();
				seed = 5706550742198787144l; // this one creates a chain decomposition error in 3-storey 4-rooms.
				//1364865666242639293
				System.out.println( "Spanning Tree Seed: " + seed );
        r.setSeed( seed );
        int num = 0 + Math.abs(r.nextInt()) % numNodes;

        if (num != 0)
        {   
            startNode = OriginGraph.getNode(num);
        }
        else
        {
            startNode = OriginGraph.getNode(num+1);
        }
        System.out.println("Startknoten: " + num);
        solNodes.add(startNode);
         
        distances = new IdentifiableIntegerMapping<Node>(OriginNetwork.numberOfNodes());
        heapedges = new IdentifiableObjectMapping<Node, Edge>(OriginNetwork.numberOfEdges());
        MinHeap<Node, Integer> queue = new MinHeap<Node, Integer>(OriginNetwork.numberOfNodes());
        IdentifiableCollection<Edge> incidentEdges;
        
        for (Node node: OriginNetwork )
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
            
            if (v != startNode)
            {
                Edge edge = new Edge(NumEdges++,heapedges.get(v).start(),heapedges.get(v).end());
                //only consider edges that are not incident to supersink
                if (heapedges.get(v).start() != supersink && heapedges.get(v).end() != supersink)
                {
                    solEdges.add(edge);
                }
            }
            incidentEdges = OriginNetwork.graph().incidentEdges(v);
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
        
        IdentifiableCollection<Edge> addEdges = OriginNetwork.graph().incidentEdges(supersink);
        for (Edge edge: addEdges)
        {
            supersinkedge = new Edge(NumEdges++, edge.start(), edge.end());
            solEdges.add(supersinkedge);
        }
        
               //2. langsamere Implementation
       
        /*currentNode = startNode;
        currentEdges = OriginNetwork.getNetworkFlowModel().incidentEdges(startNode);
        
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
               for (Edge neu: OriginNetwork.getNetworkFlowModel().incidentEdges(MinEdge.end()))
               {
                    currentEdges.add(neu);
               }
            }
            else
            {
                solNodes.add(MinEdge.start());
                //currentNode = MinEdge.start();
                for (Edge neu: OriginNetwork.getNetworkFlowModel().incidentEdges(MinEdge.start()))
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
        return new UndirectedTree( solEdges );
       
 
        
    }
    
    
}
