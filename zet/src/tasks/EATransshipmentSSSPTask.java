/*
 * EATransshipmentTask.java
 * 
 */

package tasks;

import algo.graph.dynamicflow.eat.EATransshipmentSSSP;
import ds.graph.NetworkFlowModel;

/**
 *
 */
public class EATransshipmentSSSPTask extends GraphAlgorithmTask {
	
	public EATransshipmentSSSPTask( NetworkFlowModel model ) {
		super (model);
	}
	
	@Override
	public void run() {		
			EATransshipmentSSSP algo = new EATransshipmentSSSP( model.getNetwork(), model.getTransitTimes(), model.getEdgeCapacities(), model.getCurrentAssignment() );
			algo.run();
			if (!algo.hasRun() || !algo.isPathBasedFlowAvailable()){
				throw new AssertionError("Either algorithm has not run or path based flow is not available.");
			}
			df = algo.getResultFlowPathBased();
	}
}
