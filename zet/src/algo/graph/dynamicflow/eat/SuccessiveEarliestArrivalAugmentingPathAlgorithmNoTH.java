package algo.graph.dynamicflow.eat;

import algo.graph.dynamicflow.*;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;


/**
 * This class calculates an earliest arrival transshipment
 * by using a successive earliest arrival augmenting path algorithm.
 * The optimal time horizon is found as specified in <code>Transshipment</code>. 
 */
public class SuccessiveEarliestArrivalAugmentingPathAlgorithmNoTH extends
		Transshipment<SuccessiveEarliestArrivalAugmentingPathAlgorithmTH> {

	public SuccessiveEarliestArrivalAugmentingPathAlgorithmNoTH(Network network,
			IdentifiableIntegerMapping<Edge> transitTimes,
			IdentifiableIntegerMapping<Edge> edgeCapacities,
			IdentifiableIntegerMapping<Node> nodeCapacities,
			IdentifiableIntegerMapping<Node> supplies) {
		super(network, transitTimes, edgeCapacities, nodeCapacities, supplies, SuccessiveEarliestArrivalAugmentingPathAlgorithmTH.class, null);
	}
	
}
