/* zet evacuation tool copyright © 2007-20 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package algo.graph.spanningtree;

import org.zetool.common.algorithm.AbstractAlgorithm;
import org.zetool.graph.Edge;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.container.mapping.IdentifiableObjectMapping;
import org.zetool.container.collection.ListSequence;
import org.zetool.container.priority.MinHeap;
import org.zetool.graph.DynamicNetwork;
import org.zetool.graph.Node;
import java.util.Random;

/**
 *
 * @author Marlen Schwengfelder
 */
public class PrimForNetwork extends AbstractAlgorithm<NetworkMSTProblem, NetworkMST>{
    
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
