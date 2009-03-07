/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algo.graph.staticflow;

import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Martin
 */
public class StaticTransshipmentTest {

    public StaticTransshipmentTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void getBalanceOfNode() {
    }

    @Test
    public void run() {
    }

    @Test
    public void getFlow() {
    }

    @Test
    public void getFlowEvenIfNotFeasible() {
    }

    @Test
    public void getFlowValueEvenIfInfeasibleFlow() {
    }

    @Test
    public void getResidualNetwork() {
    }

    @Test
    public void main() {
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
        IdentifiableIntegerMapping<Node> balances = new IdentifiableIntegerMapping<Node>(network.numberOfNodes());
        balances.set(source1, 2);
        balances.set(source2, 1);
        balances.set(source3, 0);
        balances.set(sink1, -1);
        balances.set(sink2, -2);
        balances.set(sink3, 0);
        StaticTransshipment algo = new StaticTransshipment(network, capacities, balances);
        algo.run();
        System.out.println(algo.getResidualNetwork());
        System.out.println(algo.getFlow());        
    }

}