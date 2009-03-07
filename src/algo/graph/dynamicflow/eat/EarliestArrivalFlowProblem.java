/*
 * EarliestArrivalFlowProblem.java
 *
 */
package algo.graph.dynamicflow.eat;

import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Martin Groß
 */
public class EarliestArrivalFlowProblem {

    private IdentifiableIntegerMapping<Edge> edgeCapacities;
    private Network network;
    private IdentifiableIntegerMapping<Node> nodeCapacities;
    private Node sink;
    private List<Node> sources;
    private IdentifiableIntegerMapping<Node> supplies;
    private int timeHorizon;
    private int totalSupplies;
    private IdentifiableIntegerMapping<Edge> transitTimes;
    
    public EarliestArrivalFlowProblem(IdentifiableIntegerMapping<Edge> edgeCapacities, Network network, IdentifiableIntegerMapping<Node> nodeCapacities, Node sink, List<Node> sources, int timeHorizon, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Node> supplies) {
        this.edgeCapacities = edgeCapacities;
        this.network = network;
        this.nodeCapacities = nodeCapacities;
        this.sink = sink;
        this.sources = sources;
        this.supplies = supplies;
        this.timeHorizon = timeHorizon;
        this.transitTimes = transitTimes;
        for (Node source : sources) {
            totalSupplies += supplies.get(source);
        }
    }
    
    public IdentifiableIntegerMapping<Edge> getEdgeCapacities() {
        return edgeCapacities;
    }

    public Network getNetwork() {
        return network;
    }

    public IdentifiableIntegerMapping<Node> getNodeCapacities() {
        return nodeCapacities;
    }

    public Node getSink() {
        return sink;
    }

    public List<Node> getSources() {
        return sources;
    }

    public IdentifiableIntegerMapping<Node> getSupplies() {
        return supplies;
    }    
    
    public int getTimeHorizon() {
        return timeHorizon;
    }

    public int getTotalSupplies() {
        return totalSupplies;
    }

    public IdentifiableIntegerMapping<Edge> getTransitTimes() {
        return transitTimes;
    }
}
