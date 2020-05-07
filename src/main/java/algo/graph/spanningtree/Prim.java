/* zet evacuation tool copyright (c) 2007-20 zet evacuation team
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
import org.zetool.graph.Node;
import java.util.Random;
import org.zetool.graph.Edge;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import org.zetool.graph.Graph;
import org.zetool.container.collection.IdentifiableCollection;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.container.mapping.IdentifiableObjectMapping;
import org.zetool.container.collection.ListSequence;
import org.zetool.container.priority.MinHeap;
import org.zetool.graph.DynamicNetwork;

/**
 *
 * @author Marlen Schwengfelder
 */
public class Prim extends AbstractAlgorithm<MinSpanningTreeProblem,UndirectedTree> {
    
    
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
        int numNodes = OriginGraph.nodeCount();
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
        
        while (solNodes.size() < OriginGraph.nodeCount()+1)
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