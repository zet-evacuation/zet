/**
 * FlowVisualization.java
 * Created: 18.03.2010, 12:28:26
 */
package de.tu_berlin.math.coga.graph.io.xml.visualization;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.graph.Edge;
import ds.graph.Node;
import ds.graph.flow.EdgeBasedFlowOverTime;
import ds.graph.network.AbstractNetwork;
import ds.mapping.IdentifiableIntegerMapping;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FlowVisualization extends GraphVisualization {
	private EdgeBasedFlowOverTime flow;
	int timeHorizon;
	int maxFlowRate;
	boolean edgesDoubled = false;

	public FlowVisualization( AbstractNetwork network, NodePositionMapping nodePositionMapping, IdentifiableIntegerMapping<Edge> edgeCapacities, IdentifiableIntegerMapping<Node> nodeCapacities, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Node> supplies, List<Node> sources, ArrayList<Node> sinks ) {
		super( network, nodePositionMapping, edgeCapacities, nodeCapacities, transitTimes, supplies, sources, sinks );
		flow = new EdgeBasedFlowOverTime( getNetwork() );
	}

	public FlowVisualization( EarliestArrivalFlowProblem eafp, NodePositionMapping nodePositionMapping ) {
		super( eafp, nodePositionMapping );
		flow = new EdgeBasedFlowOverTime( getNetwork() );
	}

	public FlowVisualization( GraphVisualization gv ) {
		super( gv );
		flow = new EdgeBasedFlowOverTime( getNetwork() );
	}

	public EdgeBasedFlowOverTime getFlow() {
		return flow;
	}

	public void setFlow( EdgeBasedFlowOverTime flow ) {
		this.flow = flow;
	}

	public int getTimeHorizon() {
		return timeHorizon;
	}

	public void setTimeHorizon( int timeHorizon ) {
		this.timeHorizon = timeHorizon;
	}

	public int getMaxFlowRate() {
		return maxFlowRate;
	}

	public void setMaxFlowRate( int maxFlowRate ) {
		this.maxFlowRate = maxFlowRate;
	}

	public boolean isEdgesDoubled() {
		return edgesDoubled;
	}

	public void setEdgesDoubled( boolean edgesDoubled ) {
		this.edgesDoubled = edgesDoubled;
	}
}
