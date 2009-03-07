package algo.graph.dynamicflow;

import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;

/**
 * The class <code>QuickestTransshipment</code> calculates the smallest
 * time horizon T such that it is
 * possible to fulfill given supplies and demands in a network
 * with capacities and transit times within this time horizon. 
 * A corresponding flow that satisfies the supplies and
 * demands is also computed.
 * 
 * The algorithm to find the so called quickest transshipment 
 * is implemented by using binary search and calling a dynamic transshipment
 * algorithm at each point of the search. Therefore
 * the classes {@link DynamicTransshipment} is used.
 * The class <code>Transshipment</code> implements the binary search
 * for an arbitrary dynamic transshipment algorithm which possibly guarantees more properties.
 */
public class QuickestTransshipment extends Transshipment<DynamicTransshipment> {

	/** 
	 * Creates a new quickest transshipment algorithm instance.
	 */
	public QuickestTransshipment(Network network, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Edge> capacities, IdentifiableIntegerMapping<Node> supplies){
		super(network, transitTimes, capacities, null, supplies, DynamicTransshipment.class, null);
	}
	
/*    *//**
     * A static method to compute a quickest transshipment in a given network.
     * Should always return a transshipment, otherwise a bug occurred.
     * @param network The given network.
	 * @param transitTimes The transit times for all edges in the network.
	 * @param edgeCapacities The capacities of all edges in the network.
	 * @param supplies Supplies and demands of all nodes in the network.
     * @return A quickest transshipment fulfilling all supplies and demands.
     *//*
	public static DynamicFlow compute(Network network,
			IdentifiableIntegerMapping<Edge> transitTimes,
			IdentifiableIntegerMapping<Edge> edgeCapacities,
			IdentifiableIntegerMapping<Node> supplies) {
		QuickestTransshipment qt = QuickestTransshipment.getInstance();
		return qt.computeNonStatic(network, transitTimes, edgeCapacities,
				supplies);
	}

    *//**
     * A method to compute a quickest transshipment in a given network.
     * Should always return a transshipment, otherwise a bug occurred.
     * @param network The given network.
	 * @param transitTimes The transit times for all edges in the network.
	 * @param edgeCapacities The capacities of all edges in the network.
	 * @param supplies Supplies and demands of all nodes in the network.
     * @return A quickest transshipment fulfilling all supplies and demands.
     *//*
	public DynamicFlow computeNonStatic(Network network,
			IdentifiableIntegerMapping<Edge> transitTimes,
			IdentifiableIntegerMapping<Edge> edgeCapacities,
			IdentifiableIntegerMapping<Node> supplies) {

		DynamicTransshipment algo = DynamicTransshipment.getInstance();
		return computeNonStatic(network, transitTimes, edgeCapacities,
				supplies, algo);
	}*/
}