/**
 * BasicOptimization.java
 * Created: 27.03.2014, 16:28:25
 */
package de.tu_berlin.math.coga.batch.operations;

import algo.graph.dynamicflow.eat.EarliestArrivalFlowProblem;
import de.tu_berlin.math.coga.batch.input.reader.InputFileReader;
import de.tu_berlin.math.coga.zet.converter.graph.NetworkFlowModel;
import ds.graph.flow.PathBasedFlowOverTime;
import ds.z.Project;


/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class BasicOptimization extends AbstractOperation implements Operation {
	InputFileReader<Project> input;

	public BasicOptimization() {
		// First, we go from zet to network flow model
		// then, we go from nfm to path based flow

		AtomicOperation<Project, NetworkFlowModel> transformationOperation = new AtomicOperation<>( "Transformation", Project.class, NetworkFlowModel.class );

		this.addOperation( transformationOperation );

		AtomicOperation<EarliestArrivalFlowProblem, PathBasedFlowOverTime> eafAlgorithm = new AtomicOperation<>( "Flow Computation", EarliestArrivalFlowProblem.class, PathBasedFlowOverTime.class );

		this.addOperation( eafAlgorithm );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public boolean consume( InputFileReader<?> o ) {

		if( o.getTypeClass() == Project.class ) {
			input = (InputFileReader<Project>)o;
			return true;
		}
		return false;
	}





	@Override
	public String toString() {
		return "Basic Optimization";
	}

	@Override
	public void run() {
		Project p = input.getSolution();

		System.out.println( p );
	}


}
