/*
 * EATransshipmentTask.java
 * 
 */

package tasks;

import algo.graph.dynamicflow.eat.SuccessiveEarliestArrivalAugmentingPathAlgorithmNoTH;
import ds.graph.NetworkFlowModel;

/**
 *
 */
public class SuccessiveEarliestArrivalAugmentingPathAlgorithmTask extends GraphAlgorithmTask {
	
	public SuccessiveEarliestArrivalAugmentingPathAlgorithmTask( NetworkFlowModel model ) {
		super (model);
	}
	
	@Override
	public void run() {		
			SuccessiveEarliestArrivalAugmentingPathAlgorithmNoTH algo = new SuccessiveEarliestArrivalAugmentingPathAlgorithmNoTH( model.getNetwork(), model.getTransitTimes(), model.getEdgeCapacities(), model.getNodeCapacities(), model.getCurrentAssignment() );
			algo.run();
			if (!algo.hasRun() || !algo.isPathBasedFlowAvailable()){
				throw new AssertionError("Either algorithm has not run or path based flow is not available.");
			}
			df = algo.getResultFlowPathBased();
	}
}
