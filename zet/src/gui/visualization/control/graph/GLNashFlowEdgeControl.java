/**
 * GLNashFlowEdgeControl.java
 * Created: 30.08.2010, 16:27:02
 */
package gui.visualization.control.graph;

import de.tu_berlin.math.coga.zet.viewer.NashFlowEdgeData;
import de.tu_berlin.math.coga.zet.viewer.NashFlowVisualization;
import de.tu_berlin.math.coga.zet.viewer.NodePositionMapping;
import ds.graph.Edge;
import gui.visualization.draw.graph.GLNashFlowEdge;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class GLNashFlowEdgeControl extends GLEdgeControl {
	NashFlowEdgeData flowDatas;
	private final NashFlowVisualization nfv;

	public GLNashFlowEdgeControl( NodePositionMapping nodePositionMapping, Edge edge, NashFlowEdgeData flowDatas, NashFlowVisualization nfv ) {
		super( nodePositionMapping, edge, false );
		this.flowDatas = flowDatas;
		this.nfv = nfv;
		setView();
	}

	@Override
	protected void setView() {
		System.out.println( "A nash flow edge was set" );
		setView( new GLNashFlowEdge( this ) );
	}

	public NashFlowEdgeData getNashFlowEdgeData() {
		return flowDatas;
	}

	public double getTimeSinceStart() {
		return nfv.getTimeSinceStart();
	}
}
