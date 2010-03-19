/**
 * FlowVisualization.java
 * Created: 18.03.2010, 12:28:26
 */
package zet.xml;

import ds.graph.Edge;
import ds.graph.IdentifiableObjectMapping;
import ds.graph.IntegerIntegerMapping;
import ds.graph.flow.EdgeBasedFlowOverTime;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class FlowVisualization {
	GraphView gv;
	private EdgeBasedFlowOverTime flow;

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

}
