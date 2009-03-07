/*
 * MaxFlowTask.java
 * 
 */

package tasks;

import ds.graph.NetworkFlowModel;
import algo.graph.dynamicflow.maxflow.TimeExpandedMaximumFlowOverTime;

/**
 *
 */
public class MFOTimeExpandedTask extends GraphAlgorithmTask {
	private int th;
	public MFOTimeExpandedTask( NetworkFlowModel model, int timeHorizon ) {
		super (model);
		this.th = timeHorizon;
	}
	
	@Override
	public void run() {
            
		TimeExpandedMaximumFlowOverTime maxFlowOverTimeAlgo =
			new TimeExpandedMaximumFlowOverTime(model.getNetwork(), model.getEdgeCapacities(), model.getTransitTimes(), model.getSources(), model.getSinks(), th);
		maxFlowOverTimeAlgo.run();
		df = maxFlowOverTimeAlgo.getDynamicFlow();
                
                //TransshipmentBoundEstimator tbe = new TransshipmentBoundEstimator();
                //int bound = tbe.calculateBound(model.getNetwork(), model.getTransitTimes(), model.getEdgeCapacities(), model.getCurrentAssignment());
                //System.out.println(bound);
	}
}
