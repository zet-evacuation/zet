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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algo.graph;

import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Martin Groß
 */
public class MaxFlowOverTimeTest {

    public MaxFlowOverTimeTest() {
    }

    @Test
    public void run() {
        Network network = new Network(9, 10);
        Node source1 = network.getNode(0);
        Node source2 = network.getNode(1);
        Node source3 = network.getNode(4);
        Node sink1 = network.getNode(5);
        Node sink2 = network.getNode(6);
        Node sink3 = network.getNode(8);
        Node a = network.getNode(2);
        Node b = network.getNode(3);
        Node c = network.getNode(7);
        Edge e = new Edge(0, source1, a);
        Edge f = new Edge(1, source1, b);
        Edge g = new Edge(2, source2, a);
        Edge h = new Edge(3, source2, source3);
        Edge i = new Edge(4, a, b);
        Edge j = new Edge(5, a, source3);
        Edge k = new Edge(6, b, sink1);
        Edge l = new Edge(7, b, sink2);
        Edge m = new Edge(8, source3, c);
        Edge n = new Edge(9, sink2, sink3);
        network.setEdge(e);
        network.setEdge(f);
        network.setEdge(g);
        network.setEdge(h);
        network.setEdge(i);
        network.setEdge(j);
        network.setEdge(k);
        network.setEdge(l);
        network.setEdge(m);
        network.setEdge(n);
        IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<Edge>(network.numberOfEdges());
        capacities.set(e, 1);
        capacities.set(f, 1);
        capacities.set(g, 2);
        capacities.set(h, 2);
        capacities.set(i, 3);
        capacities.set(j, 3);
        capacities.set(k, 1);
        capacities.set(l, 2);
        capacities.set(m, 1);
        capacities.set(n, 1);
        IdentifiableIntegerMapping<Edge> transitTimes = new IdentifiableIntegerMapping<Edge>(network.numberOfEdges());
        transitTimes.set(e, 1);
        transitTimes.set(f, 1);
        transitTimes.set(g, 2);
        transitTimes.set(h, 2);
        transitTimes.set(i, 3);
        transitTimes.set(j, 3);
        transitTimes.set(k, 1);
        transitTimes.set(l, 2);
        transitTimes.set(m, 1);
        transitTimes.set(n, 1);
        List<Node> sources = new LinkedList<Node>();
        sources.add(source1);
        sources.add(source2);
        sources.add(source3);
        List<Node> sinks = new LinkedList<Node>();
        sinks.add(sink1);
        sinks.add(sink2);
        sinks.add(sink3);
        
        //MaxFlowOverTime algo = new MaxFlowOverTime (network, capacities, sinks, sources, 25, transitTimes);
        //algo.run();        
    }
}