/*
 * SuccessiveShortestPath.java
 *
 */
package algo.graph.staticflow.mincost;

import algo.graph.traverse.*;
import ds.graph.Path;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.ResidualNetwork;
import ds.graph.TimeExpandedNetwork;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import algo.graph.shortestpath.MooreBellmanFord;

/**
 *
 * @author Martin Groß
 */
public class SuccessiveShortestPath {

    private static final Logger LOGGER = Logger.getLogger("fv.model.algorithm.SuccessiveShortestPath");
    private Network network;
    private IdentifiableIntegerMapping<Node> baseBalances;
    private IdentifiableIntegerMapping<Edge> capacities;
    private IdentifiableIntegerMapping<Edge> baseCosts;
    private boolean bFlowExists = false;
    private IdentifiableIntegerMapping<Edge> flow;
    private List<Path> paths;
    private boolean bounds;
    private transient IdentifiableIntegerMapping<Node> balances;
    private transient IdentifiableIntegerMapping<Edge> costs;
    private transient ResidualNetwork residualNetwork;

    public SuccessiveShortestPath(Network network, IdentifiableIntegerMapping<Node> balances, IdentifiableIntegerMapping<Edge> capacities, IdentifiableIntegerMapping<Edge> costs) {
        this.network = network;
        this.baseBalances = balances;
        this.baseCosts = costs;
        this.capacities = capacities;
    }

    public SuccessiveShortestPath(Network network, IdentifiableIntegerMapping<Node> balances, IdentifiableIntegerMapping<Edge> capacities, IdentifiableIntegerMapping<Edge> costs, boolean bounds) {
        this.network = network;
        this.baseBalances = balances;
        this.baseCosts = costs;
        this.capacities = capacities;
        this.bounds = bounds;
    }

    public int balance(Node node) {
        return balances.get(node);
    }

    public int capacity(Edge edge) {
        return residualNetwork.residualCapacities().get(edge);
    }

    public int cost(Edge edge) {
        return costs.get(edge);
    }

    public IdentifiableIntegerMapping<Edge> getFlow() {
        return flow;
    }

    public boolean bFlowExists() {
        return bFlowExists;
    }

    public List<Path> getPaths() {
        return paths;
    }

    private boolean existsPathBetween(Network network, Node start, Node end) {
        BFS bfs = new BFS(network);
        bfs.run(start, end);
        return bfs.distance(end) < Integer.MAX_VALUE;
    }

    public void run() {
        // Create the residual graph
        residualNetwork = new ResidualNetwork(network, capacities);
        // Create a copy of the balance map since we are going to modify it
        balances = new IdentifiableIntegerMapping<Node>(baseBalances);
        // Extend the costs to the residual graph
        costs = new IdentifiableIntegerMapping<Edge>(network.edges());
        // Prepare the path lists
        paths = new LinkedList<Path>();
        for (Edge edge : network.edges()) {
            costs.set(edge, baseCosts.get(edge));
            costs.set(residualNetwork.reverseEdge(edge), -baseCosts.get(edge));
            if (baseCosts.get(edge) < 0) {
                int capacity = capacities.get(edge);
                residualNetwork.augmentFlow(edge, capacity);
                balances.decrease(edge.start(), capacity);
                balances.increase(edge.end(), capacity);
            }
        }
        // Guarantee conservative costs by saturating negative cost edges
        for (Edge edge : residualNetwork.edges()) {
            if (cost(edge) < 0) {
                residualNetwork.augmentFlow(edge, capacity(edge));
                balances.decrease(edge.start(), capacity(edge));
                balances.increase(edge.end(), capacity(edge));
            }
        }
        while (true) {
            // Pick a feasible source-sink pair
            Node source = null;
            Node sink = null;
            for (Node node : network.nodes()) {
                if (balance(node) > 0) {
                    source = node;
                    break;
                }
            }
            if (source != null) {
                for (Node node : network.nodes()) {
                    if (balance(node) < 0 && existsPathBetween(residualNetwork, source, node)) {
                        sink = node;
                        break;
                    }
                }
            }
            // If there are no sources and sinks left, we are done
            if (source == null && (sink == null || bounds)) {
                flow = residualNetwork.flow();
                bFlowExists = true;
                return;
            // If there are only sources or sinks left, there is no b-flow
            } else if (source == null && !bounds && sink != null || source != null && sink == null) {
                bFlowExists = false;
                return;
            }
            // Find a cost minimal source-sink-path
            MooreBellmanFord mbf = new MooreBellmanFord(residualNetwork, costs, source);
            mbf.run();
            Path shortestPath = mbf.getShortestPath(sink);
            paths.add(shortestPath);
            LOGGER.finest("Der kürzeste Pfad ist " + shortestPath);
            // Augment flow along this shortest path
            int amount = Math.min(balance(source), -balance(sink));
            for (Edge edge : shortestPath) {
                if (capacity(edge) < amount) {
                    amount = capacity(edge);
                }
            }
            LOGGER.finest("Augmentiere " + amount + " Flusseinheiten.");
            balances.decrease(source, amount);
            balances.increase(sink, amount);
            LOGGER.finest("Es warten " + balances.get(source) + " Flusseinheiten in der Quelle, w�hrend " + balances.get(sink) + " Flusseinheiten in der Senke ben�tigt werden.");
            for (Edge edge : shortestPath) {
                residualNetwork.augmentFlow(edge, amount);
            }
        }
    }

    public static void main(String[] args) {
        Network network = new Network(4, 5);
        Node source = network.getNode(0);
        Node a = network.getNode(1);
        Node b = network.getNode(2);
        Node sink = network.getNode(3);
        Edge e1 = new Edge(0, source, a);
        Edge e2 = new Edge(1, source, b);
        Edge e3 = new Edge(2, a, b);
        Edge e4 = new Edge(3, a, sink);
        Edge e5 = new Edge(4, b, sink);
        network.setEdge(e1);
        network.setEdge(e2);
        network.setEdge(e3);
        network.setEdge(e4);
        network.setEdge(e5);
        IdentifiableIntegerMapping<Node> balances;
        IdentifiableIntegerMapping<Edge> capacities = new IdentifiableIntegerMapping<Edge>(network.edges());
        capacities.set(e1, 1);
        capacities.set(e2, 1);
        capacities.set(e3, 1);
        capacities.set(e4, 1);
        capacities.set(e5, 1);
        IdentifiableIntegerMapping<Edge> costs = new IdentifiableIntegerMapping<Edge>(network.edges());
        costs.set(e1, 1);
        costs.set(e2, 3);
        costs.set(e3, 1);
        costs.set(e4, 3);
        costs.set(e5, 1);
        TimeExpandedNetwork teg = new TimeExpandedNetwork(network, capacities, costs, source, sink, 8, true);
        balances = new IdentifiableIntegerMapping<Node>(teg.nodes());
        balances.set(teg.singleSource(), 10);
        balances.set(teg.singleSink(), -10);
        SuccessiveShortestPath algo = new SuccessiveShortestPath(teg, balances, teg.capacities(), teg.costs());
        algo.run();
        //System.out.println(algo.getFlow());
        //System.out.println(algo.getPaths());
        for (Path path : algo.getPaths()) {
            //System.out.println(path.toString());
        }
    }
}
