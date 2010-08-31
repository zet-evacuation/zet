/**
 * NashFlowEdgeData.java
 * Created: 30.08.2010 17:11:13
 */
package de.tu_berlin.math.coga.zet.viewer;

import java.util.ArrayList;
import java.util.Iterator;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class NashFlowEdgeData implements Iterable<FlowData> {

	// fixed properties for the edge:
	ArrayList<FlowData> flowDatas = new ArrayList<FlowData>();
	double cap = 2;
	double taue = 1;

	public NashFlowEdgeData( double capacity, double transitTime ) {
		this.cap = capacity;
		this.taue = transitTime;
	}

	public ArrayList<FlowData> getFlowDatas() {
		return flowDatas;
	}

	public void setFlowDatas( ArrayList<FlowData> flowDatas ) {
		this.flowDatas = flowDatas;
	}

	public double getCapacity() {
		return cap;
	}

	public void setCapacity( double capacity ) {
		this.cap = capacity;
	}

	public double getTransitTime() {
		return taue;
	}

	public void setTransitTime( double transitTime ) {
		this.taue = transitTime;
	}

	public void add( FlowData flowData ) {
		flowDatas.add( flowData );
	}

	public Iterator<FlowData> iterator() {
		return flowDatas.iterator();
	}
}
