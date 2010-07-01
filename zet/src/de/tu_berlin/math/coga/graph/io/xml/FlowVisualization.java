/**
 * FlowVisualization.java
 * Created: 18.03.2010, 12:28:26
 */
package de.tu_berlin.math.coga.graph.io.xml;

import ds.graph.flow.EdgeBasedFlowOverTime;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FlowVisualization {
	GraphView gv;
	private EdgeBasedFlowOverTime flow;
	int timeHorizon;
	int maxFlowRate;
	boolean edgesDoubled = false;

	public FlowVisualization( GraphView gv ) {
		this.gv = gv;
	}

	public GraphView getGv() {
		return gv;
	}

	public void setGv( GraphView gv ) {
		this.gv = gv;
	}

	public EdgeBasedFlowOverTime getFlow() {
		return flow;
	}

	void setFlow( EdgeBasedFlowOverTime flow ) {
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
