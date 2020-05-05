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
package algo.graph.nashflow;

import org.junit.Test;
import ds.graph.problem.NashFlowProblem;
import ds.graph.flow.NashFlow;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.container.mapping.IdentifiableDoubleMapping;
import org.zetool.graph.DynamicNetwork;

/**
 *
 * @author Sebastian
 */
public class NashFlowAlgoTest {
    

    
  
    @Test
    public void run() {
        DynamicNetwork dnet = new DynamicNetwork();
        Node source = new Node(0);
        Node n1 = new Node(1);
        Node n2 = new Node(2);
        Node n3 = new Node(3);
        Node n4 = new Node(4);
        Node n5 = new Node(5);
        Node n6 = new Node(6);
        Node n7 = new Node(7);
        Node n8 = new Node(8);
        Node n9 = new Node(9);
        Node n10 = new Node(10);
        Node n11 = new Node(11);
        Node sink = new Node(12);
        Edge e0 = new Edge(0, source, n1);
        Edge e1= new Edge(1, source, n2);
        Edge e2= new Edge(2, source, n3);
        Edge e3= new Edge(3, source, n4);
        Edge e4= new Edge(4, n1, n5);
        Edge e5= new Edge(5, n2, n5);
        Edge e6= new Edge(6, n2, n6);
        Edge e7= new Edge(7, n3, n6);
        Edge e8= new Edge(8, n3, n7);
        Edge e9= new Edge(9, n4, n7);
        Edge e10= new Edge(10, n5, n8);
        Edge e11= new Edge(11, n5, n9);
        Edge e12= new Edge(12, n6, n9);
        Edge e13= new Edge(13, n6, n10);
        Edge e14= new Edge(14, n7, n10);
        Edge e15= new Edge(15, n7, n11);
        Edge e16= new Edge(16, n8, sink);
        Edge e17= new Edge(17, n9, sink);
        Edge e18= new Edge(18, n10, sink);
        Edge e19= new Edge(19, n11, sink);
        dnet.setNode(source);
        dnet.setNode(n1);
        dnet.setNode(n2);
        dnet.setNode(n3);
        dnet.setNode(n4);
        dnet.setNode(n5);
        dnet.setNode(n6);
        dnet.setNode(n7);
        dnet.setNode(n8);
        dnet.setNode(n9);
        dnet.setNode(n10);
        dnet.setNode(n11);
        dnet.setNode(sink);
                
        dnet.setEdge(e0);
        dnet.setEdge(e1);
        dnet.setEdge(e2);
        dnet.setEdge(e3);
        dnet.setEdge(e4);
        dnet.setEdge(e5);
        dnet.setEdge(e6);
        dnet.setEdge(e7);
        dnet.setEdge(e8);
        dnet.setEdge(e9);
        dnet.setEdge(e10);
        dnet.setEdge(e11);
        dnet.setEdge(e12);
        dnet.setEdge(e13);
        dnet.setEdge(e14);
        dnet.setEdge(e15);
        dnet.setEdge(e16);
        dnet.setEdge(e17);
        dnet.setEdge(e18);
        dnet.setEdge(e19);
        
        IdentifiableDoubleMapping<Edge> capacities = new IdentifiableDoubleMapping<Edge>(dnet.edgeCount());
        IdentifiableDoubleMapping<Edge> transitTimes = new IdentifiableDoubleMapping<Edge>(dnet.edgeCount());
        for(Edge e: dnet.edges()) {
            capacities.set(e, 2.0);
            transitTimes.set(e, 1.0);
        }
           
        
        System.out.println("NashFlowAlgo");
        NashFlowProblem nashflowprob = new NashFlowProblem(dnet,capacities,transitTimes,source,sink,7.2);
        NashFlowAlgo nashalg = new NashFlowAlgo();
        nashalg.setProblem(nashflowprob);
        nashalg.run();
        NashFlow nashf = nashalg.getSolution();
        
        System.out.println("ENDE - " + nashf.getNodeLabels().toString());
    }

}
