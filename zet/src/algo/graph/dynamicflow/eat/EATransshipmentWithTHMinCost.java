package algo.graph.dynamicflow.eat;

import algo.graph.dynamicflow.*;
import algo.graph.staticflow.mincost.MinimumMeanCycleCancelling;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.TimeExpandedNetwork;

public class EATransshipmentWithTHMinCost extends
		TransshipmentWithTimeHorizon {

	public EATransshipmentWithTHMinCost(Network network, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Edge> edgeCapacities, IdentifiableIntegerMapping<Node> nodeCapacities, IdentifiableIntegerMapping<Node> supplies, Integer timeHorizon){
		super(network, transitTimes, edgeCapacities, nodeCapacities, supplies, timeHorizon, "Earliest Arrival Transshipment TH MinCost");
	}
	
	@Override
	protected IdentifiableIntegerMapping<Edge> transshipmentWithTimeHorizon(TimeExpandedNetwork  tnetwork){
		 return MinimumMeanCycleCancelling.compute(tnetwork, tnetwork.capacities(), tnetwork.costs(), tnetwork.supplies());
	}


}
