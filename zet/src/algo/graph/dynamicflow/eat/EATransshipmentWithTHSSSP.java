package algo.graph.dynamicflow.eat;

import algo.graph.dynamicflow.*;
import algo.graph.staticflow.mincost.SuccessiveShortestPath;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;
import ds.graph.TimeExpandedNetwork;

public class EATransshipmentWithTHSSSP extends
		TransshipmentWithTimeHorizon {

	public EATransshipmentWithTHSSSP(Network network, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Edge> edgeCapacities, IdentifiableIntegerMapping<Node> nodeCapacities, IdentifiableIntegerMapping<Node> supplies, Integer timeHorizon){
		super(network, transitTimes, edgeCapacities, nodeCapacities, supplies, timeHorizon, "Earliest Arrival Transshipment TH SSSP");
	}
	
	@Override
	protected IdentifiableIntegerMapping<Edge> transshipmentWithTimeHorizon(TimeExpandedNetwork  tnetwork){
		SuccessiveShortestPath sssp = new SuccessiveShortestPath( tnetwork, tnetwork.supplies(), tnetwork.capacities(), tnetwork.costs() );
		sssp.run();
		return sssp.getFlow();
	}


}
