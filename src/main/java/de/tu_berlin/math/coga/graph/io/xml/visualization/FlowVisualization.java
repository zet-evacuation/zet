/**
 * FlowVisualization.java
 * Created: 18.03.2010, 12:28:26
 */
package de.tu_berlin.math.coga.graph.io.xml.visualization;

import org.zetool.netflow.dynamic.problems.EarliestArrivalFlowProblem;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import org.zetool.graph.Edge;
import org.zetool.graph.Node;
import org.zetool.netflow.ds.flow.EdgeBasedFlowOverTime;
import org.zetool.container.mapping.IdentifiableIntegerMapping;
import org.zetool.graph.DirectedGraph;
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

	public FlowVisualization( DirectedGraph network, NodePositionMapping nodePositionMapping, IdentifiableIntegerMapping<Edge> edgeCapacities, IdentifiableIntegerMapping<Node> nodeCapacities, IdentifiableIntegerMapping<Edge> transitTimes, IdentifiableIntegerMapping<Node> supplies, List<Node> sources, List<Node> sinks ) {
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

	public void setFlow( EdgeBasedFlowOverTime flow, int maxFlowRate ) {
		setFlow( flow );
		setMaxFlowRate( maxFlowRate );
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

	protected void setMaxFlowRate( int maxFlowRate ) {
		this.maxFlowRate = maxFlowRate;
	}

	public boolean isEdgesDoubled() {
		return edgesDoubled;
	}

	public void setEdgesDoubled( boolean edgesDoubled ) {
		this.edgesDoubled = edgesDoubled;
	}
}
