/*
 * QuickestTransshipment.java
 * Created on 23.01.2008, 23:27:12
 */

package tasks;

import algo.graph.dynamicflow.QuickestTransshipment;
import ds.graph.NetworkFlowModel;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class QuickestTransshipmentTask extends GraphAlgorithmTask {
					
	public QuickestTransshipmentTask( NetworkFlowModel model ) {
		super (model);
	}
	
	@Override
	public void run() {
		QuickestTransshipment algo = new QuickestTransshipment( model.getNetwork(), model.getTransitTimes(), model.getEdgeCapacities(), model.getCurrentAssignment() );
		algo.run();
		if (!algo.hasRun() || !algo.isPathBasedFlowAvailable()){
			throw new AssertionError("Either algorithm has not run or path based flow is not available.");
		}
		df = algo.getResultFlowPathBased();
	}
}