
package de.tu_berlin.math.coga.batch.operations;

import org.zetool.common.algorithm.Algorithm;
import de.tu_berlin.coga.netflow.ds.flow.MaximumFlow;
import de.tu_berlin.math.coga.batch.input.reader.InputFileReader;
import ds.graph.problem.RawMaximumFlowProblem;

/**
 *
 * @author Jan-Philipp Kappmeier
 */
public class MaximumFlowComputation extends AbstractOperation<RawMaximumFlowProblem,MaximumFlow> {
	InputFileReader<RawMaximumFlowProblem> input;
	AtomicOperation<RawMaximumFlowProblem, MaximumFlow> maxFlowAlgorithm;
  MaximumFlow mf;

  public MaximumFlowComputation() {
		// First, we go from zet to network flow model
		maxFlowAlgorithm = new AtomicOperation<>( "Max Flow Computation", RawMaximumFlowProblem.class, MaximumFlow.class );
		this.addOperation( maxFlowAlgorithm );
  }

	@Override
	@SuppressWarnings( "unchecked" )
	public boolean consume( InputFileReader<?> o ) {

		if( o.getTypeClass() == RawMaximumFlowProblem.class ) {
			input = (InputFileReader<RawMaximumFlowProblem>)o;
			return true;
		}
		return false;
	}

  @Override
  public Class<MaximumFlow> produces() {
    return MaximumFlow.class;
  }

  @Override
  public MaximumFlow getProduced() {
    return this.mf;
  }

	@Override
	public String toString() {
		return "Maximum Flow Computation";
	}

  @Override
  public void run() {
    System.out.println( "Load instance..." );
    input.run();
		RawMaximumFlowProblem flowInstance = input.getSolution();

		//System.out.println( flowInstance );

		if( maxFlowAlgorithm.getSelectedAlgorithm() == null ) {
			System.out.println( "No algorithm selected!");
			return;
		}
		System.out.println( "Selected algorithm: " + maxFlowAlgorithm.getSelectedAlgorithm() );

		final Algorithm<RawMaximumFlowProblem,MaximumFlow> maxFlowAlgo = maxFlowAlgorithm.getSelectedAlgorithm();
		maxFlowAlgo.setProblem( flowInstance );
		maxFlowAlgo.run();

    mf = maxFlowAlgo.getSolution();
  }


}
