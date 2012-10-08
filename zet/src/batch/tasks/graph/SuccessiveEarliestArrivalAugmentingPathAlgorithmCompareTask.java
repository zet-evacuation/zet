/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package batch.tasks.graph;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import algo.graph.dynamicflow.eat.SEAAPAlgoWithTH;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import ds.NetworkFlowModelAlgorithm;
import ds.graph.flow.FlowOverTimeImplicit;
import ds.graph.flow.PathBasedFlowOverTime;

/**
 *
 * @author schwengf
 */
public class SuccessiveEarliestArrivalAugmentingPathAlgorithmCompareTask extends NetworkFlowModelAlgorithm {
	int globalTime;
	int[] TimeFlowPair;

	public SuccessiveEarliestArrivalAugmentingPathAlgorithmCompareTask( int Timehorizon ) {
		//setAlgorithm(new SEAAPAlgoWithTH());
		globalTime = Timehorizon;
		//saves the flow values for different time horizons
		TimeFlowPair = new int[globalTime + 1];
	}

	@Override
	public PathBasedFlowOverTime runAlgorithm( NetworkFlowModel model ) {


		System.out.println( "Time: " + this.globalTime );
		TimeFlowPair[0] = 0;
		for( int t = 1; t < this.globalTime + 1; t++ ) {
			SEAAPAlgoWithTH seaap = new SEAAPAlgoWithTH();
			EarliestArrivalFlowProblem problem = model.getEAFP( t );
			seaap.setProblem( problem );
			seaap.run();
			FlowOverTimeImplicit flow = seaap.getSolution();
			//System.out.println("Calculation done for t= " + t);
			//System.out.println("Flow Value: " + flow.getFlowAmount());         
			//System.out.println("Total Supply: " + problem.getTotalSupplies());
			//System.out.println("Time Needed: " + flow.getTimeHorizon());
			TimeFlowPair[t] = flow.getFlowAmount();
			//System.out.println("Zeit: : " + t + "Flow: " +  TimeFlowPair[t]);
			//System.out.println("___________________________________________");
		}

		return new PathBasedFlowOverTime();
	}

	public int[] getTimeFlowPair() {
		return this.TimeFlowPair;
	}
}
