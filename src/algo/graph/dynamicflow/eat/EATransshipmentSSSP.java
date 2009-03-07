package algo.graph.dynamicflow.eat;

import algo.graph.dynamicflow.*;
import ds.graph.Edge;
import ds.graph.IdentifiableIntegerMapping;
import ds.graph.Network;
import ds.graph.Node;

/**
 * The class <code>EATransshipment</code> solves two variants
 * of the earliest arrival transshipment problem: with or
 * without a given time horizon.
 * The implementation is done with time-expanded networks.
 * For the variant without a time horizon, binary search
 * is used. 
 */
public class EATransshipmentSSSP extends Transshipment<EATransshipmentWithTHMinCost>{
	
	public EATransshipmentSSSP(Network network, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Edge> capacities, IdentifiableIntegerMapping<Node> supplies){
		super(network, transitTimes, capacities, null, supplies, DynamicTransshipment.class, EATransshipmentWithTHSSSP.class);
	}	
		
}
