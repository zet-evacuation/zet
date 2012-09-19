/**
 * DynamicTransshipmentProblem.java
 * Created: 05.06.2012, 17:22:29
 */
package algo.graph.dynamicflow;

import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.network.AbstractNetwork;
import ds.mapping.IdentifiableIntegerMapping;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class DynamicTransshipmentProblem extends DynamicFlowProblem {
	protected IdentifiableIntegerMapping<Node> nodeCapacities;
	protected IdentifiableIntegerMapping<Node> supplies;
	private int timeHorizon;

	public DynamicTransshipmentProblem( IdentifiableIntegerMapping<Edge> edgeCapacities, AbstractNetwork network, IdentifiableIntegerMapping<Node> nodeCapacities, int timeHorizon, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Node> supplies ) {
		super( edgeCapacities, network, transitTimes );
		this.nodeCapacities = nodeCapacities;
		this.supplies = supplies;
		this.timeHorizon = timeHorizon;
		//  this.transitTimes = transitTimes;
//        for (Node source : sources) {
//            totalSupplies += supplies.get(source);
//        }
	}

	public int getTimeHorizon() {
		return timeHorizon;
	}

	/**
	 * Sets a new time horizon for the instance. Use this if a time horizon
	 * has changed, for example if an estimator has been used.
	 * @param timeHorizon the new time horizon
	 */
	public void setTimeHorizon( int timeHorizon ) {
		this.timeHorizon = timeHorizon;
	}

	public IdentifiableIntegerMapping<Node> getNodeCapacities() {
		return nodeCapacities;
	}

	public IdentifiableIntegerMapping<Node> getSupplies() {
		return supplies;
	}
}