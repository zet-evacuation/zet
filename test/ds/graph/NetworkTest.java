/* zet evacuation tool copyright (c) 2007-09 zet evacuation team
 *
 * This program is free software; you can redistribute it and/or
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package ds.graph;

import java.util.Random;
import static org.junit.Assert.*;
import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.easymock.classextension.EasyMock.*;


/*
 * NetworkTest.java
 * JUnit based test
 *
 * Created on 29. November 2007, 13:05
 */

/**
 *
 * @author mouk
 */
public class NetworkTest
{
    NetworkHook instance;
    Random rand = new Random();
    int nodesCount = 10;
    int edgesCount = 100;
  
    @Before
    public void setUp()
    {
        instance = new NetworkHook(nodesCount,edgesCount);
        assertNotNull("could not initiate", instance);
    }  
    
    @Test
    public void canConstruct()
    {
       Network network;
        
        try
        {
            network = new Network(nodesCount,edgesCount);
        }
        finally {}
        
        
        assertNotNull("could not initiate", network);
         
    }
    
    @Test
    public void canCreateAndSetEdge()
    {
        Edge edge = _generateEdge();
        assertEquals(0,instance.degree(edge.start() ));
        assertEquals(0,instance.outdegree(edge.start() ));
        assertEquals(0,instance.indegree(edge.start() ));
        
        assertEquals(0,instance.degree(edge.end() ));
        assertEquals(0,instance.outdegree(edge.end() ));
        assertEquals(0,instance.indegree(edge.end() ));
        
        instance.setEdge(edge);
        
        
        assertEquals(1,instance.degree(edge.start() ));
        assertEquals(1,instance.outdegree(edge.start() ));
        assertEquals(0,instance.indegree(edge.start() ));
        
        assertEquals(1,instance.degree(edge.end() ));
        assertEquals(0,instance.outdegree(edge.end() ));
        assertEquals(1,instance.indegree(edge.end() ));
    }
    
    
    
    @Test
    public void canCreateAndSetEdgeUsginnonEmptyNetwork()
    {
        Edge[] edges = _populateNetworkWithRandomEdges();
        Edge edge = _generateEdge();
        edge = new Edge(edges.length, edge.start(), edge.end());
        
        int startDegree = instance.degree(edge.start() );
        int startoutdegree = instance.outdegree(edge.start() );
        int startIndegree = instance.indegree(edge.start() );
        
     
        int endDegree = instance.degree(edge.end() ) ;
        int endOutdegree = instance.outdegree(edge.end() );
        int endIndegree = instance.indegree(edge.end() );
        
        
        instance.setEdge(edge);
        
        
        assertEquals(startDegree + 1,instance.degree(edge.start() ));
        assertEquals(startoutdegree +1,instance.outdegree(edge.start() ));
        assertEquals(startIndegree,instance.indegree(edge.start() ));
        
        assertEquals(endDegree+1,instance.degree(edge.end() ));
        assertEquals(endOutdegree,instance.outdegree(edge.end() ));
        assertEquals(endIndegree+1,instance.indegree(edge.end() ));
    }
    
    @Test
    public void canHideEdge()
    {
        Edge[] edges = _populateNetworkWithRandomEdges();
        Edge edge = edges[rand.nextInt(edges.length)];
        
        int startDegree = instance.degree(edge.start() );
        int startoutdegree = instance.outdegree(edge.start() );
        int startIndegree = instance.indegree(edge.start() );
        
     
        int endDegree = instance.degree(edge.end() ) ;
        int endOutdegree = instance.outdegree(edge.end() );
        int endIndegree = instance.indegree(edge.end() );
        

        
        instance.setHidden(edge, true);
        
        
        assertEquals(startDegree - 1,instance.degree(edge.start() ));
        assertEquals(startoutdegree -1,instance.outdegree(edge.start() ));
        assertEquals(startIndegree,instance.indegree(edge.start() ));
        
        assertEquals(endDegree-1,instance.degree(edge.end() ));
        assertEquals(endOutdegree,instance.outdegree(edge.end() ));
        assertEquals(endIndegree-1,instance.indegree(edge.end() ));
    }
    @Test
    public void canHideNode()
    {
        Edge[] edges = _populateNetworkWithRandomEdges();
        Edge edge = edges[8];
        Node node = edge.start();
        
        int endDegree = instance.degree(edge.end());
        int endOutdegree = instance.outdegree(edge.end());
        int EndIndegree = instance.indegree(edge.end() );
        
        
        instance.setHidden(node , true);
        
        assertTrue(instance.isHidden(edge));
        
        assertEquals(endDegree-1,instance.degree(edge.end() ));
        assertEquals(endOutdegree,instance.outdegree(edge.end() ));
        assertEquals(EndIndegree -1,instance.indegree(edge.end() ));
    }
    
