/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package algo.graph.spanningtree;

import de.tu_berlin.coga.common.algorithm.Algorithm;
import ds.graph.network.DynamicNetwork;
import de.tu_berlin.coga.graph.Edge;
import de.tu_berlin.coga.container.collection.IdentifiableCollection;
import de.tu_berlin.coga.container.mapping.IdentifiableIntegerMapping;
import de.tu_berlin.coga.container.mapping.IdentifiableObjectMapping;
import de.tu_berlin.coga.container.collection.ListSequence;
import de.tu_berlin.coga.container.priority.MinHeap;
import de.tu_berlin.coga.graph.Node;
import java.util.Random;

/**
 *
 * @author schwengf
 */
public class PrimForNetwork extends Algorithm<NetworkMSTProblem, NetworkMST>{
    
    DynamicNetwork OriginNetwork;
    Node startNode;
    IdentifiableCollection<Node> solNodes = new ListSequence<>();
    IdentifiableCollection<Edge> solEdges = new ListSequence<>();  
    IdentifiableIntegerMapping<Node> distances;
    IdentifiableObjectMapping<Node, Edge> heapedges;
    int NumEdge = 0;
    int overalldist = 0;
    
    @Override
    public NetworkMST runAlgorithm(NetworkMSTProblem networkprob)
    {
        
        try{
        OriginNetwork = networkprob.getGraph();
        int numNodes = OriginNetwork.nodeCount();
        
        IdentifiableIntegerMapping<Edge> TransitForEdge = networkprob.getDistances();
     
        //gives a random start node
        Random r = new Random();
        long seed = r.nextLong();
	seed = 5706550742198787144l; // this one creates a chain decomposition error in 3-storey 4-rooms.
	System.out.println( "Spanning Tree Seed: " + seed );
        r.setSeed( seed );
        int num = 0 + Math.abs(r.nextInt()) % numNodes;
        
        if (num != 0)
        {   
            startNode = OriginNetwork.getNode(num);
        }
        else
        {
            startNode = OriginNetwork.getNode(num+1);
        }
        System.out.println("Startknoten: " + startNode);
        solNodes.add(startNode);
        //distances = new IdentifiableIntegerMapping<Node>(OriginNetwork.nodeCount());
        distances = new IdentifiableIntegerMapping<Node>(OriginNetwork.nodeCount());
        heapedges = new IdentifiableObjectMapping<Node, Edge>(OriginNetwork.edgeCount());
        MinHeap<Node, Integer> queue = new MinHeap<Node, Integer>(OriginNetwork.nodeCount());
        IdentifiableCollection<Edge> incidentEdges;
        for (Node node: OriginNetwork.nodes())
        {
                distances.add(node, Integer.MAX_VALUE); 
                heapedges.set(node, null);
        }
        
        distances.set(startNode, 0);
        
        queue.insert(startNode, 0);
        
        
        while (!queue.isEmpty())
        {
            MinHeap<Node, Integer>.Element min = queue.extractMin();
            Node v = min.getObject();
            solNodes.add(v);
            distances.set(v, Integer.MIN_VALUE);
            
            if (v != startNode)
            {
                Edge edge = new Edge(NumEdge++,heapedges.get(v).start(),heapedges.get(v).end());
                //only consider edges that are not incident to supersink
                solEdges.add(edge);
            }
            incidentEdges = OriginNetwork.incidentEdges(v);
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
        
        
        
              
        
        }
        catch(Exception e) {
             System.out.println("Fehler in runMinSpan " + e.toString());
         }
        //System.out.println("Overalldistance " + overalldist);
        return new NetworkMST(networkprob,solEdges,overalldist);
            
    }
    
    
}
