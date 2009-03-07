/*
 * MaxFlowTask.java
 * 
 */

package tasks;

import algo.graph.dynamicflow.maxflow.MaxFlowOverTime;
import ds.PropertyContainer;
import ds.graph.NetworkFlowModel;

/**
 *
 */
public class MFOTMinCostTask extends GraphAlgorithmTask {
	private int th;
	public MFOTMinCostTask( NetworkFlowModel model, int timeHorizon ) {
		super (model);
		this.th = timeHorizon;
	}
	
	@Override
	public void run() {
		MaxFlowOverTime maxFlowOverTimeAlgo =
			new MaxFlowOverTime(model.getNetwork(), model.getEdgeCapacities(), model.getSinks(), model.getSources(), th, model.getTransitTimes());
		maxFlowOverTimeAlgo.run();
		df = maxFlowOverTimeAlgo.getDynamicFlow();
	}
}