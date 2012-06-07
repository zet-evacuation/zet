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

    private IdentifiableIntegerMapping<Edge> edgeCapacities;
    private AbstractNetwork network;
    private IdentifiableIntegerMapping<Edge> transitTimes;

	public DynamicFlowProblem( IdentifiableIntegerMapping<Edge> edgeCapacities, AbstractNetwork network, IdentifiableIntegerMapping<Edge> transitTimes ) {
		this.edgeCapacities = edgeCapacities;
		this.network = network;
		this.transitTimes = transitTimes;
	}
		
		
		public IdentifiableIntegerMapping<Edge> getEdgeCapacities() {
        return edgeCapacities;
    }

//    public void setCapacities(IdentifiableIntegerMapping<Edge> capacities) {
//        this.edgeCapacities = capacities;
//    }

    public AbstractNetwork getNetwork() {
        return network;
    }

//    public void setNetwork(AbstractNetwork network) {
//        this.network = network;
//    }

    public IdentifiableIntegerMapping<Edge> getTransitTimes() {
        return transitTimes;
    }

//    public void setTransitTimes(IdentifiableIntegerMapping<Edge> transitTimes) {
//        this.transitTimes = transitTimes;
//    }

}
