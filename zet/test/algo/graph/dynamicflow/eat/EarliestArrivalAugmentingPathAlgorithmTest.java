/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algo.graph.dynamicflow.eat;

import ds.graph.DynamicResidualNetwork;
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
public class EarliestArrivalAugmentingPathAlgorithmTest {

    public EarliestArrivalAugmentingPathAlgorithmTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void runAlgorithm() {
    }

    @Test
    public void main() {
        Network network = new Network(4, 5);
        Node source = network.getNode(0);
        Node a = network.getNode(1);
        Node b = network.getNode(2);
        Node sink = network.getNode(3);
        Edge e = new Edge(0, source, a);
        Edge f = new Edge(1, source, b);
        Edge g = new Edge(2, a, b);
        Edge h = new Edge(3, a, sink);
        Edge i = new Edge(4, b, sink);
        network.setEdge(e);
        network.setEdge(f);
        network.setEdge(g);
        network.setEdge(h);
        network.setEdge(i);
        IdentifiableIntegerMapping<Edge> edgeCapacities = new IdentifiableIntegerMapping<Edge>(network.numberOfEdges());
        edgeCapacities.set(e, 1);
        edgeCapacities.set(f, 1);
        edgeCapacities.set(g, 2);
        edgeCapacities.set(h, 2);
        edgeCapacities.set(i, 3);
        IdentifiableIntegerMapping<Node> nodeCapacities = new IdentifiableIntegerMapping<Node>(network.numberOfNodes());
        nodeCapacities.set(source, Integer.MAX_VALUE);
        nodeCapacities.set(a, 5);
        nodeCapacities.set(b, 5);
        nodeCapacities.set(sink, Integer.MAX_VALUE);
        IdentifiableIntegerMapping<Edge> transitTimes = new IdentifiableIntegerMapping<Edge>(network.numberOfEdges());
        transitTimes.set(e, 1);
        transitTimes.set(f, 1);
        transitTimes.set(g, 2);
        transitTimes.set(h, 2);
        transitTimes.set(i, 3);
        /*
        DynamicResidualNetwork drn = new DynamicResidualNetwork(network, edgeCapacities, nodeCapacities, transitTimes);
        EarliestArrivalAugmentingPathProblem eaapp = new EarliestArrivalAugmentingPathProblem(drn, source, sink, 10);
        EarliestArrivalAugmentingPathAlgorithm algo = new EarliestArrivalAugmentingPathAlgorithm();
        algo.setProblem(eaapp);
        algo.run();
        System.out.println(algo.getSolution());        */
    }

}