    @Test
    public void canAddAnAlredayExsistingEdgeWithoutAffectingDegrees()
    {
        Edge[] edges = _populateNetworkWithRandomEdges();
        Edge edge = edges[rand.nextInt(edges.length)];
        
        int startDegree = instance.degree(edge.start() );
        int startoutdegree = instance.outdegree(edge.start() );
        int startIndegree = instance.indegree(edge.start() );
        
     
        int endDegree = instance.degree(edge.end() ) ;
        int endOutdegree = instance.outdegree(edge.end() );
        int endIndegree = instance.indegree(edge.end() );
        

        //this should change nothing since edge already exsists.
        instance.setEdge(edge);
        
        
        assertEquals(startDegree ,instance.degree(edge.start() ));
        assertEquals(startoutdegree,instance.outdegree(edge.start() ));
        assertEquals(startIndegree,instance.indegree(edge.start() ));
        
        assertEquals(endDegree,instance.degree(edge.end() ));
        assertEquals(endOutdegree,instance.outdegree(edge.end() ));
        assertEquals(endIndegree,instance.indegree(edge.end() ));
    }
    
    @Test
    public void canSetDegreesCorrectly()
    {
        Edge[] edges = _populateNetworkWithRandomEdges();
        int edgeC = edges.length;
        IdentifiableCollection<Node> nodes = instance.nodes();
        int degree=0;
        int indegree = 0 ;
        int outdegree = 0 ;
        
        for(Node node : nodes)
        {
            degree +=  instance.degree(node);
            indegree += instance.indegree(node);
            outdegree += instance.outdegree(node);
        }
        assertEquals(2*edgeC, degree);
        assertEquals(edgeC, indegree);
        assertEquals(edgeC, outdegree);
    }

    @Test
    public void canGenerateAllNodes()
    {
        assertNotNull("could not initiate", instance);
        int i = 0;
        for (Node node : instance.nodes() )
        {
            assertNotNull("Node must be none-null object", node);
            assertEquals("Node has wrong id.", i++,node.id());
        }
        assertEquals(nodesCount, i);
    }
    
    @Test
    public void canClone()
    {
        assertTrue(instance.equals(instance.clone()));
    }
    
    
    private Edge[] _populateNetworkWithRandomEdges()
    {
        int edgesC = 10;
        IdentifiableCollection<Node> nodes = instance.nodes();
        Edge[] edges = new Edge[edgesC];
        for(int i = 0 ; i < 10; i++)
        {
            int firstNode = rand.nextInt(nodesCount);
            int secondNode = -1;
            do
            {
                secondNode = rand.nextInt(nodesCount);
            }while (firstNode==secondNode);
            edges[i] = instance.createAndSetEdge(
                    nodes.get(firstNode), nodes.get(secondNode));
        }
        return edges;
    }
    private Edge _generateEdge()
    {
        IdentifiableCollection<Node> nodes = instance.nodes();
        int firstNodeId = rand.nextInt(nodesCount);
        int secondNodeId = -1;
        do
        {
             secondNodeId = rand.nextInt(nodesCount);
        }while (firstNodeId==secondNodeId);
        Node firstNode = nodes.get(firstNodeId);
        Node secondNode = nodes.get(secondNodeId);
        return new Edge(0,firstNode,secondNode);
    }
    private void _injectHooks()
    {
        HidingSet<Edge> edges = (HidingSet<Edge>) createMock(HidingSet.class);
        instance.setEdges(edges);   
        
        HidingSet<Node> nodes = (HidingSet<Node>) createMock(HidingSet.class);
        instance.setNodes(nodes);   
    }

    
    public static junit.framework.Test suite() {
	return new JUnit4TestAdapter(NetworkTest.class);
    }
}
