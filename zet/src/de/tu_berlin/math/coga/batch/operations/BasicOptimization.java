/**
 * BasicOptimization.java
 * Created: 27.03.2014, 16:28:25
 */
package de.tu_berlin.math.coga.batch.operations;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.z.Project;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BasicOptimization extends AbstractOperation implements Operation {

	public BasicOptimization() {
		// First, we go from zet to network flow model
		// then, we go from nfm to path based flow

		AtomicOperation<Project, NetworkFlowModel> transformationOperation = new AtomicOperation<>( "Transformation", Project.class, NetworkFlowModel.class );

		this.addOperation( transformationOperation );

		AtomicOperation<EarliestArrivalFlowProblem, PathBasedFlowOverTime> eafAlgorithm = new AtomicOperation<>( "Flow Computation", EarliestArrivalFlowProblem.class, PathBasedFlowOverTime.class );

		this.addOperation( eafAlgorithm );
	}

	@Override
	public String toString() {
		return "Basic Optimization";
	}


}
