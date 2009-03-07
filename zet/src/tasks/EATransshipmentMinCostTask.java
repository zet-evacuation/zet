/*
 * EATransshipmentTask.java
 * 
 */

package tasks;

import algo.graph.dynamicflow.eat.EATransshipmentMinCost;
import ds.graph.NetworkFlowModel;

/**
 *
 */
public class EATransshipmentMinCostTask extends GraphAlgorithmTask {
	
	public EATransshipmentMinCostTask( NetworkFlowModel model ) {
		super (model);
	}
	
	@Override
	public void run() {		
			EATransshipmentMinCost algo = new EATransshipmentMinCost( model.getNetwork(), model.getTransitTimes(), model.getEdgeCapacities(), model.getCurrentAssignment() );
			algo.run();
			if (!algo.hasRun() || !algo.isPathBasedFlowAvailable()){
				throw new AssertionError("Either algorithm has not run or path based flow is not available.");
			}
			df = algo.getResultFlowPathBased();
	}
}
