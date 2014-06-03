
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


package algo.graph.nashflow;

import org.junit.Test;
import ds.graph.problem.NashFlowProblem;
import ds.graph.flow.NashFlow;

import de.tu_berlin.coga.graph.Edge;
import de.tu_berlin.coga.graph.Node;
import de.tu_berlin.coga.netflow.ds.network.DynamicNetwork;
import de.tu_berlin.coga.container.mapping.IdentifiableDoubleMapping;



/**
 *
 * @author Sebastian
 */
public class NashFlowMultipleSourcesAlgoTest3 {
    

    
  
    @Test
    public void run() {
        DynamicNetwork dnet = new DynamicNetwork();
        Node s1 = new Node(0);
        Node n1 = new Node(1);
        Node n2= new Node(2);
        Node sink = new Node(3);
        Edge e1 = new Edge(0, s1, n1);
        Edge e2= new Edge(1, s1, n2);
        Edge e3= new Edge(2, n1, sink);
        Edge e4= new Edge(3, n2, sink);
        Edge e5= new Edge(4, n1, n2);
        dnet.setNode(s1);
        dnet.setNode(n1);
        dnet.setNode(n2);
        dnet.setNode(sink);
                
        dnet.setEdge(e1);
        dnet.setEdge(e2);
        dnet.setEdge(e3);
        dnet.setEdge(e4);
        dnet.setEdge(e5);
        
        IdentifiableDoubleMapping<Edge> capacities = new IdentifiableDoubleMapping<Edge>(dnet.edgeCount());
        IdentifiableDoubleMapping<Edge> transitTimes = new IdentifiableDoubleMapping<Edge>(dnet.edgeCount());
        capacities.set(e1, 5.4);
        transitTimes.set(e1, 4.5);
        capacities.set(e2, 3.0);
        transitTimes.set(e2, 5.0);
        capacities.set(e3, 3.0);
        transitTimes.set(e3, 12.0);
        capacities.set(e4, 5.0);
        transitTimes.set(e4, 14.0);
        capacities.set(e5, 10.0);
        transitTimes.set(e5, 5.0);
        
        
        System.out.println("NashFlowAlgo");
        NashFlowProblem nashflowprob = new NashFlowProblem(dnet,capacities,transitTimes,s1,sink,true);
        NashFlowAlgo nashalg = new NashFlowAlgo();
        nashalg.setProblem(nashflowprob);
        nashalg.run();
        NashFlow nashf = nashalg.getSolution();
        
        
    }

}
