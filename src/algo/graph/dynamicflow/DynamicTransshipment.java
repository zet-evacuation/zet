package algo.graph.dynamicflow;

import algo.graph.staticflow.StaticTransshipment;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.TimeExpandedNetwork;

/**
 * The class <code>DynamicTransshipment</code> tests whether it is
 * possible to fulfill given supplies and demands in a network
 * with capacities and transit times within a given time horizon. 
 * If this is possible, a flow that satisfies the supplies and
 * demands is computed.
 * 
 * The algorithm to find the dynamic transshipment (or to say that
 * non exists) is implemented by calling a static transshipment
 * algorithm on the time-expanded network. Therefore
 * the classes {@link TimeExpandedNetwork} and {@link StaticTransshipment}
 * are used.
 */
public class DynamicTransshipment extends TransshipmentWithTimeHorizon {
	
	/**
	 * Creates a new instance of the dynamic transshipment algorithm by calling the super constructor and setting the
	 * name of the algorithm to "Dynamic Transshipment".
	 * @param network The network the dynamic transshipment algorithm shall work on.
	 * @param transitTimes The transit times the dynamic transshipment algorithm shall use.
	 * @param edgeCapacities The edge capacities the dynamic transshipment algorithm shall use.
	 * @param supplies The supplies the dynamic transshipment algorithm shall use.
	 * @param timeHorizon The time horizon for the wanted dynamic transshipment.
	 */
	public DynamicTransshipment(Network network, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Edge> edgeCapacities, IdentifiableIntegerMapping<Node> nodeCapacities, IdentifiableIntegerMapping<Node> supplies, Integer timeHorizon){
		super(network, transitTimes, edgeCapacities, nodeCapacities, supplies, timeHorizon, "Dynamic Transshipment");
	}
	
	@Override
	protected IdentifiableIntegerMapping<Edge> transshipmentWithTimeHorizon(TimeExpandedNetwork  tnetwork){
		// Create an static transshipment algorithm object with the time expanded network, its capacities and supplies / demands.
		StaticTransshipment statTrAlgo = new StaticTransshipment(tnetwork, tnetwork.capacities(), tnetwork.supplies());
		// Run algorithm and get resulting flow.
		statTrAlgo.run();
		IdentifiableIntegerMapping<Edge> flow = statTrAlgo.getFlow();
		return flow;
	}

}
