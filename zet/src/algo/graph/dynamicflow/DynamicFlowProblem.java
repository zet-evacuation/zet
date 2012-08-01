/*
 * DynamicFlowProblem.java
 *
 */

package algo.graph.dynamicflow;

import ds.graph.Edge;
import ds.graph.network.AbstractNetwork;
import ds.mapping.IdentifiableIntegerMapping;

/**
 *
 * @author Martin Groß
 */
public class DynamicFlowProblem {

    protected IdentifiableIntegerMapping<Edge> edgeCapacities;
    private AbstractNetwork network;
    protected IdentifiableIntegerMapping<Edge> transitTimes;

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
