/*
 * TransshipmentProblem.java
 *
 */

package algo.graph.staticflow;

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
public class TransshipmentProblem {

    private IdentifiableIntegerMapping<Edge> capacities;
    private Network network;    
    private IdentifiableIntegerMapping<Node> supplies;
    private transient List<Node> sinks;
    private transient List<Node> sources;

    public TransshipmentProblem(Network network, IdentifiableIntegerMapping<Edge> capacities, IdentifiableIntegerMapping<Node> supplies) {
        this.capacities = capacities;
        this.network = network;
        this.supplies = supplies;
        sources = new LinkedList<Node>();
        sinks = new LinkedList<Node>();
        for (Node node : network.nodes()) {
            if (supplies.get(node) < 0) {
                sinks.add(node);
            }
            if (supplies.get(node) > 0) {
                sources.add(node);
            }
        }        
    }

    public IdentifiableIntegerMapping<Edge> getCapacities() {
        return capacities;
    }

    public void setCapacities(IdentifiableIntegerMapping<Edge> capacities) {
        this.capacities = capacities;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }
    
    public List<Node> getSinks() {
        return sinks;
    }
    
    public List<Node> getSources() {
        return sources;
    }

    public IdentifiableIntegerMapping<Node> getSupplies() {
        return supplies;
    }

    public void setSupplies(IdentifiableIntegerMapping<Node> supplies) {
        this.supplies = supplies;
    }    
}
