/*
 * DynamicFlowProblem.java
 *
 */

package algo.graph.dynamicflow;

import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;

/**
 *
 * @author Martin Groß
 */
public class DynamicFlowProblem {

    private IdentifiableIntegerMapping<Edge> capacities;
    private Network network;
    private IdentifiableIntegerMapping<Edge> transitTimes;

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

    public IdentifiableIntegerMapping<Edge> getTransitTimes() {
        return transitTimes;
    }

    public void setTransitTimes(IdentifiableIntegerMapping<Edge> transitTimes) {
        this.transitTimes = transitTimes;
    }

}
