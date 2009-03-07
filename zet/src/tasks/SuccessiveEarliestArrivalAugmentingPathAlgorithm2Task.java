/*
 * MaxFlowTask.java
 * 
 */

package tasks;

import ds.graph.NetworkFlowModel;
import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.LongestShortestPathTimeHorizonEstimator;
import algo.graph.dynamicflow.eat.SuccessiveEarliestArrivalAugmentingPathAlgorithm;

/**
 *
 */
public class SuccessiveEarliestArrivalAugmentingPathAlgorithm2Task extends GraphAlgorithmTask {
	public SuccessiveEarliestArrivalAugmentingPathAlgorithm2Task( NetworkFlowModel model ) {
		super (model);
	}
	
	@Override
	public void run() {
		EarliestArrivalFlowProblem problem = new EarliestArrivalFlowProblem(model.getEdgeCapacities(), model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), 0, model.getTransitTimes(), model.getCurrentAssignment());            
		LongestShortestPathTimeHorizonEstimator estimator = new LongestShortestPathTimeHorizonEstimator();
		estimator.setProblem(problem);
		estimator.run();
		System.out.println(estimator.getSolution());
		problem = new EarliestArrivalFlowProblem(model.getEdgeCapacities(), model.getNetwork(), model.getNodeCapacities(), model.getSupersink(), model.getSources(), estimator.getSolution().getUpperBound(), model.getTransitTimes(), model.getCurrentAssignment());            
		SuccessiveEarliestArrivalAugmentingPathAlgorithm algo = new SuccessiveEarliestArrivalAugmentingPathAlgorithm();
		algo.setProblem(problem);
		algo.run();
		df = algo.getSolution().getPathBased();
		String result = String.format("Sent %1$s of %2$s flow units in %3$s time units successfully.", algo.getSolution().getFlowAmount(), problem.getTotalSupplies(), algo.getSolution().getTimeHorizon());
		System.out.println( result );
		AlgorithmTask.getInstance().publish( 100, result, "" );
		System.out.println(String.format("Sending the flow units required %1$s ms.", algo.getRuntime() / 1000000));                
	}
}
