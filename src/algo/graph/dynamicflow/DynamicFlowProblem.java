/*
 * DynamicFlowProblem.java
 *
 */

package algo.graph.dynamicflow;

import ds.graph.Edge;
import ds.mapping.IdentifiableIntegerMapping;
import ds.graph.network.AbstractNetwork;

/**
 *
 * @author Martin Groß
 */
public class DynamicFlowProblem {

    private IdentifiableIntegerMapping<Edge> capacities;
    private AbstractNetwork network;
    private IdentifiableIntegerMapping<Edge> transitTimes;

    public IdentifiableIntegerMapping<Edge> getCapacities() {
        return capacities;
    }

    public void setCapacities(IdentifiableIntegerMapping<Edge> capacities) {
        this.capacities = capacities;
    }

    public AbstractNetwork getNetwork() {
        return network;
    }

    public void setNetwork(AbstractNetwork network) {
        this.network = network;
    }

    public IdentifiableIntegerMapping<Edge> getTransitTimes() {
        return transitTimes;
    }

    public void setTransitTimes(IdentifiableIntegerMapping<Edge> transitTimes) {
        this.transitTimes = transitTimes;
    }

}
